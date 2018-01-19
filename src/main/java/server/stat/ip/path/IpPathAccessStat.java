package server.stat.ip.path;

import com.xiaoleilu.hutool.date.BetweenFormater;
import org.tio.utils.SystemTimer;

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @Author fyp
 * @Description
 * @Date Created at 2018/1/18 16:29
 * @Project tio-http-server
 */
public class IpPathAccessStat implements Serializable {
    private static final long serialVersionUID = 5314797979230623121L;

    /**
     * key:   path, 形如："/user/login"
     * value: 访问的次数
     */
    //	private MapWithLock<String, AtomicInteger> mapWithLock = new MapWithLock<>(new HashMap<>());


    private Long durationType;

    /**
     * 当前统计了多久，单位：毫秒
     */
    private long duration;

    /**
     * ip
     */
    private String ip;

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
     * 这个ip访问这个路径的次数
     */
    public final AtomicInteger count = new AtomicInteger();

    /**
     * 这个ip访问这个路径给服务器带来的时间消耗，单位：毫秒
     */
    public final AtomicLong timeCost = new AtomicLong();

    /**
     * 不带session的次数
     */
    public final AtomicInteger noSessionCount = new AtomicInteger();

    /**
     * @author tanyaowu
     */
    public IpPathAccessStat(Long durationType, String ip, String path) {
        this.durationType = durationType;
        this.ip = ip;
        this.path = path;
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
        double perSecond = ((double)count / (double)duration) * (double)1000;
        return perSecond;
    }

    public Long getDurationType() {
        return durationType;
    }

    public void setDurationType(Long durationType) {
        this.durationType = durationType;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
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
}