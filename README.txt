================================================================================
  AUTOMATION UTILS
  Shared Java Automation Framework Library
================================================================================

Project Name    : AutomationUtils
GroupId         : com.pksautomation.automationutils
ArtifactId      : AutomationUtils
Version         : 0.0.1-SNAPSHOT
Packaging       : jar
Java Version    : 1.8
Build Tool      : Maven 3.x
Output JAR      : target/AutomationUtils-0.0.1-SNAPSHOT.jar

Consumed by     : demo_ui_automation_suit (and other test suites)


================================================================================
1. PROJECT OVERVIEW
================================================================================

AutomationUtils is a reusable Maven library that provides the core automation
infrastructure for UI and API test suites. It is NOT a runnable test project;
it ships as a JAR dependency consumed by test suite projects.

Capabilities:
  - Selenium WebDriver automation with Healenium self-healing drivers
  - TestNG integration (base class, listeners, data providers, reporting)
  - JSON-driven reflection-based scenario execution engine
  - Page Object Model foundation with externalized JSON locators
  - Test data management (JSON, Excel, YAML, CSV, XML)
  - API testing via RestAssured with JSON schema validation
  - Database utilities (SQL/JDBC and MongoDB) via v2 DB managers
  - Healenium self-healing backend run via Docker (Infra/docker-compose.yaml)
  - ExtentReports and Allure reporting integration
  - Cucumber BDD support (optional path)


================================================================================
2. ARCHITECTURE DESIGN
================================================================================

2.1 HIGH-LEVEL ARCHITECTURE
---------------------------

  +----------------------------------------------------------+
  |              Consumer Test Suite Projects                 |
  |  (demo_ui_automation_suit, other suites)                 |
  |                                                          |
  |  ScenariosRunner extends TestBase                        |
  |  Page Objects extend BasePage                            |
  |  JSON scenarios + Excel registry + config.properties     |
  +---------------------------+------------------------------+
                              |
                              v
  +----------------------------------------------------------+
  |              Scenario Execution Layer                     |
  |                                                          |
  |  TestScenarioExecuter                                    |
  |    - Parses JSON scenario files                          |
  |    - Loads POM classes via Class.forName()               |
  |    - Invokes step methods via Method.invoke()            |
  |    - Caches page object instances per scenario           |
  +---------------------------+------------------------------+
                              |
                              v
  +----------------------------------------------------------+
  |              Test Context Layer (ThreadLocal)             |
  |                                                          |
  |  Config (per-thread)                                     |
  |    - Runtime properties (config.properties)              |
  |    - SelfHealingDriver reference                         |
  |    - SoftAssert, ExtentTest, test log buffer             |
  |    - UtilityObjectManager (composition root)             |
  |                                                          |
  |  ConfigSingleton (JVM-wide)                              |
  |    - Global test data maps                               |
  |    - Page locator cache                                  |
  |    - DB connection pool                                  |
  +---------------------------+------------------------------+
                              |
                              v
  +----------------------------------------------------------+
  |              Utility Layer                                |
  |                                                          |
  |  BrowserUtils      WebDriver lifecycle, navigation       |
  |  WaitHelper        Explicit/fluent waits                 |
  |  ElementActionsUtils  Click, fill, select, keyboard      |
  |  AssertionUtils    Hard/soft assertions with logging     |
  |  LoggerUtils       Console + Extent + Cucumber logging   |
  |  TestDataHelper    Named dataset resolution              |
  |  PageLocatorHelper JSON locator loading and By resolution|
  |  APIHelper         RestAssured HTTP calls                |
  |  EncryptionUtils   Credential encryption/decryption      |
  |  PopupUtils        Alert/confirm dialog handling         |
  +---------------------------+------------------------------+
                              |
                              v
  +----------------------------------------------------------+
  |              Infrastructure Layer                         |
  |                                                          |
  |  Selenium 3.x + Healenium + WebDriverManager             |
  |  TestNG 6.14.3 + ExtentReports 3.x + Allure              |
  |  RestAssured + Jackson + org.json                        |
  |  Apache POI (Excel) + SnakeYAML + OpenCSV                |
  |  MySQL / PostgreSQL / Redshift / MongoDB drivers         |
  +---------------------------+------------------------------+
                              |
                              v
  +----------------------------------------------------------+
  |              Healenium Backend (Docker)                   |
  |              Infra/docker-compose.yaml                    |
  |                                                          |
  |  hlm-backend:3.1.5         (port 7878, Postgres-backed)  |
  |  hlm-selector-imitator:1   (port 8000)                   |
  |                                                          |
  |  Stores healed locators; SelfHealingDriver calls this    |
  |  service when an element is not found by its locator.     |
  +----------------------------------------------------------+


