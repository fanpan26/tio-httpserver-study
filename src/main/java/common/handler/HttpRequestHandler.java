package common.handler;

import common.HttpRequest;
import common.HttpResponse;
import common.RequestLine;

/**
 * @Author fyp
 * @Description http请求处理
 * @Date Created at 2018/1/17 13:15
 * @Project tio-http-server
 */
public interface HttpRequestHandler {

    /**
     * 处理请求
     * */
    HttpResponse handler(HttpRequest packet) throws Exception;
    /**
     * 响应404
     * */
    HttpResponse resp404(HttpRequest request,RequestLine requestLine);
    /**
     * 响应500
     * */
    HttpResponse resp500(HttpRequest request, RequestLine requestLine,Throwable throwable);
    /**
     * 清除静态资源缓存
     * */
    void clearStaticResCache(HttpRequest request);
}
