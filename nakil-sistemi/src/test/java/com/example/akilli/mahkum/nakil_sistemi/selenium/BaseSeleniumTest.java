package com.example.akilli.mahkum.nakil_sistemi.selenium;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public abstract class BaseSeleniumTest {

    protected WebDriver driver;
    protected WebDriverWait wait;

    @LocalServerPort
    protected int port;

    protected String baseUrl;

    @BeforeEach
    public void setUp() throws MalformedURLException {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless=new");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--window-size=1920,1080");

        // Jenkins docker-compose içindeki selenium-hub'a bağlanır
        driver = new RemoteWebDriver(new URL("http://localhost:4444/wd/hub"), options);

        wait = new WebDriverWait(driver, Duration.ofSeconds(20));
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