2.2 CORE DESIGN PATTERNS
-------------------------

  Composition Root (UtilityObjectManager)
    Config creates and wires all utility instances for a test thread.
    Page objects access utilities through BasePage, not directly.

  ThreadLocal Context (Config)
    Each TestNG test method gets its own Config instance stored in
    ThreadLocal. Enables safe parallel execution without shared state.

  Singleton Shared State (ConfigSingleton)
    JVM-wide cache for locators, global test data, and DB connections.
    Avoids reloading JSON locator files on every page object creation.

  Template Method (BasePage)
    BasePage defines the skeleton for page interactions.
    Consumer page objects inherit click, fill, assert, log helpers.

  Strategy Pattern (PageLocatorHelper)
    Locator strategy (xpath, id, css, name) is defined in JSON.
    PageLocatorHelper resolves strategy + value to Selenium By objects.

  Reflection-Based Command Pattern (TestScenarioExecuter)
    JSON steps are commands: {ClassName, MethodName, TestData}.
    Executor dispatches commands to page object methods at runtime.

  Factory Pattern (BrowserUtils.openBrowser)
    Creates appropriate WebDriver based on config (local/remote/browser type).
    Wraps delegate driver in Healenium SelfHealingDriver.


2.3 PACKAGE STRUCTURE
---------------------

  com.pksautomation.utils.v2              Primary framework API (use this)
  com.pksautomation.utils.v2.reflections  JSON scenario executor
  com.pksautomation.utils.v2.testNG       TestNG base class and listener
  com.pksautomation.utils.v2.cucumber       BDD / Cucumber integration
  com.pksautomation.utils.v2.dataHelper     Test data and locator helpers
  com.pksautomation.utils.v2.fileutils      File I/O (JSON, Excel, YAML, etc.)
  com.pksautomation.utils.v2.dbconnection   Database managers
  com.pksautomation.utils.v2.customexception Custom exception types

  com.pksautomation.utils.v2.dbconnection   Database managers (SQL + Mongo)

  pojo                                      Shared locator model (How)
  enums                                     API method type enums
  filehandling                              Standalone JSON utilities

  NOTE: The entire legacy v1 package com.pksautomation.utils.* (Browser,
  Element, Config, Log, the dbconnection.* drivers, report.* classes, etc.)
  has been removed. All framework functionality now lives exclusively under
  com.pksautomation.utils.v2.*. The database enums (DatabaseType,
  MongoDataBaseType, DataBaseCategory) live in the v2 dbconnection package
  as com.pksautomation.utils.v2.dbconnection.DataBaseEnumConstants.


2.4 API NOTE (v2 only)
-----------------------

  The library now exposes a single generation of utilities:

  v2 (com.pksautomation.utils.v2.*)
    The one and only API. Config-centric, ThreadLocal, UtilityObjectManager
    pattern. All development and consumer projects use v2.

  The legacy v1 package (com.pksautomation.utils.*) with its monolithic
  helpers (Browser, Element, Config, Log) has been fully removed. v2 classes
  are self-contained and carry no v1 dependencies. (For example, BrowserUtils
  compresses screenshots with a standalone ImageIO routine rather than the
  old Browser.MyImageWriteParam helper.)


