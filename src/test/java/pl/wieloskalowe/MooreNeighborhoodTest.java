package pl.wieloskalowe;

import javafx.util.Pair;
import org.junit.Test;
import pl.wieloskalowe.neighborhoods.MooreNeighborhood;

import static org.junit.Assert.*;

import java.util.ArrayList;

/**
 * Created by ishfi on 02.05.2017.
 */
public class MooreNeighborhoodTest {
    @Test
    public void mooreNh() throws Exception {
        MooreNeighborhood nh = new MooreNeighborhood(0);
        ArrayList<Pair<Integer, Integer>> neighbors = nh.cellNeighbors(1, 1);

        ArrayList<Pair<Integer, Integer>> expected = new ArrayList<>();

        assertEquals(expected, neighbors);
    }
}
