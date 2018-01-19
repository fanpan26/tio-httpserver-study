package common;

import com.xiaoleilu.hutool.util.ArrayUtil;
import com.xiaoleilu.hutool.util.StrUtil;
import common.session.HttpSession;
import common.utils.IpUtils;
import org.apache.commons.lang3.StringUtils;
import org.tio.core.Aio;
import org.tio.core.ChannelContext;
import org.tio.core.Node;
import org.tio.utils.SystemTimer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author fyp
 * @Description
 * @Date Created at 2018/1/17 13:14
 * @Project tio-http-server
 */
public class HttpRequest extends HttpPacket {

    private RequestLine requestLine = null;
    private Map<String,Object[]> params = new HashMap<>();
    private List<Cookie> cookies = null;
    private Map<String,Cookie> cookieMap = null;
    private int contentLength;
    private String bodyString;
    private HttpConst.RequestBodyFormat bodyFormat;
    private String charset = HttpConst.CHARSET_NAME;
    private Boolean isAjax = null;
    private Boolean isSupportGzip = null;
    private HttpSession httpSession;
    private Node remote = null;
    private ChannelContext channelContext;
    private HttpConfig httpConfig;
    private String domain = null;
    private String host = null;
    private String clientIp = null;
    private long createTime = SystemTimer.currentTimeMillis();
    private boolean closed = false;

    public HttpRequest(Node remote){
        this.remote = remote;
    }

    public void close(){
        close(null);
    }
    public void close(String remark){
        Aio.close(channelContext,remark);
        closed = true;
    }

    /**
     * 添加参数
     * */
    public void addParam(String key,Object value){
        Object[] existValue = params.get(key);
        if(existValue != null){
            Object[] newExistValue = new Object[existValue.length+1];
            System.arraycopy(existValue,0,newExistValue,0,existValue.length);
            newExistValue[newExistValue.length - 1] = value;
            params.put(key,newExistValue);
        }else{
            Object[] newExistValue = new Object[]{ value };
            params.put(key,newExistValue);
        }
    }

    public Node getRemote() {
        return remote;
    }
    public void setRemote(Node remote) {
        this.remote = remote;
    }

    public void setHttpConfig(HttpConfig httpConfig) {
        this.httpConfig = httpConfig;
    }
    public HttpConfig getHttpConfig(){
        return httpConfig;
    }

    public HttpConst.RequestBodyFormat getBodyFormat() {
        return bodyFormat;
    }
    public String getUserAgent() {
        return this.headers.get(HttpConst.RequestHeaderKey.User_Agent);
    }
    public String getHost(){
        if (host != null){
            return host;
        }
        host = this.headers.get(HttpConst.RequestHeaderKey.Host);
        return host;
    }
    public String getClientIp(){
        if (clientIp == null){
            clientIp = IpUtils.getRealIp(this);
        }
        return clientIp;
    }
    public String getDomain(){
        if (domain != null){
            return domain;
        }
        if (StrUtil.isBlank(getHost())){
            return null;
        }
        domain = StrUtil.subBefore(getHeaderString(),":",false);
        return domain;
    }
    /**
     * @return the bodyString
     */
    public String getBodyString() {
        return bodyString;
    }

    /**
     * @return the channelContext
     */
    public ChannelContext getChannelContext() {
        return channelContext;
    }

    /**
     * @return the charset
     */
    public String getCharset() {
        return charset;
    }

    /**
     * @return the bodyLength
     */
    public int getContentLength() {
        return contentLength;
    }
    public Cookie getCookie(String cookieName){
        if (cookieMap == null){
            return null;
        }
        return cookieMap.get(cookieName);
    }
    public Map<String, Cookie> getCookieMap() {
        return cookieMap;
    }
    public List<Cookie> getCookies() {
        return cookies;
    }

    /**
     * @return the httpSession
     */
    public HttpSession getHttpSession() {
        return httpSession;
    }

    public Boolean getIsAjax() {
        if (isAjax == null){
            String X_Requested_With = this.getHeader(HttpConst.RequestHeaderKey.X_Requested_With);
            if(X_Requested_With != null && "XMLHttpRequest".equalsIgnoreCase(X_Requested_With)){
                isAjax = Boolean.TRUE;
            }else{
                isAjax = Boolean.FALSE;
            }
        }
        return isAjax;
    }

