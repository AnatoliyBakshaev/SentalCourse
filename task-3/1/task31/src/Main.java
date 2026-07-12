//Выводящую на экран случайно сгенерированное трёхзначное натуральное число и сумму его цифр.
public class Main {
    public static void main(String[] args) {
        int number = (new java.util.Random()).nextInt(999);
        int sum = 0;
        int temp = number;
        while (temp > 0) {
            sum += temp % 10;
            temp /= 10;
        }
        System.out.println("Сгенерированное число: " + number);
        System.out.println("Сумма его цифр: " + sum);
    }
}