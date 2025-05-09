package TestFunction;

import org.example.browsers.BaseSetup;
import org.example.helpers.ExcelHelpers;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
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

public class ViewDetailAccountDetail {
    String URL_login = "https://nhasachphuongnam.com/auth-loginform/?return_url=index.php%3Fprofile_id%3D177990%26selected_section%3Dgeneral%26sl%3Dvi%26dispatch%3Dprofiles.update&selected_section=general";
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
    public void viewAccountDetailCheck() throws Exception {
        excel.setExcelFile("src/test/resources/ExcelData.xlsx", "Sheet6");
        //Đăng nhập
        String email = excel.getCellData("Email", 1);
        String password = excel.getCellData("Password", 1);
        login(email, password);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        //Click biểu tượng account
        WebElement iconLink = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//i[@class=\"ut2-icon-outline-account-circle\"]")));
        iconLink.click();
        //Click "Tài khoản của tôi"
        WebElement accountOfMe = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("(//a[text()=\"Tài Khoản Của Tôi\"])[1]")));
        accountOfMe.click();

        //Kiểm tra giá trị Email
        Thread.sleep(3000);
        WebElement inputElementEmail = wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("user_data[email]")));
        // Lấy giá trị của thuộc tính "value"
        String actualValueEmail = inputElementEmail.getAttribute("value");
        String expectedValueEmail=excel.getCellData("Email", 1);
        Assert.assertEquals(actualValueEmail, expectedValueEmail, "Giá trị của thuộc tính 'Email' không khớp!");
        Thread.sleep(2000);

        ((JavascriptExecutor) driver).executeScript("window.scrollBy(0, 500);");
        //Kiểm tra giá trị Địa chỉ
        WebElement inputElementAddress= wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("user_data[fields][83]")));
        // Lấy giá trị của thuộc tính "value"
        String actualValueAddress = inputElementAddress.getAttribute("value");
        String expectedValueAddress=excel.getCellData("Address", 1);
        Assert.assertEquals(actualValueAddress, expectedValueAddress, "Giá trị của thuộc tính 'Địa chỉ' không khớp!");
        Thread.sleep(2000);

        //Kiểm tra giá trị Họ và Tên
        WebElement inputElementUserName= wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("user_data[firstname]")));
        // Lấy giá trị của thuộc tính "value"
        String actualValueUserName = inputElementUserName.getAttribute("value");
        String expectedValueUserName=excel.getCellData("UserName", 1);
        Assert.assertEquals(actualValueUserName, expectedValueUserName, "Giá trị của thuộc tính 'Họ và Tên' không khớp!");
        Thread.sleep(2000);

        //Kiểm tra giá trị Số điện thoại
        WebElement inputElementPhoneNumber= wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("user_data[phone]")));
        // Lấy giá trị của thuộc tính "value"
        String actualValuePhoneNumber = inputElementPhoneNumber.getAttribute("value");
        String expectedValuePhoneNumber=excel.getCellData("PhoneNumber", 1);
        Assert.assertEquals(actualValuePhoneNumber, expectedValuePhoneNumber, "Giá trị của thuộc tính 'Số điện thoại' không khớp!");
        Thread.sleep(2000);
    }
    @AfterMethod
    public void tearDown() {
        BaseSetup.quitDriver(); // Đảm bảo quitDriver chỉ được gọi sau khi hoàn tất
    }
}
