package com.xd.wallpaper.picdownload;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.font.FontDesignMetrics;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PosterUtil {
    private static Logger logger = LoggerFactory.getLogger(PosterUtil.class);

    /**
     * 留白
     */
    private static final int MARGIN = 25;
    /**
     * 使用的字体
     */
    private static final String USE_FONT_NAME = "黑体";

    public static void drawQrcode(Graphics2DPoster graphics2dPoster) {
        BufferedImage qrcodeImage = graphics2dPoster.getQrcodeImage();
        BufferedImage bgImage = graphics2dPoster.getBgImage();

        // 二维码起始坐标
        int qrcode_x = bgImage.getWidth() - qrcodeImage.getWidth() - MARGIN;
        int qrcode_y = bgImage.getHeight() - qrcodeImage.getHeight() - MARGIN-20;

        Graphics2D graphics2d = graphics2dPoster.getGraphics2d();
        graphics2d.drawImage(qrcodeImage, qrcode_x, qrcode_y, qrcodeImage.getWidth(), qrcodeImage.getHeight(), null);

        // 追加二维码描述文本
        graphics2d.setColor(Color.BLUE);
        Font font = new Font(USE_FONT_NAME, Font.PLAIN, 22);
        graphics2d.setFont(font);
        FontDesignMetrics metrics = FontDesignMetrics.getMetrics(graphics2d.getFont());

        graphics2d.drawString("仙迪信息中心制", qrcode_x+94, qrcode_y+85);
    }

    public static void drawEnString(Graphics2DPoster graphics2dPoster) throws IOException {
        // 设置封面图和下方中文之间的距离
        graphics2dPoster.addCurrentY(20);

        Graphics2D graphics2d = graphics2dPoster.getGraphics2d();
        graphics2d.setColor(Color.red);

        FontDesignMetrics metrics = FontDesignMetrics.getMetrics(graphics2d.getFont());
        String enWrap = FontUtil.makeEnLineFeed(graphics2dPoster.getEn(), metrics, graphics2dPoster.getSuitableWidth());
        String[] enWraps = enWrap.split("\n");
        for (int i = 0; i < enWraps.length; i++) {
            graphics2dPoster.addCurrentY(metrics.getHeight());
            graphics2d.drawString(enWraps[i], MARGIN, graphics2dPoster.getCurrentY());
        }

    }

    public static Graphics2DPoster drawZhString(Graphics2DPoster graphics2dPoster) throws IOException {
        // 获取计算机上允许使用的中文字体
        String[] fontNames = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();

        List<String> strings = new ArrayList<>();
        for (String i:fontNames){
            strings.add(i);
        }

        if (fontNames == null || !strings.contains(USE_FONT_NAME)) {
            throw new RuntimeException("计算机上未安装" + USE_FONT_NAME + "的字体");
        }

        // 设置封面图和下方中文之间的距离
        graphics2dPoster.addCurrentY(40);

        Graphics2D graphics2d = graphics2dPoster.getGraphics2d();
        graphics2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        // Font 的构造参数依次是字体名字，字体式样，字体大小
        Font font = new Font(USE_FONT_NAME, Font.BOLD, 24);
        graphics2d.setFont(font);
        graphics2d.setColor(Color.GREEN);

        FontDesignMetrics metrics = FontDesignMetrics.getMetrics(font);
        graphics2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER));

        String zhWrap = FontUtil.makeZhLineFeed(graphics2dPoster.getZh(), metrics, graphics2dPoster.getSuitableWidth());
        String[] zhWraps = zhWrap.split("\n");
        for (int i = 0; i < zhWraps.length; i++) {
            graphics2dPoster.addCurrentY(metrics.getHeight());
            graphics2d.drawString(zhWraps[i], MARGIN, graphics2dPoster.getCurrentY());
        }

        return graphics2dPoster;
    }

    public static Graphics2DPoster drawImage(BufferedImage bgImage, BufferedImage picImage) throws IOException {
        // 封面图的起始坐标
        int pic_x = MARGIN, pic_y = MARGIN;
        // 封面图的宽度
        int pic_width = bgImage.getWidth() - MARGIN * 2;
        // 封面图的高度
        int pic_height = picImage.getHeight() * pic_width / picImage.getWidth();

        // Graphics2D 类扩展 Graphics 类，以提供对几何形状、坐标转换、颜色管理和文本布局更为复杂的控制。
        Graphics2D graphics2d = bgImage.createGraphics();
        Graphics2DPoster graphics2dPoster = new Graphics2DPoster(graphics2d);
        // 海报可容纳的宽度
        graphics2dPoster.setSuitableWidth(pic_width);
        graphics2dPoster.setPicImage(picImage);
        graphics2dPoster.setBgImage(bgImage);

        // 在背景上绘制封面图
        graphics2d.drawImage(picImage, pic_x, pic_y, pic_width, pic_height, null);


        // 记录此时的 y 坐标
        graphics2dPoster.setCurrentY(pic_y + pic_height);

        return graphics2dPoster;
    }

}
