package pl.wieloskalowe.Controllers;

import pl.wieloskalowe.*;

import java.util.Observable;

/**
 * Created by ishfi on 04.05.2017.
 */
public class AutomatonAdapter extends Observable{
    private Automaton automaton;

    public AutomatonAdapter(Automaton automaton, Neighborhood neighborhood) {
        this.automaton = automaton;
    }

    public void nextAutomatonState() {
        automaton.oneIteration();
        setChanged();
        notifyObservers();
    }

    public void changeCellState(CellCoordinates cellCoordinates) {
        automaton.getBoard().getCell(cellCoordinates).nextState();
        setChanged();
        notifyObservers();
    }

    public Board2D getBoard() {
        return automaton.getBoard();
    }
}
