package common;

import com.xiaoleilu.hutool.io.FileUtil;
import common.handler.HttpRequestHandler;
import common.session.HttpSession;
import common.session.id.ISessionIdGenerator;
import org.apache.commons.lang3.StringUtils;
import org.tio.utils.cache.ICache;

import java.io.File;
import java.io.IOException;

/**
 * @Author fyp
 * @Description
 * @Date Created at 2018/1/17 13:17
 * @Project tio-http-server
 */
public class HttpConfig {
    /**
     * 存放HttpSession对象的cacheName
     */
    public static final String SESSION_CACHE_NAME = "tio-h-s";

    /**
     * 存放sessionId的cookie name
     */
    public static final String SESSION_COOKIE_NAME = "PHPSESSID";

    /**
     * session默认的超时时间，单位：秒
     */
    public static final long DEFAULT_SESSION_TIMEOUT = 30 * 60;

    /**
     * 默认的静态资源缓存时间，单位：秒
     */
    public static final int MAX_LIVETIME_OF_STATICRES = 60 * 10;

    /**
     * 文件上传时，boundary值的最大长度
     */
    public static final int MAX_LENGTH_OF_BOUNDARY = 256;

    /**
     * 文件上传时，头部的最大长度
     */
    public static final int MAX_LENGTH_OF_MULTI_HEADER = 128;

    /**
     * 文件上传时，体的最大长度
     */
    public static final int MAX_LENGTH_OF_MULTI_BODY = 1024 * 1024 * 20;

    /**
     * 是否使用session
     */
    private boolean useSession = true;

    private String bindIp = null;//"127.0.0.1";

    /**
     * 监听端口
     */
    private Integer bindPort = 80;

    private String serverInfo = HttpConst.SERVER_INFO;

    private String charset = HttpConst.CHARSET_NAME;

    private ICache sessionStore = null;

    private String contextPath = "";

    private String suffix = "";

    /**
     * 允许访问的域名，如果不限制，则为null
     */
    private String[] allowDomains = null;

    /**
     * 存放HttpSession对象的cacheName
     */
    private String sessionCacheName = SESSION_CACHE_NAME;

    /**
     * session超时时间，单位：秒
     */
    private long sessionTimeout = DEFAULT_SESSION_TIMEOUT;

    private String sessionCookieName = SESSION_COOKIE_NAME;

    /**
     * 静态资源缓存时间，如果小于等于0则不缓存，单位：秒
     */
    private int maxLiveTimeOfStaticRes = MAX_LIVETIME_OF_STATICRES;

    private String page404 = "/404.html";

    private String page500 = "/500.html";

    private ISessionIdGenerator sessionIdGenerator;

    private HttpRequestHandler httpRequestHandler;

    /**
     * 是否被代理
     */
    private boolean isProxied = false;

    /**
     * 示例：
     * 1、classpath中：page
     * 2、绝对路径：/page
     */
    private File pageRoot = null;//FileUtil.getAbsolutePath("page");//"/page";

    /**
     *
     * @author tanyaowu
     */
    public HttpConfig(Integer bindPort, Long sessionTimeout, String contextPath, String suffix) {
        this.bindPort = bindPort;
        if (sessionTimeout != null) {
            this.sessionTimeout = sessionTimeout;
        }

        if (contextPath == null) {
            contextPath = "";
        }
        this.contextPath = contextPath;

        if (suffix == null) {
            suffix = "";
        }
        this.suffix = suffix;
    }

    //	private File rootFile = null;


    /**
     * @return the bindIp
     */
    public String getBindIp() {
        return bindIp;
    }

    /**
     * @return the bindPort
     */
    public Integer getBindPort() {
        return bindPort;
    }

    /**
     * @return the charset
     */
    public String getCharset() {
        return charset;
    }

    /**
     * @return the maxLiveTimeOfStaticRes
     */
    public int getMaxLiveTimeOfStaticRes() {
        return maxLiveTimeOfStaticRes;
    }

    public String getPage404() {
        return page404;
    }

    public String getPage500() {
        return page500;
    }

