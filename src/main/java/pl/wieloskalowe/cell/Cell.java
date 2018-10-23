package pl.wieloskalowe.cell;

import javafx.scene.paint.Color;

public interface Cell {
    void nextState();
    boolean isAlive();
    Color getColor();

    CellGrain copyGrain();
}
