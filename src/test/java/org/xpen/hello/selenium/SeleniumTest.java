package org.xpen.hello.selenium;

import java.time.Duration;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * 演示浏览器自动百度搜索
 *
 */
public class SeleniumTest {
	
	@Test
	public void test() throws Exception {
        WebDriver driver = new ChromeDriver();
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        try {
            driver.get("https://www.baidu.com/");
            driver.findElement(By.name("wd")).sendKeys("cheese" + Keys.ENTER);
            WebElement firstResult = wait.until(ExpectedConditions.presenceOfElementLocated(
            		By.cssSelector("div.result")));
            System.out.println(firstResult.getText());
        } finally {
            driver.quit();
        }
	}

}
