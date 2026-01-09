package com.example.akilli.mahkum.nakil_sistemi.selenium;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import static org.junit.jupiter.api.Assertions.*;

public class DashboardSeleniumTest extends BaseSeleniumTest {

    @Test
    public void testHomePageLoadsSuccessfully() {
        // Act
        driver.get(baseUrl);

        // Assert
        WebElement pageTitle = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//h1[contains(text(), 'Akıllı Mahkum Nakil Sistemi')]")
        ));

        assertTrue(pageTitle.isDisplayed());
        assertEquals("Akıllı Mahkum Nakil Sistemi", driver.getTitle());
    }

    @Test
    public void testDashboardStatisticsDisplayed() {
        // Arrange
        driver.get(baseUrl + "/dashboard");

        // Act
        WebElement toplamMahkum = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//div[contains(@class, 'stat-card')]//h5[contains(text(), 'Toplam Mahkum')]/following-sibling::h2")
        ));

        WebElement toplamArac = driver.findElement(By.xpath(
                "//div[contains(@class, 'stat-card')]//h5[contains(text(), 'Toplam Araç')]/following-sibling::h2"
        ));

        WebElement toplamGorev = driver.findElement(By.xpath(
                "//div[contains(@class, 'stat-card')]//h5[contains(text(), 'Toplam Görev')]/following-sibling::h2"
        ));

        WebElement toplamIhlal = driver.findElement(By.xpath(
                "//div[contains(@class, 'stat-card')]//h5[contains(text(), 'Toplam İhlal')]/following-sibling::h2"
        ));

        // Assert
        assertTrue(Integer.parseInt(toplamMahkum.getText()) >= 0);
        assertTrue(Integer.parseInt(toplamArac.getText()) >= 0);
        assertTrue(Integer.parseInt(toplamGorev.getText()) >= 0);
        assertTrue(Integer.parseInt(toplamIhlal.getText()) >= 0);
    }

    @Test
    public void testNavigationLinksWork() {
        // Arrange
        driver.get(baseUrl);

        // Test Mahkumlar link
        WebElement mahkumLink = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//a[contains(@href, '/mahkum/list')]")
        ));
        mahkumLink.click();

        wait.until(ExpectedConditions.urlContains("/mahkum/list"));
        assertTrue(driver.getCurrentUrl().contains("/mahkum/list"));

        // Test Görevler link
        driver.get(baseUrl);
        WebElement gorevLink = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//a[contains(@href, '/nakil-gorevi/list')]")
        ));
        gorevLink.click();

        wait.until(ExpectedConditions.urlContains("/nakil-gorevi/list"));
        assertTrue(driver.getCurrentUrl().contains("/nakil-gorevi/list"));
    }
}