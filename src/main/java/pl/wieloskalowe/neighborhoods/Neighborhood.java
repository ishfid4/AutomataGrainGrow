package pl.wieloskalowe.neighborhoods;

import java.util.ArrayList;

public interface Neighborhood {
    ArrayList<int[]> cellNeighbors(int cellsX, int cellsY);
}
