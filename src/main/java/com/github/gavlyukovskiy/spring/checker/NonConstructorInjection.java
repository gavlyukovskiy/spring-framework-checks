package com.github.gavlyukovskiy.spring.checker;

import com.google.errorprone.BugPattern;
import com.google.errorprone.VisitorState;
import com.google.errorprone.bugpatterns.BugChecker;
import com.google.errorprone.matchers.Description;
import com.google.errorprone.util.ASTHelpers;
import com.sun.source.tree.MethodTree;
import com.sun.source.tree.Tree;
import com.sun.source.tree.VariableTree;
import com.sun.tools.javac.code.Symbol;
import org.jspecify.annotations.NullUnmarked;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.Serial;
import java.util.function.Predicate;

/**
 * Checks that only constructor is used for injection of Spring beans.
 */
@BugPattern(
        summary = "Constructor injection should be preferred to @Autowired on fields and methods",
        severity = BugPattern.SeverityLevel.ERROR,
        linkType = BugPattern.LinkType.CUSTOM,
        link = CustomLink.URL + "NonConstructorInjection"
)
@NullUnmarked
public class NonConstructorInjection extends BugChecker
        implements BugChecker.VariableTreeMatcher,  BugChecker.MethodTreeMatcher {

    @Serial
    private static final long serialVersionUID = 1L;

    public static final Predicate<Symbol> AUTOWIRED_ANNOTATION = SpringAnnotationUtils.matcher(Autowired.class);

    @Override
    public Description matchVariable(VariableTree tree, VisitorState state) {
        return match(tree);
    }

    @Override
    public Description matchMethod(MethodTree tree, VisitorState state) {
        return match(tree);
    }

    private Description match(Tree tree) {
        var symbol = ASTHelpers.getSymbol(tree);
        if (symbol == null) {
            return Description.NO_MATCH;
        }
        var annotation = SpringAnnotationUtils.findAnnotation(symbol, AUTOWIRED_ANNOTATION);
        if (annotation != null) {
            return describeMatch(tree);
        }
        return Description.NO_MATCH;
    }
}
