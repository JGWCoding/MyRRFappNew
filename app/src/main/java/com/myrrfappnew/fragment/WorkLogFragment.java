package com.myrrfappnew.fragment;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.myrrfappnew.R;
import com.myrrfappnew.adapter.MyListAdapter;
import com.myrrfappnew.bean.WorkLogBean;
import com.myrrfappnew.utils.AppUtils;
import com.myrrfappnew.utils.DbHelper;

import java.util.List;

/**
 * Created by Administrator on 2017/6/21.
 */

public class WorkLogFragment extends BaseFragment {
    List<WorkLogBean> list;
    private ListView listView;
    MyListAdapter<WorkLogBean> adapter;

    @Override
    protected View getMyView() {
        View view = View.inflate(getActivity(), R.layout.fragment_work_log, null);
        listView = (ListView) view.findViewById(R.id.list_view);
        return view;
    }

    @Override
    protected void searchData(String key) { //子线程中查询数据
        list = DbHelper.getInstance().getLogWithKey(key);
    }

    @Override
    protected void threadGetData() { //子线程中查询数据 --->不需要网络数据
        list = DbHelper.getInstance().getLog();
    }

    @Override
    protected void mainRefresh() { //主线程刷新视图
        if (adapter == null) {
            adapter = new MyListAdapter<WorkLogBean>(list) {
                @Override
                public View viewAndData(int position, View view, ViewGroup viewGroup, List<WorkLogBean> list) {
                    WorkLogHolder holder = null;
                    if (view == null) {
                        view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_work_log_item, null);
                        holder = new WorkLogHolder(view);
                        view.setTag(holder);
                    } else {
                        holder = (WorkLogHolder) view.getTag();
                    }
                    WorkLogBean info = list.get(position);
                    if (TextUtils.isEmpty(info.getTag())) {
                        if (info.getWorkId().startsWith(AppUtils.getDeviceID(getActivity()))) {
                            holder.workIdTv.setText(AppUtils.getWhiteHeadId(info.getWorkId()));
                        } else {
                            holder.workIdTv.setText(info.getWorkId());
                        }
                    } else {
                        if (info.getWorkId().startsWith(AppUtils.getDeviceID(getActivity()))) {
                            String[] id = info.getWorkId().split("-");
                            holder.workIdTv.setText(id[0].substring(id[0].length() - 9, id[0].length()) + "-" + id[1] + " " + info.getTag());
                        } else {
                            holder.workIdTv.setText(info.getWorkId() + " " + info.getTag());
                        }
                    }
                    holder.dateTv.setText(info.getCreatDate());
                    holder.timeTv.setText(info.getTime());
                    holder.stateTv.setText(info.getDesc());
                    if (info.getUpload() == 1) {
                        holder.starIv.setVisibility(View.GONE);
                    } else {
                        holder.starIv.setVisibility(View.VISIBLE);
                    }
                    return view;
                }
            };
            listView.setAdapter(adapter);
        } else {
            adapter.setDataChange(list);
        }
    }

    static class WorkLogHolder {
        private TextView workIdTv, dateTv, timeTv, stateTv;
        private ImageView starIv;
        public WorkLogHolder(View view) {
            workIdTv = (TextView) view.findViewById(R.id.work_id_tv);
            dateTv = (TextView) view.findViewById(R.id.date_tv);
            timeTv = (TextView) view.findViewById(R.id.time_tv);
            stateTv = (TextView) view.findViewById(R.id.state_tv);
            starIv = (ImageView) view.findViewById(R.id.iv_star);
        }
    }
}
