package com.github.gavlyukovskiy.spring.checker;

import org.junit.jupiter.api.Test;

class UnnecessaryAutowiredTest extends BaseCheckerTest {

    UnnecessaryAutowiredTest() {
        super(UnnecessaryAutowired.class);
    }

    @Test
    void shouldFailOnAutowiredSingleConstructor() {
        makeTestHelper().addSourceLines(
                "TestConfiguration.java",
                """
                import org.springframework.beans.factory.annotation.Autowired;
                import org.springframework.stereotype.Component;

                @Component
                class TestConfiguration {
                    // BUG: Diagnostic contains: @Autowired on a single unambiguous constructor is unnecessary
                    @Autowired
                    public TestConfiguration(String dependency) {}
                }
                """
        ).doTest();
    }

    @Test
    void shouldPassOnSingleConstructorWithoutAutowired() {
        makeTestHelper().addSourceLines(
                "TestConfiguration.java",
                """
                import org.springframework.stereotype.Component;

                @Component
                class TestConfiguration {
                    public TestConfiguration(String dependency) {}
                }
                """
        ).doTest();
    }

    @Test
    void shouldPassOnAmbiguousConstructorsWithAutowired() {
        makeTestHelper().addSourceLines(
                "TestConfiguration.java",
                """
                import org.springframework.beans.factory.annotation.Autowired;
                import org.springframework.stereotype.Component;

                @Component
                class TestConfiguration {
                    @Autowired
                    public TestConfiguration(String dependency) {}
                    public TestConfiguration() {}
                }
                """
        ).doTest();
    }
}
