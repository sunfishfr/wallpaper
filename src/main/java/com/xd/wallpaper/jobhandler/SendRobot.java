package com.xd.wallpaper.jobhandler;

import com.alibaba.fastjson.JSONObject;
import com.xd.wallpaper.picdownload.MakePaper;
import com.xd.wallpaper.wechat.PicInfo;
import com.xd.wallpaper.wechat.WxChatbotClient;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.XxlJob;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class SendRobot {

    @Value("${Wx_webhook}")
    private String webhook;

    @XxlJob("sendRobotHandler")
    public ReturnT<String> sendRobot(String s) {
        String path = MakePaper.create();
        JSONObject jsonObject = PicInfo.getPicInfo(path);
        String MD5 = jsonObject.getString("MD5");
        String BASE64 = jsonObject.getString("BASE64");
        boolean b;
        try {
            b = WxChatbotClient.send(webhook, MD5, BASE64);
        } catch (IOException e) {
            e.printStackTrace();
            return new ReturnT<String>(IJobHandler.FAIL.getCode(), "SEND_FAIL");
        }
        if (b) {
            return new ReturnT<String>(IJobHandler.SUCCESS.getCode(), "SEND_SUCCESS");
        }
        return new ReturnT<String>(IJobHandler.SUCCESS.getCode(), "SEND_SUCCESS");
    }

    public static void main(String args[]) {
        JSONObject jsonObject = PicInfo.getPicInfo("D:\\test\\poster_8723558213272666885.jpg");
        String MD5 = jsonObject.getString("MD5");
        String BASE64 = jsonObject.getString("BASE64");
        try {
            WxChatbotClient.send("https://qyapi.weixin.qq.com/cgi-bin/webhook/send?key=15c082cb-1ee4-40c9-a622-d0633771c2b7", MD5, BASE64);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
