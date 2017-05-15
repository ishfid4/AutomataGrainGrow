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
                if(initialCell.copyBinary() != null)
                    this.board2D.put(cellCoordinates, initialCell.copyBinary());
                if(initialCell.copyGrain() != null)
                    this.board2D.put(cellCoordinates, initialCell.copyGrain());
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
        if(cell == null && outerCell.copyBinary() != null) cell = outerCell.copyBinary();
        if(cell == null && outerCell.copyGrain() != null) cell = outerCell.copyGrain();
        return cell;
    }
}
