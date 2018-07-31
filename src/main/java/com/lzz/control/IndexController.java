package com.lzz.control;

import com.lzz.model.ZKParam;
import com.lzz.util.ZKnodeUtil;
import net.sf.json.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

/**
 * Created by lzz on 17/5/7.
 */
@Controller
public class IndexController {
    @RequestMapping("/index")
    public String treeAdmin(Model model) {
        return "index";
    }


    @RequestMapping(value="/get_all_path_ajax", method = RequestMethod.GET)
    @ResponseBody
    public JSONObject getAllPath(@RequestParam String zk,
                                 @RequestParam(value="path", defaultValue="/") String path){
        if( !path.startsWith("/") ){
            return new JSONObject();
        }
        JSONObject jsonObject = ZKnodeUtil.getAllPath( zk, path );
        System.out.println( jsonObject );
        return jsonObject;
    }

    @RequestMapping(value="/get_path_detail_ajax", method = RequestMethod.GET)
    @ResponseBody
    public JSONObject getPathDetail(@RequestParam String zk,
                                    @RequestParam(value="path", defaultValue="/") String path){
        if( !path.startsWith("/") ){
            return new JSONObject();
        }
        JSONObject nodeDetail = ZKnodeUtil.getDetailPath( zk, path );
        return nodeDetail;
    }

    @RequestMapping(value="/update_node", method = RequestMethod.POST)
    @ResponseBody
    public JSONObject updatePathData(@RequestBody ZKParam zkParam){
        JSONObject nodeDetail = new JSONObject();
        try {
            nodeDetail = ZKnodeUtil.updatePathData(zkParam);
        } catch (Exception e) {
            nodeDetail.put("data", e.getMessage() );
        }
        return nodeDetail;
    }

    @RequestMapping(value="/delete_node", method = RequestMethod.POST)
    @ResponseBody
    public boolean deleteNode(@RequestBody ZKParam zkParam){
        boolean res = true;
        try {
            res =  ZKnodeUtil.deletePath(zkParam);
        } catch (Exception e) {
            res = false;
        }
        return res;
    }
}
