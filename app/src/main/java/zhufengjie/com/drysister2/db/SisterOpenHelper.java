package zhufengjie.com.drysister2.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * 描述：数据库创建类
 *
 * @author coder-pig： 2016/08/18 20:09
 */
public class SisterOpenHelper extends SQLiteOpenHelper{

    private static final String DB_NAME = "sister.db3";  //数据库名
    private static final int DB_VERSION = 1;    //数据库版本号

    public SisterOpenHelper(Context context) {

        super(context, DB_NAME, null, DB_VERSION);

        Log.e("数据库创建成功：","eeeeeeeeeeeee");

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.e("数据库","==================");
        String createTableSql = "CREATE TABLE IF NOT EXISTS "
                + TableDefine.TABLE_FULI + " ("+ TableDefine.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + TableDefine.COLUMN_FULI_ID + " TEXT, "
                + TableDefine.COLUMN_FULI_CREATEAT + " TEXT, "
                + TableDefine.COLUMN_FULI_DESC + " TEXT, "
                + TableDefine.COLUMN_FULI_PUBLISHEDAT + " TEXT, "
                + TableDefine.COLUMN_FULI_SOURCE + " TEXT, "
                + TableDefine.COLUMN_FULI_TYPE + " TEXT, "
                + TableDefine.COLUMN_FULI_URL + " TEXT, "
                + TableDefine.COLUMN_FULI_USED + " INT, "
                + TableDefine.COLUMN_FULI_WHO + " TEXT"
                + ")";

        db.execSQL(createTableSql);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) { }
}
