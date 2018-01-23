package common;

import common.utils.HttpParseUtils;
import org.apache.commons.lang3.StringUtils;
import org.tio.core.ChannelContext;
import org.tio.core.exception.AioDecodeException;
import org.tio.core.exception.LengthOverflowException;
import org.tio.core.utils.ByteBufferUtils;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.net.URLDecoder;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @Author fyp
 * @Description
 * @Date Created at 2018/1/17 13:18
 * @Project tio-http-server
 */
public class HttpRequestDecoder {
    public static enum Step{
        firstline,header,body
    }

    /**
     * 头部最多字节数
     * */
    public static final int MAX_LENGTH_OF_HEADER = 20480;
    /**
     * 头部每行字节数
     * */
    public static final int MAX_LENGTH_OF_HEADERLINE = 2048;

    private static void log(String msg){
        System.out.println(msg);
    }

    public static HttpRequest decode(ByteBuffer buffer, ChannelContext channelContext) throws AioDecodeException{

        /**
         * 测试打印，这一段在源码中并不存在，是为了打印出请求的内容
         * */
//        byte[] b = new byte[buffer.remaining()];
//        buffer.get(b,0,b.length);
//        try {
//            String info = new String(b, "utf-8");
//            System.out.println(info);
//        }catch (UnsupportedEncodingException e){
//
//        }
//        buffer = ByteBuffer.wrap(b);
        //源代码开始
        int initPosition = buffer.position();
        int readableLength = buffer.limit() - initPosition;
        //当前步骤，第一行
        Step step = Step.firstline;
        //用于保存header内容
        Map<String,String> headers = new HashMap<>();
        int contentLength = 0;
        byte[] bodyBytes = null;

        StringBuilder headerString = new StringBuilder(512);
        RequestLine firstLine = null;

        /**
         * position < limit
         * 循环读取每行的内容进行解析
         * */
        while(buffer.hasRemaining()){
            String line;
            try{
                line = ByteBufferUtils.readLine(buffer,null,MAX_LENGTH_OF_HEADERLINE);
                log("开始解析："+line);
            }catch (LengthOverflowException e){
                throw new AioDecodeException(e);
            }

            int newPosition = buffer.position();
            //如果头部信息超过 20480 字节，异常
            if (newPosition - initPosition > MAX_LENGTH_OF_HEADER){
                throw new AioDecodeException("max http header length " + MAX_LENGTH_OF_HEADER);
            }
            //没有内容，返回null
            if (line == null){
                return null;
            }
            headerString.append(line).append("\r\n");
            //line为空，头部信息解析结束
            if("".equals(line)){
                log("开始解析请求体");
                //从 Content-Length:167 读取请求体的长度
                String contentLengthStr = headers.get(HttpConst.RequestHeaderKey.Content_Length);
                if(StringUtils.isBlank(contentLengthStr)){
                    contentLength = 0;
                }else{
                    contentLength = Integer.parseInt(contentLengthStr);
                }

                //头部信息长度
                int headerLength = (buffer.position() - initPosition);
                //头部和体部的总字节长度
                int allNeedLength = headerLength + contentLength;
                if(readableLength >= allNeedLength){
                    step = step.body;
                    break;
                }else{
                    channelContext.setPacketNeededLength(allNeedLength);
                    return null;
                }
            }else{
              if(step == Step.firstline){
                  log("开始解析requestLine");
                  //解析第一行
                  firstLine = parseRequestLine(line);
                  step = Step.header;
              }else if(step == Step.header){
                  //解析头部
                  log("开始解析头部信息："+line);
                  KeyValue keyValue = HttpParseUtils.parseHeaderLine(line);
                  headers.put(keyValue.getKey(),keyValue.getValue());
              }
              continue;
            }
        }
        if (step != step.body){
            return null;
        }
        //头部没有 Host
        if(!headers.containsKey(HttpConst.RequestHeaderKey.Host)){
            throw new AioDecodeException("there is no host header");
        }

        HttpRequest request = new HttpRequest(channelContext.getClientNode());
        request.setChannelContext(channelContext);
        //httpconfig 从groupContext的attribute中读取
        request.setHttpConfig((HttpConfig)channelContext.getGroupContext().getAttribute(GroupContextKey.HTTP_SERVER_CONFIG));
        request.setHeaderString(headerString.toString());
        request.setRequestLine(firstLine);
        request.setHeaders(headers);
        request.setContentLength(contentLength);

        parseQueryString(request,firstLine,channelContext);

        if (contentLength == 0){

        }else{
            bodyBytes = new byte[contentLength];
            buffer.get(bodyBytes);
            //解析消息体
            log("开始解析消息体");
            parseBody(request,firstLine,bodyBytes,channelContext);
        }
        log("解析结束");
        return request;
    }

