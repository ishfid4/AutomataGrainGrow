package pl.wieloskalowe;

import org.junit.Test;
import pl.wieloskalowe.cell.CellCoordinates;
import pl.wieloskalowe.neighborhoods.MooreNeighborhood;

import static org.junit.Assert.*;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by ishfi on 02.05.2017.
 */
public class MooreNeighborhoodTest {
    @Test
    public void mooreNh() throws Exception {
        MooreNeighborhood nh = new MooreNeighborhood(0);
        Set<CellCoordinates> neighbors = nh.cellNeighbors(new CellCoordinates(1, 1));

        Set<CellCoordinates> expected = new HashSet<>();

        assertEquals(expected, neighbors);
    }
}
