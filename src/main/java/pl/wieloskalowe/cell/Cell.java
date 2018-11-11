package pl.wieloskalowe.cell;

import javafx.scene.paint.Color;

import java.util.Objects;
import java.util.Random;

public class Cell  {
    private boolean state, onEdge, inclusion;
    private Color color;

    public Cell() {
        this.state = false;
        this.color = Color.WHITE;
        this.onEdge = false;
        this.inclusion = false;
    }

    public Cell(Cell cellGrain) {
        this.state = cellGrain.state;
        this.color = cellGrain.color;
        this.onEdge = cellGrain.onEdge;
        this.inclusion = cellGrain.inclusion;
    }

    public Cell(boolean state, Color color) {
        this.state = state;
        this.color = color;
        this.onEdge = false;
        this.inclusion = false;
    }

    public Cell(boolean state, Color color, boolean onEdge) {
        this.state = state;
        this.color = color;
        this.onEdge = onEdge;
        this.inclusion = false;
    }

    public Cell(boolean state, boolean inclusion) {
        this.state = state;
        this.color = Color.BLACK;
        this.inclusion = inclusion;
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

//    public void nextState() {
//        Random random = new Random();
//        if (this.color == Color.WHITE)
//            this.color = Color.color(random.nextFloat(), random.nextFloat(), random.nextFloat());
//        else
//            this.color = Color.WHITE;
//        this.state  = !this.state;
//    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Cell cell = (Cell) o;
        return state == cell.state &&
                onEdge == cell.onEdge &&
                inclusion == cell.inclusion &&
                Objects.equals(color, cell.color);
    }

    @Override
    public int hashCode() {
        return Objects.hash(state, onEdge, inclusion, color);
    }
}
