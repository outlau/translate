package com.production.outlau.translate;

import android.content.Context;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class ListViewAdapter extends BaseAdapter {

    private ArrayList<WordPair> listData;
    private LayoutInflater layoutInflater;
    private Context context;

    public ListViewAdapter(Context context, ArrayList<WordPair> listData) {
        this.listData = listData;
        layoutInflater = LayoutInflater.from(context);
        this.context = context;
    }

    @Override
    public int getCount() {
        return listData.size();
    }

    @Override
    public Object getItem(int position) {
        return listData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {

            convertView = layoutInflater.inflate(R.layout.list_inflater, null);
            holder = new ViewHolder();

            holder.left = (TextView) convertView.findViewById(R.id.list_left);
            holder.right = (TextView) convertView.findViewById(R.id.list_right);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.left.setText(listData.get(position).leftWord);
        holder.right.setText(listData.get(position).rightWord);
        return convertView;
    }

    static class ViewHolder {
        TextView left;
        TextView right;
    }


}
