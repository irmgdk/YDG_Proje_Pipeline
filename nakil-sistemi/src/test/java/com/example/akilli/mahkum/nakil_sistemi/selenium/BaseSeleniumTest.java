package com.example.akilli.mahkum.nakil_sistemi.selenium;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;git add .
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public abstract class BaseSeleniumTest { // abstract olması daha doğrudur

    protected WebDriver driver;
    protected WebDriverWait wait;

    @LocalServerPort
    protected int port;

    protected String baseUrl;

    @BeforeEach
    public void setUp() throws MalformedURLException {
        ChromeOptions options = new ChromeOptions();

        // Jenkins/Docker ortamı için kritik ayarlar
        options.addArguments("--headless=new"); // Ekransız mod
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--window-size=1920,1080");

        // docker-compose dosyanızdaki selenium-hub servisine bağlanıyoruz
        // Not: Jenkins yerelinde çalışıyorsa localhost, docker network içindeyse servis adı kullanılır.
        // Genellikle localhost:4444 üzerinden docker'daki hub'a erişilir.
        driver = new RemoteWebDriver(new URL("http://localhost:4444/wd/hub"), options);

        wait = new WebDriverWait(driver, Duration.ofSeconds(20));

        // ÖNEMLİ: Uygulamanız docker-compose'da 8080 portunda açılıyor
        // localhost yerine uygulama konteyner ismini veya direkt localhost:8080 kullanmalısınız
        baseUrl = "http://localhost:8080/mahkum-nakil";

        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
    }

    @AfterEach
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    protected void navigateTo(String path) {
        driver.get(baseUrl + path);
    }
}