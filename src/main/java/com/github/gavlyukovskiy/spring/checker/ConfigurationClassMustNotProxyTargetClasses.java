package com.github.gavlyukovskiy.spring.checker;

import com.google.errorprone.BugPattern;
import com.google.errorprone.VisitorState;
import com.google.errorprone.bugpatterns.BugChecker;
import com.google.errorprone.matchers.Description;
import com.google.errorprone.matchers.Matcher;
import com.google.errorprone.matchers.Matchers;
import com.google.errorprone.util.ASTHelpers;
import com.sun.source.tree.AnnotationTree;
import com.sun.source.tree.ClassTree;
import com.sun.source.tree.Tree;
import org.checkerframework.javacutil.AnnotationUtils;

import java.io.Serial;
import java.util.Objects;

/**
 * Checks that {@link org.springframework.context.annotation.Configuration} classes do not use 'proxyBeanMethods = true'
 */
@BugPattern(
        summary = "@Configuration annotation must use 'proxyBeanMethods = false'",
        severity = BugPattern.SeverityLevel.ERROR,
        linkType = BugPattern.LinkType.CUSTOM,
        link = CustomLink.URL + "configurationclassmustnotproxytargetclasses"
)
public class ConfigurationClassMustNotProxyTargetClasses
        extends BugChecker
        implements BugChecker.AnnotationTreeMatcher {

    @Serial
    private static final long serialVersionUID = 4695396454075280748L;

    private static final Matcher<Tree> HAS_CONFIGURATION_ANNOTATION =
            Matchers.hasAnnotation("org.springframework.context.annotation.Configuration");

    @SuppressWarnings("deprecation") // can't figure how another ElementType API should work
    @Override
    public Description matchAnnotation(AnnotationTree annotationTree, VisitorState state) {
        var classTree = Objects.requireNonNull(ASTHelpers.findEnclosingNode(state.getPath(), ClassTree.class));

        if (!HAS_CONFIGURATION_ANNOTATION.matches(classTree, state)) {
            return Description.NO_MATCH;
        }
        var anno = ASTHelpers.getAnnotationMirror(annotationTree);
        Boolean proxyBeanMethods = AnnotationUtils.getElementValue(anno, "proxyBeanMethods", Boolean.class, true);

        if (!proxyBeanMethods) {
            return Description.NO_MATCH;
        }
        return describeMatch(annotationTree);
    }
}
