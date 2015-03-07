package sasure.myapplication.listview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;

/**
 * 对BaseAdapter的一个继承，传入ListItem的集合，
 * 通过判断不同的ListItem的实现类来生成不同的列表项
 * Created by ZHOU on 2015-1-26.
 */
public class PartAdapter extends BaseAdapter
{
    /**
     * 列表项
     */
    private ArrayList<ListItem> mListItems;

    /**
     * 包信息
     */
    private Context mContext;

    /**
     * 用来加载View
     */
    private LayoutInflater mInflater;
 //   private int seletedPosition = -1;

    public PartAdapter(ArrayList<ListItem> mListItems,Context mContext)
    {
        this.mListItems = mListItems;
        this.mContext = mContext;

        mInflater = LayoutInflater.from(mContext);
    }

    @Override
    public int getCount()
    {
        return mListItems.size();
    }

    @Override
    public Object getItem(int position)
    {
        return mListItems.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    @Override
    public boolean isEnabled(int position)
    {
        return mListItems.get(position).isClickable();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        View tp = mListItems.get(position).getView(mContext, convertView, mInflater);
        return tp;
    }
}
