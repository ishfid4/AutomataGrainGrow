package pl.wieloskalowe.neighborhoods;

import pl.wieloskalowe.CellCoordinates;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by ishfi on 02.05.2017.
 */
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
                if (y == cellCoordinates.getY() && x == cellCoordinates.getX())
                    continue;

                neighbors.add(new CellCoordinates(x,y));
            }
        }

        return neighbors;
    }

}
