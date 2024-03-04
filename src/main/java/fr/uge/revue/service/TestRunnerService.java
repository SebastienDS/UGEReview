package fr.uge.revue.service;

import fr.uge.revue.util.JavaSourceFromString;
import fr.uge.revue.util.TestRunnerResult;
import org.junit.platform.engine.discovery.DiscoverySelectors;
import org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder;
import org.junit.platform.launcher.core.LauncherFactory;
import org.junit.platform.launcher.listeners.SummaryGeneratingListener;
import org.junit.platform.launcher.listeners.TestExecutionSummary;
import org.springframework.stereotype.Service;

import javax.tools.*;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Pattern;

@Service
public class TestRunnerService {
    private static final String CLASS_NAME_REGEX = "class (\\w+)";
    private static final Pattern CLASS_NAME_PATTERN = Pattern.compile(CLASS_NAME_REGEX);

    public TestRunnerResult launchTests(String classToTest, String testClass) {
        Objects.requireNonNull(classToTest);
        Objects.requireNonNull(testClass);
        var classToTestName = findClassname(classToTest);
        if (classToTestName.isEmpty()) return TestRunnerResult.withError("classToTest classname not found");
        var testClassName = findClassname(testClass);
        if (testClassName.isEmpty()) return TestRunnerResult.withError("testClass classname not found");

        var source = new JavaSourceFromString(classToTestName.get(), classToTest);
        var test = new JavaSourceFromString(testClassName.get(), testClass);

        var errors = compile(source, test);
        if (errors.isPresent()) return TestRunnerResult.withErrors(errors.get());

        Class<?> clazz;
        try {
            clazz = loadTestClass(testClassName.get());
        } catch (MalformedURLException | ClassNotFoundException e) {
            return TestRunnerResult.withError(e.getMessage());
        }
        var summary = launchTests(clazz);
        return TestRunnerResult.withSuccess(summary);
    }

    private static Optional<String> findClassname(String sourceCode) {
        var matcher = CLASS_NAME_PATTERN.matcher(sourceCode);
        if (!matcher.find()) return Optional.empty();
        return Optional.of(matcher.group(1));
    }

    private static TestExecutionSummary launchTests(Class<?> clazz) {
        var request = LauncherDiscoveryRequestBuilder.request()
                .selectors(DiscoverySelectors.selectClass(clazz))
                .build();
        var launcher = LauncherFactory.create();
        var listener = new SummaryGeneratingListener();

        launcher.registerTestExecutionListeners(listener);
        launcher.execute(request);
        return listener.getSummary();
    }

    public static void main(String[] args) throws ClassNotFoundException, IOException, NoSuchMethodException {
        // Code source de la classe à tester en tant que chaîne de caractères
        String classToTestCode = """
public class ClassToTest {
    public int add(int a, int b) {
        return a + b;
    }
}
""";

        // Code source de la classe de test en tant que chaîne de caractères
        String testClassCode = """
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class DynamicTest {
    @Test
    public void testAddition() {
        ClassToTest instance = new ClassToTest();
        assertEquals(4, instance.add(2, 2));
    }
    
    @Test
    public void failTest() {
        ClassToTest instance = new ClassToTest();
        assertEquals(5, instance.add(2, 2));
    }
}
""";

        var testRunnerService = new TestRunnerService();
        var result = testRunnerService.launchTests(classToTestCode, testClassCode);

//        var summary = result.summary();
//        long testFoundCount = summary.getTestsFoundCount();
//        System.out.println("testFoundCount = " + testFoundCount);
//        var failures = summary.getFailures();
//        System.out.println("getTestsSucceededCount() = " + summary.getTestsSucceededCount());
//        failures.forEach(failure -> System.out.println("failure = " + failure.getTestIdentifier() + " " + failure.getException()));
    }

    private static Optional<List<String>> compile(JavaFileObject source, JavaFileObject test) {
        var compiler = ToolProvider.getSystemJavaCompiler();
        var diagnostics = new DiagnosticCollector<JavaFileObject>();

        var compilationUnits = List.of(source, test);
        var options = List.of("-d", "./target/testRunner");

        var task = compiler.getTask(null, null, diagnostics, options, null, compilationUnits);
        var success = task.call();

        if (success) return Optional.empty();

        var errors = diagnostics.getDiagnostics()
                .stream()
                .map(Object::toString)
                .toList();
        return Optional.of(errors);
    }

    private static Class<?> loadTestClass(String classname) throws MalformedURLException, ClassNotFoundException {
        return URLClassLoader.newInstance(new URL[]  {
                Paths.get("target", "testRunner").toUri().toURL()
        }).loadClass(classname);
    }

}
