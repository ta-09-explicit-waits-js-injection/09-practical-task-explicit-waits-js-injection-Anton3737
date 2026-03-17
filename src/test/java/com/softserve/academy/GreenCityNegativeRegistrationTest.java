package com.softserve.academy;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeOptions;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GreenCityNegativeRegistrationTest {
    private static WebDriver driver;

    @BeforeAll
    static void setUp() {
        ChromeOptions options = new ChromeOptions();

        options.addArguments("--lang=en-GB");
        options.setExperimentalOption("prefs", java.util.Map.of("intl.accept_languages", "en-GB,en"));

        if (System.getenv("GITHUB_ACTIONS") != null) {
            options.addArguments(
                    "--headless=new",
                    "--no-sandbox",
                    "--disable-dev-shm-usage",
                    "--window-size=1920,1080");
        }

        driver = WebDriverManager.chromedriver().capabilities(options).create();
        if (System.getenv("GITHUB_ACTIONS") == null) {
            driver.manage().window().maximize();
        }
    }

    @BeforeEach
    void openRegistrationForm() {
        driver.manage().deleteAllCookies();
        driver.navigate().to("https://www.greencity.cx.ua/#/greenCity");
        sleep(5000);
        driver.findElement(By.cssSelector(".header_sign-up-btn > span")).click();
        sleep(2000);
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("invalidEmailValues")
    @DisplayName("Invalid email values -> email error")
    void shouldShowErrorForInvalidEmail(String scenario, String email) {
        typeEmail(email);
        blur();

        assertEmailErrorVisible();
        assertSignUpButtonDisabled();
    }

    @Test
    @DisplayName("All fields empty → required errors shown")
    void shouldShowErrorsForAllEmptyFields() {
        driver.findElement(By.id("email")).click();
        driver.findElement(By.id("firstName")).click();
        driver.findElement(By.id("password")).click();
        driver.findElement(By.id("repeatPassword")).click();
        blur();

        assertEmailErrorVisible();
        assertUsernameErrorVisible();
        assertSignUpButtonDisabled();
    }

    @Test
    @DisplayName("Empty username → username required")
    void shouldShowErrorForEmptyUsername() {
        typeEmail("valid@email.com");
        typePassword("ValidPass123!");
        typeConfirm("ValidPass123!");
        driver.findElement(By.id("firstName")).click();
        blur();

        assertUsernameErrorVisible();
        assertSignUpButtonDisabled();
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("invalidPasswords")
    @DisplayName("Invalid password values -> password rule error")
    void shouldShowErrorForInvalidPassword(String scenario, String password) {
        fillValidRegistrationDataWithoutConfirm();
        typePassword(password);
        blur();
        sleep(1000);

        assertPasswordErrorVisible();
        assertSignUpButtonDisabled();
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("invalidConfirmPasswordActions")
    @DisplayName("Invalid confirm password scenarios -> confirm error")
    void shouldShowErrorForInvalidConfirmPassword(
            String scenario,
            boolean shouldTypeConfirmPassword,
            String confirmPasswordValue,
            String expectedMessagePart
    ) {
        fillValidRegistrationDataWithoutConfirm();
        typePassword("ValidPass123!");
        applyConfirmPasswordState(shouldTypeConfirmPassword, confirmPasswordValue);
        blur();

        assertConfirmPasswordErrorVisible();
        assertConfirmPasswordErrorContains(expectedMessagePart);
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
        WebElement field = driver.findElement(By.id("email"));
        field.clear();
        field.sendKeys(value);
    }

    private void typeUsername(String value) {
        WebElement field = driver.findElement(By.id("firstName"));
        field.clear();
        field.sendKeys(value);
    }

    private void typePassword(String value) {
        WebElement field = driver.findElement(By.id("password"));
        field.clear();
        field.sendKeys(value);
    }

    private void typeConfirm(String value) {
        WebElement field = driver.findElement(By.id("repeatPassword"));
        field.clear();
        field.sendKeys(value);
    }

    private void fillValidRegistrationDataWithoutConfirm() {
        typeEmail("valid@email.com");
        typeUsername("ValidUsername");
    }

    private void applyConfirmPasswordState(boolean shouldTypeConfirmPassword, String confirmPasswordValue) {
        if (shouldTypeConfirmPassword) {
            typeConfirm(confirmPasswordValue);
            return;
        }
        driver.findElement(By.id("repeatPassword")).click();
    }

    private void blur() {
        driver.findElement(By.id("firstName")).click();
        sleep(500);
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
        WebElement btn = driver.findElement(By.cssSelector("button[type='submit'].greenStyle"));
        assertFalse(btn.isEnabled(), "The 'Sign Up' button should be disabled");
    }

    private void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    @AfterAll
    static void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}
