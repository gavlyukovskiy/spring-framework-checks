package com.github.gavlyukovskiy.spring.checker;

import com.google.errorprone.util.ASTHelpers;
import com.sun.source.tree.AnnotationTree;
import com.sun.tools.javac.code.Attribute;
import com.sun.tools.javac.code.Symbol;
import org.jspecify.annotations.Nullable;

import java.util.function.Predicate;

/**
 * Utilities to handle Spring annotations
 */
public final class SpringAnnotationUtils {
    private SpringAnnotationUtils() {
    }

    public static Attribute.@Nullable Compound getAnnotation(AnnotationTree annotationTree, Predicate<Symbol> matcher, boolean includeMetaAnnotations) {
        Symbol sym = ASTHelpers.getSymbol(annotationTree);
        if (sym == null) {
            return null;
        }

        if (matcher.test(sym)) {
            return (Attribute.Compound) ASTHelpers.getAnnotationMirror(annotationTree);
        }
        if (includeMetaAnnotations) {
            // only searching 1 level up, don't want to bother with the "annotation caching"
            return findAnnotation(sym, matcher);
        }
        return null;
    }

    public static Attribute.@Nullable Compound findAnnotation(Symbol sym, Predicate<Symbol> matcher) {
        return sym.getAnnotationMirrors()
                .stream()
                .filter(am -> matcher.test(((Symbol.TypeSymbol) am.getAnnotationType().asElement())))
                .findFirst()
                .orElse(null);
    }

    public static boolean hasAnnotation(Symbol sym, Predicate<Symbol> matcher) {
        return findAnnotation(sym, matcher) != null;
    }

    /**
     * Creates a {@link Symbol} predicate for the given annotation
     *
     * @param annotationClass the annotation
     * @return a predicate to be used on {@link Symbol}
     */
    public static Predicate<Symbol> matcher(Class<?> annotationClass) {
        return sym -> sym.getQualifiedName().contentEquals(annotationClass.getName());
    }
}
