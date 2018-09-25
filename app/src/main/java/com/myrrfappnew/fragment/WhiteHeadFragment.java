package com.myrrfappnew.fragment;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.myrrfappnew.R;
import com.myrrfappnew.activity.MainActivity;
import com.myrrfappnew.adapter.MyListAdapter;
import com.myrrfappnew.bean.WorkInfo;
import com.myrrfappnew.seriver.SyncService;
import com.myrrfappnew.utils.AppUtils;
import com.myrrfappnew.utils.DbHelper;
import com.myrrfappnew.utils.DialogUtils;

import java.util.List;

/**
 * 白头单展示页面
 */

public class WhiteHeadFragment extends BaseFragment implements AdapterView.OnItemClickListener {

    ListView listView;
    private List<WorkInfo> list;
    public MyListAdapter<WorkInfo> adapter;

    @Override
    protected View getMyView() { //我的视图
        View rootView = View.inflate(getActivity(), R.layout.fragment_white_head, null);
        listView = (ListView) rootView.findViewById(R.id.list_view);
        listView.setOnItemClickListener(this);
        return rootView;
    }

    @Override
    protected void searchData(String key) {
        list = DbHelper.getInstance().getAllWhiteHeadWithKey(key);
    }

    @Override
    protected void threadGetData() { //得到数据  ---> 在子线程中 定要注意要在主线程中更新视图
        list = DbHelper.getInstance().getAllWhiteHead();
    }

    @Override
    protected void mainRefresh() { //主线程中更新视图
        //TODO 打开配对列表即跳转到未到场Fragment
        if (adapter == null) {
            adapter = new MyListAdapter<WorkInfo>(list) {
                @Override
                public View viewAndData(int position, View view, ViewGroup viewGroup, List<WorkInfo> list) {
                    ViewHolder holder = null;
                    if (view == null) {
                        view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_white_head_list_view, null);
                         holder = new ViewHolder(view);
                        view.setTag(holder);
                    }else {
                        holder = (ViewHolder) view.getTag();
                    }
                    final WorkInfo info = list.get(position);
                    holder.tvRemark.setText(info.getNotCompleteImgs());//显示一个白头单的备注字段
                    holder.tvId.setText(AppUtils.getWhiteHeadId(info.getWorkId()));
                    holder.tvDate.setText(info.getDate());
                    holder.tvAddress.setText(info.getAddress());
                    holder.tvType.setText(info.getType());
                    switch (info.getState()) {
                        case 0:
                            holder.tvState.setText("未到場");
                            break;
                        case 1:
                            holder.tvState.setText("未完工");
                            break;
                        case 2:
                            holder.tvState.setText("已完工");
                            break;
                    }
                    holder.ivStar.setVisibility(info.getUpload() == 1?View.GONE:View.VISIBLE);
                    holder.tvMatch.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (AppUtils.isNetworkAvailable(getActivity())) {
                                DialogUtils.alertDialog(getActivity(),"没有網絡，請有網絡時再配對",null,true,null);
                            }else if(info.getUpload() == 0){
                                DialogUtils.alertDialog("取消上傳", "立即上傳", getActivity(), "沒有上傳是否立即上傳你的白頭單,請先上傳再配對", null, true, new Runnable() {
                                    @Override
                                    public void run() {
                                        Intent intent = new Intent(getActivity(), SyncService.class);
                                        getActivity().startService(intent);
                                    }
                                });
                            }else {
                                //TODO 配对
                            }
                        }
                    });
                    return view;
                }
            };
            listView.setAdapter(adapter);
        } else {
            adapter.setDataChange(list);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        MyFragmentManger.showWorkFragment((MainActivity) getActivity(),list.get(position).getWorkId());
    }

    static class ViewHolder {
        public ViewHolder(View v) {
            tvMatch = (TextView) v.findViewById(R.id.tv_match);
            tvId = (TextView) v.findViewById(R.id.tv_id);
            tvDate = (TextView) v.findViewById(R.id.tv_date);
            tvAddress = (TextView) v.findViewById(R.id.tv_address);
            tvType = (TextView) v.findViewById(R.id.tv_type);
            tvState = (TextView) v.findViewById(R.id.tv_state);
            ivStar = (ImageView) v.findViewById(R.id.iv_star);
            tvRemark = (TextView) v.findViewById(R.id.tv_remark);
        }
        private TextView tvMatch, tvId, tvDate, tvAddress, tvType, tvState,tvRemark;
        private ImageView ivStar;
    }
}