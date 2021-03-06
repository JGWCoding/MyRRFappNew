package com.myrrfappnew.utils;

import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 类    名:  ThreadUtils
 * 描    述： ThreadPool的代理
 * 描    述： 对ThreadPool做增强操作,对提交任务,执行任务,移除任务做增强
 */
public class ThreadUtils {
    //线程池对象
    ThreadPoolExecutor mExecutor;

    private int mCorePoolSize;//核心线程数
    private int mMaximumPoolSize;//最大线程数

    private ThreadUtils(int corePoolSize) {
        mCorePoolSize = corePoolSize;
        mMaximumPoolSize = corePoolSize;
    }
    /**
     * 创建线程池对象
     */
    private void initThreadPoolExecutor() {
        if(pools ==null) {
            pools = new ThreadUtils(mMaximumPoolSize);
        }
        //类似单例的双重检查加锁,只有在第一次实例化的时候才启用同步机制,提供了性能
        if (mExecutor == null || mExecutor.isShutdown() || mExecutor.isTerminated()) {
            synchronized (ThreadUtils.class) {
                if (mExecutor == null || mExecutor.isShutdown() || mExecutor.isTerminated()) {
                    long keepAliveTime = 3000;//保持存活时间-->用不上
                    TimeUnit unit = TimeUnit.MILLISECONDS;//时间的单位-->用不上
                    BlockingQueue<Runnable> workQueue = new LinkedBlockingQueue<>();//任务队列-->无界队列
                    ThreadFactory threadFactory = Executors.defaultThreadFactory();//线程工厂
                    RejectedExecutionHandler handler = new ThreadPoolExecutor.DiscardPolicy();//异常捕获器-->用不上

                    mExecutor = new ThreadPoolExecutor(mCorePoolSize, mMaximumPoolSize, keepAliveTime, unit, workQueue, threadFactory, handler);
                }
            }
        }

    }
    /*
    执行任务和提交任务的区别?
      1.提交有返回值,执行没有返回值
      2.返回值Future干嘛的?
            1.表示异步执行完成后的结果,提供了方法去等待接收结果
            2.get方法可以接收结果,get方法可能会"阻塞"等待结果,get方法还可以得知任务执行过程中抛出异常,
              甚至可以得知到底抛出了什么异常

     */

    /**
     * 提交任务
     */
    public Future<?> submit(Runnable task) {
        //完成初始化
        initThreadPoolExecutor();
        Future<?> result  = mExecutor.submit(task);
        return result;
    }

    /**
     * 执行任务
     */
    public void execute(Runnable task) {
        //完成初始化
        initThreadPoolExecutor();
        mExecutor.execute(task);
    }
    private static Handler handler = new Handler(Looper.getMainLooper());
    /**
     * 主线程执行任务
     */
    public static void mainThread(Runnable task) {
        handler.post(task);
    }

    /**
     * 移除任务
     */
    public void remove(Runnable task) {
        //完成初始化
        initThreadPoolExecutor();
        mExecutor.remove(task);
    }
    public static ThreadUtils pools;
    public static ThreadUtils setNormalThreadPoolProxy(int size) {
        return pools = new ThreadUtils(size);
    }
    static {
        pools = new ThreadUtils(5);
    }
}
