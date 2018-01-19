package common;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Author fyp
 * @Description
 * @Date Created at 2018/1/17 13:13
 * @Project tio-http-server
 */
public class Cookie {

    public static Cookie buildCookie(Map<String,String> cookieMap){
        Cookie cookie = new Cookie();
        for(Map.Entry<String,String> cookieMapItem :cookieMap.entrySet()){
            switch (cookieMapItem.getKey().toLowerCase()){
                case "domain":
                    cookie.setDomain(cookieMapItem.getValue());
                    break;
                case "path":
                    cookie.setPath(cookieMapItem.getValue());
                    break;
                case "max-age":
                    cookie.setMaxAge(Long.parseLong(cookieMapItem.getValue()));
                    break;
                case "secure":
                    cookie.setSecure(true);
                    break;
                case "httponly":
                    cookie.setHttpOnly(true);
                    break;
                case "expires":
                    cookie.setExpires(cookieMapItem.getValue());
                    break;
                default:
                    //设置 name 和 value 的值
                    cookie.setName(cookieMapItem.getKey());
                    try{
                        cookie.setValue(URLDecoder.decode(cookieMapItem.getValue(),HttpConst.CHARSET_NAME));
                    }catch (UnsupportedEncodingException e){

                    }
                    break;
            }
        }
        return cookie;
    }

    /**
     * 拆解 cookie string
     * @return Map<String,String>
     * */
    public static Map<String,String> getEqualMap(String cookieLine){
        Map<String,String> equalMap = new HashMap<>();
        String[] searchedStrings = searchRByRegex(cookieLine,"([^ ;,]+=[^ ;,]+)");
        for(String groupString : searchedStrings){
            String[] equalStrings = new String[2];
            int equalCharIndex = groupString.indexOf("=");
            equalStrings[0] = groupString.substring(0,equalCharIndex);
            equalStrings[1] = groupString.substring(equalCharIndex+1,groupString.length());

            if(equalStrings.length == 2){
                String key = equalStrings[0];
                String value = equalStrings[1];
                if(value.startsWith("\"") && value.endsWith("\"")){
                    value = value.substring(1,value.length()-1);
                }
                equalMap.put(key,value);
            }
        }
        return null;
    }

    public static String[] searchRByRegex(String source,String regex){
        if(source == null){ return null; }

        Map<Integer,Pattern> regexPattern = new HashMap<>();
        Pattern pattern = null;
        if(regexPattern.containsKey(regex.hashCode())){
            pattern = regexPattern.get(regex.hashCode());
        }else{
            pattern = Pattern.compile(regex);
            regexPattern.put(regex.hashCode(),pattern);
        }
        Matcher matcher = pattern.matcher(source);
        ArrayList<String> result = new ArrayList<>();
        while (matcher.find()){
            result.add(matcher.group());
        }
        return result.toArray(new String[0]);
    }

    public Cookie(){}

    public Cookie(String domain,String name,String value,Long maxAge){
        setName(name);
        setValue(value);
        setPath("/");
        setDomain(domain);
        setMaxAge(maxAge);
        setHttpOnly(false);
    }

    private String domain;
    private String path;

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Long getMaxAge() {
        return maxAge;
    }

    public void setMaxAge(Long maxAge) {
        this.maxAge = maxAge;
    }

    public String getExpires() {
        return expires;
    }

    public void setExpires(String expires) {
        this.expires = expires;
    }

    public boolean isSecure() {
        return secure;
    }

    public void setSecure(boolean secure) {
        this.secure = secure;
    }

    public boolean isHttpOnly() {
        return httpOnly;
    }

    public void setHttpOnly(boolean httpOnly) {
        this.httpOnly = httpOnly;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    private Long maxAge;
    private String expires = null;
    private boolean secure = false;
    private  boolean httpOnly = false;
    private String name ;
    private String value;

    @Override
    public String toString() {
        return (this.name != null || this.value != null ? this.name + "=" + this.value : "") + (this.domain != null ? "; Domain=" + this.domain : "")
                + (this.maxAge != null ? "; Max-Age=" + this.maxAge : "") + (this.path != null ? "; Path=" + this.path : " ") + (this.httpOnly ? "; httponly; " : "")
                + (this.secure ? "; Secure" : "");
    }
}
