package com.github.gavlyukovskiy.spring.checker;

import org.junit.jupiter.api.Test;

class BeanMethodsMustNotBeInvokedTest extends BaseCheckerTest {

    BeanMethodsMustNotBeInvokedTest() {
        super(BeanMethodsMustNotBeInvoked.class);
    }

    @Test
    void shouldFailOnInvocationOfBeanMethod() {
        makeTestHelper().addSourceLines(
                "TestConfiguration.java",
                """
                import org.springframework.context.annotation.Configuration;
                import org.springframework.context.annotation.Bean;

                @Configuration(proxyBeanMethods = false)
                class TestConfiguration {
                    @Bean
                    String myBean() { return ""; }
                    @Bean
                    String myOtherBean() {
                        // BUG: Diagnostic contains: @Bean methods must not be invoked, instead the Spring bean must be injected
                        return myBean();
                    }
                }
                """
        ).doTest();
    }

    @Test
    void shouldNotFailOnInvocationOfOtherMethods() {
        makeTestHelper().addSourceLines(
                "TestConfiguration.java",
                """
                import org.springframework.context.annotation.Configuration;
                import org.springframework.context.annotation.Bean;

                @Configuration(proxyBeanMethods = false)
                class TestConfiguration {
                    String myString() { return ""; }
                    @Bean
                    String myOtherBean() {
                        return myString();
                    }
                }
                """
        ).doTest();
    }
}