2.5 EXECUTION FLOW DIAGRAMS
---------------------------

  (A) END-TO-END TEST RUN (TestNG + JSON Reflection)

      mvn test
         |
         v
   +--------------------+
   | testng.xml         |   selects consumer test runner (extends TestBase)
   +---------+----------+
             |
             v
   +------------------------------+
   | @BeforeTest startReport()    |  build ExtentReports + HTML reporter
   +---------+--------------------+
             |
             v
   +------------------------------+
   | DataProvider                 |  read ScenarioDetails.xlsx ->
   | (ScenariosRunner / testData) |  list of enabled scenario rows
   +---------+--------------------+
             |
             | one invocation per scenario row (may run in parallel)
             v
   +------------------------------+
   | @BeforeMethod                |  new Config(method)
   |                              |  -> load defaultConfig + config.properties
   |                              |  -> Config.threadLocalConfig.set(config)
   |                              |  -> create ExtentTest node
   +---------+--------------------+
             |
             v
   +------------------------------+
   | @Test scenarioRunner(row)    |
   |   BrowserUtils.openBrowser() |  WebDriver -> wrapped in SelfHealingDriver
   |   executeScenarioFromJson()  |  (see diagram B)
   +---------+--------------------+
             |
             v
   +------------------------------+
   | @AfterMethod tearDown()      |  screenshot on FAIL, write test log,
   |                              |  quitBrowser(), update Extent status
   +---------+--------------------+
             |
             v
   +------------------------------+
   | TestListener.afterInvocation |  softAssert.assertAll()
   +---------+--------------------+
             |
             v
   +------------------------------+
   | @AfterTest tearDown()        |  extentReport.flush() -> HTML report
   +------------------------------+


  (B) JSON REFLECTION SCENARIO EXECUTION (TestScenarioExecuter)

   executeScenarioFromJsonFile(jsonPath)
         |
         v
   parse JSON  ->  { TestCaseId, FeatureName, Package, Steps[] }
         |
         v
   assign ExtentReports category = FeatureName
         |
         v
   for each step { ClassName, MethodName, TestData }:
         |
         |--> TestData not empty?  --yes--> config.putRunTimeProperty(
         |                                     "TestDataName", TestData)
         |
         v
   class cached in classHashMap?
         |                         \
        no                          yes
         |                            \
         v                             v
   Class.forName(Package+"."+      reuse cached instance
   ClassName); newInstance();      (1 object per class per scenario)
   cache in classHashMap
         |                            /
         +--------------+-------------+
                        |
                        v
            method.invoke(instance)   (page object extends BasePage)
                        |
            +-----------+-----------+
            |                       |
         success                exception
            |                       |
            v                       v
       next step           LoggerUtils.logException(); break loop
                                    |
                                    v
                        @AfterMethod marks test FAILED


  (C) PAGE OBJECT + UTILITY WIRING (per scenario thread)

   Config (ThreadLocal)
      |
      |-- ConfigSingleton (JVM-wide cache: locators, global data, DB conns)
      |
      |-- UtilityObjectManager (composition root)
      |        |-- BrowserUtils      (driver lifecycle, screenshots)
      |        |-- WaitHelper        (explicit / fluent waits)
      |        |-- AssertionUtils    (hard/soft asserts + logging)
      |        |-- TestDataHelper     (named dataset resolution)
      |        |-- EncryptionUtils   (credential encrypt/decrypt)
      |        +-- APIHelper         (RestAssured calls)
      |
      v
   BasePage (extends PageLocatorHelper)
      |   on construct: wire utilities, load {ClassName}.json locators,
      |                 PageFactory.initElements(driver, this)
      v
   Consumer Page Object (LoginPage, etc.)
      step methods call delegated helpers: clickOnButton(), fillData(),
      assertEquals(), getTestData(), logComment() ...


  (D) SELF-HEALING LOCATOR FLOW (Healenium)

   Page step requests element by locator (How: strategy + value)
         |
         v
   SelfHealingDriver.findElement(by)
         |
         |-- found ------------------> return WebElement
         |
         +-- NOT found
                 |
                 v
         call Healenium backend (Infra/docker-compose.yaml, port 7878)
                 |
                 v
         selector-imitator (port 8000) scores candidate elements
                 |
                 v
         best match found?  --yes--> heal locator, persist, return element
                 |
                 no
                 v
         throw NoSuchElementException -> step fails -> logged + screenshot

