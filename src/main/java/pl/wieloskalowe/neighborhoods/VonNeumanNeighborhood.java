package pl.wieloskalowe.neighborhoods;

import java.util.ArrayList;

public class VonNeumanNeighborhood implements Neighborhood{
    private int radius;

    public VonNeumanNeighborhood(int radius) {
        this.radius = radius;
    }

    @Override
    public ArrayList<int[]> cellNeighbors(int cellsX, int cellsY) {
        ArrayList<int[]> neighbours = new ArrayList<>();

        for (int x = 0; x <= radius; x++) {
            int y = -radius + x;

            for (; y <= radius - x; y++) {
                if (cellsX == x && cellsY == y) continue;

                neighbours.add(new int[]{cellsX + x, cellsY + y});
                if (x != 0) {
                    neighbours.add(new int[]{cellsX - x, cellsY + y});
                }
            }
        }

        return  neighbours;
    }
}
