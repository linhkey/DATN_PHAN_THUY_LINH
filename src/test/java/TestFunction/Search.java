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

public class Search {
    String URL_login = "https://nhasachphuongnam.com/auth-loginform/?return_url=index.php%3Fprofile_id%3D177990%26selected_section%3Dgeneral%26sl%3Dvi%26dispatch%3Dprofiles.update&selected_section=general";
    String URL_dashBoard = "https://nhasachphuongnam.com/";
    WebDriver driver;
    WebDriverWait wait;
    private ExcelHelpers excel;

    public void search(String keySearch) throws InterruptedException {
//        // Khởi tạo WebDriverWait với thời gian chờ tối đa 30 giây
//        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(40));
//
//        // Chờ đợi và nhấp vào nút close
//        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[@aria-label='Close dialog']"))).click();

        Thread.sleep(2000);
        WebElement searchField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("search_input")));
        searchField.sendKeys(keySearch);
        Thread.sleep(2000);
        WebElement iconSearch = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//button[@title=\"Tìm kiếm\"]//i")));
        iconSearch.click();
        Thread.sleep(2000);
    }
    @BeforeMethod
    public void setUp() {
        excel = new ExcelHelpers();
//        System.setProperty("webdriver.chrome.driver", "C:/Users/Admin/Downloads/chromedriver-win64/chromedriver-win64/chromedriver.exe");
//        this.driver = new ChromeDriver();
        driver = BaseSetup.getDriver();
        this.driver.manage().window().maximize();
        this.driver.get(this.URL_dashBoard);
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }
    @Test(priority=0, enabled = true)
    public void searchWithCorrectName() throws Exception {
        excel.setExcelFile("src/test/resources/ExcelData.xlsx", "Sheet3");
        String keySearch = excel.getCellData("keySearch", 1);
        search(keySearch);

        // Kiểm tra hiển thị tiêu đề Sản phẩm tìm thấy
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        WebElement searchResultsElement = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//span[contains(text(),'Sản phẩm tìm thấy')]\n")));
        Assert.assertTrue(searchResultsElement.isDisplayed());
        String actualText = searchResultsElement.getText();
        Assert.assertTrue(actualText.contains("Sản phẩm tìm thấy"), "Không tìm thấy chuỗi mong muốn!");

        // Tìm danh sách sản phẩm
        List<WebElement> productList = driver.findElements(By.xpath("//div[@class=\"grid-list\"]"));

        // Kiểm tra danh sách không rỗng
        Assert.assertFalse(productList.isEmpty(), "Danh sách sản phẩm trống!!");
        Thread.sleep(2000);
    }
    @Test(priority=1, enabled = true)
    public void searchApproximatelyName() throws Exception {
        excel.setExcelFile("src/test/resources/ExcelData.xlsx", "Sheet3");
        String keySearch = excel.getCellData("keySearch", 2);
        search(keySearch);
        // Kiểm tra hiển thị tiêu đề Sản phẩm tìm thấy
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        WebElement searchResultsElement = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//span[contains(text(),'Sản phẩm tìm thấy')]\n")));
        Assert.assertTrue(searchResultsElement.isDisplayed());
        String actualText = searchResultsElement.getText();
        Assert.assertTrue(actualText.contains("Sản phẩm tìm thấy"), "Không tìm thấy chuỗi mong muốn!");

        // Cuộn xuống để hiển thị toàn bộ danh sách sản phẩm
        JavascriptExecutor js = (JavascriptExecutor) driver;

        // Tìm danh sách sản phẩm
        List<WebElement> productList = driver.findElements(By.xpath("//div[@class=\"grid-list\"]"));

        // Cuộn qua từng phần tử
        for (int i = 0; i < productList.size(); i++) {
            js.executeScript("arguments[0].scrollIntoView(true);", productList.get(i));
            Thread.sleep(4000); // Tạm dừng để kiểm tra
        }
        Thread.sleep(1000);

        // Kiểm tra danh sách không rỗng
        Assert.assertTrue(!productList.isEmpty(), "Danh sách sản phẩm trống!!");
    }

    @Test(priority=2, enabled = true)
    public void searchWithIncorrectName() throws Exception {
        excel.setExcelFile("src/test/resources/ExcelData.xlsx", "Sheet3");
        String keySearch = excel.getCellData("keySearch", 3);
        search(keySearch);

        // Kiểm tra hiển thị tiêu đề "Không tìm thấy sản phẩm phù hợp với tiêu chí tìm kiếm"
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        WebElement searchWithIncorrectName = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//div[@id=\"products_search_11\"]//div")));

        Assert.assertTrue(searchWithIncorrectName.isDisplayed());
        String actualText = searchWithIncorrectName.getText();
        String expectedText = "Không tìm thấy sản phẩm phù hợp với tiêu chí tìm kiếm";
        Assert.assertEquals(actualText,expectedText, "Không tìm thấy chuỗi mong muốn!");
        Thread.sleep(2000);
    }
    @AfterMethod
    public void tearDown() {
        BaseSetup.quitDriver(); // Đảm bảo quitDriver chỉ được gọi sau khi hoàn tất
    }
}
