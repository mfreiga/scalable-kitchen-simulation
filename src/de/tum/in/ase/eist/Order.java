package de.tum.in.ase.eist;

import java.time.Duration;

// IMPORTANT: Please don't change this implementation
// Note: Comparable is used to ensure correct order in the data structure. 
class Order implements Comparable<Order> {
    private int ID;
    private Duration processingDuration;
    private Priority priority;

    public Order(int ID, Duration duration, Priority priority) {
        this.ID = ID;
        this.processingDuration = duration;
        this.priority = priority;
    }

    public void process() throws InterruptedException {
        Thread.sleep(processingDuration.toMillis());
    }

    @Override
    public String toString() {
        return String.format("Order[ID=%d]", ID);
    }

    @Override
    public int compareTo(Order o) {
        return this.priority.compareTo(o.priority);
    }

    public int getID() {
        return this.ID;
    }

    public Priority getPriority() {
        return priority;
    }

    public Duration getProcessingDuration() {
        return processingDuration;
    }
}
