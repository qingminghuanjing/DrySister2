package zhufengjie.com.drysister2.ui.activity;

import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

import zhufengjie.com.drysister2.R;
import zhufengjie.com.drysister2.Spider;
import zhufengjie.com.drysister2.bean.entity.Sister;
import zhufengjie.com.drysister2.db.SisterDBHelper;
import zhufengjie.com.drysister2.imgloader.PictureLoader;
import zhufengjie.com.drysister2.imgloader.SisterLoader;
import zhufengjie.com.drysister2.imgloader.helper.DiskCacheHelper;
import zhufengjie.com.drysister2.network.SisterApi;
import zhufengjie.com.drysister2.task.OneSpiderTask;
import zhufengjie.com.drysister2.utils.NetworkUtils;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private Button showBtn_1;//点击按钮切换上一张图片
    private Button showBtn_2;//点击按钮切换下一张图片
    private ImageView showImg;//用于显示图片
    private Button refreshBtn_1;//切换上一批图片
    private Button refreshBtn_2;//切换下一批图片

    private Toolbar toolbar;
    private ArrayList data;
    private int curPos = 0;//当前显示的是哪一张
    private int page = 1;//当前页数
    private PictureLoader loader;//用了自己写的图片优化框架后就不用这个进行图片的加载了
    private SisterApi sisterApi;

    private SisterTask sisterTask;
    private SisterLoader mLoader;
    private SisterDBHelper mDbHelper;
    private TextView textView;//用于显示这张图片是第几页的第几张图片
    private DrawerLayout drawerLayout;
    private  NavigationView navigationView;
    private AlertDialog dialog = null;
    private ArrayList onelist;//唯一图网返回的图片list，里面存储的是图片的URL
    private int flag=1;//1表示干货集中营，2表示唯一图库
    private int pageone=0;//0表示第一个

    // 定义一个变量，来标识是否退出
    private static boolean isExit = false;


    private static final String fuli = chineseToutf_8("福利");
    private String stationUrl="http://gank.io/api/data/"+fuli+"/";//干货集中营的网址

    public static String chineseToutf_8(String s){
        String fuli = null;
        try {
            fuli = URLEncoder.encode(s, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return fuli;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initUI();
        initData();
        mylisenter();
    }
    private void initUI() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawerLayout= (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setCheckedItem(R.id.nav_gan);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.mipmap.ic_menu);
        }
        showBtn_1 = (Button) findViewById(R.id.btn_show_1);
        showBtn_2 = (Button) findViewById(R.id.btn_show_2);
        showImg = (ImageView) findViewById(R.id.img_show);
        refreshBtn_1 = (Button) findViewById(R.id.btn_refresh_1);
        refreshBtn_2 =(Button) findViewById(R.id.btn_refresh_2);
        textView = (TextView) findViewById(R.id.tv);
    }
    private void initData(){
        //loader = new PictureLoader();
        sisterApi = new SisterApi();
        mLoader = SisterLoader.getInstance(MainActivity.this);
        mDbHelper = SisterDBHelper.getInstance(MainActivity.this);
        data=new ArrayList<>();
        sisterTask =new SisterTask();
        sisterTask.execute();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar,menu);
        return true;
    }
    public void mylisenter(){
        showBtn_1.setOnClickListener(this);
        showBtn_2.setOnClickListener(this);
        refreshBtn_1.setOnClickListener(this);
        refreshBtn_2.setOnClickListener(this);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.nav_gan:
                        flag=1;
                        Toast.makeText(MainActivity.this,"您请求干货集中营的图片",Toast.LENGTH_SHORT).show();
                        stationUrl="http://gank.io/api/data/"+fuli+"/";//干货集中营的网址
                        sisterTask=new SisterTask();
                        sisterTask.execute();
                        break;
                    case R.id.nav_one:
                        flag=2;
                        Toast.makeText(MainActivity.this,"您请求唯一图网图片",Toast.LENGTH_SHORT).show();

                        OneSpiderTask oneSpiderTask =new OneSpiderTask(pageone){
                            @Override
                            protected void onPostExecute(ArrayList list) {
                                onelist=list;
                                data.clear();
                                data.addAll(onelist);
                                Log.e("唯一图网",onelist.toString());
                                onelist.clear();
                                textView.setText("第"+(pageone+1)+"个妹子"+"第"+(curPos+1)+"图");
                                mLoader.bindBitmap(((String)data.get(curPos)),showImg,400,400);
                            }
                        };
                        oneSpiderTask.execute();
                        break;
                    case R.id.nav_mei:
                        Toast.makeText(MainActivity.this,"您请求美女网的图片",Toast.LENGTH_SHORT).show();
                        break;
                }
                return true;
            }
        });
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.backup:
                Toast.makeText(this,"点击了回退键",Toast.LENGTH_SHORT).show();
                break;
            case R.id.delete:
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                dialog=builder.setTitle("系统提示：").setMessage("是否删除缓存中所有的图片").setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        DiskCacheHelper diskCacheHelper = new DiskCacheHelper(MainActivity.this);
                        diskCacheHelper.delete();//将删除磁盘缓存区中的所有内容
                        Toast.makeText(MainActivity.this,"恭喜您删除了磁盘中的全部缓存内容",Toast.LENGTH_SHORT).show();
                    }
                }).create();
                dialog.show();
                break;
            case R.id.settings:
                Toast.makeText(this,"点击了设置键",Toast.LENGTH_SHORT).show();
                break;
            case android.R.id.home:
                drawerLayout.openDrawer(Gravity.START);
                break;
        }
        return true;
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_show_1://上一张
                if (flag==1){
                    curPos--;
                    if (curPos<0){
                        curPos=data.size()-1;
                    }
                    //textView.setText(curPos+"");
                    textView.setText("第"+page+"页"+"第"+(curPos+1)+"图");
                    mLoader.bindBitmap(((Sister)data.get(curPos)).getUrl(),showImg,400,400);
                }else if (flag==2){
                    curPos--;
                    if (curPos<0){
                        curPos=data.size()-1;
                    }
                    //textView.setText(curPos+"");
                    textView.setText("第"+(pageone+1)+"个妹子"+"第"+(curPos+1)+"图");
                    mLoader.bindBitmap(((String)data.get(curPos)),showImg,400,400);
                }
                //mLoader.bindBitmap(data.get(curPos).getUrl(),showImg,400,400);
                break;
            case R.id.btn_show_2://下一张
                if (flag==1){
                    curPos++;
                    if (curPos>data.size()-1){
                        curPos=0;
                    }
                    textView.setText("第"+page+"页"+"第"+(curPos+1)+"图");
                    mLoader.bindBitmap(((Sister)data.get(curPos)).getUrl(),showImg,400,400);
                    //mLoader.bindBitmap(data.get(curPos).getUrl(),showImg,400,400);
                }else if (flag==2){
                    curPos++;
                    if (curPos>data.size()-1){
                        curPos=0;
                    }
                    textView.setText("第"+(pageone+1)+"个妹子"+"第"+(curPos+1)+"图");
                    mLoader.bindBitmap(((String)data.get(curPos)),showImg,400,400);
                }
                break;
            case R.id.btn_refresh_1:
                if (flag==1){
                    curPos=0;
                    page--;
                    if (page>0){
                        sisterTask=new SisterTask();
                        sisterTask.execute();
                    }else{
                        page=1;
                    }
                }else if (flag==2){
                    curPos=0;
                    pageone--;
                    if (pageone>=0){
                        OneSpiderTask oneSpiderTask =new OneSpiderTask(pageone){
                            @Override
                            protected void onPostExecute(ArrayList list) {
                                onelist=list;
                                data.clear();
                                data.addAll(onelist);
                                Log.e("唯一图网",onelist.toString());
                                onelist.clear();
                                textView.setText("第"+(pageone+1)+"个妹子"+"第"+(curPos+1)+"图");
                                mLoader.bindBitmap(((String)data.get(curPos)),showImg,400,400);
                            }
                        };
                        oneSpiderTask.execute();
                    }else{
                        pageone=0;
                    }
                }

                break;
            case R.id.btn_refresh_2:
                if (flag==1){
                    curPos=0;
                    page++;
                    sisterTask=new SisterTask();
                    sisterTask.execute();
                }else if (flag==2){
                    curPos=0;
                    pageone++;
                    OneSpiderTask oneSpiderTask =new OneSpiderTask(pageone){
                        @Override
                        protected void onPostExecute(ArrayList list) {
                            onelist=list;
                            data.clear();
                            data.addAll(onelist);
                            Log.e("唯一图网",onelist.toString());
                            onelist.clear();
                            textView.setText("第"+(pageone+1)+"个妹子"+"第"+(curPos+1)+"图");
                            mLoader.bindBitmap(((String)data.get(curPos)),showImg,400,400);
                        }
                    };
                    oneSpiderTask.execute();
                    }
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
                result = sisterApi.fetchSister(stationUrl,10, page);//感觉这句话应该放入下面的if语句中执行更合理
                //查询数据库里有多少个妹子，避免重复插入
                if(mDbHelper.getSistersCount() / 10 <page) {
                    mDbHelper.insertSisters(result);//，如果该页的信息从未被加载过，则将该页所有的妹子的信息都加入到数据库中//感觉十个十个的加入并不合理，应该改为一个一个的加入
                }
            } else {
                //result.clear();//如果数据库中已经包含该页的数据信息，则将请求到的result清空，并将该页的10个妹子的信息查询出来加入到result中。
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
            //textView.setText(curPos+"");
            textView.setText("第"+page+"页"+"第"+(curPos+1)+"图");
            mLoader.bindBitmap(((Sister)data.get(curPos)).getUrl(),showImg,400,400);
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
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            exit();
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }
    //点击两次退出应用
    private void exit() {
        if (!isExit) {
            isExit = true;
            Toast.makeText(getApplicationContext(), "再按一次退出程序",
                    Toast.LENGTH_SHORT).show();
            // 利用handler延迟发送更改状态信息
            mHandler.sendEmptyMessageDelayed(0, 2000);
        } else {
            finish();
            System.exit(0);
        }
    }
    Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 0:
                    isExit = false;
            }
        }
    };































}
