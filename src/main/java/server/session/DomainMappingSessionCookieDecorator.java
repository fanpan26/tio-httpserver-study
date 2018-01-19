package server.session;

import com.xiaoleilu.hutool.util.ReUtil;
import common.Cookie;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;
import java.util.Set;

/**
 * @Author fyp
 * @Description
 * @Date Created at 2018/1/18 16:27
 * @Project tio-http-server
 */
public class DomainMappingSessionCookieDecorator implements SessionCookieDecorator {
    /**

     * key:    原始domain，譬如:www.baidu.com，也可以是正则表达式，譬如*.baidu.com

     * value : 替换原始domain的domain，譬如.baidu.com

     */
    private Map<String, String> domainMap = null;

    /**

     *

     * @author: tanyaowu

     */
    public DomainMappingSessionCookieDecorator(Map<String, String> domainMap) {
        this.domainMap = domainMap;
    }

    protected DomainMappingSessionCookieDecorator() {

    }

    public void addMapping(String key, String value) {
        domainMap.put(key, value);
    }

    public void removeMapping(String key) {
        domainMap.remove(key);
    }

    /**

     * @param sessionCookie

     * @author: tanyaowu

     */
    @Override
    public void decorate(Cookie sessionCookie) {
        Set<Map.Entry<String, String>> set = domainMap.entrySet();
        String initDomain = sessionCookie.getDomain();
        for (Map.Entry<String, String> entry : set) {
            String key = entry.getKey();
            String value = entry.getValue();
            if (StringUtils.equalsIgnoreCase(key, initDomain) || ReUtil.isMatch(key, initDomain)) {
                sessionCookie.setDomain(value);
            }
        }
    }
}
