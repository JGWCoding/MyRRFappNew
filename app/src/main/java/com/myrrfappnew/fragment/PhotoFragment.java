package com.myrrfappnew.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

import com.myrrfappnew.R;
import com.myrrfappnew.activity.PhotoActivity;
import com.myrrfappnew.adapter.ImgGridAdapter;
import com.myrrfappnew.bean.WorkInfo;
import com.myrrfappnew.bean.WorkLogBean;
import com.myrrfappnew.utils.AppUtils;
import com.myrrfappnew.utils.DbHelper;
import com.myrrfappnew.utils.HttpManager;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Administrator on 2017/2/23.
 */
public class PhotoFragment extends Fragment implements ImgGridAdapter.OnImgRemove {

    private GridView arriveGrid;
    private ImgGridAdapter arriveAdapter;
    private TextView tvTips;

    private String workId;//任務id
    private WorkInfo info;//任務對象
    private String[] imgs = null;
    ArrayList<String> list = new ArrayList<String>();
    public static PhotoFragment getInstance(String workId) {
        Bundle bundle = new Bundle();
        bundle.putString("work_id", workId);
        PhotoFragment fragment = new PhotoFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_photo, null);
        assignViews(view);
        return view;

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        workId = getArguments().getString("work_id");
        info = DbHelper.getInstance().getWorkById(workId);
    }

    private void assignViews(View v) {
        tvTips = (TextView) v.findViewById(R.id.tv_tips);
        arriveGrid = (GridView) v.findViewById(R.id.arrive_grid);
        if (!AppUtils.isEmpty(info.getImgs())) {
            imgs = info.getImgs().split(",");
            list = new ArrayList<>(Arrays.asList(imgs));
        } else {
            imgs = new String[0];
        }
        tvTips.setText(getString(R.string.photos_taken, list.size()));
        arriveAdapter = new ImgGridAdapter(getActivity(), list, 1); //这个state 1 没有用处
        arriveAdapter.setOnImgRemove(this);
        arriveGrid.setAdapter(arriveAdapter);
        arriveGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String path = imgs[i];
                Intent intent = new Intent(getActivity(), PhotoActivity.class);
                intent.putExtra("path", path);
                intent.putExtra("paths", list);
                startActivity(intent);
            }
        });
    }

    /**
     * 保存日志
     *
     * @param state 日志分类
     */
    public void writeLog(int state) {
        final WorkLogBean bean = new WorkLogBean();
        bean.setWorkId(workId);
        bean.setWorkState(state);
        bean.setCreatDate(AppUtils.getDate());
        bean.setDesc("成功刪除照片");
        bean.setTime(AppUtils.getTime());
        bean.setUpload(0);
        int id = DbHelper.getInstance().actionLog(bean);
        bean.setId(id);
        //这个不是上传数据库的bean,是上传内存里的bean -->设置id是为了上传之后可以通过id修改数据库的数据
        HttpManager.getInstance(getActivity()).uploadLog(bean);
    }

    /**
     * 删除照片
     *
     * @param imgPath 地址
     */
    public void deleterImgFile(String imgPath) {
        File file = new File(imgPath);
        if (file.exists()) {
            file.delete();
        }
    }

    @Override
    public void onRemove(String imgPath, int state) { //删除我的数据库里的相册数据
        deleterImgFile(imgPath); //删除照片原件
        if (info.getImgs().contains(imgPath + ",")) {
            info.setImgs(info.getImgs().replace(imgPath + ",", ""));
        } else if (info.getImgs().contains("," + imgPath)) {
            info.setImgs(info.getImgs().replace("," + imgPath, ""));
        } else {
            info.setImgs("");
        }
        DbHelper.getInstance().updataWork(info);//更新我数据库的相册数
        writeLog(info.getState()); //写日志
        if (!AppUtils.isEmpty(info.getImgs())) {
            imgs = info.getImgs().split(",");
            list = new ArrayList<>(Arrays.asList(imgs));
        } else {
            imgs = new String[0];
            list = new ArrayList<>();
        }
        tvTips.setText(getString(R.string.photos_taken, list.size())); //刷新照片的数量
    }
}
