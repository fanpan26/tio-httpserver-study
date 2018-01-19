package server.stat.token;

import common.Cookie;
import common.HttpRequest;
import common.session.HttpSession;
import server.handler.DefaultHttpRequestHandler;

/**
 * @Author fyp
 * @Description
 * @Date Created at 2018/1/18 16:30
 * @Project tio-http-server
 */
public class DefaultTokenGetter implements TokenGetter{

    public static DefaultTokenGetter me = new DefaultTokenGetter();


    @Override
    public String getToken(HttpRequest request) {
        HttpSession httpSession = request.getHttpSession();
        if(httpSession != null){
            return httpSession.getId();
        }
        Cookie cookie = DefaultHttpRequestHandler.getSessionCookie(request, request.getHttpConfig());
        if (cookie != null) {
            return cookie.getValue();
        }
        return null;
    }
}
