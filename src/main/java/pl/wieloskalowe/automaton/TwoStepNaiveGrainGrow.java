package pl.wieloskalowe.automaton;

import javafx.util.Pair;
import pl.wieloskalowe.Board2D;
import pl.wieloskalowe.CoordinatesWrapper;
import pl.wieloskalowe.cell.Cell;
import pl.wieloskalowe.neighborhoods.Neighborhood;

import java.util.*;
import java.util.stream.IntStream;

public class TwoStepNaiveGrainGrow extends Automaton {
    boolean secondStep;
    List<Cell> fixedCells;

    public TwoStepNaiveGrainGrow(Board2D board2D, Neighborhood neighborhood) {
        super(board2D, neighborhood);
        this.secondStep = false;
        this.fixedCells = new ArrayList<>();
    }

    @Override
    protected Cell getNextCellState(Cell cell, List<List<Cell>> neighbours) {
        Cell initialCell = board2D.getInitialCell();
        Cell inclusionCell = board2D.getInclusionCell();

        if(cell == inclusionCell) return cell;
        if(!fixedCells.isEmpty()){
            for (Cell c : fixedCells) {
                if (c == cell)
                    return cell;
            }
        }
        if(cell != initialCell) return cell;

        if(neighbours.stream().allMatch(c -> c == initialCell || c == inclusionCell)) {
            return cell;
        }

        Map<Cell, Pair<Cell, Integer>> listOfColors = new HashMap<>();

        for (Cell c : neighbours.get(0)) {
            if (c != board2D.getInitialCell() && c != board2D.getInclusionCell()) {
                if(!fixedCells.isEmpty()){
                    for (Cell c1 : fixedCells) {
                        if (c1 != c){
                            Pair<Cell, Integer> currentCount = listOfColors.getOrDefault(c, new Pair<>(cell, 0));
                            listOfColors.put(c, new Pair<>(c, currentCount.getValue() + 1));
                        }
                    }
                } else {
                    Pair<Cell, Integer> currentCount = listOfColors.getOrDefault(c, new Pair<>(cell, 0));
                    listOfColors.put(c, new Pair<>(c, currentCount.getValue() + 1));
                }
            }
        }

        if (listOfColors.isEmpty())
            return cell;

        return Collections.max(listOfColors.values(), Comparator.comparingInt(Pair::getValue)).getKey();
    }

    public void getReadyToFuck(int fixedStateCount, int statesToGenerate, int countToGenerate) {
        this.fixedCells = new ArrayList<>();
        fixedCells.addAll(board2D.popXFromPrecomputedCellsAndClean(fixedStateCount));
        List<Cell> precomputedCells = board2D.precomputeCells(statesToGenerate);

        IntStream.range(0, board2D.width * board2D.height).forEach(i -> {
            int x = i % board2D.width;
            int y = i / board2D.width;
            boolean needChange = true;
            Cell current = board2D.getCell(x, y);
            for (Cell cell : fixedCells) {
                if (cell == current)
                    needChange = false;
            }

            if (needChange)
                board2D.setCell(x, y, board2D.getInitialCell());
        });
//
//        Random random = new Random();
//        boolean isInitialCell;
//        while(countToGenerate > 0) {
//            for (int i = 0; i < statesToGenerate; ++i) {
//                isInitialCell = false;
//                int x = 0, y = 0;
//                while(!isInitialCell) {
//                    x = random.nextInt(board2D.getWidth());
//                    y = random.nextInt(board2D.height);
//                    if (board2D.getCell(x, y) == board2D.getInitialCell())
//                        isInitialCell = true;
//                }
//
//                board2D.setCell(x, y, precomputedCells.get(i));
//                syncNextBoard();
//                --countToGenerate;
//            }
//        }

        syncNextBoard();
    }
}
