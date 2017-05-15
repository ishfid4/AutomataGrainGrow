package pl.wieloskalowe;


import javafx.scene.paint.Color;

import java.util.Random;

/**
 * Created by ishfi on 13.05.2017.
 */
public class CellGrain implements Cell {
    private boolean state;
    private Color color;

    public CellGrain() {
        this.state = false;
        this.color = Color.color(1, 1, 1);
    }

    public CellGrain(CellGrain cellGrain) {
        this.state = cellGrain.state;
        this.color = cellGrain.color;
    }

    public CellGrain(boolean state, Color color) {
        this.state = state;
        this.color = color;
    }

    @Override
    public boolean isAlive() {
        return state;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
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
    public CellBinary copyBinary() {
        return null;
    }

    @Override
    public CellGrain copyGrain() {
        return new CellGrain(this);
    }
}
