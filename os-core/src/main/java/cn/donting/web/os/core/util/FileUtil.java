package cn.donting.web.os.core.util;

import cn.donting.web.os.api.wap.FileType;
import cn.donting.web.os.api.wap.WapInfo;
import org.springframework.http.MediaType;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;

public class FileUtil {
    /**
     * 复制 source 子文件到  target文件夹下
     *
     * @param source 源目录，源目录
     * @param target 目标目录
     * @throws IOException IO异常
     */
    public static void copyFolder(Path source, Path target) throws IOException {
        Files.walkFileTree(source, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                Path targetDir = target.resolve(source.relativize(dir));
                Files.createDirectories(targetDir);
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                Files.copy(file, target.resolve(source.relativize(file)), StandardCopyOption.REPLACE_EXISTING);
                return FileVisitResult.CONTINUE;
            }
        });
    }

    /**
     * 负责文件
     *
     * @param source 源文件
     * @param target 目标文件，不存在自动创建
     * @throws IOException IO异常
     */
    public static void copyFile(URL source, File target) throws IOException {
        if (!target.exists()) {
            target.getParentFile().mkdirs();
            target.createNewFile();
        }
        URLConnection connection = source.openConnection();
        try (InputStream inputStream = connection.getInputStream();
             OutputStream outputStream = new FileOutputStream(target)) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
        }
    }

    /**
     * 删除文件或 目录
     *
     * @param file 文件 或 目录
     * @throws IOException IO异常
     */
    public static void deleteFile(File file) throws IOException {
        if (!file.exists()) {
            return;
        }
        if (file.isFile()) {
            file.delete();
            return;
        }
        Files.walkFileTree(file.toPath(), new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                Files.delete(file);
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                Files.delete(dir);
                return FileVisitResult.CONTINUE;
            }
        });
        file.delete();
    }

    /**
     * 获取文件的 名称和扩展名
     *
     * @param file 文件
     * @return [fileName, extName] extName 小写
     */
    public static String[] splitNameAndExt(File file) {
        if (!file.exists()) {
            return new String[]{file.getName(), FileType.ext_name_unknown};
        }
        if (file.isDirectory()) {
            return new String[]{file.getName(), FileType.ext_name_Directory};
        }
        String fileName = file.getName();
        return splitNameAndExt(fileName);
    }
    public static String extName(String fileName) {
        return splitNameAndExt(fileName)[1];
    }

    public static String[] splitNameAndExt(String fileName) {
        // 如果文件名为空，返回一个空数组
        if (fileName == null || fileName.isEmpty()) {
            return new String[]{"", FileType.ext_name_unknown};
        }
        int lastDotIndex = fileName.lastIndexOf(".");

        // 如果没有找到扩展名分隔符，返回文件名和一个空的扩展名
        if (lastDotIndex == -1) {
            return new String[]{fileName, FileType.ext_name_unknown};
        }
        String name = fileName.substring(0, lastDotIndex);
        String extension = fileName.substring(lastDotIndex + 1).toLowerCase();
        return new String[]{name, extension};
    }

    /**
     * 获取文件扩展名，
     *
     * @param file 文件
     * @return 小写扩展名
     */
    public static String extName(File file) {
        String[] splitNameAndExt = splitNameAndExt(file);
        return splitNameAndExt[1];
    }

    /**
     * 根据文件名获取 图像类型
     * @param name 文件名
     */
    public static String getImageContentType(String name) {
        String[] nameAndExt = splitNameAndExt(name);
        String extName = nameAndExt[1];
        return "image/"+extName;

    }

    public static void main(String[] args) {
    }
}
