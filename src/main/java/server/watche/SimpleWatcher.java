package server.watche;

import com.xiaoleilu.hutool.io.watch.Watcher;

import java.nio.file.Path;
import java.nio.file.WatchEvent;

/**
 * @Author fyp
 * @Description
 * @Date Created at 2018/1/18 16:36
 * @Project tio-http-server
 */
public class SimpleWatcher implements Watcher{
    @Override
    public void onCreate(WatchEvent<?> watchEvent, Path path) {
        Object obj = watchEvent.context();
        System.out.println("创建：" + obj);
    }

    @Override
    public void onDelete(WatchEvent<?> watchEvent, Path path) {
        Object obj = watchEvent.context();
        System.out.println("删除：" + obj);
    }

    @Override
    public void onModify(WatchEvent<?> watchEvent, Path path) {
        Object obj = watchEvent.context();
        System.out.println("修改：" + obj);
    }

    @Override
    public void onOverflow(WatchEvent<?> watchEvent, Path path) {
        Object obj = watchEvent.context();
        System.out.println("Overflow：" + obj);
    }
}
