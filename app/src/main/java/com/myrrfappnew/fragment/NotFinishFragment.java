package com.myrrfappnew.fragment;

import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.myrrfappnew.MyApp;
import com.myrrfappnew.R;
import com.myrrfappnew.activity.ExplainActivity;
import com.myrrfappnew.adapter.MyBaseAdapter;
import com.myrrfappnew.bean.WorkInfo;
import com.myrrfappnew.utils.AppUtils;
import com.myrrfappnew.utils.Constant;
import com.myrrfappnew.utils.DbHelper;
import com.myrrfappnew.utils.PresentComparator;
import com.myrrfappnew.utils.ThreadUtils;
import com.myrrfappnew.utils.ToastUtils;

import java.util.Collections;
import java.util.List;

/**
 * Created by Administrator on 2017/6/16.
 */

public class NotFinishFragment extends BaseFragment implements AdapterView.OnItemClickListener {

    private ListView listView;
    private List<WorkInfo> list;
    private MyBaseAdapter adapter;
    @Override
    protected View getMyView() {
     View view =   View.inflate(getActivity(),R.layout.fragment_not_finish,null);
        listView = (ListView) view.findViewById(R.id.list_view);
        listView.setOnItemClickListener(this);
        return view;
    }

    @Override
    protected void searchData(String key) {
        list = DbHelper.getInstance().searchWorkList(Constant.notFinish, key);
    }

    @Override
    protected void threadGetData() {
        if (Constant.isNetWorkGetData) {
            queryRRFAll(Constant.notFinish);
        }
        list = DbHelper.getInstance().getWorkList(Constant.notFinish);
        if (list != null) {
            if (list.size() > 1)
                Collections.sort(list, new PresentComparator(imei));
        }
    }

    @Override
    protected void mainRefresh() {
        if (adapter == null) {
            adapter = new MyBaseAdapter<WorkInfo>(getActivity(), R.layout.fragment_not_finish_item, list) {
                @Override
                protected void dataAndView(Holder holer, final WorkInfo info) {
                    holer.setTxetView(R.id.work_id_tv, info.getUrgent() + " " + info.getWorkId() + "(" + info.getWorker() + ")");
                    holer.setTxetView(R.id.date_tv, info.getDate());
                    holer.setTxetView(R.id.address_tv, info.getAddress());
                    holer.getView(R.id.pdf_tv).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            ThreadUtils.pools.execute(new Runnable() {
                                @Override
                                public void run() {
                                    //显示加载中 --->下载 完成后自动跳转到打开 -->失败提示下-->关闭的显示条
                                    final String fileName = downLoadFile(info.getPdfLink());
                                    MyApp.handler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            AppUtils.openPDF(fileName);
                                        }
                                    });
                                }
                            });
                        }
                    });
                    holer.getView(R.id.jpg_tv).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            final String fileName = downLoadFile(info.getPdfLink());
                            MyApp.handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    Intent intent = new Intent(getActivity(), ExplainActivity.class);
                                    intent.putExtra("jpg",fileName);
                                    startActivity(intent);
                                }
                            });
                        }
                    });
                    holer.getView(R.id.iv_star).setVisibility(info.getUpload() == 1 ? View.GONE : View.VISIBLE);
                }
            };
            listView.setAdapter(adapter);
        } else {
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        ToastUtils.showToast("条目"+position+"被点击了"); //TODO 没写完
    }
}
