package common;

/**
 * @Author fyp
 * @Description
 * @Date Created at 2018/1/17 13:19
 * @Project tio-http-server
 */
public class RequestLine {
    private Method method;
    private String path; //譬如http://www.163.com/user/get?name=tan&id=789，那些此值就是/user/get
    private String initPath; //同path，只是path可能会被业务端修改，而这个是记录访问者访问的最原始path的
    private String query; //譬如http://www.163.com/user/get?name=tan&id=789，那些此值就是name=tan&id=789
    private String pathAndQuery;
    private String protocol;
    private String version;
    private String line;

    /**
     * @return the line
     */
    public String getLine() {
        return line;
    }

    /**
     * @return the method
     */
    public Method getMethod() {
        return method;
    }

    /**
     * 譬如http://www.163.com/user/get?name=tan&id=789，那些此值就是/user/get
     * @return the path
     */
    public String getPath() {
        return path;
    }

    public String getPathAndQuery() {
        return pathAndQuery;
    }

    /**
     * 譬如http://www.163.com/user/get?name=tan&id=789，那些此值就是name=tan&id=789
     * @return the query
     */
    public String getQuery() {
        return query;
    }

    /**
     * @return the version
     */
    public String getVersion() {
        return version;
    }

    /**
     * @param line the line to set
     */
    public void setLine(String line) {
        this.line = line;
    }

    /**
     * @param method the method to set
     */
    public void setMethod(Method method) {
        this.method = method;
    }

    /**
     * 譬如http://www.163.com/user/get?name=tan&id=789，那些此值就是/user/get
     * @param path the path to set
     */
    public void setPath(String path) {
        this.path = path;
    }

    public void setPathAndQuery(String pathAndQuery) {
        this.pathAndQuery = pathAndQuery;
    }

    /**
     * 譬如http://www.163.com/user/get?name=tan&id=789，那些此值就是name=tan&id=789
     * @param query the query to set
     */
    public void setQuery(String query) {
        this.query = query;
    }

    /**
     * @param version the version to set
     */
    public void setVersion(String version) {
        this.version = version;
    }

    /**
     * @return the protocol
     */
    public String getProtocol() {
        return protocol;
    }

    /**
     * @param protocol the protocol to set
     */
    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public String getInitPath() {
        return initPath;
    }

    public void setInitPath(String initPath) {
        this.initPath = initPath;
    }
}
