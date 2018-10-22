package pl.wieloskalowe.cell;

public interface Cell {
    void nextState();
    boolean isAlive();

    CellGrain copyGrain();
}
