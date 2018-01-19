package server.stat.token;

import com.xiaoleilu.hutool.date.BetweenFormater;
import org.tio.utils.SystemTimer;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @Author fyp
 * @Description
 * @Date Created at 2018/1/18 16:30
 * @Project tio-http-server
 */
public class TokenPathAccessStat {
    /**
     *
     */
    private static final long serialVersionUID = 3463584577647075747L;

    private Long durationType;

    /**
     * 当前统计了多久，单位：毫秒
     */
    private long duration;

    /**
     * token
     */
    private String token;

    private String ip;

    private String uid;

    /**
     * 路径
     */
    private String path;

    /**
     * 第一次访问时间， 单位：毫秒
     */
    private long firstAccessTime = SystemTimer.currentTimeMillis();

    /**
     * 最近一次访问时间， 单位：毫秒
     */
    private long lastAccessTime = SystemTimer.currentTimeMillis();

    /**
     * 这个token访问这个路径的次数
     */
    public final AtomicInteger count = new AtomicInteger();

    /**
     * 这个token访问这个路径给服务器带来的时间消耗，单位：毫秒
     */
    public final AtomicLong timeCost = new AtomicLong();


    /**
     *
     * @param durationType
     * @param token
     * @param path
     * @param ip
     * @param uid
     */
    public TokenPathAccessStat(Long durationType, String token, String path, String ip, String uid) {
        this.durationType = durationType;
        this.token = token;
        this.path = path;
        this.ip = ip;
        this.uid = uid;
    }

    /**
     * @return the duration
     */
    public String getFormatedDuration() {
        duration = SystemTimer.currentTimeMillis() - this.firstAccessTime;
        BetweenFormater betweenFormater = new BetweenFormater(duration, BetweenFormater.Level.MILLSECOND);
        return betweenFormater.format();
    }

    public double getPerSecond() {
        int count = this.count.get();
        long duration = getDuration();
        double perSecond = (double)((double)count / (double)duration) * (double)1000;
        return perSecond;
    }

    public Long getDurationType() {
        return durationType;
    }

    public void setDurationType(Long durationType) {
        this.durationType = durationType;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public long getFirstAccessTime() {
        return firstAccessTime;
    }

    public void setFirstAccessTime(long firstAccessTime) {
        this.firstAccessTime = firstAccessTime;
    }

    public long getLastAccessTime() {
        return lastAccessTime;
    }

    public void setLastAccessTime(long lastAccessTime) {
        this.lastAccessTime = lastAccessTime;
    }

    public long getDuration() {
        duration = SystemTimer.currentTimeMillis() - this.firstAccessTime;
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}