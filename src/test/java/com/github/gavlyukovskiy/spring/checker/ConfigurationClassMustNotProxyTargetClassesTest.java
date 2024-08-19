package com.github.gavlyukovskiy.spring.checker;

import com.google.errorprone.CompilationTestHelper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.util.List;

class ConfigurationClassMustNotProxyTargetClassesTest {

    @TempDir @SuppressWarnings("NullAway") private Path temporaryFolder;

    @Test
    void shouldFailOnConfigurationClassWithoutProxyBeanMethods() {
        makeTestHelper().addSourceLines(
                "TestConfiguration.java",
                """
                import org.springframework.context.annotation.Configuration;

                // BUG: Diagnostic contains: @Configuration annotation must use 'proxyBeanMethods = false'
                @Configuration
                class TestConfiguration {}
                """
        ).doTest();
    }

    @Test
    void shouldFailOnConfigurationClassWithProxyBeanMethodsTrue() {
        makeTestHelper().addSourceLines(
                "TestConfiguration.java",
                """
                import org.springframework.context.annotation.Configuration;

                // BUG: Diagnostic contains: @Configuration annotation must use 'proxyBeanMethods = false'
                @Configuration(proxyBeanMethods = true)
                class TestConfiguration {}
                """
        ).doTest();
    }

    @Test
    void shouldPassOnConfigurationClassWithProxyBeanMethodsFalse() {
        makeTestHelper().addSourceLines(
                "TestConfiguration.java",
                """
                import org.springframework.context.annotation.Configuration;

                @Configuration(proxyBeanMethods = false)
                class TestConfiguration {}
                """
        ).doTest();
    }

    @Test
    void shouldPassIfOtherParamsArePresent() {
        makeTestHelper().addSourceLines(
                "TestConfiguration.java",
                """
                import org.springframework.context.annotation.Configuration;

                @Configuration(value = "TestConfig", proxyBeanMethods = false)
                class TestConfiguration {}
                """
        ).doTest();
    }

    protected CompilationTestHelper makeTestHelper() {
        return CompilationTestHelper.newInstance(ConfigurationClassMustNotProxyTargetClasses.class, getClass())
                .setArgs(List.of("-d", temporaryFolder.toString()));
    }
}
