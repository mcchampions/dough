package io.github.bakedlibs.dough.collections;

import java.util.Map;
import java.util.Objects;

public class Pair<P, S> {
    private P firstValue;
    private S secondValue;

    public Pair(P a, S b) {
        this.firstValue = a;
        this.secondValue = b;
    }

    public Pair(Map.Entry<P, S> mapEntry) {
        this(mapEntry.getKey(), mapEntry.getValue());
    }

    public Pair(OptionalPair<P, S> pair) {
        this(pair.getFirstValue().orElse(null), pair.getSecondValue().orElse(null));
    }

    public P getFirstValue() {
        return this.firstValue;
    }

    public S getSecondValue() {
        return this.secondValue;
    }

    public void setFirstValue(P firstValue) {
        this.firstValue = firstValue;
    }

    public void setSecondValue(S secondValue) {
        this.secondValue = secondValue;
    }

    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof Pair<?, ?> other)) {
            return false;
        }
        if (!Objects.equals(this.firstValue, other.firstValue)) {
            return false;
        }
        return Objects.equals(this.secondValue, other.secondValue);
    }

    public int hashCode() {
        final int prime = 59;
        int result = 1;
        result = result * prime + (firstValue == null ? 43 : firstValue.hashCode());
        result = result * prime + (secondValue == null ? 43 : secondValue.hashCode());
        return result;
    }

    public String toString() {
        return "Pair(firstValue=" + firstValue + ", secondValue=" + secondValue + ")";
    }
}
