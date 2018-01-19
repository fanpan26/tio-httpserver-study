package common.session.id;

import common.HttpConfig;
import common.HttpRequest;

/**
 * @Author fyp
 * @Description
 * @Date Created at 2018/1/17 13:15
 * @Project tio-http-server
 */
public interface ISessionIdGenerator {

    String sessionId(HttpConfig httpConfig, HttpRequest request);
}
