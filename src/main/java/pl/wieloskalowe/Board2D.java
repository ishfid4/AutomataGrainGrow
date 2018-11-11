package pl.wieloskalowe;

import javafx.scene.paint.Color;
import pl.wieloskalowe.cell.Cell;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Board2D {
    private ArrayList<Cell> cellBoard2D;
    public int width, height;
    private Cell outerCell;
    private Cell initialCell;

    List<Cell> precomputedCells;

    public Board2D(int width, int height, Cell outerCell, Cell initialCell) {
        this.width = width;
        this.height = height;
        this.outerCell = outerCell;
        this.cellBoard2D = new ArrayList<>(width * height);
        this.initialCell = initialCell;
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
        this.precomputedCells = board2D.precomputedCells;
    }

    public List<Cell> precomputeCells(int n) {
        Random random = new Random();

        for(int i = 0; i < n; ++i) {
            Color randomColor = Color.color(random.nextFloat(), random.nextFloat(), random.nextFloat());
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
        if (x >=0 && x < width && y >=0 && y < height) //TODO reconsider this if
            cellBoard2D.set(x * width + y, cell);
    }

    public Cell getCell(int x, int y) {
        if (x >=0 && x < width && y >=0 && y < height) { //TODO reconsider this if
            Cell cell = cellBoard2D.get(x * width + y);
//            if(cell == null && outerCell != null) cell = new Cell(outerCell);
            return cell;
        }

        return initialCell; //TODO Reduce ammount of new in algorithm
    }

    public Cell getInitialCell() {
        return initialCell;
    }
}
