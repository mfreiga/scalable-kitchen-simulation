package de.tum.in.ase.eist;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

class RestaurantManager {

    private List<Kitchen> kitchens;
    private ExecutorService threadpool = Executors.newFixedThreadPool(2);

    public RestaurantManager(int numKitchens, int numChefsPerKitchen) {
        this.kitchens = new ArrayList<>();
        for (int i = 0; i < numKitchens; ++i) {
            Kitchen kitchen = new Kitchen("Kitchen " + i, numChefsPerKitchen);
            this.kitchens.add(kitchen);
            kitchen.start();
        }
    }

    public void start(Path orderFile) {
        List<Order> orders = parseOrdersFromFile(orderFile);
        startAssigningOrders(orders);
        startMonitoring();
    }

    /**
     * Parse/deserialize a list of orders from a file.
     * Each line contains one order, and each order is formatted as
     * "<ID>,<ProcessingDuration>,<Priority>".
     * The processing duration should be in milliseconds.
     * 
     * May throw a runtime exception if an IO error occurs during reading of the
     * file.
     *
     * Example: "2,1000,LOW"
     * 
     * @return List of orders.
     */
    public static List<Order> parseOrdersFromFile(Path filePath) {
        // TODO: Fill this function with respect to the comment above it
        List <Order> orders = new ArrayList<>();

        try {
            List <String> content = Files.readAllLines(filePath);
            for(String line : content){
                String[] parts = line.split(",");
                int id = Integer.parseInt(parts[0]);
                Duration duration = Duration.ofMillis(Long.parseLong(parts[1]));
                Priority priority = Priority.valueOf(parts[2]);

                Order order = new Order(id, duration, priority);

                orders.add(order);
            }


        } catch (RuntimeException | IOException e) {
            throw new RuntimeException(e);
        }
        return orders;

    }

    /**
     * Used for initial assignments of orders to kitchens.
     * This method distributes orders to kitchens in a deterministic, round-robin
     * fashion based on the order's index in the list. It ensures that during the
     * initial assignment, orders are evenly spread across the available kitchens.
     * 
     * @param orders     List of orders to be assigned.
     * @param orderIndex The index of the order in the list of orders.
     */
    public void initiallyAssignOrderToKitchen(List<Order> orders, int orderIndex) {
        Order order = orders.get(orderIndex);

        // Simplistic hashing with orderIndex
        int assignedKitchenIndex = orderIndex % kitchens.size();
        kitchens.get(assignedKitchenIndex).assignOrder(order);
    }


    private static final int OVERLOADED_KITCHEN_THRESHOLD = 5;

    /**
     * Find all overloaded kitchens. A kitchen is considered overloaded if its
     * (unfinished) order count is larger than the average order count of all
     * kitchens by a threshold defined as OVERLOADED_KITCHEN_THRESHOLD.
     * Note that thread-safety (using locks) should be handled in/implemented by
     * the methods in `Order` and `Kitchen` that this function should call, so
     * there is no need for thread-safety mechanisms here.
     *
     * Example:
     * Order count of 5 sample kitchens: 20 20 20 0 0
     * Average: 60/5 = 12
     * 20 > 12+5
     * So kitchen(s) with 20 orders are overloaded
     * 
     * @return List of overloaded kitchens.
     */
    public List<Kitchen> findOverloadedKitchens() {
        // TODO: Fill this function with respect to the comment above it
        List<Kitchen> overloaded = new ArrayList<>();

        var totalOrders = 0;
        var numOfKitchen = 0;
        for(Kitchen kitchen: kitchens){
            totalOrders+= kitchen.getOrderCount();
            numOfKitchen++;
        }
        var average = totalOrders / numOfKitchen;
        var threshold = average + OVERLOADED_KITCHEN_THRESHOLD;

        for(Kitchen k: kitchens){
            if (k.getOrderCount() > threshold){
                overloaded.add(k);
            }
        }
        return overloaded;
    }

    /**
     * This function, if there is at least one kitchen that is not overloaded, extracts one order
     * from each overloaded kitchen and adds it to a non-overloaded kitchen.
     * @param overloadedKitchens List of overloaded kitchens.
     */
    public void rebalanceOrders(List<Kitchen> overloadedKitchens) {
        // TODO: Fill this function with respect to the comment above it
        // find non overloaded kitchen
        List<Kitchen> notOverloaded = new ArrayList<>();
        for (Kitchen k : kitchens) {
            if (!overloadedKitchens.contains(k)) {
                notOverloaded.add(k);
            }
        }

        if (notOverloaded.isEmpty()) {
            return;
        }

        List<Order> orders = new ArrayList<>();
        for (Kitchen overloaded : overloadedKitchens) {
            orders.addAll(overloaded.extractOrders(1));
        }

        for (int j = 0; j < orders.size(); j++) {
            Kitchen target = notOverloaded.get(j % notOverloaded.size());
            target.assignOrder(orders.get(j));
        }

    }

    private static final int ORDER_ASSIGNMENT_INTERVAL_MS = 1000;

    public void startAssigningOrders(List<Order> orders) {
        threadpool.submit(() -> {
            for (int i = 0; i < orders.size(); ++i) {
                initiallyAssignOrderToKitchen(orders, i);

                try {
                    // Sleep for a while so kitchens have time to process orders
                    Thread.sleep(ORDER_ASSIGNMENT_INTERVAL_MS);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

            }
        });
    }

    private static final int MONITORING_INTERVAL_MS = 5000;

    public void startMonitoring() {
        threadpool.submit(() -> {
            while (true) {
                try {
                    Thread.sleep(MONITORING_INTERVAL_MS);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

                List<Kitchen> overloadedKitchens = findOverloadedKitchens();
                rebalanceOrders(overloadedKitchens);
            }
        });
    }

    public List<Kitchen> getKitchens() {
        return kitchens;
    }

}


