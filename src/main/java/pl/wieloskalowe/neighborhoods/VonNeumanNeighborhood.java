package pl.wieloskalowe.neighborhoods;

import javafx.util.Pair;

import java.util.ArrayList;

public class VonNeumanNeighborhood implements Neighborhood{
    private int radius;

    public VonNeumanNeighborhood(int radius) {
        this.radius = radius;
    }

    @Override
    public ArrayList<int[]> cellNeighbors(int cellsX, int cellsY) {
//        ArrayList<Pair<Integer, Integer>> neighbours = new ArrayList<>();
//
//        for (int x = 0; x <= radius; x++) {
//            int y = -radius + x;
//
//            for (; y <= radius - x; y++) {
//                if (cellsX == x && cellsY == y) continue;
//
//                neighbours.add(new Pair<>(cellsX + x, cellsY + y));
//                if (x != 0) {
//                    neighbours.add(new Pair<>(cellsX - x, cellsY + y));
//                }
//            }
//        }

        return  null;
    }
}
