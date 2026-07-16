package task2;

public class AlternatingThreads {
    public static void main(String[] args) {


        Object lock = new Object();
        boolean[] turn = {true}; // true - первый поток, false - второй

        Thread thread1 = new Thread(() -> {
            for (int i = 0; i < 5; i++) {
                synchronized (lock) {
                    while (!turn[0]) {
                        try {
                            lock.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    System.out.println("Поток 1: шаг " + (i + 1));
                    turn[0] = false;
                    lock.notifyAll();
                }
            }
        }, "Поток-1");

        Thread thread2 = new Thread(() -> {
            for (int i = 0; i < 5; i++) {
                synchronized (lock) {
                    while (turn[0]) {
                        try {
                            lock.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    System.out.println("Поток 2: шаг " + (i + 1));
                    turn[0] = true;
                    lock.notifyAll();
                }
            }
        }, "Поток-2");

        thread1.start();
        thread2.start();

        try {
            thread1.join();
            thread2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}
