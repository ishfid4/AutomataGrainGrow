package pl.wieloskalowe;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by ishfi on 03.05.2017.
 */

public abstract class Automaton {
    protected Board2D board2D;
    protected Neighborhood neighborhood;

    public Automaton(Board2D board2D, Neighborhood neighborhood) {
        this.board2D = board2D;
        this.neighborhood = neighborhood;
    }


    abstract protected CellBinary getNextCellState(CellBinary cell, Set<CellBinary> neighbours);

    public void oneIteration() {
        Set<CellCoordinates> coordinatesSet = board2D.getAllCoordinates();

        Board2D nextBoard = new Board2D(board2D);

        for (CellCoordinates cellCoordinates : coordinatesSet) {
            CellBinary currentCell = (CellBinary) board2D.getCell(cellCoordinates);
            Set<CellBinary> neighbours = neighborhood.cellNeighbors(cellCoordinates).stream()
                    .map(cord -> (CellBinary) board2D.getCell(cord)).collect(Collectors.toSet());

            nextBoard.setCell(cellCoordinates, getNextCellState(currentCell, neighbours));
        }

        board2D = nextBoard;
    }

    public Board2D getBoard() {
        return board2D;
    }
}
