package pl.wieloskalowe;

import org.junit.Test;
import static org.junit.Assert.*;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by ishfi on 02.05.2017.
 */
public class MooreNeighborhoodTest {
    @Test
    public void dupa() throws Exception {
        MooreNeighborhood nh = new MooreNeighborhood(0,3,3, false);
        Set<CellCoordinates> neighbors = nh.cellNeighbors(new CellCoordinates(1, 1));

        Set<CellCoordinates> expected = new HashSet<>();

        assertEquals(expected, neighbors);
    }
}
