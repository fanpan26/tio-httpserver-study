package server.session;

import common.Cookie;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author fyp
 * @Description
 * @Date Created at 2018/1/18 16:27
 * @Project tio-http-server
 */
public class DomainSessionCookieDecorator implements SessionCookieDecorator{

    private String domain;
    private DomainMappingSessionCookieDecorator domainMappingSessionCookieDecorator;


    public DomainSessionCookieDecorator(String domain){
        this.domain = domain;

        Map<String,String> domainMap = new HashMap<>();
        domainMap.put("(\\w)*(" + domain + "){1}", domain);

        domainMappingSessionCookieDecorator = new DomainMappingSessionCookieDecorator(domainMap);
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    @Override
    public void decorate(Cookie sessionCookie) {
        domainMappingSessionCookieDecorator.decorate(sessionCookie);
    }
}
