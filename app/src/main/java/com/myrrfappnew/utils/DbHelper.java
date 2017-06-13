package com.myrrfappnew.utils;

import android.content.Context;
import android.database.Cursor;
import android.text.TextUtils;
import com.myrrfappnew.bean.WorkInfo;
import com.myrrfappnew.bean.WorkLogBean;

import org.xutils.DbManager;
import org.xutils.DbManager.DaoConfig;
import org.xutils.common.util.KeyValue;
import org.xutils.db.sqlite.WhereBuilder;
import org.xutils.ex.DbException;
import org.xutils.x;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.myrrfappnew.utils.FileUtils.context;

/**
 * 数据库工具类
 */
public class DbHelper {
    private static DbHelper instance;
    /**
     * 数据库对象
     */
    private DaoConfig daoConfig;

    /**
     * 数据库管理工具
     */
    private DbManager db;
    private Runnable runnable;

    /**
     * 初始化
     */
    private DbHelper() {

        daoConfig = getDaoConfig(context);
        db = x.getDb(daoConfig);
    }

    /**
     * 构建数据库
     */
    private DaoConfig getDaoConfig(Context context) {
        if (daoConfig == null) {
            daoConfig = new DaoConfig().setAllowTransaction(true)// 设置允许开启事务
                    .setDbName("rrf.db")// 创建数据库的名称
                    // 不设置dbDir时, 默认存储在app的私有目录.
                    .setDbDir(FileUtils.getInstant().getDBFile()) // "sdcard"的写法并非最佳实践,
                    // 这里为了简单, 先这样写了.
                    .setDbVersion(2)// 数据库版本号
                    .setDbOpenListener(new DbManager.DbOpenListener() {
                        @Override
                        public void onDbOpened(DbManager db) {
                            // 开启WAL, 对写入加速提升巨大
                            db.getDatabase().enableWriteAheadLogging();
                        }
                    }).setDbUpgradeListener(new DbManager.DbUpgradeListener() {
                        @Override
                        public void onUpgrade(DbManager db, int oldVersion, int newVersion) {
                            switch (oldVersion) {
                                case 1:
                                    switch (newVersion) {
                                        case 2:
                                            try {
                                                db.addColumn(WorkInfo.class, "type");
                                                db.addColumn(WorkInfo.class, "isWhiteHead");
                                            } catch (DbException e) {
                                                e.printStackTrace();
                                            }
                                            break;
                                    }
                                    break;
                            }
                            // 数据库升级时的操作
                            // TODO: ...
                            // db.addColumn(...);
                            // db.dropTable(...);
                            // ...
                            // or
                            // db.dropDb();
                        }
                    });
        }
        return daoConfig;
    }
    public void deleteDB() throws Exception {
        db.dropDb();
    }
    /**
     * 单例模式创建数据库工具类对象
     */
    public static DbHelper getInstance() {
        if (instance == null) {
            instance = new DbHelper();
        }
        return instance;
   }
    public void deleteWorkInfo(WorkInfo workInfo) {
        try {
            if (workInfo != null)
                db.delete(workInfo);
        } catch (DbException e) {
            e.printStackTrace();
        }
    }
    public void deleteList(List<WorkInfo> list) {
        try {
            if (list != null)
                db.delete(list);
        } catch (DbException e) {
            e.printStackTrace();
        }
    }
    /**
     * 查询列表中有多少个(指定状态和含有某个字符串)
     * @param state
     * @return
     */
    public String calToatal(int state) {
        try {
           final long H= db.selector(WorkInfo.class).where("urgent", "=","H").and("state", "=", state).count();
            final long U= db.selector(WorkInfo.class).where("urgent", "=","U").and("state", "=", state).count();
            final long E= db.selector(WorkInfo.class).where("urgent", "=","E").and("state", "=", state).count();
            final long N= db.selector(WorkInfo.class).where("urgent", "=","N").and("state", "=", state).count();
            return "H:"+H+"      E:"+E+"      U:"+U+"      N:"+N +"　　Total:"+(H+U+E+N); //Ｈ:3     Ｕ：0     Ｅ：2     Ｎ：3
            //Ｈ：3     Ｅ：0     Ｕ：2     Ｎ：3　　Total: 8
        } catch (DbException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 添加工作集合
     *
     * @param list
     */
    public void saveWorkList(List<WorkInfo> list) {
        try {
            if (list != null)
                db.saveOrUpdate(list);
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    /**
     * 根据任务ID获取信息
     *
     * @param workId
     * @return
     */
    public WorkInfo getWorkById(String workId) {
        try {
            return db.findById(WorkInfo.class, workId);
        } catch (DbException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 根据任务ID获取信息
     *
     * @param workId
     * @return
     */
    public WorkInfo updateWorkById(String workId) {
        try {
            return db.findById(WorkInfo.class, workId);
        } catch (DbException e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 根據關鍵字查詢任務列表
     *
     * @param state 任务状态 1未到场 2未完工 3.已完工
     * @return
     */
    public List<WorkInfo> searchWorkList(int state, String key) {
        LogUtil.e("----state = " + state + "---key = " + key);
        try {
            WhereBuilder whereBuilder = WhereBuilder.b();
            whereBuilder.and("isWhiteHead", "=", 0).or("isWhiteHead", "=", null);
            return db.selector(WorkInfo.class).where("workId", "LIKE", "%" + key + "%").and("state", "=", state).findAll();
        } catch (DbException e) {
            e.printStackTrace();
        }
        return null;
    }
    public List<WorkInfo> getNotWhiteHead() {
        try {
            WhereBuilder whereBuilder = WhereBuilder.b();
            whereBuilder.and("isWhiteHead", "=", 0).or("isWhiteHead", "=", null);
            List<WorkInfo> workInfos = db.selector(WorkInfo.class).where(whereBuilder).findAll();
            if (workInfos != null) {
                for (int i = 0; i < workInfos.size(); i++) {
                    if (getNotUploadLog(workInfos.get(i).getWorkId()) > 0) {
                        workInfos.get(i).setUpload(0); //设置未上传
                    }
                }
            }
            return workInfos;
        } catch (DbException e) {
            e.printStackTrace();
        }
        return null;
    }
    /**
     * 获取任务列表
     *
     * @param state 任务状态 1未到场 2未完工 3.已完工
     * @return
     */
    public List<WorkInfo> getWorkList(int state) {
        try {
            WhereBuilder whereBuilder = WhereBuilder.b();
            whereBuilder.and("isWhiteHead", "=", 0).or("isWhiteHead", "=", null);
            List<WorkInfo> workInfos = db.selector(WorkInfo.class).where("state", "=", state).and(whereBuilder).findAll();
            if (workInfos != null) {
                for (int i = 0; i < workInfos.size(); i++) {
                    if (getNotUploadLog(workInfos.get(i).getWorkId()) > 0) {
                        workInfos.get(i).setUpload(0); //设置未上传
                    }
                }
            }
            return workInfos;
        } catch (DbException e) {
            e.printStackTrace();
        }
        return null;
    }
    public List<WorkInfo> getWorkListUpload(int state) {
        try {
            WhereBuilder whereBuilder = WhereBuilder.b();
            whereBuilder.and("isWhiteHead", "=", 0).or("isWhiteHead", "=", null);
            List<WorkInfo> workInfos = db.selector(WorkInfo.class).where("state", "=", state).and(whereBuilder).findAll();
            if (workInfos != null) {
                for (int i = 0; i < workInfos.size(); i++) {
                    if (workInfos.get(i).getUpload()==0) {//如果没有上传的就删除掉这条数据
                        workInfos.remove(workInfos.get(i));
                        i--;
                    }
                }
            }
            return workInfos;
        } catch (DbException e) {
            e.printStackTrace();
        }
        return null;
    }
    public List<String> getWorkListUnuploadID(int state) {
            ArrayList<String> list = new ArrayList<>();
        try {
            WhereBuilder whereBuilder = WhereBuilder.b();
            whereBuilder.and("isWhiteHead", "=", 0).or("isWhiteHead", "=", null);
            List<WorkInfo> workInfos = db.selector(WorkInfo.class).where("state", "=", state).and(whereBuilder).and("upload","=",0).findAll();
            if (workInfos != null) {
                for (int i = 0; i < workInfos.size(); i++) {
                        list.add(workInfos.get(i).getWorkId());
                }
            }
        } catch (DbException e) {
            e.printStackTrace();
        }
        return list;
    }
    /**
     * 获取所有任务信息
     *
     * @return
     */
    public List<WorkInfo> getAllWorkList() {
        try {
            return db.findAll(WorkInfo.class);
        } catch (DbException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 修改任务
     *
     * @param info
     */
    public void updataWork(WorkInfo info) {
        try {
            db.saveOrUpdate(info);
        } catch (DbException e) {
            e.printStackTrace();
        }
    }


    public void insertNetWorkData(WorkInfo info) { //插入一条数据
        try {
            WorkInfo dbInfo = getWorkById(info.getWorkId());
            if (dbInfo == null) {
                db.save(info);
            } else {
                if (dbInfo.getWhiteHead() != 1) {
                    dbInfo.setWhiteHead(0);
                }
//                info.setCompleteDate(dbInfo.getCompleteDate());
                info.setImgs(dbInfo.getImgs());
//                info.setState(dbInfo.getState());
                db.saveOrUpdate(info);
            }
        } catch (DbException e) {
            e.printStackTrace();
        }


    }


    /**
     * 修改任务状态
     *
     * @param workId
     * @param state
     */
    public void updataWork(String workId, int state) {
        try {
            db.update(WorkInfo.class, WhereBuilder.b("workId", "=", workId), new KeyValue("state", state));
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    /**
     * 修改任务状态
     *
     * @param workId
     * @param state
     */
    public void updataWork(String workId, int state, String completeDate) {
        try {
            db.update(WorkInfo.class, WhereBuilder.b("workId", "=", workId), new KeyValue("state", state), new KeyValue("completeDate", completeDate));
        } catch (DbException e) {
            e.printStackTrace();
        }
    }


    /**
     * 修改任务影相信息
     *
     * @param workId Id
     * @param imgs
     */
    public void updateWorkImgs(String workId, String imgs) {
        try {
            db.update(WorkInfo.class, WhereBuilder.b("workId", "=", workId), new KeyValue("imgs", imgs));
            LogUtil.e("修改成功");
        } catch (DbException e) {
            e.printStackTrace();
        }

    }

    /**
     * 添加单个影相信息
     *
     * @param workId   id;
     * @param imgsPath 图片路径
     */
    public void addImgForWork(String workId, String imgsPath) {
        if (TextUtils.isEmpty(imgsPath)) return;

        WorkInfo info = getWorkById(workId);
        String imgs = info.getImgs();
        if (TextUtils.isEmpty(imgs))
            imgs = imgsPath;
        else
            imgs = imgs + "," + imgsPath;
        updateWorkImgs(workId, imgs);
    }

    /**
     * 删除多张影相信息
     *
     * @param workId
     * @param imgPath
     */
    public void deleterImgForWork(String workId, List imgPath) {
        if (imgPath == null || imgPath.size() < 1) return;
        WorkInfo info = getWorkById(workId);
        String imgs = info.getImgs();
//        if (sort == 1) imgs = info.getArriveImgs();
//        if (sort == 2) imgs = info.getCompleteImgs();
//        if (sort == 3) imgs = info.getNotCompleteImgs();

        if (!TextUtils.isEmpty(imgs)) {
            String[] imgss = imgs.split(",");//分割开
            List<String> list = new ArrayList<>(Arrays.asList(imgss));//转为集合
            //对比删除
            for (int j = 0; j < imgPath.size(); j++) {
                for (int i = 0; i < list.size(); i++) {
                    if (list.get(i).equals(imgPath.get(j))) {
                        LogUtil.e(list.get(i) + "======" + imgPath.get(j));
                        list.remove(i);//删除
                        LogUtil.e("刪除");
                        break;
                    }
                }
            }

            //删除操作结束后，格式化为String
            String ss = null;
            for (int i = 0; i < list.size(); i++) {
                if (i == 0)
                    ss = list.get(i);
                else
                    ss = ss + "," + list.get(i);
            }
            //最后保存
            LogUtil.e("開始保存" + ss);
            updateWorkImgs(workId, ss);
        }
    }
/*

 插入日記，并返回當前插入的
 */

    public int actionLog(WorkLogBean row) {
        try {
            if (db.saveBindingId(row)) {

                Cursor c = db.execQuery("select max(id) from log ");

                c.moveToFirst();
                LogUtil.i("-----4444444444444444444----返回ID為:" + c.getInt(0));
                return c.getInt(0);
            }


        } catch (DbException e) {
            e.printStackTrace();
        }
        return 0;
    }
    /*

    插入日記，并返回當前插入的
    */

    public void actionUpdataStatus(int _id) {
        try {
            db.execNonQuery("update log set upload=1 where id=" + _id);
            Cursor c = db.execQuery("select upload from log where id=" + _id);
            c.moveToFirst();
            LogUtil.i("-----4444444444444444444----update狀態:" + c.getInt(0));


        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /*
     獲得當前日志
      */
    public WorkLogBean workLogRow(int _id) {
        try {
            return db.findById(WorkLogBean.class, _id);
        } catch (DbException e) {
            e.printStackTrace();
        }
        return null;
        // return WorkLogBean.class;

    }


    /**
     * 添加日志
     *
     * @param bean
     */
    public void addLog(WorkLogBean bean) {
        try {
            db.saveBindingId(bean);


        } catch (DbException e) {
            e.printStackTrace();
        }
    }


    public void addLog(List<WorkLogBean> list) {
        try {
            db.saveBindingId(list);
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    /**
     * 上传日志成功后修改日志为已上传标识
     *
     * @param bean
     */
    public void updataLog(WorkLogBean bean) {
        try {
            db.saveOrUpdate(bean);
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    /**
     * 上传日志成功后修改日志为已上传标识
     *
     * @param list
     */
    public void updataLog(List<WorkLogBean> list) {
        try {
            db.saveOrUpdate(list);
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取日志列表
     *
     * @return
     */
    public List<WorkLogBean> getLog() {
        try {
            return db.selector(WorkLogBean.class).orderBy("id", true).findAll();
        } catch (DbException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 根據關鍵字获取日志列表
     *
     * @return
     */
    public List<WorkLogBean> getLogWithKey(String key) {
        try {
            return db.selector(WorkLogBean.class).where("workId", "LIKE", "%" + key + "%").orderBy("id", true).findAll();
        } catch (DbException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取未上传日志集合
     *
     * @return
     */
    public List<WorkLogBean> getNotUploadLog() {
        try {
            return db.selector(WorkLogBean.class).where("upload", "=", 0).findAll();
        } catch (DbException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取未上传日志
     *
     * @return
     */
    public long getNotUploadLog(String workId) {
        try {
            return db.selector(WorkLogBean.class).where("upload", "=", 0).and("workId", "=", workId).count();
        } catch (DbException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 保存日志
     *
     * @param state
     */


    public static WorkLogBean writeLog(String workId, int state, String imgPath) {
        WorkLogBean bean = new WorkLogBean();
        bean.setWorkId(workId);
        bean.setWorkState(state);
        String desc = "";
        switch (state) {
            case 1:
                desc = "報到場";
                break;
            case 2:
                desc = "報到場拍照";
                bean.setImgUrls(imgPath);
                break;
            case 3:
                desc = "未完工";
                break;
            case 4:
                desc = "完工";
                break;
            case 5:
                desc = "完工拍照";
                bean.setImgUrls(imgPath);
                break;
            case 6:
                desc = "開始影像";
                bean.setImgUrls(imgPath);
                break;
        }
        bean.setDesc(desc);
        bean.setCreatDate(AppUtils.getDate());
        bean.setTime(AppUtils.getTime());
        int id = DbHelper.getInstance().actionLog(bean);
        bean.setId(id);
        return bean;
    }

    /**
     * 获取白頭單數量
     *
     * @return
     */
    public int getWhiteHeadCount() {
        List<WorkInfo> whiteHeads = getAllWhiteHead();
        if (whiteHeads == null) return 0;
        else
            return whiteHeads.size();
    }

    /**
     * 获取白頭單記錄
     *
     * @return
     */
    public List<WorkInfo> getAllWhiteHead() {
        try {
            return db.selector(WorkInfo.class).where("isWhiteHead", "=", 1).orderBy("workId",true).findAll();
        } catch (DbException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取未上传白頭單記錄
     *
     * @return
     */
    public List<WorkInfo> getAllUnUploadWhiteHead() {
        try {
            return db.selector(WorkInfo.class).where("isWhiteHead", "=", 1).and("upload","=",0).findAll();
        } catch (DbException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取白頭單記錄
     *
     * @return
     */
    public List<WorkInfo> getAllWhiteHeadWithKey(String key) {
        try {
            return db.selector(WorkInfo.class).where("isWhiteHead", "=", 1).and("workId", "LIKE", "%" + key + "%").orderBy("workId",true).findAll();
        } catch (DbException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 刪除白頭單
     *
     * @return
     */
    public void deleteWhiteHead(WorkInfo info) {
        try {
            db.delete(info);
        } catch (DbException e) {
            e.printStackTrace();
        }
    }


}
