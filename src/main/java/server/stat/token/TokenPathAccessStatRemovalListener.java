package server.stat.token;

import com.github.benmanes.caffeine.cache.RemovalCause;
import com.github.benmanes.caffeine.cache.RemovalListener;
import org.tio.core.GroupContext;

/**
 * @Author fyp
 * @Description
 * @Date Created at 2018/1/18 16:31
 * @Project tio-http-server
 */
/**
 * @author tanyaowu
 * 2017年8月21日 下午1:32:32
 */
@SuppressWarnings("rawtypes")
public class TokenPathAccessStatRemovalListener implements RemovalListener {
    private TokenPathAccessStatListener tokenPathAccessStatListener;

    private GroupContext groupContext = null;

    /**
     *
     * @author: tanyaowu
     */
    public TokenPathAccessStatRemovalListener(GroupContext groupContext, TokenPathAccessStatListener tokenPathAccessStatListener) {
        this.groupContext = groupContext;
        this.tokenPathAccessStatListener = tokenPathAccessStatListener;
    }

    @Override
    public  void onRemoval(Object key, Object value, RemovalCause cause) {
        String token = (String) key;
        TokenAccessStat tokenAccessStat = (TokenAccessStat) value;
        if (tokenPathAccessStatListener != null) {
            tokenPathAccessStatListener.onExpired(groupContext, token, tokenAccessStat);
        }
    }
}