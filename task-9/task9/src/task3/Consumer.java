package task3;

public class Consumer implements Runnable {
    private final Buffer buffer;
    private final int count;

    public Consumer(Buffer buffer, int count) {
        this.buffer = buffer;
        this.count = count;
    }

    @Override
    public void run() {
        System.out.println("  [ПОТРЕБИТЕЛЬ] запущен");
        try {
            for (int i = 0; i < count; i++) {
                int value = buffer.consume();
                System.out.println("  [ПОТРЕБИТЕЛЬ] получил: " + value);
                Thread.sleep(150);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("  [ПОТРЕБИТЕЛЬ] завершен");
    }
}
