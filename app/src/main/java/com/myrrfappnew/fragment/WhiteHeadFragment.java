package com.myrrfappnew.fragment;

import android.content.Intent;
import android.view.View;
import android.widget.ListView;

import com.myrrfappnew.R;
import com.myrrfappnew.adapter.MyBaseAdapter;
import com.myrrfappnew.bean.WorkInfo;
import com.myrrfappnew.seriver.SyncService;
import com.myrrfappnew.utils.AppUtils;
import com.myrrfappnew.utils.DbHelper;
import com.myrrfappnew.utils.DialogUtils;
import com.myrrfappnew.utils.ToastUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * 白头单展示页面
 */

public class WhiteHeadFragment extends BaseFragment {

    @BindView(R.id.list_view)
    ListView listView;
    Unbinder unbinder;
    private List<WorkInfo> list;
    private MyBaseAdapter adapter;

    @Override
    protected View getMyView() { //我的视图
        View rootView = View.inflate(getActivity(), R.layout.fragment_white_head, null);
        unbinder = ButterKnife.bind(this, rootView);
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
            adapter = new MyBaseAdapter<WorkInfo>(getActivity(), R.layout.fragment_white_head_list_view, list) {
                @Override
                protected void dataAndView(Holder holder, final WorkInfo item) {
                    holder.setTxetView(R.id.tv_id, AppUtils.getWhiteHeadId(item.getWorkId()));
                    holder.setTxetView(R.id.tv_date, item.getDate());
                    holder.setTxetView(R.id.tv_address, item.getAddress());
                    holder.setTxetView(R.id.tv_type, item.getType().split(",")[0]);
                    holder.setTxetView(R.id.tv_state, item.getState() == 0 ? "未到場" : item.getState() == 1 ? "未完工" : "已完工");
                    holder.getView(R.id.iv_star).setVisibility(item.getUpload() == 1 ? View.GONE : View.INVISIBLE);
                    if (AppUtils.isEmpty(item.getTag())) {
                        holder.getView(R.id.iv_star).setVisibility(View.GONE);
                    } else {
                        holder.setTxetView(R.id.iv_star, item.getTag()).setVisibility(View.VISIBLE);
                    }
                    holder.getView(R.id.tv_match).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (!AppUtils.isNetworkAvailable(getActivity())) {
                                DialogUtils.alertDialog(getActivity(), "没有網絡，請有網絡時再配對", null, false, null);
                            } else if (item.getUpload() == 0) {
                                DialogUtils.alertDialog("立即上傳", "取消上傳", getActivity(), "沒有上傳是否立即上傳你的白頭單,請先上傳再配對", null, false, new Runnable() {
                                    @Override
                                    public void run() {
                                        Intent intent = new Intent(getActivity(), SyncService.class);
                                        getActivity().startService(intent);
                                    }
                                });
                            } else {
                                //TODO 打开配对列表即跳转到未到场Fragment
                                ToastUtils.showToast("开始配对");
                            }
                        }
                    });


                }
            };
        listView.setAdapter(adapter);
        } else {
            adapter.setDataChange(list);
        }

    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}