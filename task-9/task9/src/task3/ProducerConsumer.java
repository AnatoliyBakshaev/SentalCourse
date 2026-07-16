package task3;

public class ProducerConsumer {
    public static void main(String[] args) {

        Buffer buffer = new Buffer(3);
        int count = 10;

        Thread producer = new Thread(new Producer(buffer, count), "Производитель");
        Thread consumer = new Thread(new Consumer(buffer, count), "Потребитель");

        producer.start();
        consumer.start();

        try {
            producer.join();
            consumer.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("  ВСЕ ПОТОКИ ЗАВЕРШЕНЫ");
    }
}
