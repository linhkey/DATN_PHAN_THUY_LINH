package TestFunction;

import org.example.browsers.BaseSetup;
import org.example.listeners.ReportListener;
import org.example.listeners.TestListener;
import org.example.helpers.ExcelHelpers;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import java.lang.reflect.Method;
import java.time.Duration;
import java.util.concurrent.TimeUnit;

//@Listeners(TestListener.class)
@Listeners(ReportListener.class)
public class ChangePassword {
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
    public void setUp(Method method, ITestContext context) throws Exception {
//        CaptureHelpers.startRecord("ChangePassword");
        excel = new ExcelHelpers();
//        System.setProperty("webdriver.chrome.driver", "C:/Users/Admin/Downloads/chromedriver-win64/chromedriver-win64/chromedriver.exe");
//        this.driver = new ChromeDriver();
        driver = BaseSetup.getDriver();
        this.driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        this.driver.manage().window().maximize();
        this.driver.get(this.URL_login);
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        context.setAttribute("driver", driver);
    }
    @Test(priority=0, enabled = true)
    public void changePasswordSuccess() throws Exception {
        excel.setExcelFile("src/test/resources/ExcelData.xlsx", "Sheet7");
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

        // Tìm các trường nhập liệu đổi mật khẩu và nút Lưu
        WebElement newPasswordField = driver.findElement(By.name("user_data[password1]")); // Tên trường nhập mật khẩu mới
        WebElement confirmPasswordField = driver.findElement(By.name("user_data[password2]")); // Tên trường xác nhận mật khẩu mới
        WebElement changePasswordButton = driver.findElement(
                By.id("save_profile_but")); // Lấy element theo id nút "Lưu"

        // Nhập thông tin mật khẩu
        String newPassword=excel.getCellData("PasswordChange", 1);
        String aceptNewPassword=excel.getCellData("PasswordChange", 1);
        newPasswordField.clear();
        newPasswordField.sendKeys(newPassword); // Mật khẩu mới
        Thread.sleep(2000);

        confirmPasswordField.clear();
        confirmPasswordField.sendKeys(aceptNewPassword);
        Thread.sleep(2000);

        ((JavascriptExecutor) driver).executeScript("window.scrollBy(0, 2600);");
        changePasswordButton.click();

        // Chờ thông báo đổi mật khẩu thành công
//        WebElement successMessage = wait.until(ExpectedConditions.visibilityOfElementLocated(
//                By.xpath("//*[strong[text()=\"THÔNG TIN\"]]/text()[contains(., \"Dữ liệu hồ sơ đã được cập nhật thành công\")]"))); // Thay đổi xpath nếu cần
        WebElement successMessage = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//div[@class='cm-notification-content notification-content cm-auto-hide alert-success']"))); // Thay đổi xpath nếu cần
        System.out.println(successMessage.getText()); // In thông báo thành công

        // Nếu có yêu cầu xác nhận lại mật khẩu cũ hoặc thông báo lỗi, bạn có thể xử lý thêm ở đây
        Thread.sleep(3000);
    }
    @Test(priority=0, enabled = true)
    public void changePasswordwitdrongPass() throws Exception {
        excel.setExcelFile("src/test/resources/ExcelData.xlsx", "Sheet7");
        //Đăng nhập
        String email = excel.getCellData("Email", 2);
        String password = excel.getCellData("Password", 2);
        login(email, password);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        //Click biểu tượng account
        WebElement iconLink = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//i[@class=\"ut2-icon-outline-account-circle\"]")));
        iconLink.click();
        //Click "Tài khoản của tôi"
        WebElement accountOfMe = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("(//a[text()=\"Tài Khoản Của Tôi\"])[1]")));
        accountOfMe.click();

        // Tìm các trường nhập liệu đổi mật khẩu và nút Lưu
        WebElement newPasswordField = driver.findElement(By.name("user_data[password1]")); // Tên trường nhập mật khẩu mới
        WebElement confirmPasswordField = driver.findElement(By.name("user_data[password2]")); // Tên trường xác nhận mật khẩu mới
        WebElement changePasswordButton = driver.findElement(
                By.id("save_profile_but")); // Lấy element theo id nút "Lưu"

        // Nhập thông tin mật khẩu
        String newPassword=excel.getCellData("Password", 2);
        String aceptNewPassword=excel.getCellData("PasswordChangeWrong", 2);
        newPasswordField.clear();
        newPasswordField.sendKeys(newPassword); // Mật khẩu mới
        Thread.sleep(2000);

        confirmPasswordField.clear();
        confirmPasswordField.sendKeys(aceptNewPassword);
        Thread.sleep(2000);

        ((JavascriptExecutor) driver).executeScript("window.scrollBy(0, 2600);");
        changePasswordButton.click();

        WebElement wrongMessage = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//span[@id=\"password2_error_message\"]"))); // Thay đổi xpath nếu cần
        System.out.println(wrongMessage.getText());
        Assert.assertEquals("Lỗi: Thông báo không đúng như mong đợi!",wrongMessage,"Mật khẩu không khớp");
    }

    // Nó sẽ thực thi sau mỗi lần thực thi testcase (@Test)
    @AfterMethod
    public void tearDown() {
        BaseSetup.quitDriver(); // Đảm bảo quitDriver chỉ được gọi sau khi hoàn tất
    }


    public void takeScreenshot(ITestResult result) throws Exception {
        // Khởi tạo đối tượng result thuộc ITestResult để lấy trạng thái và tên của từng Step
        // Ở đây sẽ so sánh điều kiện nếu testcase passed hoặc failed
        // passed = SUCCESS và failed = FAILURE
//        if (ITestResult.FAILURE == result.getStatus()) {
//            try {
//                CaptureHelpers.captureScreenshot(driver,result.getName());
//            } catch (Exception e) {
//                System.out.println("Exception while taking screenshot " + e.getMessage());
//            }
//        }
//        CaptureHelpers.stopRecord();
    }
}
