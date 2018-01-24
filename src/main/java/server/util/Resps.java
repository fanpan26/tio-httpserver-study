package server.util;

import com.xiaoleilu.hutool.io.FileUtil;
import com.xiaoleilu.hutool.util.ClassUtil;
import common.*;
import jodd.io.FileNameUtil;
import org.apache.commons.lang3.StringUtils;
import org.tio.utils.json.Json;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.Map;
import java.util.Objects;

/**
 * @Author fyp
 * @Description
 * @Date Created at 2018/1/18 16:34
 * @Project tio-http-server
 */
public class Resps {
    public static HttpResponse css(HttpRequest request,String bodyString){
        return css(request,bodyString,request.getHttpConfig().getCharset());
    }

    public static HttpResponse css(HttpRequest request,String bodyString,String charset){
        HttpResponse ret = string(request,bodyString,charset, MimeType.TEXT_CSS_CSS+";charset="+charset);
        return ret;
    }


    public static HttpResponse js(HttpRequest request, String bodyString) {
        return js(request, bodyString, request.getHttpConfig().getCharset());
    }

    public static HttpResponse js(HttpRequest request, String bodyString, String charset) {
        HttpResponse ret = string(request, bodyString, charset, MimeType.APPLICATION_JAVASCRIPT_JS.getType() + ";charset=" + charset);
        return ret;
    }

    public static HttpResponse html(HttpRequest request, String bodyString) {
        HttpConfig httpConfig = request.getHttpConfig();
        return html(request, bodyString, httpConfig.getCharset());
    }


    public static HttpResponse html(HttpRequest request, String bodyString, String charset) {
        HttpResponse ret = string(request, bodyString, charset, MimeType.TEXT_HTML_HTML.getType() + ";charset=" + charset);
        return ret;
    }


    public static HttpResponse string(HttpRequest request,String bodyString,String Content_Type){
        return string(request,bodyString,request.getHttpConfig().getCharset(),Content_Type);
    }

    public static HttpResponse string(HttpRequest request,String bodyString,String charset,String Content_Type){
        HttpResponse ret = new HttpResponse(request);
        if(bodyString != null){
            try{
                ret.setBody(bodyString.getBytes(charset));
            }catch (UnsupportedEncodingException e){

            }
        }
        ret.addHeader(HttpConst.ResponseHeaderKey.Content_Type,Content_Type);
        return ret;
    }

    public static HttpResponse bytesWithContentType(HttpRequest request,byte[] bodyBytes,String contentType) {
        HttpResponse ret = new HttpResponse(request);
        ret.setBody(bodyBytes);
        ret.addHeader(HttpConst.ResponseHeaderKey.Content_Type, contentType);
        return ret;
    }

    public  static HttpResponse bytesWithHeaders(HttpRequest request,byte[] bodyBytes,Map<String,String> headers) {
        HttpResponse ret = new HttpResponse(request);
        ret.setBody(bodyBytes);
        ret.addHeaders(headers);
        return ret;
    }

    public static HttpResponse json(HttpRequest request, Object body) {
        return json(request, body, request.getHttpConfig().getCharset());
    }

    public static HttpResponse json(HttpRequest request, Object body,String charset){
        HttpResponse ret = null;
        String jsonMimeString = MimeType.TEXT_PLAIN_JSON.getType()+";charset="+charset;
        if(body == null){
            ret = string(request,"",charset,jsonMimeString);
        }else{
            if(body.getClass() == String.class && ClassUtil.isBasicType(body.getClass())){
                ret = string(request,body+"",charset,jsonMimeString);
            }else{
                ret = string(request, Json.toJson(body),charset,jsonMimeString);
            }
        }
        return ret;
    }

    public static HttpResponse redirect(HttpRequest request,String path){
        HttpResponse ret = new HttpResponse(request);
        ret.setStatus(HttpResponseStatus.C302);
        ret.addHeader(HttpConst.ResponseHeaderKey.Location,path);
        return ret;
    }

