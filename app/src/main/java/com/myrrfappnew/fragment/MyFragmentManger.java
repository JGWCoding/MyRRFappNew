package com.myrrfappnew.fragment;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.myrrfappnew.R;
import com.myrrfappnew.activity.MainActivity;
import com.myrrfappnew.utils.Constant;

import java.util.HashMap;

import static com.tencent.bugly.crashreport.crash.c.i;

/**
 * Created by Administrator on 2017/6/14.
 */

public class MyFragmentManger {

    private static FragmentManager fm;
    static HashMap<Integer, Fragment> hashMap = new HashMap<>(); //存储Fragment,以便不用重复创建对象
    static Fragment currentFragment; //记录前面显示的Fragment,达到控制Fragment的显示

    public static void showFragment(MainActivity activity, int index) {
        if (fm == null) {
            fm = activity.getSupportFragmentManager();
        }
        FragmentTransaction ft = fm.beginTransaction();
        Fragment fragment = hashMap.get(i);
        if (fragment == null) {
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
//                case 4:
//                    break;
                default:
                    fragment = new NotArriveFragment();
            }
            ft.add(R.id.content_layout, fragment);
        } else {
            ft.show(fragment);
        }
        if (currentFragment != null && currentFragment!=fragment) {
            ft.hide(currentFragment);
        }
        ft.commit();
        currentFragment = fragment;
    }

    public static void fragmentRefresh() {
        if (currentFragment instanceof BaseFragment) {
            Constant.isNetWorkGetData = true;
            ((BaseFragment) currentFragment).triggerRefresh();
        }
    }

    public static void search(String key) {
        if (currentFragment instanceof BaseFragment) {
            ((BaseFragment) currentFragment).search(key);
        }
    }
}
