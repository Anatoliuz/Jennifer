package fix.jennifer.executor;


import android.os.Process;
import fix.jennifer.executor.MainThreadExecutor;
import fix.jennifer.executor.PriorityThreadFactory;

import java.util.concurrent.*;

public class DefaultExecutorSupplier{

    public static final int NUMBER_OF_CORES = Runtime.getRuntime().availableProcessors();

    private final ThreadPoolExecutor mForBackgroundTasks;

    private final Executor mMainThreadExecutor;

    private static DefaultExecutorSupplier sInstance;


    public static DefaultExecutorSupplier getInstance() {
        if (sInstance == null) {
            synchronized (DefaultExecutorSupplier.class) {
                sInstance = new DefaultExecutorSupplier();
            }
        }
        return sInstance;
    }


    private DefaultExecutorSupplier() {

            // setting the thread factory
            ThreadFactory backgroundPriorityThreadFactory = new
                    PriorityThreadFactory(Process.THREAD_PRIORITY_FOREGROUND);

            // setting the thread pool executor for mForBackgroundTasks;
            mForBackgroundTasks = new ThreadPoolExecutor(
                    NUMBER_OF_CORES * 2,
                    NUMBER_OF_CORES * 2,
                    60L,
                    TimeUnit.SECONDS,
                    new LinkedBlockingQueue<Runnable>(),
                    backgroundPriorityThreadFactory
            );

            // setting the thread pool executor for mMainThreadExecutor;
            mMainThreadExecutor = new MainThreadExecutor();
        }


    public ThreadPoolExecutor forBackgroundTasks() {
        return mForBackgroundTasks;
    }

}