package cn.hellosix.zookeeper.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.file.*;

/**
 * 判断系统环境，根据系统环境创建相应的 zookeeper-manager-data 目录
 *
 * @author Jay.H.Zou
 * @date 2018/10/24
 */
public class FileUtils {

    /**
     * 创建文件夹
     *
     * @param fileDir
     * @throws Exception
     */
    public static void mkdir(String fileDir) throws Exception {
        Path path = Paths.get(fileDir);
        boolean pathExists = Files.exists(path, new LinkOption[]{ LinkOption.NOFOLLOW_LINKS});
        if(!pathExists) {
            path.toFile().mkdirs();
        }
    }

    /**
     * 写单个文件
     *
     * @param originData
     * @param filePath
     */
    public static void writeFile(byte[] originData, String filePath) throws IOException {
        // "data/logging.properties"
        Path path = Paths.get(filePath);
        boolean pathExists = Files.exists(path, new LinkOption[]{ LinkOption.NOFOLLOW_LINKS});
        if (!pathExists) {
            Files.createFile(path);
            Files.write(
                    path,
                    originData,
                    StandardOpenOption.TRUNCATE_EXISTING
            );
        }
    }

}
