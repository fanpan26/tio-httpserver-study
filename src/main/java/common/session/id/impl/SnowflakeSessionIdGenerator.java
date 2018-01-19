package common.session.id.impl;

import com.xiaoleilu.hutool.lang.Snowflake;
import common.HttpConfig;
import common.HttpRequest;
import common.session.id.ISessionIdGenerator;

/**
 * @Author fyp
 * @Description
 * @Date Created at 2018/1/17 13:16
 * @Project tio-http-server
 */
public class SnowflakeSessionIdGenerator implements ISessionIdGenerator {

    private Snowflake snowflake;

    public SnowflakeSessionIdGenerator(int workerId, int datacenterId) {
        snowflake = new Snowflake(workerId, datacenterId);
    }

    @Override
    public String sessionId(HttpConfig httpConfig, HttpRequest request) {
        return snowflake.nextId() + "";
    }
}
