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
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class CartManagement {
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
    @Test(priority=0, enabled = true)
    public void viewCartHasNoProduct() throws Exception {
        excel.setExcelFile("src/test/resources/ExcelData.xlsx", "Sheet5");
        //Đăng nhập
        String email = excel.getCellData("Email", 1);
        String password = excel.getCellData("Password", 1);
        login(email, password);
        wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        WebElement iconCart = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//i[@class=\"ut2-icon-use_icon_cart empty\"]")));
        iconCart.click();
        WebElement emptyCartMessage = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@class=\"ty-cart-items__empty ty-center\"]")));
        Assert.assertTrue(emptyCartMessage.isDisplayed());
        Assert.assertEquals(emptyCartMessage.getText().trim(), "Bạn chưa có sản phẩm nào trong giỏ hàng");

    }
    @Test(priority= 1, enabled = true)
    public void addNewProductToCart() throws Exception {
        excel.setExcelFile("src/test/resources/ExcelData.xlsx", "Sheet5");
        //Đăng nhập
        String email = excel.getCellData("Email2", 1);
        String password = excel.getCellData("Password", 1);
        login(email, password);
        //Lấy tên sách cần kiểm tra
        String productCheck=excel.getCellData("productName", 1);
        //Tìm kiếm sách
        String keySearch = excel.getCellData("keySearch", 1);
        search(keySearch);
        // Cuộn trang nếu cần
        ((JavascriptExecutor) driver).executeScript("window.scrollBy(0, 300);");

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
        Thread.sleep(3000);
        WebElement messageElement = buttonAddToCart.findElement(By.xpath("//h1[contains(text(),'Sản phẩm đã được thêm vào giỏ hàng của bạn')]"));
        String message = messageElement.getText();
        Assert.assertEquals(message, "Sản phẩm đã được thêm vào giỏ hàng của bạn");

        WebDriverWait waitCart = new WebDriverWait(driver, Duration.ofSeconds(5));
        Thread.sleep(2000); // giữ lại nếu muốn xem popup một chút
        driver.get("https://nhasachphuongnam.com/checkout-checkout/");

        // Cuộn trang nếu cần
        ((JavascriptExecutor) driver).executeScript("window.scrollBy(0, 700);");

        //Sản phẩm trong giỏ hàng
        WebElement cartItems = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//ul[@class=\"ty-order-products__list order-product-list\"]")));
        List<WebElement> productNames = cartItems.findElements(By.xpath("//li[@class=\"ty-order-products__item\"]"));

        //Kiểm tra xem sản phm có trong giỏ không
        boolean productFound = false;

        // Chuẩn hóa tên sản phẩm cần kiểm tra (xóa khoảng trắng thừa)
        String normalizedCheck = productCheck.trim().replaceAll("\\s+", " ");

        for (WebElement productName : productNames) {
            // Lấy phần tên sản phẩm (dòng đầu tiên của text trong thẻ WebElement)
            String[] lines = productName.getText().split("\n");
            String nameOnly = lines[0].trim().replaceAll("\\s+", " ");

            // In ra để kiểm tra
            System.out.println(">>> So sánh: [" + nameOnly + "] với [" + normalizedCheck + "]");

            // So sánh tên sản phẩm (không phân biệt hoa thường)
            if (nameOnly.equalsIgnoreCase(normalizedCheck)) {
                productFound = true;
                break;
            }
        }
        Assert.assertTrue(productFound, "❌ Sản phẩm '" + productCheck + "' không có trong giỏ hàng!");
    }

    @Test(priority=2, enabled = true)
    public void ViewCartHasProduct() throws Exception {
        excel.setExcelFile("src/test/resources/ExcelData.xlsx", "Sheet5");
        //Đăng nhập
        String email = excel.getCellData("Email2", 1);
        String password = excel.getCellData("Password", 1);
        login(email, password);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        // Bước 1: Kiểm tra số lượng sản phẩm trên biểu tượng giỏ hàng
        WebElement cartQuantityElement = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//span[@class=\"ty-minicart-count\"]")
        ));
        int cartQuantity = Integer.parseInt(cartQuantityElement.getText());
        Assert.assertTrue(cartQuantity >= 1, "Giỏ hàng trống hoặc số lượng sản phẩm hiển thị sai!");

        Thread.sleep(5000);
        // Bước 2: Mở giao diện chi tiết giỏ hàng
        WebElement cartIcon = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//i[@class=\"ut2-icon-use_icon_cart filled\"]")
        ));
        cartIcon.click();

        // Bước 3: Kiểm tra sản phẩm hiển thị trong giỏ hàng
        WebElement productNameElement = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//li[@class=\"ty-cart-items__list-item\"]")
        ));
        Assert.assertTrue(productNameElement.isDisplayed(), "Tên sản phẩm không hiển thị trong giỏ hàng!");
    }
    @Test(priority=3, enabled = true)
    public void removeProduct() throws Exception {
        excel.setExcelFile("src/test/resources/ExcelData.xlsx", "Sheet5");
        //Đăng nhập
        String email = excel.getCellData("Email2", 1);
        String password = excel.getCellData("Password", 1);
        login(email, password);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        Thread.sleep(5000);
        // Mở giao diện chi tiết giỏ hàng
        WebElement cartIcon = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//i[@class=\"ut2-icon-use_icon_cart filled\"]")
        ));
        cartIcon.click();
        //Click "Xem giỏ hàng"
        WebElement viewCart = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//a[text()=\"Xem giỏ hàng\"]")
        ));
        viewCart.click();

        // Cuộn trang nếu cần
        ((JavascriptExecutor) driver).executeScript("window.scrollBy(0, 250);");

        String selectedProductName = excel.getCellData("productName", 1);
        // Tạo XPath tới nút "Loại bỏ"
        String xpathRemove = String.format(
                "//a[@class='ty-cart-content__product-title' and normalize-space(text())=\"%s\"]/following-sibling::a[@title=\"Loại bỏ\"]",
                selectedProductName.trim()
        );

        try {
            wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            WebElement removeButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath(xpathRemove)));
            removeButton.click();

            // Đợi sản phẩm biến mất khỏi giỏ hàng
            String productXpath = String.format(
                    "//a[@class='ty-cart-content__product-title' and normalize-space(text())=\"%s\"]",
                    selectedProductName.trim()
            );
            wait.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath(productXpath)));

            System.out.println("✅ Đã xóa sản phẩm: " + selectedProductName);

        } catch (Exception e) {
            System.out.println("⚠️ Sản phẩm không tồn tại trong giỏ hàng hoặc đã bị xóa trước đó: " + selectedProductName);
        }
    }
    @AfterMethod
    public void tearDown() {
        BaseSetup.quitDriver(); // Đảm bảo quitDriver chỉ được gọi sau khi hoàn tất
    }
}
