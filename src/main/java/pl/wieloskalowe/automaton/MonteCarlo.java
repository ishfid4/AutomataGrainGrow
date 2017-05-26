package pl.wieloskalowe.automaton;

import pl.wieloskalowe.Board2D;
import pl.wieloskalowe.CoordinatesWrapper;
import pl.wieloskalowe.neighborhoods.Neighborhood;

/**
 * Created by ishfi on 26.05.2017.
 */
public class MonteCarlo extends NaiveGrainGrow {
    public MonteCarlo(Board2D board2D, Neighborhood neighborhood) {
        super(board2D, neighborhood);
    }

    public MonteCarlo(Board2D board2D, Neighborhood neighborhood, CoordinatesWrapper coordinatesWrapper) {
        super(board2D, neighborhood, coordinatesWrapper);
    }

    @Override
    public synchronized void oneIteration() {
        super.oneIteration();
    }
}
