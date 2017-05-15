package pl.wieloskalowe;

import pl.wieloskalowe.neighborhoods.Neighborhood;

import java.util.Set;

/**
 * Created by ishfi on 02.05.2017.
 */
public class GameOfLife extends Automaton {

    public GameOfLife(Board2D board2D, Neighborhood neighborhood) {
        super(board2D, neighborhood);
    }

    public GameOfLife(Board2D board2D, Neighborhood neighborhood, CoordinatesWrapper coordinatesWrapper) {
        super(board2D, neighborhood, coordinatesWrapper);
    }

    @Override
    protected CellBinary getNextCellState(Cell cell, Set<Cell> neighbours) {

        long neighborsCount = neighbours.stream().filter(Cell::isAlive).count();


        if (!cell.isAlive() && neighborsCount == 3) {
            return new CellBinary(true);
        }

        if (cell.isAlive() && (neighborsCount == 3 || neighborsCount == 2)) {
            return new CellBinary(true);
        }

        return new CellBinary(false);
    }

}