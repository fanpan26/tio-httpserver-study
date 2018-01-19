package server.stat.ip.path;

import common.HttpRequest;
import org.tio.core.GroupContext;

/**
 * @Author fyp
 * @Description
 * @Date Created at 2018/1/18 16:29
 * @Project tio-http-server
 */
public interface IpPathAccessStatListener {

    /**
     *
     * @param groupContext
     * @param ip
     * @param ipAccessStat
     * @author tanyaowu
     */
     void onExpired(GroupContext groupContext, String ip, IpAccessStat ipAccessStat);

    /**
     *
     * @param httpRequest
     * @param ip
     * @param path
     * @param ipAccessStat
     * @param ipPathAccessStat
     * @author tanyaowu
     */
     boolean onChanged(HttpRequest httpRequest, String ip, String path, IpAccessStat ipAccessStat, IpPathAccessStat ipPathAccessStat);


}