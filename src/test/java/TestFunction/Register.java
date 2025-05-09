package TestFunction;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

import org.example.browsers.BaseSetup;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.example.helpers.ExcelHelpers;


public class Register {
    String URL_register = "https://nhasachphuongnam.com/profiles-add-vi/";
    String URL_dashBoard = "https://nhasachphuongnam.com/";
    WebDriver driver;
    private ExcelHelpers excel;

    public Register() {
    }

    public void registerAccount(String address, String username, String phoneNumber, String email, String password, String acceptPassword) throws InterruptedException {
//        // Khởi tạo WebDriverWait với thời gian chờ tối đa 30 giây
//        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(40));
//
//        // Chờ đợi và nhấp vào nút close
//        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[@aria-label='Close dialog']"))).click();

        Thread.sleep(2000L);
        driver.findElement(By.id("elm_83")).sendKeys(address);
        Thread.sleep(2000L);
        driver.findElement(By.id("elm_6")).sendKeys(username);
        Thread.sleep(2000L);
        driver.findElement(By.id("elm_9")).sendKeys(phoneNumber);
        Thread.sleep(2000L);
        driver.findElement(By.id("email")).sendKeys(email);
        Thread.sleep(2000L);
        driver.findElement(By.id("password1")).sendKeys(password);
        Thread.sleep(2000L);
        driver.findElement(By.id("password2")).sendKeys(acceptPassword);
        Thread.sleep(2000L);
        driver.findElement(By.name("dispatch[profiles.update]")).click();
    }
    @BeforeMethod
    public void setUp() {
        excel = new ExcelHelpers();
//        System.setProperty("webdriver.chrome.driver", "C:/Users/Admin/Downloads/chromedriver-win64/chromedriver-win64/chromedriver.exe");
//        this.driver = new ChromeDriver();
        driver = BaseSetup.getDriver();
        this.driver.manage().window().maximize();
        this.driver.get(this.URL_register);
    }

    @Test(priority = 0, enabled = true)
    public void registerByGuest() throws Exception {
        excel.setExcelFile("src/test/resources/ExcelData.xlsx", "Sheet1");
        String address = excel.getCellData("Address", 1);
        String username = excel.getCellData("Username", 1);
        String phoneNumber = excel.getCellData("Phone Number", 1);
        String email = excel.getCellData("Email", 1);
        String password = excel.getCellData("Password", 1);
        String acceptPassword = excel.getCellData("Acept Password", 1);
        registerAccount(address, username, phoneNumber, email, password, acceptPassword);
        Assert.assertTrue(this.driver.findElement(By.xpath("//div[@class='cm-notification-content notification-content cm-auto-hide alert-success']")).isDisplayed());
        Thread.sleep(1000L);
        String actualMessage = driver.findElement(By.xpath("//div[@class='cm-notification-content notification-content cm-auto-hide alert-success']")).getText();
        Assert.assertTrue(actualMessage.contains("Tài khoản đã được tạo thành công."), "Thông báo không đúng!");
        Thread.sleep(1000L);
        System.out.println("Actual Message (innerText): [" + actualMessage + "]");
    }

    @Test(priority = 1, enabled = true)
    public void registerByExistedUser() throws Exception {
        excel.setExcelFile("src/test/resources/ExcelData.xlsx", "Sheet1");
        String address = excel.getCellData("Address", 1);
        String username = excel.getCellData("Username", 1);
        String phoneNumber = excel.getCellData("Phone Number", 1);
        String email = excel.getCellData("Email", 1);
        String password = excel.getCellData("Password", 1);
        String acceptPassword = excel.getCellData("Acept Password", 1);
        registerAccount(address, username, phoneNumber, email, password, acceptPassword);
        Assert.assertTrue(this.driver.findElement(By.xpath("//div[@class=\"cm-notification-content notification-content alert-error\"]")).isDisplayed());
        String actualMessage = driver.findElement(By.xpath("//div[@class=\"cm-notification-content notification-content alert-error\"]")).getText();
        Assert.assertTrue(actualMessage.contains("Tên người dùng hoặc email bạn chọn đã tồn tại. Vui lòng thử với một email khác."), "Thông báo không đúng!");
        Thread.sleep(1000L);
        System.out.println("Actual Message (innerText): [" + actualMessage + "]");
    }

    @Test(priority = 2, enabled = true)
    public void checkTextAddress() throws Exception  {
        excel.setExcelFile("src/test/resources/ExcelData.xlsx", "Sheet1");
        String address = excel.getCellData("Address", 15);
        String username = excel.getCellData("Username", 1);
        String phoneNumber = excel.getCellData("Phone Number", 1);
        String email = excel.getCellData("Email", 1);
        String password = excel.getCellData("Password", 1);
        String acceptPassword = excel.getCellData("Acept Password", 1);
        registerAccount(address, username, phoneNumber, email, password, acceptPassword);
        Assert.assertTrue(this.driver.findElement(By.xpath("//span[@id=\"elm_83_error_message\"]/p[1]")).isDisplayed());
        Assert.assertEquals(this.driver.findElement(By.xpath("//span[@id=\"elm_83_error_message\"]/p[1]")).getText(), "Mục Địa chỉ là bắt buộc.");
        Thread.sleep(1000L);
    }
    @Test(priority = 3, enabled = true)
    public void checkTextUsername() throws Exception  {
        excel.setExcelFile("src/test/resources/ExcelData.xlsx", "Sheet1");
        String address = excel.getCellData("Address", 1);
        String username = excel.getCellData("Username", 15);
        String phoneNumber = excel.getCellData("Phone Number", 1);
        String email = excel.getCellData("Email", 1);
        String password = excel.getCellData("Password", 1);
        String acceptPassword = excel.getCellData("Acept Password", 1);
        registerAccount(address, username, phoneNumber, email, password, acceptPassword);
        Assert.assertTrue(this.driver.findElement(By.xpath("//span[@id=\"elm_6_error_message\"]/p[1]")).isDisplayed());
        Assert.assertEquals(this.driver.findElement(By.xpath("//span[@id=\"elm_6_error_message\"]/p[1]")).getText(), "Mục Họ và Tên là bắt buộc.");
        Thread.sleep(1000L);
    }

