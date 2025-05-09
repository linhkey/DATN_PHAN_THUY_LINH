package TestFunction;

import org.example.browsers.BaseSetup;
import org.example.helpers.ExcelHelpers;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

public class Logout {
    String URL_login = "https://nhasachphuongnam.com/auth-loginform/?return_url=index.php%3Fprofile_id%3D177990%26selected_section%3Dgeneral%26sl%3Dvi%26dispatch%3Dprofiles.update&selected_section=general";
    String URL_dashBoard = "https://nhasachphuongnam.com/";
    WebDriver driver;
    WebDriverWait wait;
    private ExcelHelpers excel;

    public void login(String email, String password) throws InterruptedException {
//        // Khởi tạo WebDriverWait với thời gian chờ tối đa 30 giây
//        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(40));
//
//        // Chờ đợi và nhấp vào nút close
//        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[@aria-label='Close dialog']"))).click();

        WebElement emailField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//input[@name='user_login'])[2]")));
        emailField.sendKeys(email);

        WebElement passwordField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//input[@name='password'])[2]")));
        passwordField.sendKeys(password);

        WebElement loginButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("(//button[@type='submit'])[4]")));
        loginButton.click();
    }

    @BeforeMethod
    public void setUp() {
        excel = new ExcelHelpers();
//        System.setProperty("webdriver.chrome.driver", "C:/Users/Admin/Downloads/chromedriver-win64/chromedriver-win64/chromedriver.exe");
//        this.driver = new ChromeDriver();
        driver = BaseSetup.getDriver();
        this.driver.manage().window().maximize();
        this.driver.get(this.URL_login);
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    @Test
    public void logoutCheck() throws Exception {
        excel.setExcelFile("src/test/resources/ExcelData.xlsx", "Sheet2");
        String email = excel.getCellData("Email", 1);
        String password = excel.getCellData("Password", 1);
        login(email, password);
        Thread.sleep(10000);
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement iconLink = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//span[text()=\"Tài Khoản\"]")));
        iconLink.click();
        WebElement iconLink2 = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//a[text()=\"Thoát\"]")));
        iconLink2.click();
        WebDriverWait wait2 = new WebDriverWait(driver, Duration.ofSeconds(5));
        wait2.until(ExpectedConditions.or(ExpectedConditions.urlToBe(URL_login)));
        Assert.assertEquals(driver.getCurrentUrl(), URL_login);
    }

    @AfterMethod
    public void tearDown() {
        BaseSetup.quitDriver(); // Đảm bảo quitDriver chỉ được gọi sau khi hoàn tất
    }
}
