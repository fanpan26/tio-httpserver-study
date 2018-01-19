package server.stat.ip.path;

/**
 * @Author fyp
 * @Description
 * @Date Created at 2018/1/18 16:29
 * @Project tio-http-server
 */

import com.github.benmanes.caffeine.cache.RemovalCause;
import com.github.benmanes.caffeine.cache.RemovalListener;
import org.tio.core.GroupContext;

/**

 * @author tanyaowu

 * 2017年8月21日 下午1:32:32

 */
@SuppressWarnings("rawtypes")
public class IpPathAccessStatRemovalListener implements RemovalListener {

    private IpPathAccessStatListener ipPathAccessStatListener;

    private GroupContext groupContext = null;

    /**

     *

     * @author: tanyaowu

     */
    public IpPathAccessStatRemovalListener(GroupContext groupContext, IpPathAccessStatListener ipPathAccessStatListener) {
        this.groupContext = groupContext;
        this.ipPathAccessStatListener = ipPathAccessStatListener;
    }

    @Override
    public void onRemoval(Object key, Object value, RemovalCause cause) {
        String ip = (String) key;
        IpAccessStat ipAccessStat = (IpAccessStat) value;

        if (ipPathAccessStatListener != null) {
            ipPathAccessStatListener.onExpired(groupContext, ip, ipAccessStat);
        }


    }
}
