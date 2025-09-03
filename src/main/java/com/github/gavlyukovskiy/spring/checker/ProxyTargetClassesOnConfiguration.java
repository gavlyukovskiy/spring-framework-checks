package com.github.gavlyukovskiy.spring.checker;

import com.google.errorprone.BugPattern;
import com.google.errorprone.VisitorState;
import com.google.errorprone.bugpatterns.BugChecker;
import com.google.errorprone.matchers.Description;
import com.google.errorprone.util.ASTHelpers;
import com.google.errorprone.util.MoreAnnotations;
import com.sun.source.tree.AnnotationTree;
import com.sun.tools.javac.code.Attribute;
import com.sun.tools.javac.code.Symbol;

import java.io.Serial;
import java.util.function.Predicate;

import static com.google.errorprone.matchers.Description.NO_MATCH;

/**
 * Checks that {@link org.springframework.context.annotation.Configuration} classes do not use 'proxyBeanMethods = true'
 */
@BugPattern(
        summary = "@Configuration must use 'proxyBeanMethods = false'",
        severity = BugPattern.SeverityLevel.ERROR,
        linkType = BugPattern.LinkType.CUSTOM,
        link = CustomLink.URL + "ProxyTargetClassesOnConfiguration"
)
public class ProxyTargetClassesOnConfiguration
        extends BugChecker
        implements BugChecker.AnnotationTreeMatcher {

    @Serial
    private static final long serialVersionUID = 1L;

    private static final Predicate<Symbol> CONFIGURATION_ANNOTATION = SpringAnnotationUtils.matcher("org.springframework.context.annotation.Configuration");

    @Override
    public Description matchAnnotation(AnnotationTree tree, VisitorState state) {
        var sym = ASTHelpers.getSymbol(tree);
        if (sym == null) {
            return NO_MATCH;
        }

        if (!CONFIGURATION_ANNOTATION.test(sym)) {
            return NO_MATCH;
        }

        var am = (Attribute.Compound) ASTHelpers.getAnnotationMirror(tree);
        boolean proxyBeanMethods = MoreAnnotations.getAnnotationValue(am, "proxyBeanMethods").map(v -> (boolean) v.getValue()).orElse(true);

        if (!proxyBeanMethods) {
            return Description.NO_MATCH;
        }
        return describeMatch(tree);
    }
}
