package sasure.myapplication.listview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

public interface ListItem
{

    public boolean isClickable();

    public int getLayout();
    public View getView(Context context, View convertView, LayoutInflater inflater);
     
}
