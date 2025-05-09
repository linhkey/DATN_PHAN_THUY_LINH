package org.example.listeners;

import org.example.helpers.CaptureHelpers;
import org.example.logs.LogUtils;
import org.openqa.selenium.WebDriver;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

public class TestListener implements ITestListener {

    @Override
    public void onFinish(ITestContext result) {
        LogUtils.info("Kết thúc automation test");
    }

    @Override
    public void onStart(ITestContext result) {
        LogUtils.info("Đây là đoạn khởi động automation test");
    }

    @Override
    public void onTestFailedButWithinSuccessPercentage(ITestResult result) {

    }

    @Override
    public void onTestFailure(ITestResult result) {
        LogUtils.error("Đây là test case bị fail: " + result.getName());
        try {
            WebDriver driver = (WebDriver) result.getTestContext().getAttribute("driver"); // 👈 Lấy driver
            CaptureHelpers.captureScreenshot(driver, result.getName());
            CaptureHelpers.stopRecord();
        } catch (Exception e) {
            LogUtils.info("Exception while taking screenshot " + e.getMessage());
        }

    }

    @Override
    public void onTestSkipped(ITestResult result) {
        LogUtils.info("Đây là test case bị bỏ qua: " + result.getName());
        try {
            CaptureHelpers.stopRecord();
        } catch (Exception e) {
            LogUtils.error("Không thể stop record khi skipped: " + e.getMessage());
        }
    }

    @Override
    public void onTestStart(ITestResult result) {
        LogUtils.info("Bắt đầu chạy test case: " + result.getName());
        try {
            CaptureHelpers.startRecord(result.getName());
        } catch (Exception e) {
            LogUtils.error("Không thể start record: " + e.getMessage());
        }
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        LogUtils.info("Đây là test case chạy thành công: " + result.getName());
        try {
            CaptureHelpers.stopRecord();
        } catch (Exception e) {
            LogUtils.error("Không thể stop record: " + e.getMessage());
        }
    }
}