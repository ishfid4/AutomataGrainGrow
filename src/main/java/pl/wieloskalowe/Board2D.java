package pl.wieloskalowe;

import pl.wieloskalowe.cell.Cell;
import pl.wieloskalowe.cell.CellCoordinates;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Board2D {

    //TODO zmienić na arraylist koordynaty jako klasa są niepotrzebne
    @Deprecated
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
                if(initialCell != null)
                    this.board2D.put(cellCoordinates, new Cell(initialCell));
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
        if(cell == null && outerCell != null) cell = new Cell(outerCell);
        return cell;
    }
}
