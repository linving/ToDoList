package sasure.myapplication.todolist;


import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import sasure.myapplication.mysql.mDataBaseHelper;


public class EditActivity extends Activity implements View.OnClickListener
{
    public static String BUNDLE_CONTENT = "bundle_content";
   // public static final int GETRESULT =0x01;
    public static final String GETRESULT = "gerResult";
    private final static int FINISH = 0X00;

    private SQLiteDatabase db;

    private EditText editTitle;
    private EditText editDetails;
    private Button comfirmButton;
    private Spinner mSpinner;

    private boolean isEdit;

    private int _id;
    private String title = new String("");
    private String detail = new String("");
    private String type = new String("");

//    private final Handler handler = new Handler()
//    {
//        @Override
//        public void handleMessage(Message msg)
//        {
//            switch (msg.what)
//            {
//                case FINISH:
//
//                    break;
//            }
//            super.handleMessage(msg);
//        }
//    };

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        initActionbar();
        initField();
    }

    private void initField()
    {
        setContentView(R.layout.activity_edit);

        editTitle = (EditText) findViewById(R.id.edit_title);
        editDetails = (EditText) findViewById(R.id.edit_details);
        comfirmButton = (Button) findViewById(R.id.comfiem_button);
        db = mDataBaseHelper.getInstance().getWritableDatabase();

        comfirmButton.setOnClickListener(this);

        getExtras();
    }

    private void getExtras()
    {
        Intent intent = getIntent();
        Bundle bundle = intent.getBundleExtra(BUNDLE_CONTENT);

        if(bundle == null)
        {
            isEdit = false;
            setTitle(getResources().getString(R.string.create));
        }
        else
        {
            isEdit = true;
            setTitle(getResources().getString(R.string.edit));

            title = bundle.getString(mDataBaseHelper.TITLE);
            _id = bundle.getInt(mDataBaseHelper.TITLE_ID);
            type = bundle.getString(mDataBaseHelper.TYPE);
            getDetil();

            initContent();
        }
    }

    private void getDetil()
    {
        final String statement = "select " + mDataBaseHelper.DETAIL + " from " + mDataBaseHelper.DETAIL_TABLE +
                " where " + mDataBaseHelper.TITLE_ID + " = ?";

        Cursor cursor = db.rawQuery(statement,new String[]{_id+""});

        if(cursor != null)
            if(cursor.moveToFirst())
                detail = cursor.getString(cursor.getColumnIndex(mDataBaseHelper.DETAIL));
    }

    private void initActionbar()
    {
        ActionBar actionBar = getActionBar();

        ArrayAdapter<CharSequence> list = ArrayAdapter.createFromResource(this, R.array.classify, R.layout.spinner_item);
        list.setDropDownViewResource(R.layout.spinner_dropdown_item);

        View myView = LayoutInflater.from(this).inflate(R.layout.myspinner, null);
        mSpinner = (Spinner) myView.findViewById(R.id.myspinner);
        mSpinner.setAdapter(list);

        ActionBar.LayoutParams layoutParams = new ActionBar.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
        layoutParams.gravity = Gravity.RIGHT; // set your layout's gravity to 'right'
        actionBar.setCustomView(myView, layoutParams);
    }

    private void initContent()
    {
        if(isEdit == true)
        {
            if(title != null)
                editTitle.setText(title);

            if(editDetails != null)
                editDetails.setText(detail);

            mSpinner.setSelection(typeToPosition(type));
        }
    }

    private int typeToPosition(String type)
    {
//        final String life = getResources().getString(R.string.life);
//        final String study = getResources().getString(R.string.study);
//        final String work = getResources().getString(R.string.work);
        int position = 0;

        switch (type)
        {
            case mDataBaseHelper.LIFE:
                position = 0;
                break;

            case mDataBaseHelper.STUDY:
                position = 1;
                break;

            case mDataBaseHelper.WORK:
                position = 2;
                break;

            default:
                break;
        }

        return position;
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu)
//    {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        return true;
//    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home)
        {
            finishAndExit();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * 菜单、返回键响应
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if(keyCode == KeyEvent.KEYCODE_BACK)
        {
            finishAndExit();
            return true;
        }
        return false;
    }

    private void finishAndExit()
    {
        if(canFinish() == true)
            finish();
    }

    private boolean canFinish()
    {
        String newTitle = editTitle.getText().toString();
        String newDetail = editDetails.getText().toString();


        if(isEdit == true)
        {
            String newType = positionToType(mSpinner.getSelectedItemPosition());

            if(!title.equals(newTitle) || !detail.equals(newDetail) || !type.equals(newType))
            {
                //显示对话框
                Toast.makeText(this,"不同",Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        else
        {
            if(!newTitle.isEmpty() || !newDetail.isEmpty())
            {
                //显示对话框
                Toast.makeText(this,"确定放弃？",Toast.LENGTH_SHORT).show();
                return false;
            }
        }

        return true;
    }

    @Override
    public void onClick(View v)
    {
        String newTitle = editTitle.getText().toString();
        String newDetail = editDetails.getText().toString();
        String newType = positionToType(mSpinner.getSelectedItemPosition());

        if(titleIsEmpty(newTitle))
            return;
        else if(isEdit == true)
            edit(newTitle,newDetail,newType);
        else
            create(newTitle,newDetail,newType);
    }

//    private void finishAndPush()
//    {
//        Intent intent = getIntent();
////        Bundle bundle = new Bundle();
//
////        bundle.putString(mDataBaseHelper.TITLE,title);
////        bundle.putString(mDataBaseHelper.TYPE,type);
////        bundle.putInt(mDataBaseHelper._ID,_id);
//
// //       intent.putExtra(GETRESULT,bundle);
//        Log.i("tttt","finishAndPush");
//        setResult(0,intent);
//        finish();
//    }

    private void edit(final String newTitle, final String newDetail, final String newType)
    {
        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                if(!title.equals(newTitle) || !type.equals(newType))
                {
                    final String update_title_table = "update " + mDataBaseHelper.TITLE_TABLE + " set " + mDataBaseHelper.TITLE + " = '" + newTitle +
                            "'," + mDataBaseHelper.TYPE + " = '" + newType + "'," + mDataBaseHelper.LOGTIME +" = " + mDataBaseHelper.CURRENTTIME + " where " +
                            mDataBaseHelper._ID + " = ?" ;

                    db.execSQL(update_title_table,new Integer[]{_id});
                    Log.i("test",update_title_table);
                }

                if(!detail.equals(newDetail))
                {
                    final String update_detail_table = "update " + mDataBaseHelper.DETAIL_TABLE + " set " + mDataBaseHelper.DETAIL +" = '" + newDetail +
                            "' where " + mDataBaseHelper.TITLE_ID + " = ?";

                    db.execSQL(update_detail_table,new Integer[]{_id});
                    Log.i("test",update_detail_table);
                }

                EditActivity.this.finish();
            }
        }).start();
    }

    private void create(String newTitle,String newDetail,String newType)
    {
        title = newTitle;
        detail = newDetail;
        type = newType;

        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                final String create_title_table = "insert into " + mDataBaseHelper.TITLE_TABLE + "(" + mDataBaseHelper.TITLE + "," +
                        mDataBaseHelper.TYPE +") values(?,?)";

                db.execSQL(create_title_table,new String[]{title,type});

                final String selectStatement = "select distinct last_insert_rowid() from " + mDataBaseHelper.TITLE_TABLE;

                Cursor cursor = db.rawQuery(selectStatement,null);

                if(cursor != null)
                    if(cursor.moveToFirst())
                        _id = cursor.getInt(0);

                final String create_detail_table = "insert into " + mDataBaseHelper.DETAIL_TABLE + "(" + mDataBaseHelper.DETAIL +","+mDataBaseHelper.TITLE_ID+
                        ") values(?,?)";

                db.execSQL(create_detail_table,new Object[]{new String(detail),new Integer(_id)});

          //      db.insert(mDataBaseHelper.DETAIL_TABLE,)
                EditActivity.this.finish();
            }
        }).start();
    }
//    ") values('" + detail + "'," + _id + ")";
    private boolean titleIsEmpty(String title)
    {
        if(title.isEmpty())
        {
            Toast.makeText(this,"主题不能为空！！",Toast.LENGTH_SHORT).show();
            return true;
        }
        return false;
    }

    private String positionToType(int position)
    {
        switch (position)
        {
            case 0:
                return mDataBaseHelper.LIFE;

            case 1:
                return  mDataBaseHelper.STUDY;

            case 2:
                return mDataBaseHelper.WORK;

            default:
                return  mDataBaseHelper.LIFE;
        }
    }
}
