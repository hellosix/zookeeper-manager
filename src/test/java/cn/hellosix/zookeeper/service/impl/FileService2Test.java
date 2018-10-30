package cn.hellosix.zookeeper.service.impl;

import cn.hellosix.zookeeper.entity.Constants;
import cn.hellosix.zookeeper.entity.FileDTO;
import cn.hellosix.zookeeper.entity.ZKParam;
import cn.hellosix.zookeeper.service.IFileService2;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ContentDisposition;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.nio.charset.StandardCharsets;

/**
 * 文件服务测试
 *
 * @author Zenuo
 * @date 2018/09/22
 */
@SpringBootTest()
@RunWith(SpringJUnit4ClassRunner.class)
public class FileService2Test {

    @Autowired
    private IFileService2 fileService2;

    @Test
    public void download() throws Exception {
        //构造参数实例
        final ZKParam param = new ZKParam();
        param.setZkAddress("127.0.0.1:2181");
        param.setZkPath("/zookeeper/test/test1");
        //调用
        final FileDTO fileDTO = fileService2.download(param, false);
        //断言
        //Assert.assertTrue(fileDTO.getPath().toFile().exists());
    }

    @Test
    public void contentDisposition() {
        final ContentDisposition build = ContentDisposition.builder(Constants.CONTENT_DISPOSITION_TYPE)
                .filename("汉字", StandardCharsets.UTF_8)
                .build();
        System.out.println(build.toString());
    }
}