package com.myrrfappnew.bean;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/2/22.
 */
@Table(name = "work")
public class WorkInfo implements Serializable {
    private static final long serialVersionUID = -7541220071938409082L;
//    <urgent>U</urgent><crnNo>54663910</crnNo><worker>8</worker><IEME>865623021897593</IEME><pdfLink>EMMS_Files\Business\WORRF\TXT\20170324\54663910 - RRF.pdf</pdfLink><AppointDate>2017-03-24</AppointDate><rrfTime>16:00:00-18:00:00</rrfTime><address>C.S.D. DEPTAL Qtrs at Butterfly Valley - Block G, Flat 702</address><appstatus>1</appstatus><appstatusdate></appstatusdate><jpgLink></jpgLink></RRF>
    @Column(name = "workId", property = "NOT NUll", isId = true)
    private String workId; //对应服务器的crnNo字段,一个唯一号
    @Column(name = "date") //日期字段  例如 2017-01-01
    private String date;
    @Column(name = "urgent") //控制 H U等字段
    private String urgent;
    @Column(name = "time")  //具体时间字段 例如 13:00
    private String time;
    @Column(name = "state")
    private int state; //状态 --> 0是未到场 1是未完工 2是完工

    @Column(name = "arriveImgs")
    private String arriveImgs;  //到场拍摄的图片字段     -->没用过的字段
    @Column(name = "completeImgs")
    private String completeImgs; //完工拍的图片字段      -->没用过的字段
    @Column(name = "notCompleteImgs")
    private String notCompleteImgs; //未完工怕的图片字段  -->没用过的字段
    @Column(name = "address")
    private String address;  //地址字段
    @Column(name = "tag")
    private String tag;  //标记字段
    @Column(name = "pdfLink")
    private String pdfLink; //pdf下载链接字段
    @Column(name = "upload")
    private int upload = 1; //标记是否上传字段 -->1标识为上传过  0表示为没有上传


    @Column(name = "jpgLink")
    private String jpgLink ;//jpg下载链接字段

    @Column(name = "worker")
    private String worker; //工人号
    @Column(name = "ieme")
    private String ieme; //手机唯一号
    @Column(name = "imgs")
    private String imgs; //图片地址的集合,多个地址时用","隔开

    @Column(name = "completeDate")
    private String completeDate; //到场时间
    @Column(name = "type")
    private String type;    //类别  白头单中的类别字段
    @Column(name = "isWhiteHead")
    private int isWhiteHead = 0; //是否为白头单  0代表为白头单   1为不是白头单

    @Override
    public String toString() {
        return "WorkInfo{" +
                "workId='" + workId + '\'' +
                ", date='" + date + '\'' +
                ", urgent='" + urgent + '\'' +
                ", time='" + time + '\'' +
                ", state=" + state +
                ", arriveImgs='" + arriveImgs + '\'' +
                ", completeImgs='" + completeImgs + '\'' +
                ", notCompleteImgs='" + notCompleteImgs + '\'' +
                ", address='" + address + '\'' +
                ", tag='" + tag + '\'' +
                ", pdfLink='" + pdfLink + '\'' +
                ", upload=" + upload +
                ", worker='" + worker + '\'' +
                ", ieme='" + ieme + '\'' +
                ", imgs='" + imgs + '\'' +
                ", completeDate='" + completeDate + '\'' +
                '}';
    }
    public String getJpgLink() {
        return jpgLink;
    }

    public void setJpgLink(String jpgLink) {
        this.jpgLink = jpgLink;
    }

    public String getWorker() {
        return worker;
    }

    public void setWorker(String worker) {
        this.worker = worker;
    }

    public String getIeme() {
        return ieme;
    }

    public void setIeme(String ieme) {
        this.ieme = ieme;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public String getArriveImgs() {
        return arriveImgs;
    }

    public void setArriveImgs(String arriveImgs) {
        this.arriveImgs = arriveImgs;
    }

    public String getCompleteImgs() {
        return completeImgs;
    }

    public void setCompleteImgs(String completeImgs) {
        this.completeImgs = completeImgs;
    }

    public String getNotCompleteImgs() {
        return notCompleteImgs;
    }

    public void setNotCompleteImgs(String notCompleteImgs) {
        this.notCompleteImgs = notCompleteImgs;
    }

    public WorkInfo() {
    }

    public WorkInfo(String workId, String date) {
        this.workId = workId;
        this.date = date;
    }

    public WorkInfo(String workId, String date, String time, String address) {
        this.workId = workId;
        this.date = date;
        this.time = time;
        this.address = address;
    }

    public WorkInfo(int state, String workId, String date, String time, String address) {
        this.state = state;
        this.workId = workId;
        this.date = date;
        this.time = time;
        this.address = address;
    }

    public String getWorkId() {
        return workId;
    }

    public void setWorkId(String workId) {
        this.workId = workId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPdfLink() {
        return pdfLink;
    }

    public void setPdfLink(String pdfLink) {
        this.pdfLink = pdfLink;
    }

    public String getUrgent() {
        return urgent;
    }

    public void setUrgent(String urgent) {
        this.urgent = urgent;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public int getUpload() {
        return upload;
    }

    public void setUpload(int upload) {
        this.upload = upload;
    }

    public String getImgs() {
        return imgs;
    }

    public void setImgs(String imgs) {
        this.imgs = imgs;
    }

    public String getCompleteDate() {
        return completeDate;
    }

    public void setCompleteDate(String completeDate) {
        this.completeDate = completeDate;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getWhiteHead() {
        return isWhiteHead;
    }

    public void setWhiteHead(int whiteHead) {
        isWhiteHead = whiteHead;
    }
}
