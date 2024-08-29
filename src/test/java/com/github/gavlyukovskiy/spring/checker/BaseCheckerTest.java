package com.github.gavlyukovskiy.spring.checker;

import com.google.errorprone.CompilationTestHelper;
import com.google.errorprone.bugpatterns.BugChecker;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.util.List;

class BaseCheckerTest {

    @TempDir @SuppressWarnings("NullAway") private Path temporaryFolder;
    private final Class<? extends BugChecker> checker;

    public BaseCheckerTest(Class<? extends BugChecker> checker) {
        this.checker = checker;
    }

    protected CompilationTestHelper makeTestHelper() {
        return CompilationTestHelper.newInstance(checker, getClass())
                .setArgs(List.of("-d", temporaryFolder.toString()));
    }
}
