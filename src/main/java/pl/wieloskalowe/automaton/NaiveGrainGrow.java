package pl.wieloskalowe.automaton;

import javafx.scene.paint.Color;
import pl.wieloskalowe.Board2D;
import pl.wieloskalowe.cell.Cell;
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

    //TODO Only on edge cells should be processed?/or cell dead
    //TODO przenieść sprawdzanie reguły przejscia do pojedynczej komórki ->
    @Override
    protected Cell getNextCellState(Cell cell, List<Cell> neighbours) {
        if (cell.isAlive()) {
            return cell;
        }

        if ((cell).isInclusion()) {
            return cell;
        }

        Color cellColor = Color.WHITE;

        if(neighbours.stream().allMatch(c -> c.getColor() == Color.WHITE || c.getColor().equals(cell.getColor()))) {
            return cell;
        }

        Map<Color, Integer> listOfColors = new HashMap<>();
        int maxCount = 0;

        for (Cell c : neighbours) { // TODO jeżeli wszystkie kolory sa równej ilości to jest zawsze nieżywa
            cellColor = c.getColor();
            if (cellColor != Color.WHITE) {
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

        //TODO zmienić stan komórki zamiast tworzenia: current state -> next state: -> update(thisstate = nextstate) -> rerender
        if (maxCount > 0 && cellColor != Color.BLACK)
            return new Cell(true, cellColor);
        else
            return cell;
    }
}
