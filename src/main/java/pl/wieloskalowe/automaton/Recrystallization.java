package pl.wieloskalowe.automaton;

import javafx.scene.paint.Color;
import pl.wieloskalowe.Board2D;
import pl.wieloskalowe.CoordinatesWrapper;
import pl.wieloskalowe.cell.Cell;
import pl.wieloskalowe.cell.CellCoordinates;
import pl.wieloskalowe.cell.CellGrain;
import pl.wieloskalowe.neighborhoods.Neighborhood;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by ishfi on 22.05.2017.
 */
public class Recrystallization extends Automaton {
    private double iteration = 0;
    private double criticalIteration = 65; //for 300x300 board
    double criticalRo = roFunction(65) / (300 * 300);  //for 300x300 board
    private final double a = 86710969050178.5;
    private final double b = 9.41268203527779;
    private final double k = 100; //WTF coefficient
    private Set<CellCoordinates> cellsOnEdge = new HashSet<>();

    public Recrystallization(Board2D board2D, Neighborhood neighborhood) {
        super(board2D, neighborhood);
    }

    public Recrystallization(Board2D board2D, Neighborhood neighborhood, CoordinatesWrapper coordinatesWrapper) {
        super(board2D, neighborhood, coordinatesWrapper);
    }

    @Override
    protected Cell getNextCellState(Cell cell, Set<Cell> neighbours) {
        if (cell.copyGrain().isNewFromRecrystallization()) {
            return cell.copyGrain();
        } else {
            for (Cell c : neighbours) {
                CellGrain cellGrain = c.copyGrain();
                if (cellGrain.isNewFromRecrystallization()) {
                    return new CellGrain(true, cellGrain.getColor());
                }
            }
        }

        if (cell.isAlive()) {
            Color cellColor = cell.copyGrain().getColor();
            boolean onEdge = false;
            for (Cell c : neighbours) {
                if (!cellColor.equals(c.copyGrain().getColor()) && !c.copyGrain().getColor().equals(Color.color(1,1,1)))
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

    //TODO: Its probably bad way of implementing this
    @Override
    public synchronized void oneIteration() {
        super.oneIteration();

        //Searching for alive -> incrementing cell iterator var
        //Also calc for each cell ro and sum leftovers
        double sumOfLeftoversFromRos = propagateRoValuesAndReturnSumOfLeftovers();

        // randomly sum of leftovers add? to ro in cells on edge
        //Searching for edges ->if newFromRecryst -> set it false and reset ro and iterator
        // if ro > critical -> set newFromRecryst true
        sumOfLeftoversFromRos = sumOfLeftoversFromRos / k;
        randomlySpreadLeftoversAndHandleCellsIntendentToRecrystalization(sumOfLeftoversFromRos);
    }

    private synchronized void randomlySpreadLeftoversAndHandleCellsIntendentToRecrystalization(double sumDevidedByK) {
        Random random = new Random();
        Set<CellCoordinates> coordinatesSet = super.board2D.getAllCoordinates();
        Board2D nextBoard = new Board2D(super.board2D);

        for (CellCoordinates cellCoordinates : coordinatesSet) {
            CellGrain currentCell = super.board2D.getCell(cellCoordinates).copyGrain();

            if (currentCell.isNewFromRecrystallization()) {
                currentCell = new CellGrain(true, currentCell.getColor());
            }

            if (currentCell.isOnEdge()) {
                if (random.nextInt(1000000) % 4000 == 0){
                    double currentRo = currentCell.getRo();
                    currentCell.setRo(currentRo + sumDevidedByK);
                }

                if (currentCell.getRo() > criticalRo){
                    currentCell = new CellGrain();
                    currentCell.nextState();
                    currentCell.setNewFromRecrystallization(true);
                } else {
                    currentCell.setNewFromRecrystallization(false);
                }
            }

            nextBoard.setCell(cellCoordinates, currentCell);
        }

        super.board2D = nextBoard;
    }

    private synchronized double propagateRoValuesAndReturnSumOfLeftovers() {
        double sumOfLeftoversFromRos = 0.0;
        Set<CellCoordinates> coordinatesSet = super.board2D.getAllCoordinates();
        Board2D nextBoard = new Board2D(super.board2D);

        for (CellCoordinates cellCoordinates : coordinatesSet) {
            CellGrain currentCell = super.board2D.getCell(cellCoordinates).copyGrain();

            if (currentCell.isNewFromRecrystallization())
                currentCell = new CellGrain(true, currentCell.getColor());
            else {
                if (currentCell.isAlive()) {
                    double currentIteration = currentCell.getIteration();
                    currentCell.setIteration(currentIteration + 1);

                    double cellsRo = roFunction(currentIteration) - roFunction(currentIteration - 1);
                    cellsRo = cellsRo / (300 * 300);

                    if (currentCell.isOnEdge()) {
                        currentCell.setRo(0.8 * cellsRo);
                        sumOfLeftoversFromRos += 0.2 * cellsRo;
                    } else {
                        currentCell.setRo(0.2 * cellsRo);
                        sumOfLeftoversFromRos += 0.8 * cellsRo;
                    }
                }
            }

            nextBoard.setCell(cellCoordinates, currentCell);
        }

        super.board2D = nextBoard;
        return sumOfLeftoversFromRos;
    }

    private double roFunction(double it){
        return (a / b) + (1 - (a / b)) * Math.pow(Math.E, -b * (it/1000));
    }
}
