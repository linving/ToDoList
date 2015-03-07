package sasure.myapplication.listview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import sasure.myapplication.todolist.R;

/**
 * 内容列表项
 */
public class ContentItem implements ListItem
{
    /**
     * 保存该项的题目
     */
    public String mTitle;

    /**
     * 保存该项属于的类型
     */
    public String mType;

    /**
     * 保存该项是否已完成
     */
    public String isDone;

    /**
     * 保存该项在数据库表的_id
     */
    public int _id;

    public ContentItem(int _id,String mTitle,String mType,String isDone)
    {
        this._id = _id;
        this.mTitle = mTitle;
        this.mType = mType;
        this.isDone = isDone;
    }

    @Override
    public int getLayout()
    {
        return R.layout.content_layout;
    }
 
    @Override
    public boolean isClickable()
    {
        return true;
    }
 
    @Override
    public View getView(Context context, View convertView, LayoutInflater inflater)
    {
        convertView = inflater.inflate(getLayout(), null);
        TextView contentItem = (TextView) convertView.findViewById(R.id.item_view);

        contentItem.setText(mTitle);

        return convertView;
    }
}
