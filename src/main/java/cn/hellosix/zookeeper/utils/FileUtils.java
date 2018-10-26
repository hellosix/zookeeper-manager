package cn.hellosix.zookeeper.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;

/**
 * 判断系统环境，根据系统环境创建相应的 zookeeper-manager-data 目录
 *
 * @author Jay.H.Zou
 * @date 2018/10/24
 */
public class FileUtils {

    /**
     * 写单个文件或创建文件夹
     *
     * @param origin
     * @param path
     */
    public static void writeFile(String origin, String path) {
        FileOutputStream fos = null;
        try {

            fos = new FileOutputStream(new File(path));
            FileChannel channel = fos.getChannel();
            ByteBuffer src = Charset.forName("utf8").encode("你好你好你好你好你好");
            // 字节缓冲的容量和limit会随着数据长度变化，不是固定不变的
            System.out.println("初始化容量和limit：" + src.capacity() + ","
                    + src.limit());
            int length = 0;

            while ((length = channel.write(src)) != 0) {
                /*
                 * 注意，这里不需要clear，将缓冲中的数据写入到通道中后 第二次接着上一次的顺序往下读
                 */
                System.out.println("写入长度:" + length);
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void zip(String path) {

    }

    public static void deleteFileDir(String path) {

    }
}
