package com.myrrfappnew.utils;


import com.myrrfappnew.bean.WorkInfo;

import java.util.Comparator;

/**
 * author by john
 */

public class ComparatorForWhiteHead implements Comparator<WorkInfo> {

    @Override
    public int compare(WorkInfo workInfo, WorkInfo t1) {
        int date = AppUtils.dateEquals(workInfo.getDate(), t1.getDate());
        if (date > 0) {
            return -1;
        } else if (date == 0) {
            return 0;
        } else {
            return 1;
        }
    }

}
