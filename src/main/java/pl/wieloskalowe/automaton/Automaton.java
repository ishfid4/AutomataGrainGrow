package pl.wieloskalowe.automaton;

import pl.wieloskalowe.Board2D;
import pl.wieloskalowe.CoordinatesWrapper;
import pl.wieloskalowe.cell.Cell;
import pl.wieloskalowe.neighborhoods.Neighborhood;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public abstract class Automaton {
    protected Board2D board2D;
    protected Neighborhood neighborhood;
    protected CoordinatesWrapper coordinatesWrapper = null;
    protected boolean boardChanged = true;
    Board2D nextBoard;
    List<List<int[]>> mooreNeighPos;
    List<List<int[]>> vonNeumanNeighPos;
    List<List<int[]>> cornersOfMooreNeighPos;

    public Automaton(Board2D board2D, Neighborhood neighborhood) {
        this(board2D, neighborhood, null);
    }

    public Automaton(Board2D board2D, Neighborhood neighborhood, CoordinatesWrapper coordinatesWrapper) {
        this.board2D = board2D;
        this.neighborhood = neighborhood;
        this.coordinatesWrapper = coordinatesWrapper;
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

    public synchronized boolean oneIteration() {
        boardChanged = false;

        IntStream.range(0, board2D.width * board2D.height).parallel().forEach(i -> {
            List<List<Cell>> neighborhoods = new ArrayList<>();

            Cell current = board2D.getCell(i);
            if(current != board2D.getInitialCell()) {
                nextBoard.setCell(i, current);
                return;
            }

            List<Cell> neighborPos = mooreNeighPos.get(i).stream().map(coords ->
                    board2D.getCell(coords[0], coords[1])).collect(Collectors.toCollection(ArrayList::new));
            neighborhoods.add(neighborPos);

            // Additional two neighborhoods
            if (!vonNeumanNeighPos.isEmpty() && !cornersOfMooreNeighPos.isEmpty()) {
                List<Cell> neighborPosVonNeuman = vonNeumanNeighPos.get(i).stream().map(coords ->
                        board2D.getCell(coords[0], coords[1])).collect(Collectors.toCollection(ArrayList::new));
                List<Cell> neighborPosCornersMoore = cornersOfMooreNeighPos.get(i).stream().map(coords ->
                            board2D.getCell(coords[0], coords[1])).collect(Collectors.toCollection(ArrayList::new));

                neighborhoods.add(neighborPosVonNeuman);
                neighborhoods.add(neighborPosCornersMoore);
            }

            Cell nextCell = getNextCellState(board2D.getCell(i), neighborhoods);
            if(current != nextCell) {
                nextBoard.setCell(i, nextCell);
                boardChanged = true;
            }
        });

        Board2D swapBoardTmp = board2D;

        board2D = nextBoard;
        nextBoard = swapBoardTmp;

        return boardChanged;
    }

    Board2D getBoard() {
        return board2D;
    }

    public void syncNextBoard() {
        nextBoard = new Board2D(board2D);
    }

    public Neighborhood getNeighborhood() {
        return neighborhood;
    }
}
