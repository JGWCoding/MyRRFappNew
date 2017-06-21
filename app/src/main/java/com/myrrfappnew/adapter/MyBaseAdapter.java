package com.myrrfappnew.adapter;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Administrator on 2017/6/15.
 */

public abstract class MyBaseAdapter<T> extends BaseAdapter {
    List<T> data;
    int layout;
    Context context;

    public MyBaseAdapter(Context context, @LayoutRes int layout, List<T> data) {
        this.data = data;
        this.layout = layout;
        this.context = context;
    }

    @Override
    public int getCount() {
        if (data == null) {
            return 0;
        }
        return data.size();
    }

    public void setDataChange(List<T> data) {
        this.data = data;
        this.notifyDataSetChanged();
    }
    @Override
    public T getItem(int position) {
        this.notifyDataSetChanged();
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    //convertView 划出屏幕的view对象
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Holder holder = null;
        if (convertView == null) {
            convertView = View.inflate(context, layout, null);
            holder = new Holder(convertView);
            convertView.setTag(holder);
        }else {
            holder = (Holder) convertView.getTag();  //不能使用静态内部类,不然全是同样的视图
        }
        dataAndView(holder,getItem(position));
        return convertView;
    }

    protected abstract void dataAndView(Holder holder, T item);


    protected  class Holder {
        Holder holder;
        View rootView;
    SparseArray<View>  viewList = new SparseArray();
        public Holder(View rootView) {
            this.rootView = rootView;
        }
        public View getView(int viewId) {
            if (viewList.get(viewId)!=null) {
                return viewList.get(viewId);
            }else {
                View itemView = rootView.findViewById(viewId);
                viewList.put(viewId, itemView);
                return itemView;
            }
        }
        public TextView setTxetView(int viewId,String content) {
            TextView textView= (TextView) getView(viewId);
            textView.setText(content);
            return textView;
        }
    }
}