    /**
     * 解析第一行 GET http://www.baidu.com:80?user=1&pw=23
     * */
    public static RequestLine parseRequestLine(String line) throws AioDecodeException{
        try {
            //GET /test/hello HTTP/1.1
            int index1 = line.indexOf(' ');
            //得到请求方法 GET
            String _method = StringUtils.upperCase(line.substring(0,index1));
            //转化为枚举的Method
            Method method = Method.from(_method);

            //截取路径  /test/hello
            int index2 = line.indexOf(' ',index1 + 1);
            // /test/hello
            String pathAndQueryStr = line.substring(index1 + 1,index2);
            String path = null;
            String queryStr = null;
            //是否带有?参数
            int indexOfQuestionMark = pathAndQueryStr.indexOf("?");
            //URL上是否带参数，例如 ?user=123456
            if(indexOfQuestionMark != -1){
                queryStr = StringUtils.substring(pathAndQueryStr,indexOfQuestionMark + 1);
                path = StringUtils.substring(pathAndQueryStr,0,indexOfQuestionMark);
            }else{
                path = pathAndQueryStr;
                queryStr = "";
            }

            //HTTP/1.1
            String protocolVersion = line.substring(index2 + 1);
            String[] pv = StringUtils.split(protocolVersion,"/");
            //HTTP
            String protocol = pv[0];
            //1.1
            String version = pv[1];

            RequestLine requestLine = new RequestLine();
            requestLine.setMethod(method);
            requestLine.setPath(path);
            requestLine.setInitPath(path);
            requestLine.setPathAndQuery(pathAndQueryStr);
            requestLine.setQuery(queryStr);
            requestLine.setVersion(version);
            requestLine.setProtocol(protocol);
            requestLine.setLine(line);

            return requestLine;

        }catch (Throwable e){
            throw  new AioDecodeException(e);
        }
    }

    /**
     * 解析params赋值给 HttpRequest.params
     * */
    public static void decodeParams(Map<String,Object[]> params,String paramsStr,String charset,ChannelContext channelContext){
        if(StringUtils.isBlank(paramsStr)){
            return ;
        }
        String[] keyValues = StringUtils.split(paramsStr,"&");
        for(String kv :keyValues){
            String[] keyValueArr = StringUtils.split(kv,"=");
            if(keyValueArr.length != 2){
                continue;
            }
            String key = keyValueArr[0];
            String value = null;
            try{
                value = URLDecoder.decode(keyValueArr[1],charset);
            }catch (UnsupportedEncodingException e){

            }

            Object[] existValue = params.get(key);
            if(existValue != null){
                String[] newExistValue = new String[existValue.length + 1];
                System.arraycopy(existValue,0,newExistValue,0,existValue.length);
                newExistValue[newExistValue.length - 1] = value;
                params.put(key,newExistValue);
            }else{
                String[] newExitstValue = new String[]{ value };
                params.put(key,newExitstValue);
            }
        }
        return;
    }

    private static void parseQueryString(HttpRequest request,RequestLine firstLine,ChannelContext channelContext){
        String paramStr = firstLine.getQuery();
        if(StringUtils.isNotBlank(paramStr)){
            decodeParams(request.getParams(),paramStr,request.getCharset(),channelContext);
        }
    }

    /**
     * 解析请求体
     * */
    private static void parseBody(HttpRequest request,RequestLine firstLine,byte[] bodyBytes,ChannelContext channelContext) throws AioDecodeException {
        parseBodyFormat(request);
        HttpConst.RequestBodyFormat bodyFormat = request.getBodyFormat();

        request.setBody(bodyBytes);
        String bodyString = null;
        //解析multipart
        if (bodyFormat == HttpConst.RequestBodyFormat.MULTIPART) {
            String initBoundary = HttpParseUtils.getPerprotyEqualValue(request.getHeaders(), HttpConst.RequestHeaderKey.Content_Type, "boundary");
            HttpMultiBodyDecoder.decode(request, firstLine, bodyBytes, initBoundary, channelContext);
        } else {
            if (bodyBytes != null && bodyBytes.length > 0) {
                try {
                    bodyString = new String(bodyBytes, request.getCharset());
                    request.setBodyString(bodyString);

                } catch (UnsupportedEncodingException e) {

                }
            }

            if (bodyFormat == HttpConst.RequestBodyFormat.URLENCODED) {
                parseUrlencoded(request,bodyString, channelContext);
            }
        }
    }

    /**
     * 解析form请求体的内容
     * */
    private static void parseUrlencoded(HttpRequest httpRequest, String bodyString, ChannelContext channelContext) {
        if (StringUtils.isNotBlank(bodyString)) {
            decodeParams(httpRequest.getParams(), bodyString, httpRequest.getCharset(), channelContext);
        }
    }

    /**
     *
     * 解析请求体类型
     * Content-Type : application/x-www-form-urlencoded; charset=UTF-8
     * */
    public static void parseBodyFormat(HttpRequest request){
        Map<String,String> headers =request.getHeaders();
        String Content_Type = StringUtils.lowerCase(headers.get(HttpConst.RequestHeaderKey.Content_Type));
        HttpConst.RequestBodyFormat bodyFormat;

        if(StringUtils.contains(Content_Type,HttpConst.RequestHeaderValue.Content_Type.application_x_www_form_urlencoded)){
            bodyFormat = HttpConst.RequestBodyFormat.URLENCODED;
        }else if(StringUtils.contains(Content_Type,HttpConst.RequestHeaderValue.Content_Type.multipart_form_data)){
            bodyFormat = HttpConst.RequestBodyFormat.MULTIPART;
        }else{
            bodyFormat = HttpConst.RequestBodyFormat.TEXT;
        }
        request.setBodyFormat(bodyFormat);
        if(StringUtils.isNoneBlank(Content_Type)){
            String charset = HttpParseUtils.getPerprotyEqualValue(headers,HttpConst.RequestHeaderKey.Content_Type,"charset");
            if(StringUtils.isNoneBlank(charset)){
                request.setCharset(charset);
            }
        }
    }
}







































