package pl.wieloskalowe;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by ishfi on 02.05.2017.
 */
public class GameOfLife extends Automaton {

    public GameOfLife(Board2D board2D, MooreNeighborhood mooreNeighborhood) {
        super(board2D, mooreNeighborhood);
    }

    @Override
    protected CellBinary getNextCellState(CellBinary cell, Set<CellBinary> neighbours) {

        long neighborsCount = neighbours.stream().filter(CellBinary::isAlive).count();


        if (!cell.isAlive() && neighborsCount == 3) {
            return new CellBinary(true);
        }

        if (cell.isAlive() && (neighborsCount == 3 || neighborsCount == 2)) {
            return new CellBinary(true);
        }

        return new CellBinary(false);
    }

}