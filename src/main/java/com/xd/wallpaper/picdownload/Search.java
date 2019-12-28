package com.xd.wallpaper.picdownload;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
public class Search {

    /**
     * 获取页面文档字串(等待异步JS执行)
     *
     * @param url 页面URL
     * @return
     * @throws Exception
     */
    public static String getHtmlPageResponse(String url) throws Exception {
        String result = "";

        final WebClient webClient = new WebClient(BrowserVersion.CHROME);

        webClient.getOptions().setThrowExceptionOnScriptError(false);//当JS执行出错的时候是否抛出异常
        webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);//当HTTP的状态非200时是否抛出异常
        webClient.getOptions().setActiveXNative(false);
        webClient.getOptions().setCssEnabled(false);//是否启用CSS
        webClient.getOptions().setJavaScriptEnabled(true); //很重要，启用JS
        webClient.setAjaxController(new NicelyResynchronizingAjaxController());//很重要，设置支持AJAX

        webClient.getOptions().setTimeout(30000);//设置“浏览器”的请求超时时间
        webClient.setJavaScriptTimeout(30000);//设置JS执行的超时时间

        HtmlPage page;
        try {
            page = webClient.getPage(url);
        } catch (Exception e) {
            webClient.close();
            throw e;
        }
        webClient.waitForBackgroundJavaScript(30000);//该方法阻塞线程

        result = page.asXml();
        webClient.close();

        return result;
    }


    //解析xml获取ImageUrl地址
    public static List<String> getImageUrl(String html,String className){
        List<String> result = new ArrayList<>();
        Document document = Jsoup.parse(html);//获取html文档
        List<Element> infoListEle = document.getElementsByClass(className);//获取元素节点等
        infoListEle.forEach(element -> {
            result.add(element.attr("style"));
        });
        return result;
    }
    //解析xml获取ImageUrl地址
    public static List<String> getPoem(String html,String className){
        List<String> result = new ArrayList<>();
        Document document = Jsoup.parse(html);//获取html文档
        List<Element> infoListEle = document.getElementsByClass(className);//获取元素节点等
        infoListEle.forEach(element -> {
//            System.out.println(element);
            result.add(element.toString());
        });
        return result;
    }
    public static JSONObject getPicAndPoem(String url) throws Exception{
        String xml = Search.getHtmlPageResponse(url);
//        System.out.println(JSON.toJSONString(Search.getImageUrl(xml,"banner-blur")));
        String path = JSON.toJSONString(Search.getImageUrl(xml,"banner-blur"));
        System.out.println(path);
        String imgUrl = path.substring(path.indexOf("(")+1,path.indexOf(")"));
        List<String> englishList = Search.getPoem(xml,"english");
        String poemEng = englishList.get(englishList.size()-1);
        List<String> chineseList = Search.getPoem(xml,"chinese");
        String poemChe = chineseList.get(chineseList.size()-1);
//        System.out.println(imgUrl);
        JSONArray jsonArray = new JSONArray();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("img",imgUrl);
        jsonObject.put("en",poemEng.replace("<span class=\"english\">","").replace("</span>",""));
        jsonObject.put("zh",poemChe.replace("<span class=\"chinese\">","").replace("</span>",""));
        return jsonObject;
    }
    public static void main(String[] args) throws Exception {
//        String xml = Search.getHtmlPageResponse("http://news.iciba.com/");
//        System.out.println(JSON.toJSONString(Search.getPoem(xml,"english")));
//        String i ="background-image: url(https://edu-wps.ks3-cn-beijing.ksyun.com/image/40c0b1ee516103a657e80a32c926dbf7.jpg);";
//        String imgUrl = i.substring(i.indexOf("(")+1,i.indexOf(")"));
//        System.out.println(imgUrl);
//        getPicUrl("http://news.iciba.com/");
        System.out.println(Search.getPicAndPoem("http://news.iciba.com/"));
    }
}
