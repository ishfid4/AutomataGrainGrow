package pl.wieloskalowe.automaton;

import pl.wieloskalowe.Board2D;
import pl.wieloskalowe.cell.Cell;
import pl.wieloskalowe.neighborhoods.Neighborhood;

import java.util.ArrayList;
import java.util.List;

public abstract class Automaton {
    protected Board2D board2D;
    protected Neighborhood neighborhood;
    protected boolean boardChanged = true;
    Board2D nextBoard;
    List<List<int[]>> mooreNeighPos;
    List<List<int[]>> vonNeumanNeighPos;
    List<List<int[]>> cornersOfMooreNeighPos;

    public Automaton(Board2D board2D, Neighborhood neighborhood) {
        this.board2D = board2D;
        this.neighborhood = neighborhood;
        nextBoard = new Board2D(board2D);
        mooreNeighPos = new ArrayList<>();
        vonNeumanNeighPos = new ArrayList<>();
        cornersOfMooreNeighPos = new ArrayList<>();

        for(int x = 0; x < board2D.height; ++x) {
            for(int y = 0; y < board2D.width; ++y) {
                mooreNeighPos.add(neighborhood.cellNeighbors(x, y));
            }
        }
    }

    abstract protected Cell getNextCellState(Cell cell, List<List<Cell>> neighbours);

    abstract public boolean oneIteration();

    Board2D getBoard() {
        return board2D;
    }

    public void syncNextBoard() {
        nextBoard = new Board2D(board2D);
    }

    public Neighborhood getNeighborhood() {
        return neighborhood;
    }

    public void syncBoards(Board2D board2D) {
        this.board2D = new Board2D(board2D);
        syncNextBoard();
    }
}
