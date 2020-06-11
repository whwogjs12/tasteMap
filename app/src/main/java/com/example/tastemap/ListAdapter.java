package com.example.tastemap;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RatingBar;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class ListAdapter extends BaseAdapter implements RatingBar.OnRatingBarChangeListener
{
    LayoutInflater inflater = null;
    private ArrayList<ListData> items = null;

    public ListAdapter(ArrayList<ListData> tasteList)
    {
        this.items = tasteList;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        if (convertView == null)
        {
            final Context context = parent.getContext();
            if (inflater == null)
            {
                inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            }
            convertView = inflater.inflate(R.layout.listviewitem, parent, false);
        }

        TextView shopName = (TextView)convertView.findViewById(R.id.item_name);
        TextView shopAddress = (TextView)convertView.findViewById(R.id.item_address);
        RatingBar ratingBar = convertView.findViewById(R.id.ratingBar);

        ListData data = items.get(position);
        ratingBar.setRating(data.getRate());
        ratingBar.setTag(position);
        ratingBar.setOnRatingBarChangeListener(this);
        shopName.setText(data.getName());
        shopAddress.setText(data.getAddress());

        return convertView;
    }


    public void setItem(ArrayList<ListData> addedItem)
    {
        items = addedItem;
        notifyDataSetChanged();
    }


    public void addItem(ListData item){
//       ListData item = new ListData(shopName, address);
        int count = getCount();
        this.items.add(count, item);
        Log.d("TEST", "addItem: 아이템 추가 완료");
        Log.d("TEST", "count: " +count);
    }

    @Override
    public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser)
    {
        ListData data = items.get(Integer.parseInt(String.valueOf(ratingBar.getTag())));
        data.setRate((int)rating);
        notifyDataSetChanged();
        Log.d("레이팅은?",String.valueOf(data.getRate()));
    }
}