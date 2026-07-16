package task3;

import java.util.LinkedList;
import java.util.Queue;

public class Buffer {
    private final Queue<Integer> queue;
    private final int capacity;

    public Buffer(int capacity) {
        this.queue = new LinkedList<>();
        this.capacity = capacity;
    }

    public synchronized void produce(int value) throws InterruptedException {
        while (queue.size() == capacity) {
            System.out.println("  [БУФЕР] полон, производитель ждет...");
            wait();
        }
        queue.offer(value);
        System.out.println("  [БУФЕР] добавлено: " + value + ", размер: " + queue.size());
        notifyAll();
    }

    public synchronized int consume() throws InterruptedException {
        while (queue.isEmpty()) {
            System.out.println("  [БУФЕР] пуст, потребитель ждет...");
            wait();
        }
        int value = queue.poll();
        System.out.println("  [БУФЕР] извлечено: " + value + ", размер: " + queue.size());
        notifyAll();
        return value;
    }
}
