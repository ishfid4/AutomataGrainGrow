package pl.wieloskalowe.automaton;

import javafx.scene.paint.Color;
import pl.wieloskalowe.Board2D;
import pl.wieloskalowe.cell.Cell;
import pl.wieloskalowe.cell.CellGrain;
import pl.wieloskalowe.CoordinatesWrapper;
import pl.wieloskalowe.neighborhoods.Neighborhood;

import java.util.*;

public class NaiveGrainGrow extends Automaton {
    public NaiveGrainGrow(Board2D board2D, Neighborhood neighborhood) {
        super(board2D, neighborhood);
    }

    public NaiveGrainGrow(Board2D board2D, Neighborhood neighborhood, CoordinatesWrapper coordinatesWrapper) {
        super(board2D, neighborhood, coordinatesWrapper);
    }

    @Override
    protected CellGrain getNextCellState(Cell cell, Set<Cell> neighbours) {
        if (cell.isAlive()) {
            return cell.copyGrain();
        } else {
            Color cellColor = Color.color(1, 1, 1);

            Map<Color, Integer> listOfColors = new HashMap<>();
            int maxCount = 0;

            for (Cell c : neighbours) {
                CellGrain cellGrain = c.copyGrain();
                cellColor = cellGrain.getColor();
                if (!cellColor.equals(Color.color(1, 1, 1))) {
                    if (listOfColors.containsKey(cellColor)) {
                        int tmp = listOfColors.get(cellColor);
                        tmp++;
                        listOfColors.replace(cellColor, tmp);
                    } else {
                        listOfColors.put(cellColor, 1);
                    }
                }
            }

            for (Color col : listOfColors.keySet()) {
                if (listOfColors.get(col) >= maxCount) {
                    maxCount = listOfColors.get(col);
                    cellColor = col;
                }
            }

            if (maxCount > 0)
                return new CellGrain(true, cellColor);
            else
                return new CellGrain();
        }
    }
}
