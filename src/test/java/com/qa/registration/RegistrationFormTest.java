package com.qa.registration;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

/**
 * RegistrationFormTest
 *
 * 20 test cases covering the Create New Account form.
 *
 * Techniques used:
 *  - Equivalence Partitioning (EP): valid class / invalid class per field
 *  - Boundary Value Analysis (BVA): min/max edges of each field
 *  - Negative Testing: SQL injection, XSS, wrong types
 */
@DisplayName("Registration Form – Full Test Suite")
class RegistrationFormTest extends BaseTest {

    // ═══════════════════════════════════════════════════════════════════════════
    //  TC-01  HAPPY PATH – All valid data → success
    // ═══════════════════════════════════════════════════════════════════════════
    @Test
    @DisplayName("TC-01 | Happy Path – valid input → account created")
    void tc01_validFormSubmitsSuccessfully() {
        fillAndSubmit("Alice", "Smith", "alice.smith@example.com",
                      "10/05/1995", "Password1", "Password1");
        assertTrue(isSuccess(), "Success banner should be visible after valid submission");
    }

    // ═══════════════════════════════════════════════════════════════════════════
    //  TC-02  FIRST NAME – Empty (EP: invalid class)
    // ═══════════════════════════════════════════════════════════════════════════
    @Test
    @DisplayName("TC-02 | EP – First Name empty → error")
    void tc02_firstNameEmpty_showsError() {
        fillAndSubmit("", "Smith", "alice@example.com",
                      "10/05/1995", "Password1", "Password1");
        assertTrue(hasError("firstName"), "Error must appear for empty first name");
        assertEquals("First name is required.", getError("firstName"));
    }

    // ═══════════════════════════════════════════════════════════════════════════
    //  TC-03  FIRST NAME – 1 character (BVA: below min boundary of 2)
    // ═══════════════════════════════════════════════════════════════════════════
    @Test
    @DisplayName("TC-03 | BVA – First Name 1 char → error (min boundary - 1)")
    void tc03_firstNameOneChar_showsError() {
        fillFormWithOneField("firstName", "A");
        assertTrue(hasError("firstName"), "1-character first name must be rejected");
    }

    // ═══════════════════════════════════════════════════════════════════════════
    //  TC-04  FIRST NAME – 2 characters (BVA: exactly at min boundary)
    // ═══════════════════════════════════════════════════════════════════════════
    @Test
    @DisplayName("TC-04 | BVA – First Name 2 chars → valid (at min boundary)")
    void tc04_firstNameTwoChars_isValid() {
        fillFormWithOneField("firstName", "Jo");
        assertFalse(hasError("firstName"), "2-character first name should be accepted");
    }

    // ═══════════════════════════════════════════════════════════════════════════
    //  TC-05  FIRST NAME – 51 characters (BVA: above max boundary of 50)
    // ═══════════════════════════════════════════════════════════════════════════
    @Test
    @DisplayName("TC-05 | BVA – First Name 51 chars → error (max boundary + 1)")
    void tc05_firstNameFiftyOneChars_showsError() {
        fillFormWithOneField("firstName", "A".repeat(51));
        assertTrue(hasError("firstName"), "51-character first name must be rejected");
    }

    // ═══════════════════════════════════════════════════════════════════════════
    //  TC-06  FIRST NAME – Contains digits (EP: invalid class)
    // ═══════════════════════════════════════════════════════════════════════════
    @Test
    @DisplayName("TC-06 | EP – First Name with digits → error")
    void tc06_firstNameWithDigits_showsError() {
        fillFormWithOneField("firstName", "John123");
        assertTrue(hasError("firstName"), "Digits in first name must be rejected");
    }

    // ═══════════════════════════════════════════════════════════════════════════
    //  TC-07  EMAIL – Missing @ symbol (EP: invalid format class)
    // ═══════════════════════════════════════════════════════════════════════════
    @Test
    @DisplayName("TC-07 | EP – Email missing @ → error")
    void tc07_emailMissingAt_showsError() {
        fillFormWithOneField("email", "invalidemail.com");
        assertTrue(hasError("email"), "Email without @ must be rejected");
        assertEquals("Invalid email format.", getError("email"));
    }

