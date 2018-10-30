package pl.wieloskalowe.neighborhoods;

import javafx.util.Pair;

import java.util.ArrayList;

public class MooreNeighborhood implements Neighborhood {
    private int radius;

    public MooreNeighborhood(int radius) {
        this.radius = radius;
    }

    @Override
    public ArrayList<Pair<Integer, Integer>> cellNeighbors(int cellsX, int cellsY) {
        ArrayList<Pair<Integer, Integer>> neighbors = new ArrayList<>();

        for (int x = cellsX - radius; x <= cellsX + radius; x++) {
            for (int y = cellsY - radius; y <= cellsY + radius; y++) {
                if (!(y == cellsY && x == cellsX))
                    neighbors.add(new Pair<>(x,y));
            }
        }

        return neighbors;
    }

}
