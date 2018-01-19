package server.intf;

import common.HttpRequest;
import common.HttpResponse;
import common.RequestLine;

/**
 * @Author fyp
 * @Description
 * @Date Created at 2018/1/18 16:26
 * @Project tio-http-server
 */
public interface ThrowableHandler {
     HttpResponse handler(HttpRequest request, RequestLine requestLine, Throwable throwable);
}
