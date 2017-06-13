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
    @Column(name = "date")
    private String date;
    @Column(name = "urgent") //控制 H U等字段
    private String urgent;
    @Column(name = "time")
    private String time;
    @Column(name = "state")
    private int state; //状态

    @Column(name = "arriveImgs")
    private String arriveImgs;
    @Column(name = "completeImgs")
    private String completeImgs;
    @Column(name = "notCompleteImgs")
    private String notCompleteImgs;
    @Column(name = "address")
    private String address;
    @Column(name = "tag")
    private String tag;
    @Column(name = "pdfLink")
    private String pdfLink;
    @Column(name = "upload")
    private int upload = 1;


    @Column(name = "jpgLink")
    private String jpgLink ;

    @Column(name = "worker")
    private String worker;
    @Column(name = "ieme")
    private String ieme;
    @Column(name = "imgs")
    private String imgs;

    @Column(name = "completeDate")
    private String completeDate;
    @Column(name = "type")
    private String type;
    @Column(name = "isWhiteHead")
    private int isWhiteHead = 0;

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
