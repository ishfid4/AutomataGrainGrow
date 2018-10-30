package pl.wieloskalowe.cell;


import javafx.scene.paint.Color;

import java.util.Random;

@Deprecated
public class CellGrain implements Cell {
    private boolean state, onEdge, inclusion;
    private Color color;

    public CellGrain() {
        this.state = false;
        this.color = Color.color(1, 1, 1);
        this.onEdge = false;
        this.inclusion = false;
    }

    public CellGrain(CellGrain cellGrain) {
        this.state = cellGrain.state;
        this.color = cellGrain.color;
        this.onEdge = cellGrain.onEdge;
        this.inclusion = cellGrain.inclusion;
    }

    public CellGrain(boolean state, Color color) {
        this.state = state;
        this.color = color;
        this.onEdge = false;
        this.inclusion = false;
    }

    public CellGrain(boolean state, Color color, boolean onEdge) {
            this.state = state;
        this.color = color;
        this.onEdge = onEdge;
        this.inclusion = false;
    }

    public CellGrain(boolean state, boolean inclusion) {
        this.state = state;
        this.color = Color.BLACK;
        this.inclusion = inclusion;
    }

    @Override
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

    @Override
    public void nextState() {
        Random random = new Random();
        if (this.color.equals(Color.color(1, 1, 1)))
            this.color = Color.color(random.nextFloat(), random.nextFloat(), random.nextFloat());
        else
            this.color = Color.color(1, 1, 1);
        this.state  = !this.state;
    }

    @Override
    public CellGrain copyGrain() {
        return new CellGrain(this);
    }
}
