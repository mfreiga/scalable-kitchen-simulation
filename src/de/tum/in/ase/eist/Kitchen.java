package de.tum.in.ase.eist;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class Kitchen {
    private List<Thread> chefThreads;
    private Queue<Order> orderQueue;
    private String name;
    private final LoggingReadWriteLock lock;

    public Kitchen(String name, int chefCount) {
        this.name = name;
        // Using PriorityQueue for auto-sorting on insertion
        this.orderQueue = new PriorityQueue<>();
        this.chefThreads = new ArrayList<>(chefCount);
        this.lock = new LoggingReadWriteLock();
        for (int i = 0; i < chefCount; i++) {
            Chef chef = new Chef(name + "-Chef-" + (i + 1), orderQueue);
            Thread thread = new Thread(chef);
            chefThreads.add(thread);
        }
    }

    public void start() {
        chefThreads.forEach(Thread::start);
    }

    /**
     * Assign an order to this kitchen. Make sure to do this in a thread-safe way.
     * 
     * @param order The order to assign to this kitchen
     */
    public void assignOrder(Order order) {
        // TODO: Fill this function with respect to the comment above it
        lock.lockWrite();
        try{
            orderQueue.add(order);
        }finally {
            lock.unlockWrite();

        }

    }

    /**
     * Extracts a specified number of orders from the order queue.
     * 
     * Attempts to retrieve and remove the desired number of orders from the front
     * of the order queue.
     * If there are fewer orders in the queue than requested, retrieves whatever is
     * available.
     * Make sure the usage of the orders is thread-safe.
     *
     * @param count The number of orders to extract from the queue.
     * @return A list of extracted orders.
     */
    public List<Order> extractOrders(int count) {
        // TODO: Fill this function with respect to the comment above it
        List<Order> extractedOrders = new ArrayList<>();
        int j  = 0;
        lock.lockWrite();
        try{
        while(!orderQueue.isEmpty() && j < count){
                Order order = orderQueue.poll();
                extractedOrders.add(order);
                j++;
        }}finally {
            lock.unlockWrite();
        }
        return extractedOrders;
    }

    /**
     * Get the order count (all pending/queued) orders in the current kitchen.
     * Think about which type of synchronization primitive makes most sense here.
     * 
     * @return order count
     */
    public int getOrderCount() {
        // TODO: Fill this function with respect to the comment above it
        lock.lockRead();
        int count;
        try{
            count = orderQueue.size();
        }finally {
            lock.unlockRead();
        }
        return count;
    }

    public Queue<Order> getOrderQueue() {
        return orderQueue;
    }

    // Getter for name
    public String getName() {
        return name;
    }

    // Getter for lock
    public LoggingReadWriteLock getLock() {
        return lock;
    }
}
