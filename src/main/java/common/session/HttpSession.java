package common.session;

import common.HttpConfig;
import org.tio.utils.SystemTimer;

import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author fyp
 * @Description
 * @Date Created at 2018/1/17 13:15
 * @Project tio-http-server
 */
public class HttpSession implements Serializable{

    private static final long serialVersionUID = 6077020620501316538L;

    private Map<String, Serializable> data = new ConcurrentHashMap<>();

    private String id = null;

    private long createTime = SystemTimer.currentTimeMillis();

    /**

     * 此处空的构造函数必须要有

     *

     * @author: tanyaowu

     */
    public HttpSession() {
    }

    /**

     * @author tanyaowu

     */
    public HttpSession(String id) {
        this.id = id;
    }

    /**

     * 清空所有属性

     * @param httpConfig

     * @author tanyaowu

     */
    public void clear(HttpConfig httpConfig) {
        data.clear();
        update(httpConfig);
    }

    /**

     * 获取会话属性

     * @param key

     * @return

     * @author tanyaowu

     */
    public Object getAttribute(String key) {
        return data.get(key);
    }

    /**

     *

     * @param key

     * @param clazz

     * @return

     * @author: tanyaowu

     */
    @SuppressWarnings("unchecked")
    public <T> T getAttribute(String key, Class<T> clazz) {
        return (T) data.get(key);
    }

    @SuppressWarnings("unchecked")
    public <T> T getAttribute(String key, Class<T> clazz, T defaultObj) {
        T t = (T) data.get(key);
        if (t == null) {
            return defaultObj;
        }
        return t;
    }

    public String getId() {
        return id;
    }

    /**

     *

     * @param key

     * @param httpConfig

     * @author tanyaowu

     */
    public void removeAttribute(String key, HttpConfig httpConfig) {
        data.remove(key);
        update(httpConfig);
    }

    /**

     * 设置会话属性

     * @param key

     * @param value

     * @param httpConfig

     * @author tanyaowu

     */
    public void setAttribute(String key, Serializable value, HttpConfig httpConfig) {
        data.put(key, value);
        update(httpConfig);
    }

    public void update(HttpConfig httpConfig) {
        httpConfig.getSessionStore().put(id, this);
    }

    public void setId(String id) {
        this.id = id;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }
}
