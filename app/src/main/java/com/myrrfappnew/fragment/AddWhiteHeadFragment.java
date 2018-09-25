package com.myrrfappnew.fragment;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.EditText;
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
import com.myrrfappnew.utils.SPManager;
import com.myrrfappnew.utils.ToastUtils;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;


public class AddWhiteHeadFragment extends Fragment implements View.OnClickListener {

    private TextView tvId, tvAddress, tvType, tvDate, tvSubmit, tvTitle;
    private String[] address, type;
    private Dialog dialog;
    private ListView listView;
    private MyListAdapter<String> adapter;
    private String imei;
    private int year, month, day, typeIndex;
    private DatePickerDialog pickerDialog;
    private EditText remark;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_white_head, null);
        assignViews(view);
        return view;

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        imei = AppUtils.getDeviceID(getActivity());
        Calendar ca = Calendar.getInstance();
        year = ca.get(Calendar.YEAR);
        month = ca.get(Calendar.MONTH);
        day = ca.get(Calendar.DAY_OF_MONTH);
    }

    private int sum;

    private void assignViews(View v) {
        tvId = (TextView) v.findViewById(R.id.tv_id);
        sum = SPManager.getInstance(getActivity()).getWhiteHeadSum();
        sum++;
        String id = AppUtils.getDateWithWhiteHead();
        tvId.setText(id);
        tvId.setHint(imei + "-" + id);
        tvAddress = (TextView) v.findViewById(R.id.tv_address);
        tvType = (TextView) v.findViewById(R.id.tv_type);
        tvDate = (TextView) v.findViewById(R.id.tv_date);
        tvDate.setText(AppUtils.getDate());
        tvSubmit = (TextView) v.findViewById(R.id.tv_submit);
        tvSubmit.setOnClickListener(this);
        tvAddress.setOnClickListener(this);
        tvType.setOnClickListener(this);
        tvDate.setOnClickListener(this);
        address = getResources().getStringArray(R.array.address);
        type = getResources().getStringArray(R.array.type);

        remark = (EditText) v.findViewById(R.id.et_remark);
        remark.setHintTextColor(Color.parseColor("#016938"));

    }

    private void showDialog(int resId, String[] items) {
        if (dialog == null) {
            dialog = new Dialog(getActivity(), R.style.ActionSheetDialogStyle);
            View inflate = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_white_head, null);
            dialog.setContentView(inflate);

            tvTitle = (TextView) inflate.findViewById(R.id.tv_title);
            listView = (ListView) inflate.findViewById(R.id.listview);
        }
        tvTitle.setText(resId);
        adapter = new MyListAdapter<String>(Arrays.asList(items)) {
            @Override
            public View viewAndData(int position, View view, ViewGroup viewGroup, List<String> list) {
                ViewHolder holder;
                if (view == null) {
                    view = LayoutInflater.from(getActivity()).inflate(R.layout.item_add_white_head_dialog, null);
                    holder = new ViewHolder();
                    holder.tvItem = (TextView) view.findViewById(R.id.tv_item);
                    view.setTag(holder);
                } else {
                    holder = (ViewHolder) view.getTag();
                }
                if (tvType.isSelected()) {
                    String type = list.get(position).split(",")[0];
                    holder.tvItem.setText(type);
                } else {
                    holder.tvItem.setText(list.get(position));
                }
                return view;
            }
        };
        listView.setAdapter(adapter);
        Window dialogWindow = dialog.getWindow();
        dialogWindow.setGravity(Gravity.BOTTOM);
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.height = AppUtils.dp2px(getActivity(), 200);
        lp.width = getResources().getDisplayMetrics().widthPixels;
        dialogWindow.setAttributes(lp);

        dialog.show();//显示对话框
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (tvAddress.isSelected()) {
                    tvAddress.setText(address[i]);
                } else {
                    tvType.setText(type[i].split(",")[0]);
                    tvType.setTag(type[i]);
                    typeIndex = i;
                }
                dialog.dismiss();
            }
        });

    }

    private void showDate() {
        if (pickerDialog == null)
            pickerDialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                    DecimalFormat df = new DecimalFormat("00");
                    tvDate.setText(df.format(day) + "-" + df.format(month + 1) + "-" + year);
                }
            }, year, month, day);
        pickerDialog.show();

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_address:
                tvAddress.setSelected(true);
                tvType.setSelected(false);
                showDialog(R.string.address, address);
                break;
            case R.id.tv_type:
                tvType.setSelected(true);
                tvAddress.setSelected(false);
                showDialog(R.string.type, type);
                break;
            case R.id.tv_date:
                showDate();
                break;
            case R.id.tv_submit:
                if (tvAddress.getText().toString().trim().length() == 0) {
                    ToastUtils.showToast(R.string.please_choose_address);
                } else if (tvType.getText().toString().trim().length() == 0) {
                    ToastUtils.showToast(R.string.please_choose_type);
                } else {
                    WorkInfo info = new WorkInfo();
                    info.setWorkId(tvId.getHint().toString());
                    info.setState(0);
                    info.setUpload(0);
                    info.setAddress(tvAddress.getText().toString());
                    info.setType(tvType.getTag().toString());
                    info.setDate(tvDate.getText().toString());
                    info.setWhiteHead(1); //设置为白头单
                    info.setNotCompleteImgs(remark.getText().toString()==null?"":remark.getText().toString()); //设置一个备注信息
                    DbHelper.getInstance().updataWork(info); //更新白头单
                    HttpManager.getInstance(getActivity()).addWhiteHead(info);//上传workinfo
                    SPManager.getInstance(getActivity()).setWhiteHeadSum(sum);//更新白头单的数量

                    WorkLogBean bean = new WorkLogBean();
                    bean.setWorkId(info.getWorkId());
                    bean.setWorkState(info.getState());
                    bean.setDesc("新增白頭單");
                    bean.setCreatDate(AppUtils.getDate());
                    bean.setTime(AppUtils.getTime());
                    DbHelper.getInstance().actionLog(bean); //插入本地数据库
                    HttpManager.getInstance(getActivity()).uploadLog(bean); //上传worklog日志
                    MyFragmentManger.showRootFragment((MainActivity) getActivity(),-1); //跳转
                }
                break;
        }
    }


    class ViewHolder {
        private TextView tvItem;
    }

}
