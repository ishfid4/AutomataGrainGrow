package pl.wieloskalowe;

import java.util.Set;

/**
 * Created by ishfi on 04.05.2017.
 */
public interface Neighborhood {
    Set<CellCoordinates> cellNeighbors(CellCoordinates cellCoordinates);
}
