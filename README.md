# Selenium WebDriver Practical Task: Optimization and Improvement of Automated Tests

## Objective
This task is aimed at improving existing negative tests for the registration form on the [GreenCity](https://www.greencity.cx.ua/#/greenCity) website. The primary focus is on transitioning from beginner-level practices to professional automation approaches.

## Development Task Description

Your goal is to optimize the existing test suite in the `GreenCityNegativeRegistrationTest.java` class using modern Selenium WebDriver techniques.

### Core Requirements:

1.  **Wait Optimization:**
    *   Completely remove the use of `Thread.sleep()` (hard/implicit waits).
    *   Implement **Explicit Waits** using `WebDriverWait` and `ExpectedConditions`.
    *   Configure waits for element appearance, clickability, or state changes (e.g., appearance of an error message).
    *   If necessary, configure **Implicit Wait** at the driver level, but avoid conflicts with Explicit Waits.

2.  **Use of JavaScript Injection:**
    *   Replace some standard Selenium actions (if they are unstable) with JS scripts via `JavascriptExecutor`.
    *   For example: use `arguments[0].blur()` for reliable field defocusing instead of clicking on another element.
    *   Use `arguments[0].click()` or `scrollIntoView()` if standard Selenium methods fail due to overlapping elements.

3.  **Improvement of Test Scenarios and Parameterization:**
    *   Expand data sets in `@MethodSource` to cover more boundary values (Boundary Value Analysis) and equivalence classes (Equivalence Partitioning).
    *   Make parameterized tests more informative by using the `name` parameter in the `@ParameterizedTest` annotation.
    *   Ensure full field clearing before entering new data to avoid cross-test influence.

4.  **Refactoring of Helper Methods:**
    *   Make `assert...` methods more flexible and stable by adding waits before the `isDisplayed()` check.
    *   Improve the `blur()` method by making it universal or replacing it with a more reliable approach.

## Current Project State
The project currently has:
*   Basic test structure using JUnit 5.
*   `WebDriverManager` for driver management.
*   Initial implementation of parameterized tests for passwords and emails.
*   Helper methods for field interaction and error verification.

**Problem:** Tests use `Thread.sleep()`, which slows down execution and makes them unstable. Some methods (e.g., `blur()`) are implemented via workarounds (clicking on another field), which is not a professional standard.

## How to Get Started
1.  Open the project in your IDE (IntelliJ IDEA recommended).
2.  Review the code in `src/test/java/com/softserve/academy/GreenCityNegativeRegistrationTest.java`.
3.  Start by setting up `WebDriverWait` in the `setUp` method or directly in the tests.
4.  Gradually replace `sleep()` with appropriate wait conditions.

## Success Criteria
*   All 7 tests pass successfully (green).
*   The code contains no `Thread.sleep()` calls.
*   Tests run faster due to dynamic waits.
*   The code is cleaner and follows professional automation principles.
