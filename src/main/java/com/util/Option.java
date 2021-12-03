package com.util;

import java.util.function.Supplier;
import com.functional.Function;

public abstract class Option<A> {
    @SuppressWarnings("rawtypes")
    private static final Option none = new None();

    public abstract A getOrThrow();

    public abstract A getOrElse(Supplier<A> defaultValue);

    public abstract <B> Option<B> map(Function<A, B> f);

    public <B> Option<B> flatMap(Function<A, Option<B>> f) {
        return map(f).getOrElse(Option::none);
    }

    public Option<A> filter(Function<A, Boolean> p) {
        return flatMap(x -> p.apply(x) ? this : none());
    }

    @Override
    public abstract String toString();

    private static class None<A> extends Option<A> {
        private None() {}

        @Override
        public A getOrThrow() {
            throw new IllegalStateException("get called on None");
        }

        @Override
        public String toString() {
            return "None";
        }

        @Override
        public A getOrElse(Supplier<A> defaultValue) {
            return defaultValue.get();
        }

        @Override
        public <B> Option<B> map(Function<A, B> f) {
            return none();
        }
    }

    private static class Some<A> extends Option<A> {
        private final A value;

        private Some(A a) {
            value = a;
        }

        @Override
        public A getOrThrow() {
            return value;
        }

        @Override
        public String toString() {
            return String.format("Some(%s)", value);
        }

        @Override
        public A getOrElse(Supplier<A> defaultValue) {
            return value;
        }

        @Override
        public <B> Option<B> map(Function<A, B> f) {
            return some(f.apply(value));
        }
    }

    public static <A> Option<A> some(A a) {
        return new Some<>(a);
    }

    @SuppressWarnings("unchecked")
    public static <A> Option<A> none() {
        return none;
    }
}
