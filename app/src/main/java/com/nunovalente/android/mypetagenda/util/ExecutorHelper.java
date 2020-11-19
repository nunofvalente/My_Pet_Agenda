package com.nunovalente.android.mypetagenda.util;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class ExecutorHelper {

    /**
     * This class will help with asynchronous tasks along the application
     */

    private static final Object LOCK = new Object();
    private static ExecutorHelper eInstance;
    private final Executor databaseExecutor;

    private ExecutorHelper(Executor databaseExecutor) {
        this.databaseExecutor = databaseExecutor;
    }

    public static ExecutorHelper getInstance() {
        if(eInstance == null) {
            synchronized (LOCK) {
                eInstance = new ExecutorHelper(Executors.newSingleThreadExecutor());
            }
        }
        return eInstance;
    }

    public Executor getDatabaseExecutor() {
        return databaseExecutor;
    }
}
