package com.myrrfappnew.utils;


import com.myrrfappnew.bean.WorkInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/2/22.
 */

public class TestData {


    public static List<WorkInfo> getTestWorkList() {
     List<WorkInfo> list = new ArrayList<>();
//        list.add(new WorkInfo(0, "E 45710080(1)", AppUtils.getDate(), "08:56",
//                "荔枝角收押所 "));
//    list.add(new WorkInfo(0, "E 45710070(2)" , AppUtils.getDate(), "08:12", "榮芳街街市 "));
//        list.add(new WorkInfo(0, "E 45710050(1)" , AppUtils.getDate(), "17:06", "青衣游泳池"));
//        list.add(new WorkInfo(0, "U 45682940(1)" , AppUtils.getDate(), "10:04", "歌和老街壁球中心 "));
//        list.add(new WorkInfo(0, "U 45682790(1)" , "25-03-2017", "09:44", "葵芳郵政局 "));
//        list.add(new WorkInfo(0, "U 45682360(1)" , "26-05-2017", "10:04", "貨櫃碼頭路18號 "));
//        list.add(new WorkInfo(0, "N 45641380(1)" , "08-04-2017", "09:44", "觀塘偉業街 "));
//        list.add(new WorkInfo(0, "N 46352390(1)" , "26-03-2017", "10:04", "夏愨道18號 "));
//        list.add(new WorkInfo(0, "N 54077740(1)" , "28-03-2017", "09:44", "樂華北邨 "));
//        list.add(new WorkInfo(0, "N 54077130(2)" , "29-03-2017", "09:44", "荃灣荃威花園 "));


//       for (int i = 0; i < 60; i++) {
//            if (i < 20) {
//                if(i<3){
//                    list.add(new WorkInfo(0, "E  00123450" + i, AppUtils.getDate(), "14:43", "沙頭角公路石涌凹段"));
//                }else if(i<6){
//                    list.add(new WorkInfo(0, "U  00123450" + i, AppUtils.getDate(), "14:43", "沙頭角公路石涌凹段"));
//                }else {
//                    list.add(new WorkInfo(0, "N  00123450" + i, AppUtils.getDate(), "14:43", "沙頭角公路石涌凹段"));
//                }
//            } else if (i < 40) {
//                list.add(new WorkInfo(1, "00123450" + i, AppUtils.getDate(), "14:43", "沙頭角公路石涌凹段"));
//            } else {
//                list.add(new WorkInfo(2, "00123450" + i, AppUtils.getDate(), "14:43", "沙頭角公路石涌凹段"));
//            }
//        }
        return list;
    }


    public static List<String> notCompateList() {
        List<String> list = new ArrayList<>();
//        維修未完成原因
//                戶主失約
//        戶主要事離開
//                要求維修項目與現場不符
//        欠工人
//                欠材料
//        要搭棚
//                高空工作台
//        維修複雜等指示
//                工人遲到
//        其他
        list.add("戶主失約");
        list.add("戶主有事要離開");
        list.add("要求維修項目與現場不符");
        list.add("欠工人");
        list.add("欠材料");
        list.add("要搭棚");
        list.add("高空工作臺");
        list.add("維修複雜等指示");
        list.add("工人遲到");
        list.add("交回區判處理");
        list.add("等候ASD指示");
        list.add("其他");
        return list;
    }
}
