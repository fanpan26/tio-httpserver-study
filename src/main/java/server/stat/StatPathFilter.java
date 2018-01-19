package server.stat;

import common.HttpRequest;
import common.HttpResponse;

/**
 * @Author fyp
 * @Description
 * @Date Created at 2018/1/18 16:28
 * @Project tio-http-server
 */
public interface StatPathFilter {

    /**
     * @return true 统计， false 不统计
     * */
    boolean filter(String path, HttpRequest request, HttpResponse response);
}
