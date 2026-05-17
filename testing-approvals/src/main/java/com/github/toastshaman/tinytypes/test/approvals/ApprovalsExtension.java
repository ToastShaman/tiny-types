package com.github.toastshaman.tinytypes.test.approvals;

import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;

public final class ApprovalsExtension implements ParameterResolver {

    private final ApproverFactory factory;

    public ApprovalsExtension() {
        this(ApprovalsExtension::fileApprover);
    }

    public ApprovalsExtension(ApproverFactory factory) {
        this.factory = factory;
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) {
        return Approver.class.equals(parameterContext.getParameter().getType());
    }

    @Override
    public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) {
        Class<?> testClass = extensionContext.getRequiredTestClass();
        String methodName = extensionContext.getRequiredTestMethod().getName();
        return factory.create(testClass, methodName);
    }

    private static Approver fileApprover(Class<?> testClass, String methodName) {
        Path sourceDir = findSourceDir(testClass);
        String packagePath = testClass.getPackageName().replace('.', '/');
        String fileName = testClass.getSimpleName() + "." + methodName + ".approved";
        return new FileApprover(sourceDir.resolve(packagePath).resolve(fileName));
    }

    /**
     * Walks up from the compiled classes directory (e.g. {@code build/classes/java/test}) until it
     * finds a sibling {@code src/test/java} directory, which is where approved files live.
     */
    private static Path findSourceDir(Class<?> testClass) {
        try {
            Path classesDir = Path.of(testClass
                    .getProtectionDomain()
                    .getCodeSource()
                    .getLocation()
                    .toURI());
            Path candidate = classesDir;
            while (candidate != null) {
                Path srcTestJava = candidate.resolve("src/test/java");
                if (Files.isDirectory(srcTestJava)) {
                    return srcTestJava;
                }
                candidate = candidate.getParent();
            }
            throw new ParameterResolutionException(
                    "Cannot find src/test/java directory walking up from: " + classesDir);
        } catch (Exception e) {
            throw new ParameterResolutionException("Cannot resolve source directory for " + testClass.getName(), e);
        }
    }
}
