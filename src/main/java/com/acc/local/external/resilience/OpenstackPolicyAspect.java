package com.acc.local.external.resilience;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class OpenstackPolicyAspect {

    @Around("within(com.acc.local.external.modules..*) && @annotation(policy)")
    public Object around(ProceedingJoinPoint pjp, OpenstackPolicy policy) throws Throwable {
        OpenstackCallContext.set(new OpenstackCallContext.Ctx(
                policy.retry(),
                policy.circuitBreaker()
        ));
        try {
            return pjp.proceed();
        } finally {
            OpenstackCallContext.clear();
        }
    }
}