================================================================================
3. DIRECTORY STRUCTURE
================================================================================

automationutils/
|
|-- pom.xml
|-- README.txt
|
|-- Infra/
|   +-- docker-compose.yaml                Healenium self-healing backend
|
|-- src/main/java/
|   |-- com/pksautomation/utils/v2/        Primary framework (v2)
|   |   |-- BasePage.java                  Page Object foundation
|   |   |-- BrowserUtils.java              WebDriver lifecycle
|   |   |-- Config.java                    ThreadLocal test context
|   |   |-- ConfigSingleton.java           JVM-wide shared state
|   |   |-- LoggerUtils.java               Unified logging
|   |   |-- WaitHelper.java                Selenium wait utilities
|   |   |-- ElementActionsUtils.java       UI interaction actions
|   |   |-- AssertionUtils.java            Hard/soft assertions
|   |   |-- PopupUtils.java                Alert handling
|   |   |-- EncryptionUtils.java           Credential encryption
|   |   |-- Helper.java                    General utilities
|   |   |-- UtilityObjectManager.java      Composition root
|   |   |-- APIHelper.java                 RestAssured HTTP client
|   |   |-- reflections/
|   |   |   +-- TestScenarioExecuter.java  JSON reflection engine
|   |   |-- testNG/
|   |   |   |-- TestBase.java              TestNG base class
|   |   |   +-- TestListener.java          TestNG event listener
|   |   |-- cucumber/
|   |   |   |-- CommonTestBase.java        Cucumber base
|   |   |   |-- TestContext.java           Cucumber scenario context
|   |   |   +-- TestNGCucumberTests.java   TestNG-Cucumber bridge
|   |   |-- dataHelper/
|   |   |   |-- TestDataHelper.java        Named dataset resolver
|   |   |   |-- PageLocatorHelper.java     JSON locator loader
|   |   |   +-- ExcelDataReader.java       Excel data reader
|   |   |-- fileutils/
|   |   |   |-- JSONUtils.java, ExcelUtils.java, YamlUtils.java
|   |   |   |-- CSVUtils.java, XMLUtils.java, TextFileUtils.java
|   |   |   +-- ...
|   |   |-- dbconnection/
|   |   |   |-- DBManager.java             DB manager interface
|   |   |   |-- SQLDBManager.java          SQL database manager
|   |   |   |-- MongoDBManager.java        MongoDB manager
|   |   |   +-- DataBaseEnumConstants.java DB type / category enums
|   |   +-- customexception/
|   |       +-- CustomRuntimeException.java
|   |
|   |-- pojo/How.java                      Locator POJO (Strategy, Value)
|   |-- enums/APIMethodType.java
|   +-- filehandling/JSONUtils.java        Standalone JSON utility
|
|-- src/test/
|   |-- java/com/pksautomation/commonutilty/  Sample/demo code
|   +-- resources/
|       |-- config/config.properties        Default config template
|       |-- PythonFile/EncryptCreds.py      Credential encryption script
|       +-- reportGenerator/Template.html   Extent report template
|
+-- target/
    +-- AutomationUtils-0.0.1-SNAPSHOT.jar  Built output


================================================================================
4. KEY COMPONENTS
================================================================================

