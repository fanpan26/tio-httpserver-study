package server.intf;

import common.HttpRequest;
import common.HttpResponse;
import common.RequestLine;

/**
 * @Author fyp
 * @Description
 * @Date Created at 2018/1/18 16:25
 * @Project tio-http-server
 */
public interface HttpServerInterceptor {
    /**
     * 在执行org.tio.http.server.handler.IHttpRequestHandler.handler()前会先调用这个方法<br>
     * 如果返回了HttpResponse对象，则后续都不再执行，表示调用栈就此结束<br>
     * @param request
     * @param requestLine
     * @param responseFromCache 从缓存中获取到的HttpResponse对象
     * @return
     * @throws Exception
     * @author tanyaowu
     */
     HttpResponse doBeforeHandler(HttpRequest request, RequestLine requestLine, HttpResponse responseFromCache) throws Exception;

    /**
     * 在执行org.tio.http.server.handler.IHttpRequestHandler.handler()后会调用此方法，业务层可以统一在这里给HttpResponse作一些修饰
     * @param request
     * @param requestLine
     * @param response
     * @throws Exception
     */
     void doAfterHandler(HttpRequest request, RequestLine requestLine, HttpResponse response) throws Exception;
}
