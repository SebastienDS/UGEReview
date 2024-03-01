package fr.uge.revue.service;

import fr.uge.revue.util.JavaSourceFromString;
import org.junit.platform.engine.discovery.DiscoverySelectors;
import org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder;
import org.junit.platform.launcher.core.LauncherFactory;
import org.junit.platform.launcher.listeners.SummaryGeneratingListener;
import org.junit.platform.launcher.listeners.TestExecutionSummary;
import org.springframework.stereotype.Service;

import javax.tools.*;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

@Service
public class TestRunnerService {
    private static final String CLASS_NAME_REGEX = "class (\\w+)";
    private static final Pattern CLASS_NAME_PATTERN = Pattern.compile(CLASS_NAME_REGEX);

    public record Result(TestExecutionSummary summary) {}

    public Result launchTests(String classToTest, String testClass) throws IOException, ClassNotFoundException {
        var source = new JavaSourceFromString(findClassname(classToTest).orElseThrow(ClassNotFoundException::new), classToTest);
        var test = new JavaSourceFromString(findClassname(testClass).orElseThrow(ClassNotFoundException::new), testClass);

        var clazz = compileAndLoadTestClass(source, test).orElseThrow();
        return launchTests(clazz);
    }

    private static Optional<String> findClassname(String sourceCode) {
        var matcher = CLASS_NAME_PATTERN.matcher(sourceCode);
        if (!matcher.find()) return Optional.empty();
        return Optional.of(matcher.group(1));
    }

    private static Result launchTests(Class<?> clazz) {
        var request = LauncherDiscoveryRequestBuilder.request()
                .selectors(DiscoverySelectors.selectClass(clazz))
                .build();
        var launcher = LauncherFactory.create();
        var listener = new SummaryGeneratingListener();

        launcher.registerTestExecutionListeners(listener);
        launcher.execute(request);
        return new Result(listener.getSummary());
    }

    public static void main(String[] args) throws ClassNotFoundException, IOException, NoSuchMethodException {
        // Code source de la classe à tester en tant que chaîne de caractères
        String classToTestCode = """
                public class ClassToTest {
                    public int add(int a, int b) {
                        return a + b;
                    }
                }""";

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
                }""";

        var testRunnerService = new TestRunnerService();
        var result = testRunnerService.launchTests(classToTestCode, testClassCode);

//        var summary = result.summary;
//        long testFoundCount = summary.getTestsFoundCount();
//        System.out.println("testFoundCount = " + testFoundCount);
//        var failures = summary.getFailures();
//        System.out.println("getTestsSucceededCount() = " + summary.getTestsSucceededCount());
//        failures.forEach(failure -> System.out.println("failure = " + failure.getTestIdentifier() + " " + failure.getException()));
    }

    private static Optional<Class<?>> compileAndLoadTestClass(JavaFileObject source, JavaFileObject test) throws IOException, ClassNotFoundException {
        var compiler = ToolProvider.getSystemJavaCompiler();
        var diagnostics = new DiagnosticCollector<JavaFileObject>();

        var compilationUnits = List.of(source, test);
        var options = List.of("-d", "./target/testRunner");

        var task = compiler.getTask(null, null, diagnostics, options, null, compilationUnits);
        var success = task.call();

        if (!success) {
            for (var diagnostic : diagnostics.getDiagnostics()) {
                System.err.println(diagnostic.toString());
            }
            return Optional.empty();
        }

        var clazz = URLClassLoader.newInstance(new URL[]  {
                Paths.get("target", "testRunner").toUri().toURL()
        }).loadClass("DynamicTest");
        return Optional.of(clazz);
    }

}
