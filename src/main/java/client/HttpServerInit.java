package client;

import common.HttpConfig;
import common.handler.HttpRequestHandler;
import org.tio.utils.SystemTimer;
import server.HttpServerStarter;
import server.handler.DefaultHttpRequestHandler;
import server.mvc.Routes;

/**
 * @Author fyp
 * @Description
 * @Date Created at 2018/1/22 16:03
 * @Project tio-http-server
 */
public class HttpServerInit {

    public static HttpConfig httpConfig;

    public static HttpRequestHandler requestHandler;

    public static HttpServerStarter httpServerStarter;

    public static void init() throws Exception {
        long start = SystemTimer.currentTimeMillis();

        String pageRoot = "classpath:";

        String[] scanPackages = new String[] { HttpServerDemoStarter.class.getPackage().getName() };//tio mvc需要扫描的根目录包



        httpConfig = new HttpConfig(8080, null, "/tio", ".php");
        httpConfig.setPageRoot(pageRoot);


        Routes routes = new Routes(scanPackages);
        DefaultHttpRequestHandler requestHandler = new DefaultHttpRequestHandler(httpConfig, routes);


        httpServerStarter = new HttpServerStarter(httpConfig, requestHandler);
        httpServerStarter.start();

        long end = SystemTimer.currentTimeMillis();
        long iv = end - start;
        System.out.println("Tio Http Server启动完毕,耗时:"+iv+"ms,访问地址:http://127.0.0.1:8080");
    }

    /**

     *

     * @author tanyaowu

     */
    public HttpServerInit() {
    }
}
