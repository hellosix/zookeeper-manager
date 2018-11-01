package cn.hellosix.zookeeper.service.impl;

import cn.hellosix.zookeeper.entity.Constants;
import cn.hellosix.zookeeper.entity.ZKParam;
import cn.hellosix.zookeeper.service.IFileService;
import cn.hellosix.zookeeper.service.IZKService;
import cn.hellosix.zookeeper.utils.CompressUtils;
import cn.hellosix.zookeeper.utils.FileUtils;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.apache.curator.framework.CuratorFramework;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.util.List;
import java.util.concurrent.*;

/**
 * @author Jay.H.Zou
 * @date 2018/10/15
 */
@Service
public class FileService implements IFileService, ApplicationListener<ContextRefreshedEvent> {

    @Autowired
    private IZKService zkService;

    private static ExecutorService executorService = new ThreadPoolExecutor(
            5,
            5,
            60L,
            TimeUnit.SECONDS, new SynchronousQueue<>(),
            new ThreadFactoryBuilder().setNameFormat("Write File pool-thread-%d").build(),
            new ThreadPoolExecutor.CallerRunsPolicy());

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        executorService = Executors.newFixedThreadPool(1);
    }


    @Override
    public String download(ZKParam param) {
        CuratorFramework client = zkService.getClient(param.getZkAddress());
        String nodePath = param.getZkPath();
        try {
            batchWrite(client, nodePath, Constants.ROOT_PATH);
        } catch (Exception e) {
            e.printStackTrace();
        }
        String[] splitPath = nodePath.split("/");
        String parentNode = splitPath[splitPath.length - 1];
        /*if(splitPath.length == 0) {
            parentNode = Constants.ZOOKEEPER;
        } else {
            parentNode = splitPath[splitPath.length - 1];
        }*/
        String targetFolderPath = Constants.ROOT_PATH + parentNode;
        String newZipFilePath = Constants.ROOT_PATH + parentNode + Constants.ZIP;
        try {
            CompressUtils.compress(targetFolderPath, newZipFilePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return newZipFilePath;
    }

    private void batchWrite(CuratorFramework client, String nodePath, String downDir) throws Exception {
        String[] splitPath = nodePath.split("/");
        /*String parentDir;
        if(splitPath.length == 0) {
            parentDir = Constants.ZOOKEEPER;
        } else {
            parentDir = splitPath[splitPath.length - 1];
        }*/

        String[] splitNodePath = nodePath.split("/");
        // 最后一个 “/”后的节点作为目录，创建文件夹

        String parentDir = splitNodePath[splitNodePath.length - 1];
        String currentDownDir = downDir + parentDir + "/";

        byte[] data = client.getData().forPath(nodePath);

        // 先判断是否有子目录
        List<String> childrenList = client.getChildren().forPath(nodePath);
        // 没有子节点，则不将当前节点视为文件夹，且认为它是单个文件
        if (childrenList == null || childrenList.isEmpty()) {
            if (data != null) {
                FileUtils.writeFile(data, downDir + parentDir);
            }
        } else {
            // 认为此节点可能是文件夹，并创建文件夹
            FileUtils.mkdir(downDir + parentDir);
            if(data != null) {
                // 当其有子节点，且其本身也有数据，情况很小
                String fileName = parentDir + Constants._FILE;
                FileUtils.writeFile(data, currentDownDir + fileName);
                for (String childPath : childrenList) {
                    batchWrite(client, nodePath + "/" + childPath, currentDownDir);
                }
            } else {
                for (String childPath : childrenList) {
                    batchWrite(client, nodePath + "/" + childPath, currentDownDir);
                }
            }
        }

    }

    private void deleteFiles() throws IOException {
        long currentTimeMillis = System.currentTimeMillis();

        Path rootPath = Paths.get(Constants.ROOT_PATH);
        DirectoryStream<Path> paths = Files.newDirectoryStream(rootPath);
        for (Path childPath : paths) {
            FileTime lastModifiedTime = Files.getLastModifiedTime(childPath);
            if ((currentTimeMillis - lastModifiedTime.toMillis()) / (1000 * 60 * 60 * 24) >= 7) {
                Files.walkFileTree(childPath, new SimpleFileVisitor<Path>() {

                    @Override
                    public FileVisitResult visitFile(Path file, BasicFileAttributes attributes) throws IOException {
                        Files.delete(file);
                        return FileVisitResult.CONTINUE;
                    }

                    @Override
                    public FileVisitResult postVisitDirectory(Path dir, IOException e) throws IOException {
                        Files.delete(dir);
                        return FileVisitResult.CONTINUE;
                    }

                });
            }
        }

    }

}
