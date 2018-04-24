package zhufengjie.com.drysister2.ui.activity;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.util.ArrayList;

import zhufengjie.com.drysister2.R;
import zhufengjie.com.drysister2.bean.entity.Sister;
import zhufengjie.com.drysister2.db.SisterDBHelper;
import zhufengjie.com.drysister2.imgloader.PictureLoader;
import zhufengjie.com.drysister2.imgloader.SisterLoader;
import zhufengjie.com.drysister2.network.SisterApi;
import zhufengjie.com.drysister2.utils.NetworkUtils;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private Button showBtn_1;//点击按钮切换上一张图片
    private Button showBtn_2;//点击按钮切换下一张图片
    private ImageView showImg;//用于显示图片
    private Button refreshBtn_1;
    private Button refreshBtn_2;

    private ArrayList<Sister> data;
    private int curPos = 0;//当前显示的是哪一张
    private int page = 1;//当前页数
    private PictureLoader loader;//用了自己写的图片优化框架后就不用这个进行图片的加载了
    private SisterApi sisterApi;

    private SisterTask sisterTask;
    private SisterLoader mLoader;
    private SisterDBHelper mDbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //loader = new PictureLoader();
        sisterApi = new SisterApi();
//        loader = new PictureLoader();
        mLoader = SisterLoader.getInstance(MainActivity.this);
        mDbHelper = SisterDBHelper.getInstance(MainActivity.this);
        initData();
        initUI();
    }
    private void initData(){
        data=new ArrayList<>();
        sisterTask =new SisterTask();
        sisterTask.execute();
    }

    private void initUI() {
        showBtn_1 = (Button) findViewById(R.id.btn_show_1);
        showBtn_2 = (Button) findViewById(R.id.btn_show_2);
        showImg = (ImageView) findViewById(R.id.img_show);
        refreshBtn_1 = (Button) findViewById(R.id.btn_refresh_1);
        refreshBtn_2 =(Button) findViewById(R.id.btn_refresh_2);
        showBtn_1.setOnClickListener(this);
        showBtn_2.setOnClickListener(this);
        refreshBtn_1.setOnClickListener(this);
        refreshBtn_2.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_show_1://上一张
                curPos--;
                if (curPos<0){
                    curPos=data.size()-1;
                }
                mLoader.bindBitmap(data.get(curPos).getUrl(),showImg,400,400);
                break;
            case R.id.btn_show_2://下一张
                curPos++;
                if (curPos>data.size()-1){
                    curPos=0;
                }
                mLoader.bindBitmap(data.get(curPos).getUrl(),showImg,400,400);

                break;
            case R.id.btn_refresh_1:
                curPos=0;
                page--;
                if (page>0){
                    sisterTask=new SisterTask();
                    sisterTask.execute();
                }else{
                    page=1;
                }
                break;
            case R.id.btn_refresh_2:
                curPos=0;
                page++;
                sisterTask=new SisterTask();
                sisterTask.execute();
                break;
        }

    }

    private class SisterTask extends AsyncTask<Void,Void,ArrayList<Sister>>{

        public SisterTask(){
        }

        @Override
        protected ArrayList<Sister> doInBackground(Void... params) {
            ArrayList<Sister> result = new ArrayList<>();
            //判断是否有网络
            if (NetworkUtils.isAvailable(getApplicationContext())) {
                result = sisterApi.fetchSister(10, page);//感觉这句话应该放入下面的if语句中执行更合理
                //查询数据库里有多少个妹子，避免重复插入
                if(mDbHelper.getSistersCount() / 10 <page) {
                    mDbHelper.insertSisters(result);//，如果该页的信息从未被加载过，则将该页所有的妹子的信息都加入到数据库中
                }
            } else {
                result.clear();//如果数据库中已经包含该页的数据信息，则将请求到的result清空，并将该页的10个妹子的信息查询出来加入到result中。
                result.addAll(mDbHelper.getSistersLimit(page - 1, 10));
            }
            return result;
//            return sisterApi.fetchSister(10,page);
        }

        @Override
        protected void onPostExecute(ArrayList<Sister> sisters) {
            super.onPostExecute(sisters);
            data.clear();
            data.addAll(sisters);
            curPos=0;
            Log.e("sisters",sisters.size()+"hahahahahahahaha");
            mLoader.bindBitmap(data.get(curPos).getUrl(),showImg,400,400);
        }
        @Override
        protected void onCancelled() {
            super.onCancelled();
            sisterTask = null;
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        sisterTask.cancel(true);
    }


































}
