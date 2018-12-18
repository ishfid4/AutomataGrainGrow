package pl.wieloskalowe.automaton;

import javafx.scene.paint.Color;
import javafx.util.Pair;
import pl.wieloskalowe.Board2D;
import pl.wieloskalowe.cell.Cell;
import pl.wieloskalowe.neighborhoods.Neighborhood;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Recrystalization extends Automaton {
    List<Cell> fixedCells;
    boolean isFirstStepNaiveGrainGrow, isNucleationRate, isHeterogenous, isRandomDistribution, secondStep = false;
    private double grainBoundaryEnergy = 0.2;
    int nucleationCountOrRate;

    public Recrystalization(Board2D board2D, Neighborhood neighborhood, boolean isFirstStepNaiveGrainGrow) {
        super(board2D, neighborhood);
        this.fixedCells = new ArrayList<>();
        this.isFirstStepNaiveGrainGrow = isFirstStepNaiveGrainGrow;
    }

    @Override
    public boolean oneIteration() {
        if (!secondStep && isFirstStepNaiveGrainGrow)
            return oneIterationNaiveGrainGrow();

        if (!secondStep && !isFirstStepNaiveGrainGrow)
            return oneIterationMonteCarlo();

//        if (secondStep)
        if (isNucleationRate)
            generateNecluation(board2D.getPrecomputedRecrystalizedCells());
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

        return boardChanged;
    }

    @Override
    protected Cell getNextCellState(Cell cell, List<List<Cell>> neighbours) {
        if (!secondStep && isFirstStepNaiveGrainGrow)
            return  nextCellStateNaiveGrainGrow(cell, neighbours);

        if (!secondStep && !isFirstStepNaiveGrainGrow)
            return  nexCellStateMonteCarlo(cell, neighbours);

//        if (secondStep)
        return  nexCellStateMonteCarloRecryst(cell, neighbours);
    }

    private Cell nexCellStateMonteCarloRecryst(Cell cell, List<List<Cell>> neighbours) {
        Cell inclusionCell = board2D.getInclusionCell();
        Cell initialCell = board2D.getInitialCell();
        double sameCellCount, energyBefore, energyAfter, deltaEnergy;
        Random rnd = new Random();

        if(cell.isRecrystalized()) return cell;
        if(cell.isFixedState()) return cell;
        if(cell == inclusionCell) return cell;

        if(neighbours.get(0).stream().allMatch(c -> c == cell || c == inclusionCell || c == initialCell || c.isFixedState() || c.isRecrystalized())) {
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

//        Here we can take cell randomly from all cell states or just from neighborhood
//        Cell rndCell = board2D.getPrecomputedRecrystalizedCells().get(rnd.nextInt(board2D.getPrecomputedRecrystalizedCells().size()));
        Cell rndCell = neighbours.get(0).get(rnd.nextInt(neighbours.get(0).size()));
        if(!rndCell.isRecrystalized())
            return cell;

        if (listOfColors.get(rndCell) != null)
            sameCellCount = listOfColors.get(rndCell).getValue();
        else
            sameCellCount = 0;
        energyAfter = grainBoundaryEnergy * (neighbours.get(0).size() - sameCellCount);

        if (listOfColors.get(cell) != null)
            sameCellCount = listOfColors.get(cell).getValue();
        else
            sameCellCount = 0;
        energyBefore = grainBoundaryEnergy * (neighbours.get(0).size() - sameCellCount) + 2.0;

        deltaEnergy =  energyBefore - energyAfter;
        if (deltaEnergy > 0)
            return rndCell; // nucleons h =0
        else
            return cell;
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

    //TODO statest to generate must be taken for MC from unique states
    public void get2ndStepReady(int nucleationCountOrRate, boolean isNucleationRate, boolean isHeterogenous, boolean isRandomDistribution) {
        List<Double> cellsEnergyList = new ArrayList<>();
        secondStep = true;
        this.isNucleationRate = isNucleationRate;
        this.nucleationCountOrRate = nucleationCountOrRate;
        this.isHeterogenous = isHeterogenous;
        this.isRandomDistribution = isRandomDistribution;
        if (isHeterogenous){
            IntStream.range(0, board2D.width * board2D.height).forEach(value -> cellsEnergyList.add(value, 2.0));
        } else {
            IntStream.range(0, board2D.width * board2D.height).forEach(value -> cellsEnergyList.add(value, 5.0));
        }

        if (board2D.isFilled() && isHeterogenous) {
            List<Integer> cellsOnEdge = board2D.edgeCells(neighborhood);
            cellsOnEdge.forEach(integer -> cellsEnergyList.set(integer, 7.0));
        }

        board2D.setCellsEnergy(cellsEnergyList);

        List<Cell> precomutedRecystalizedCells = board2D.precomuteRecrystalizedCells(100);

        generateNecluation(precomutedRecystalizedCells);

        syncNextBoard();
    }

    private void generateNecluation(List<Cell> precomputedRecrystalizedCells) {
        if (!isRandomDistribution) {
            List<Integer> cellsOnEdge = board2D.edgeCells(neighborhood);
            Collections.shuffle(cellsOnEdge);
            int countToGenerate = nucleationCountOrRate;
            boolean brakeAloop;
            int cellsOnEdgeIdx = 0;
            while (countToGenerate > 0) {
                for (int i = 0; i < 100 && countToGenerate > 0; ++i) {
                    if (cellsOnEdgeIdx > cellsOnEdge.size())
                        cellsOnEdgeIdx = 0;
                    brakeAloop = false;
                    int x = 0, y = 0;
                    while (!brakeAloop) {
                        x = cellsOnEdge.get(cellsOnEdgeIdx) % board2D.getWidth();
                        y = cellsOnEdge.get(cellsOnEdgeIdx) / board2D.getWidth();
                        if (!board2D.getCell(y, x).isRecrystalized()){
                            brakeAloop = !board2D.getCell(y, x).isRecrystalized();
                            cellsOnEdgeIdx++;
                        }
                    }

                    board2D.setCell(y, x, precomputedRecrystalizedCells.get(i));
                    board2D.setCellEnergy(cellsOnEdge.get(cellsOnEdgeIdx), 0.0);
                    --countToGenerate;
                }
            }
        } else {
            int countToGenerate = nucleationCountOrRate;
            boolean brakeAloop;
            Random random = new Random();
            while (countToGenerate > 0) {
                for (int i = 0; i < 100 && countToGenerate > 0; ++i) {
                    brakeAloop = false;
                    int x = 0, y = 0;
                    while (!brakeAloop) {
                        x = random.nextInt(board2D.getWidth());
                        y = random.nextInt(board2D.height);
                        brakeAloop = !board2D.getCell(x, y).isRecrystalized();
                    }

                    board2D.setCell(x, y, precomputedRecrystalizedCells.get(i));
                    board2D.setCellEnergy(x, y, 0.0);
                    --countToGenerate;
                }
            }
        }
    }
}
