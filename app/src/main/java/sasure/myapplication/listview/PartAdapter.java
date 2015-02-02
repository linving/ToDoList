package sasure.myapplication.listview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;

/**
 * Created by ZHOU on 2015-1-26.
 */
public class PartAdapter extends BaseAdapter
{
    private ArrayList<ListItem> mListItems;
    private Context mContext;
    private LayoutInflater mInflater;
    private int seletedPosition = -1;

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

//        if(seletedPosition == position && mListItems.get(position).getClass() == ContentItem.class)
//        {
//            tp.setSelected(true);
//            tp.setPressed(true);
//            tp.setBackgroundColor(mContext.getResources().getColor(R.color.selected));
//        }
//        else
//        {
//            tp.setSelected(false);
//            tp.setPressed(false);
//            tp.setBackgroundColor(mContext.getResources().getColor(R.color.list_item));
//        }

        return tp;
    }

    public void remove(int position)
    {
        Object tp = getItem(position);
        mListItems.remove(tp);
        this.notifyDataSetChanged();
    }

    public void setSelectedPosition(int selectedPosition)
    {
        this.seletedPosition = selectedPosition;
    }
}
