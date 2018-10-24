package cn.hellosix.zookeeper.service.impl;

import cn.hellosix.zookeeper.entity.Constants;
import cn.hellosix.zookeeper.entity.FileDTO;
import cn.hellosix.zookeeper.entity.ZKParam;
import cn.hellosix.zookeeper.service.IFileService2;
import cn.hellosix.zookeeper.service.IZKService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.curator.framework.CuratorFramework;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Comparator;
import java.util.concurrent.TimeUnit;

/**
 * 下载、上传文件实现
 *
 * @author Zenuo
 * @date 2018/9/22
 */


public class FileService2 implements IFileService2 {

    private static final Log logger = LogFactory.getLog(FileService2.class);
    /**
     * ZK服务实例
     */

    @Autowired
    private final IZKService zkService;


    /**
     * 暂存目录
     * todo 根据配置文件读取暂存目录
     */

    private final Path tempDir = Paths.get(System.getProperty("java.io.tmpdir"), "zookeeper-manager");


    /**
     * 构造方法
     *
     * @param zkService ZK服务，由Spring注入
     */

    public FileService2(@Autowired IZKService zkService) {
        //创建暂存目录
        makeTempDir();
        //注入
        this.zkService = zkService;
        //JVM关闭时，删除暂存目录
        Runtime.getRuntime().addShutdownHook(new Thread(this::deleteTempDir));
        //创建刪除过期的暂存文件线程
        final Thread deleteExpiredTempFileThread = new Thread(this::deleteExpiredTempFile, "zk-manager-deleteExpiredTempFile");
        //开始线程
        deleteExpiredTempFileThread.start();
        logger.info("初始化完成");
    }


    /**
     * {@inheritDoc}
     */

    @Override
    public FileDTO download(ZKParam param, Boolean recursive) throws Exception {
        //获取客户端实例
        try (final CuratorFramework client = zkService.getClient(param.getZkAddress())) {
            //获取数据
            final byte[] bytes = client.getData().forPath(param.getZkPath());
            //todo 实现判断是否有子节点，若有，则打包
            //文件路径
            final Path tempFilePath = Files.createTempFile(
                    tempDir,
                    Constants.TEMP_FILE_PREFIX,
                    Constants.TEMP_FILE_SUFFIX);
            //写入数据
            Files.write(
                    tempFilePath,
                    bytes,
                    StandardOpenOption.TRUNCATE_EXISTING);
            logger.info("暂存文件[{}]写入[{}]字节数据" + tempFilePath + " " + bytes.length);
            //节点名称 todo 由ZK的API获取节点名称
            final String nodeName = param.getZkPath()
                    .substring(1)
                    .replaceAll(Constants.QUERY_PARAM_PATH_PREFIX, "_");
            //返回DTO
            return null;
            /*return FileDTO.builder()
                    .name(nodeName)
                    .path(tempFilePath)
                    .build();*/
        }
    }


    /**
     * 创建暂存目录
     */

    private void makeTempDir() {
        //尝试创建
        final boolean mkdirs = tempDir.toFile().mkdirs();
        if (mkdirs) {
            //创建成功
            logger.info("创建暂存目录[{}]完成" + tempDir);
        } else {
            //创建失败
            final String message = "创建暂存目录" + tempDir + "失败";
            logger.error(message);
            //抛出异常
            throw new IllegalStateException(message);
        }
    }

    /**
     * 删除暂存目录
     */

    private void deleteTempDir() {
        try {
            //遍历目录
            Files.walk(tempDir)
                    //逆序
                    .sorted(Comparator.reverseOrder())
                    .forEach(path -> {
                        try {
                            //删除
                            Files.delete(path);
                        } catch (IOException e) {
                            logger.error("删除文件异常", e);
                        }
                    });
        } catch (IOException e) {
            logger.error("删除暂存目录异常", e);
        }
    }


    /**
     * 刪除过期的暂存文件
     */

    private void deleteExpiredTempFile() {
        while (!Thread.currentThread().isInterrupted()) {
            //当前时间毫秒数
            final long currentTimeMillis = System.currentTimeMillis();
            //遍历暂存目录
            try {
                Files.list(tempDir)
                        .map(Path::toFile)
                        //超时的文件
                        .filter(file -> file.lastModified() - currentTimeMillis > Constants.TEMP_FILE_EXPIRE_TIME_IN_MILLISECONDS)
                        //删除
                        .forEach(File::delete);
            } catch (IOException e) {
                logger.error("刪除过期的暂存文件异常", e);
            }
            //等待
            try {
                TimeUnit.MINUTES.sleep(Constants.TEMP_FILE_EXPIRE_TIME_IN_MINUTES);
            } catch (InterruptedException e) {
                logger.error("线程中断异常");
            }
        }
    }
}

