package pl.wieloskalowe;

import org.junit.Test;
import pl.wieloskalowe.neighborhoods.HexagonalNeighborhood;
import pl.wieloskalowe.neighborhoods.PentagonalNeighborhood;

import java.util.HashSet;
import java.util.Set;

import static junit.framework.TestCase.assertEquals;

/**
 * Created by ishfi on 13.05.2017.
 */
public class HexagonalNeighborhoodTest {
    @Test
    public void pentNhRight() throws Exception {
        HexagonalNeighborhood nh = new HexagonalNeighborhood(HexagonalNeighborhood.version.RIGHT);
        Set<CellCoordinates> neighbors = nh.cellNeighbors(new CellCoordinates(0, 0));

        Set<CellCoordinates> expected = new HashSet<>();
        expected.add(new CellCoordinates(0, 1));
        expected.add(new CellCoordinates(0, -1));
        expected.add(new CellCoordinates(-1, 0));
        expected.add(new CellCoordinates(-1, 1));
        expected.add(new CellCoordinates(1, 0));
        expected.add(new CellCoordinates(1, -1));

        assertEquals(expected, neighbors);
    }


    @Test
    public void pentNhLeft() throws Exception {
        HexagonalNeighborhood nh = new HexagonalNeighborhood(HexagonalNeighborhood.version.LEFT);
        Set<CellCoordinates> neighbors = nh.cellNeighbors(new CellCoordinates(0, 0));

        Set<CellCoordinates> expected = new HashSet<>();
        expected.add(new CellCoordinates(0, 1));
        expected.add(new CellCoordinates(0, -1));
        expected.add(new CellCoordinates(-1, 0));
        expected.add(new CellCoordinates(-1, -1));
        expected.add(new CellCoordinates(1, 0));
        expected.add(new CellCoordinates(1, 1));

        assertEquals(expected, neighbors);
    }
}