4.1 Config (com.pksautomation.utils.v2.Config)
----------------------------------------------
  Central test execution context, one instance per test thread.

  Responsibilities:
    - Load defaultConfig.properties (classpath) + project config.properties
    - Store runtime properties with {$key} token substitution
    - Hold SelfHealingDriver, SoftAssert, ExtentTest, test log buffer
    - Provide access to UtilityObjectManager
    - loadTestData() populates global/scenario test data maps

  Access: Config.getConfig()  (reads from ThreadLocal)


4.2 ConfigSingleton
--------------------
  JVM-wide singleton for cross-scenario shared state:
    - Browser settings and encryption keys
    - Global test data map (all loaded JSON datasets)
    - Page locator cache: Map<String, Map<String, How>>
    - Database connection references


4.3 UtilityObjectManager
-------------------------
  Composition root that creates and wires per-test utility instances:

    BrowserUtils         WebDriver open/close, navigation, screenshots
    WaitHelper           Explicit and fluent Selenium waits
    TestDataHelper       Resolves field values from named datasets
    AssertionUtils       Hard and soft assertions with integrated logging
    EncryptionUtils      Credential encryption and decryption
    APIHelper            RestAssured HTTP calls and schema validation


4.4 BasePage
-------------
  Abstract foundation for all consumer page objects.

  On construction:
    1. Reads Config from ThreadLocal
    2. Creates UtilityObjectManager, LoggerUtils, AssertionUtils,
       ElementActionsUtils, TestDataHelper
    3. Loads {ClassName}.json locators via PageLocatorHelper
    4. Calls PageFactory.initElements(driver, this)

  Provides delegated methods:
    clickOnButton(), fillData(), selectFromDropdown()
    assertTrue(), assertEquals(), softAssertAll()
    logComment(), logException()
    getTestData(), getScenarioContext()
    navigateToURL(), getDriver()


4.5 BrowserUtils
-----------------
  WebDriver lifecycle and browser operations.

  Key methods:
    openBrowser()          Launch browser (local via WebDriverManager or remote Grid)
    quitBrowser()          Close driver with retry logic
    navigateToLoginPage()  Navigate to {Environment}/login
    takeScreenshot()       Capture and optionally compress screenshot
    executeJavaScript()    Run JS in browser context

  Wraps delegate WebDriver in Healenium SelfHealingDriver for
  automatic locator self-healing on element-not-found errors.


4.6 TestScenarioExecuter (com.pksautomation.utils.v2.reflections)
-------------------------------------------------------------------
  JSON-driven reflection engine. Core of the data-driven test design.

  Entry point:
    executeScenarioFromJsonFile(String jsonFilePath)

  Execution algorithm:
    1. Parse JSON with JSONParser; extract Package, FeatureName, Steps array
    2. Set ExtentReports category from FeatureName
    3. For each step in Steps:
         a. If TestData is non-empty:
              config.putRunTimeProperty("TestDataName", testData)
         b. classHashMap lookup or Class.forName(package + "." + className)
         c. Cache instance: one object per class name per scenario run
         d. getMethod(methodName).invoke(classObject)
         e. On exception: log via LoggerUtils, break step loop
    4. Return when all steps complete or first failure occurs

  Page object contract for reflection:
    - Must extend BasePage
    - Must have public no-arg constructor calling super()
    - Step methods must be public, no-arg, return type any (often `this`)


4.7 TestBase (com.pksautomation.utils.v2.testNG.TestBase)
----------------------------------------------------------
  TestNG base class for consumer test runners.

  Data providers:
    "ScenariosRunner"  Reads ScenarioDetails.xlsx; returns enabled scenario rows
    "testData"           Feature/suite-filtered scenario list
    "GetTestConfig"    Injects Config instance per test method

  Lifecycle hooks:
    @BeforeTest   startReport()         Initialize ExtentReports
    @BeforeMethod BeforeMethod()       Create ThreadLocal Config + ExtentTest
    @AfterMethod  getResult()           Capture screenshot, attach logs
    @AfterMethod  tearDown()            Quit browser, flush report, cleanup

  Implements ITest for dynamic test naming in reports.


