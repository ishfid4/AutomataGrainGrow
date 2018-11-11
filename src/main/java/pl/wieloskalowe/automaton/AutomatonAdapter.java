package pl.wieloskalowe.automaton;

import javafx.scene.paint.Color;
import pl.wieloskalowe.*;
import pl.wieloskalowe.cell.Cell;

import java.util.Observable;

public class AutomatonAdapter extends Observable{
    private Automaton automaton;
    public boolean boardChanged;

    public AutomatonAdapter(Automaton automaton) {
        this.automaton = automaton;
    }

    public synchronized void nextAutomatonState() {
        boardChanged = automaton.oneIteration();
        if (boardChanged) {
            setChanged();
            notifyObservers();
        }
    }

    public synchronized void changeCellState(int x, int y) {
        automaton.getBoard().getCell(x, y).nextState();
        setChanged();
        notifyObservers();
    }

    public synchronized void setCellState(int x, int y, Color color){
        automaton.getBoard().setCell(x, y , new Cell(true,color));
        setChanged();
        notifyObservers();
    }

    public synchronized void setCellState(int x, int y, boolean inclusion){
        automaton.getBoard().setCell(x, y, new Cell(true,inclusion));
        setChanged();
        notifyObservers();
    }

    public Board2D getBoard() {
        return automaton.getBoard();
    }
}
