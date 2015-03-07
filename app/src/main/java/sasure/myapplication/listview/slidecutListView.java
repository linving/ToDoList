package sasure.myapplication.listview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.animation.OvershootInterpolator;
import android.widget.AdapterView;
import android.widget.ListView;

import com.actionbarsherlock.internal.nineoldandroids.animation.Animator;
import com.actionbarsherlock.internal.nineoldandroids.animation.AnimatorSet;
import com.actionbarsherlock.internal.nineoldandroids.animation.ObjectAnimator;

import sasure.myapplication.todolist.MainActivity;
import sasure.myapplication.todolist.R;

/**
 * 自定义列表
 */
public class slidecutListView extends ListView implements Animator.AnimatorListener
{
	/**
	 * 当前滑动的position
	 */
	private int slidePosition;

	/**
	 * 手指按下X的坐标
	 */
	private float downY;

	/**
	 * 手指按下Y的坐标
	 */
	private float downX;

    /**
     * 保留第一次的X坐标
     */
//	private float firstDownX;

	/**
	 * 屏幕宽度
	 */
	private  final int  screenWidth = MainActivity.screenWidth;

	/**
	 * 具体的列表项
	 */
	private View itemView;

    /**
     * 动画集合类
     */
    private AnimatorSet set;

    /**
     * 标准速度
     */
//	private static final int SNAP_VELOCITY = 500;

	/**
	 * 速度追踪对象
	 */
//	private VelocityTracker velocityTracker;

	/**
	 * 是否响应滑动，默认为不响应
	 */
	private boolean isSlide = false;

	/**
	 * 认为是用户滑动的最小距离
	 */
	private int mTouchSlop;

	/**
	 *  移除item后的回调接口
	 */
	private RemoveListener mRemoveListener;

	/**
	 * 用来指示item滑出屏幕的方向,向左或者向右,用一个枚举值来标记
	 */
	private RemoveDirection removeDirection;

	/**
	 * 是否为LabelItem项
	 */
	private boolean istitle ;

    /**
     * 保留包
     */
//	private Context mContext;

    /**
     *  滑动删除方向的枚举值
     */
	public enum RemoveDirection 
	{
		RIGHT, LEFT, BACK;
	}

	public slidecutListView(Context context)
    {
		this(context, null);
	}

	public slidecutListView(Context context, AttributeSet attrs)
    {
		this(context, attrs, 0);
	}

	public slidecutListView(Context context, AttributeSet attrs, int defStyle)
    {
		super(context, attrs, defStyle);
		mTouchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
        set = new AnimatorSet();
	}
	
	/**
	 * 设置滑动删除的回调接口
	 * @param removeListener
	 */
	public void setRemoveListener(RemoveListener removeListener)
    {
		this.mRemoveListener = removeListener;
	}

	/**
	 * 分发事件，主要做的是判断点击的是那个item
	 */
	@Override
	public boolean dispatchTouchEvent(MotionEvent event)
    {
        if(!set.isRunning())
        {
            switch (event.getAction())
            {
                case MotionEvent.ACTION_DOWN:

                    //firstDownX = downX = event.getX();
                    downY = event.getY();

                    slidePosition = pointToPosition((int)downX, (int) downY);

                    // 无效的position, 不做任何处理
                    if (slidePosition == AdapterView.INVALID_POSITION)
                    {
                        break;
                    }

                    // 获取我们点击的item view
                    int position = slidePosition - getFirstVisiblePosition();
                    View tp = getChildAt(position);

                    if (tp.findViewById(R.id.label_item) != null)//label_item的装载有变记得改
                    {
                        istitle = true;
                    }
                    else
                    {
                        itemView = tp.findViewById(R.id.item_view);
                        istitle = false;
                    }
                    break;

                case MotionEvent.ACTION_MOVE:
                    if (((Math.abs(event.getX() - downX) > mTouchSlop
                            && Math.abs(event.getY() - downY) < mTouchSlop))
                            && istitle == false && slidePosition != AdapterView.INVALID_POSITION)
                    {
                        isSlide = true;
                    }
                   break;
            }
        }
		return super.dispatchTouchEvent(event);
	}

	/**
	 * 往右滑动
	 */
	private void scrollRight()
    {
		removeDirection = RemoveDirection.RIGHT;
		final float delta = screenWidth - itemView.getX();

        ObjectAnimator mObAnimator = ObjectAnimator.ofFloat(this,"ItemScrollX",screenWidth);
        ObjectAnimator alphaAnimator = ObjectAnimator.ofFloat(itemView,"alpha",0);

        set.setDuration((long) Math.abs(delta));
        set.playTogether(mObAnimator,alphaAnimator);
        set.addListener(this);
        set.start();
	}

