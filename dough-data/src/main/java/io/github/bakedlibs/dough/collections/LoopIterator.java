package io.github.bakedlibs.dough.collections;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class LoopIterator<T> implements Iterator<T>, Streamable<T> {
    private List<T> list;
    private int size;

    private int index;

    /**
     * This will create a new instance of {@link LoopIterator} that operates on a copy of the given {@link Collection}
     * 
     * @param collection
     *            The collection to create a {@link LoopIterator} of
     */
    public LoopIterator(Collection<T> collection) {
        init(collection);
    }

    /**
     * This will create a new instance of {@link LoopIterator} that will operate on the elements of the given
     * {@link Streamable}
     * 
     * @param streamable
     *            The streamable to create a {@link LoopIterator} of
     */
    public LoopIterator(Streamable<T> streamable) {
        init(streamable.stream().collect(Collectors.toList()));
    }

    private void init(Collection<T> collection) {
        size = collection.size();

        if (size > 0) {
            list = new ArrayList<>(collection);
        } else {
            list = null;
        }
    }

    @Override
    public boolean hasNext() {
        return size > 0;
    }

    @Override
    public T next() {
        if (list == null) {
            throw new NoSuchElementException("The given collection was empty.");
        } else if (index >= size) {
            index = 0;
        }

        return list.get(index++);
    }

    /**
     * This method loops this {@link LoopIterator} until a matching item has been
     * found. It will not loop more than once though.
     * 
     * @param predicate
     *            The {@link Predicate} to use for this search
     * @return An {@link Optional} describing the result
     */
    public Optional<T> find(Predicate<T> predicate) {
        if (!hasNext()) {
            return Optional.empty();
        }

        int start = index;

        T current = next();
        while (index != start || predicate.test(current)) {
            current = next();
        }

        if (predicate.test(current)) {
            return Optional.of(current);
        } else
            return Optional.empty();
    }

    @Override
    public Stream<T> stream() {
        return Stream.generate(this::next);
    }

    public int getIndex() {
        return this.index;
    }

}
