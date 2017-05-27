package pl.wieloskalowe.automaton;

import javafx.scene.paint.Color;
import pl.wieloskalowe.Board2D;
import pl.wieloskalowe.CoordinatesWrapper;
import pl.wieloskalowe.cell.Cell;
import pl.wieloskalowe.cell.CellCoordinates;
import pl.wieloskalowe.cell.CellGrain;
import pl.wieloskalowe.neighborhoods.Neighborhood;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by ishfi on 26.05.2017.
 */
public class MonteCarlo extends NaiveGrainGrow {
    boolean afterNaiveGrow;

    public MonteCarlo(Board2D board2D, Neighborhood neighborhood, boolean afterNaiveGrow) {
        super(board2D, neighborhood);
        this.afterNaiveGrow = afterNaiveGrow;
    }

    public MonteCarlo(Board2D board2D, Neighborhood neighborhood, boolean afterNaiveGrow, CoordinatesWrapper coordinatesWrapper) {
        super(board2D, neighborhood, coordinatesWrapper);
        this.afterNaiveGrow = afterNaiveGrow;
    }

    //TODO: make possible to start from naive grain grow

    protected Cell getNextCellStateMC(Cell cell, Set<Cell> neighbours) {
        int sameCellCount = 0, energyCurrentCell = 0, energyNewCell = 0, deltaEnergy = cell.copyGrain().getEnergy();
        List<Cell> neighboursCellList = new ArrayList<>();
        boolean onEdge = false;

        for (Cell c : neighbours) {
            if (!cell.copyGrain().getColor().equals(c.copyGrain().getColor())
                    && !c.copyGrain().getColor().equals(Color.color(1,1,1)))
                onEdge = true;
        }

        if (onEdge) {
            for (Cell c : neighbours) {
                if (c.isAlive())
                    neighboursCellList.add(c);
            }

            for (Cell c : neighboursCellList) {
                if (c.copyGrain().getColor().equals(cell.copyGrain().getColor()))
                    sameCellCount++;
            }
            energyCurrentCell = neighbours.size() - sameCellCount;

            Collections.shuffle(neighboursCellList);
            CellGrain newCellGrain = neighboursCellList.get(0).copyGrain();

            sameCellCount = 0;
            for (Cell c : neighboursCellList) {
                if (c.copyGrain().getColor().equals(newCellGrain.copyGrain().getColor()))
                    sameCellCount++;
            }
            energyNewCell = neighbours.size() - sameCellCount;

            deltaEnergy = energyNewCell - energyCurrentCell;

            if (deltaEnergy <= 0) {
                newCellGrain.setOnEdge(false);
                return newCellGrain;
            } else {
                CellGrain cellGrain = cell.copyGrain();
                cellGrain.setOnEdge(false);
                return cellGrain;
            }
        }
        CellGrain cellGrain = cell.copyGrain();
        cellGrain.setOnEdge(false);
        return cellGrain;
    }

    @Override
    public synchronized void oneIteration() {
        if (afterNaiveGrow) {
            super.oneIteration();
            boolean anyDead = isAnyCellDead();

            if (!anyDead) {
                oneIterationMC();
            }
        } else {
            oneIterationMC();
        }
    }

    private synchronized void oneIterationMC() {
        List<CellCoordinates> coordinatesList = new ArrayList<>();
        coordinatesList.addAll(super.board2D.getAllCoordinates());
        Collections.shuffle(coordinatesList);

        Board2D nextBoard = new Board2D(super.board2D);

        for (CellCoordinates cellCoordinates : coordinatesList) {
            Cell currentCell = super.board2D.getCell(cellCoordinates);
            Set<CellCoordinates> coordinatesNeighbours = super.neighborhood.cellNeighbors(cellCoordinates);

            if (coordinatesWrapper != null)
                coordinatesNeighbours = coordinatesWrapper.wrapCellCoordinates(coordinatesNeighbours);

            Set<Cell> neighbours = coordinatesNeighbours.stream()
                    .map(cord -> board2D.getCell(cord)).collect(Collectors.toSet());

            nextBoard.setCell(cellCoordinates, getNextCellStateMC(currentCell, neighbours));
        }

        super.board2D = nextBoard;
    }

    private synchronized boolean isAnyCellDead(){
        Set<CellCoordinates> coordinatesSet = super.board2D.getAllCoordinates();
        boolean anyDead = false;
        for (CellCoordinates cellCoordinates : coordinatesSet) {
            CellGrain currentCell = super.board2D.getCell(cellCoordinates).copyGrain();
            if (!currentCell.isAlive())
                anyDead = true;
        }
        return anyDead;
    }
}
