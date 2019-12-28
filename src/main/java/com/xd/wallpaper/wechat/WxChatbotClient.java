package com.xd.wallpaper.wechat;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

public class WxChatbotClient {
    static HttpClient httpclient = HttpClients.createDefault();

    public static Boolean send(String webhook,String picMd5,String picBase64) throws IOException {

        if(StringUtils.isBlank(webhook)){
            return false;
        }
        HttpPost httppost = new HttpPost(webhook);
        httppost.addHeader("Content-Type", "application/json; charset=utf-8");
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("msgtype","image");
        JSONObject jsonObject1 = new JSONObject();
        jsonObject1.put("base64",picBase64);
        jsonObject1.put("md5",picMd5);
        jsonObject.put("image",jsonObject1);
        StringEntity se = new StringEntity(jsonObject.toJSONString(), "utf-8");
        httppost.setEntity(se);
        HttpResponse response = httpclient.execute(httppost);
        if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
            String result = EntityUtils.toString(response.getEntity());
            JSONObject obj = JSONObject.parseObject(result);
            Integer errcode = obj.getInteger("errcode");
                        return false;
        }
        return true;
    }

}
