package pl.wieloskalowe.automaton;

import pl.wieloskalowe.*;
import pl.wieloskalowe.cell.CellCoordinates;

import java.util.Observable;

/**
 * Created by ishfi on 04.05.2017.
 */
public class AutomatonAdapter extends Observable{
    private Automaton automaton;

    public AutomatonAdapter(Automaton automaton) {
        this.automaton = automaton;
    }

    public synchronized void nextAutomatonState() {
        automaton.oneIteration();
        setChanged();
        notifyObservers();
    }

    public synchronized void changeCellState(CellCoordinates cellCoordinates) {
        automaton.getBoard().getCell(cellCoordinates).nextState();
        setChanged();
        notifyObservers();
    }

    public Board2D getBoard() {
        return automaton.getBoard();
    }
}
