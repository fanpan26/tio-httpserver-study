package server.stat;

import common.HttpRequest;
import common.HttpResponse;

/**
 * @Author fyp
 * @Description
 * @Date Created at 2018/1/18 16:28
 * @Project tio-http-server
 */
public class DefaultStatPathFilter implements StatPathFilter{

    public static final DefaultStatPathFilter me = new DefaultStatPathFilter();
    public  DefaultStatPathFilter(){}

    @Override
    public boolean filter(String path, HttpRequest request, HttpResponse response) {
        return true;
    }
}
