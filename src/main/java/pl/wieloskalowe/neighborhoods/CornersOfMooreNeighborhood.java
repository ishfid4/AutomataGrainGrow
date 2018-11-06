package pl.wieloskalowe.neighborhoods;

import javafx.util.Pair;
import java.util.ArrayList;

public class CornersOfMooreNeighborhood implements Neighborhood{
    private int radius;

    public CornersOfMooreNeighborhood(int radius) {
        this.radius = radius;
    }

    @Override
    public ArrayList<Pair<Integer, Integer>> cellNeighbors(int cellsX, int cellsY) {
        ArrayList<Pair<Integer, Integer>> neighbours = new ArrayList<>();

        //TODO: redo this BS
        neighbours.add(new Pair<>(cellsX - 1, cellsY - 1));
        neighbours.add(new Pair<>(cellsX + 1, cellsY - 1));
        neighbours.add(new Pair<>(cellsX - 1, cellsY + 1));
        neighbours.add(new Pair<>(cellsX + 1, cellsY + 1));

        return  neighbours;
    }
}
