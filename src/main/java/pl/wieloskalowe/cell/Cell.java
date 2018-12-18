package pl.wieloskalowe.cell;

import javafx.scene.paint.Color;

import java.util.Objects;

public class Cell  {
    private boolean state, onEdge, inclusion, fixedState, recrystalized;
    private Color color;

    public Cell() {
        this.state = false;
        this.color = Color.WHITE;
        this.onEdge = false;
        this.inclusion = false;
        this.fixedState = false;
        this.recrystalized = false;
    }

    public Cell(Cell cellGrain) {
        this.state = cellGrain.state;
        this.color = cellGrain.color;
        this.onEdge = cellGrain.onEdge;
        this.inclusion = cellGrain.inclusion;
        this.fixedState = cellGrain.fixedState;
        this.recrystalized = cellGrain.recrystalized;
    }

    public Cell(boolean state, Color color) {
        this.state = state;
        this.color = color;
        this.onEdge = false;
        this.inclusion = false;
        this.fixedState = false;
        this.recrystalized = false;
    }

    public Cell(boolean state, Color color, boolean onEdge) {
        this.state = state;
        this.color = color;
        this.onEdge = onEdge;
        this.inclusion = false;
        this.fixedState = false;
        this.recrystalized = false;
    }

    public Cell(boolean state, boolean inclusion) {
        this.state = state;
        this.color = Color.BLACK;
        this.inclusion = inclusion;
        this.fixedState = false;
        this.recrystalized = false;
    }

    public boolean isAlive() {
        return state;
    }

    public Color getColor() {
        return color;
    }

    public boolean isInclusion() {
        return inclusion;
    }

    public boolean isOnEdge() {
        return onEdge;
    }

    public boolean isFixedState() {
        return fixedState;
    }

    public void setFixedState(boolean fixedState) {
        this.fixedState = fixedState;
    }

    public boolean isRecrystalized() {
        return recrystalized;
    }

    public void setRecrystalized(boolean recrystalized) {
        this.recrystalized = recrystalized;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Cell cell = (Cell) o;
        return state == cell.state &&
                onEdge == cell.onEdge &&
                inclusion == cell.inclusion &&
                fixedState == cell.fixedState &&
                recrystalized == cell.recrystalized &&
                Objects.equals(color, cell.color);
    }

    @Override
    public int hashCode() {
        return Objects.hash(state, onEdge, inclusion, fixedState, recrystalized, color);
    }
}
