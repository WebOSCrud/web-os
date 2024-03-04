package cn.donting.web.os.core.file;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 文件监控服务类
 */
@Slf4j
public class FileWatchService {



    /**
     * 把一个文件添加进 监听器
     * @param file
     */
    public static FileWatchThread addWatchFile(File file, FileWatch fileWatch, WatchEvent.Kind<Path>... kinds) throws IOException {
        return new FileWatchThread(file,fileWatch,kinds);
    }

    public static class FileWatchThread extends Thread{
        private File file;
        private File dir;
        private WatchService watchService;
        private FileWatch fileWatch;
        public FileWatchThread(File file,FileWatch fileWatch, WatchEvent.Kind<Path>... kinds) throws IOException {
            this.file = file;
            this.fileWatch = fileWatch;
            watchService = FileSystems.getDefault().newWatchService();
            if(file.isFile()){
                //必须监听文件夹
                file.toPath().getParent().register(watchService, kinds);
                dir=file.getParentFile();
            }else {
                throw new RemoteException("不支持文件夹监听");
            }
        }

        @Override
        public void run() {
            while (true){
                try {
                    WatchKey take = watchService.take();
                    List<WatchEvent<?>> watchEvents = take.pollEvents();
                    for (WatchEvent<?> watchEvent : watchEvents) {
                        if (watchEvent.kind()!= StandardWatchEventKinds.OVERFLOW) {
                            Path path = (Path)watchEvent.context();
                            Path resolve = dir.toPath().resolve(path);
                            if(resolve.equals(file.toPath())){
                                fileWatch.watch(resolve.toFile(),watchEvent.kind());
                            }
                        }
                    }
                    take.reset();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

            }
        }
    }

}
