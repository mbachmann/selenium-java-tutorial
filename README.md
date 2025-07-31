# Tutorial Selenium Java

https://github.com/mbachmann/selenium-java-tutorial

The solution per chapter is in the corresponding branch.

## Chapter 1: Introduction & Setup

* Set up a Maven project with the latest Selenium version and Junit 5
* Download WebDriver from https://googlechromelabs.github.io/chrome-for-testing/
* Insert SLF4J logging into the Page Object
* Create a SeleniumConfiguration and a AbstractTest class
* Write a test that opens www.duckduckgo.com

## Chapter 2: Locator Strategies

* Identify an input field using: ID, Name, CSS Selector, XPath
* Create a test class that compares all methods
* Test a form (e.g., contact form)


## Chapter 3: WebElement Interaction

* Write a test that enters a search term into DuckDuckGo
* Click on the first search result
* Use findElement() and sendKeys()
* Use Select for dropdowns
* Check validation error messages with getText()


## Chapter 4: Synchronization

* Load a website with an intentionally delayed element
* Write two tests:
* Using Thread.sleep() (dirty)
* Using WebDriverWait and ExpectedConditions (best practice)

## Chapter 5: Page Object Model

* Refactor your Google test into Page classes
* Use PageFactory for Initialization
* Reusable Methods: searchFor(String query)

## Chapter 6: Advanced Techniques

* Test a form (e.g., contact form)
* Use Select for dropdowns
* Check validation error messages with getText()
* Test accepting a JavaScript alert
* Switch to an iFrame and interact with a button
* Open a new tab/window and check content
* JavaScriptExecutor

## Chapter 7: Error Analysis & Logging

* Implement screenshots in case of errors
* Simulate an error and analyze the screenshot + log

## Chapter 8: Test Execution with Maven and Junit

* use surefire for text execution

## Chapter 9: Best Practices

* Outsource WebDriver generation
* Use properties files
* Reusable utility methods
* Use @ParameterizedTest with CSV data

## Chapter 10: Selenium Grid & Parallelization

* Start Selenium Grid via Docker
* Connect to a Chrome node via RemoteWebDriver
* Run three tests in parallel using JUnit5







