package server.stat.token;

import common.HttpRequest;

/**
 * @Author fyp
 * @Description
 * @Date Created at 2018/1/18 16:30
 * @Project tio-http-server
 */
public interface TokenGetter {

    String getToken(HttpRequest request);
}
