package pl.wieloskalowe.neighborhoods;

import java.util.ArrayList;

public class CircleNeighborhood implements Neighborhood {
    private int radius;

    public CircleNeighborhood(int radius) {
        this.radius = radius;
    }

    @Override
    public ArrayList<int[]> cellNeighbors(int cellsX, int cellsY) {
        ArrayList<int[]> neighbors = new ArrayList<>();

        for (int x = cellsX - radius; x <= cellsX + radius; x++) {
            for (int y = cellsY - radius; y <= cellsY + radius; y++) {
                if (!(y == cellsY && x == cellsX) && ((x - cellsX) * (x - cellsX) + (y - cellsY) * (y - cellsY) < radius * radius))
                    neighbors.add(new int[]{x, y});
            }
        }

        return neighbors;
    }

}
