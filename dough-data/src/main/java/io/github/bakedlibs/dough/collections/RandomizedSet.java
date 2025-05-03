package io.github.bakedlibs.dough.collections;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.IntFunction;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class RandomizedSet<T> implements Iterable<T>, Streamable<T> {
    private final Set<WeightedNode<T>> internalSet;

    private int size;
    private float totalWeights;

    public RandomizedSet() {
        this(LinkedHashSet::new);
    }

    public RandomizedSet(Supplier<Set<WeightedNode<T>>> constructor) {
        internalSet = constructor.get();
    }

    public RandomizedSet(Collection<T> collection) {
        this();

        for (T element : collection) {
            add(element, 1);
        }
    }

    public int size() {
        return size;
    }

    public float sumWeights() {
        return totalWeights;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public boolean contains(T obj) {
        for (WeightedNode<T> node : internalSet) {
            if (node.equals(obj)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public Iterator<T> iterator() {
        return new Iterator<>() {
            private final Iterator<WeightedNode<T>> iterator = internalSet.iterator();

            @Override
            public boolean hasNext() {
                return iterator.hasNext();
            }

            @Override
            public T next() {
                WeightedNode<T> node = iterator.next();
                return node == null ? null : node.getObject();
            }

            @Override
            public void remove() {
                iterator.remove();
            }
        };
    }

    public T[] toArray(IntFunction<T[]> constructor) {
        T[] array = constructor.apply(size);
        Iterator<T> iterator = iterator();
        int i = 0;

        while (iterator.hasNext()) {
            array[i] = iterator.next();
            i++;
        }

        return array;
    }

    public boolean add(T obj, float weight) {
        if (weight <= 0F) {
            throw new IllegalArgumentException("A Weight may never be less than or equal to zero!");
        }

        if (internalSet.add(new WeightedNode<>(weight, obj))) {
            size++;
            totalWeights += weight;
            return true;
        } else {
            return false;
        }
    }

    public void setWeight(T obj, float weight) {
        if (weight <= 0F) {
            throw new IllegalArgumentException("A Weight may never be less than or equal to zero!");
        }

        for (WeightedNode<T> node : internalSet) {
            if (node.equals(obj)) {
                size--;
                totalWeights -= node.getWeight();
                totalWeights += weight;

                node.setWeight(weight);
                return;
            }
        }

        throw new IllegalStateException("The specified Object is not contained in this Set");
    }

    public boolean remove(T obj) {
        Iterator<WeightedNode<T>> iterator = internalSet.iterator();

        while (iterator.hasNext()) {
            WeightedNode<T> node = iterator.next();

            if (node.equals(obj)) {
                size--;
                totalWeights -= node.getWeight();

                iterator.remove();
                return true;
            }
        }

        return false;
    }

    public void clear() {
        size = 0;
        totalWeights = 0F;

        internalSet.clear();
    }

    @Override
    public Stream<T> stream() {
        return StreamSupport.stream(spliterator(), false);
    }

    public T getRandom() {
        return getRandom(ThreadLocalRandom.current());
    }

    public T getRandom(Random random) {
        float goal = random.nextFloat() * totalWeights;
        float i = 0;

        Iterator<WeightedNode<T>> iterator = internalSet.iterator();
        WeightedNode<T> node = null;

        while (iterator.hasNext()) {
            node = iterator.next();
            i += node.getWeight();

            if (i >= goal) {
                return node.getObject();
            }
        }

        return node == null ? null : node.getObject();
    }

    public Set<T> getRandomSubset(int size) {
        return getRandomSubset(ThreadLocalRandom.current(), size);
    }

    public Set<T> getRandomSubset(Random random, int size) {
        if (size > size()) {
            throw new IllegalArgumentException("A random Subset may not be larger than the original Set! (" + size + " > " + size() + ")");
        }

        if (size == size()) {
            return internalSet.stream().map(WeightedNode::getObject).collect(Collectors.toSet());
        }

        Set<T> subset = new HashSet<>();

        while (subset.size() < size) {
            subset.add(getRandom(random));
        }

        return subset;
    }

    public Map<T, Float> toMap() {
        Map<T, Float> map = new HashMap<>();

        for (WeightedNode<T> node : internalSet) {
            map.put(node.getObject(), node.getWeight() / totalWeights);
        }

        return map;
    }

    public Stream<T> randomInfiniteStream() {
        return Stream.generate(this::getRandom);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();

        boolean first = true;

        for (WeightedNode<T> node : internalSet) {
            if (!first) {
                builder.append(", ");
            } else {
                first = false;
            }

            builder.append("(").append(node.getObject()).append(" | ").append(node.getWeight()).append(")");
        }

        return getClass().getSimpleName() + "{" + builder + "}";
    }
}