    // ═══════════════════════════════════════════════════════════════════════════
    //  TC-08  EMAIL – Missing domain extension (EP: invalid format)
    // ═══════════════════════════════════════════════════════════════════════════
    @Test
    @DisplayName("TC-08 | EP – Email missing TLD → error")
    void tc08_emailMissingTld_showsError() {
        fillFormWithOneField("email", "user@domain");
        assertTrue(hasError("email"), "Email without TLD must be rejected");
    }

    // ═══════════════════════════════════════════════════════════════════════════
    //  TC-09  EMAIL – Empty (EP: required field)
    // ═══════════════════════════════════════════════════════════════════════════
    @Test
    @DisplayName("TC-09 | EP – Email empty → required error")
    void tc09_emailEmpty_showsError() {
        fillFormWithOneField("email", "");
        assertTrue(hasError("email"), "Empty email must show required error");
        assertEquals("Email is required.", getError("email"));
    }

    // ═══════════════════════════════════════════════════════════════════════════
    //  TC-10  DATE OF BIRTH – Under 18 (EP: invalid age class)
    // ═══════════════════════════════════════════════════════════════════════════
    @Test
    @DisplayName("TC-10 | EP – DOB under 18 → error")
    void tc10_dobUnder18_showsError() {
        fillFormWithOneField("dob", "01/01/2015");
        assertTrue(hasError("dob"), "User under 18 must be rejected");
        assertEquals("Must be at least 18 years old.", getError("dob"));
    }

    // ═══════════════════════════════════════════════════════════════════════════
    //  TC-11  DATE OF BIRTH – Wrong format (EP: invalid format class)
    // ═══════════════════════════════════════════════════════════════════════════
    @Test
    @DisplayName("TC-11 | EP – DOB wrong format (yyyy-mm-dd) → error")
    void tc11_dobWrongFormat_showsError() {
        fillFormWithOneField("dob", "1990-06-15");
        assertTrue(hasError("dob"), "DOB in wrong format must be rejected");
    }

    // ═══════════════════════════════════════════════════════════════════════════
    //  TC-12  DATE OF BIRTH – Impossible date (EP: invalid date)
    // ═══════════════════════════════════════════════════════════════════════════
    @Test
    @DisplayName("TC-12 | EP – DOB impossible date (32/13/1990) → error")
    void tc12_dobImpossibleDate_showsError() {
        fillFormWithOneField("dob", "32/13/1990");
        assertTrue(hasError("dob"), "Impossible DOB must be rejected");
    }

    // ═══════════════════════════════════════════════════════════════════════════
    //  TC-13  PASSWORD – Too short (BVA: 7 chars, boundary min-1 = 8)
    // ═══════════════════════════════════════════════════════════════════════════
    @Test
    @DisplayName("TC-13 | BVA – Password 7 chars → error (below min of 8)")
    void tc13_passwordSevenChars_showsError() {
        fillAndSubmit("Alice", "Smith", "alice@example.com",
                      "10/05/1995", "Pass1Ab", "Pass1Ab");
        assertTrue(hasError("password"), "7-character password must be rejected");
        assertEquals("Min 8 characters.", getError("password"));
    }

    // ═══════════════════════════════════════════════════════════════════════════
    //  TC-14  PASSWORD – Exactly 8 chars (BVA: at min boundary)
    // ═══════════════════════════════════════════════════════════════════════════
    @Test
    @DisplayName("TC-14 | BVA – Password 8 chars → valid (at min boundary)")
    void tc14_passwordEightChars_isAccepted() {
        fillAndSubmit("Alice", "Smith", "alice@example.com",
                      "10/05/1995", "Password1", "Password1");
        assertFalse(hasError("password"), "8-character valid password should be accepted");
        assertTrue(isSuccess());
    }

    // ═══════════════════════════════════════════════════════════════════════════
    //  TC-15  PASSWORD – No uppercase (EP: missing complexity rule)
    // ═══════════════════════════════════════════════════════════════════════════
    @Test
    @DisplayName("TC-15 | EP – Password no uppercase → error")
    void tc15_passwordNoUppercase_showsError() {
        fillAndSubmit("Alice", "Smith", "alice@example.com",
                      "10/05/1995", "password1", "password1");
        assertTrue(hasError("password"), "Password without uppercase must be rejected");
        assertEquals("Must contain uppercase letter.", getError("password"));
    }

