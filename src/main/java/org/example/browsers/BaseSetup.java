package org.example.browsers;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import java.util.concurrent.TimeUnit;

public class BaseSetup {

    private static WebDriver driver;

    // Khởi tạo WebDriver chỉ một lần trong suốt suite kiểm thử
    public static WebDriver getDriver() {
        if (driver == null) {
            // Set system property for Chrome driver
            System.setProperty("webdriver.chrome.driver", "C:/Users/Admin/Downloads/chromedriver-win64/chromedriver-win64/chromedriver.exe");

            // Khởi tạo ChromeDriver với các tùy chọn
            ChromeOptions options = new ChromeOptions();
            options.addArguments("--start-maximized");
            options.addArguments("--disable-notifications");

            // Khởi tạo WebDriver
            driver = new ChromeDriver(options);


        }
        return driver;
    }

    // Phương thức dọn dẹp WebDriver khi kiểm thử kết thúc
    public static void quitDriver() {
        if (driver != null) {
            driver.quit();
            driver = null;
        }
    }
}
