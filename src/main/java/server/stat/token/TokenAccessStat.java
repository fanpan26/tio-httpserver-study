package server.stat.token;

import com.xiaoleilu.hutool.date.BetweenFormater;
import org.tio.utils.SystemTimer;
import org.tio.utils.lock.MapWithLock;

import java.io.Serializable;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @Author fyp
 * @Description
 * @Date Created at 2018/1/18 16:30
 * @Project tio-http-server
 */
public class TokenAccessStat  implements Serializable{
    private static final long serialVersionUID = 5314797979230623121L;

    private MapWithLock<String,TokenPathAccessStat> tokenPathAccessStatMap = new MapWithLock<String, TokenPathAccessStat>(new HashMap<>());

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

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    private Long durationType;
    private String ip;
    private String uid;
    private long duration;
    public long getDuration(){
        duration = SystemTimer.currentTimeMillis() - this.firstAccessTime;
        return duration;
    }
    public  void setDuration(long duration){
        this.duration = duration;
    }

    private String token;

    private long firstAccessTime = SystemTimer.currentTimeMillis();
    private long lastAccessTime = SystemTimer.currentTimeMillis();

    public final AtomicInteger count  = new AtomicInteger();

    public final AtomicLong timeCost = new AtomicLong();
    public TokenPathAccessStat get(String path){
        return get(path,true);
    }
    public TokenPathAccessStat get(String path,boolean forceCreate){
        if (path == null){
            return null;
        }
        TokenPathAccessStat tokenPathAccessStat = tokenPathAccessStatMap.get(path);
        if(tokenPathAccessStat == null && forceCreate){
            tokenPathAccessStat = tokenPathAccessStatMap.putIfAbsent(path,new TokenPathAccessStat(durationType,token,path,ip,uid));
        }
        return tokenPathAccessStat;
    }

    public TokenAccessStat(Long durationType, String token, String ip, String uid) {
        this.durationType = durationType;
        this.token = token;
        this.ip = ip;
        this.setUid(uid);
    }

    public MapWithLock<String, TokenPathAccessStat> getTokenPathAccessStatMap() {
        return tokenPathAccessStatMap;
    }
    public void setTokenPathAccessStatMap(MapWithLock<String, TokenPathAccessStat> tokenPathAccessStatMap) {
        this.tokenPathAccessStatMap = tokenPathAccessStatMap;
    }
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
    public void setLastAccessTime(long lastAccessTime) {
        this.lastAccessTime = lastAccessTime;
    }
    public long getLastAccessTime() {
        return lastAccessTime;
    }
}
