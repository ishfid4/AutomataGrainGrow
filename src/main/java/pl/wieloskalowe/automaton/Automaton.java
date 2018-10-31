package pl.wieloskalowe.automaton;

import javafx.util.Pair;
import pl.wieloskalowe.Board2D;
import pl.wieloskalowe.cell.Cell;
import pl.wieloskalowe.CoordinatesWrapper;
import pl.wieloskalowe.neighborhoods.Neighborhood;

import java.lang.reflect.Array;
import java.util.*;
import java.util.stream.Collectors;

public abstract class Automaton {
    protected Board2D board2D;
    protected Neighborhood neighborhood;
    protected CoordinatesWrapper coordinatesWrapper = null;
    protected boolean boardChanged;

    public Automaton(Board2D board2D, Neighborhood neighborhood) {
        this.board2D = board2D;
        this.neighborhood = neighborhood;
    }

    public Automaton(Board2D board2D, Neighborhood neighborhood, CoordinatesWrapper coordinatesWrapper) {
        this.board2D = board2D;
        this.neighborhood = neighborhood;
        this.coordinatesWrapper = coordinatesWrapper;
    }

    abstract protected Cell getNextCellState(Cell cell, ArrayList<Cell> neighbours);

    public synchronized void oneIteration() {
        boardChanged = false;

        Board2D nextBoard = new Board2D(board2D);


        for (int x = 0; x < board2D.getWidth(); x++) {
            for (int y = 0; y < board2D.getHeight(); y++) {
                Cell currentCell = board2D.getCell(x, y);
                ArrayList<Pair<Integer, Integer>> coordinatesNeighbours =  neighborhood.cellNeighbors(x, y);

                if (coordinatesWrapper != null)
                    coordinatesNeighbours = coordinatesWrapper.wrapCellCoordinates(coordinatesNeighbours);


                ArrayList<Cell> cellsNeighbors = new ArrayList<>(coordinatesNeighbours.size());
                for (Pair<Integer, Integer> coordinatesNeighbour : coordinatesNeighbours) {
                    cellsNeighbors.add(board2D.getCell(coordinatesNeighbour.getKey(), coordinatesNeighbour.getValue()));
                }

                nextBoard.setCell(x, y, getNextCellState(currentCell, cellsNeighbors));
            }
        }

        for (int x = 0; x < board2D.getWidth(); x++) {
            for (int y = 0; y < board2D.getHeight(); y++) {
                Cell currentCellB1 = board2D.getCell(x, y);
                Cell currentCellB2 = nextBoard.getCell(x, y);
                if (!currentCellB1.getColor().equals(currentCellB2.getColor()))
                    boardChanged = true;
            }
        }

        board2D = nextBoard;
    }

    public Board2D getBoard() {
        return board2D;
    }
}
