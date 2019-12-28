package com.xd.wallpaper.picdownload;



import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;


public class PicDownload {
    private static Logger logger = LoggerFactory.getLogger(PicDownload.class);
    private static final Path DIRECTORY = Paths.get("D:\\test");
    public static  void  downloadPic() throws Exception {
        JSONObject jsonObject = Search.getPicAndPoem("http://news.iciba.com/");
        String picURL = jsonObject.getString("img");
        System.out.println(picURL);
        // 根据路径发起 HTTP get 请求
        HttpGet httpget = new HttpGet(picURL);
        // 使用 addHeader 方法添加请求头部
        httpget.addHeader("Content-Type", "text/html;charset=UTF-8");
        RequestConfig requestConfig = RequestConfig.custom().setConnectionRequestTimeout(500).setConnectTimeout(500)
                .setSocketTimeout(500).build();
        httpget.setConfig(requestConfig);
        CloseableHttpClient httpclient = HttpClientBuilder.create().build();
        CloseableHttpResponse response = httpclient.execute(httpget);
        HttpEntity entity = response.getEntity();
        InputStream picStream = entity.getContent();
        Path pic = Files.createTempFile(Paths.get("D:\\test"), "pic_", ".jpg");
        FileOutputStream fos = new FileOutputStream(pic.toFile());
        int read = 0;
        // 1024Byte(字节)=1KB 1024KB=1MB
        byte[] bytes = new byte[1024 * 100];
        while ((read = picStream.read(bytes)) != -1) {
            fos.write(bytes, 0, read);
        }

        fos.flush();
        fos.close();
    }

    /**
     * 从 HttpResponse 实例中获取状态码、错误信息、以及响应信息等等.
     *
     * @param response
     * @return
     * @throws IOException
     */
    private static HttpEntity handleResponse(final HttpResponse response) throws IOException {
        // 状态码
        final StatusLine statusLine = response.getStatusLine();
        // 获取响应实体
        final HttpEntity entity = response.getEntity();

        // 状态码一旦大于 300 表示请求失败
        if (statusLine.getStatusCode() >= 300) {
            EntityUtils.consume(entity);
            throw new HttpResponseException(statusLine.getStatusCode(), statusLine.getReasonPhrase());
        }
        return entity;
    }

    /**
     * 将输入流信息读入到临时文件中
     *
     * @param inputStream
     * @param prefix
     * @param suffix
     * @param
     * @return
     * @throws IOException
     */
    private static File createTmpFile(InputStream inputStream, String prefix, String suffix) throws IOException {
        // 在指定目录中创建一个新的空文件，使用给定的前缀和后缀字符串生成其名称。
        File tmpFile = Files.createTempFile(DIRECTORY, prefix, suffix).toFile();

        // 新建文件输出流对象
        FileOutputStream fos = new FileOutputStream(tmpFile);
        int read = 0;

        // 1024Byte(字节)=1KB 1024KB=1MB
        byte[] bytes = new byte[1024 * 100];
        while ((read = inputStream.read(bytes)) != -1) {
            fos.write(bytes, 0, read);
        }

        fos.flush();
        fos.close();
        return tmpFile;
    }

    public static File capture(String picURL ) throws Exception {

        // 金山词霸的图片路径
//        if (StringUtils.isEmpty(picURL)) {
//            String formatDate = DateFormatUtils.format(new Date(), "yyyyMMdd");
//            picURL = "http://cdn.iciba.com/news/word/big_" + formatDate + "b.jpg";
//        }

        // 根据路径发起 HTTP get 请求
        HttpGet httpget = new HttpGet(picURL);
        // 使用 addHeader 方法添加请求头部
        httpget.addHeader("Content-Type", "text/html;charset=UTF-8");

        // 配置请求的超时设置
        RequestConfig requestConfig = RequestConfig.custom().setConnectionRequestTimeout(500).setConnectTimeout(500)
                .setSocketTimeout(500).build();
        httpget.setConfig(requestConfig);

        File pic = null;

        // 使用 HttpClientBuilder 创建 CloseableHttpClient 对象
        // CloseableHttpClient 是一个抽象类，它是 HttpClient 的基本实现，也实现了java.io.Closeable。
        CloseableHttpClient httpclient = HttpClientBuilder.create().build();

        // 通过执行此 HttpGet 请求获取 CloseableHttpResponse 实例
        // 获取响应信息的输入流
        try (CloseableHttpResponse response = httpclient.execute(httpget);
             // InputStream 实现了 Closeable 接口
             InputStream picStream = handleResponse(response).getContent();) {

            pic = createTmpFile(picStream, "pic_", ".jpg");
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            // 释放连接
            httpget.releaseConnection();
        }

        if (pic != null && !pic.exists()) {
            throw new IllegalArgumentException("请提供正确的网络图片路径！");
        }
        logger.debug(pic.getAbsolutePath());
        return pic;
    }

    public static void main(String args[]){
        try {
            downloadPic();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
