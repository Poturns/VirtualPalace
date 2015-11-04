package kr.poturns.virtualpalace.util;

import java.util.ArrayDeque;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Myungjin Kim on 2015-09-30.
 * <p/>
 * Thread 관련 적업을 처리하는데 유용한 기능을 제공하는 유틸리티 클래스.
 * <p/>
 * ThreadPool과 Executor 관련 코드는 AsyncTask 클래스를 참조하였다.
 */
public final class ThreadUtils {

    private ThreadUtils() {
    }

    private static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();
    private static final int CORE_POOL_SIZE = CPU_COUNT + 1;
    private static final int MAXIMUM_POOL_SIZE = CPU_COUNT * 2 + 1;
    private static final int KEEP_ALIVE = 1;

    /**
     * 주어진 Queue와 ThreadFactory로 새로운 ThreadPoolExecutor를 생성한다.
     *
     * Pool Size는 Core Size + 1, Max Pool Size는 Core Size * 2 + 1, Keep Alive Time은 1 Sec 이다.
     * */
    public static Executor newThreadPoolExecutor(BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory){
        return new ThreadPoolExecutor(CORE_POOL_SIZE, MAXIMUM_POOL_SIZE, KEEP_ALIVE, TimeUnit.SECONDS, workQueue, threadFactory);
    }

    /**
     * 병렬로 작업을 수행하는 {@link Executor}
     */
    public static final Executor THREAD_POOL_EXECUTOR = newThreadPoolExecutor(
            new LinkedBlockingQueue<Runnable>(128),
            new ThreadFactory() {
                private final AtomicInteger mCount = new AtomicInteger(1);

                public Thread newThread(Runnable r) {
                    Thread t = new Thread(r, "VirtualPalace #" + mCount.getAndIncrement());
                    t.setDaemon(true);
                    return t;
                }
            });

    /**
     * 순서대로 하나씩 작업을 수행하는 {@link Executor}
     */
    public static final Executor SERIAL_EXECUTOR = new SerialExecutor();

    private static class SerialExecutor implements Executor {
        final ArrayDeque<Runnable> mTasks = new ArrayDeque<Runnable>();
        Runnable mActive;

        public synchronized void execute(final Runnable r) {
            mTasks.offer(new Runnable() {
                public void run() {
                    try {
                        r.run();
                    } finally {
                        scheduleNext();
                    }
                }
            });
            if (mActive == null) {
                scheduleNext();
            }
        }

        protected synchronized void scheduleNext() {
            if ((mActive = mTasks.poll()) != null) {
                THREAD_POOL_EXECUTOR.execute(mActive);
            }
        }
    }

}
