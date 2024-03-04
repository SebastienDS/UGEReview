package fr.uge.revue.util;

import org.junit.platform.launcher.listeners.TestExecutionSummary;

import java.util.List;

public record TestRunnerResult(boolean compilationError, TestExecutionSummary summary, List<String> errors) {
    public static TestRunnerResult withSuccess(TestExecutionSummary summary) {
        return new TestRunnerResult(false, summary, null);
    }

    public static TestRunnerResult withErrors(List<String> errors) {
        return new TestRunnerResult(true, null, errors);
    }

    public static TestRunnerResult withError(String error) {
        return new TestRunnerResult(true, null, List.of(error));
    }

}
