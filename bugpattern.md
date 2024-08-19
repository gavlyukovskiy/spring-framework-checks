# Bug patterns provided by the [spring-framework-checks](https://github.com/gavlyukovskiy/spring-framework-checks)

## ConfigurationClassMustNotProxyTargetClasses

A `@Configuration` class should use `proxyBeanMethods = false` to avoid unnecessary proxying that negatively affects
memory usage and startup time (see [spring-boot#9068](https://github.com/spring-projects/spring-boot/issues/9068)).


### The problem

To make sure that `dependencyBean()` does not create a new instance of the `DependencyBean`, the proxy is created for
the configuration class (e.g. `MyConfiguration$$SpringCGLIB$$0`):
```java
@Configuration
public class MyConfiguration {
    @Bean
    public DependencyBean dependencyBean() {
        return new DependencyBean();
    }

    @Bean
    public MainBean mainBean() {
        return new MainBean(dependencyBean());
    }
}
```
the configuration class can be transformed to avoid proxying and requires to use method arguments for the `@Bean`
factory methods: 
```java
@Configuration(proxyBeanMethods = false)
public class MyConfiguration {
    @Bean
    public DependencyBean dependencyBean() {
        return new DependencyBean();
    }

    @Bean
    public MainBean mainBean(DependencyBean dependencyBean) {
        return new MainBean(dependencyBean);
    }
}
```

### Suppression

Suppress false positives by adding the suppression annotation
`@SuppressWarnings("ConfigurationClassMustNotProxyTargetClasses")` to the enclosing element.

