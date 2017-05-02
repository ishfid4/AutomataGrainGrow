package pl.wieloskalowe;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by ishfi on 02.05.2017.
 */
public class Board2D {
    private Map<CellCoordinates, CellBinary> board2D = new HashMap<>();
    private int width, height;
    private CellBinary outerCell;

    public Board2D(int width, int height, CellBinary outerCell) {
        this.width = width;
        this.height = height;
        this.outerCell = outerCell;
        for (int x = 0; x < width; ++x) {
            for (int y = 0; y < height; ++y) {
                CellCoordinates cellCoordinates = new CellCoordinates(x, y);
                this.board2D.put(cellCoordinates, new CellBinary());
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

    public void setCell(CellCoordinates cellCoordinates, CellBinary cellBinary){
        board2D.put(cellCoordinates, cellBinary);
    }

    public CellBinary getCell(CellCoordinates cellCoordinates) {
        CellBinary cell = board2D.get(cellCoordinates);
        if(cell == null) cell = new CellBinary(outerCell);
        return cell;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}
