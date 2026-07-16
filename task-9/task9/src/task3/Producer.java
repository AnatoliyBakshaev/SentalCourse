package task3;


import java.util.Random;

public class Producer implements Runnable {
    private final Buffer buffer;
    private final int count;
    private final Random random;

    public Producer(Buffer buffer, int count) {
        this.buffer = buffer;
        this.count = count;
        this.random = new Random();
    }

    @Override
    public void run() {
        System.out.println("  [ПРОИЗВОДИТЕЛЬ] запущен");
        try {
            for (int i = 0; i < count; i++) {
                int value = random.nextInt(100);
                System.out.println("  [ПРОИЗВОДИТЕЛЬ] генерирую: " + value);
                buffer.produce(value);
                Thread.sleep(random.nextInt(100));
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("  [ПРОИЗВОДИТЕЛЬ] завершен");
    }
}