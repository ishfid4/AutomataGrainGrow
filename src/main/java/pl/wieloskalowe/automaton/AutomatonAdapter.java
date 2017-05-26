package pl.wieloskalowe.automaton;

import javafx.scene.paint.Color;
import pl.wieloskalowe.*;
import pl.wieloskalowe.cell.CellCoordinates;
import pl.wieloskalowe.cell.CellGrain;

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

    public synchronized void setCellState(CellCoordinates cellCoordinates, Color color){
        automaton.getBoard().setCell(cellCoordinates, new CellGrain(true,color));
        setChanged();
        notifyObservers();
    }

    public Board2D getBoard() {
        return automaton.getBoard();
    }
}
