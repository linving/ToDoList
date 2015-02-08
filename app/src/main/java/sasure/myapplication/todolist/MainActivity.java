package sasure.myapplication.todolist;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.internal.nineoldandroids.animation.ObjectAnimator;
import com.actionbarsherlock.view.MenuItem;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingListActivity;

import java.util.ArrayList;

import sasure.myapplication.listview.ContentItem;
import sasure.myapplication.listview.LabelItem;
import sasure.myapplication.listview.ListItem;
import sasure.myapplication.listview.PartAdapter;
import sasure.myapplication.listview.slidecutListView;
import sasure.myapplication.mysql.mDataBaseHelper;

public class MainActivity extends SlidingListActivity implements slidecutListView.RemoveListener ,
        ListView.OnItemClickListener,ListView.OnScrollListener
{
    private final float upAlpha = 1f;
    private final float downAlpha = 0.328f;
    private final long alphaDuration = 618;

    private final static int FLASH = 0x00;
//    private boolean hasFinished;
//    private boolean hasUnFinished;
//    private int unFinishedPosition;

    private int unFinishCount = 0;
    private int FinishCount = 0;
    private LabelItem unFinishLable;
    private LabelItem FinishLable;

    private SlidingMenu slidingMenu;
    private Spinner mSpinner;
    public static Context mContext;
    private LayoutInflater mInflater;
    private ArrayList<ListItem> mListItems;
  //  private PartAdapter listAdapter;
    private slidecutListView mListView;
    public static int screenWidth;
    private ImageButton buttonAdd;
    private SQLiteDatabase db;
    /**
     * 当前屏幕可见的第一个item在整个listview中的下标
     */
    private int firstIndex;

    private final Handler handle = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            switch (msg.what)
            {
                case FLASH:
                    if(mListView != null)
                        mListView.invalidateViews();
                    break;

                default:
                     super.handleMessage(msg);
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        initField();
        initActionbar();
        initSlidingMenu();
        initListView();
        initaddButton();
    }

    @Override
    public void onResume()
    {
        super.onResume();
      //  Log.i("tttt","onActivityResult");
        initListItems();

    }

//    @Override
//    public void onActivityResult(int requestCode,int resultCode,Intent intent)
//    {
//
//        if(requestCode == 0 && resultCode == 0)
//        {
//            Log.i("tttt","onActivityResult");
//            initListItems();
////            Bundle bundle = intent.getBundleExtra(EditActivity.GETRESULT);
////
////            String newType = bundle.getString(mDataBaseHelper.TYPE);
//////            String newTitle = bundle.getString(mDataBaseHelper.TITLE);
//////
////            if(shouldReflash(newType) == true)
////            {
////                initListItems();
////            }
//        }
//    }

//    private boolean shouldReflash(String newType)
//    {
//        String currentType = positionToType(mSpinner.getSelectedItemPosition());
//
//        if(currentType.equals(newType) || currentType.equals(mDataBaseHelper.ALL))
//            return true;
//
//        return false;
//    }

//    private String positionToType(int position)
//    {
//        switch (position)
//        {
//            case 0:
//                return  mDataBaseHelper.ALL;
//
//            case 1:
//                return mDataBaseHelper.LIFE;
//
//            case 2:
//                return  mDataBaseHelper.STUDY;
//
//            case 3:
//                return mDataBaseHelper.WORK;
//
//            default:
//                return  mDataBaseHelper.ALL;
//        }
//    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();

        if(mDataBaseHelper.getInstance() != null)
            mDataBaseHelper.getInstance().close();
    }




    private void initField()
    {
        mContext = this;
        mInflater = LayoutInflater.from(mContext);
        mListItems = new ArrayList<>();
        screenWidth = ((WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getWidth();//一定要在ListView之前调用！！
        db = mDataBaseHelper.getInstance().getReadableDatabase();
        unFinishLable = new LabelItem(getResources().getString(R.string.unfinished));
        FinishLable = new LabelItem(getResources().getString(R.string.finished));

        db.execSQL("PRAGMA foreign_keys=ON");
  //      dd();
    }

    private void dd()
    {
    //    SQLiteDatabase db = databaseHelper.getReadableDatabase();

        db.execSQL("insert into title_table(title,type) values('1','work')");
        db.execSQL("insert into title_table(title,type) values('2','study')");
        db.execSQL("insert into title_table(title,type) values('3','work')");
        db.execSQL("insert into title_table(title,type) values('4','life')");
        db.execSQL("insert into title_table(title,type) values('5','work')");
    }

    /**
     * 初始化ActionBar
     */
    private void initActionbar()
    {
        ActionBar actionBar = getSupportActionBar();

        ArrayAdapter<CharSequence> list =  ArrayAdapter.createFromResource(this, R.array.type, R.layout.spinner_item);
        list.setDropDownViewResource(R.layout.spinner_dropdown_item);

        View myView = LayoutInflater.from(this).inflate(R.layout.myspinner, null);
        mSpinner = (Spinner) myView.findViewById(R.id.myspinner);
        mSpinner.setAdapter(list);

        ActionBar.LayoutParams layoutParams = new ActionBar.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
        layoutParams.gravity = Gravity.RIGHT; // set your layout's gravity to 'right'
        actionBar.setCustomView(myView, layoutParams);

        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                mSpinner.setSelection(position);
                initListItems();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {
            }
        });
    }

    private void initListItems()
    {
 //       ArrayList<ContentItem> unFinished = new ArrayList<>();
  //      ArrayList<ContentItem> Finished = new ArrayList<>();
//        hasFinished = false;
//        hasUnFinished = false;

//        setSupportProgress(Window.PROGRESS_START);
//        setSupportProgressBarVisibility(true);

        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                unFinishCount = 0;
                FinishCount = 0;

                Cursor cursor = db.rawQuery(getQueryStatement(),null);

                //  cursor.moveToLast();

                mListItems.clear();

                if(cursor != null)
                    while (cursor.moveToNext())
                    {
                        int _id = cursor.getInt(cursor.getColumnIndex(mDataBaseHelper._ID));
                        String title = cursor.getString(cursor.getColumnIndex(mDataBaseHelper.TITLE));
                        String type = cursor.getString(cursor.getColumnIndex(mDataBaseHelper.TYPE));
                        String isDone = cursor.getString(cursor.getColumnIndex(mDataBaseHelper.ISDONE));

                        ContentItem tp = new ContentItem(_id,title,type,isDone);

                        switch (isDone)
                        {
                            case mDataBaseHelper.FALSE:
                                //   unFinished.add(tp);
                                if(unFinishCount == 0)
                                    mListItems.add(0,unFinishLable);

                                ++unFinishCount;

                                mListItems.add(unFinishCount,tp);

                                break;

                            case mDataBaseHelper.TRUE:
                                //Finished.add(tp);
                                int f = unFinishCount == 0 ? 0 : unFinishCount + 1;

                                if(FinishCount == 0)
                                    mListItems.add(f,FinishLable);

                                ++FinishCount;

                                mListItems.add(f + FinishCount,tp);
                                break;

                            default:
                                Log.i("warn","isDone just can true or false");
                                break;
                        }
                    }

                handle.sendEmptyMessage(FLASH);
            }
        }).start();
    }

    private String getQueryStatement()
    {
        StringBuffer statement = new StringBuffer();

        statement.append("select * from " + mDataBaseHelper.TITLE_TABLE);

        switch (mSpinner.getSelectedItemPosition())
        {
            case 0:
                break;

            case 1:
                statement.append(" where " + mDataBaseHelper.TYPE + " = '" + mDataBaseHelper.LIFE + "'");
                break;

            case 2:
                statement.append(" where " + mDataBaseHelper.TYPE + " = '" + mDataBaseHelper.STUDY + "'");
                break;

            case 3:
                statement.append(" where " + mDataBaseHelper.TYPE + " = '" + mDataBaseHelper.WORK + "'");
                break;

            default:
                break;
        }

        statement.append(" order by " + mDataBaseHelper.LOGTIME + " desc");

        Log.i("test",statement.toString());

        return statement.toString();
    }

    /**
     * 初始化SlidingMenu
     */
    private void initSlidingMenu()
    {
        setContentView(R.layout.activity_main);
        setBehindContentView(R.layout.hidingview);

        slidingMenu = getSlidingMenu();
        slidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
        slidingMenu.setFadeEnabled(true);
        slidingMenu.setFadeDegree(0.5f);
        slidingMenu.setShadowDrawable(R.drawable.shadow);
        slidingMenu.setShadowWidthRes(R.dimen.slide_shadow_width);
        slidingMenu.setBehindWidthRes(R.dimen.behind_width);
    }

    /**
     * 初始化ListView
     */
    public void initListView()
    {
        mListView = (slidecutListView) getListView();

//        String[] Countries = getResources().getStringArray(R.array.countries);
//        LabelItem label = new LabelItem("近期");
//        mListItems.add(label);

//        for (int i = 0; i < 30; i++)
//        {
//                String item = new String("" + Countries[i]);
//                ContentItem content = new ContentItem(item);
//                mListItems.add(content);
//        }

   //     initListItems();

     //   listAdapter = new PartAdapter(mListItems, mContext);

        mListView.setLayoutAnimation(getListAnim());
        mListView.setAdapter(new PartAdapter(mListItems, mContext));
        mListView.setOnItemClickListener(this);
        mListView.setOnScrollListener(this);
        mListView.setRemoveListener(this);
    }

    public void initaddButton()
    {
        buttonAdd = (ImageButton) findViewById(R.id.button_add);

        buttonAdd.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                buttonAdd.setAlpha(1f);
                Intent tp = new Intent(MainActivity.this,EditActivity.class);

                startActivity(tp);
            }
        });

        buttonAdd.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                dd();
                initListItems();
                return true;
            }
        });
    }

    /**
     *
     * @return 飞入效果
     */
    private LayoutAnimationController getListAnim()
    {
        Animation layoutAnim = AnimationUtils.loadAnimation(this, R.anim.layout_anim);
  //      layoutAnim.setDuration(550);

        LayoutAnimationController controller = new LayoutAnimationController(layoutAnim);
        return controller;
    }

    @Override
    public void onBackPressed()
    {
        if (slidingMenu.isMenuShowing())
            slidingMenu.showContent();
        else
            super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home:
                toggle();
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
        if(keyCode == KeyEvent.KEYCODE_MENU)
        {
            toggle();
            return true;
        }
        return false;
    }


    @Override
    public void removeItem(slidecutListView.RemoveDirection direction, int position)
    {


   //     listAdapter.remove(position);

//        if (listAdapter.getCount() != position)
//        {
//            if (listAdapter.getItem(position).getClass() == LabelItem.class && listAdapter.getItem(position - 1).getClass() == LabelItem.class) {
//                mListItems.remove(position - 1);
//            }
//        }
//        else
//        {
//            if (listAdapter.getItem(position - 1).getClass() == LabelItem.class)
//                mListItems.remove(position - 1);
//        }
        switch (direction)
        {
            case RIGHT:
                toDone(position);
                break;
            case LEFT:
                toDelete(position);
                break;

//            case BACK:
//                Toast.makeText(this,"Back",Toast.LENGTH_SHORT).show();
//                break;

            default:
                break;
        }

        mListView.invalidateViews();
    }

    private void toDone(int position)
    {
        final String toFinish = "update " + mDataBaseHelper.TITLE_TABLE + " set " + mDataBaseHelper.ISDONE +
                " = '" + mDataBaseHelper.TRUE + "'," + mDataBaseHelper.LOGTIME +" = " + mDataBaseHelper.CURRENTTIME +
                " where " + mDataBaseHelper._ID + " = ?";

        final String toUnFinish = "update " + mDataBaseHelper.TITLE_TABLE + " set " + mDataBaseHelper.ISDONE +
                " = '" + mDataBaseHelper.FALSE + "'," + mDataBaseHelper.LOGTIME +" = " + mDataBaseHelper.CURRENTTIME +
                " where " + mDataBaseHelper._ID + " = ?";

        final ContentItem tp = (ContentItem) mListItems.get(position);

        if(tp.isDone.equals(mDataBaseHelper.FALSE))//从未完成到已完成
        {
            new Thread(new Runnable()
            {
                @Override
                public void run()
                {
                    db.execSQL(toFinish,new String[]{tp._id+""});
                }
            }).start();

            if(FinishCount == 0)
                mListItems.add(FinishLable);

            ++FinishCount;
            --unFinishCount;

            removeLable();

            tp.isDone = mDataBaseHelper.TRUE;
            mListItems.remove(tp);
            mListItems.add((unFinishCount == 0 ? 1 : unFinishCount + 2),tp);

            Toast.makeText(this,getResources().getString(R.string.toast_unf_to_f), Toast.LENGTH_SHORT).show();
        }
        else if(tp.isDone.equals(mDataBaseHelper.TRUE))//从已完成到未完成
        {
            new Thread(new Runnable()
            {
                @Override
                public void run()
                {
                    db.execSQL(toUnFinish,new String[]{tp._id+""});
                }
            }).start();

            if(unFinishCount == 0)
                mListItems.add(0,unFinishLable);

            ++unFinishCount;
            --FinishCount;

            removeLable();

            tp.isDone = mDataBaseHelper.FALSE;
            mListItems.remove(tp);
            mListItems.add(1,tp);

            Toast.makeText(this,getResources().getString(R.string.toast_f_to_unf), Toast.LENGTH_SHORT).show();
        }

      //  changeElse(position);
   //     initListItems();
     //   listAdapter.notifyDataSetInvalidated();
    }

    private void toDelete(final int position)
    {
        final String statement = "delete from " + mDataBaseHelper.TITLE_TABLE + " where " + mDataBaseHelper._ID +" = ?";
        final ContentItem tp = (ContentItem) mListItems.get(position);

        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                db.execSQL(statement,new String[]{tp._id+""});
            }
        }).start();

        if(tp.isDone.equals(mDataBaseHelper.FALSE))
            --unFinishCount;
        else if(tp.isDone.equals(mDataBaseHelper.TRUE))
            --FinishCount;

        mListItems.remove(tp);
        removeLable();

        Toast.makeText(this,getResources().getString(R.string.delete), Toast.LENGTH_SHORT).show();
    }

    private void removeLable()
    {
        if(unFinishCount == 0)
            mListItems.remove(unFinishLable);

        if(FinishCount == 0)
            mListItems.remove(FinishLable);

    }

