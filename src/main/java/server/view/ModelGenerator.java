package server.view;

import common.HttpRequest;

/**
 * @Author fyp
 * @Description
 * @Date Created at 2018/1/18 16:34
 * @Project tio-http-server
 */
public interface ModelGenerator {
    Object generate(HttpRequest request);
}
