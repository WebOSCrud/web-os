package cn.donting.web.os.core.file;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;

@FunctionalInterface
public interface FileWatch {
    /**
     * @see
     * @param file
     * @param pathKind
     */
    void watch (File file, WatchEvent.Kind pathKind);
}
