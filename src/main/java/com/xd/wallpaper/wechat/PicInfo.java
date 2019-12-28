package com.xd.wallpaper.wechat;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.codec.digest.DigestUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;

public class PicInfo {
    public static JSONObject getPicInfo(String filepath) {
        Base64.Encoder encoder = Base64.getEncoder();
        File f;
        try {
            f = new File(filepath);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        String MD5 =null;
        try {
            MD5=  DigestUtils.md5Hex(new FileInputStream(filepath));
        } catch (IOException e) {
            e.printStackTrace();
        }

        Path path = Paths.get(f.getAbsolutePath());
        byte[] data;
        try {
            data = Files.readAllBytes(path);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        String encodedText = encoder.encodeToString(data);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("BASE64",encodedText);
        jsonObject.put("MD5",MD5);
        return jsonObject;
    }
}
