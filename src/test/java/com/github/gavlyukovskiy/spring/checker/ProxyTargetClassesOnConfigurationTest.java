package com.github.gavlyukovskiy.spring.checker;

import org.junit.jupiter.api.Test;

class ProxyTargetClassesOnConfigurationTest extends BaseCheckerTest {

    ProxyTargetClassesOnConfigurationTest() {
        super(ProxyTargetClassesOnConfiguration.class);
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
    void shouldFailIfMetaAnnotatedWithProxyEnabled() {
        makeTestHelper().addSourceLines(
                "MetaConfigurationWithProxyEnabled.java",
                """
                import org.springframework.context.annotation.Configuration;
                
                import java.lang.annotation.ElementType;
                import java.lang.annotation.Retention;
                import java.lang.annotation.RetentionPolicy;
                import java.lang.annotation.Target;

                @Target(ElementType.TYPE)
                @Retention(RetentionPolicy.RUNTIME)
                // BUG: Diagnostic contains: @Configuration must use 'proxyBeanMethods = false'
                @Configuration
                public @interface MetaConfigurationWithProxyEnabled {
                }
                """
        ).doTest();
    }
}
