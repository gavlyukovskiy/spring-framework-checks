package com.github.gavlyukovskiy.spring.checker;

import com.google.errorprone.BugPattern;
import com.google.errorprone.VisitorState;
import com.google.errorprone.bugpatterns.BugChecker;
import com.google.errorprone.fixes.SuggestedFix;
import com.google.errorprone.matchers.Description;
import com.google.errorprone.util.ASTHelpers;
import com.sun.source.tree.ClassTree;
import com.sun.source.tree.MethodTree;

import java.io.Serial;
import java.util.List;

/**
 * Checks that @Autowired is not used on a single unambiguous constructor.
 */
@BugPattern(
        summary = "@Autowired on a single unambiguous constructor is unnecessary",
        severity = BugPattern.SeverityLevel.ERROR,
        linkType = BugPattern.LinkType.CUSTOM,
        link = CustomLink.URL + "UnnecessaryAutowired"
)
public class UnnecessaryAutowired extends BugChecker implements BugChecker.ClassTreeMatcher {

    @Serial
    private static final long serialVersionUID = 1L;

    @Override
    public Description matchClass(ClassTree classTree, VisitorState state) {
        List<MethodTree> constructors = classTree.getMembers().stream()
                .filter(member -> member instanceof MethodTree)
                .map(member -> (MethodTree) member)
                .filter(method -> method.getName().contentEquals("<init>"))
                .toList();
        if (constructors.size() == 1) {
            var constructor = constructors.get(0);
            var annotations = ASTHelpers.getAnnotations(constructor);
            var autowired = ASTHelpers.getAnnotationWithSimpleName(annotations, "Autowired");
            if (autowired != null) {
                return describeMatch(autowired, SuggestedFix.delete(autowired));
            }
        }
        return Description.NO_MATCH;
    }
}
