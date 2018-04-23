package zhufengjie.com.drysister2.network;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

import zhufengjie.com.drysister2.bean.entity.Sister;

/**
 * Created by ASUS on 2018/4/20.
 */

public class SisterApi {
    private static final String TAG = "Network";
//    private static final String fuli = toChineseHex("福利");

//    private static final String BASE_URL = "http://gank.io/api/data/"+fuli+"/";
//    private static final String BASE_URL = "http://gank.io/api/data/福利/";
//private static final String BASE_URL = "http://gank.io/api/data/%E7%A6%8F%E5%88%A9/";
    private static final String fuli = chineseToutf_8("福利");
    private static final String BASE_URL = "http://gank.io/api/data/"+fuli+"/";


    public static String chineseToutf_8(String s){
        String fuli = null;
        try {
            fuli = URLEncoder.encode(s, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return fuli;
    }
    /**
     * 查询图片信息
     */



    public ArrayList<Sister> fetchSister(int count, int page){
        String fetchUrl = BASE_URL+count+"/"+page;
        ArrayList<Sister> sisters = new ArrayList<>();
        try {
            URL url = new URL(fetchUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setReadTimeout(5000);
            int code = conn.getResponseCode();
            Log.v(TAG,"Server responseCode:"+code);
            if (code==200){
                InputStream in = conn.getInputStream();
                Log.e("sisters","111111");
                byte[] data = readFromStream(in);
                Log.e("sisters",data.length+"=======222222222222=============");
                String result = new String(data,"UTF-8");
                Log.e("sisters",result);
                sisters = parseSister(result);
            }else {
                Log.e(TAG,"请求失败："+code);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        Log.e("sisters",sisters.size()+"====================");
     return sisters;
    }

    private ArrayList<Sister> parseSister(String result) {
        ArrayList<Sister> sisters = new ArrayList<>();
        try {
            JSONObject object = new JSONObject(result);
            JSONArray array = object.getJSONArray("results");
            for (int i = 0; i<array.length();i++){
                JSONObject results = (JSONObject)array.get(i);
                Sister sister = new Sister();
                sister.set_id(results.getString("_id"));
                sister.setCreateAt(results.getString("createdAt"));
                sister.setDesc(results.getString("desc"));
                sister.setPublishedAt(results.getString("publishedAt"));
                sister.setSource(results.getString("source"));
                sister.setType(results.getString("type"));
                sister.setUrl(results.getString("url"));
                sister.setUsed(results.getBoolean("used"));
                sister.setWho(results.getString("who"));
                sisters.add(sister);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return sisters;
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
