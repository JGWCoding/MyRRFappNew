package com.myrrfappnew.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

/**
 * SharedPreferences 管理
 */
public class SPManager {
    private static final String SP_NAME = "rrf";
    private static final String WHITE_HEAD_SUM = "white_head_sum";

    private static SPManager sInstance;
    private Context mContext;
    private SharedPreferences sp;
    private Editor mEditor;

    private SPManager(Context context) {
        this.mContext = context;
    }

    public static SPManager getInstance(Context context) {
        if (null == sInstance) {
            sInstance = new SPManager(context);
        }
        return sInstance;
    }

    private SharedPreferences getSharedPreferences() {
        if (null == sp) {
            sp = mContext.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        }
        return sp;
    }

    private Editor getEditor() {
        if (null == mEditor) {
            mEditor = getSharedPreferences().edit();
        }
        return mEditor;
    }

    public boolean setWhiteHeadSum(int sum) {
        return getEditor().putInt(WHITE_HEAD_SUM, sum).commit();
    }

    public int getWhiteHeadSum() {
        return getSharedPreferences().getInt(WHITE_HEAD_SUM, 0);
    }


}