    /**
     * @return the pageRoot
     */
    public File getPageRoot() {
        return pageRoot;
    }

    /**
     * @return the serverInfo
     */
    public String getServerInfo() {
        return serverInfo;
    }

    /**
     * @return the sessionCacheName
     */
    public String getSessionCacheName() {
        return sessionCacheName;
    }

    public String getSessionCookieName() {
        return sessionCookieName;
    }

    public ISessionIdGenerator getSessionIdGenerator() {
        return sessionIdGenerator;
    }

    public ICache getSessionStore() {
        return sessionStore;
    }

    public long getSessionTimeout() {
        return sessionTimeout;
    }

    /**
     * @param bindIp the bindIp to set
     */
    public void setBindIp(String bindIp) {
        this.bindIp = bindIp;
    }

    /**
     * @param charset the charset to set
     */
    public void setCharset(String charset) {
        this.charset = charset;
    }

    /**
     * @param maxLiveTimeOfStaticRes the maxLiveTimeOfStaticRes to set
     */
    public void setMaxLiveTimeOfStaticRes(int maxLiveTimeOfStaticRes) {
        this.maxLiveTimeOfStaticRes = maxLiveTimeOfStaticRes;
    }

    public void setPage404(String page404) {
        this.page404 = page404;
    }

    public void setPage500(String page500) {
        this.page500 = page500;
    }

    /**
     *
     * @param pageRoot 如果是以"classpath:"开头，则从classpath中查找，否则视为普通的文件路径
     * @author tanyaowu
     * @throws IOException
     */
    public void setPageRoot(String pageRoot) throws IOException {
        if (pageRoot == null) {
            return;
        }

        if (StringUtils.startsWithIgnoreCase(pageRoot, "classpath:")) {
            this.pageRoot = new File(FileUtil.getAbsolutePath(pageRoot));
        } else {
            this.pageRoot = new File(pageRoot);
        }
    }

    /**
     * @param serverInfo the serverInfo to set
     */
    public void setServerInfo(String serverInfo) {
        this.serverInfo = serverInfo;
    }

    /**
     * @param sessionCacheName the sessionCacheName to set
     */
    public void setSessionCacheName(String sessionCacheName) {
        this.sessionCacheName = sessionCacheName;
    }

    public void setSessionCookieName(String sessionCookieName) {
        this.sessionCookieName = sessionCookieName;
    }

    public void setSessionIdGenerator(ISessionIdGenerator sessionIdGenerator) {
        this.sessionIdGenerator = sessionIdGenerator;
    }

    public void setSessionStore(ICache sessionStore) {
        this.sessionStore = sessionStore;
        //		this.httpSessionManager = HttpSessionManager.getInstance(sessionStore);

    }

    /**
     * @return the httpRequestHandler
     */
    public HttpRequestHandler getHttpRequestHandler() {
        return httpRequestHandler;
    }

    /**
     * @param httpRequestHandler the httpRequestHandler to set
     */
    public void setHttpRequestHandler(HttpRequestHandler httpRequestHandler) {
        this.httpRequestHandler = httpRequestHandler;
    }

    public String getContextPath() {
        return contextPath;
    }

    public String getSuffix() {
        return suffix;
    }

    public String[] getAllowDomains() {
        return allowDomains;
    }

    public void setAllowDomains(String[] allowDomains) {
        this.allowDomains = allowDomains;
    }

    /**
     * @return the isProxied
     */
    public boolean isProxied() {
        return isProxied;
    }

    /**
     * @param isProxied the isProxied to set
     */
    public void setProxied(boolean isProxied) {
        this.isProxied = isProxied;
    }

    public boolean isUseSession() {
        return useSession;
    }

    public void setUseSession(boolean useSession) {
        this.useSession = useSession;
    }

    /**
     * 根据sessionId获取HttpSession对象
     * @param sessionId
     * @return
     */
    public HttpSession getHttpSession(String sessionId) {
        if (StringUtils.isBlank(sessionId)) {
            return null;
        }
        HttpSession httpSession = (HttpSession) getSessionStore().get(sessionId);
        return httpSession;
    }
}
