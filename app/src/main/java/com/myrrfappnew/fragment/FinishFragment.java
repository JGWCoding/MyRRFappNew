package com.myrrfappnew.fragment;

import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.myrrfappnew.MyApp;
import com.myrrfappnew.R;
import com.myrrfappnew.activity.ExplainActivity;
import com.myrrfappnew.activity.MainActivity;
import com.myrrfappnew.adapter.MyListAdapter;
import com.myrrfappnew.bean.WorkInfo;
import com.myrrfappnew.utils.AppUtils;
import com.myrrfappnew.utils.Constant;
import com.myrrfappnew.utils.DbHelper;
import com.myrrfappnew.utils.PresentComparator;
import com.myrrfappnew.utils.ThreadUtils;

import java.util.Collections;
import java.util.List;

/**
 * Created by Administrator on 2017/6/16.
 */

public class FinishFragment extends BaseFragment implements AdapterView.OnItemClickListener {
    private ListView listView;
    private List<WorkInfo> list;
    private MyListAdapter<WorkInfo> adapter;

    @Override
    protected View getMyView() {
        View view = View.inflate(getActivity(), R.layout.fragment_finish, null);
        listView = (ListView) view.findViewById(R.id.list_view);
        listView.setOnItemClickListener(this);
        return view;
    }

    @Override
    protected void searchData(String key) {
        list = DbHelper.getInstance().searchWorkList(Constant.finish, key);
        if (list != null && list.size() > 1) {
            Collections.sort(list, new PresentComparator(imei));
        }
    }

    @Override
    protected void threadGetData() { //子线程中加载数据
        if (Constant.isNetWorkGetData) {
            queryRRFAll(Constant.finish);
        }
        list = DbHelper.getInstance().getWorkList(Constant.finish);
        if (list != null && list.size() > 1) {
            Collections.sort(list, new PresentComparator(imei));
        }
    }

    @Override
    protected void mainRefresh() { //刷新数据后的视图
        if (adapter == null) {
            //显示加载中 --->下载 完成后自动跳转到打开 -->失败提示下-->关闭的显示条
            adapter = new MyListAdapter<WorkInfo>(list) {
                @Override
                public View viewAndData(int position, View view, ViewGroup viewGroup, List<WorkInfo> list) {
                    ViewHolder holder = null;
                    if (view == null) {
                        view = View.inflate(getActivity(), R.layout.fragment_finish_item, null);
                        holder = new ViewHolder(view);
                        view.setTag(holder);
                    } else {
                        holder = (ViewHolder) view.getTag();
                    }
                    final WorkInfo info = list.get(position);
                    holder.workIdTv.setText(info.getUrgent() + " " + info.getWorkId() + "(" + info.getWorker() + ")");
                    holder.dateTv.setText(AppUtils.dateChange(info.getCompleteDate()));
                    holder.addressTv.setText(info.getAddress());
                    holder.starIv.setVisibility(info.getUpload()==1?View.GONE:View.VISIBLE);
                    holder.pdfTv.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ThreadUtils.pools.execute(new Runnable() {
                                @Override
                                public void run() {
                                    //显示加载中 --->下载 完成后自动跳转到打开 -->失败提示下-->关闭的显示条
                                    final String fileName = downLoadFile(info.getPdfLink());
                                    if (fileName != null)
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
                    holder.jpgTV.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            final String fileName = downLoadFile(info.getPdfLink());
                            if (fileName != null)
                                MyApp.handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        Intent intent = new Intent(getActivity(), ExplainActivity.class);
                                        intent.putExtra("jpg", fileName);
                                        startActivity(intent);
                                    }
                                });
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
        public ViewHolder(View view) {
            assignViews(view);
        }

        private TextView workIdTv, pdfTv, dateTv, addressTv, jpgTV;
        private ImageView starIv;

        private void assignViews(View v) {
            workIdTv = (TextView) v.findViewById(R.id.work_id_tv);
            pdfTv = (TextView) v.findViewById(R.id.pdf_tv);
            dateTv = (TextView) v.findViewById(R.id.date_tv);
            addressTv = (TextView) v.findViewById(R.id.address_tv);
            starIv = (ImageView) v.findViewById(R.id.iv_star);
            jpgTV = (TextView) v.findViewById(R.id.jpg_tv);
        }
    }
}
