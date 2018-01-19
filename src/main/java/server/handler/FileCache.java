package server.handler;

import common.HttpRequest;
import common.HttpResponse;

/**
 * @Author fyp
 * @Description
 * @Date Created at 2018/1/18 16:25
 * @Project tio-http-server
 */
public class FileCache implements java.io.Serializable{
    private static final long serialVersionUID = 6517890350387789902L;

    private  long lastModified;
    private HttpResponse response;

    public FileCache(){}
    public FileCache(HttpResponse response,long lastModified){
        super();
        this.response = response;
        this.lastModified = lastModified;
    }

    public long getLastModified() {
        return lastModified;
    }

    public void setLastModified(long lastModified) {
        this.lastModified = lastModified;
    }

    public HttpResponse getResponse() {
        return response;
    }

    public void setResponse(HttpResponse response) {
        this.response = response;
    }

    public HttpResponse cloneResponse(HttpRequest request){
        HttpResponse ret = new HttpResponse(request);
        ret.setBody(response.getBody());
        ret.setHasGzipped(response.isHasGzipped());
        ret.setHeaders(response.getHeaders());
        return ret;
    }
}
