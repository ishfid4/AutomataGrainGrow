package pl.wieloskalowe.neighborhoods;

import pl.wieloskalowe.CellCoordinates;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by ishfi on 10.05.2017.
 */
public class VonNeumanNeighborhood implements Neighborhood{
    private int radius;

    public VonNeumanNeighborhood(int radius) {
        this.radius = radius;
    }

    @Override
    public Set<CellCoordinates> cellNeighbors(CellCoordinates cellCoordinates) {
        Set<CellCoordinates> neighbours = new HashSet<>();

        for (int x = 0; x <= radius; x++) {
            int tmpX = cellCoordinates.getY() + x;
            int y = -radius + x;

            for (; y <= radius - x; y++) {
                int tmpY = cellCoordinates.getY() + y;

                if (tmpX == 0 && tmpY == 0) continue;

                neighbours.add(new CellCoordinates(tmpX, tmpY));
                if (x != 0) {
                    neighbours.add(new CellCoordinates(-tmpX, tmpY));
                }
            }
        }

        return  neighbours;
    }
}

