package pl.wieloskalowe.automaton;

import javafx.scene.paint.Color;
import javafx.util.Pair;
import pl.wieloskalowe.Board2D;
import pl.wieloskalowe.cell.Cell;
import pl.wieloskalowe.CoordinatesWrapper;
import pl.wieloskalowe.neighborhoods.Neighborhood;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public abstract class Automaton {
    protected Board2D board2D;
    protected Neighborhood neighborhood;
    protected CoordinatesWrapper coordinatesWrapper = null;
    protected boolean boardChanged = true;
    int currentX, currentY;
    Board2D nextBoard;
    List<List<Pair<Integer, Integer>>> neighPos;

    public Automaton(Board2D board2D, Neighborhood neighborhood) {
        this(board2D, neighborhood, null);
    }

    public Automaton(Board2D board2D, Neighborhood neighborhood, CoordinatesWrapper coordinatesWrapper) {
        this.board2D = board2D;
        this.neighborhood = neighborhood;
        this.coordinatesWrapper = coordinatesWrapper;
        nextBoard = new Board2D(board2D);
        neighPos = new ArrayList<>();

        for(int y = 0; y < board2D.height; ++y) {
            for(int x = 0; x < board2D.width; ++x) {
                neighPos.add(neighborhood.cellNeighbors(x, y));
            }
        }
    }

    abstract protected Cell getNextCellState(Cell cell, List<Cell> neighbours);

    public synchronized boolean oneIteration() {
        boardChanged = false;

        //nextBoard.clear();

        IntStream.range(0, board2D.width * board2D.height).parallel().forEach(i -> {
            int x = i % board2D.width;
            int y = i / board2D.width;

            Cell current = board2D.getCell(x, y);
            if(current.getColor() != Color.WHITE) {
                nextBoard.setCell(x, y, current);
                return;
            }

            List<Cell> neighborPos = neighPos.get(i).parallelStream().map(coords ->
                    board2D.getCell(coords.getKey(), coords.getValue())).collect(Collectors.toCollection(ArrayList::new));

            nextBoard.setCell(x, y, getNextCellState(board2D.getCell(x, y), neighborPos));
        });

        Board2D swapBoardTmp = board2D;

        board2D = nextBoard;
        nextBoard = swapBoardTmp;

        return true;
    }

    public Board2D getBoard() {
        return board2D;
    }
}
