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

public class ViewProductDetail {
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
    //Xem chi tiết sản phẩm không cần đăng nhập
    @Test(priority = 0, enabled = true)
    public void viewProductDetailNotLoggedIn() throws Exception {
        // Nhập từ khóa tìm kiếm và nhấn nút tìm kiếm
        excel.setExcelFile("src/test/resources/ExcelData.xlsx", "Sheet4");
        String keySearch = excel.getCellData("keySearch", 1);
        search(keySearch);
        // Kiểm tra hiển thị tiêu đề Sản phẩm tìm thấy
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        WebElement searchResultsElement = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//span[contains(text(),'Sản phẩm tìm thấy')]\n")));
        // Cuộn trang nếu cần
        ((JavascriptExecutor) driver).executeScript("window.scrollBy(0, 500);");
        // Tìm danh sách sản phẩm
        List<WebElement> productList = driver.findElements(By.xpath("//div[@class=\"grid-list\"]"));
        // Kiểm tra danh sách không rỗng
        Assert.assertTrue(!productList.isEmpty(), "Danh sách sản phẩm trống!!");

        String selectedProductName = excel.getCellData("productName", 1);

        // 🪄 Tạo XPath động từ biến sản phẩm
        String xpathProduct = String.format("//a[@class='product-title' and contains(normalize-space(), \"%s\")]", selectedProductName);

        // Đợi và click vào sản phẩm theo XPath động
        WebElement productElement = wait.until(ExpectedConditions.elementToBeClickable(By.xpath(xpathProduct)));
        String expectProductName = productElement.getText().trim();

        // Dựa vào productElement, đi ngược DOM để tìm phần tử giá
        WebElement priceElement = productElement.findElement(By.xpath("../../..//span[contains(@id,'sec_discounted_price_')]"));
        String expectPrice = priceElement.getText().trim();

        // Dựa vào productElement, đi ngược DOM để tìm phần tử tiền tệ
        WebElement currencyElement = productElement.findElement(
                By.xpath("../../..//span[contains(@id,'sec_discounted_price_')]/following-sibling::span[1]"));
        String expectCurrency = currencyElement.getText().trim();

        // Dựa vào productElement, đi ngược DOM để tìm phần tử tình trạng hàng
        WebElement orderStatusElement = productElement.findElement(
                By.xpath("../../..//span[contains(@id,'in_stock_info')]"));
        String expectOrderStatus = orderStatusElement.getText().trim();

        productElement.click();

        // Xác minh rằng trang chi tiết sản phẩm đã được mở
        WebElement productTitle = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(
                "//label[text()=\"Barcode:\"]")));
        Assert.assertTrue(productTitle.isDisplayed(), "Không thể mở trang chi tiết sản phẩm!");

        //Kiểm tra hiển thị đúng và đủ tên sách
         WebElement productDetailElement = wait.until(ExpectedConditions.elementToBeClickable(
                 By.xpath("//div[@class=\"ut2-pb__title\"]/h1/bdi")));
        String actualProductName = productDetailElement.getText().trim();
        Assert.assertEquals(expectProductName,actualProductName,"Không hiển thị đúng tên sách");
        Assert.assertTrue(productDetailElement.isDisplayed(),"Không hiển thị đủ tên sách");

        //Kiểm tra hiển thị đúng và đủ giá tiền
        WebElement priceDetailElement = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("(//div[@class=\"ut2-pb__rating\"])[1]/following::bdi[1]/span[1]")));
        String actualPrice = priceDetailElement.getText().trim();
        Assert.assertEquals(expectPrice,actualPrice,"Không hiển thị đúng giá tiền");
        Assert.assertTrue(priceDetailElement.isDisplayed(),"Không hiển thị đủ giá tiền");

        //Kiểm tra hiển thị đúng và đủ loại tiền tệ
        WebElement currencyDetailElement = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("(//div[@class=\"ut2-pb__rating\"])[1]/following::bdi[1]/span[2]")));
        String actualCurrency = currencyDetailElement.getText().trim();
        Assert.assertEquals(expectCurrency,actualCurrency,"Không hiển thị đúng loại tiền tệ");
        Assert.assertTrue(currencyDetailElement.isDisplayed(),"Không hiển thị đủ loại tiền tệ");

        //Kiểm tra hiển thị đúng và đủ tình trạng hàng
        WebElement orderStatusDetailElement = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("(//span[contains(@id,\"in_stock_info_\")])[1]")));
        String actuaOrderStatus = orderStatusDetailElement.getText().trim();
        Assert.assertEquals(expectOrderStatus,actuaOrderStatus,"Không hiển thị đúng tình trạng hàng");
        Assert.assertTrue(orderStatusDetailElement.isDisplayed(),"Không hiển thị đủ tình trạng hàng");

        //Kiểm tra hiển thị đủ Barcode
        WebElement barcodeElement = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//label[text()=\"Barcode:\"]//following-sibling::span[1]")));
        Assert.assertTrue(barcodeElement.isDisplayed(),"Không hiển thị đủ Barcode");

        //Kiểm tra hiển thị đủ button CHỌN MUA
        WebElement orderButtonElement = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("(//button[contains(@id,\"button_cart\")])[1]")));
        Assert.assertTrue(orderButtonElement.isDisplayed(),"Không hiển thị button CHỌN MUA");
        //Kiểm tra hiển thị đủ Mô tả sản phẩm
        //Kiểm tra hiển thị đủ thông tin chi tiết
        //Kiểm tra hiển thị đủ đánh giá của khách hàng
    }

    //Xem chi tiết sản phẩm cần đăng nhập
    @Test(priority = 1, enabled = true)
    public void viewProductDetailLoggedIn() throws Exception {
        // Nhập từ khóa tìm kiếm và nhấn nút tìm kiếm
        excel.setExcelFile("src/test/resources/ExcelData.xlsx", "Sheet4");

        //Đăng nhập
        String email = excel.getCellData("Email", 1);
        String password = excel.getCellData("Password", 1);
        login(email, password);
        //Tìm kiếm sách
        String keySearch = excel.getCellData("keySearch", 1);
        search(keySearch);
        // Kiểm tra hiển thị tiêu đề Sản phẩm tìm thấy
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        WebElement searchResultsElement = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//span[contains(text(),'Sản phẩm tìm thấy')]\n")));
        // Cuộn trang nếu cần
        ((JavascriptExecutor) driver).executeScript("window.scrollBy(0, 300);");
        // Tìm danh sách sản phẩm
        List<WebElement> productList = driver.findElements(By.xpath("//div[@class=\"grid-list\"]"));
        // Kiểm tra danh sách không rỗng
        Assert.assertTrue(!productList.isEmpty(), "Danh sách sản phẩm trống!!");

        String selectedProductName = excel.getCellData("productName", 1);

        // 🪄 Tạo XPath động từ biến sản phẩm
        String xpathProduct = String.format("//a[@class='product-title' and contains(normalize-space(), \"%s\")]", selectedProductName);

        // Đợi và click vào sản phẩm theo XPath động
        WebElement productElement = wait.until(ExpectedConditions.elementToBeClickable(By.xpath(xpathProduct)));
        String expectProductName = productElement.getText().trim();

        // Dựa vào productElement, đi ngược DOM để tìm phần tử giá
        WebElement priceElement = productElement.findElement(By.xpath("../../..//span[contains(@id,'sec_discounted_price_')]"));
        String expectPrice = priceElement.getText().trim();

        // Dựa vào productElement, đi ngược DOM để tìm phần tử tiền tệ
        WebElement currencyElement = productElement.findElement(
                By.xpath("../../..//span[contains(@id,'sec_discounted_price_')]/following-sibling::span[1]"));
        String expectCurrency = currencyElement.getText().trim();

        // Dựa vào productElement, đi ngược DOM để tìm phần tử tình trạng hàng
        WebElement orderStatusElement = productElement.findElement(
                By.xpath("../../..//span[contains(@id,'in_stock_info')]"));
        String expectOrderStatus = orderStatusElement.getText().trim();

        productElement.click();

        // Xác minh rằng trang chi tiết sản phẩm đã được mở
        WebElement productTitle = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(
                "//label[text()=\"Barcode:\"]")));
        Assert.assertTrue(productTitle.isDisplayed(), "Không thể mở trang chi tiết sản phẩm!");

        //Kiểm tra hiển thị đúng và đủ tên sách
        WebElement productDetailElement = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//div[@class=\"ut2-pb__title\"]/h1/bdi")));
        String actualProductName = productDetailElement.getText().trim();
        Assert.assertEquals(expectProductName,actualProductName,"Không hiển thị đúng tên sách");
        Assert.assertTrue(productDetailElement.isDisplayed(),"Không hiển thị đủ tên sách");

        //Kiểm tra hiển thị đúng và đủ giá tiền
        WebElement priceDetailElement = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("(//div[@class=\"ut2-pb__rating\"])[1]/following::bdi[1]/span[1]")));
        String actualPrice = priceDetailElement.getText().trim();
        Assert.assertEquals(expectPrice,actualPrice,"Không hiển thị đúng giá tiền");
        Assert.assertTrue(priceDetailElement.isDisplayed(),"Không hiển thị đủ giá tiền");

        //Kiểm tra hiển thị đúng và đủ loại tiền tệ
        WebElement currencyDetailElement = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("(//div[@class=\"ut2-pb__rating\"])[1]/following::bdi[1]/span[2]")));
        String actualCurrency = currencyDetailElement.getText().trim();
        Assert.assertEquals(expectCurrency,actualCurrency,"Không hiển thị đúng loại tiền tệ");
        Assert.assertTrue(currencyDetailElement.isDisplayed(),"Không hiển thị đủ loại tiền tệ");

        //Kiểm tra hiển thị đúng và đủ tình trạng hàng
        WebElement orderStatusDetailElement = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("(//span[contains(@id,\"in_stock_info_\")])[1]")));
        String actuaOrderStatus = orderStatusDetailElement.getText().trim();
        Assert.assertEquals(expectOrderStatus,actuaOrderStatus,"Không hiển thị đúng tình trạng hàng");
        Assert.assertTrue(orderStatusDetailElement.isDisplayed(),"Không hiển thị đủ tình trạng hàng");

        //Kiểm tra hiển thị đủ Barcode
        WebElement barcodeElement = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//label[text()=\"Barcode:\"]//following-sibling::span[1]")));
        Assert.assertTrue(barcodeElement.isDisplayed(),"Không hiển thị đủ Barcode");

        //Kiểm tra hiển thị đủ button CHỌN MUA
        WebElement orderButtonElement = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("(//button[contains(@id,\"button_cart\")])[1]")));
        Assert.assertTrue(orderButtonElement.isDisplayed(),"Không hiển thị button CHỌN MUA");
        //Kiểm tra hiển thị đủ Mô tả sản phẩm
        //Kiểm tra hiển thị đủ thông tin chi tiết
        //Kiểm tra hiển thị đủ đánh giá của khách hàng
    }
    @AfterMethod
    public void tearDown() {
        BaseSetup.quitDriver(); // Đảm bảo quitDriver chỉ được gọi sau khi hoàn tất
    }
}
