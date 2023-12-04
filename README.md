# Project Title
# Introduction

This is a Selenium Java project created for DEMO purposes. 
This framework is designed for creation of GUI and API test cases. TestNG library is used for testing.
RestAssured library is used for API testing.

# Getting Started
# Prerequisites
Tools, libraries, and environment setup required to run this project.

Java JDK (version 1.8 or higher)

Maven (for dependency management and project build)

Browser drivers (ChromeDriver for Google Chrome and GeckoDriver for Firefox)

RestAssured (for API queries)

An IDE of your choice (e.g., IntelliJ IDEA, Eclipse)

TestRail is used as a test management tool. By default update is set as false.
For updating TestRail cases set property. See example in Usage section of this document.

# Installation
Steps description for installation and set up the project. For example:

1. Clone the repository:
bash
git clone https://github.com/gorancelic/myDemoProjectQA.git
2. Navigate to the project directory:
bash
cd your-repo-name
3. Install dependencies using Maven:
mvn clean install

# Usage
Commands for running specific tests or test suites. For example:

To run all tests(bash):
mvn test

To run a specific test suite(bash):
mvn test '-DsuiteXmlFile=src/test/resources/TestSuites/ui/SearchSuite.xml'

To run a specific test suite with TestRail update next example could be used:
mvn test '-DsuiteXmlFile=src/test/resources/TestSuites/FullSuite.xml' -DupdateTestRail=true

# Structure
Describe the structure of your project. Mention how your tests are organized, 
and how the TestNG and RestAssured libraries are utilized.

# Framework structure tree:
```plaintext
src
│
├── main
│   ├── java
│   │   └── com.myapp.example
│   │          ├── base (BaseTest, BaseTestGui, BrowserDriverFactory, CsvDataProviders, 
│   │          │          EnvironmentConfig, TestListener, TestUtilities, TestRailAPI) 
│   │          └── pages (all GUI pages for SUT)
│   │   
│   └── resources
│       ├── config.properties (environments details)
│       ├── chromedriver.exe
│       ├── geckodriver.exe
│       └── log4j2.xml
│
└── test
│   ├── java
│   │   └── com.myapp.example
│   │          ├── api (All API tests)
│   │          └── ui (All UI tests)
│   │   
│   └── resources
│       ├── dataproviders (test data section)
│       └── TestSuites
│           ├── api
│           │── ui
│           └── FullSuite.xml
└── logs
target
│
└── test-output (this is folder designed for reports. 
       │            This can be changed in pom.xml file under plugin: [maven-surefire-plugin] )
       └── screenshots
```
# Contributing
Use common test naming conventions and best java coding practices stated in: [https://www.oracle.com/technetwork/java/codeconventions-150003.pdf]

# Copyright [2023] [Goran Celic]

Licensed under the [no license] 
Contact [0658989856]
Provide your contact information or other ways to get in touch regarding the project.
[celicgoran@yahoo.com]
