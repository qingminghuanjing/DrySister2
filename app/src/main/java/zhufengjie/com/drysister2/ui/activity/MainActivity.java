package zhufengjie.com.drysister2.ui.activity;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.util.ArrayList;

import zhufengjie.com.drysister2.R;
import zhufengjie.com.drysister2.bean.entity.Sister;
import zhufengjie.com.drysister2.imgloader.PictureLoader;
import zhufengjie.com.drysister2.network.SisterApi;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private Button showBtn;//点击按钮切换下一张图片
    private ImageView showImg;//用于显示图片
    private Button refreshBtn_1;
    private Button refreshBtn_2;

    private ArrayList<Sister> data;
    private int curPos = 0;//当前显示的是哪一张
    private int page = 1;//当前页数
    private PictureLoader loader;
    private SisterApi sisterApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        loader = new PictureLoader();
        sisterApi = new SisterApi();
        initData();
        initUI();
    }
    private void initData(){
        data=new ArrayList<>();
        new SisterTask(page).execute();
    }

    private void initUI() {
        showBtn = (Button) findViewById(R.id.btn_show);
        showImg = (ImageView) findViewById(R.id.img_show);
        refreshBtn_1 = (Button) findViewById(R.id.btn_refresh_1);
        refreshBtn_2 =(Button) findViewById(R.id.btn_refresh_2);
        showBtn.setOnClickListener(this);
        refreshBtn_1.setOnClickListener(this);
        refreshBtn_2.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_show:
                curPos++;
                if (curPos>9){
                    curPos=0;
                }
                loader.load(showImg,data.get(curPos).getUrl());

                break;
            case R.id.btn_refresh_1:
                page--;
                if (page>0){
                    new SisterTask(page).execute();
                }else{
                    page=1;
                }
                break;
            case R.id.btn_refresh_2:
                page++;
                new SisterTask(page).execute();
                break;
        }

    }

    private class SisterTask extends AsyncTask<Void,Void,ArrayList<Sister>>{

        private int page;

        public SisterTask(int page){
            this.page = page;
        }

        @Override
        protected ArrayList<Sister> doInBackground(Void... params) {
            return sisterApi.fetchSister(10,page);
        }

        @Override
        protected void onPostExecute(ArrayList<Sister> sisters) {
            super.onPostExecute(sisters);
            data.clear();
            data.addAll(sisters);
            curPos=0;
            loader.load(showImg,data.get(curPos).getUrl());
        }
    }



































}