4.8 TestListener
-----------------
  TestNG listener (ITestListener + IInvokedMethodListener).

  On test failure  : log exception, attach Allure logs, mark result false
  On test success  : end test, attach execution logs
  afterInvocation  : run softAssert.assertAll() to surface soft assertion failures


4.9 LoggerUtils
----------------
  Unified logging to multiple sinks:

    [Pass]      Passed step confirmation
    [Fail]      Failed assertion or action
    [INFO]      Informational comment
    [WARNING]   Non-fatal warning
    [Exception] Exception with stack trace

  Outputs to: stdout, in-memory test log, ExtentReports, Cucumber Scenario.write()
  Attaches screenshots on failure when enableScreenshot=true.


4.10 PageLocatorHelper
-----------------------
  Loads and caches page locator JSON files.

  File naming: {PageClassName}.json in PageLocatorFilePath directory
  Cache key  : class name string in ConfigSingleton locator map
  Resolution : How.Strategy + How.Value -> Selenium By object

  Supported strategies: xpath, id, css, name, linkText, partialLinkText,
                        className, tagName


4.11 TestDataHelper
--------------------
  Resolves test input values from named datasets.

  Flow:
    1. Step sets runtime property TestDataName = "Credential"
    2. Page method calls getTestData("Email")
    3. TestDataHelper looks up "Credential" map in global test data
    4. Returns value for key "Email"

  Supports scenario-level and global-level test data maps.


================================================================================
5. REFLECTION-BASED SCENARIO EXECUTION (DETAILED)
================================================================================

5.1 JSON Scenario Schema
-------------------------

  {
    "TestCaseId":  "testScenarioID1",
    "Description": "Executing Test Scenario with ID as 1",
    "FeatureName": "Successful_Login",
    "Package":     "com.pksautomation.uidemo.pom",
    "Steps": [
      {
        "ClassName":  "LoginPage",
        "MethodName": "_navigate_to_the_login_page",
        "TestData":   "Credential"
      },
      {
        "ClassName":  "LoginPage",
        "MethodName": "_fillUserCredential",
        "TestData":   ""
      }
    ]
  }


5.2 Field Reference
--------------------

  Field        Type     Purpose
  -----------  -------  --------------------------------------------------
  TestCaseId   String   Unique scenario identifier (matches JSON filename)
  Description  String   Human-readable scenario description
  FeatureName  String   ExtentReports category label
  Package      String   Java package prefix for page object classes
  Steps        Array    Ordered list of step commands

  Steps[].ClassName   Page object class name (without package)
  Steps[].MethodName  Public no-arg method to invoke
  Steps[].TestData    Named dataset key (empty string = no data binding)


5.3 Instance Caching
---------------------
  TestScenarioExecuter maintains:
    HashMap<String, SingletonMap<Class<?>, Object>> classHashMap

  One page object instance is created per ClassName per scenario run.
  This supports fluent method chaining where step N returns `this`
  and step N+1 operates on the same object state.


5.4 Error Handling
-------------------
  If a step throws an exception:
    - LoggerUtils logs the exception with stack trace
    - Remaining steps in the scenario are skipped (loop breaks)
    - TestBase.@AfterMethod captures screenshot and marks test failed
    - Soft assertions accumulated during the scenario are flushed


================================================================================
6. TESTNG LIFECYCLE
================================================================================

  @BeforeTest  (once per suite)
    startReport()
      -> Initialize ExtentReports with configured ResultsDir
      -> Load extent-config.xml if reportConfigPath is set

  @BeforeMethod (once per test method / scenario)
    BeforeMethod(method, testParameters, context)
      -> Create new Config instance for this thread
      -> Set ThreadLocal: Config.threadLocalConfig.set(config)
      -> Create ExtentTest node for this scenario
      -> Set dynamic test name from data provider row

  @Test
    scenarioRunner(testData)
      -> BrowserUtils.openBrowser()
      -> TestScenarioExecuter.executeScenarioFromJsonFile(jsonPath)

  @AfterMethod
    getResult(method, testParameters, context, result)
      -> Attach screenshot if test failed
      -> Write test log to ExtentReports
    tearDown(result)
      -> BrowserUtils.quitBrowser()
      -> Flush ExtentReports
      -> Clear ThreadLocal Config

  TestListener.afterInvocation
    -> softAssert.assertAll()  (surfaces soft assertion failures)


