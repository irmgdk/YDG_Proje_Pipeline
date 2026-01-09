package com.example.akilli.mahkum.nakil_sistemi.selenium;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInstance;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.net.URL;
import java.time.Duration;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public abstract class BaseSeleniumTest {

    @LocalServerPort
    protected int port;

    protected WebDriver driver;
    protected WebDriverWait wait;
    protected String baseUrl;

    @BeforeEach
    public void setUp() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless=new");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--disable-gpu");
        options.addArguments("--window-size=1920,1080");

        try {
            // Docker'daki Selenium Grid'e bağlan
            String hubUrl = System.getProperty("selenium.grid.url", "http://localhost:4444/wd/hub");
            String appUrl = System.getProperty("app.url", "http://localhost:8080/mahkum-nakil");

            System.out.println("🎯 Selenium Grid URL: " + hubUrl);
            System.out.println("🎯 Hedef Uygulama URL: " + appUrl);

            driver = new RemoteWebDriver(new URL(hubUrl), options);
            wait = new WebDriverWait(driver, Duration.ofSeconds(15));

            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(15));
            driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(60));

            // Docker'daki uygulamanın URL'sini kullan
            baseUrl = appUrl;
            System.out.println("✅ WebDriver başlatıldı. Base URL: " + baseUrl);
        } catch (Exception e) {
            System.err.println("❌ Selenium bağlantı hatası: " + e.getMessage());
            throw new RuntimeException("Selenium başlatılamadı", e);
        }
    }

    @AfterEach
    public void tearDown() {
        if (driver != null) {
            try {
                driver.quit();
                System.out.println("✅ WebDriver kapatıldı");
            } catch (Exception e) {
                System.err.println("⚠️ WebDriver kapatılırken hata: " + e.getMessage());
            }
        }
    }

    protected void navigateTo(String path) {
        String fullUrl = baseUrl + path;
        System.out.println("🌐 Sayfaya gidiliyor: " + fullUrl);

        // Retry mekanizması ekle
        for (int i = 1; i <= 3; i++) {
            try {
                driver.get(fullUrl);
                System.out.println("✅ Sayfa yüklendi: " + fullUrl);
                return;
            } catch (Exception e) {
                System.err.println("⚠️ Sayfa yükleme hatası (Deneme " + i + "/3): " + e.getMessage());
                if (i < 3) {
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                    }
                }
            }
        }
        throw new RuntimeException("Sayfa yüklenemedi: " + fullUrl);
    }

    protected String getBaseUrl() {
        return baseUrl;
    }
}