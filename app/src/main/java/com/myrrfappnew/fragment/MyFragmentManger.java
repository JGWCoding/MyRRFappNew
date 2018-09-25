package com.myrrfappnew.fragment;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.myrrfappnew.R;
import com.myrrfappnew.activity.MainActivity;
import com.myrrfappnew.utils.Constant;

/**
 * Created by Administrator on 2017/6/14.
 */

public class MyFragmentManger {
    private static FragmentManager fm;
    //    static HashMap<Integer, Fragment> hashMap = new HashMap<>(); //存储Fragment,以便不用重复创建对象
    public static Fragment currentFragment;  //记录前面显示的Fragment,达到控制Fragment的显示和隐藏
    public static int rootInterface = -1; //记录最顶层的Fragment
    public static String id = "";

    public static void showRootFragment(MainActivity activity, int index) {
        rootInterface = index;
        showFragment(activity, index, null);
    }

    //刷新当前页面数据
    public static void fragmentRefresh() {
        if (currentFragment instanceof BaseFragment) {
            Constant.isNetWorkGetData = true;
            ((BaseFragment) currentFragment).triggerRefresh();
        }
    }

    //搜索指定数据
    public static void search(String key) {
        if (currentFragment instanceof BaseFragment) {
            ((BaseFragment) currentFragment).search(key);
        }
    }

    //显示签到页面 -->id 为 WorkInfo的id
    public static void showWorkFragment(MainActivity activity, String id) {
        showFragment(activity, 4, id);
    }

    //显示照片页面 -->id 为 WorkInfo的id
    public static void showPhotoFragment(MainActivity activity, String id) {
        showFragment(activity, 5, id);
    }
    //显示未完成选择原因页面 -->id 为 WorkInfo的id
    public static void showCauseFragment(MainActivity activity, String id) {
        showFragment(activity, 6, id);
    }
    public static void showFragment(MainActivity activity, int index, String id) {
        if (fm == null) {
            fm = activity.getSupportFragmentManager();
        }
        MainActivity.index = index;
        MyFragmentManger.id = id;
        FragmentTransaction ft = fm.beginTransaction();
        Fragment fragment = null;
        switch (index) {
            case -1:
                fragment = new WhiteHeadFragment();
                break;
            case 0:
                fragment = new NotArriveFragment();
                break;
            case 1:
                fragment = new NotFinishFragment();
                break;
            case 2:
                fragment = new FinishFragment();
                break;
            case 3:
                fragment = new WorkLogFragment();
                break;
            case 4:
                fragment = WorkFragment.getInstance(id);
                break;
            case 5:
                fragment = PhotoFragment.getInstance(id);
                break;
            case 6:
                fragment = NotFinishCauseFragment.getInstance(id);
                break;
            case 7:
                fragment = new AddWhiteHeadFragment();
                break;
            default:
                fragment = new NotArriveFragment();
        }
        ft.replace(R.id.content_layout, fragment);
        ft.commit();
        currentFragment = fragment;
    }
}
