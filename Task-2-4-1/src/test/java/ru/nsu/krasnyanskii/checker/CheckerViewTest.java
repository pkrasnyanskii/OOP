package ru.nsu.krasnyanskii.checker;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("CheckerView — console output methods")
class CheckerViewTest {

    private ByteArrayOutputStream errBuf;
    private ByteArrayOutputStream outBuf;
    private CheckerView view;

    @BeforeEach
    void setUp() {
        errBuf = new ByteArrayOutputStream();
        outBuf = new ByteArrayOutputStream();
        view   = new CheckerView(new PrintStream(errBuf), new PrintStream(outBuf));
    }

    private String err() {
        return errBuf.toString();
    }

    private String out() {
        return outBuf.toString();
    }

    @Test
    @DisplayName("printBanner outputs application title")
    void printBanner() {
        view.printBanner();
        assertTrue(err().contains("OOP Checker"));
    }

    @Test
    @DisplayName("printWorkDir outputs the working directory path")
    void printWorkDir() {
        view.printWorkDir("/work/dir");
        assertTrue(err().contains("/work/dir"));
    }

    @Test
    @DisplayName("printConfigLoaded outputs student and task counts")
    void printConfigLoaded() {
        view.printConfigLoaded(5, 3);
        assertTrue(err().contains("5"));
        assertTrue(err().contains("3"));
    }

    @Test
    @DisplayName("printConfigError outputs the error message and cause")
    void printConfigError() {
        Throwable cause = new RuntimeException("inner cause");
        view.printConfigError("top-level error", cause);
        assertTrue(err().contains("top-level error"));
        assertTrue(err().contains("inner cause"));
        assertTrue(err().contains("gradlew run"));
    }

    @Test
    @DisplayName("printConfigError with null cause does not throw")
    void printConfigErrorNullCause() {
        view.printConfigError("no cause", null);
        assertTrue(err().contains("no cause"));
    }

    @Test
    @DisplayName("printReportSaved outputs the report path")
    void printReportSaved() {
        view.printReportSaved("report.html");
        assertTrue(err().contains("report.html"));
    }

    @Test
    @DisplayName("printHtml writes to stdout, not stderr")
    void printHtml() {
        view.printHtml("<html>test</html>");
        assertTrue(out().contains("<html>test</html>"));
        assertFalse(err().contains("<html>"));
    }

    @Test
    @DisplayName("printGitVersion outputs the version string")
    void printGitVersion() {
        view.printGitVersion("git version 2.40.0");
        assertTrue(err().contains("git version 2.40.0"));
    }

    @Test
    @DisplayName("warnGitNotFound outputs a WARNING")
    void warnGitNotFound() {
        view.warnGitNotFound();
        assertTrue(err().contains("WARNING"));
    }

    @Test
    @DisplayName("warnGitCheckFailed outputs the error message")
    void warnGitCheckFailed() {
        view.warnGitCheckFailed("some error");
        assertTrue(err().contains("some error"));
    }

    @Test
    @DisplayName("printGitUser outputs the configured user name")
    void printGitUser() {
        view.printGitUser("Test User");
        assertTrue(err().contains("Test User"));
    }

    @Test
    @DisplayName("warnGitUserNotSet outputs a WARNING")
    void warnGitUserNotSet() {
        view.warnGitUserNotSet();
        assertTrue(err().contains("WARNING"));
    }

    @Test
    @DisplayName("warnGitUserCheckFailed outputs the error message")
    void warnGitUserCheckFailed() {
        view.warnGitUserCheckFailed("fail msg");
        assertTrue(err().contains("fail msg"));
    }

    @Test
    @DisplayName("infoStep outputs github, taskId and step description")
    void infoStep() {
        view.infoStep("student1", "Task-1-1", "Step 1: compile");
        assertTrue(err().contains("student1"));
        assertTrue(err().contains("Task-1-1"));
        assertTrue(err().contains("Step 1: compile"));
    }

    @Test
    @DisplayName("warnStudentNotFound outputs the github login")
    void warnStudentNotFound() {
        view.warnStudentNotFound("unknown-user");
        assertTrue(err().contains("unknown-user"));
    }

    @Test
    @DisplayName("errorCloneFailed outputs github and error message")
    void errorCloneFailed() {
        view.errorCloneFailed("user1", "connection refused");
        assertTrue(err().contains("user1"));
        assertTrue(err().contains("connection refused"));
    }

    @Test
    @DisplayName("warnXmlParseFailed outputs file path and message")
    void warnXmlParseFailed() {
        view.warnXmlParseFailed("/some/file.xml", "parse error");
        assertTrue(err().contains("/some/file.xml"));
        assertTrue(err().contains("parse error"));
    }

    @Test
    @DisplayName("warnWalkFailed outputs directory and message")
    void warnWalkFailed() {
        view.warnWalkFailed("/some/dir", "access denied");
        assertTrue(err().contains("/some/dir"));
        assertTrue(err().contains("access denied"));
    }

    @Test
    @DisplayName("infoCloning outputs github and repository URL")
    void infoCloning() {
        view.infoCloning("user1", "https://github.com/user1/OOP");
        assertTrue(err().contains("user1"));
        assertTrue(err().contains("https://github.com/user1/OOP"));
    }

    @Test
    @DisplayName("infoUpdating outputs the github login")
    void infoUpdating() {
        view.infoUpdating("user1");
        assertTrue(err().contains("user1"));
    }

    @Test
    @DisplayName("warnActiveWeeksFailed outputs the error message")
    void warnActiveWeeksFailed() {
        view.warnActiveWeeksFailed("git failed");
        assertTrue(err().contains("git failed"));
    }

}
