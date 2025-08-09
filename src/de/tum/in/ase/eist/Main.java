package de.tum.in.ase.eist;

import java.nio.file.Path;
import java.nio.file.Paths;

public class Main {
    private static final int NUM_KITCHENS = 3;
    private static final int NUM_CHEFS_PER_KITCHEN = 5;

    public static void main(String[] args) {
        RestaurantManager restaurantManager = new RestaurantManager(NUM_KITCHENS, NUM_CHEFS_PER_KITCHEN);
        String orderFileString = "requests.csv";
        Path orderFile = Paths.get(orderFileString);
        restaurantManager.start(orderFile);
    }
}
