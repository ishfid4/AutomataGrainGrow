package pl.wieloskalowe.cell;


import javafx.scene.paint.Color;

import java.util.Random;

/**
 * Created by ishfi on 13.05.2017.
 */
public class CellGrain implements Cell {
    private boolean state, newFromRecrystallization, onEdge;
    private Color color;
    private double iteration, ro, sumOfCellsRoDividedByK;
    private int energy;

    public CellGrain() {
        this.state = false;
        this.color = Color.color(1, 1, 1);
        this.newFromRecrystallization = false;
        this.iteration = 0;
        this.sumOfCellsRoDividedByK = 0;
        this.ro = 1;
        this.onEdge = false;
        this.energy = 0;
    }

    public CellGrain(CellGrain cellGrain) {
        this.newFromRecrystallization = cellGrain.newFromRecrystallization;
        this.state = cellGrain.state;
        this.color = cellGrain.color;
        this.iteration = cellGrain.iteration;
        this.ro = cellGrain.ro;
        this.sumOfCellsRoDividedByK = cellGrain.sumOfCellsRoDividedByK;
        this.onEdge = cellGrain.onEdge;
        this.energy = 0;
    }

    public CellGrain(boolean state, Color color) {
        this.state = state;
        this.color = color;
        this.newFromRecrystallization = false;
        this.iteration = 0;
        this.sumOfCellsRoDividedByK = 0;
        this.ro = 1;
        this.onEdge = false;
        this.energy = 0;
    }

    public CellGrain(boolean state, Color color, boolean onEdge) {
        this.state = state;
        this.color = color;
        this.onEdge = onEdge;
        this.newFromRecrystallization = false;
        this.iteration = 0;
        this.sumOfCellsRoDividedByK = 0;
        this.ro = 1;
        this.energy = 0;
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

    public boolean isNewFromRecrystallization() {
        return newFromRecrystallization;
    }

    public void setNewFromRecrystallization(boolean newFromRecrystallization) {
        this.newFromRecrystallization = newFromRecrystallization;
    }

    public double getRo() {
        return ro;
    }

    public void setRo(double ro) {
        this.ro = ro;
    }

    public double getSumOfCellsRoDividedByK() {
        return sumOfCellsRoDividedByK;
    }

    public void setSumOfCellsRoDividedByK(double sumOfCellsRoDividedByK) {
        this.sumOfCellsRoDividedByK = sumOfCellsRoDividedByK;
    }

    public double getIteration() {
        return iteration;
    }

    public void setIteration(double iteration) {
        this.iteration = iteration;
    }

    public boolean isOnEdge() {
        return onEdge;
    }

    public void setOnEdge(boolean onEdge) {
        this.onEdge = onEdge;
    }

    public int getEnergy() {
        return energy;
    }

    public void setEnergy(int energy) {
        this.energy = energy;
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
