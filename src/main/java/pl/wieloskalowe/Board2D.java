package pl.wieloskalowe;

import javafx.scene.paint.Color;
import pl.wieloskalowe.cell.Cell;
import pl.wieloskalowe.neighborhoods.Neighborhood;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Board2D {
    private ArrayList<Cell> cellBoard2D;
    public int width, height;
    private Cell outerCell;
    private Cell initialCell;
    private Cell inclusionCell;

    List<Cell> precomputedCells;

    public Board2D(int width, int height, Cell outerCell, Cell initialCell) {
        this.width = width;
        this.height = height;
        this.outerCell = outerCell;
        this.cellBoard2D = new ArrayList<>(width * height);
        this.initialCell = initialCell;
        this.inclusionCell = new Cell(true, Color.BLACK);
        this.precomputedCells = new ArrayList<>();

        if (initialCell != null) {
            for (int x = 0; x < width; ++x) {
                for (int y = 0; y < height; ++y) {
                    this.cellBoard2D.add(initialCell);
                }
            }
        }
    }

    public Board2D(Board2D board2D) {
        this.width = board2D.width;
        this.height = board2D.height;
        this.outerCell = board2D.outerCell;
        this.cellBoard2D = new ArrayList<>(board2D.cellBoard2D);
        this.initialCell = board2D.initialCell;
        this.inclusionCell = board2D.inclusionCell;
        this.precomputedCells = board2D.precomputedCells;
    }

    public List<Cell> precomputeCells(int n) {
        Random random = new Random();
        precomputedCells.clear();

        for(int i = 0; i < n; ++i) {
            Color randomColor = Color.color(random.nextFloat(), random.nextFloat(), random.nextFloat());
            while(randomColor == Color.BLACK || randomColor == Color.WHITE)
                randomColor = Color.color(random.nextFloat(), random.nextFloat(), random.nextFloat());

            Cell randomCell = new Cell(true, randomColor);
            precomputedCells.add(randomCell);
        }

        return precomputedCells;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public void setCell(int x, int y, Cell cell){
        //Neighborhood returns offset to specific cell so it can be out of bounds
        if (x >=0 && x < width && y >=0 && y < height)
            cellBoard2D.set(x * width + y, cell); //TODO swap x with y
    }

    public Cell getCell(int x, int y) {
        //Neighborhood returns offset to specific cell so it can be out of bounds
        if (x >=0 && x < width && y >=0 && y < height) {
            Cell cell = cellBoard2D.get(x * width + y); //TODO swap x with y
            return cell;
        }

        return initialCell;
    }

    public Cell getCell(int i) {
        return cellBoard2D.get(i);
    }

    public void setInclusion(int i) {
        //Neighborhood returns offset to specific cell so it can be out of bounds
        if (i >= 0 && i < width * height)
            cellBoard2D.set(i, inclusionCell);
    }

    public Cell getInitialCell() {
        return initialCell;
    }

    public Cell getInclusionCell() {
        return inclusionCell;
    }

    public boolean isFilled () {
        return cellBoard2D.stream().noneMatch(cell -> cell == initialCell);
    }

    public List<Integer> edgeCells(Neighborhood neighborhood) {
        return IntStream.range(0, width * height).parallel().filter(i -> {
            int x = i % width;
            int y = i / width;

            long colouredNeigs = neighborhood.cellNeighbors(x, y).stream()
                    .map(c -> getCell(c[1], c[0]))
                    .filter(cell -> cell != initialCell && cell != inclusionCell)
                    .distinct()
                    .count();

            return colouredNeigs > 1;
        }).boxed().collect(Collectors.toList());
    }

    public void setInclusions(List<Integer> cells, Neighborhood neighborhood) {
        cells.forEach(integer -> {
            int x = integer % width;
            int y = integer / width;

            setInclusion(integer);
            neighborhood.cellNeighbors(x, y).forEach(c -> setInclusion(c[1] * width + c[0]));
        });
    }

    public void importCell(int x, int y, Color color) {
        if (color.equals(Color.BLACK)) {
            setCell(x, y, inclusionCell);
            return;
        }
        if (color.equals(Color.WHITE)) {
            setCell(x, y, initialCell);
            return;
        }

        for (Cell cell : precomputedCells) {
            if (cell.getColor().equals(color)) {
                setCell(x, y, cell);
                return;
            }
        }

        Cell importedCell = new Cell(true, color);
        precomputedCells.add(importedCell);
        setCell(x, y, importedCell);
    }

    public List<Cell> popXFromPrecomputedCellsAndClean(int count) {
        List<Cell> cells = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            cells.add(precomputedCells.get(i));
        }
        precomputedCells.clear();
        return cells;
    }

    public List<Cell> getPrecomputedCells() {
        return precomputedCells;
    }
}
