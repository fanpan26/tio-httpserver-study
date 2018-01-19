package server.session;

import common.Cookie;

/**
 * @Author fyp
 * @Description
 * @Date Created at 2018/1/18 16:27
 * @Project tio-http-server
 */
public interface  SessionCookieDecorator {
    /**
     * DefaultHttpRequestHandler根据host字段创建了用于session的cookie，用户可以通过本方法定制一下Cookie，
     * 譬如把cookie的域名由www.baidu.com改成.baidu.com
     * @param sessionCookie
     * @author: tanyaowu
     */
    void decorate(Cookie sessionCookie);
}
