package pl.wieloskalowe.automaton;

import javafx.util.Pair;
import pl.wieloskalowe.Board2D;
import pl.wieloskalowe.CoordinatesWrapper;
import pl.wieloskalowe.cell.Cell;
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
        Cell initialCell = board2D.getInitialCell();

        if(cell != initialCell) return cell;

        if(neighbours.stream().allMatch(c -> c == initialCell)) {
            return cell;
        }

        Map<Cell, Pair<Cell, Integer>> listOfColors = new HashMap<>();

        for (Cell c : neighbours) {
            if (c != board2D.getInitialCell()) {
                Pair<Cell, Integer> currentCount = listOfColors.getOrDefault(c, new Pair<>(cell, 0));
                listOfColors.put(c, new Pair<>(c, currentCount.getValue() + 1));
            }
        }

        return Collections.max(listOfColors.values(), Comparator.comparingInt(Pair::getValue)).getKey();
    }
}
