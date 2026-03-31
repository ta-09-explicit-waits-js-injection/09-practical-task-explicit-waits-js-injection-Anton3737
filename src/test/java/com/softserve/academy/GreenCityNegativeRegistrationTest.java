package com.softserve.academy;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.*;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.time.LocalDate;
import java.util.Date;
import java.util.Random;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GreenCityNegativeRegistrationTest {
    private static WebDriver driver;
    private static WebDriverWait wait;
    private static Duration IMPLICIT_WAIT = Duration.ofSeconds(2);
    private static Duration EXPLICIT_WAIT = Duration.ofSeconds(15);
    private static Duration PAGE_LOAD_WAIT = Duration.ofSeconds(60);
    private static final String BASE_URL = "https://www.greencity.cx.ua/#/greenCity";


    @BeforeAll
    static void setUp() {
//        ChromeOptions options = new ChromeOptions();
        FirefoxOptions options = new FirefoxOptions();
        options.addPreference("intl.accept_languages", "en-GB, en");

//        options.addArguments("--lang=en-GB");
//        options.setExperimentalOption("prefs", java.util.Map.of("intl.accept_languages", "en-GB,en"));

        if (System.getenv("GITHUB_ACTIONS") != null) {
            options.addArguments(
                    "--headless=new",
                    "--no-sandbox",
                    "--disable-dev-shm-usage",
                    "--window-size=1920,1080");
        }

//        driver = WebDriverManager.chromedriver().capabilities(options).create();
        driver = WebDriverManager.firefoxdriver().capabilities(options).create();
        if (System.getenv("GITHUB_ACTIONS") == null) {
            driver.manage().window().maximize();
        }
        driver.manage().timeouts().implicitlyWait(IMPLICIT_WAIT);
        driver.manage().timeouts().pageLoadTimeout(PAGE_LOAD_WAIT);
    }

    @BeforeEach
    void openRegistrationForm() {
        driver.manage().deleteAllCookies();
        driver.navigate().to(BASE_URL);
        wait = new WebDriverWait(driver, EXPLICIT_WAIT);
        wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(".header_sign-up-btn"))).click();
//        WebElement signUpBtn = wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(".header_sign-up-btn")));
//        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", signUpBtn);
    }

    // ---------------------- працює
    @ParameterizedTest(name = "Email: {0}")
    @DisplayName("Invalid email values -> email error")
    @CsvFileSource(resources = "/invalid_emails.csv", numLinesToSkip = 1)
    void shouldShowErrorForInvalidEmail(String email) throws InterruptedException {
        typeEmail(email);
//        typeUsername(name);
//        typePassword(password);
//        typeConfirm(confirmPassword);
        blur();

        assertEmailErrorVisible();
        assertSignUpButtonDisabled();
    }

    // ---------------------- працює
    @ParameterizedTest(name = "Scenario: {0}, Email: {1}, Name: {2}, Password: {3},ConfirmPassword: {4}")
    @CsvSource({"'All fields is empty', '', '', '', ''"})
    @DisplayName("All fields empty → required errors shown")
    void shouldShowErrorsForAllEmptyFields(String scenario, String email, String name, String pass, String confirm) {
        typeEmail(email);
        typeUsername(name);
        typePassword(pass);
        typeConfirm(confirm);

        blur();

        assertEmailErrorVisible();
        assertUsernameErrorVisible();
        assertPasswordErrorVisible();
        assertConfirmPasswordErrorVisible();
        assertSignUpButtonDisabled();
    }

    // ---------------------- працює
    @ParameterizedTest(name = "Email: {0}, Name: {1}, Password: {2},ConfirmPassword: {3}")
    @DisplayName("Empty username → username required")
    @CsvFileSource(resources = "/invalid_names_isempty.csv", numLinesToSkip = 1)
    void shouldShowErrorForEmptyUsername(String email, String name, String pass, String confirm) {
        typeEmail(email);
        typeUsername(name);
        typePassword(pass);
        typeConfirm(confirm);

        blur();

        assertUsernameErrorVisible();
        assertSignUpButtonDisabled();
    }

    // ---------------------- працює
    @ParameterizedTest(name = "Email: {0}, Password: {2}")
    @DisplayName("Invalid password values -> password rule error")
    @CsvFileSource(resources = "/invalid_passwords_tooshort.csv", numLinesToSkip = 1)
    void shouldShowErrorForInvalidPassword(String scenario, String password) throws InterruptedException {
        typePassword(password);
        blur();

        assertPasswordErrorVisible();
        assertSignUpButtonDisabled();
    }


    @ParameterizedTest(name = "Email: {0}, Name:{1}, Password:{2}, ConfirmPassword:{3}")
    @DisplayName("Confirm password mismatch → confirm error")
    @CsvFileSource(resources = "/password_mismatch.csv", numLinesToSkip = 1)
    void shouldShowErrorForAllCsvRows(String email, String name, String password, String confirmPassword) {
        typeEmail(email);
        typeUsername(name);
        typePassword(password);
        typeConfirm(confirmPassword);

        blur();

        assertConfirmPasswordErrorVisible();
        assertConfirmPasswordErrorContains("match");
        assertSignUpButtonDisabled();
    }


    private static Stream<Arguments> invalidEmailValues() {
        return Stream.of(
                Arguments.of("Email without @", "invalid-email")
        );
    }

    private static Stream<Arguments> invalidPasswords() {
        return Stream.of(
                Arguments.of("Password shorter than 8 chars", "pass12!"),
                Arguments.of("Password with space", "Pass 123!")
        );
    }

    private static Stream<Arguments> invalidConfirmPasswordActions() {
        return Stream.of(
                Arguments.of("Confirm password mismatch", true, "DifferentPass123!", ""),
                Arguments.of("Empty confirm password", false, "", "required")
        );
    }

    private void typeEmail(String value) {
        WebElement field = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("email")));
