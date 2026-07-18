package bookstore.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

@Configuration
@ComponentScan(basePackages = "bookstore")
@PropertySource("classpath:application.properties")
public class AppConfig {

    @Value("${book.old.months:6}")
    private int oldMonths;

    @Value("${book.request.auto.fulfill:true}")
    private boolean autoFulfillRequests;

    @Value("${csv.export.delimiter:;}")
    private String csvDelimiter;

    @Value("${csv.import.skip.header:true}")
    private boolean skipHeader;

    @Value("${log.level:INFO}")
    private String logLevel;

    @Value("${log.show.stacktrace:false}")
    private boolean showStacktrace;

    // PropertySourcesPlaceholderConfigurer для работы с @Value
    @Bean
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        PropertySourcesPlaceholderConfigurer configurer = new PropertySourcesPlaceholderConfigurer();
        configurer.setLocation(new ClassPathResource("application.properties"));
        configurer.setIgnoreUnresolvablePlaceholders(false);
        return configurer;
    }

    // ===== ГЕТТЕРЫ =====
    public int getOldMonths() { return oldMonths; }
    public boolean isAutoFulfillRequests() { return autoFulfillRequests; }
    public String getCsvDelimiter() { return csvDelimiter; }
    public boolean isSkipHeader() { return skipHeader; }
    public String getLogLevel() { return logLevel; }
    public boolean isShowStacktrace() { return showStacktrace; }

    // ===== СЕТТЕРЫ (добавлены) =====
    public void setOldMonths(int months) {
        this.oldMonths = months;
    }

    public void setAutoFulfillRequests(boolean autoFulfill) {
        this.autoFulfillRequests = autoFulfill;
    }

    public void setCsvDelimiter(String delimiter) {
        this.csvDelimiter = delimiter;
    }

    public void setSkipHeader(boolean skipHeader) {
        this.skipHeader = skipHeader;
    }

    public void setLogLevel(String logLevel) {
        this.logLevel = logLevel;
    }

    public void setShowStacktrace(boolean showStacktrace) {
        this.showStacktrace = showStacktrace;
    }

    // ===== МЕТОД ДЛЯ ОТОБРАЖЕНИЯ =====
    public void showConfig() {
        System.out.println();
        System.out.println("==================================================");
        System.out.println("  текущие настройки (Spring)");
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