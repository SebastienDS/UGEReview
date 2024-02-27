package fr.uge.revue.service;

import fr.uge.revue.util.JavaSourceFromString;
import org.junit.platform.engine.discovery.DiscoverySelectors;
import org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder;
import org.junit.platform.launcher.core.LauncherFactory;
import org.junit.platform.launcher.listeners.SummaryGeneratingListener;
import org.springframework.stereotype.Service;

import javax.tools.*;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

@Service
public class TestRunnerService {
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

        // Compiler les classes au runtime
        var source = new JavaSourceFromString("ClassToTest", classToTestCode);
        var test = new JavaSourceFromString("DynamicTest", testClassCode);
        var dynamicTestClass = compileAndLoadTestClass(source, test).orElseThrow();

        System.out.println(dynamicTestClass.getName());

        var request = LauncherDiscoveryRequestBuilder.request()
                        .selectors(DiscoverySelectors.selectClass(dynamicTestClass))
                        .build();

        var launcher = LauncherFactory.create();
        var listener = new SummaryGeneratingListener();

        launcher.registerTestExecutionListeners(listener);
        launcher.execute(request);

        var summary = listener.getSummary();
        long testFoundCount = summary.getTestsFoundCount();
        System.out.println("testFoundCount = " + testFoundCount);
        var failures = summary.getFailures();
        System.out.println("getTestsSucceededCount() = " + summary.getTestsSucceededCount());
        failures.forEach(failure -> System.out.println("failure = " + failure.getTestIdentifier() + " " + failure.getException()));
    }

    private static Optional<Class<?>> compileAndLoadTestClass(JavaFileObject source, JavaFileObject test) throws IOException, ClassNotFoundException {
        // Compilateur Java
        var compiler = ToolProvider.getSystemJavaCompiler();
        var diagnostics = new DiagnosticCollector<JavaFileObject>();

        // Liste des classes à compiler
        var compilationUnits = List.of(source, test);

        // Options de compilation
        var options = List.of("-d", "./target/testRunner"); // Spécifiez le répertoire de sortie si nécessaire

        // Compiler la classe en mémoire
        var task = compiler.getTask(null, null, diagnostics, options, null, compilationUnits);
        var success = task.call();

        // Afficher les diagnostics en cas d'échec de la compilation
        if (!success) {
            for (var diagnostic : diagnostics.getDiagnostics()) {
                System.err.println(diagnostic.toString());
            }
            return Optional.empty();
        }

        // Charger la classe compilée à partir du bytecode
        var clazz = URLClassLoader.newInstance(new URL[]  {
                Paths.get("target", "testRunner").toUri().toURL()
        }).loadClass("DynamicTest");
        return Optional.of(clazz);
    }

}
