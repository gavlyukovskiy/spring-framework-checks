package com.github.gavlyukovskiy.spring.checker;

import org.junit.jupiter.api.Test;

class NonConstructorInjectionTest extends BaseCheckerTest {

    NonConstructorInjectionTest() {
        super(NonConstructorInjection.class);
    }

    @Test
    void shouldFailOnFieldInjection() {
        makeTestHelper().addSourceLines(
                "TestConfiguration.java",
                """
                import org.springframework.beans.factory.annotation.Autowired;
                import org.springframework.stereotype.Component;

                @Component
                class TestConfiguration {
                    // BUG: Diagnostic contains: Constructor injection should be preferred to @Autowired on fields and methods
                    @Autowired
                    private String dependency;
                }
                """
        ).doTest();
    }

    @Test
    void shouldFailOnMethodInjection() {
        makeTestHelper().addSourceLines(
                "TestConfiguration.java",
                """
                import org.springframework.beans.factory.annotation.Autowired;
                import org.springframework.stereotype.Component;

                @Component
                class TestConfiguration {
                    private String dependency;

                    // BUG: Diagnostic contains: Constructor injection should be preferred to @Autowired on fields and methods
                    @Autowired
                    public void setDependency(String dependency) {
                        this.dependency = dependency;
                    }
                }
                """
        ).doTest();
    }

    @Test
    void shouldPassOnConstructorInjection() {
        makeTestHelper().addSourceLines(
                "TestConfiguration.java",
                """
                import org.springframework.beans.factory.annotation.Autowired;
                import org.springframework.stereotype.Component;

                @Component
                class TestConfiguration {
                    private final String dependency;

                    @Autowired
                    public TestConfiguration(String dependency) {
                        this.dependency = dependency;
                    }
                }
                """
        ).doTest();
    }
}
