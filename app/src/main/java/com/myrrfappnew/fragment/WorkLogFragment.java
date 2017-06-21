package com.myrrfappnew.fragment;

import android.text.TextUtils;
import android.view.View;
import android.widget.ListView;

import com.myrrfappnew.R;
import com.myrrfappnew.adapter.MyBaseAdapter;
import com.myrrfappnew.bean.WorkLogBean;
import com.myrrfappnew.utils.DbHelper;

import java.util.List;

/**
 * Created by Administrator on 2017/6/21.
 */

public class WorkLogFragment extends BaseFragment {
    List<WorkLogBean> list;
    private ListView listView;
    MyBaseAdapter<WorkLogBean> adapter;
    @Override
    protected View getMyView() {
        View view = View.inflate(getActivity(), R.layout.fragment_work_log,null);
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
            adapter = new MyBaseAdapter<WorkLogBean>(getActivity(),R.layout.fragment_work_log_item,list) {
                @Override
                protected void dataAndView(Holder holder, WorkLogBean info) {
                    if (TextUtils.isEmpty(info.getTag())) { //判断是否有法律纠纷
                        holder.setTxetView(R.id.work_id_tv,info.getWorkId());
                    } else {
                       holder.setTxetView(R.id.work_id_tv,info.getWorkId()+" "+info.getTag());
                    }
            holder.getView(R.id.iv_star).setVisibility(info.getUpload()==1?View.GONE:View.VISIBLE);
                    holder.setTxetView(R.id.date_tv,info.getCreatDate());
                    holder.setTxetView(R.id.time_tv,info.getTime());
                    holder.setTxetView(R.id.state_tv,info.getDesc());
                }
            };
            listView.setAdapter(adapter);
        } else {
            adapter.setDataChange(list);
        }
    }
}
