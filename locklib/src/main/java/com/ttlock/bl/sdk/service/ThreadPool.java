//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.ttlock.bl.sdk.service;

import com.ttlock.bl.sdk.util.LogUtil;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThreadPool {
    private static final int threadCount = Runtime.getRuntime().availableProcessors() * 2;
    private static ExecutorService mThreadPool = null;

    public ThreadPool() {
    }

    public static ExecutorService getThreadPool() {
        if(mThreadPool == null) {
            Class var0 = ExecutorService.class;
            synchronized(ExecutorService.class) {
                if(mThreadPool == null) {
                    LogUtil.d("threadCount:" + threadCount, true);
                    mThreadPool = Executors.newFixedThreadPool(threadCount);
                }
            }
        }

        return mThreadPool;
    }
}
