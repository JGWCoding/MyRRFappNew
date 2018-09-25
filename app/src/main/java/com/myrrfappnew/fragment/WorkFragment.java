package com.myrrfappnew.fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.myrrfappnew.R;
import com.myrrfappnew.activity.MainActivity;
import com.myrrfappnew.bean.WorkInfo;
import com.myrrfappnew.bean.WorkLogBean;
import com.myrrfappnew.utils.AppUtils;
import com.myrrfappnew.utils.DbHelper;
import com.myrrfappnew.utils.FileUtils;
import com.myrrfappnew.utils.HttpManager;
import com.myrrfappnew.utils.PhotoUtils;
import com.myrrfappnew.utils.ToastUtils;

import java.io.File;

/**
 * Created by Administrator on 2017/2/21.
 */

public class WorkFragment extends Fragment implements View.OnClickListener {


    private LinearLayout layoutArrive, layoutNoComplete, layoutComplete, layoutTakePhoto, layoutGallery;
    private String work_Id;
    private TextView tvPhoto;
    private View maskArrive, maskNocomplete, maskComplete, maskTakePhoto, maskGallery;


    private String photoSavePath;
    private WorkInfo workInfo;


    private void assignViews(View v) {
        TextView count = (TextView) getActivity().findViewById(R.id.tv_count);
        count.setText("");

        layoutArrive = (LinearLayout) v.findViewById(R.id.layout_arrive);
        layoutNoComplete = (LinearLayout) v.findViewById(R.id.layout_no_complete);
        layoutComplete = (LinearLayout) v.findViewById(R.id.layout_complete);
        layoutTakePhoto = (LinearLayout) v.findViewById(R.id.layout_take_photo);
        layoutGallery = (LinearLayout) v.findViewById(R.id.layout_gallery);
        tvPhoto = (TextView) v.findViewById(R.id.tv_photo);
        layoutArrive.setOnClickListener(this);
        layoutNoComplete.setOnClickListener(this);
        layoutComplete.setOnClickListener(this);
        layoutGallery.setOnClickListener(this);
        layoutTakePhoto.setOnClickListener(this);
        maskArrive = v.findViewById(R.id.mask_arrive);
        maskNocomplete = v.findViewById(R.id.mask_no_complete);
        maskComplete = v.findViewById(R.id.mask_complete);
        maskTakePhoto = v.findViewById(R.id.mask_take_photo);
        maskGallery = v.findViewById(R.id.mask_gallery);
    }


