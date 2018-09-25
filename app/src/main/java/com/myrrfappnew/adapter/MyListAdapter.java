package com.myrrfappnew.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.List;


public abstract class MyListAdapter<T> extends BaseAdapter {
    private List<T> list;
    public MyListAdapter(List<T> list){
        this.list = list;
        if (this.list == null)
            this.list = new ArrayList<>();
    }

    public void setDataChange(List<T> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        if (list==null)
            return 0;
        return list.size();
    }

    @Override
    public Object getItem(int i) {
        return list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        return viewAndData(position,convertView,viewGroup,list);
    }
    public abstract View viewAndData(int position, View view, ViewGroup viewGroup,List<T> list);
}
