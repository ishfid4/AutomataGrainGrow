package pl.wieloskalowe.neighborhoods;

import java.util.ArrayList;

public class CornersOfMooreNeighborhood implements Neighborhood{
    private int radius;

    public CornersOfMooreNeighborhood(int radius) {
        this.radius = radius;
    }

    @Override
    public ArrayList<int[]> cellNeighbors(int cellsX, int cellsY) {
        ArrayList<int[]> neighbours = new ArrayList<>();
        for (int i = 1; i <= radius; ++i){
            //TODO is there any other way to do this?
            neighbours.add(new int[]{cellsX - i, cellsY - i});
            neighbours.add(new int[]{cellsX + i, cellsY - i});
            neighbours.add(new int[]{cellsX - i, cellsY + i});
            neighbours.add(new int[]{cellsX + i, cellsY + i});
        }

        return neighbours;
    }
}
