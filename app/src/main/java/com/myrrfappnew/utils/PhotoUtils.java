package com.myrrfappnew.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.widget.PopupWindow;

import com.myrrfappnew.R;
import com.myrrfappnew.bean.WorkInfo;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import static com.myrrfappnew.utils.AppUtils.dp2px;


/**
 * Created by Administrator on 2016/9/28.
 */
public class PhotoUtils {


    private static final int CAMERA_CODE = 1;
    private static final int GALLERY_CODE = 2;
    private String photoSavePath = null;
    private PopupWindow popu;


    private static PhotoUtils instance;

    public static PhotoUtils getInstance() {
        if (instance == null) instance = new PhotoUtils();

        return instance;
    }

    private PhotoUtils() {

    }


    /**
     * 压缩图片尺寸
     *
     * @param srcPath
     * @return
     */
    private Bitmap getimage(String srcPath) {
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        //开始读入图片，此时把options.inJustDecodeBounds 设回true了
        newOpts.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeFile(srcPath, newOpts);//此时返回bm为空

        newOpts.inJustDecodeBounds = false;
        int w = newOpts.outWidth;
        int h = newOpts.outHeight;
        //现在主流手机比较多是800*480分辨率，所以高和宽我们设置为
        float hh = 1280;//这里设置高度
        float ww = 720;//这里设置宽度
        //缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
        int be = 1;//be=1表示不缩放
        if (w > h && w > ww) {//如果宽度大的话根据宽度固定大小缩放
            be = (int) (newOpts.outWidth / ww);
        } else if (w < h && h > hh) {//如果高度高的话根据宽度固定大小缩放
            be = (int) (newOpts.outHeight / hh);
        }
        if (be <= 0)
            be = 1;

        if (h % 3 > 0 && be > 2 && be < 5) {
            if (h % 4 == 0) {
                be = 4;
            }
        } else {

        }


        newOpts.inSampleSize = be;//设置缩放比例
        //重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
        bitmap = BitmapFactory.decodeFile(srcPath, newOpts);
        return bitmap;//压缩好比例大小后再进行质量压缩
    }


    /**
     * 多线程压缩图片的质量
     *
     * @param imgPath 图片的保存路径
     * @author JPH
     * @date 2014-12-5下午11:30:43
     */
    public void compressImageByQuality(final Context context, final WorkInfo info, final String imgPath, final Handler handler, final int requestCode) {
        if (TextUtils.isEmpty(imgPath)) return;
        final Bitmap bt;
        if (info.getWhiteHead() == 0) {
            bt = drawTextToRightTop(context, getimage(imgPath), info.getWorkId(), 10, 10);
        } else {
            bt = getimage(imgPath);
        }
        new Thread(new Runnable() {//开启多线程进行压缩处理
            @Override
            public void run() {
                // TODO Auto-generated method stub
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                int options = 65;
                bt.compress(Bitmap.CompressFormat.JPEG, options, baos);//质量压缩方法，把压缩后的数据存放到baos中 (100表示不压缩，0表示压缩到最小)
                try {

                    File file = new File(imgPath);
                    if (!file.exists()) file.createNewFile();
                    FileOutputStream fos = new FileOutputStream(file);//将压缩后的图片保存的本地上指定路径中
                    FileInputStream inputFile = new FileInputStream(file);


                    fos.write(baos.toByteArray());
                    fos.flush();
                    fos.close();
                    bt.recycle();
                    Message msg = new Message();

                    msg.what = requestCode;
                    msg.obj = imgPath;
//
//                    String base64 = Base64.encodeToString(baos.toByteArray(), Base64.NO_WRAP);
                    Bundle bundle = new Bundle();
//                    bundle.putString("base64", base64);

                    bundle.putInt("code", requestCode);
                    msg.setData(bundle);


                    if (handler != null) {
                        handler.sendMessage(msg);
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }

    /**
     * 多线程压缩图片的质量
     *
     * @param imgPath 图片的保存路径
     * @author JPH
     * @date 2014-12-5下午11:30:43
     */
    public void drawTextToRightTop(final Context context, final String imgPath, final String id) {
        LogUtil.i("----------drawTextToRightTop = " + imgPath);
        if (TextUtils.isEmpty(imgPath)) return;
        new Thread(new Runnable() {//开启多线程进行压缩处理
            @Override
            public void run() {
                try {
                    Bitmap bt = getimage(imgPath);
                    bt = drawTextToRightTop(context, bt, id, 10, 10);
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    int options = 100;
                    bt.compress(Bitmap.CompressFormat.JPEG, options, baos);
                    File file = new File(imgPath);
                    if (file.exists()) file.delete();
                    FileOutputStream fos = new FileOutputStream(file);//将压缩后的图片保存的本地上指定路径中
                    fos.write(baos.toByteArray());
                    fos.flush();
                    fos.close();
                    bt.recycle();
                    LogUtil.i("----------drawTextToRightTop");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }

    /**
     * 绘制文字到右上方
     *
     * @param context
     * @param bitmap
     * @param text
     * @param paddingRight
     * @param paddingTop
     * @return
     */
    public static Bitmap drawTextToRightTop(Context context, Bitmap bitmap, String text,
                                            int paddingRight, int paddingTop) {
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(context.getResources().getColor(R.color.gray));
        paint.setTextSize(dp2px(context, 20));
        Rect bounds = new Rect();
        paint.getTextBounds(text, 0, text.length(), bounds);
        return drawTextToBitmap(context, bitmap, text, paint, bounds,
                bitmap.getWidth() - bounds.width() - dp2px(context, paddingRight),
                dp2px(context, paddingTop) + bounds.height());
    }

    //图片上绘制文字
    private static Bitmap drawTextToBitmap(Context context, Bitmap bitmap, String text,
                                           Paint paint, Rect bounds, int paddingLeft, int paddingTop) {
        Bitmap.Config bitmapConfig = bitmap.getConfig();

        paint.setDither(true); // 获取跟清晰的图像采样
        paint.setFilterBitmap(true);// 过滤一些
        if (bitmapConfig == null) {
            bitmapConfig = Bitmap.Config.ARGB_8888;
        }
        bitmap = bitmap.copy(bitmapConfig, true);
        Canvas canvas = new Canvas(bitmap);

        canvas.drawText(text, paddingLeft, paddingTop, paint);
        return bitmap;
    }

}
