package pl.wieloskalowe.neighborhoods;

import javafx.util.Pair;

import java.util.ArrayList;

public interface Neighborhood {
    ArrayList<Pair<Integer, Integer>> cellNeighbors(int cellsX, int cellsY);
}
