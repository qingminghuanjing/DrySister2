package zhufengjie.com.drysister2.task;

import android.os.AsyncTask;
import android.util.Log;

import java.util.ArrayList;

import zhufengjie.com.drysister2.Spider;

/**
 * Created by ASUS on 2018/4/26.
 */

public class OneSpiderTask extends AsyncTask<Void,Void,ArrayList> {
    private ArrayList onelist;
    private int index1;
    public OneSpiderTask(int index){
        index1 = index;
    }
    @Override
    protected ArrayList doInBackground(Void... params) {
        Spider spider = new Spider();
        onelist = spider.getImgUrl(index1);
        return onelist;
    }
    @Override
    protected void onPostExecute(ArrayList list) {

    }
}
