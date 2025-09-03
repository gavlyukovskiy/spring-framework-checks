package com.github.gavlyukovskiy.spring.checker;

import com.google.auto.service.AutoService;
import com.google.errorprone.BugPattern;
import com.google.errorprone.VisitorState;
import com.google.errorprone.bugpatterns.BugChecker;
import com.google.errorprone.fixes.SuggestedFix;
import com.google.errorprone.matchers.Description;
import com.google.errorprone.util.ASTHelpers;
import com.sun.source.tree.ClassTree;
import com.sun.source.tree.MethodTree;

import java.util.List;

/**
 * Checks that @Autowired is not used on a single unambiguous constructor.
 */
@AutoService(BugChecker.class)
@BugPattern(
        summary = "@Autowired on a single unambiguous constructor is unnecessary",
        severity = BugPattern.SeverityLevel.ERROR,
        linkType = BugPattern.LinkType.CUSTOM,
        link = CustomLink.URL + "UnnecessaryAutowired"
)
public class UnnecessaryAutowired extends BugChecker implements BugChecker.ClassTreeMatcher {

    @Override
    public Description matchClass(ClassTree classTree, VisitorState state) {
        List<MethodTree> constructors = ASTHelpers.getConstructors(classTree);
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
