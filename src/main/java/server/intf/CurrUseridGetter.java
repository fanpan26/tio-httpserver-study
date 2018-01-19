package server.intf;

import common.HttpRequest;

/**
 * @Author fyp
 * @Description
 * @Date Created at 2018/1/18 16:25
 * @Project tio-http-server
 */
public interface CurrUseridGetter {
    /**
     * 获取当前用户ID
     * */
    String getUserid(HttpRequest request);
}
