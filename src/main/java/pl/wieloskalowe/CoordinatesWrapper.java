package pl.wieloskalowe;

import javafx.util.Pair;

import java.util.ArrayList;
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

    public ArrayList<Pair<Integer, Integer>> wrapCellCoordinates (ArrayList<Pair<Integer, Integer>> cellCoordinatesList) {
        ArrayList<Pair<Integer, Integer>> wrappedCellCoordinatesList = new ArrayList<>();
        for (Pair<Integer, Integer> cellCoordinates: cellCoordinatesList) {
            int x = cellCoordinates.getKey(), y = cellCoordinates.getValue();
            if (cellCoordinates.getKey() < 0)
                x = width + cellCoordinates.getKey();

            if (cellCoordinates.getValue() < 0)
                y =(height + cellCoordinates.getValue());

            if (cellCoordinates.getKey() >= width)
                x = (cellCoordinates.getKey() - width);

            if (cellCoordinates.getValue() >= height)
                y = (cellCoordinates.getValue() - height);

            wrappedCellCoordinatesList.add(new Pair<>(x, y));
        }

        return wrappedCellCoordinatesList;
    }
}
