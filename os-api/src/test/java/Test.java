import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

public class Test {
    public static void main(String[] args) throws IOException {
        File file = new File("./text.txt");
        String absolutePath = file.getAbsolutePath();
        absolutePath=absolutePath.replaceFirst("\\.\\","");
        System.out.println("Absolute Path: " + absolutePath);

//        File file = new File("D:\\test\\web-os2\\os-launch\\build\\libs\\os-wap-test-0.0.3.jar");
//        JarFile jarFile = new JarFile(file);
//        Manifest manifest = jarFile.getManifest();
//        String value = manifest.getMainAttributes().getValue("Start-Class");
//        Enumeration<JarEntry> entries = jarFile.entries();
//        String absolutePath = file.getAbsoluteFile().getAbsolutePath();
////        jar:file:/D:/test/web-os2/os-launch/build/libs/os-wap-test-0.0.3.jar!/BOOT-INF/lib/spring-webmvc-5.3.29.jar!/
//        while (entries.hasMoreElements()) {
//            JarEntry jarEntry = entries.nextElement();
//            String name = jarEntry.getName();
//            if (name.startsWith("BOOT-INF/lib/") && name.endsWith(".jar")) {
//                URL url = new URL("jar:file:/" + absolutePath + "!/" + name + "!/");
//                System.out.println("jar:file:/" + absolutePath + "!/" + name + "!/");
//                System.out.println(url.getPath());
//            }
//        }
//        System.out.println();
    }
}