//    private void changeElse(int position)
//    {
//        if (listAdapter.getCount() != position)
//        {
//            if (listAdapter.getItem(position).getClass() == LabelItem.class && listAdapter.getItem(position - 1).getClass() == LabelItem.class)
//            {
//                hasUnFinished = false;
//                mListItems.remove(position - 1);
//            }
//        }
//        else
//        {
//            if (listAdapter.getItem(position - 1).getClass() == LabelItem.class)
//            {
//                if(hasUnFinished == true)
//                    hasUnFinished = false;
//                else
//                    hasFinished = false;
//
//                mListItems.remove(position - 1);
//            }
//        }
//    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
    {
//        mAdapter.setSelectedPosition(position);
//        mAdapter.notifyDataSetInvalidated();
        Intent intent = new Intent(this,EditActivity.class);
        Bundle bundle = new Bundle();

        ContentItem item = (ContentItem) mListItems.get(position);

        bundle.putInt(mDataBaseHelper.TITLE_ID,item._id);
        bundle.putString(mDataBaseHelper.TITLE,item.mTitle);
        bundle.putString(mDataBaseHelper.TYPE,item.mType);
     //   bundle.putString(mDataBaseHelper.ISDONE,item.isDone);

        intent.putExtra(EditActivity.BUNDLE_CONTENT,bundle);

        startActivity(intent);
        //Toast.makeText(MainActivity.this,position+"",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState)
    {
        switch (scrollState)
        {
            case SCROLL_STATE_TOUCH_SCROLL:
                firstIndex = view.getLastVisiblePosition();
                break;

            case SCROLL_STATE_FLING:
                int nextIndex = view.getLastVisiblePosition();

                if(nextIndex > firstIndex)//向下滚动
                {
                    if(buttonAdd.getAlpha() == upAlpha)
                    {
                        ObjectAnimator tp1 = ObjectAnimator.ofFloat(buttonAdd, "alpha", downAlpha).setDuration(alphaDuration);
                        tp1.setInterpolator(new AccelerateDecelerateInterpolator());

                        tp1.start();
                    }
                }
                else //向上滚动
                {
                    if(buttonAdd.getAlpha() == downAlpha)
                    {
                        ObjectAnimator tp2 = ObjectAnimator.ofFloat(buttonAdd, "alpha", upAlpha).setDuration(alphaDuration);
                        tp2.setInterpolator(new AccelerateDecelerateInterpolator());

                        tp2.start();
                    }
                }
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount)
    {}
}