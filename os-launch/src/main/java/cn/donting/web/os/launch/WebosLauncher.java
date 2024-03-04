package cn.donting.web.os.launch;


import java.io.File;

/**
 * web os 启动器，负责加载 os-core核心
 * user.dir下有 os-core.classpath.dev， 说明 核心处于开发者状态，使用 dev加载核心
 *
 * @see DevWebosLaunch
 * @see JarWebosLaunch
 */
public interface WebosLauncher {
    /**
     * wap-core 加载的目录
     * os-wap-launch
     * os-launch
     * 所在的目录
     */
    String loadDirPropertyKey="wap.dir";

    /**
     * 核心的 dev 开发启动文件
     * 由 build.gradle task   "classpath.dev"  生成
     */
    String coreDevName = "os-core.wev";

    void launch(String[] args) throws Exception;

    static void main(String[] args) throws Exception {
        StartMode startMode = startMode(args);
        System.out.printf("StartMode:"+startMode);
        if (startMode== StartMode.Source) {
            new DevWebosLaunch().launch(args);
            return;
        }
        if(startMode==StartMode.WapRun){
            String filePath = WebosLauncher.class.getProtectionDomain().getCodeSource().getLocation().getFile();
            File file = new File(filePath);
            File parentFile = file.getParentFile();
            for (File listFile : parentFile.listFiles()) {
                if (listFile.getName().startsWith("os-core") && listFile.getName().endsWith(".jar")) {
                    System.out.println("加载os-core-jar: " + listFile.getPath());
                    new JarWebosLaunch(listFile).launch(args);
                    return;
                }
            }
            throw new RuntimeException("JarWebosLaunch 未找到核心jar-> os-core*.jar " + file);
        }

        if(startMode==StartMode.Normal){
            String userDir = System.getProperty("user.dir");
            for (File listFile : new File(userDir).listFiles()) {

            }
            throw new RuntimeException("JarWebosLaunch 未找到核心jar-> os-core*.jar " + userDir);
        }
    }

    static StartMode startMode(String[] args) {
        String filePath = WebosLauncher.class.getProtectionDomain().getCodeSource().getLocation().getFile();
        File file = new File(filePath);
        if (file.isDirectory()) {
            return StartMode.Source;
        }
        for (String arg : args) {
            if (arg.equals("--startMode=WapRun")) {
                return StartMode.WapRun;
            }
        }
        return StartMode.Normal;
    }


    /**
     * 启动方式
     */
    static enum StartMode {
        /**
         * 源码启动
         */
        Source,
        /**
         * 开发者 gradle task 启动
         */
        WapRun,
        /**
         * 正常启动
         */
        Normal
    }
}
