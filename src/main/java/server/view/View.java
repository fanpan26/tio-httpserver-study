package server.view;

import common.HttpRequest;

/**
 * @Author fyp
 * @Description
 * @Date Created at 2018/1/18 16:34
 * @Project tio-http-server
 */
public interface View {
    String[] getSuffixes();
    String output(String path, String content, HttpRequest request);
    ModelGenerator getModelGenerator();
}
