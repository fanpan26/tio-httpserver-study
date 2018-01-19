package server.view.freemarker;

import common.HttpRequest;

/**
 * @Author fyp
 * @Description
 * @Date Created at 2018/1/18 16:36
 * @Project tio-http-server
 */
public interface ModelMaker {
    Object maker(HttpRequest request);
}
