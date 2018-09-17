package org.hellosix.zookeeper.controller;

import com.alibaba.fastjson.JSONObject;
import org.hellosix.zookeeper.entity.ZKParam;
import org.hellosix.zookeeper.service.IZKService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

/**
 * Created by lzz on 17/5/7.
 */
@Controller
public class IndexController {

    @Autowired
    private IZKService zkService;

    @RequestMapping("/")
    public String treeAdmin() {
        return "index";
    }


    @RequestMapping(value="/getAllPath", method = RequestMethod.GET)
    @ResponseBody
    public JSONObject getAllPath(@RequestParam String address,
                                 @RequestParam(value="path", defaultValue="/") String path){
        if( !path.startsWith("/") ){
            return new JSONObject();
        }
        ZKParam param = new ZKParam();
        param.setZkAddress(address);
        param.setZkPath(path);
        JSONObject jsonObject = zkService.getAllPath(param);
        return jsonObject;
    }

    @RequestMapping(value="/getPathDetail", method = RequestMethod.GET)
    @ResponseBody
    public JSONObject getPathDetail(@RequestParam String address,
                                    @RequestParam(value="path", defaultValue="/") String path){
        if( !path.startsWith("/") ){
            return new JSONObject();
        }
        ZKParam param = new ZKParam();
        param.setZkAddress(address);
        param.setZkPath(path);

        JSONObject nodeDetail = zkService.getDetail(param);
        return nodeDetail;
    }

    @RequestMapping(value="/updateNode", method = RequestMethod.POST)
    @ResponseBody
    public JSONObject updatePathData(@RequestBody ZKParam zkParam){
        JSONObject nodeDetail = new JSONObject();
        try {
            nodeDetail = zkService.updateNodeData(zkParam);
        } catch (Exception e) {
            nodeDetail.put("data", e.getMessage() );
        }
        return nodeDetail;
    }

    @RequestMapping(value="/deleteMode", method = RequestMethod.POST)
    @ResponseBody
    public boolean deleteNode(@RequestBody ZKParam zkParam){
        boolean res = false;
        try {
            res =  zkService.removeNode(zkParam);
        } catch (Exception e) {
            res = false;
        }
        return res;
    }
}
