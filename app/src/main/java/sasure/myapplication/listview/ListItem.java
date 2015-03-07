package sasure.myapplication.listview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

/**
 * 接口，通过实现这个接口可以产生不同的列表项
 */
public interface ListItem
{
    /**
     * 是否可被点击
     * @return
     */
    public boolean isClickable();

    /**
     * 获取列表项的view在R类的引用
     * @return
     */
    public int getLayout();

    /**
     * 通过上面的方法包装convertView并返回
     * @param context
     * @param convertView
     * @param inflater
     * @return
     */
    public View getView(Context context, View convertView, LayoutInflater inflater);
//    public String getTitle();
//    public String getType();
}
