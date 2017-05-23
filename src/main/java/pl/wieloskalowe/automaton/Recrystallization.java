package pl.wieloskalowe.automaton;

import javafx.scene.paint.Color;
import pl.wieloskalowe.Board2D;
import pl.wieloskalowe.CoordinatesWrapper;
import pl.wieloskalowe.cell.Cell;
import pl.wieloskalowe.cell.CellCoordinates;
import pl.wieloskalowe.cell.CellGrain;
import pl.wieloskalowe.neighborhoods.Neighborhood;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by ishfi on 22.05.2017.
 */
public class Recrystallization extends Automaton {
    private double iteration = 0;
    private double criticalIteration = 65; //for 300x300 board
    private final double a = 86710969050178.5;
    private final double b = 9.41268203527779;
    private final double k = 100;
    private Set<CellCoordinates> cellsOnEdge = new HashSet<>();
    private double sumOfLeftoversOfRo = 0;


    public Recrystallization(Board2D board2D, Neighborhood neighborhood) {
        super(board2D, neighborhood);
    }

    public Recrystallization(Board2D board2D, Neighborhood neighborhood, CoordinatesWrapper coordinatesWrapper) {
        super(board2D, neighborhood, coordinatesWrapper);
    }

    @Override
    protected Cell getNextCellState(Cell cell, Set<Cell> neighbours) {
        //TODO: in moore middle cell is border at first iteration
        if (cell.copyGrain().isNewFromRecrystallization()) {
            return cell.copyGrain();
        } else {
            for (Cell c : neighbours) {
                if (c.copyGrain().isNewFromRecrystallization()) {
                    CellGrain cellGrain = c.copyGrain();
                    cellGrain.setNewFromRecrystallization(false);
                    return cellGrain;
                }
            }
        }

        if (cell.isAlive()) {
            Color cellColor = cell.copyGrain().getColor();
            boolean onEdge = false;
            for (Cell c : neighbours) {
                if (!cellColor.equals(c.copyGrain().getColor()) && !cellColor.equals(Color.color(1,1,1)))
                    onEdge = true;
            }
            CellGrain cellGrain = cell.copyGrain();
            cellGrain.setOnEdge(onEdge);
            return cellGrain;
        } else {
            Color cellColor = Color.color(1, 1, 1);

            Map<Color, Integer> listOfColors = new HashMap<>();
            int maxCount = 0;
            int colorCount = 0;

            for (Cell c : neighbours) {
                CellGrain cellGrain = c.copyGrain();
                cellColor = cellGrain.getColor();
                if (!cellColor.equals(Color.color(1, 1, 1))) {
                    if (listOfColors.containsKey(cellColor)) {
                        int tmp = listOfColors.get(cellColor);
                        tmp++;
                        listOfColors.replace(cellColor, tmp);
                    } else {
                        listOfColors.put(cellColor, 1);
                        colorCount++;
                    }
                }
            }

            for (Color col : listOfColors.keySet()) {
                if (listOfColors.get(col) >= maxCount) {
                    maxCount = listOfColors.get(col);
                    cellColor = col;
                }
            }

            if (maxCount > 0) {
                if (colorCount > 1)
                    return new CellGrain(true,cellColor,true);

                return new CellGrain(true,cellColor);
            }
            else
                return new CellGrain();
        }
    }
//
//    @Override
//    public void oneIteration() {
//        super.oneIteration();
//
//
//    }

    private double roFunction(double it){
        return (a / b) + (1 - (a / b)) * Math.pow(Math.E, -b * (it/100));
    }
}
