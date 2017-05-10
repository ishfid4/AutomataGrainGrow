package pl.wieloskalowe;

import java.util.Set;

/**
 * Created by ishfi on 10.05.2017.
 */
public class CoordinatesWrapper {
    protected int width, height;

    public CoordinatesWrapper(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public Set<CellCoordinates> wrapCellCoordinates (Set<CellCoordinates> cellCoordinatesSet) {
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

        return cellCoordinatesSet;
    }
}