================================================================================
7. CONFIGURATION MODEL
================================================================================

  Consumer projects must provide:

  Resource                                          Purpose
  ------------------------------------------------  --------------------------
  src/test/resources/config/config.properties       Browser, URLs, timeouts
  src/test/resources/TestData/ScenarioDetails.xlsx  Scenario registry (Excel)
  src/test/resources/TestData/ScenariosFiles/       Reflection scenario JSON
  src/test/resources/TestData/PageLocatorsFile/     Element locator JSON
  src/test/resources/TestData/ (JSON folders)       Named test data datasets

  Key config.properties properties:

  Property              Default / Example              Purpose
  --------------------  -----------------------------  -------------------------
  Browser               chrome                         Browser type
  RemoteExecution       false                          Use Selenium Grid
  ObjectWaitTime        20                             Wait timeout (seconds)
  Environment           https://example.com            Base application URL
  isHeadlessMode        false                          Headless browser mode
  PageLocatorFilePath   /src/test/resources/.../       Locator JSON directory
  TestDataJSONPath      src/test/resources/.../        Test data JSON directory
  ExtentReportEnable    True                           Enable HTML reporting
  ResultsDir            test-output/Report             Report output directory
  enableScreenshot      false                          Capture screenshot on fail

  System property overrides (runtime):
    -DBrowser=firefox
    -DisHeadlessMode=true
    -DlogsMode=true
    -DEnvironment=https://staging.example.com


================================================================================
8. LOCAL INFRASTRUCTURE (HEALENIUM SELF-HEALING)
================================================================================

  The framework wraps every WebDriver in a Healenium SelfHealingDriver
  (see BrowserUtils). When a locator no longer matches an element,
  Healenium asks its backend service for the closest matching element and
  "heals" the locator instead of failing immediately. This backend must be
  running for self-healing to work.

  Infra/docker-compose.yaml defines two services:

  Service               Image                          Port    Purpose
  --------------------  -----------------------------  ------  ------------------
  healenium             healenium/hlm-backend:3.1.5    7878    Stores and serves
                                                              healed locators
  selector-imitator     healenium/hlm-selector-          8000    Scores candidate
                        imitator:1                            elements for heal

  The backend persists data in PostgreSQL (schema "healenium"). The compose
  file expects a reachable Postgres at host.docker.internal:5432 with:
    SPRING_POSTGRES_DB=healenium
    SPRING_POSTGRES_USER / SPRING_POSTGRES_PASSWORD  (see compose file)

  Start the infrastructure:

    cd Infra
    docker-compose up -d          # start healenium + selector-imitator
    docker-compose ps             # verify both containers are healthy
    docker-compose down           # stop when finished

  If the backend is not running, tests still execute but locators will not
  self-heal; an unresolved locator results in a normal NoSuchElementException.


================================================================================
9. DEPENDENCIES
================================================================================

  Category          Libraries
  ----------------  -------------------------------------------------------
  Selenium          selenium-java 3.14.0, selenium-chrome/support/api 3.141.59
                    htmlunit-driver, selenium-server, operadriver
  Self-healing      healenium-web 3.1.6
  WebDriver mgmt    webdrivermanager 5.0.3
  TestNG            testng 6.14.3, reportng 1.1.4
  Cucumber          cucumber-java/core/java8/jvm/testng 4.2.3
                    cucumber-reporting 4.2.3, cucumber-picocontainer 4.2.3
  REST / API        rest-assured 2.9.0 + 3.0.0, json-path, xml-path
                    json-schema-validator 1.0.42, okhttp 4.9.x
  Reporting         extentreports 3.1.5, allure-java-annotations 1.5.4
  JSON / Data       jackson-core/databind 2.6.3, json-simple, org.json
  File I/O          apache poi 3.17, commons-io 2.6, snakeyaml 1.24, opencsv 5.6
  Databases         mysql-connector 5.1.6, postgresql 9.4
                    redshift-jdbc42 2.0.0.5, mongodb-driver 3.2.2
  Other             guava 25.0, assertj 3.11.1, jython-standalone 2.7.2
                    browsermob-proxy, jsch, pdfbox, appium java-client 4.1.2


