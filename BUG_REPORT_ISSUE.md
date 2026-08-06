# Bug: Unit tests fail when running full Maven test suite (Java 11)

**Title:** Unit tests fail when running `mvn clean install` - multiple modules require external services or have environment-dependent failures

**Labels:** bug, tests, java11

## Description

When running the full test suite with `mvn clean install` (without `-DskipTests`), multiple tests fail. The README recommends running install without tests, but for CI or local verification it would be useful to have tests pass or be clearly skipped.

## Affected areas

### jsystemCore
- **NPE in RunnerListenersManager**: `StringUtils.getClassName(String)` receives `null` when test class name is not set → `NullPointerException`.
- **ClassSearchUtilTest**: Missing `META-INF/jsystemCore.build.properties` on classpath; test expects `IOException` when property key is missing but implementation returns `null`.
- **ScenariosManagerTest.testIsScenarioExists**: Scenario path format from `ScenarioCollector` may not match expected name (path/separator); depends on `TESTS_CLASS_FOLDER`.
- **FileUtilsTests.checkFileReplace**: `FileUtils.replaceInFile` throws "Attempt to replace text with a longer text is not supported".
- **PublishTest**: Requires local mail server (localhost:25, POP3 110).

### jsystemCommon
- **TestReportForGetCurrentTestFileName**: `HtmlReporterUtils.getCurrentTestFileName()` returns "Not Defined" when run via Maven (requires JSystem report runner context).

### jsystemAgent
- **LocalAgentTest**, **RemoteAgentClientTest**, **ExecutionClientTest**: Require Agent / RMI (e.g. "Fail to init system object: AgentConnection", "Connection refused").
- **MD5Test**, **ProjectZipTest**: NPE (e.g. array length).

### jsystemApp
- **SutFactoryTest**, **DeviceManagerTest**, **ReferenceExampleTest**, etc.: Require SUT files and device init (e.g. "Object was not found: /sut/device1/ip/text()", "Fail to init system object: device1").
- **DataSourceTests**, **ClassPathFileTest**: Missing resources or invalid commands.

## Suggested direction

- Fix or guard against null in `StringUtils.getClassName` and ensure test runner handles missing class name.
- Add `META-INF/jsystemCore.build.properties` (e.g. in `src/main/resources`) and make `ClassSearchUtil.getPropertyFromClassPath` throw `IOException` when the requested property key is missing, to match test expectations.
- Mark or skip tests that require external services (mail, Agent, RMI, SUT) with `@Ignore` and a clear reason, or exclude them in Surefire for default build.
- Document in README that full test suite requires optional services and that `mvn clean install -DskipTests` is the default supported build.

## Environment

- Branch: Java11
- Java: 11
- Maven: default
- OS: Windows (path separators may affect scenario name comparison)
