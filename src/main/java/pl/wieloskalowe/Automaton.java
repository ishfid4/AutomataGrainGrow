package pl.wieloskalowe;

import pl.wieloskalowe.neighborhoods.Neighborhood;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by ishfi on 03.05.2017.
 */

public abstract class Automaton {
    protected Board2D board2D;
    protected Neighborhood neighborhood;
    protected CoordinatesWrapper coordinatesWrapper = null;

    public Automaton(Board2D board2D, Neighborhood neighborhood) {
        this.board2D = board2D;
        this.neighborhood = neighborhood;
    }

    public Automaton(Board2D board2D, Neighborhood neighborhood, CoordinatesWrapper coordinatesWrapper) {
        this.board2D = board2D;
        this.neighborhood = neighborhood;
        this.coordinatesWrapper = coordinatesWrapper;
    }

    abstract protected Cell getNextCellState(Cell cell, Set<Cell> neighbours);

    public void oneIteration() {
        Set<CellCoordinates> coordinatesSet = board2D.getAllCoordinates();

        Board2D nextBoard = new Board2D(board2D);

        for (CellCoordinates cellCoordinates : coordinatesSet) {
//            CellBinary currentCell = (CellBinary) board2D.getCell(cellCoordinates);
            Cell currentCell = board2D.getCell(cellCoordinates);
            Set<CellCoordinates> coordinatesNeighbours =  neighborhood.cellNeighbors(cellCoordinates);

            if (coordinatesWrapper != null)
                coordinatesNeighbours = coordinatesWrapper.wrapCellCoordinates(coordinatesNeighbours);

            Set<Cell> neighbours = coordinatesNeighbours.stream()
                    .map(cord -> board2D.getCell(cord)).collect(Collectors.toSet());
//
//            Set<CellBinary> neighbours = neighborhood.cellNeighbors(cellCoordinates).stream()
//                    .map(cord -> (CellBinary) board2D.getCell(cord)).collect(Collectors.toSet());

            nextBoard.setCell(cellCoordinates, getNextCellState(currentCell, neighbours));
        }

        board2D = nextBoard;
    }

    public Board2D getBoard() {
        return board2D;
    }
}
