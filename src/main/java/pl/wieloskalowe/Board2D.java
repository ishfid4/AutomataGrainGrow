package pl.wieloskalowe;

import javafx.scene.paint.Color;
import pl.wieloskalowe.cell.Cell;
import pl.wieloskalowe.neighborhoods.Neighborhood;

import java.util.ArrayList;
import java.util.Arrays;
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

    private List<Double> cellsEnergy;
    private List<Cell> precomputedCells;
    private List<Cell> precomputedRecrystalizedCells;
    private List<Color> energyColor = Arrays.asList(Color.color(0.0,0.0,0.0),
            Color.color(0.0,0.00, 0.4),
            Color.color(0.0,0.00,0.6),
            Color.color(0.0,0.00,0.8),
            Color.color(0.0,0.00,1.0),
            Color.color(0.0,0.4,1.00),
            Color.color(0.05, 0.6,1.00));

    public Board2D(int width, int height, Cell outerCell, Cell initialCell) {
        this.width = width;
        this.height = height;
        this.outerCell = outerCell;
        this.cellBoard2D = new ArrayList<>(width * height);
        this.initialCell = initialCell;
        this.inclusionCell = new Cell(true, Color.BLACK);
        this.precomputedCells = new ArrayList<>();
        this.precomputedRecrystalizedCells = new ArrayList<>();
        this.cellsEnergy = new ArrayList<>();

        IntStream.range(0, width * height).forEach(value -> this.cellBoard2D.add(initialCell));
    }

    public Board2D(Board2D board2D) {
        this.width = board2D.width;
        this.height = board2D.height;
        this.outerCell = board2D.outerCell;
        this.cellBoard2D = new ArrayList<>(board2D.cellBoard2D);
        this.initialCell = board2D.initialCell;
        this.inclusionCell = board2D.inclusionCell;
        this.precomputedCells = board2D.precomputedCells;
        this.precomputedRecrystalizedCells = board2D.precomputedRecrystalizedCells;
        this.cellsEnergy = board2D.cellsEnergy;
    }

    public List<Cell> precomputeCells(int n) {
        Random random = new Random();
        precomputedCells.clear();

        for(int i = 0; i < n; ++i) {
            Color randomColor = Color.color(0.0, random.nextFloat(), random.nextFloat());
            while(randomColor == Color.BLACK || randomColor == Color.WHITE || randomColor == Color.GOLD)
                randomColor = Color.color(0.0, random.nextFloat(), random.nextFloat());

            Cell randomCell = new Cell(true, randomColor);
            precomputedCells.add(randomCell);
        }

        return precomputedCells;
    }

    public List<Cell> precomuteRecrystalizedCells(int n) {
        Random random = new Random();
        precomputedRecrystalizedCells.clear();

        for(int i = 0; i < n; ++i) {
            Color randomColor = Color.color(random.nextFloat(), 0.0, 0.0);
            while(randomColor == Color.BLACK || randomColor == Color.WHITE || randomColor == Color.GOLD)
                randomColor = Color.color(random.nextFloat(), 0.0, 0.0);

            Cell randomCell = new Cell(true, randomColor);
            randomCell.setRecrystalized(true);
            precomputedRecrystalizedCells.add(randomCell);
        }

        return precomputedRecrystalizedCells;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public void setCell(int idx, Cell cell){
        cellBoard2D.set(idx, cell);
    }

     public Double getCellEnergy(int idx) {
        if (idx >= 0 && idx < width * height) {
            return cellsEnergy.get(idx);
        }

        return -1.0;
    }

    public void setCellsEnergy(List<Double> cellsEnergy) {
        this.cellsEnergy = cellsEnergy;
    }

    public void setCellEnergy(int idx, Double en) {
        cellsEnergy.set(idx, en);
    }

    public List<Double> getCellsEnergy() {
        return cellsEnergy;
    }

    public List<Color> getEnergyColor() {
        return energyColor;
    }

    public Cell getCell(int i) {
        if (i >= 0 && i < width * height)
            return cellBoard2D.get(i);

        return initialCell;
    }

    public int coordsToIdx(int x, int y) {
        int idx = x * width + y;
        if (idx >= 0 && idx < width * height)
            return idx;

        return -1;
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
            int y = i % width;
            int x = i / width;

            long colouredNeigs = neighborhood.cellNeighbors(x, y).stream()
                    .map(c -> getCell(c[0] * width + c[1]))
                    .filter(cell -> cell != initialCell && cell != inclusionCell && !cell.isRecrystalized())
                    .distinct()
                    .count();

            return colouredNeigs > 1;
        }).boxed().collect(Collectors.toList());
    }

    @Deprecated
    public void setInclusions(List<Integer> cells, Neighborhood neighborhood) {
        cells.forEach(integer -> {
            int y = integer % width;
            int x = integer / width;

            setInclusion(integer);
            neighborhood.cellNeighbors(x, y).forEach(c -> setInclusion(c[0] * width + c[1]));
        });
    }

    public void importCell(int x, int y, Color color) {
        if (color.equals(Color.BLACK)) {
            setCell(x *width + y, inclusionCell);
            return;
        }
        if (color.equals(Color.WHITE)) {
            setCell(x * width + y, initialCell);
            return;
        }

        for (Cell cell : precomputedCells) {
            if (cell.getColor().equals(color)) {
                setCell(x * width + y, cell);
                return;
            }
        }

        Cell importedCell = new Cell(true, color);
        precomputedCells.add(importedCell);
        setCell(x * width + y, importedCell);
    }

    public List<Cell> popXFromPrecomputedCellsAndClean(int count) {
        List<Cell> cells = new ArrayList<>();
        int maxIdx = precomputedCells.size();
        for (int i = 0; i < count && i < maxIdx; i++) {
            cells.add(precomputedCells.get(i));
        }
        precomputedCells.clear();
        return cells;
    }

    public List<Cell> getPrecomputedRecrystalizedCells() {
        return precomputedRecrystalizedCells;
    }

    public List<Cell> getPrecomputedCells() {
        return precomputedCells;
    }
}
