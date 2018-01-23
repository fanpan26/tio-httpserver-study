package server;

import common.GroupContextKey;
import common.HttpConfig;
import common.HttpUuid;
import common.handler.HttpRequestHandler;
import common.session.id.impl.UUIDSessionIdGenerator;
import org.tio.server.AioServer;
import org.tio.server.ServerGroupContext;
import org.tio.utils.cache.ICache;
import org.tio.utils.cache.caffeine.CaffeineCache;
import org.tio.utils.thread.pool.SynThreadPoolExecutor;
import server.handler.DefaultHttpRequestHandler;
import server.intf.HttpServerInterceptor;
import server.intf.HttpSessionListener;
import server.mvc.Routes;

import java.io.IOException;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @Author fyp
 * @Description
 * @Date Created at 2018/1/18 16:19
 * @Project tio-http-server
 */
public class HttpServerStarter {
    private HttpConfig httpConfig = null;

    private HttpServerAioHandler httpServerAioHandler = null;

    //	private HttpGroupListener httpGroupListener = null;


    private HttpServerAioListener httpServerAioListener = null;

    private ServerGroupContext serverGroupContext = null;

    private AioServer aioServer = null;

    private HttpRequestHandler httpRequestHandler;



    /**

     *

     * @param httpConfig

     * @param requestHandler

     * @author tanyaowu

     */
    public HttpServerStarter(HttpConfig httpConfig, HttpRequestHandler requestHandler) {
        this(httpConfig, requestHandler, null, null);
    }

    /**

     *

     * @param httpConfig

     * @param requestHandler

     * @param tioExecutor

     * @param groupExecutor

     * @author tanyaowu

     */
    public HttpServerStarter(HttpConfig httpConfig, HttpRequestHandler requestHandler, SynThreadPoolExecutor tioExecutor, ThreadPoolExecutor groupExecutor) {
        init(httpConfig, requestHandler, tioExecutor, groupExecutor);
    }

    /**

     * @deprecated

     * @param pageRoot 如果为null，则不提供静态资源服务

     * @param serverPort

     * @param contextPath

     * @param scanPackages

     * @param httpServerInterceptor

     * @author tanyaowu

     * @throws IOException

     */
    public HttpServerStarter(String pageRoot, int serverPort, String contextPath, String[] scanPackages, HttpServerInterceptor httpServerInterceptor) throws IOException {
        this(pageRoot, serverPort, contextPath, scanPackages, httpServerInterceptor, null, null, null);
    }

    /**

     * @deprecated

     * @param pageRoot 如果为null，则不提供静态资源服务

     * @param serverPort

     * @param contextPath

     * @param scanPackages

     * @param httpServerInterceptor

     * @param sessionStore

     * @author tanyaowu

     * @throws IOException

     */
    public HttpServerStarter(String pageRoot, int serverPort, String contextPath, String[] scanPackages, HttpServerInterceptor httpServerInterceptor, ICache sessionStore) throws IOException {
        this(pageRoot, serverPort, contextPath, scanPackages, httpServerInterceptor, sessionStore, null, null);
    }

    /**

     * @deprecated

     * pageRoot 如果为null，则不提供静态资源服务

     * @param pageRoot

     * @param serverPort

     * @param contextPath

     * @param scanPackages

     * @param httpServerInterceptor

     * @param sessionStore

     * @param tioExecutor

     * @param groupExecutor

     * @author tanyaowu

     * @throws IOException

     */
    public HttpServerStarter(String pageRoot, int serverPort, String contextPath, String[] scanPackages, HttpServerInterceptor httpServerInterceptor, ICache sessionStore,
                             SynThreadPoolExecutor tioExecutor, ThreadPoolExecutor groupExecutor) throws IOException {
        this(pageRoot, serverPort, contextPath, scanPackages, httpServerInterceptor, null, sessionStore, tioExecutor, groupExecutor);
    }

    /**

     * @deprecated

     * pageRoot 如果为null，则不提供静态资源服务

     * @param pageRoot

     * @param serverPort

     * @param contextPath

     * @param scanPackages

     * @param httpServerInterceptor

     * @param httpSessionListener

     * @param sessionStore

     * @param tioExecutor

     * @param groupExecutor

     * @author tanyaowu

     * @throws IOException

     */
    public HttpServerStarter(String pageRoot, int serverPort, String contextPath, String[] scanPackages, HttpServerInterceptor httpServerInterceptor, HttpSessionListener httpSessionListener, ICache sessionStore,
                             SynThreadPoolExecutor tioExecutor, ThreadPoolExecutor groupExecutor) throws IOException {
        int port = serverPort;

        httpConfig = new HttpConfig(port, null, contextPath, null);
        httpConfig.setPageRoot(pageRoot);
        if (sessionStore != null) {
            httpConfig.setSessionStore(sessionStore);
        }

        Routes routes = new Routes(scanPackages);
        DefaultHttpRequestHandler requestHandler = new DefaultHttpRequestHandler(httpConfig, routes);
        requestHandler.setHttpServerInterceptor(httpServerInterceptor);
        requestHandler.setHttpSessionListener(httpSessionListener);

        init(httpConfig, requestHandler, tioExecutor, groupExecutor);
    }

    /**

     * @return the httpConfig

     */
    public HttpConfig getHttpConfig() {
        return httpConfig;
    }

    public HttpRequestHandler getHttpRequestHandler() {
        return httpRequestHandler;
    }

    /**

     * @return the httpServerAioHandler

     */
    public HttpServerAioHandler getHttpServerAioHandler() {
        return httpServerAioHandler;
    }

    /**

     * @return the httpServerAioListener

     */
    public HttpServerAioListener getHttpServerAioListener() {
        return httpServerAioListener;
    }

    /**

     * @return the serverGroupContext

     */
    public ServerGroupContext getServerGroupContext() {
        return serverGroupContext;
    }

    private void init(HttpConfig httpConfig, HttpRequestHandler requestHandler, SynThreadPoolExecutor tioExecutor, ThreadPoolExecutor groupExecutor) {
        this.httpConfig = httpConfig;
        this.httpRequestHandler = requestHandler;
        httpConfig.setHttpRequestHandler(this.httpRequestHandler);
        this.httpServerAioHandler = new HttpServerAioHandler(httpConfig, requestHandler);
        httpServerAioListener = new HttpServerAioListener();
        serverGroupContext = new ServerGroupContext("Tio Http Server", httpServerAioHandler, httpServerAioListener, tioExecutor, groupExecutor);
        serverGroupContext.setHeartbeatTimeout(1000 * 20);
        serverGroupContext.setShortConnection(true);
        serverGroupContext.setAttribute(GroupContextKey.HTTP_SERVER_CONFIG, httpConfig);

        aioServer = new AioServer(serverGroupContext);

        HttpUuid imTioUuid = new HttpUuid();
        serverGroupContext.setTioUuid(imTioUuid);
    }

    public void setHttpRequestHandler(HttpRequestHandler requestHandler) {
        this.httpRequestHandler = requestHandler;
    }

    public void start() throws IOException {
        if (httpConfig.getSessionStore() == null) {
            CaffeineCache caffeineCache = CaffeineCache.register(httpConfig.getSessionCacheName(), null, httpConfig.getSessionTimeout());
            httpConfig.setSessionStore(caffeineCache);
        }

        if (httpConfig.getSessionIdGenerator() == null) {
            httpConfig.setSessionIdGenerator(UUIDSessionIdGenerator.instance);
        }

        aioServer.start(this.httpConfig.getBindIp(), this.httpConfig.getBindPort());
    }

    public void stop() throws IOException {
        aioServer.stop();
    }
}
