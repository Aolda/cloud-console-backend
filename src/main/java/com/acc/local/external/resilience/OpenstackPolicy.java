package com.acc.local.external.resilience;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface OpenstackPolicy {
    // 비워두면: 기존 규칙(component-method) 그대로
    String retry() default "";
    String circuitBreaker() default "";
}
