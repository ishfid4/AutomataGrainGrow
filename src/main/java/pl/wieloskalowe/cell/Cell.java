package pl.wieloskalowe.cell;

import javafx.scene.paint.Color;

import java.util.Random;

public class Cell  {
    private boolean state, onEdge, inclusion;
    private Color color;

    public Cell() {
        this.state = false;
        this.color = Color.color(1, 1, 1);
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

    public void setColor(Color color) {
        this.color = color;
    }

    public boolean isOnEdge() {
        return onEdge;
    }

    public void setOnEdge(boolean onEdge) {
        this.onEdge = onEdge;
    }

    public void nextState() {
        Random random = new Random();
        if (this.color.equals(Color.color(1, 1, 1)))
            this.color = Color.color(random.nextFloat(), random.nextFloat(), random.nextFloat());
        else
            this.color = Color.color(1, 1, 1);
        this.state  = !this.state;
    }

    public void changeState(Color color) {
        this.state = !this.state;
        this.color = color;
    }
}
