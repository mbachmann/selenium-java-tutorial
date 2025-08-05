# Tutorial Selenium Java

https://github.com/mbachmann/selenium-java-tutorial

The solution per chapter is in the corresponding branch.

# Part 1

## Chapter 1: Introduction & Setup

* Set up a Maven project with the latest Selenium version and Junit 5
* Download WebDriver from https://googlechromelabs.github.io/chrome-for-testing/
* Insert SLF4J logging into the Page Object
* Create a SeleniumConfiguration and a AbstractTest class
* Write a test that opens www.duckduckgo.com


## Chapter 2: Locator Strategies

* Identify an input field using: ID, Name, CSS Selector, XPath
* Create a test class that compares all methods
* Test a form (e.g., login form)


## Chapter 3: WebElement Interaction

* Write a test that enters a search term into DuckDuckGo
* Click on the first search result
* Use findElement() and sendKeys()
* Check validation error messages with getText()
* Use Select for dropdowns
* Check validation error messages with getText()
* Test accepting a JavaScript alert
* Switch to an iFrame and interact with a button
* Open a new tab/window and check content
* JavaScriptExecutor

# Part 2

## Chapter 4: Synchronization

* Load a website with an intentionally delayed element
* Write two tests:
* task01: Using WebDriverWait and ExpectedConditions
* task02: Using FluentWait and ExpectedConditions 


## Chapter 5: Page Object Model

* task03: Refactor your Login test into LoginPage classes
* task04: Refactor your Login test into LoginPage and BasePage classes
* task05: Use PageFactory for Initialization with @FindBy


## Chapter 6: Test data management and test data driven testing

* task06: Use csv file for @ParameterizedTest with @CsvFileSource
* task07: Use data sample for @ParameterizedTest with @CsvSource
* task08: use JavaFaker to produce test data

## Chapter 7: File up- and download, Error Analysis & Logging

* task09: Test File upload
* task10: Test File download
* task11: test screenshots in case of errors and simulate an error and analyze the screenshot + log

## Chapter 8: Test Execution with Maven and Junit, implement Reporting

* task12: use surefire for text execution


## Chapter 9: Assertions and Validation

* Use different Assertion Methods


## Chapter 10: Selenium Grid & Parallelization

* Start Selenium Grid via Docker
* Connect to a Chrome node via RemoteWebDriver
* Run three tests in parallel using JUnit5







