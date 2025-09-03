# Bug patterns provided by the [spring-framework-checks](https://github.com/gavlyukovskiy/spring-framework-checks)

## BeanMethodsMustNotBeInvoked

A method annotated with `@Bean` declared the bean to be used in Spring context.

### The problem

A method annotated with `@Bean` must not be invoked directly, as it will create a new instance of the bean instead of
returning the Spring-managed bean. Instead, the Spring bean must be injected as a method argument or via constructor injection.
```java
@Configuration(proxyBeanMethods = false)
public class MyConfiguration {
    @Bean
    public DependencyBean dependencyBean() {
        return new DependencyBean();
    }

    @Bean
    public MainBean mainBean() {
        return new MainBean(dependencyBean()); // WRONG
    }
}
```
instead the Spring bean must be injected as a method argument:
```java
@Configuration(proxyBeanMethods = false)
public class MyConfiguration {
    @Bean
    public DependencyBean dependencyBean() {
        return new DependencyBean();
    }

    @Bean
    public MainBean mainBean(DependencyBean dependencyBean) { // CORRECT
        return new MainBean(dependencyBean);
    }
}
```

### Suppression
Suppress false positives by adding the suppression annotation
`@SuppressWarnings("BeanMethodsMustNotBeInvoked")` to the enclosing element.

## ConfigurationMustNotProxyTargetClasses

A class annotated with `@Configuration` should use `proxyBeanMethods = false` to avoid unnecessary proxying that negatively affects
memory usage and startup time (see [spring-boot#9068](https://github.com/spring-projects/spring-boot/issues/9068)).

### The problem

To make sure that `dependencyBean()` does not create a new instance of the `DependencyBean`, the proxy is created for
the configuration class (e.g. `MyConfiguration$$SpringCGLIB$$0`):
```java
@Configuration // WRONG
public class MyConfiguration {
    @Bean
    public MyBean myBean() {
        return new MyBean();
    }
}
```
the configuration class can be transformed to avoid proxying and requires to use method arguments for the `@Bean`
factory methods: 
```java
@Configuration(proxyBeanMethods = false) // CORRECT
public class MyConfiguration {
    @Bean
    public MyBean myBean() {
        return new MyBean();
    }
}
```

### Suppression

Suppress false positives by adding the suppression annotation
`@SuppressWarnings("ConfigurationMustNotProxyTargetClasses")` to the enclosing element.

## NonConstructorInjection
A spring bean should use constructor injection instead of field or setter injection.

### The problem
Field and setter injection make dependencies less explicit, harder to test, and can lead to null pointer exceptions. Constructor injection is preferred for better immutability and testability.

```java
@Component
public class MyBean {
    @Autowired // WRONG
    private DependencyBean dependencyBean;
}
```
instead use constructor injection:
```java
@Component
public class MyBean {
    private final DependencyBean dependencyBean;
    public MyBean(DependencyBean dependencyBean) { // CORRECT
        this.dependencyBean = dependencyBean;
    }
}
```

### Suppression
Suppress false positives by adding the suppression annotation `@SuppressWarnings("NonConstructorInjection")` to the enclosing element.

## UnnecessaryAutowired
Reports unnecessary usage of `@Autowired` on a single constructor. In Spring, if a bean has only one constructor, `@Autowired` is not required and can be omitted.

### The problem
Annotating a single constructor with `@Autowired` is redundant, as Spring will automatically use it for dependency injection. Removing the annotation makes the code cleaner and more idiomatic.

```java
@Component
public class MyBean {
    @Autowired // WRONG
    public MyBean(DependencyBean dependencyBean) {}
}
```
Instead, omit the annotation:
```java
@Component
public class MyBean {
    public MyBean(DependencyBean dependencyBean) {} // CORRECT
}
```

If there are multiple constructors, `@Autowired` is allowed to indicate which one should be used:
```java
@Component
public class MyBean {
    @Autowired // ALLOWED
    public MyBean(DependencyBean dependencyBean) {}
    public MyBean() {}
}
```

### Suppression
Suppress false positives by adding the suppression annotation `@SuppressWarnings("UnnecessaryAutowired")` to the enclosing element.
