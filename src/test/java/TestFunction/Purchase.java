package TestFunction;

import org.example.browsers.BaseSetup;
import org.example.helpers.ExcelHelpers;
import org.example.listeners.ReportListener;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.ITestContext;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import java.lang.reflect.Method;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Listeners(ReportListener.class)
public class Purchase {
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

    public void search(String keySearch) throws InterruptedException {
        Thread.sleep(5000);
        WebElement searchField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("search_input")));
        searchField.clear();
        searchField.sendKeys(keySearch);
        Thread.sleep(2000);
        WebElement iconSearch = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//button[@title=\"Tìm kiếm\"]//i")));
        iconSearch.click();
        Thread.sleep(3000);
    }
    public void inputPhoneNumber() throws Exception {
        excel.setExcelFile("src/test/resources/ExcelData.xlsx", "Sheet8");

        //Đăng nhập
        String phoneNumber = excel.getCellData("phoneNumber", 1);

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement inputPhoneNumber = wait.until(ExpectedConditions.elementToBeClickable(By.name("user_data[phone]")));
        inputPhoneNumber.clear();
        inputPhoneNumber.sendKeys(phoneNumber);
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
    public void addNewProductToCart() throws Exception {
        // Nhập từ khóa tìm kiếm và nhấn nút tìm kiếm
        excel.setExcelFile("src/test/resources/ExcelData.xlsx", "Sheet8");

        //Đăng nhập
        String email = excel.getCellData("Email", 1);
        String password = excel.getCellData("Password", 1);
        login(email, password);
        //Tìm kiếm sách
        String keySearch = excel.getCellData("keySearch", 1);
        search(keySearch);

        String selectedProductName = excel.getCellData("productName", 1);

        // 🪄 Tạo XPath động từ biến sản phẩm
        String xpathProduct = String.format("//a[@class='product-title' and contains(normalize-space(), \"%s\")]", selectedProductName);

        // Đợi và click vào sản phẩm theo XPath động
        WebElement productElement = wait.until(ExpectedConditions.elementToBeClickable(By.xpath(xpathProduct)));
        productElement.click();
        WebDriverWait wait = new WebDriverWait(driver,Duration.ofSeconds(5));
        //Click button thêm vào giỏ
        WebElement buttonAddToCart = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//i[@class=\"ut2-icon-use_icon_cart\"])[1]")));
        buttonAddToCart.click();
    }
    @Test(priority=0, enabled = true)
    public void paymentCash() throws Exception {
        addNewProductToCart();
        Thread.sleep(6000);

        // Nhấn nút thanh toán
        driver.get("https://nhasachphuongnam.com/checkout-checkout/");

        // Nhập số điện thoại
        inputPhoneNumber();
        // Cuộn trang nếu cần
        ((JavascriptExecutor) driver).executeScript("window.scrollBy(0, 700);");

        // Chọn phương thức thanh toán
        List<WebElement> paymentMethods = driver.findElements(By.xpath("(//div[@class=\"b--pay-way__unit__label__text__pseudo-radio\"])[1]"));
        WebElement paymentOption = paymentMethods.get(0);
        if (!paymentOption.isSelected()) {
            paymentOption.click();
        }
        // Nhấn nút "Đặt hàng"
        WebElement buttonPaymen = driver.findElement(By.name("dispatch[checkout.place_order]"));
        buttonPaymen.click();
    }
}
