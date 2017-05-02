package pl.wieloskalowe;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by ishfi on 02.05.2017.
 */
public class MooreNeighborhood {
    private int radius, width, height;
    private boolean wrap;

    public MooreNeighborhood(int radius, int width, int height, boolean wrap) {
        this.radius = radius;
        this.width = width;
        this.height = height;
        this.wrap = wrap;
    }

    public Set<CellCoordinates> cellNeighbors(CellCoordinates cellCoordinates) {
        Set<CellCoordinates> neighbors = new HashSet<>();

        for (int x = cellCoordinates.getX() - radius; x <= cellCoordinates.getX() + radius; x++) {
            for (int y = cellCoordinates.getY() - radius; y <= cellCoordinates.getY() + radius; y++) {
                if (y == cellCoordinates.getY() && x == cellCoordinates.getX())
                    continue;

                neighbors.add(new CellCoordinates(x,y));
            }
        }

        if (wrap) wrapCellCoordinates(neighbors);

        return neighbors;
    }

    private void wrapCellCoordinates (Set<CellCoordinates> cellCoordinatesSet) {
        for (CellCoordinates cellCoordinates: cellCoordinatesSet) {
            if (cellCoordinates.getX() < 0)
                cellCoordinates.setX(width + cellCoordinates.getX());

            if (cellCoordinates.getY() < 0)
                cellCoordinates.setY(height + cellCoordinates.getY());

            if (cellCoordinates.getX() >= width)
                cellCoordinates.setX(cellCoordinates.getX() - width);

            if (cellCoordinates.getY() >= height)
                cellCoordinates.setY(cellCoordinates.getY() - height);
        }
    }

}