//        WebElement field = driver.findElement(By.id("email"));
        field.clear();
        field.sendKeys(value);
    }

    private void typeUsername(String value) {
        WebElement field = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("firstName")));
//        WebElement field = driver.findElement(By.id("firstName"));
        field.clear();
        field.sendKeys(value);
    }

    private void typePassword(String value) {
        WebElement field = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("password")));
//        WebElement field = driver.findElement(By.id("password"));
        field.clear();
        field.sendKeys(value);
    }

    private void typeConfirm(String value) {
        WebElement field = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("repeatPassword")));
//        WebElement field = driver.findElement(By.id("repeatPassword"));
        field.clear();
        field.sendKeys(value);
    }

//    private void fillValidRegistrationDataWithoutConfirm() {
//        typeEmail("valid@email.com");
//        typeUsername("ValidUsername");
//    }

    private void applyConfirmPasswordState(boolean shouldTypeConfirmPassword, String confirmPasswordValue) {
        if (shouldTypeConfirmPassword) {
            typeConfirm(confirmPasswordValue);
            return;
        }
        driver.findElement(By.id("repeatPassword")).click();
    }

    private void blur() {
//        driver.findElement(By.id("firstName")).click();
//        ((JavascriptExecutor) driver).executeScript(".right-side h2");
        ((JavascriptExecutor) driver).executeScript("document.activeElement.blur();");
//        sleep(500);
    }

    private void assertEmailErrorVisible() {
        WebElement error = driver.findElement(By.id("email-err-msg"));
        assertTrue(error.isDisplayed(), "Email error message should be visible");
    }

    private void assertUsernameErrorVisible() {
        WebElement error = driver.findElement(By.xpath("//input[@id='firstName']/following-sibling::div"));
        assertTrue(error.isDisplayed(), "Username error message should be visible");
    }

    private void assertPasswordErrorVisible() {
        WebElement error = driver.findElement(By.cssSelector("p.password-not-valid"));
        assertTrue(error.isDisplayed(), "Password validation rules should be visible");
    }

    private void assertConfirmPasswordErrorVisible() {
        WebElement error = driver.findElement(By.id("confirm-err-msg"));
        assertTrue(error.isDisplayed(), "Confirm password error message should be visible");
    }

    private void assertConfirmPasswordErrorContains(String expectedMessagePart) {
        WebElement error = driver.findElement(By.id("confirm-err-msg"));
        String actualMessage = error.getText().toLowerCase();
        assertTrue(
                actualMessage.contains(expectedMessagePart.toLowerCase()),
                "Confirm password error '" + actualMessage + "' should contain '" + expectedMessagePart + "'"
        );
    }

    private void assertSignUpButtonDisabled() {
        WebElement btn = wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("button[type='submit'].greenStyle")));
//        WebElement btn = driver.findElement(By.cssSelector("button[type='submit'].greenStyle"));
        assertFalse(btn.isEnabled(), "The 'Sign Up' button should be disabled");
    }

//    private void sleep(long millis) {
//        try {
//            Thread.sleep(millis);
//        } catch (InterruptedException e) {
//            Thread.currentThread().interrupt();
//        }
//    }

    @AfterEach
    void tearDown() {
        if (driver != null) {
//            driver.quit();
            driver.navigate().refresh();
        }
    }
}
