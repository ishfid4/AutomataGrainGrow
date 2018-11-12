package pl.wieloskalowe.automaton;

import javafx.scene.paint.Color;
import pl.wieloskalowe.Board2D;

import java.util.Observable;

public class AutomatonAdapter extends Observable{
    private Automaton automaton;
    public boolean boardChanged;
    public int timesNotChanged = 0;

    public AutomatonAdapter(Automaton automaton) {
        this.automaton = automaton;
    }

    public synchronized void nextAutomatonState() {
        boardChanged = automaton.oneIteration();
        if (boardChanged) {
            setChanged();
            notifyObservers();
            timesNotChanged = 0;
        } else {
            ++timesNotChanged;
        }
    }

    public synchronized void importCell(int x, int y, Color color){
        automaton.getBoard().importCell(x, y , color);
        setChanged();
        notifyObservers();
    }

    public Board2D getBoard() {
        return automaton.getBoard();
    }

    public Automaton getAutomaton() {
        return automaton;
    }

    public void refresh() {
        setChanged();
        notifyObservers();
    }
}
