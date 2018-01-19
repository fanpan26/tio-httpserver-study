package server.handler;

import common.Cookie;
import common.HttpConfig;
import common.HttpRequest;

/**
 * @Author fyp
 * @Description
 * @Date Created at 2018/1/18 16:25
 * @Project tio-http-server
 */
public class DefaultHttpRequestHandler {

    public static Cookie getSessionCookie(HttpRequest request, HttpConfig httpConfig) {
        Cookie sessionCookie = request.getCookie(httpConfig.getSessionCookieName());
        return sessionCookie;
    }
}
