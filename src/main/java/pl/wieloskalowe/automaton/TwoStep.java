package pl.wieloskalowe.automaton;

import javafx.scene.paint.Color;
import javafx.util.Pair;
import pl.wieloskalowe.Board2D;
import pl.wieloskalowe.cell.Cell;
import pl.wieloskalowe.neighborhoods.Neighborhood;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class TwoStep extends Automaton {
    List<Cell> fixedCells;
    boolean isNaiveGrainGrow, secondStep = false;
    private double grainBoundaryEnergy = 0.2;

    public TwoStep(Board2D board2D, Neighborhood neighborhood, boolean isNaiveGrainGrow) {
        super(board2D, neighborhood);
        this.fixedCells = new ArrayList<>();
        this.isNaiveGrainGrow = isNaiveGrainGrow;
    }

    @Override
    public boolean oneIteration() {
        if (isNaiveGrainGrow)
            return oneIterationNaiveGrainGrow();
        else
            return oneIterationMonteCarlo();
    }

    private boolean oneIterationNaiveGrainGrow() {
        boardChanged = false;

        IntStream.range(0, board2D.width * board2D.height).parallel().forEach(i -> {
            List<List<Cell>> neighborhoods = new ArrayList<>();
            int x = i % board2D.width;
            int y = i / board2D.width;

            Cell current = board2D.getCell(x, y);
            if(current != board2D.getInitialCell()) {
                nextBoard.setCell(x, y, current);
                return;
            }

            List<Cell> neighborPos = mooreNeighPos.get(i).stream().map(coords ->
                    board2D.getCell(coords[0], coords[1])).collect(Collectors.toCollection(ArrayList::new));
            neighborhoods.add(neighborPos);

            Cell nextCell = getNextCellState(board2D.getCell(x, y), neighborhoods);
            if(current != nextCell) {
                nextBoard.setCell(x, y, nextCell);
                boardChanged = true;
            }
        });

        Board2D swapBoardTmp = board2D;

        board2D = nextBoard;
        nextBoard = swapBoardTmp;

        secondStep = false;
        return boardChanged;
    }

    private boolean oneIterationMonteCarlo() {
        boardChanged = false;

        List<Integer> indexesList = new ArrayList<>();
        IntStream.range(0, board2D.width * board2D.height).forEach(indexesList::add);
        Collections.shuffle(indexesList);

        indexesList.parallelStream().forEach(idx -> {
            List<List<Cell>> neighborhoods = new ArrayList<>();
            int x = idx % board2D.width;
            int y = idx / board2D.width;
            Cell current = board2D.getCell(x, y);

            List<Cell> neighborPos = mooreNeighPos.get(idx).stream().map(coords ->
                    board2D.getCell(coords[0], coords[1])).collect(Collectors.toCollection(ArrayList::new));
            neighborhoods.add(neighborPos);

            Cell nextCell = getNextCellState(board2D.getCell(x, y), neighborhoods);
            if (current != nextCell) {
                board2D.setCell(x, y, nextCell);
                boardChanged = true;
            }
        });

        secondStep = false;
        return boardChanged;
    }

    @Override
    protected Cell getNextCellState(Cell cell, List<List<Cell>> neighbours) {
        if ((secondStep && isNaiveGrainGrow) || (!secondStep && isNaiveGrainGrow))
            return  nextCellStateNaiveGrainGrow(cell, neighbours);

//        if ((!secondStep && !isNaiveGrainGrow) || (secondStep && !isNaiveGrainGrow))
        return  nexCellStateMonteCarlo(cell, neighbours);
    }

    private Cell nexCellStateMonteCarlo(Cell cell, List<List<Cell>> neighbours) {
        Cell inclusionCell = board2D.getInclusionCell();
        Cell initialCell = board2D.getInitialCell();
        double sameCellCount, energyBefore, energyAfter, deltaEnergy;
        Random rnd = new Random();

        if(cell.isFixedState()) return cell;
        if(cell == inclusionCell) return cell;

        if(neighbours.get(0).stream().allMatch(c -> c == cell || c == inclusionCell || c == initialCell || c.isFixedState())) {
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

        if (listOfColors.get(cell) != null)
            sameCellCount = listOfColors.get(cell).getValue();
        else
            sameCellCount = 0;
        energyBefore = grainBoundaryEnergy * (neighbours.get(0).size() - sameCellCount);

//        Here we can take cell randomly from all cell states or just from neighborhood
//        Cell rndCell = board2D.getPrecomputedCells().get(rnd.nextInt(board2D.getPrecomputedCells().size()));
        Cell rndCell = neighbours.get(0).get(rnd.nextInt(neighbours.get(0).size()));
        if (listOfColors.get(rndCell) != null)
            sameCellCount = listOfColors.get(rndCell).getValue();
        else
            sameCellCount = 0;
        energyAfter = grainBoundaryEnergy * (neighbours.get(0).size() - sameCellCount);

        deltaEnergy = energyAfter - energyBefore;
        if (deltaEnergy <= 0)
            return rndCell;
        else
            return cell;
    }

    private Cell nextCellStateNaiveGrainGrow(Cell cell, List<List<Cell>> neighbours) {
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

        if (isNaiveGrainGrow)
            generationForNaiveGrainGrow(statesToGenerate, countToGenerate, precomputedCells);
        else
            generationForMonteCarlo(statesToGenerate, countToGenerate, precomputedCells);

        syncNextBoard();
    }


    private void generationForMonteCarlo(int statesToGenerate, int countToGenerate, List<Cell> precomputedCells) {
        Random random = new Random();
        for(int x = 0; x < board2D.getWidth(); ++x){
            for(int y = 0; y < board2D.getHeight(); ++y){
                if (board2D.getCell(x, y) == board2D.getInitialCell())
                    board2D.setCell(x, y, precomputedCells.get(random.nextInt(statesToGenerate)));
            }
        }
    }

    private void generationForNaiveGrainGrow(int statesToGenerate, int countToGenerate, List<Cell> precomputedCells) {
        secondStep = true;
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
    }

    public void setGrainBoundaryEnergy(double grainBoundaryEnergy) {
        this.grainBoundaryEnergy = grainBoundaryEnergy;
    }
}