	/**
	 * 向左滑动
	 */
	private void scrollLeft()
    {
		removeDirection = RemoveDirection.LEFT;
		final float delta = screenWidth + itemView.getX();

        ObjectAnimator mObAnimator = ObjectAnimator.ofFloat(this,"ItemScrollX",-screenWidth);
        ObjectAnimator alphaAnimator = ObjectAnimator.ofFloat(itemView,"alpha",0);

        set.setDuration((long) Math.abs(delta));
        set.playTogether(mObAnimator,alphaAnimator);
        set.addListener(this);
        set.start();
	}

    /**
     * 回到原位
     */
    public void scrollBack()
    {
        removeDirection = RemoveDirection.BACK;
        final float delta = itemView.getX();

        ObjectAnimator mObAnimator = ObjectAnimator.ofFloat(this,"ItemScrollX",0);
        ObjectAnimator alphaAnimator = ObjectAnimator.ofFloat(itemView,"alpha",1);

        set.setDuration((long) Math.abs(delta));
        set.playTogether(mObAnimator,alphaAnimator);
        set.setInterpolator(new OvershootInterpolator());
        set.addListener(this);
        set.start();
    }

	/**
	 * 根据手指滚动itemView的距离来判断是滚动到开始位置还是向左或者向右滚动
	 */
	private void scrollByDistanceX()
    {
		// 如果向左滚动的距离大于conDelete，就让其删除
        final float canDelete = screenWidth * 0.328f;

		if (itemView.getX() <= -canDelete)
        {
       //     Log.i("test",itemView.getX() +"  left  " + -canDelete);
			scrollLeft();
		}
        else if (itemView.getX() >= canDelete)
        {
     //       Log.i("test",itemView.getX() +"  right  " + -canDelete);
			scrollRight();
		}
        else
        {
			// 滚回到原始位置
            scrollBack();
		}
	}

	/**
	 * 处理我们拖动ListView item的逻辑
	 */
	@Override
	public boolean onTouchEvent(MotionEvent ev)
    {
		if (!set.isRunning() && isSlide == true && itemView != null )
        {

			requestDisallowInterceptTouchEvent(true);

			final int action = ev.getAction();
			float x = ev.getX();

			switch (action)
            {
			case MotionEvent.ACTION_DOWN:
				break;

			case MotionEvent.ACTION_MOVE:
				MotionEvent cancelEvent = MotionEvent.obtain(ev);
	            cancelEvent.setAction(MotionEvent.ACTION_CANCEL |
	                       (ev.getActionIndex()<< MotionEvent.ACTION_POINTER_INDEX_SHIFT));
	            onTouchEvent(cancelEvent);

				float deltaX = itemView.getX() - (downX - x);
				downX = x;
                float alphaRate =1 - Math.abs(itemView.getX()) / screenWidth;

				itemView.setX(deltaX);
                itemView.setAlpha(alphaRate);

				return true;  //拖动的时候ListView不滚动

			case MotionEvent.ACTION_UP:
					scrollByDistanceX();
    			    return true;
			}
		}

		//否则直接交给ListView来处理onTouchEvent事件
		return super.onTouchEvent(ev);
	}

    @Override
    public void onAnimationStart(Animator animation)
    {}

    @Override
    public void onAnimationEnd(Animator animation)
    {
        if(mRemoveListener == null)
            throw new NullPointerException("RemoveListener is null, we should called setRemoveListener()");

        if(removeDirection != RemoveDirection.BACK)
            mRemoveListener.removeItem(removeDirection, slidePosition);

        isSlide = false;

        set = new AnimatorSet();
    }

    @Override
    public void onAnimationCancel(Animator animation)
    {
    }

    @Override
    public void onAnimationRepeat(Animator animation)
    {
    }

    /**
     * 方便动画类调用
     * @return
     */
    public float getItemScrollX()
    {
        return itemView.getX();
    }

    /**
     * 方便动画类调用
     * @param value
     */
    public void setItemScrollX(float value)
    {
        itemView.setX(value);
    }

	/**
	 * 添加用户的速度跟踪器
	 * 
	 * @param event
	 */
//	private void addVelocityTracker(MotionEvent event)
//    {
//		if (velocityTracker == null)
//        {
//			velocityTracker = VelocityTracker.obtain();
//		}
//
//		velocityTracker.addMovement(event);
//	}

	/**
	 * 移除用户速度跟踪器
	 */
//	private void recycleVelocityTracker()
//    {
//		if (velocityTracker != null)
//        {
//			velocityTracker.recycle();
//			velocityTracker = null;
//		}
//	}

	/**
	 * 当ListView item滑出屏幕，回调这个接口
	 * 我们需要在回调方法removeItem()中移除该Item,然后刷新ListView
	 */
	public interface RemoveListener
    {
		public void removeItem(RemoveDirection direction, int position);
	}
}
