package cn.hellosix.zookeeper.utils;

import cn.hellosix.zookeeper.entity.Constants;
import cn.hellosix.zookeeper.service.IZKService;
import org.apache.curator.framework.CuratorFramework;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;
import java.util.List;

/**
 * @author Jay.H.Zou
 * @date 2018/10/30
 */
@SpringBootTest()
@RunWith(SpringJUnit4ClassRunner.class)
public class FileUtilsTest {

    @Autowired
    private IZKService zkService;

    @Test
    public void testBatchWrite() {
        CuratorFramework client = zkService.getClient("101.132.108.223:2181");
        String zkPath = "/my-conf";
        String downPath = "data/";
        try {
            batchWrite(client, zkPath, downPath);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void batchWrite(CuratorFramework client, String nodePath, String downDir) throws Exception {
        String[] splitNodePath = nodePath.split("/");
        // 最后一个 “/”后的节点作为目录，创建文件夹
        String parentDir = splitNodePath[splitNodePath.length - 1];
        String currentDownDir = downDir + parentDir + "/";

        byte[] data = client.getData().forPath(nodePath);

        // 先判断是否有子目录
        List<String> childrenList = client.getChildren().forPath(nodePath);
        // 没有子节点，则不将挡墙节点视为文件夹，且认为它是单个文件
        if (childrenList == null || childrenList.isEmpty()) {
            if (data != null) {
                FileUtils.writeFile(data, downDir + parentDir);
            }
        } else {
            // 认为此节点可能是文件夹，并创建文件夹
            FileUtils.mkdir(downDir + parentDir);
            if(data != null) {
                // 当其有子节点，且其本身也有数据，情况很小
                String fileName = parentDir + "_file";
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

    @Test
    public void zip() throws IOException {

        /*String targetFolderPath = Constants.ROOT_PATH + "conf";
        String newZipFilePath = Constants.ROOT_PATH + "conf.zip";

        //将目标目录的文件压缩成Zip文件
        CompressUtils.compress(targetFolderPath , newZipFilePath);*/

        String root = "/";
        System.out.println(root.split("/").length);
    }


}
