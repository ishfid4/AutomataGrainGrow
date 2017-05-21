package pl.wieloskalowe.Controllers;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import pl.wieloskalowe.*;
import pl.wieloskalowe.neighborhoods.Neighborhood;

import java.util.Observable;

/**
 * Created by ishfi on 04.05.2017.
 */
public class AutomatonAdapter extends Observable{
    private Automaton automaton;
    private GraphicsContext graphicsContext;
    private CellDrawer cellDrawer;

    public AutomatonAdapter(Automaton automaton, Canvas canvas,
                            double cellHeight, double cellWidth, String automatonType) {
        this.automaton = automaton;
        this.cellDrawer = new CellDrawer(canvas, cellWidth, cellHeight, automatonType);
    }

    public synchronized void nextAutomatonState() {
        automaton.oneIteration();
        cellDrawer.drawBoard(automaton.getBoard());
        setChanged();
        notifyObservers();
    }

    public synchronized void changeCellState(CellCoordinates cellCoordinates) {
        automaton.getBoard().getCell(cellCoordinates).nextState();
        cellDrawer.drawBoard(automaton.getBoard());
        setChanged();
        notifyObservers();
    }

    public Board2D getBoard() {
        return automaton.getBoard();
    }

    public synchronized GraphicsContext getGraphicsContext() {
        cellDrawer.drawBoard(automaton.getBoard());
        return graphicsContext;
    }
}
