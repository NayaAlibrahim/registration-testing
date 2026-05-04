# Registration Form – Quality Engineering Assignment

> **Course Assignment** | Unit Testing | Quality Engineering  
> **Tool Stack:** Java 17 · JUnit 5 · Selenium WebDriver 4 · Maven · GitHub Actions

---

##  Project Overview

This project implements a **complete QA test suite** for a "Create New Account" registration form.  
As quality engineers, our goal is to **break the form before users do** by covering every input field with a systematic set of automated tests.

### Fields Under Test
| Field | Rules |
|---|---|
| First Name | Required, 2–50 chars, letters only |
| Last Name | Required, 2–50 chars, letters only |
| E-mail | Required, valid format (x@y.z) |
| Date of Birth | Required, dd/mm/yyyy, age ≥ 18 |
| Password | Required, 8–64 chars, uppercase + digit |
| Confirm Password | Required, must match Password |

---

## Test Techniques Used

### Equivalence Partitioning (EP)
Each field is divided into **valid** and **invalid** equivalence classes. We test one representative from each class rather than exhaustive combinations.

**Example – Email field:**
-  Valid class: `alice@example.com`
-  Invalid class 1: missing `@` → `invalidemail.com`
-  Invalid class 2: missing domain → `user@`
-  Invalid class 3: empty string

### Boundary Value Analysis (BVA)
We test values **at, just below, and just above** boundary edges.

**Example – First Name (min=2, max=50 chars):**
| Value | Length | Expected |
|---|---|---|
| `"A"` | 1 |  Reject (min − 1) |
| `"Jo"` | 2 |  Accept (at min) |
| `"A"×50` | 50 |  Accept (at max) |
| `"A"×51` | 51 |  Reject (max + 1) |

---

##  Test Case Summary (20 Tests + Parameterized)

| TC | Field | Technique | Input | Expected |
|---|---|---|---|---|
| TC-01 | All | Happy Path | All valid data |  Success |
| TC-02 | First Name | EP | Empty |  Required error |
| TC-03 | First Name | BVA | 1 char |  Min boundary−1 |
| TC-04 | First Name | BVA | 2 chars |  Min boundary |
| TC-05 | First Name | BVA | 51 chars |  Max boundary+1 |
| TC-06 | First Name | EP | "John123" |  Digits not allowed |
| TC-07 | Email | EP | Missing @ |  Format error |
| TC-08 | Email | EP | Missing TLD |  Format error |
| TC-09 | Email | EP | Empty |  Required error |
| TC-10 | DOB | EP | Under 18 |  Age error |
| TC-11 | DOB | EP | yyyy-mm-dd format |  Format error |
| TC-12 | DOB | EP | 32/13/1990 |  Invalid date |
| TC-13 | Password | BVA | 7 chars |  Min boundary−1 |
| TC-14 | Password | BVA | 8 chars (valid) |  Min boundary |
| TC-15 | Password | EP | No uppercase |  Complexity error |
| TC-16 | Password | EP | No digit |  Complexity error |
| TC-17 | Confirm PW | EP | Mismatch |  Mismatch error |
| TC-18 | Confirm PW | EP | Empty |  Required error |
| TC-19 | First Name | Security | XSS script tag |  Rejected + stable |
| TC-20 | All fields | EP | All empty |  All 6 errors shown |
| TC-21 | Email | EP (param.) | 5 invalid formats |  All rejected |

---

##  Project Structure

```
registration-testing/
├── .github/
│   └── workflows/
│       └── ci.yml                   ← GitHub Actions CI pipeline
├── src/
│   ├── main/
│   │   └── resources/
│   │       └── registration.html    ← The page under test
│   └── test/
│       └── java/com/qa/registration/
│           ├── BaseTest.java         ← @BeforeAll, @BeforeEach, @AfterEach + helpers
│           └── RegistrationFormTest.java  ← 20+ test cases
├── pom.xml                          ← Maven dependencies
└── README.md
```

---

##  How to Run Locally

### Prerequisites
- Java 17+
- Maven 3.8+
- Google Chrome installed

### Steps

```bash
# 1. Clone the repository
git clone https://github.com/YOUR_USERNAME/registration-testing.git
cd registration-testing

# 2. Run all tests
mvn test

# 3. View the HTML report
open target/surefire-reports/index.html
```

You should see output like:
```
Tests run: 25, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
```

---

##  GitHub Actions – CI/CD Integration

Every `push` and `pull_request` to `main` automatically:

1.  Checks out the code
2.  Installs Java 17 (Temurin)
3.  Installs Google Chrome
4.  Runs `mvn test` (headless Chrome)
5.  Publishes test results as a check on the PR
6.  Uploads Surefire XML reports as a downloadable artifact

### Workflow file: `.github/workflows/ci.yml`

### Recommended Git workflow:
```bash
# Feature branch → PR → auto tests run → merge if green
git checkout -b feature/add-last-name-tests
git commit -m "feat: add boundary tests for last name"
git push origin feature/add-last-name-tests
# Open Pull Request on GitHub → CI runs automatically
```

---

##  Architecture Decisions

| Decision | Reason |
|---|---|
| **JUnit 5** | Modern Java test framework; supports `@BeforeAll`, `@BeforeEach`, `@AfterEach`, parameterized tests |
| **Selenium 4** | Industry-standard browser automation; tests real DOM behavior |
| **WebDriverManager** | Auto-downloads correct ChromeDriver version; no manual setup |
| **Headless Chrome** | Runs in CI/CD with no display; faster, less resource-heavy |
| **BaseTest class** | DRY principle; all setup/teardown in one place |
| **`@DisplayName`** | Makes test reports human-readable |
| **`assertAll()`** | Groups multiple assertions; all checked even if one fails |

---

##  Authors

- Student 1: NAIA ALIBRAHIM - 231504909
- Student 2: WIAAM ALIBRAHIM - 231504903

