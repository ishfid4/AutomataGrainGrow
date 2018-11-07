package pl.wieloskalowe;

import pl.wieloskalowe.cell.Cell;
import java.util.ArrayList;

public class Board2D {
    private ArrayList<Cell> cellBoard2D;
    private int width, height;
    private Cell outerCell;
    private Cell initialCell;

    public Board2D(int width, int height, Cell outerCell, Cell initialCell) {
        this.width = width;
        this.height = height;
        this.outerCell = outerCell;
        this.cellBoard2D = new ArrayList<>(width * height);
        this.initialCell = initialCell;

        if (initialCell != null) {
            for (int x = 0; x < width; ++x) {
                for (int y = 0; y < height; ++y) {
                    this.cellBoard2D.add(new Cell(initialCell));
                }
            }
        }
    }

    public Board2D(Board2D board2D) {
        this.width = board2D.width;
        this.height = board2D.height;
        this.outerCell = board2D.outerCell;
        this.cellBoard2D = new ArrayList<>(width * height);
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public void setCell(int x, int y, Cell cell){
        if (x >=0 && x < width && y >=0 && y < height) //TODO reconsider this if
            cellBoard2D.add(x * width + y, cell);
    }

    public Cell getCell(int x, int y) {
        if (x >=0 && x < width && y >=0 && y < height) { //TODO reconsider this if
            Cell cell = cellBoard2D.get(x * width + y);
//            if(cell == null && outerCell != null) cell = new Cell(outerCell);
            return cell;
        }

        return outerCell; //TODO Reduce ammount of new in algorithm
    }

    public void clear() {
        cellBoard2D.clear();
    }

    public Cell getInitialCell() {
        return initialCell;
    }
}
