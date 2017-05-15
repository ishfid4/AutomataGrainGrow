package pl.wieloskalowe;

/**
 * Created by ishfi on 05.05.2017.
 */
public interface Cell {
    void nextState();
    boolean isAlive();

    CellBinary copyBinary();
    CellGrain copyGrain();
}
