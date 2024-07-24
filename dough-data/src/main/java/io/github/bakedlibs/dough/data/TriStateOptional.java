package io.github.bakedlibs.dough.data;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

public final class TriStateOptional<T> {
    private TriState state;
    private T value;

    private TriStateOptional(TriState state) {
        this.state = state;
    }

    private TriStateOptional(T value) {
        state = TriState.COMPUTED;
        this.value = value;
    }

    public static <T> TriStateOptional<T> empty() {
        return new TriStateOptional<>(TriState.EMPTY);
    }

    public static <T> TriStateOptional<T> createNew() {
        return new TriStateOptional<>(TriState.NOT_COMPUTED);
    }

    public static <T> TriStateOptional<T> of(T value) {
        return new TriStateOptional<>(value);
    }

    public static <T> TriStateOptional<T> ofNullable(T value) {
        if (value == null) {
            return empty();
        } else {
            return new TriStateOptional<>(value);
        }
    }

    public boolean isEmpty() {
        return state == TriState.EMPTY;
    }

    public boolean isComputed() {
        return state != TriState.NOT_COMPUTED;
    }

    public boolean isPresent() {
        return state == TriState.COMPUTED;
    }

    public void ifPresent(Consumer<T> consumer) {
        if (isPresent()) {
            consumer.accept(value);
        }
    }

    public void compute(T value) {
        if (isComputed()) {
            throw new IllegalStateException("This Optional has already been computed.");
        }

        if (value == null) {
            state = TriState.EMPTY;
        } else {
            state = TriState.COMPUTED;
            this.value = value;
        }
    }

    public void compute(Supplier<T> supplier) {
        compute(supplier.get());
    }

    public T get() {
        if (!isPresent()) {
            throw new IllegalAccessError("This Optional has no value! Check .isPresent() first!");
        }

        return value;
    }

    public T getOrElse(T value) {
        if (!isComputed()) {
            throw new IllegalStateException("This Optional has not yet been computed!");
        }

        return isPresent() ? this.value : value;
    }

    public Optional<T> getAsOptional() {
        if (!isComputed()) {
            throw new IllegalStateException("This Optional has not yet been computed!");
        }

        return isPresent() ? Optional.of(value) : Optional.empty();
    }
}