================================================================================
10. BUILD COMMANDS
================================================================================

  From automationutils project root:

  Build and install to local Maven repository (~/.m2):
    mvn clean install

  Build JAR only (skip tests):
    mvn clean package -DskipTests

  Compile main sources only:
    mvn clean compile

  Output JAR:
    target/AutomationUtils-0.0.1-SNAPSHOT.jar


================================================================================
11. CONSUMER INTEGRATION (demo_ui_automation_suit)
================================================================================

  The consumer project references AutomationUtils in its pom.xml:

    <dependency>
      <groupId>com.pksautomation.automationutils</groupId>
      <artifactId>AutomationUtils</artifactId>
      <version>0.0.1-SNAPSHOT</version>
    </dependency>

  During Maven validate phase, the consumer automatically installs the
  built JAR and POM from the sibling project:

    ../automationutils/target/AutomationUtils-0.0.1-SNAPSHOT.jar
    ../automationutils/pom.xml

  This ensures transitive dependencies are resolved without manual install.

  Typical development workflow:

    # 1. Make changes to AutomationUtils
    cd /path/to/automationutils
    mvn clean install

    # 2. Run consumer test suite (validate phase picks up latest JAR)
    cd /path/to/demo_ui_automation_suit
    mvn clean test


================================================================================
12. EXTENDING THE FRAMEWORK
================================================================================

  Adding support for a new consumer test suite:

  1. Create a new Maven project with AutomationUtils as a dependency
  2. Create page object classes extending BasePage in your own package
  3. Create JSON locator files in PageLocatorsFile/ (one per page class)
  4. Create JSON scenario files in ScenariosFiles/
  5. Create ScenarioTestData.json with named input datasets
  6. Create ScenarioDetails.xlsx to register enabled scenarios
  7. Create config.properties with Environment, Browser, paths
  8. Create a test runner class extending TestBase with @Test method
     calling TestScenarioExecuter.executeScenarioFromJsonFile()
  9. Configure testng.xml to run your test runner class

  Adding a new utility to AutomationUtils:

  1. Create the utility class in com.pksautomation.utils.v2/
  2. Register it in UtilityObjectManager if it should be per-test
  3. Expose access via BasePage delegate method if page objects need it
  4. Rebuild: mvn clean install
  5. Consumer projects pick up changes on next mvn validate/test


================================================================================
13. REPORTING
================================================================================

  ExtentReports (primary):
    Built in TestBase.startReport() (@BeforeTest) and flushed in @AfterTest.
    HTML output: ${user.dir}/ExtendReport/testReport.html
    Theme DARK; report/system-info values (project, branch, environment) are
    currently hardcoded in TestBase.startReport().
    Includes: step logs, screenshots, pass/fail status, feature categories

    NOTE: The output path is hardcoded to ExtendReport/testReport.html in
    TestBase rather than derived from the ResultsDir config property. Adjust
    TestBase.startReport() if a config-driven path is required.

  TestNG reports:
    Output directory: test-output/
    Includes: emailable-report.html, junitreports/, index.html

  Allure (optional):
    Annotations supported via allure-java-annotations dependency
    Requires Allure plugin configuration in consumer pom.xml

  Per-test log files:
    Written to test-output/Report/ReportsLogs/ during execution


================================================================================
  END OF README
================================================================================