    public static HttpResponse try304(HttpRequest request,long lastModifiedOnServer){
        String If_Modified_Since = request.getHeader(HttpConst.RequestHeaderKey.If_Modified_Since);
        if(StringUtils.isNoneBlank(If_Modified_Since)){
            Long If_Modified_Since_Date = null;
            try{
                If_Modified_Since_Date = Long.parseLong(If_Modified_Since);

                if(lastModifiedOnServer <= If_Modified_Since_Date){
                    HttpResponse ret = new HttpResponse(request);
                    ret.setStatus(HttpResponseStatus.C304);
                    return ret;
                }
            }catch (NumberFormatException e){
                return null;
            }
        }
        return null;
    }

    public static HttpResponse resp500(HttpRequest request,RequestLine requestLine,HttpConfig httpConfig,Throwable throwable){
        File pageRoot = httpConfig.getPageRoot();
        if(pageRoot != null){
            String file500 = httpConfig.getPage500();

            File file = new File(pageRoot + file500);
            if(file.exists()){
                HttpResponse ret = Resps.redirect(request,file500+"?tio_initpath="+requestLine.getPathAndQuery());
                return ret;
            }
        }
        HttpResponse ret = Resps.html(request,"500");
        ret.setStatus(HttpResponseStatus.C500);
        return ret;
    }

    public static HttpResponse resp404(HttpRequest request, RequestLine requestLine, HttpConfig httpConfig) {
        File pageRoot = httpConfig.getPageRoot();

        if (pageRoot != null) {
            String file404 = httpConfig.getPage404();
            File file = new File(pageRoot + file404);
            if (file.exists()) {
                HttpResponse ret = Resps.redirect(request, file404 + "?tio_initpath=" + requestLine.getPathAndQuery());
                return ret;
            }
        }

        HttpResponse ret = Resps.html(request, "404");
        ret.setStatus(HttpResponseStatus.C404);
        return ret;
    }

    public static HttpResponse resp405(HttpRequest request) {
        HttpResponse ret = Resps.html(request, "");
        ret.setStatus(HttpResponseStatus.C405);
        return ret;
    }

    public static HttpResponse txt(HttpRequest request, String bodyString, String charset) {
        HttpResponse ret = string(request, bodyString, charset, MimeType.TEXT_PLAIN_TXT.getType() + ";charset=" + charset);
        return ret;
    }

    public static HttpResponse txt(HttpRequest request, String bodyString) {
        return txt(request, bodyString, request.getHttpConfig().getCharset());
    }

    public static HttpResponse bytes(HttpRequest request,byte[] bodyBytes,String extension){
        String contentType = null;
        if (StringUtils.isNoneBlank(extension)){
            MimeType mimeType = MimeType.fromExtension(extension);
            if (mimeType != null){
                contentType = mimeType.getType();
            }else{
                contentType = "application/octet-stream";
            }
        }
        return bytesWithContentType(request,bodyBytes,contentType);
    }

    public static HttpResponse file(HttpRequest request,String path,HttpConfig httpConfig) throws IOException{
        File pageRoot = httpConfig.getPageRoot();
        if (pageRoot != null){
            File file = new File(pageRoot + path);
            if (!file.exists()){
                return resp404(request,request.getRequestLine(),httpConfig);
            }
            return file(request,file);
        }else{
            return resp404(request,request.getRequestLine(),httpConfig);
        }
    }
    public static HttpResponse file(HttpRequest request, File fileOnServer) throws IOException {
        Date lastModified = FileUtil.lastModifiedTime(fileOnServer);
        HttpResponse ret = try304(request, lastModified.getTime());
        if (ret != null) {
            return ret;
        }

        byte[] bodyBytes = FileUtil.readBytes(fileOnServer);
        String filename = fileOnServer.getName();
        String extension = FileNameUtil.getExtension(filename);
        ret = bytes(request, bodyBytes, extension);
        ret.addHeader(HttpConst.ResponseHeaderKey.Last_Modified, lastModified.getTime() + "");
        return ret;
    }
}
