package com.github.gavlyukovskiy.spring.checker;

import com.sun.tools.javac.code.Symbol;

import java.util.function.Predicate;

/**
 * Utilities to handle Spring annotations
 */
public final class SpringAnnotationUtils {
    private SpringAnnotationUtils() {
    }

    /**
     * Creates a {@link Symbol} predicate for the given annotation
     *
     * @param annotationFullyQualifiedName the annotation fully qualified name
     * @return a predicate to be used on {@link Symbol}
     */
    public static Predicate<Symbol> matcher(String annotationFullyQualifiedName) {
        return sym -> sym.getQualifiedName().contentEquals(annotationFullyQualifiedName);
    }
}
