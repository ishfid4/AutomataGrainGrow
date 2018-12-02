package pl.wieloskalowe.automaton;

import javafx.scene.paint.Color;
import javafx.util.Pair;
import pl.wieloskalowe.Board2D;
import pl.wieloskalowe.cell.Cell;
import pl.wieloskalowe.neighborhoods.Neighborhood;

import java.util.*;
import java.util.stream.IntStream;

public class TwoStepNaiveGrainGrow extends Automaton {
    List<Cell> fixedCells;

    public TwoStepNaiveGrainGrow(Board2D board2D, Neighborhood neighborhood) {
        super(board2D, neighborhood);
        this.fixedCells = new ArrayList<>();
    }

    @Override
    protected Cell getNextCellState(Cell cell, List<List<Cell>> neighbours) {
        Cell initialCell = board2D.getInitialCell();
        Cell inclusionCell = board2D.getInclusionCell();

        if(cell == inclusionCell) return cell;
        if(cell.isFixedState()) return cell;
        if(cell != initialCell) return cell;

        if(neighbours.get(0).stream().allMatch(c -> c == initialCell || c == inclusionCell || c.isFixedState())) {
            return cell;
        }

        Map<Cell, Pair<Cell, Integer>> listOfColors = new HashMap<>();

        for (Cell c : neighbours.get(0)) {
            if (c != board2D.getInitialCell() && c != board2D.getInclusionCell() && !c.isFixedState()) {
                    Pair<Cell, Integer> currentCount = listOfColors.getOrDefault(c, new Pair<>(cell, 0));
                    listOfColors.put(c, new Pair<>(c, currentCount.getValue() + 1));
            }
        }

        if (listOfColors.isEmpty())
            return cell;

        return Collections.max(listOfColors.values(), Comparator.comparingInt(Pair::getValue)).getKey();
    }

    public void get2ndStepReady(int fixedStateCount, int statesToGenerate, int countToGenerate, boolean isDualPhase) {
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
            int x = i % board2D.width;
            int y = i / board2D.width;
            boolean needChange = true;
            Cell current = board2D.getCell(x, y);
            for (Cell cell : fixedCells) {
                if ((cell == current) && isDualPhase && !oneFixedCell.isEmpty()) {
                    board2D.setCell(x, y, oneFixedCell.get(0));
                }
                if (cell == current) {
                    needChange = false;
                }
            }

            if (needChange)
                board2D.setCell(x, y, board2D.getInitialCell());
        });
        fixedCells.clear();
        fixedCells.addAll(oneFixedCell);

        Random random = new Random();
        boolean isInitialCell;
        while(countToGenerate > 0) {
            for (int i = 0; i < statesToGenerate; ++i) {
                isInitialCell = false;
                int x = 0, y = 0;
                while(!isInitialCell) {
                    x = random.nextInt(board2D.getWidth());
                    y = random.nextInt(board2D.height);
                    if (board2D.getCell(x, y) == board2D.getInitialCell())
                        isInitialCell = true;
                }

                board2D.setCell(x, y, precomputedCells.get(i));
                --countToGenerate;
            }
        }

        syncNextBoard();
    }
}
