package cn.hellosix.zookeeper.controller;

import cn.hellosix.zookeeper.entity.Constants;
import cn.hellosix.zookeeper.entity.ZKParam;
import cn.hellosix.zookeeper.service.IFileService;
import cn.hellosix.zookeeper.service.IZKService;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * Created by lzz on 17/5/7.
 */
@Controller
@Slf4j
public class IndexController {

    @Autowired
    private IZKService zkService;

    @Autowired
    private IFileService fileService;

    @RequestMapping("/zookeeper")
    public String treeAdmin() {
        return "index";
    }


    @RequestMapping(value = "/getAllPath", method = RequestMethod.GET)
    @ResponseBody
    public JSONObject getAllPath(@RequestParam String address,
                                 @RequestParam(value = "path", defaultValue = "/") String path) {
        if (!path.startsWith(Constants.QUERY_PARAM_PATH_PREFIX)) {
            return new JSONObject();
        }
        ZKParam param = new ZKParam();
        param.setZkAddress(address);
        param.setZkPath(path);
        JSONObject jsonObject = zkService.getAllPath(param);
        return jsonObject;
    }

    @RequestMapping(value = "/getPathDetail", method = RequestMethod.GET)
    @ResponseBody
    public JSONObject getPathDetail(@RequestParam String address,
                                    @RequestParam(value = "path", defaultValue = "/") String path) {
        if (!path.startsWith(Constants.QUERY_PARAM_PATH_PREFIX)) {
            return new JSONObject();
        }
        ZKParam param = new ZKParam();
        param.setZkAddress(address);
        param.setZkPath(path);

        JSONObject nodeDetail = zkService.getDetail(param);
        return nodeDetail;
    }

    @RequestMapping(value = "/updateNode", method = RequestMethod.POST)
    @ResponseBody
    public JSONObject updatePathData(@RequestBody ZKParam zkParam) {
        JSONObject nodeDetail = new JSONObject();
        try {
            nodeDetail = zkService.updateNodeData(zkParam);
        } catch (Exception e) {
            nodeDetail.put("data", e.getMessage());
        }
        return nodeDetail;
    }

    @RequestMapping(value = "/deleteMode", method = RequestMethod.POST)
    @ResponseBody
    public boolean deleteNode(@RequestBody ZKParam zkParam) {
        boolean res = false;
        try {
            res = zkService.removeNode(zkParam);
        } catch (Exception e) {
            res = false;
        }
        return res;
    }

    @RequestMapping(value = "/download", method = RequestMethod.POST)
    public void download(HttpServletRequest request, HttpServletResponse response, String address, String nodePath) {
        response.setCharacterEncoding(request.getCharacterEncoding());
        response.setContentType("application/octet-stream");
        ZKParam param = new ZKParam();
        param.setZkAddress(address);
        param.setZkPath(nodePath);
        String zipName = fileService.download(param);
        FileInputStream fis = null;
        try {
            File file = new File(zipName);
            fis = new FileInputStream(file);
            response.setHeader("Content-Disposition", "attachment; filename=" + file.getName());
            IOUtils.copy(fis, response.getOutputStream());
            response.flushBuffer();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