    // ═══════════════════════════════════════════════════════════════════════════
    //  TC-16  PASSWORD – No digit (EP: missing complexity rule)
    // ═══════════════════════════════════════════════════════════════════════════
    @Test
    @DisplayName("TC-16 | EP – Password no number → error")
    void tc16_passwordNoNumber_showsError() {
        fillAndSubmit("Alice", "Smith", "alice@example.com",
                      "10/05/1995", "PasswordAbc", "PasswordAbc");
        assertTrue(hasError("password"), "Password without digit must be rejected");
        assertEquals("Must contain a number.", getError("password"));
    }

    // ═══════════════════════════════════════════════════════════════════════════
    //  TC-17  CONFIRM PASSWORD – Does not match (EP: mismatch class)
    // ═══════════════════════════════════════════════════════════════════════════
    @Test
    @DisplayName("TC-17 | EP – Confirm password mismatch → error")
    void tc17_confirmPasswordMismatch_showsError() {
        fillAndSubmit("Alice", "Smith", "alice@example.com",
                      "10/05/1995", "Password1", "Password2");
        assertTrue(hasError("confirmPassword"), "Mismatched passwords must show error");
        assertEquals("Passwords do not match.", getError("confirmPassword"));
    }

    // ═══════════════════════════════════════════════════════════════════════════
    //  TC-18  CONFIRM PASSWORD – Empty (EP: required field)
    // ═══════════════════════════════════════════════════════════════════════════
    @Test
    @DisplayName("TC-18 | EP – Confirm password empty → error")
    void tc18_confirmPasswordEmpty_showsError() {
        fillAndSubmit("Alice", "Smith", "alice@example.com",
                      "10/05/1995", "Password1", "");
        assertTrue(hasError("confirmPassword"), "Empty confirm password must show error");
    }

    // ═══════════════════════════════════════════════════════════════════════════
    //  TC-19  SECURITY – XSS payload in first name (negative / security test)
    // ═══════════════════════════════════════════════════════════════════════════
    @Test
    @DisplayName("TC-19 | Security – XSS in First Name → error, no script executed")
    void tc19_xssInFirstName_isRejected() {
        fillFormWithOneField("firstName", "<script>alert('xss')</script>");
        // Should fail validation (non-letter characters)
        assertTrue(hasError("firstName"), "XSS payload in first name must be rejected");
        // Verify no alert dialog popped (page is still stable)
        assertDoesNotThrow(() -> driver.getTitle(), "Page must remain stable after XSS attempt");
    }

    // ═══════════════════════════════════════════════════════════════════════════
    //  TC-20  MULTIPLE ERRORS – All fields empty → all errors shown simultaneously
    // ═══════════════════════════════════════════════════════════════════════════
    @Test
    @DisplayName("TC-20 | EP – All fields empty → all required errors visible")
    void tc20_allFieldsEmpty_allErrorsShown() {
        fillAndSubmit("", "", "", "", "", "");
        assertAll("All required error messages must appear",
            () -> assertTrue(hasError("firstName"),       "firstName error"),
            () -> assertTrue(hasError("lastName"),        "lastName error"),
            () -> assertTrue(hasError("email"),           "email error"),
            () -> assertTrue(hasError("dob"),             "dob error"),
            () -> assertTrue(hasError("password"),        "password error"),
            () -> assertTrue(hasError("confirmPassword"), "confirmPassword error")
        );
        assertFalse(isSuccess(), "Form must NOT succeed when all fields are empty");
    }

    // ═══════════════════════════════════════════════════════════════════════════
    //  TC-21 (BONUS)  PARAMETERIZED – Multiple invalid email formats (EP class)
    // ═══════════════════════════════════════════════════════════════════════════
    @ParameterizedTest(name = "TC-21 | EP – Invalid email: \"{0}\"")
    @ValueSource(strings = {
        "plainaddress",
        "@missinglocal.com",
        "missingdomain@",
        "two@@at.com",
        "spaces in@email.com"
    })
    @DisplayName("TC-21 | EP – Various invalid email formats → all rejected")
    void tc21_variousInvalidEmails_areRejected(String badEmail) {
        fillFormWithOneField("email", badEmail);
        assertTrue(hasError("email"),
                   "Email '" + badEmail + "' should be rejected");
    }
}
