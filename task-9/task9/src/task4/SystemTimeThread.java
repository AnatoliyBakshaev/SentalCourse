package task4;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class SystemTimeThread extends Thread {
    private final long intervalSeconds;
    private volatile boolean running;

    public SystemTimeThread(long intervalSeconds) {
        this.intervalSeconds = intervalSeconds;
        this.running = true;
        this.setDaemon(true); // Служебный поток
        this.setName("SystemTimeThread");
    }

    @Override
    public void run() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        System.out.println("  [СЛУЖЕБНЫЙ] запущен, интервал: " + intervalSeconds + " сек.");

        while (running) {
            try {
                Thread.sleep(intervalSeconds * 1000);
                String time = LocalDateTime.now().format(formatter);
                System.out.println("  [СЛУЖЕБНЫЙ] системное время: " + time);
            } catch (InterruptedException e) {
                System.out.println("  [СЛУЖЕБНЫЙ] прерван");
                break;
            }
        }
        System.out.println("  [СЛУЖЕБНЫЙ] завершен");
    }

    public void stopThread() {
        System.out.println("  [СЛУЖЕБНЫЙ] остановка...");
        this.running = false;
        this.interrupt();
    }

    public static void main(String[] args) throws InterruptedException {

        // Создаем служебный поток с интервалом 5 секунды
        SystemTimeThread timeThread = new SystemTimeThread(5);
        timeThread.start();

        // Ждем 10 секунд, чтобы увидеть работу потока
        System.out.println("работает 20 секунд...");
        Thread.sleep(20000);

        // Останавливаем служебный поток
        timeThread.stopThread();
        timeThread.join();


    }
}
