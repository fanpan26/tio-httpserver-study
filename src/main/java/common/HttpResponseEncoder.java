package common;

import org.tio.core.ChannelContext;
import org.tio.core.GroupContext;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @Author fyp
 * @Description
 * @Date Created at 2018/1/17 13:18
 * @Project tio-http-server
 */
public class HttpResponseEncoder {
    public static enum Step{
        firstline,header,body
    }

    public static final int MAX_HEADER_LENGTH = 20480;

    /**
     * 消息返回编码
     * */
    public static ByteBuffer encode(HttpResponse httpResponse, GroupContext groupContext, ChannelContext channelContext,boolean skipCookie){
        byte[] encodedBytes = httpResponse.getEncodedBytes();
        if(encodedBytes != null){
            ByteBuffer ret = ByteBuffer.wrap(encodedBytes);
            ret.position(ret.limit());
            return ret;
        }
        int bodyLength = 0;
        byte[] body = httpResponse.getBody();
        if(body != null){
            bodyLength = body.length;
        }

        StringBuilder str = new StringBuilder(256);

        HttpResponseStatus httpResponseStatus = httpResponse.getStatus();

        str.append("HTTP/1.1").append(" ").append(httpResponseStatus.getStatus()).append(" ").append(httpResponseStatus.getDescription()).append("\r\n");
        Map<String, String> headers = httpResponse.getHeaders();
        if(headers != null && headers.size() > 0){
            headers.put(HttpConst.ResponseHeaderKey.Content_Length,bodyLength+"");
            Set<Map.Entry<String,String>> headerSet = headers.entrySet();
            for(Map.Entry<String,String> entry : headerSet){
                str.append(entry.getKey()).append(":").append(entry.getValue()).append("\r\n");
            }
        }
        if(!skipCookie){
            List<Cookie> cookies = httpResponse.getCookies();
            if(cookies != null){
                for(Cookie cookie : cookies){
                    str.append(HttpConst.ResponseHeaderKey.Set_Cookie).append(":");
                    str.append(cookie.toString());
                    str.append("\r\n");
                }
            }
        }
        str.append("\r\n");

        byte[] headerBytes;
        try{
            String headerString = str.toString();
            httpResponse.setHeaderString(headerString);
            headerBytes = headerString.getBytes(httpResponse.getCharset());
        }catch (Throwable e){
            throw new RuntimeException(e);
        }
        ByteBuffer buffer = ByteBuffer.allocate(headerBytes.length + bodyLength);
        buffer.put(headerBytes);

        if(bodyLength > 0){
            buffer.put(body);
        }
        return buffer;
    }
}
