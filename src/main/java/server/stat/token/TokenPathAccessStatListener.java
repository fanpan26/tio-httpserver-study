package server.stat.token;

import common.HttpRequest;
import org.tio.core.GroupContext;

/**
 * @Author fyp
 * @Description
 * @Date Created at 2018/1/18 16:31
 * @Project tio-http-server
 */
public interface TokenPathAccessStatListener {
    /**
     *
     * @param groupContext
     * @param token
     * @param tokenAccessStat
     * @author tanyaowu
     */
    public void onExpired(GroupContext groupContext, String token, TokenAccessStat tokenAccessStat);

    /**
     *
     * @param httpRequest
     * @param token
     * @param path
     * @param tokenAccessStat
     * @param tokenPathAccessStat
     * @author tanyaowu
     */
    public boolean onChanged(HttpRequest httpRequest, String token, String path, TokenAccessStat tokenAccessStat, TokenPathAccessStat tokenPathAccessStat);
}
