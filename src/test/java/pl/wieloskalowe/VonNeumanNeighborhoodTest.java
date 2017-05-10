package pl.wieloskalowe;

import org.junit.Test;
import pl.wieloskalowe.neighborhoods.VonNeumanNeighborhood;

import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;

/**
 * Created by ishfi on 10.05.2017.
 */
public class VonNeumanNeighborhoodTest {
    @Test
    public void vonNeumanNh1() throws Exception {
        VonNeumanNeighborhood nh = new VonNeumanNeighborhood(1);
        Set<CellCoordinates> neighbors = nh.cellNeighbors(new CellCoordinates(0, 0));

        Set<CellCoordinates> expected = new HashSet<>();
        expected.add(new CellCoordinates(0, 1));
        expected.add(new CellCoordinates(0, -1));
        expected.add(new CellCoordinates(-1, 0));
        expected.add(new CellCoordinates(1, 0));
//        expected.add(new CellCoordinates(0, 0));

        assertEquals(expected, neighbors);
    }

    @Test
    public void vonNeumanNh2() throws Exception {
        VonNeumanNeighborhood nh = new VonNeumanNeighborhood(2);
        Set<CellCoordinates> neighbors = nh.cellNeighbors(new CellCoordinates(0, 0));

        Set<CellCoordinates> expected = new HashSet<>();
        expected.add(new CellCoordinates(-1, -1));
        expected.add(new CellCoordinates(-1, 0));
        expected.add(new CellCoordinates(-1, 1));
        expected.add(new CellCoordinates(0, -1));
//        expected.add(new CellCoordinates(0, 0));
        expected.add(new CellCoordinates(0, 1));
        expected.add(new CellCoordinates(1, -1));
        expected.add(new CellCoordinates(1, 0));
        expected.add(new CellCoordinates(1, 1));
        expected.add(new CellCoordinates(0, 2));
        expected.add(new CellCoordinates(0, -2));
        expected.add(new CellCoordinates(-2, 0));
        expected.add(new CellCoordinates(2, 0));

        assertEquals(expected, neighbors);
    }
}