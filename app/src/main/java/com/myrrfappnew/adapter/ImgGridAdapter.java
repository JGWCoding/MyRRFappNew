package com.myrrfappnew.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.myrrfappnew.R;

import java.util.List;

/**
 * 展示拍的照片的adapter
 */
public class ImgGridAdapter extends BaseAdapter {
    private List<String> list;
    private Context context;

    private int state;//标识 1到场  2 完成 3未完成

    public List<String> getList() {
        return list;
    }
    public OnImgRemove onImgRemove;

    public void setOnImgRemove(OnImgRemove onImgRemove) {
        this.onImgRemove = onImgRemove;
    }

    public ImgGridAdapter(Context context, List<String> list, int state) {
        this.list = list;
        this.context = context;
        this.state = state;
    }

    @Override
    public int getCount() {
        if (list == null) return 0;
        return list.size();
    }

    public void setList(List<String> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.fragment_photo_item, null);
            ViewHolder viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        }
        ViewHolder holder = (ViewHolder) convertView.getTag();
        holder.itemImg.setImageURI(Uri.parse(list.get(position)));
        holder.itemDeleter.setVisibility(View.VISIBLE);
        holder.itemDeleter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //移除此图片
                showDeleteDialog(position);
            }
        });
        return convertView;
    }

    private void showDeleteDialog(final int position) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(R.string.delete_dialog_tip);
        builder.setMessage(R.string.delete_dialog_message);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (onImgRemove != null) {
                    onImgRemove.onRemove(list.get(position), state);
                }
                list.remove(position);
                notifyDataSetChanged();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        builder.create().show();
    }

    class ViewHolder {
        private ImageView itemImg;
        private ImageView itemDeleter;

        private void assignViews(View view) {
            itemImg = (ImageView) view.findViewById(R.id.item_img);
            itemDeleter = (ImageView) view.findViewById(R.id.item_deleter);
        }

        ViewHolder(View view) {
            assignViews(view);
        }
    }

    /**
     * 删除图片接口
     */
    public interface OnImgRemove {
        void onRemove(String imgPath, int state);
    }
}
