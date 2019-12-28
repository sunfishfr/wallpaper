package com.xd.wallpaper.picdownload;


import com.alibaba.fastjson.JSONObject;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.file.Files;

import static com.xd.wallpaper.picdownload.PosterUtil.*;

public class MakePaper {
    private static Logger logger = LoggerFactory.getLogger(MakePaper.class);

    public static String create() {
        try {
            logger.debug("根据输入的路径采集网络图片");
            JSONObject jsonObject = Search.getPicAndPoem("http://news.iciba.com/");
            logger.info(jsonObject.toJSONString());
            String picURL = jsonObject.getString("img");
            String zh = jsonObject.getString("zh");
            String en = jsonObject.getString("en");
            // 背景
            File bgFile = FileUtil.read("bg_", ".jpg", "img/bg.jpg");
            // 请求在虚拟机终止时删除此抽象路径名所表示的文件或目录。
            bgFile.deleteOnExit();

            // BufferedImage 使用可访问的图像数据缓冲区描述图像，由颜色模型和图像数据栅格组成。
            // 所有 BufferedImage 对象的左上角坐标为(0，0)。
            BufferedImage bgImage = ImageIO.read(bgFile);

            // 绘制封面图
            // 封面图

            File picFile =PicDownload.capture(picURL);
            picFile.deleteOnExit();
            BufferedImage picImage = ImageIO.read(picFile);
            Graphics2DPoster graphics2dPoster = drawImage(bgImage, picImage);

            graphics2dPoster.setZh(zh);
            drawZhString(graphics2dPoster);

            if (StringUtils.isNotEmpty(en)) {
                graphics2dPoster.setEn(en);
                drawEnString(graphics2dPoster);
            }

            // 二维码
            File qrcodeFile = FileUtil.read("qrcode_", ".png", "img/logo.png");
            qrcodeFile.deleteOnExit();
            BufferedImage qrcodeImage = ImageIO.read(qrcodeFile);
            graphics2dPoster.setQrcodeImage(qrcodeImage);
            drawQrcode(graphics2dPoster);

            // 释放图形上下文，以及它正在使用的任何系统资源。
            graphics2dPoster.getGraphics2d().dispose();

            File posterFile = Files.createTempFile(FileUtil.DIRECTORY, "poster_", ".jpg").toFile();
            ImageIO.write(graphics2dPoster.getBgImage(), "jpg", posterFile);

            return posterFile.getAbsolutePath();
        } catch (Exception e) {
            logger.error(e.getMessage());
            logger.error(e.getMessage(), e);
            return "海报生成失败";
        }
    }

    public static void main(String args[]){
        MakePaper.create();
    }
}
