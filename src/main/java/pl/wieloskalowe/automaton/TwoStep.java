package pl.wieloskalowe.automaton;

import javafx.scene.paint.Color;
import pl.wieloskalowe.Board2D;
import pl.wieloskalowe.cell.Cell;
import pl.wieloskalowe.neighborhoods.Neighborhood;

import java.util.*;
import java.util.stream.IntStream;

public class TwoStep extends Automaton {
    List<Cell> fixedCells;
    boolean isFirstStepNaiveGrainGrow, isSecondStepNaiveGrainGrow, secondStep = false;
    private double grainBoundaryEnergy = 0.2;
    NaiveGrainGrow naiveGrainGrow = null;
    MonteCarlo monteCarlo = null;

    public TwoStep(Board2D board2D, Neighborhood neighborhood, boolean isFirstStepNaiveGrainGrow, boolean isSecondStepNaiveGrainGrow) {
        super(board2D, neighborhood);
        this.fixedCells = new ArrayList<>();
        this.isFirstStepNaiveGrainGrow = isFirstStepNaiveGrainGrow;
        this.isSecondStepNaiveGrainGrow = isSecondStepNaiveGrainGrow;

        if (isFirstStepNaiveGrainGrow || isSecondStepNaiveGrainGrow)
            naiveGrainGrow = new NaiveGrainGrow(board2D, neighborhood);

        if (!isFirstStepNaiveGrainGrow || !isSecondStepNaiveGrainGrow)
            monteCarlo = new MonteCarlo(board2D, neighborhood);
    }

    @Override
    public boolean oneIteration() {
        boolean boardChanged = false;
        if (!secondStep && isFirstStepNaiveGrainGrow){
            naiveGrainGrow.syncBoards(board2D);
            boardChanged = naiveGrainGrow.oneIteration();
            syncBoards(naiveGrainGrow.getBoard());
        }

        if (!secondStep && !isFirstStepNaiveGrainGrow){
            monteCarlo.syncBoards(board2D);
            boardChanged = monteCarlo.oneIteration();
            syncBoards(monteCarlo.getBoard());
        }

        if (secondStep && isSecondStepNaiveGrainGrow){
            naiveGrainGrow.syncBoards(board2D);
            boardChanged = naiveGrainGrow.oneIteration();
            syncBoards(naiveGrainGrow.getBoard());
        }

        if (secondStep && !isSecondStepNaiveGrainGrow) {
            monteCarlo.syncBoards(board2D);
            boardChanged = monteCarlo.oneIteration();
            syncBoards(monteCarlo.getBoard());
        }

        return boardChanged;
    }

    @Override
    protected Cell getNextCellState(Cell cell, List<List<Cell>> neighbours) {
        return null; //TODO make throw exception
    }

    //Statest to generate must be taken for MC from unique states
    public void get2ndStepReady(int fixedStateCount, int statesToGenerate, int countToGenerate, boolean isDualPhase) {
        secondStep = true;
        ArrayList<Cell> oneFixedCell = new ArrayList<>();
        this.fixedCells.clear();
        fixedCells.addAll(board2D.popXFromPrecomputedCellsAndClean(fixedStateCount));
        List<Cell> precomputedCells = board2D.precomputeCells(statesToGenerate);
        if (isDualPhase) {
            oneFixedCell.add(new Cell(true, Color.GOLD));
            oneFixedCell.get(0).setFixedState(true);
        } else {
            for (Cell c: fixedCells) {
                c.setFixedState(true);
            }
        }

        IntStream.range(0, board2D.width * board2D.height).forEach(i -> {
            boolean needChange = true;
            Cell current = board2D.getCell(i);
            for (Cell cell : fixedCells) {
                if ((cell == current) && isDualPhase && !oneFixedCell.isEmpty()) {
                    board2D.setCell(i, oneFixedCell.get(0));
                }
                if (cell == current) {
                    needChange = false;
                }
            }

            if (needChange)
                board2D.setCell(i, board2D.getInitialCell());
        });
        fixedCells.clear();
        fixedCells.addAll(oneFixedCell);

        if (isSecondStepNaiveGrainGrow)
            generationForNaiveGrainGrow(statesToGenerate, countToGenerate, precomputedCells);
        else
            generationForMonteCarlo(statesToGenerate, countToGenerate, precomputedCells);

        syncNextBoard();
    }

    private void generationForMonteCarlo(int statesToGenerate, int countToGenerate, List<Cell> precomputedCells) {
        Random random = new Random();
        IntStream.range(0, board2D.width * board2D.height).forEach(value -> {
            if (board2D.getCell(value) == board2D.getInitialCell())
                board2D.setCell(value, precomputedCells.get(random.nextInt(statesToGenerate)));
        });
    }

    private void generationForNaiveGrainGrow(int statesToGenerate, int countToGenerate, List<Cell> precomputedCells) {
        secondStep = true;
        List<Integer> indexesCellList = new ArrayList<>();
        IntStream.range(0, board2D.width * board2D.height).forEach(indexesCellList::add);
        Collections.shuffle(indexesCellList);
        int idx = 0, precomputedIdx = 0;
        while(countToGenerate > 0) {
            if (idx > indexesCellList.size())
                idx = 0;
            if (precomputedIdx > statesToGenerate - 1)
                precomputedIdx = 0;

            if (board2D.getCell(indexesCellList.get(idx)) == board2D.getInitialCell()) {
                board2D.setCell(indexesCellList.get(idx), precomputedCells.get(precomputedIdx));
                --countToGenerate;
                ++precomputedIdx;
            }
            ++idx;
        }
    }

    public void setGrainBoundaryEnergy(double grainBoundaryEnergy) {
        this.grainBoundaryEnergy = grainBoundaryEnergy;
    }
}
