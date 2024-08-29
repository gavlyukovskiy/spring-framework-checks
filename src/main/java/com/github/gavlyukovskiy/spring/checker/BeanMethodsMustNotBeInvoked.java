package com.github.gavlyukovskiy.spring.checker;

import com.google.errorprone.BugPattern;
import com.google.errorprone.VisitorState;
import com.google.errorprone.bugpatterns.BugChecker;
import com.google.errorprone.matchers.Description;
import com.google.errorprone.util.ASTHelpers;
import com.sun.source.tree.MethodInvocationTree;
import com.sun.tools.javac.code.Symbol;
import org.springframework.context.annotation.Bean;

import java.io.Serial;
import java.util.function.Predicate;

/**
 * Checks that {@link Bean} methods are not invoked directly and the beans are injected instead.
 */
@BugPattern(
        summary = "@Bean methods must not be invoked, instead the Spring bean must be injected",
        severity = BugPattern.SeverityLevel.ERROR,
        linkType = BugPattern.LinkType.CUSTOM,
        link = CustomLink.URL + "beanmethodsmustnotbeinvoked"
)
public class BeanMethodsMustNotBeInvoked
        extends BugChecker
        implements BugChecker.MethodInvocationTreeMatcher {

    @Serial
    private static final long serialVersionUID = 1L;

    private static final Predicate<Symbol> BEAN_ANNOTATION = SpringAnnotationUtils.matcher(Bean.class);

    @Override
    public Description matchMethodInvocation(MethodInvocationTree tree, VisitorState state) {
        var sym = ASTHelpers.getSymbol(tree);
        if (!SpringAnnotationUtils.hasAnnotation(sym, BEAN_ANNOTATION)) {
            return Description.NO_MATCH;
        }
        return describeMatch(tree);
    }
}
