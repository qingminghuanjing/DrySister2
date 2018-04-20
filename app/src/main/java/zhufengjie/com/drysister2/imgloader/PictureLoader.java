package zhufengjie.com.drysister2.imgloader;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by ASUS on 2018/4/20.
 */

public class PictureLoader {
    private ImageView loadImg;
    private String imgUrl;
    private byte[] picByte;

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg){
            super.handleMessage(msg);
            if (picByte!=null){
                Bitmap bitmap = BitmapFactory.decodeByteArray(picByte,0,picByte.length);
                loadImg.setImageBitmap(bitmap);
            }
        }
    };

    public void load(ImageView loadImg,String imgUrl){
        this.loadImg=loadImg;
        this.imgUrl=imgUrl;
        Drawable drawable = loadImg.getDrawable();
        if (drawable!=null && drawable instanceof BitmapDrawable){
            Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
            if (bitmap!=null && !bitmap.isRecycled()){
                bitmap.recycle();
            }
        }

        new Thread(runnable).start();
    }
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            try {
                URL url = new URL(imgUrl);
                HttpURLConnection conn =(HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setReadTimeout(10000);
                if (conn.getResponseCode()==200){
                    InputStream in = conn.getInputStream();
                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    byte[] bytes = new byte[1024];
                    int length = -1;
                    while((length = in.read(bytes))!=-1){
                        out.write(bytes,0,length);
                    }
                    picByte = out.toByteArray();
                    in.close();
                    out.close();
                    handler.sendEmptyMessage(0x123);
                }
            }catch (Exception e){
                e.printStackTrace();
            }


        }
    };

}
