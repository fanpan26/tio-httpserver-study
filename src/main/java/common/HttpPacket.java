package common;

import org.tio.core.intf.Packet;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author fyp
 * @Description
 * @Date Created at 2018/1/17 13:14
 * @Project tio-http-server
 */
public class HttpPacket extends Packet{

    //	private static Logger log = LoggerFactory.getLogger(HttpPacket.class);
    private static final long serialVersionUID = 3903186670675671956L;

    //	public static final int MAX_LENGTH_OF_BODY = (int) (1024 * 1024 * 5.1); //只支持多少M数据

    private Map<String, Serializable> props = new ConcurrentHashMap<>();

    /**
     * 获取请求属性
     * @return
     * @author tanyaowu
     */
    public Object getAttribute(String key) {
        return props.get(key);
    }

    public Object getAttribute(String key, Serializable defaultValue) {
        Serializable ret = props.get(key);
        if (ret == null) {
            return defaultValue;
        }
        return ret;
    }

    /**
     * @param key
     * @author tanyaowu
     */
    public void removeAttribute(String key) {
        props.remove(key);
    }

    /**
     * 设置请求属性
     * @param key
     * @param value
     * @author tanyaowu
     */
    public void setAttribute(String key, Serializable value) {
        props.put(key, value);
    }

    protected byte[] body;

    private String headerString;

    protected Map<String, String> headers = new HashMap<>();

    public HttpPacket() {

    }

    public void addHeader(String key, String value) {
        headers.put(key, value);
    }

    public void addHeaders(Map<String, String> headers) {
        if (headers != null) {
            this.headers.putAll(headers);
        }
    }

    /**
     * @return the body
     */
    public byte[] getBody() {
        return body;
    }

    public String getHeader(String key) {
        return headers.get(key);
    }

    /**
     * @return the headers
     */
    public Map<String, String> getHeaders() {
        return headers;
    }

    public String getHeaderString() {
        return headerString;
    }

    public void removeHeader(String key, String value) {
        headers.remove(key);
    }

    /**
     * @param headers the headers to set
     */
    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public void setHeaderString(String headerString) {
        this.headerString = headerString;
    }
}
