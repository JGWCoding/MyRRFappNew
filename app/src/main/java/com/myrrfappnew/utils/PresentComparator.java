package com.myrrfappnew.utils;

import com.myrrfappnew.bean.WorkInfo;
import java.util.Comparator;
/**
 * author by john
 */

public class PresentComparator implements Comparator<WorkInfo> {
    private String myIeme;

    public PresentComparator(String myIeme) {
        this.myIeme = myIeme;
    }

    @Override
    public int compare(WorkInfo workInfo, WorkInfo t1) {
        String urgent1 = workInfo.getUrgent();
        String urgent2 = t1.getUrgent();
        if (getValue(urgent1) == getValue(urgent2)){
            if (AppUtils.getExpiredDay(workInfo.getDate()) == AppUtils.getExpiredDay(t1.getDate())){
                if(AppUtils.isEmpty(workInfo.getIeme()))
                    return 0;
                if (workInfo.getIeme().equals(myIeme)) {
                    return -1;
                } else {
                    return 0;
                }
            }else{
                return AppUtils.getExpiredDay(t1.getDate()) - AppUtils.getExpiredDay(workInfo.getDate());
            }
        }else{
            return getValue(urgent2) - getValue(urgent1);
        }
    }


    private int getValue(String urgent) {
        if(AppUtils.isEmpty(urgent))
            return 0;
        if (urgent.equals("H"))
            return 4;
        if (urgent.equals("E"))
            return 3;
        if (urgent.equals("U"))
            return 2;
        if (urgent.equals("N"))
            return 1;
        return 0;
    }
}
