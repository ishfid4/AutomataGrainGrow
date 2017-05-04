package pl.wieloskalowe;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by ishfi on 02.05.2017.
 */
public class Board2D {
    private Map<CellCoordinates, Cell> board2D = new HashMap<>();
    private int width, height;
    private Cell outerCell;

    public Board2D(int width, int height, Cell outerCell, Cell initialCell) {
        this.width = width;
        this.height = height;
        this.outerCell = outerCell;
        for (int x = 0; x < width; ++x) {
            for (int y = 0; y < height; ++y) {
                CellCoordinates cellCoordinates = new CellCoordinates(x, y);
                this.board2D.put(cellCoordinates, initialCell.copy());
            }
        }
    }

    public Board2D(Board2D board2D) {
        this.width = board2D.width;
        this.height = board2D.height;
        this.outerCell = board2D.outerCell;
    }

    public Set<CellCoordinates> getAllCoordinates () {
        return board2D.keySet();
    }

    public void setCell(CellCoordinates cellCoordinates, Cell cell){
        board2D.put(cellCoordinates, cell);
    }

    public Cell getCell(CellCoordinates cellCoordinates) {
        Cell cell = board2D.get(cellCoordinates);
        if(cell == null) cell = outerCell.copy();
        return cell;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}
