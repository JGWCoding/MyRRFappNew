package com.myrrfappnew.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.myrrfappnew.R;
import com.myrrfappnew.activity.MainActivity;
import com.myrrfappnew.adapter.MyListAdapter;
import com.myrrfappnew.bean.WorkInfo;
import com.myrrfappnew.bean.WorkLogBean;
import com.myrrfappnew.utils.AppUtils;
import com.myrrfappnew.utils.DbHelper;
import com.myrrfappnew.utils.HttpManager;
import com.myrrfappnew.utils.TestData;
import com.myrrfappnew.utils.ToastUtils;

import java.util.List;

/**
 * Created by Administrator on 2017/2/21.
 */

public class NotFinishCauseFragment extends Fragment implements AdapterView.OnItemClickListener {
    private ListView listView;
    private String workId;
    private MyListAdapter<String> adapter;
    public static NotFinishCauseFragment getInstance(String workId) {
        Bundle bundle = new Bundle();
        bundle.putString("work_id", workId);
        NotFinishCauseFragment fragment = new NotFinishCauseFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        workId = getArguments().getString("work_id");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_not_finish_cause, null);
        assignViews(view);
        return view;
    }

    private void assignViews(View v) {
        listView = (ListView) v.findViewById(R.id.list_view);
        adapter = new MyListAdapter<String>(TestData.notCompateList()) {
            @Override
            public View viewAndData(int position, View view, ViewGroup viewGroup, List<String> list) {
                if (view == null) {
                    view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_not_finish_cause_item, null);
                    ViewHolder holder = new ViewHolder(view);
                    view.setTag(holder);
                }

                ViewHolder holder = (ViewHolder) view.getTag();
                holder.stateTv.setText(list.get(position));
                return view;
            }
        };
        listView.setOnItemClickListener(this);
        listView.setAdapter(adapter);

    }



    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        WorkLogBean bean = new WorkLogBean();
        MainActivity activity = (MainActivity) getActivity();
        WorkInfo workInfo = DbHelper.getInstance().getWorkById(workId);
        if (workInfo==null) {
            ToastUtils.showToast("改數據不存在了");
            return;
        }
        if (i == 9 || i == 10) { //如果跟法律有纠纷需特殊表明下
                workInfo.setTag("#");
                bean.setTag(workInfo.getTag());
                DbHelper.getInstance().updataWork(workInfo);
        } else {
                workInfo.setTag("");
                bean.setTag(workInfo.getTag());
                DbHelper.getInstance().updataWork(workInfo);
                ToastUtils.showToast(R.string.report_finish);
        }
        // TODO --> crn.no 显示
        bean.setWorkId(workId);
        bean.setWorkState(workInfo.getState());
        bean.setDesc("未完工" + "&" + adapter.getItem(i));
        bean.setCreatDate(AppUtils.getDate());
        bean.setTime(AppUtils.getTime());
        int id = DbHelper.getInstance().actionLog(bean);
        bean.setId(id);
        DbHelper.getInstance().updataWork(workId, 1);
        HttpManager.getInstance(getActivity()).uploadLog(bean);
        //跳转到签到页面
        MyFragmentManger.showWorkFragment((MainActivity) getActivity(),workInfo.getWorkId());
    }

   static class ViewHolder {
        private TextView stateTv;
        public ViewHolder(View v) {
            assignViews(v);
        }
        private void assignViews(View v) {
            stateTv = (TextView) v.findViewById(R.id.state_tv);
        }
    }
}
