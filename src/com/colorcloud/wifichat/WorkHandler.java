/*
 * (c) COPYRIGHT 2009-2011 MOTOROLA INC.
 * MOTOROLA CONFIDENTIAL PROPRIETARY
 * MOTOROLA Advanced Technology and Software Operations
 *
 * REVISION HISTORY:
 * Author        Date       CR Number         Brief Description
 * ------------- ---------- ----------------- ------------------------------
 * e51141        2010/08/27 IKCTXTAW-19		   Initial version
 */
package com.colorcloud.wifichat;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Process;

/**
 *<code><pre>
 * CLASS:
 *  implements stand alone worker thread
 *
 * RESPONSIBILITIES: for component to create a standalone thread other than main thread
 *
 * COLABORATORS:
 * 	LocationSensorManager
 *
 * USAGE:
 * 	See each method.
 *
 *</pre></code>
 */
public final class WorkHandler {

    private HandlerThread mHandlerThread;
    private Handler mHandler;

    /**
     * Create a new WorkHandler instance.
     *
     * @param threadName The name of the thread.
     */
    public WorkHandler(String threadName) {
        //mHandlerThread = new HandlerThread(threadName, Process.THREAD_PRIORITY_BACKGROUND);
        mHandlerThread = new HandlerThread(threadName, Process.THREAD_PRIORITY_DEFAULT);
        mHandlerThread.start();
        mHandler = new Handler(mHandlerThread.getLooper());
    }

    /**
     * Get the Handler instance for this WorkHandler. Returns null if the
     * WorkHandler has been closed.
     *
     * @return An <code>android.os.Handler</code> instance or null.
     */
    public Handler getHandler() {
        return mHandler;
    }

    /**
     * TODO
     *
     * @return
     */
    public Looper getLooper() {
        Handler h = mHandler;
        return h != null ? h.getLooper() : null;
    }

    /**
     * Close the WorkHandler instance. Should be called when the
     * WorkHandler is no longer needed.
     */
    public void close() {
        if (mHandler != null) {
            mHandlerThread.getLooper().quit();
            mHandlerThread.quit();
            mHandler = null;
        }
    }

    /**
     * Cleanup if needed and log the fact the WorkHandler instance was
     * not properly closed.
     *
     * @override
     */
    @Override
    protected void finalize() {
        if (mHandler != null) {
            close();
        }
    }
}
