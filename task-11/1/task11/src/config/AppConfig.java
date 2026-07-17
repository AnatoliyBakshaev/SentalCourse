package config;

import annotation.config.ConfigProperty;
import annotation.config.ConfigProcessor;
import annotation.di.Component;

@Component
public class AppConfig {
    private static AppConfig instance;

    @ConfigProperty(propertyName = "book.old.months", type = int.class)
    private int oldMonths = 6;

    @ConfigProperty(propertyName = "book.request.auto.fulfill", type = boolean.class)
    private boolean autoFulfillRequests = true;

    @ConfigProperty(propertyName = "csv.export.delimiter")
    private String csvDelimiter = ",";

    @ConfigProperty(propertyName = "csv.import.skip.header", type = boolean.class)
    private boolean skipHeader = true;

    @ConfigProperty(propertyName = "log.level")
    private String logLevel = "INFO";

    @ConfigProperty(propertyName = "log.show.stacktrace", type = boolean.class)
    private boolean showStacktrace = false;

    //конструктор без параметров
    public AppConfig() {
        ConfigProcessor.process(this);
    }

    public static AppConfig getInstance() {
        if (instance == null) {
            instance = new AppConfig();
        }
        return instance;
    }


    // ===== ГЕТТЕРЫ =====
    public int getOldMonths() { return oldMonths; }
    public boolean isAutoFulfillRequests() { return autoFulfillRequests; }
    public String getCsvDelimiter() { return csvDelimiter; }
    public boolean isSkipHeader() { return skipHeader; }
    public String getLogLevel() { return logLevel; }
    public boolean isShowStacktrace() { return showStacktrace; }

    // ===== СЕТТЕРЫ =====
    public void setOldMonths(int months) { this.oldMonths = months; }
    public void setAutoFulfillRequests(boolean autoFulfill) { this.autoFulfillRequests = autoFulfill; }

    public void showConfig() {
        System.out.println();
        System.out.println("==================================================");
        System.out.println("  текущие настройки");
        System.out.println("==================================================");
        System.out.println("  залежавшиеся книги (месяцев): " + oldMonths);
        System.out.println("  авто-выполнение запросов: " + (autoFulfillRequests ? "включено" : "отключено"));
        System.out.println("  разделитель csv: '" + csvDelimiter + "'");
        System.out.println("  пропуск заголовка csv: " + (skipHeader ? "да" : "нет"));
        System.out.println("  уровень логирования: " + logLevel);
        System.out.println("  показ стектрейса: " + (showStacktrace ? "да" : "нет"));
        System.out.println("==================================================");
    }
}