package pl.wieloskalowe.automaton;

import javafx.scene.paint.Color;
import pl.wieloskalowe.*;
import pl.wieloskalowe.cell.CellCoordinates;
import pl.wieloskalowe.cell.CellGrain;

import java.util.Observable;

public class AutomatonAdapter extends Observable{
    private Automaton automaton;
    public boolean boardChanged;

    public AutomatonAdapter(Automaton automaton) {
        this.automaton = automaton;
    }

    public synchronized void nextAutomatonState() {
        boardChanged = false;
        automaton.oneIteration();
        if (automaton.boardChanged)
            boardChanged = true;
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

    public synchronized void setCellState(CellCoordinates cellCoordinates, boolean inclusion){
        automaton.getBoard().setCell(cellCoordinates, new CellGrain(true,inclusion));
        setChanged();
        notifyObservers();
    }

    public Board2D getBoard() {
        return automaton.getBoard();
    }
}
