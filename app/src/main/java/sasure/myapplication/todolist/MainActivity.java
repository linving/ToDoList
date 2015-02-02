package sasure.myapplication.todolist;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.LayoutAnimationController;
import android.view.animation.TranslateAnimation;
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

public class MainActivity extends SlidingListActivity implements slidecutListView.RemoveListener ,
        ListView.OnItemClickListener,ListView.OnScrollListener
{
    private SlidingMenu slidingMenu;
    //  private String [] mClassify;
    private Spinner mySpinner;
    public static Context mContext;
    private LayoutInflater mInflater;
    private ArrayList<ListItem> mListItems;
    private PartAdapter mAdapter;
    private slidecutListView mListView;
    public static int screenWidth;
    private ImageButton buttonAdd;

    private final float upAlpha = 1f;
    private final float downAlpha = 0.328f;
    private final long alphaDuration = 618;
    /**
     * 当前屏幕可见的第一个item在整个listview中的下标
     */
    private int firstIndex;

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


    private void initField()
    {
        mContext = this;
        mInflater = LayoutInflater.from(mContext);
        mListItems = new ArrayList<>();
        screenWidth = ((WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getWidth();//一定要在ListView之前调用！！
    //    Log.i("test","screenWidth="+screenWidth);
    }

    /**
     * 初始化ActionBar
     */
    private void initActionbar()
    {
        ActionBar actionBar = getSupportActionBar();
        //     mClassify = getResources().getStringArray(R.array.classify);

        ArrayAdapter<CharSequence> list = ArrayAdapter.createFromResource(this, R.array.classify, R.layout.spinner_item);
       list.setDropDownViewResource(R.layout.spinner_dropdown_item);

        View myView = LayoutInflater.from(this).inflate(R.layout.myspinner, null);
        mySpinner = (Spinner) myView.findViewById(R.id.myspinner);
        mySpinner.setAdapter(list);

        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM | ActionBar.DISPLAY_HOME_AS_UP | ActionBar.DISPLAY_SHOW_TITLE);//设置显示的选项

        ActionBar.LayoutParams layoutParams = new ActionBar.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
        layoutParams.gravity = Gravity.RIGHT; // set your layout's gravity to 'right'
        actionBar.setCustomView(myView, layoutParams);


//        BitmapDrawable bg = (BitmapDrawable) getResources().getDrawable(R.drawable.bg_striped);//设置ActionBar的背景
//        bg.setTileModeXY(Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);
        actionBar.setBackgroundDrawable(getResources().getDrawable(R.drawable.actionbar_background));

//      BitmapDrawable bgSplit = (BitmapDrawable)getResources().getDrawable(R.drawable.bg_striped_split_img);
//      bgSplit.setTileModeXY(Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);
//      getSupportActionBar().setSplitBackgroundDrawable(bgSplit);
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
        //    slidingMenu.setBehindOffsetRes(R.dimen.behindofset);
        slidingMenu.setBehindWidthRes(R.dimen.behind_width);
    }

    /**
     * 初始化ListView
     */
    public void initListView()
    {
        mListView = (slidecutListView) getListView();

        String[] Countries = getResources().getStringArray(R.array.countries);
        LabelItem label = new LabelItem("近期");
        mListItems.add(label);

        for (int i = 0; i < 30; i++)
        {


      //      for (int j = 0; j <= i; j++)
      //      {
                String item = new String("" + Countries[i]);
                ContentItem content = new ContentItem(item);
                mListItems.add(content);
      //      }
        }

        mAdapter = new PartAdapter(mListItems, mContext);

        mListView.setLayoutAnimation(getListAnim());
        mListView.setAdapter(mAdapter);
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

    }


    /**
     *
     * @return 飞入效果
     */
    private LayoutAnimationController getListAnim()
    {
        AnimationSet set = new AnimationSet(true);
        set.setDuration(200);
        set.setFillBefore(true);

        Animation animation = new AlphaAnimation(0.0f, 1.0f);
        set.addAnimation(animation);

        animation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
                -0.618f, Animation.RELATIVE_TO_SELF, 0.0f);
        set.addAnimation(animation);

        LayoutAnimationController controller = new LayoutAnimationController(set, 0.5f);
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
        mAdapter.remove(position);

        if (mAdapter.getCount() != position)
        {
            if (mAdapter.getItem(position).getClass() == LabelItem.class && mAdapter.getItem(position - 1).getClass() == LabelItem.class) {
                mAdapter.remove(position - 1);

            }
        }
        else
        {
            if (mAdapter.getItem(position - 1).getClass() == LabelItem.class)
                mAdapter.remove(position - 1);
        }
        switch (direction)
        {
            case RIGHT:
                Toast.makeText(this, position + "完成 ", Toast.LENGTH_SHORT).show();
                break;
            case LEFT:
                Toast.makeText(this, position + "删除  ", Toast.LENGTH_SHORT).show();
                break;

//            case BACK:
//                Toast.makeText(this,"Back",Toast.LENGTH_SHORT).show();
//                break;

            default:
                break;
        }
    }



    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
    {
//        mAdapter.setSelectedPosition(position);
//        mAdapter.notifyDataSetInvalidated();
        Toast.makeText(MainActivity.this,position+"",Toast.LENGTH_SHORT).show();
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