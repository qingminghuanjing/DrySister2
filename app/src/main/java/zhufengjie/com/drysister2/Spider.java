package zhufengjie.com.drysister2;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by ASUS on 2018/4/26.
 */

public class Spider {

    String url_str = "http://www.mmonly.cc/mmtp/";//唯一图库网网址
    String[] indexname = {"xgmn", "swmn", "hgmn", "wgmv", "bjnmn", "nymn",
            "qcmn", "ctmn", "mnmx", "jpmn",};
    String url_qc_req ="(http://www\\.mmonly\\.cc/mmtp/[a-zA-Z]+/\\d+\\.html)\"><img";;
    String url_qc ;
    String newUrl;
    String rere = "[a-z]{3}='(http://t1\\.mmonly\\.cc/uploads/.+\\.jpg)'>";;
    public Spider(){

    }

    //爬取页面第一页第index个美女的所有图片url，index指明是爬取的第几个美女
    public ArrayList getImgUrl(int index){
        //清纯网页第一页的所有美女所在网址爬取
        String htmlContent = fetchHtml(url_str,6);
        ArrayList urlList= fetchContentByReq(htmlContent, url_qc_req);//对网页内容进行爬取，得到含图片的网址的集合
        ArrayList allImgList=null;
        if(index<urlList.size()){
            String url = (String)urlList.get(index);
            //爬取第一个网址的图片
            allImgList = new ArrayList();
            String urlcreate = url.substring(0,url.lastIndexOf("."));
            for(int i=1;i<100;i++){
                if (i==1){
                    newUrl=url;
                }else if (i>1){
                    newUrl = urlcreate+"_"+i+".html";
                }else {

                }
                String htmlContentOfImgURL = fetchHtml(newUrl,-1);
                if(htmlContentOfImgURL!=null){
                    ArrayList imgList = fetchContentByReq(htmlContentOfImgURL,rere);
                    allImgList.add(imgList.get(0));
                }else{
                    break;
                }
            }
        }
        return allImgList;
    }
    //根据字符串的内容，按照正则表达式爬取内容
    public static ArrayList fetchContentByReq(String str, String req){
        Pattern pattern = Pattern.compile(req);
        Matcher matcher=pattern.matcher(str);
        ArrayList list = new ArrayList();
        while (matcher.find()){
            list.add(matcher.group(1));
        }
        return list;
    }
    //爬取html的内容
    public String fetchHtml(String stationUrl,int index){
        String fetchUrl = null;
        if(index==-1){
            fetchUrl = stationUrl;
        }else{
            fetchUrl=stationUrl+indexname[index];
        }
        ArrayList sisters = new ArrayList<>();
        String result=null;
        try {
            URL url = new URL(fetchUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setReadTimeout(5000);
            int code = conn.getResponseCode();
            if (code==200){
                InputStream in = conn.getInputStream();
                byte[] data = readFromStream(in);
                result = new String(data,"UTF-8");

            }else {

            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return result;
    }

    public byte[] readFromStream(InputStream in){
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] bytes = new byte[1024];
        int length = -1;
        try {
            while ((length=in.read(bytes))!=-1){
                out.write(bytes,0,length);
            }
            in.close();
        }catch (Exception e){
            e.printStackTrace();
        }finally {
        }
        return out.toByteArray();
    }

}
