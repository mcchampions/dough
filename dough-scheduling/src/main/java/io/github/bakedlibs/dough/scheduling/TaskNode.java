package io.github.bakedlibs.dough.scheduling;

import java.util.function.IntConsumer;

import org.apache.commons.lang.Validate;

// TODO: Convert to Java 16 record
class TaskNode {

    private final IntConsumer runnable;
    private final boolean asynchronous;
    private int delay = 0;
    private TaskNode nextNode;

    protected TaskNode(IntConsumer consumer, boolean async) {
        this.runnable = consumer;
        this.asynchronous = async;
    }

    protected TaskNode(IntConsumer consumer, int delay, boolean async) {
        this.runnable = consumer;
        this.delay = delay;
        this.asynchronous = async;
    }

    protected boolean hasNextNode() {
        return nextNode != null;
    }

    public TaskNode getNextNode() {
        return nextNode;
    }

    public void setNextNode(TaskNode node) {
        this.nextNode = node;
    }

    public void execute(int index) {
        runnable.accept(index);
    }

    public boolean isAsynchronous() {
        return asynchronous;
    }

    public int getDelay() {
        return delay;
    }

    public void setDelay(int delay) {
        Validate.isTrue(delay >= 0, "The delay cannot be negative.");

        this.delay = delay;
    }

}
