package common;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author fyp
 * @Description
 * @Date Created at 2018/1/17 13:14
 * @Project tio-http-server
 */
public class HttpResponse extends HttpPacket{
    private static final long serialVersionUID = -3512681144230291786L;

    private HttpResponseStatus status = HttpResponseStatus.C200;

    private boolean isStaticRes = false;
    private HttpRequest request = null;
    private List<Cookie> cookies = null;

    private boolean hasGzipped = false;

    private String charset = HttpConst.CHARSET_NAME;
    private byte[] encodedBytes = null;
    private boolean skipIpStat = false;
    private boolean skipTokenStat = false;

    public HttpResponse(HttpRequest request){
        this.request = request;

        String connection = request.getHeader(HttpConst.RequestHeaderKey.Connection);
        RequestLine requestLine = request.getRequestLine();
        String version = requestLine.getVersion();
        if("1.0".equals(version)){
            if(StringUtils.equals(connection,HttpConst.RequestHeaderValue.Connection.keep_alive)){
                addHeader(HttpConst.ResponseHeaderKey.Connection,HttpConst.ResponseHeaderValue.Connection.keep_alive);
                addHeader(HttpConst.ResponseHeaderKey.Keep_Alive,"timeout=10,max=20");
            }else{
                addHeader(HttpConst.ResponseHeaderKey.Connection, HttpConst.ResponseHeaderValue.Connection.close);
            }
        }else{
            if (StringUtils.equals(connection, HttpConst.RequestHeaderValue.Connection.close)) {
                addHeader(HttpConst.ResponseHeaderKey.Connection, HttpConst.ResponseHeaderValue.Connection.close);
            } else {
                addHeader(HttpConst.ResponseHeaderKey.Connection, HttpConst.ResponseHeaderValue.Connection.keep_alive);
                addHeader(HttpConst.ResponseHeaderKey.Keep_Alive, "timeout=10, max=20");
            }
        }
        HttpConfig httpConfig = request.getHttpConfig();
        if(httpConfig != null){
            addHeader(HttpConst.ResponseHeaderKey.Server,httpConfig.getServerInfo());
        }
    }

    public String getContentType(){
        return this.headers.get(HttpConst.RequestHeaderKey.Content_Type);
    }

    public boolean addCookie(Cookie cookie){
        if(cookies == null){
            synchronized (this){
                if (cookies == null){
                    cookies = new ArrayList<>();
                }
            }
        }
        return cookies.add(cookie);
    }
    /**
     * @return the charset
     */
    public String getCharset() {
        return charset;
    }

    /**
     * @return the cookies
     */
    public List<Cookie> getCookies() {
        return cookies;
    }

    /**
     * @return the encodedBytes
     */
    public byte[] getEncodedBytes() {
        return encodedBytes;
    }

    /**
     * @return the request
     */
    public HttpRequest getHttpRequest() {
        return request;
    }

    /**
     * @return the status
     */
    public HttpResponseStatus getStatus() {
        return status;
    }
    public boolean isStaticRes() {
        return isStaticRes;
    }
    @Override
    public String logstr() {
        String str = null;
        if (request != null) {
            str = "\r\n响应: 请求ID_" + request.getId() + "  " + request.getRequestLine().getPathAndQuery();
            str += "\r\n" + this.getHeaderString();
        } else {
            str = "\r\n响应\r\n" + status.getHeaderText();
        }
        return str;
    }
    public void setBody(byte[] body) {
        this.body = body;
    }

    /**

     * @param charset the charset to set

     */
    public void setCharset(String charset) {
        this.charset = charset;
    }

    /**

     * @param cookies the cookies to set

     */
    public void setCookies(List<Cookie> cookies) {
        this.cookies = cookies;
    }

    /**

     * @param encodedBytes the encodedBytes to set

     */
    public void setEncodedBytes(byte[] encodedBytes) {
        this.encodedBytes = encodedBytes;
    }

    /**

     * @param request the request to set

     */
    public void setHttpRequestPacket(HttpRequest request) {
        this.request = request;
    }

    /**

     * @param isStaticRes the isStaticRes to set

     */
    public void setStaticRes(boolean isStaticRes) {
        this.isStaticRes = isStaticRes;
    }

    /**

     * @param status the status to set

     */
    public void setStatus(HttpResponseStatus status) {
        this.status = status;
    }

    public boolean isHasGzipped() {
        return hasGzipped;
    }

    public void setHasGzipped(boolean hasGzipped) {
        this.hasGzipped = hasGzipped;
    }

    public boolean isSkipIpStat() {
        return skipIpStat;
    }

    public void setSkipIpStat(boolean skipIpStat) {
        this.skipIpStat = skipIpStat;
    }

    public boolean isSkipTokenStat() {
        return skipTokenStat;
    }

    public void setSkipTokenStat(boolean skipTokenStat) {
        this.skipTokenStat = skipTokenStat;
    }

    public static HttpResponse cloneResponse(HttpRequest request, HttpResponse response) {
        HttpResponse cloneResponse = new HttpResponse(request);
        cloneResponse.setStatus(response.getStatus());
        cloneResponse.setBody(response.getBody());
        cloneResponse.setHasGzipped(response.isHasGzipped());
        cloneResponse.addHeaders(response.getHeaders());

        if (cloneResponse.getCookies() != null) {
            cloneResponse.getCookies().clear();
        }
        return cloneResponse;
    }
}
