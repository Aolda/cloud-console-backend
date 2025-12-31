package com.acc.local.external.resilience;

public final class OpenstackCallContext {

    private OpenstackCallContext() {}

    private static final ThreadLocal<Ctx> HOLDER = new ThreadLocal<>();

    public static void set(Ctx ctx) { HOLDER.set(ctx); }
    public static Ctx get() { return HOLDER.get(); }
    public static void clear() { HOLDER.remove(); }

    public record Ctx(String retry, String circuitBreaker) {}
}