    public Boolean getIsSupportGzip(){
        if(isSupportGzip == null){
            String Accept_Encoding = getHeader(HttpConst.RequestHeaderKey.Accept_Encoding);
            if(StringUtils.isNoneBlank(Accept_Encoding)){
                String[] ss = StringUtils.split(Accept_Encoding,",");
                if (ArrayUtil.contains(ss,"gzip")){
                    isSupportGzip = Boolean.TRUE;
                }else{
                    isSupportGzip = Boolean.FALSE;
                }
            }else{
                isSupportGzip= Boolean.TRUE;
            }
        }
        return isSupportGzip;
    }

    public Map<String,Object[]> getParams(){
        return params;
    }

    public String getParam(String name){
        if (params == null){
            return null;
        }
        Object[] values = params.get(name);
        if(values != null && values.length > 0){
            Object obj = values[0];
            return (String )obj;
        }
        return null;
    }
    public RequestLine getRequestLine() {
        return requestLine;
    }

    public void parseCookie(){
        String cookieLine = headers.get(HttpConst.RequestHeaderKey.Cookie);
        if(StringUtils.isNotBlank(cookieLine)){
            cookies = new ArrayList<>();
            cookieMap = new HashMap<>();
            Map<String,String> _cookieMap = Cookie.getEqualMap(cookieLine);
            List<Map<String,String>> cookieListMap = new ArrayList<>();
            for(Map.Entry<String,String> cookieMapEntry : _cookieMap.entrySet()){
                HashMap<String,String> cookieOneMap = new HashMap<>();
                cookieOneMap.put(cookieMapEntry.getKey(),cookieMapEntry.getValue());
                cookieListMap.add(cookieOneMap);

                Cookie cookie = Cookie.buildCookie(cookieOneMap);
                cookies.add(cookie);
                cookieMap.put(cookie.getName(),cookie);
            }
        }
    }

    public void setBody(byte[] body) {
        this.body = body;
    }

    /**
     * @param bodyFormat the bodyFormat to set
     */
    public void setBodyFormat(HttpConst.RequestBodyFormat bodyFormat) {
        this.bodyFormat = bodyFormat;
    }

    /**
     * @param bodyString the bodyString to set
     */
    public void setBodyString(String bodyString) {
        this.bodyString = bodyString;
    }

    /**
     * @param channelContext the channelContext to set
     */
    public void setChannelContext(ChannelContext channelContext) {
        this.channelContext = channelContext;
    }

    /**

     * @param charset the charset to set

     */
    public void setCharset(String charset) {
        this.charset = charset;
    }

    /**
     * @param contentLength the bodyLength to set
     */
    public void setContentLength(int contentLength) {
        this.contentLength = contentLength;
    }

    /**
     * @param cookieMap the cookieMap to set
     */
    public void setCookieMap(Map<String, Cookie> cookieMap) {
        this.cookieMap = cookieMap;
    }

    /**
     * @param cookies the cookies to set
     */
    public void setCookies(List<Cookie> cookies) {
        this.cookies = cookies;
    }

    @Override
    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
        if(this.headers!=null){
            parseCookie();
        }
    }

    /**
     * @param httpSession the httpSession to set
     */
    public void setHttpSession(HttpSession httpSession) {
        this.httpSession = httpSession;
    }

    /**
     * @param isAjax the isAjax to set
     */
    public void setIsAjax(Boolean isAjax) {
        this.isAjax = isAjax;
    }

    /**
     * @param isSupportGzip the isSupportGzip to set
     */
    public void setIsSupportGzip(Boolean isSupportGzip) {
        this.isSupportGzip = isSupportGzip;
    }

    /**
     * @param params the params to set
     */
    public void setParams(Map<String, Object[]> params) {
        this.params = params;
    }

    /**
     * @param requestLine the requestLine to set
     */
    public void setRequestLine(RequestLine requestLine) {
        this.requestLine = requestLine;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    @Override
    public String toString() {
        String ret =  this.getHeaderString();
        if (this.getBodyString() != null) {
            ret += this.getBodyString();
        }
        return ret;

    }

    public boolean isClosed() {
        return closed;
    }

    public void setClosed(boolean closed) {
        this.closed = closed;
    }
}
