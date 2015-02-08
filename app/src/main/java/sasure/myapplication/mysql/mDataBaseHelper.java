package sasure.myapplication.mysql;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import sasure.myapplication.todolist.MainActivity;

/**
 * Created by ZHOU on 2015-2-5.
 */
public class mDataBaseHelper extends SQLiteOpenHelper
{
    public static final int VERSION = 1;
    public static final String DATABASE = "database.db3";
    public static final String ALL = "all";
    public static final String LIFE = "life";
    public static final String STUDY = "study";
    public static final String WORK = "work";
    public static  final String TRUE = "true";
    public static final String FALSE = "false";

    public static final String TITLE_TABLE = "title_table";
    public static final String _ID = "_id";
    public static final String TITLE = "title";
    public static final String TYPE = "type";
    public static final String ISDONE = "isdone";
    public static final String LOGTIME = "logtime";
    public static final String CURRENTTIME = " (datetime('now','localtime')) ";
    public static final String CREATE_TITLE_TABLE = "create table " + TITLE_TABLE + "(" +
            _ID + " integer primary key autoincrement," +
            TITLE + " text not null," +
            TYPE + " text not null," +
            ISDONE + " text  default "+ FALSE + "," +
            LOGTIME + " timestamp default " + CURRENTTIME + "" +
            ")";

    public static final String DETAIL_TABLE = "detail_table";
    public static final String DETAIL = "detail";
    public static final  String TITLE_ID = "title_id";
    public static final String CREATE_DETAIL_TABLE = "create table " + DETAIL_TABLE + "(" +
            _ID + " integer primary key autoincrement," +
            DETAIL + " text null," +
            TITLE_ID + " integer references "+ TITLE_TABLE + "("+ _ID +") on delete cascade" +
            ")";
    private static mDataBaseHelper helper;

    /**
     * 单例类
     * @return 数据库对象
     */
    public  static mDataBaseHelper getInstance()
    {
        if(helper == null)
            helper = new mDataBaseHelper(MainActivity.mContext,DATABASE,VERSION);

        return helper;
    }


    private mDataBaseHelper(Context context, String name, int version)
    {
        super(context, name, null, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        db.beginTransaction();

        try
        {
            db.execSQL(CREATE_TITLE_TABLE);
            db.execSQL(CREATE_DETAIL_TABLE);

            db.setTransactionSuccessful();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
            Log.e("error","fail to create table !!!");
        }
        finally
        {
            db.endTransaction();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {}
}
