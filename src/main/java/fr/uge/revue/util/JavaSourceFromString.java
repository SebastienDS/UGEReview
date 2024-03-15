package fr.uge.revue.util;

import javax.tools.SimpleJavaFileObject;
import java.net.URI;

public class JavaSourceFromString extends SimpleJavaFileObject {
    private final String code;

    public JavaSourceFromString(String className, String code) {
        super(URI.create("string:///" + className.replace('.', '/') + Kind.SOURCE.extension), Kind.SOURCE);
        this.code = code;
    }

    @Override
    public CharSequence getCharContent(boolean ignoreEncodingErrors) {
        return code;
    }
}
