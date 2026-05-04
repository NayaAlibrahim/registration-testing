package com.qa.registration;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;
import java.time.Duration;

/**
 * BaseTest - Provides shared setup/teardown and utility methods
 * for all registration form test classes.
 */
public abstract class BaseTest {

    protected WebDriver driver;
    protected WebDriverWait wait;

    // Path to the HTML file under test
    private static String PAGE_URL;

    @BeforeAll
    static void setupDriverManager() {
        // WebDriverManager auto-downloads the correct ChromeDriver
        WebDriverManager.chromedriver().setup();

        // Resolve HTML file path
        File htmlFile = new File("src/main/resources/registration.html");
        PAGE_URL = "file:///" + htmlFile.getAbsolutePath().replace("\\", "/");
    }

    @BeforeEach
    void setUp() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless=new");   // Run headless in CI
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--window-size=1280,900");

        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        driver.get(PAGE_URL);
    }

    @AfterEach
    void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    // ─── Helper Methods ────────────────────────────────────────────────────────

    /** Fill the complete form with given values, then click Submit */
    protected void fillAndSubmit(String fn, String ln, String email,
                                  String dob, String pw, String cpw) {
        type("firstName", fn);
        type("lastName", ln);
        type("email", email);
        type("dob", dob);
        type("password", pw);
        type("confirmPassword", cpw);
        driver.findElement(By.id("submitBtn")).click();
    }

    /** Fill only one field; leave others with valid defaults */
    protected void fillFormWithOneField(String fieldId, String value) {
        fillAndSubmit(
            fieldId.equals("firstName")      ? value : "John",
            fieldId.equals("lastName")       ? value : "Doe",
            fieldId.equals("email")          ? value : "john.doe@example.com",
            fieldId.equals("dob")            ? value : "15/06/1990",
            fieldId.equals("password")       ? value : "Password1",
            fieldId.equals("confirmPassword")? value : (
                fieldId.equals("password") ? value : "Password1"
            )
        );
    }

    /** Type text into an input field by its ID */
    protected void type(String id, String text) {
        WebElement el = driver.findElement(By.id(id));
        el.clear();
        if (text != null && !text.isEmpty()) {
            el.sendKeys(text);
        }
    }

    /** Return the visible text of an error message element */
    protected String getError(String fieldId) {
        WebElement err = driver.findElement(By.id(fieldId + "Err"));
        wait.until(ExpectedConditions.visibilityOf(err));
        return err.getText();
    }

    /** Return true if the success banner is visible */
    protected boolean isSuccess() {
        WebElement msg = driver.findElement(By.id("successMsg"));
        return msg.isDisplayed();
    }

    /** Return true if an error message for a field is visible */
    protected boolean hasError(String fieldId) {
        WebElement err = driver.findElement(By.id(fieldId + "Err"));
        return err.isDisplayed();
    }
}
