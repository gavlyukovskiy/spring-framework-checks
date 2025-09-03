package com.github.gavlyukovskiy.spring.checker;

import com.google.auto.service.AutoService;
import com.google.errorprone.BugPattern;
import com.google.errorprone.VisitorState;
import com.google.errorprone.bugpatterns.BugChecker;
import com.google.errorprone.fixes.SuggestedFix;
import com.google.errorprone.matchers.Description;
import com.google.errorprone.matchers.Matcher;
import com.google.errorprone.util.ASTHelpers;
import com.sun.source.tree.MethodTree;
import com.sun.source.tree.Tree;
import com.sun.source.tree.VariableTree;
import org.jspecify.annotations.NullUnmarked;

import javax.lang.model.element.ElementKind;

import static com.google.errorprone.matchers.Matchers.hasAnnotation;

/**
 * Checks that only constructor is used for injection of Spring beans.
 */
@AutoService(BugChecker.class)
@BugPattern(
        summary = "Constructor injection should be preferred to @Autowired on fields and methods",
        severity = BugPattern.SeverityLevel.ERROR,
        linkType = BugPattern.LinkType.CUSTOM,
        link = CustomLink.URL + "NonConstructorInjection"
)
@NullUnmarked
public class NonConstructorInjection extends BugChecker
        implements BugChecker.VariableTreeMatcher, BugChecker.MethodTreeMatcher {

    private static final Matcher<Tree> AUTOWIRED = hasAnnotation("org.springframework.beans.factory.annotation.Autowired");

    @Override
    public Description matchVariable(VariableTree tree, VisitorState state) {
        if (AUTOWIRED.matches(tree, state)) {
            return match(tree);
        }
        return Description.NO_MATCH;
    }

    @Override
    public Description matchMethod(MethodTree tree, VisitorState state) {
        if (AUTOWIRED.matches(tree, state)) {
            var symbol = ASTHelpers.getSymbol(tree);
            if (symbol != null && symbol.getKind() == ElementKind.CONSTRUCTOR) {
                return Description.NO_MATCH;
            }
            return match(tree);
        }
        return Description.NO_MATCH;
    }

    private Description match(Tree tree) {
        var annotations = ASTHelpers.getAnnotations(tree);
        var autowired = ASTHelpers.getAnnotationWithSimpleName(annotations, "Autowired");
        if (autowired != null) {
            return describeMatch(autowired, SuggestedFix.delete(autowired));
        }
        return Description.NO_MATCH;
    }
}
