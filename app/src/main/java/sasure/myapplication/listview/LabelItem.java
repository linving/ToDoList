package sasure.myapplication.listview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import sasure.myapplication.todolist.R;

public class LabelItem implements ListItem {

    private String mLabel;
    public LabelItem(String label){
        mLabel = label;
    }

    @Override
    public int getLayout()
    {
        return R.layout.label_layout;
    }
 
    @Override
    public boolean isClickable()
    {
        return false;
    }
 
    @Override
    public View getView(Context context, View convertView, LayoutInflater inflater)
    {
        convertView = inflater.inflate(getLayout(), null);
        TextView title = (TextView) convertView.findViewById(R.id.label_item);
        title.setText(mLabel);
        return convertView;
    }

}
