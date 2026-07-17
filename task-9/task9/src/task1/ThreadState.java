package task1;

public class ThreadState {
    public static void main(String[] args) throws InterruptedException {
        // 1. NEW - поток создан, но не запущен
        Thread thread = new Thread(() -> {});
        System.out.println("1. NEW: " + thread.getState());

        // 2. RUNNABLE - поток выполняется
        Thread runnableThread = new Thread(() -> {
            for (int i = 0; i < 1000000; i++) {
                // просто считаем
            }
        });
        runnableThread.start();
        Thread.sleep(10);
        System.out.println("2. RUNNABLE: " + runnableThread.getState());

        // 3. BLOCKED - поток заблокирован
        System.out.println("\n3. BLOCKED:");
        Object lock = new Object();

        Thread blockedThread1 = new Thread(() -> {
            synchronized (lock) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        Thread blockedThread2 = new Thread(() -> {
            synchronized (lock) {
                System.out.println("   получил блокировку");
            }
        });

        blockedThread1.start();
        Thread.sleep(50);
        blockedThread2.start();
        Thread.sleep(50);
        System.out.println("   blockedThread2 состояние: " + blockedThread2.getState());

        // 4. WAITING - поток ожидает
        System.out.println("\n4. WAITING:");
        Object waitLock = new Object();
        Thread waitingThread = new Thread(() -> {
            synchronized (waitLock) {
                try {
                    waitLock.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        waitingThread.start();
        Thread.sleep(50);
        System.out.println("   waitingThread состояние: " + waitingThread.getState());

        // 5. TIMED_WAITING - поток ожидает с таймаутом
        System.out.println("\n5. TIMED_WAITING:");
        Thread timedWaitingThread = new Thread(() -> {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        timedWaitingThread.start();
        Thread.sleep(50);
        System.out.println("   timedWaitingThread состояние: " + timedWaitingThread.getState());

        // 6. TERMINATED - поток завершен
        System.out.println("\n6. TERMINATED:");
        Thread terminatedThread = new Thread(() -> {});
        terminatedThread.start();
        terminatedThread.join();
        System.out.println("   terminatedThread состояние: " + terminatedThread.getState());

        // Ожидаем завершения всех потоков
        runnableThread.join();
        blockedThread1.join();
        blockedThread2.join();
        waitingThread.interrupt();
        waitingThread.join();
        timedWaitingThread.join();

    }
}
