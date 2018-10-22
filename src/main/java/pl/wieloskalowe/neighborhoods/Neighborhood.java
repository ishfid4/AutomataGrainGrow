package pl.wieloskalowe.neighborhoods;

import pl.wieloskalowe.cell.CellCoordinates;

import java.util.Set;

public interface Neighborhood {
    Set<CellCoordinates> cellNeighbors(CellCoordinates cellCoordinates);
}
