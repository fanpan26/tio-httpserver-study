package common.session.id.impl;

import com.xiaoleilu.hutool.util.RandomUtil;
import common.HttpConfig;
import common.HttpRequest;
import common.session.id.ISessionIdGenerator;

/**
 * @Author fyp
 * @Description
 * @Date Created at 2018/1/17 13:16
 * @Project tio-http-server
 */
public class UUIDSessionIdGenerator implements ISessionIdGenerator{

    public final static UUIDSessionIdGenerator instance = new UUIDSessionIdGenerator();

    private UUIDSessionIdGenerator() {
    }

    @Override
    public String sessionId(HttpConfig httpConfig, HttpRequest request) {
        return RandomUtil.randomUUID().replace("-", "");
    }
}
