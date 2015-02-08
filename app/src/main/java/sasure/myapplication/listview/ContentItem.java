package sasure.myapplication.listview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import sasure.myapplication.todolist.R;

public class ContentItem implements ListItem {
	 
    public String mTitle;
    public String mType;
    public String isDone;
    public int _id;
//    private final int itemWidth = MainActivity.screenWidth;
//    private final int itemHeight = MainActivity.mContext.getResources().getDimensionPixelOffset(R.dimen.item_height);
//    private final  int textSize = MainActivity.mContext.getResources().getDimensionPixelOffset(R.dimen.list_content_text);

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
//        TextView toDelete = (TextView) convertView.findViewById(R.id.to_delete);
//        TextView toDone = (TextView) convertView.findViewById(R.id.to_done);
//
//        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
//                screenWidth,
//                LinearLayout.LayoutParams.MATCH_PARENT);
//
//
//        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
//                screenWidth,
//                LinearLayout.LayoutParams.MATCH_PARENT);
//        Log.i("test",convertView.getWidth() +"");
//        item.setLayoutParams(params);
//        toDelete.setLayoutParams(params);
//        toDone.setLayoutParams(params);

//        LinearLayout ll =  (LinearLayout) inflater.inflate(R.layout.content_layout,null);
//        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
//                itemWidth,
//                itemHeight);
//
//        TextView contentItem = (TextView) inflater.inflate(R.layout.content_item,null);
//        View toDone = inflater.inflate(R.layout.to_done,null);
//        View toDelete = inflater.inflate(R.layout.to_delete,null);
//
//        ll.addView(toDone,params);
//        ll.addView(contentItem,params);
//        ll.addView(toDelete,params);
//
        contentItem.setText(mTitle);
//
//        convertView = ll;

        return convertView;
    }

}
