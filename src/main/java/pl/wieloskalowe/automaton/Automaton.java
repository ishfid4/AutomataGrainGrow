package pl.wieloskalowe.automaton;

import javafx.util.Pair;
import pl.wieloskalowe.Board2D;
import pl.wieloskalowe.cell.Cell;
import pl.wieloskalowe.CoordinatesWrapper;
import pl.wieloskalowe.neighborhoods.Neighborhood;

import java.util.*;

public abstract class Automaton {
    protected Board2D board2D;
    protected Neighborhood neighborhood;
    protected CoordinatesWrapper coordinatesWrapper = null;
    protected boolean boardChanged;
    int currentX, currentY;
    Board2D nextBoard;

    private ArrayList<Cell> cellsNeighbors;

    public Automaton(Board2D board2D, Neighborhood neighborhood) {
        this(board2D, neighborhood, null);
    }

    public Automaton(Board2D board2D, Neighborhood neighborhood, CoordinatesWrapper coordinatesWrapper) {
        this.board2D = board2D;
        this.neighborhood = neighborhood;
        this.coordinatesWrapper = coordinatesWrapper;
        cellsNeighbors = new ArrayList<>(8);
        nextBoard = new Board2D(board2D);
    }

    abstract protected Cell getNextCellState(Cell cell, ArrayList<Cell> neighbours);

    public synchronized boolean oneIteration() {
        boardChanged = false;

        nextBoard.clear();

        for (int x = 0; x < board2D.getWidth(); ++x) {
            for (int y = 0; y < board2D.getHeight(); ++y) {
                Cell currentCell = board2D.getCell(x, y);
                ArrayList<Pair<Integer, Integer>> coordinatesNeighbours =  neighborhood.cellNeighbors(x, y);

                if (coordinatesWrapper != null)
                    coordinatesNeighbours = coordinatesWrapper.wrapCellCoordinates(coordinatesNeighbours);


                cellsNeighbors.clear();
                for (Pair<Integer, Integer> coordinatesNeighbour : coordinatesNeighbours) {
                    cellsNeighbors.add(board2D.getCell(coordinatesNeighbour.getKey(), coordinatesNeighbour.getValue()));
                }

                currentX = x;
                currentY = y;

                Cell nextCellState = getNextCellState(currentCell, cellsNeighbors);

                nextBoard.setCell(x, y, nextCellState);

                if(!boardChanged && !nextCellState.equals(currentCell))
                    boardChanged = true;
            }
        }

        Board2D swapBoardTmp = board2D;

        board2D = nextBoard;
        nextBoard = swapBoardTmp;

        return boardChanged;
    }

    public Board2D getBoard() {
        return board2D;
    }
}