    /**
     * 传参
     *
     * @param workId
     * @return
     */
    public static WorkFragment getInstance(String workId) {
        Bundle args = new Bundle();
        args.putString("work_id", workId);
        WorkFragment fragment = new WorkFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        work_Id = getArguments().getString("work_id");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.work_view, null);
        assignViews(view); //初始化控件
        updatePhotoSum(); //更新相片数量
        setActionStatus(); //激活可点击的对应状态
        return view;
    }

    private void updatePhotoSum() {
        workInfo = DbHelper.getInstance().getWorkById(work_Id);
        if (workInfo == null) return;
        String imgs = workInfo.getImgs();
        if (!TextUtils.isEmpty(imgs))
            tvPhoto.setText(getString(R.string.photos_sum, imgs.split(",").length));
        else
            tvPhoto.setText(getString(R.string.photos_sum, 0));
    }

    private void setActionStatus() {
        if (workInfo.getState() == 0) {
            maskNocomplete.setVisibility(View.VISIBLE);
            maskComplete.setVisibility(View.VISIBLE);
            maskTakePhoto.setVisibility(View.VISIBLE);
            maskGallery.setVisibility(View.VISIBLE);
        } else if (workInfo.getState() == 2) {
            maskArrive.setVisibility(View.VISIBLE);
            maskNocomplete.setVisibility(View.VISIBLE);
            maskComplete.setVisibility(View.VISIBLE);
        } else {
            maskNocomplete.setVisibility(View.GONE);
            maskComplete.setVisibility(View.GONE);
            maskTakePhoto.setVisibility(View.GONE);
            maskGallery.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.layout_arrive: //到场
                if (workInfo.getState() == 2) {
                    ToastUtils.showToast("個案已完工");
                } else {
                    showDialog(view.getId());
                }
                break;
            case R.id.layout_no_complete:   //未完工
                if (workInfo.getState() == 0) {
                    ToastUtils.showToast(R.string.please_present_first);
                } else if (workInfo.getState() == 1) {
                    MyFragmentManger.showCauseFragment((MainActivity) getActivity(),work_Id);
                } else {
                    ToastUtils.showToast("個案已完工");
                }
                break;
            case R.id.layout_complete:   //完工
                if (workInfo.getState() == 1) {
                    showDialog(view.getId());

                } else if (workInfo.getState() == 2) {
                    ToastUtils.showToast("個案已完工");
                } else {
                    ToastUtils.showToast(R.string.please_present_first);
                }
                break;
            case R.id.layout_take_photo:  //開始影像
                if (workInfo.getState() != 0) {
                    showDialog(view.getId());
                } else {
                    ToastUtils.showToast(R.string.please_present_first);
                }
                break;
            case R.id.layout_gallery:  //已拍照片
                if (workInfo.getState() != 0) {
                    MyFragmentManger.showPhotoFragment((MainActivity) getActivity(),work_Id);
                } else {
                    ToastUtils.showToast(R.string.please_present_first);
                }
                break;
        }

    }

    private void showDialog(final int id) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("提示");
        String message = "";
        switch (id) {
            case R.id.layout_arrive:
                message = "是否報到場";
                break;
            case R.id.layout_complete:
                message = "是否報完工";
                break;
            case R.id.layout_take_photo:
                message = "是否開始影相";
                break;
        }
        builder.setMessage(message);
        builder.setPositiveButton("確認", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                switch (id) {
                    case R.id.layout_arrive:
                        //到场
                        workInfo.setState(1);
                        setActionStatus();
                        DbHelper.getInstance().updataWork(work_Id, 1);
                        WorkLogBean bean = writeLog(workInfo.getState(), "報到場");
                        //保存日志
                        HttpManager.getInstance(getActivity()).uploadLog(bean);
                        ToastUtils.showToast("你已報到場");
                        layoutArrive.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                camera(1, work_Id);
                            }
                        }, 1000);
                        break;
                    case R.id.layout_complete:
                        workInfo.setState(2);
                        DbHelper.getInstance().updataWork(work_Id, 2, AppUtils.completeDate());//更新workinfo数据库
                        bean = writeLog(workInfo.getState(), "完工");//变更work -- log日志数据库
                        HttpManager.getInstance(getActivity()).uploadLog(bean); //上传log数据库
                        ToastUtils.showToast("你已報完工");
                        layoutArrive.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                camera(2, work_Id);
                            }
                        }, 1000);
                        maskArrive.setVisibility(View.VISIBLE);
                        maskNocomplete.setVisibility(View.VISIBLE);
                        maskComplete.setVisibility(View.VISIBLE);
                        break;
                    case R.id.layout_take_photo:
                        camera(3, work_Id);
                        break;
                }
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
        builder.create().show();
    }

    /**
     * 保存日志
     *
     * @param state
     */
    public WorkLogBean writeLog(int state, String desc, String imgPath) {
        WorkLogBean bean = new WorkLogBean();
        bean.setWorkId(work_Id);
        bean.setWorkState(state);
        if (!AppUtils.isEmpty(imgPath))
            bean.setImgUrls(imgPath);
        bean.setDesc(desc);
        bean.setCreatDate(AppUtils.getDate());
        bean.setTime(AppUtils.getTime());
        DbHelper.getInstance().updataLog(bean);
        int id = DbHelper.getInstance().actionLog(bean);
        bean.setId(id); //给上传成功时可以找到数据库的的数据
        return bean;
    }

    public WorkLogBean writeLog(int state, String desc) {
        return writeLog(state, desc, null);
    }

    /**
     * 打开相机拍照
     *
     * @param code 1到场拍照 2完工拍照 3 开始影相
     */
    private void camera(int code, String work_Id) {
        String state = Environment.getExternalStorageState();
        if (state.equals(Environment.MEDIA_MOUNTED)) {
            //构建隐式Intent
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            //调用系统相机
            intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
            String out_file_path = Environment.getExternalStorageDirectory() + "/image";
            File dir = new File(out_file_path);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            photoSavePath = FileUtils.getInstant().getimgFile() + File.separator + System.currentTimeMillis() + ".jpg";
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(photoSavePath)));
            intent.putExtra("work_Id", work_Id);
            startActivityForResult(intent, code);
        } else {
            ToastUtils.showToast("Make sure to insert the SD card");
        }
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.obj != null) {
                //添加图片地址 ---> 给workinfo 的imgs字段增加图片地址
                DbHelper.getInstance().addImgForWork(work_Id, msg.obj.toString());
                //添加日志
                String desc = "";
                switch (msg.what) {
                    case 1:
                        desc = "報到場拍照";
                        break;
                    case 2:
                        desc = "完工拍照";
                        break;
                    case 3:
                        desc = "開始影像";
                        break;
                }
                WorkLogBean bean = writeLog(workInfo.getState(), desc, msg.obj.toString());
                HttpManager.getInstance(getActivity()).uploadLog(bean);
                updatePhotoSum();
            }
        }
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != getActivity().RESULT_OK) return;
        //压缩图片
        PhotoUtils.getInstance().compressImageByQuality(getActivity(), workInfo, photoSavePath, handler, requestCode);
        //上傳圖片 ----> workInfo没有上传
    }
}
