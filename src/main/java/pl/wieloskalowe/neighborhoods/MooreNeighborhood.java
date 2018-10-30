package pl.wieloskalowe.neighborhoods;

import pl.wieloskalowe.cell.CellCoordinates;

import java.util.HashSet;
import java.util.Set;

public class MooreNeighborhood implements Neighborhood {
    private int radius;

    public MooreNeighborhood(int radius) {
        this.radius = radius;
    }

    @Override
    public Set<CellCoordinates> cellNeighbors(CellCoordinates cellCoordinates) {
        Set<CellCoordinates> neighbors = new HashSet<>();

        for (int x = cellCoordinates.getX() - radius; x <= cellCoordinates.getX() + radius; x++) {
            for (int y = cellCoordinates.getY() - radius; y <= cellCoordinates.getY() + radius; y++) {
                if (!(y == cellCoordinates.getY() && x == cellCoordinates.getX()))
                    neighbors.add(new CellCoordinates(x,y));
            }
        }

        return neighbors;
    }

}