    @Test(priority = 4, enabled = true)
    public void checkTextEmail() throws Exception  {
        excel.setExcelFile("src/test/resources/ExcelData.xlsx", "Sheet1");
        String address = excel.getCellData("Address", 1);
        String username = excel.getCellData("Username", 1);
        String phoneNumber = excel.getCellData("Phone Number", 1);
        String email = excel.getCellData("Email", 15);
        String password = excel.getCellData("Password", 1);
        String acceptPassword = excel.getCellData("Acept Password", 1);
        registerAccount(address, username, phoneNumber, email, password, acceptPassword);
        Assert.assertTrue(this.driver.findElement(By.xpath("//span[@id=\"email_error_message\"]/p[1]")).isDisplayed());
        Assert.assertEquals(this.driver.findElement(By.xpath("//span[@id=\"email_error_message\"]/p[1]")).getText(), "Mục E-mail là bắt buộc.");
        Thread.sleep(1000L);
    }

    @Test(priority = 5, enabled = true)
    public void checkTextPassword() throws Exception  {
        excel.setExcelFile("src/test/resources/ExcelData.xlsx", "Sheet1");
        String address = excel.getCellData("Address", 1);
        String username = excel.getCellData("Username", 1);
        String phoneNumber = excel.getCellData("Phone Number", 1);
        String email = excel.getCellData("Email", 1);
        String password = excel.getCellData("Password", 15);
        String acceptPassword = excel.getCellData("Acept Password", 1);
        registerAccount(address, username, phoneNumber, email, password, acceptPassword);
        Assert.assertTrue(this.driver.findElement(By.xpath("//span[@id=\"password1_error_message\"]/p[1]")).isDisplayed());
        Assert.assertEquals(this.driver.findElement(By.xpath("//span[@id=\"password1_error_message\"]/p[1]")).getText(), "Mục Mật khẩu là bắt buộc.");
        Thread.sleep(1000L);
    }

    @Test(priority = 6, enabled = true)
    public void checkTextAcceptPassword() throws Exception  {
        excel.setExcelFile("src/test/resources/ExcelData.xlsx", "Sheet1");
        String address = excel.getCellData("Address", 1);
        String username = excel.getCellData("Username", 1);
        String phoneNumber = excel.getCellData("Phone Number", 1);
        String email = excel.getCellData("Email", 1);
        String password = excel.getCellData("Password", 1);
        String acceptPassword = excel.getCellData("Acept Password", 15);
        registerAccount(address, username, phoneNumber, email, password, acceptPassword);
        Assert.assertTrue(this.driver.findElement(By.xpath("//span[@id=\"password2_error_message\"]/p[1]")).isDisplayed());
        Assert.assertEquals(this.driver.findElement(By.xpath("//span[@id=\"password2_error_message\"]/p[1]")).getText(), "Mục Xác nhận mật khẩu là bắt buộc.");
        Thread.sleep(1000L);
    }

    @Test(priority = 7, enabled = true)
    public void checkPasswordMismatch() throws Exception  {
        excel.setExcelFile("src/test/resources/ExcelData.xlsx", "Sheet1");
        String address = excel.getCellData("Address", 2);
        String username = excel.getCellData("Username", 2);
        String phoneNumber = excel.getCellData("Phone Number", 2);
        String email = excel.getCellData("Email", 2);
        String password = excel.getCellData("Password", 2);
        String acceptPassword = excel.getCellData("Acept Password", 2);
        registerAccount(address, username, phoneNumber, email, password, acceptPassword);
        Assert.assertTrue(this.driver.findElement(By.xpath("//span[@id=\"password1_error_message\"]/p[1]")).isDisplayed());
        Assert.assertEquals(this.driver.findElement(By.xpath("//span[@id=\"password1_error_message\"]/p[1]")).getText(), "Mật khẩu trong các mục Xác nhận mật khẩu và Mật khẩu không khớp.");
        Thread.sleep(1000L);
    }

    @Test(priority = 8, enabled = true)
    public void checkConfirmPasswordMismatch() throws Exception  {
        excel.setExcelFile("src/test/resources/ExcelData.xlsx", "Sheet1");
        String address = excel.getCellData("Address", 2);
        String username = excel.getCellData("Username", 2);
        String phoneNumber = excel.getCellData("Phone Number", 2);
        String email = excel.getCellData("Email", 2);
        String password = excel.getCellData("Password", 2);
        String acceptPassword = excel.getCellData("Acept Password", 2);
        registerAccount(address, username, phoneNumber, email, password, acceptPassword);
        Assert.assertTrue(this.driver.findElement(By.xpath("//span[@id=\"password2_error_message\"]/p[1]")).isDisplayed());
        Assert.assertEquals(this.driver.findElement(By.xpath("//span[@id=\"password2_error_message\"]/p[1]")).getText(), "Mật khẩu trong các mục Mật khẩu và Xác nhận mật khẩu không khớp.");
        Thread.sleep(1000L);
    }
    @AfterMethod
    public void tearDown() {
        BaseSetup.quitDriver(); // Đảm bảo quitDriver chỉ được gọi sau khi hoàn tất
    }
}
