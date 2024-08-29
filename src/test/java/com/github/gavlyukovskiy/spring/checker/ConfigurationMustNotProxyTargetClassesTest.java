package com.github.gavlyukovskiy.spring.checker;

import org.junit.jupiter.api.Test;

class ConfigurationMustNotProxyTargetClassesTest extends BaseCheckerTest {

    ConfigurationMustNotProxyTargetClassesTest() {
        super(ConfigurationMustNotProxyTargetClasses.class);
    }

    @Test
    void shouldFailOnConfigurationClassWithoutProxyBeanMethods() {
        makeTestHelper().addSourceLines(
                "TestConfiguration.java",
                """
                import org.springframework.context.annotation.Configuration;

                // BUG: Diagnostic contains: @Configuration must use 'proxyBeanMethods = false'
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

                // BUG: Diagnostic contains: @Configuration must use 'proxyBeanMethods = false'
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

    @Test
    void shouldPassIfMetaAnnotatedWithProxyDisabled() {
        makeTestHelper().addSourceLines(
                "TestConfiguration.java",
                """
                import com.github.gavlyukovskiy.spring.checker.testannotations.MetaConfigurationWithProxyDisabled;

                @MetaConfigurationWithProxyDisabled
                class TestConfiguration {}
                """
        ).doTest();
    }

    @Test
    void shouldPassIfMetaAnnotatedWithProxyEnabled() {
        makeTestHelper().addSourceLines(
                "TestConfiguration.java",
                """
                import com.github.gavlyukovskiy.spring.checker.testannotations.MetaConfigurationWithProxyEnabled;

                // BUG: Diagnostic contains: @MetaConfigurationWithProxyEnabled is meta-annotated with @Configuration that must use 'proxyBeanMethods = false'
                @MetaConfigurationWithProxyEnabled
                class TestConfiguration {}
                """
        ).doTest();
    }
}
