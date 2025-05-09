package TestFunction;

import org.apache.commons.io.FileUtils;
import org.example.browsers.BaseSetup;
import org.example.helpers.ExcelHelpers;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import java.time.Duration;
import java.util.concurrent.TimeUnit;

public class Login {
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

    @Test(priority = 0, enabled = true)
    public void loginByUser() throws Exception {
        excel.setExcelFile("src/test/resources/ExcelData.xlsx", "Sheet2");
        String email = excel.getCellData("Email", 1);
        String password = excel.getCellData("Password", 1);
        login(email, password);
        WebElement Message = new WebDriverWait(driver, Duration.ofSeconds(5)).until(
                ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@class='cm-notification-content notification-content cm-auto-hide alert-success']")));
        Assert.assertTrue(Message.isDisplayed(), "Không tìm thấy thông báo đăng nhập thành công");
        String messageText = Message.getText();  // Lấy nội dung thông báo lỗi
        Assert.assertTrue(messageText.contains("Bạn đã đăng nhập thành công."), "Thông báo không đúng!");
        System.out.println("Thông báo: " + messageText);
    }
    @Test(priority = 1, enabled = true)
    public void loginWrongEmail() throws Exception{
        excel.setExcelFile("src/test/resources/ExcelData.xlsx", "Sheet2");
        String email = excel.getCellData("Email", 2);
        String password = excel.getCellData("Password", 2);
        login(email, password);
        WebElement errorMessage = new WebDriverWait(driver, Duration.ofSeconds(5)).until(
                ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@class='cm-notification-content notification-content alert-error']")));
        Assert.assertTrue(errorMessage.isDisplayed(), "Không tìm thấy thông báo lỗi đăng nhập");
        String messageText = errorMessage.getText();
        Assert.assertTrue(messageText.contains("Tên người dùng và mật khẩu bạn đã nhập không hợp lệ. Xin vui lòng thử lại."), "Thông báo lỗi không đúng!");
    }

    @Test(priority = 2, enabled = true)
    public void loginWrongPassword() throws Exception{
        excel.setExcelFile("src/test/resources/ExcelData.xlsx", "Sheet2");
        String email = excel.getCellData("Email", 3);
        String password = excel.getCellData("Password", 3);
        login(email, password);
        WebElement errorMessage = new WebDriverWait(driver, Duration.ofSeconds(5)).until(
                ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@class='cm-notification-content notification-content alert-error']")));
        Assert.assertTrue(errorMessage.isDisplayed(), "Không tìm thấy thông báo lỗi đăng nhập");
        String messageText = errorMessage.getText();
        Assert.assertTrue(messageText.contains("Tên người dùng và mật khẩu bạn đã nhập không hợp lệ. Xin vui lòng thử lại."), "Thông báo lỗi không đúng!");
    }

    @Test(priority = 3, enabled = true)
    public void checkTextEmail() throws Exception {
        excel.setExcelFile("src/test/resources/ExcelData.xlsx", "Sheet2");
        String email = excel.getCellData("Email", 4);
        String password = excel.getCellData("Password", 4);
        login(email, password);
        Assert.assertTrue(this.driver.findElement(By.id("login_main_login_error_message")).isDisplayed(),"Không tìm thấy thông báo lỗi đăng nhập");
        Assert.assertEquals(this.driver.findElement(By.id("login_main_login_error_message")).getText(), "Mục E-mail là bắt buộc.");
    }

    @Test(priority = 4, enabled = true)
    public void checkTextPassword() throws Exception {
        excel.setExcelFile("src/test/resources/ExcelData.xlsx", "Sheet2");
        String email = excel.getCellData("Email", 5);
        String password = excel.getCellData("Password", 5);
        login(email, password);
        Assert.assertTrue(this.driver.findElement(By.id("psw_main_login_error_message")).isDisplayed(),"Không tìm thấy thông báo lỗi đăng nhập");
        Assert.assertEquals(this.driver.findElement(By.id("psw_main_login_error_message")).getText(), "Mục Mật khẩu là bắt buộc.");
    }

    @AfterMethod
    public void tearDown() {
        BaseSetup.quitDriver(); // Đảm bảo quitDriver chỉ được gọi sau khi hoàn tất
    }
}
