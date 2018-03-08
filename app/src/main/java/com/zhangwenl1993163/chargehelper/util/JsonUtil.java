package com.zhangwenl1993163.chargehelper.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by zhangwenliang on 2018/3/8.
 */

public class JsonUtil {
    public static List<Map<String,Object>> json2MapList(String json){
        List<Map<String,Object>> mapList = new ArrayList<>();
        JSONArray array = JSON.parseArray(json);
        for (int i = 0 ; i < array.size() ; i++){
            JSONObject jsonObject = array.getJSONObject(i);
            Map<String,Object> map = jsonObject.getInnerMap();
            mapList.add(map);
        }
        return mapList;
    }
}
