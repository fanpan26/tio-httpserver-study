package server.intf;

import common.HttpConfig;
import common.HttpRequest;
import common.session.HttpSession;

/**
 * @Author fyp
 * @Description
 * @Date Created at 2018/1/18 16:26
 * @Project tio-http-server
 */
public interface HttpSessionListener {
    void doAfterCreated(HttpRequest request, HttpSession session, HttpConfig httpConfig);
}
