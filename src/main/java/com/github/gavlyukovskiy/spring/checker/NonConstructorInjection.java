package com.github.gavlyukovskiy.spring.checker;

import com.google.errorprone.BugPattern;
import com.google.errorprone.VisitorState;
import com.google.errorprone.bugpatterns.BugChecker;
import com.google.errorprone.fixes.SuggestedFix;
import com.google.errorprone.matchers.Description;
import com.google.errorprone.util.ASTHelpers;
import com.sun.source.tree.MethodTree;
import com.sun.source.tree.Tree;
import com.sun.source.tree.VariableTree;
import org.jspecify.annotations.NullUnmarked;

import java.io.Serial;

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
        implements BugChecker.VariableTreeMatcher, BugChecker.MethodTreeMatcher {

    @Serial
    private static final long serialVersionUID = 1L;

    @Override
    public Description matchVariable(VariableTree tree, VisitorState state) {
        return match(tree);
    }

    @Override
    public Description matchMethod(MethodTree tree, VisitorState state) {
        return match(tree);
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
