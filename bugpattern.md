# Bug patterns provided by the [spring-framework-checks](https://github.com/gavlyukovskiy/spring-framework-checks)

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

## BeanMethodsMustNotBeInvoked

A method annotated with `@Bean` declared the bean to be used in Spring context. It must not be called directly, even
from a `@Configuration` class.
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
