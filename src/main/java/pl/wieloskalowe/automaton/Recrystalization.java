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
    List<Double> cellsEnergryCopy = null;
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
        if (isNucleationRate) {
            generateNecluation(board2D.getPrecomputedRecrystalizedCells());
        }
        syncNextBoard();
        this.cellsEnergryCopy = board2D.getCellsEnergy();
        return oneIterationMonteCarloRecryst();
    }

    private boolean oneIterationNaiveGrainGrow() {
        boardChanged = false;

        IntStream.range(0, board2D.width * board2D.height).parallel().forEach(i -> {
            List<List<Cell>> neighborhoods = new ArrayList<>();

            Cell current = board2D.getCell(i);
            if(current != board2D.getInitialCell()) {
                nextBoard.setCell(i, current);
                return;
            }

            List<Cell> neighborPos = mooreNeighPos.get(i).stream().map(coords ->
                    board2D.getCell(coords[0] * board2D.width + coords[1])).collect(Collectors.toCollection(ArrayList::new));
            neighborhoods.add(neighborPos);

            Cell nextCell = getNextCellState(board2D.getCell(i), neighborhoods);
            if(current != nextCell) {
                nextBoard.setCell(i, nextCell);
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
            Cell current = board2D.getCell(idx);

            List<Cell> neighborPos = mooreNeighPos.get(idx).stream().map(coords ->
                    board2D.getCell(coords[0]* board2D.width + coords[1])).collect(Collectors.toCollection(ArrayList::new));
            neighborhoods.add(neighborPos);

            Cell nextCell = getNextCellState(board2D.getCell(idx), neighborhoods);
            if (current != nextCell) {
                board2D.setCell(idx, nextCell);
                boardChanged = true;
            }
        });

        return boardChanged;
    }

 private boolean oneIterationMonteCarloRecryst() {
        boardChanged = false;

        List<Integer> indexesList = new ArrayList<>();
        IntStream.range(0, board2D.width * board2D.height).forEach(indexesList::add);
        Collections.shuffle(indexesList);

        indexesList.parallelStream().forEach(idx -> {
            List<List<Cell>> neighborhoods = new ArrayList<>();
            Cell current = board2D.getCell(idx);

            List<Cell> neighborPos = mooreNeighPos.get(idx).stream().map(coords ->
                    board2D.getCell(coords[0] * board2D.width + coords[1])).collect(Collectors.toCollection(ArrayList::new)); //TODO sprawdzic czy czasem to ułożenie indexow nie jest zjebane
            neighborhoods.add(neighborPos);

            Cell nextCell = getNextCellStateRecryst(board2D.getCell(idx), neighborhoods, idx);
            if (current != nextCell) {
                board2D.setCell(idx, nextCell);
                boardChanged = true;
            }
        });

        board2D.setCellsEnergy(cellsEnergryCopy);

        return boardChanged;
    }

    @Override
    protected Cell getNextCellState(Cell cell, List<List<Cell>> neighbours) {
        if (!secondStep && isFirstStepNaiveGrainGrow)
            return  nextCellStateNaiveGrainGrow(cell, neighbours);

        if (!secondStep && !isFirstStepNaiveGrainGrow)
            return  nexCellStateMonteCarlo(cell, neighbours);

//        if (secondStep)
        return null;
    }

    protected Cell getNextCellStateRecryst(Cell cell, List<List<Cell>> neighbours, int cellEnergyIdx) {
        return  nexCellStateMonteCarloRecryst(cell, neighbours, cellEnergyIdx);
    }

    private Cell nexCellStateMonteCarloRecryst(Cell cell, List<List<Cell>> neighbours, int cellEnergyIdx) {
        Cell inclusionCell = board2D.getInclusionCell();
        Cell initialCell = board2D.getInitialCell();
        double sameCellCount, energyBefore, energyAfter, deltaEnergy;
        Random rnd = new Random();

        if(cell.isRecrystalized()) {
            cellsEnergryCopy.set(cellEnergyIdx, 0.0);
            return cell;
        }
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
        energyBefore = (neighbours.get(0).size() - sameCellCount) + cellsEnergryCopy.get(cellEnergyIdx);


//        Here we can take cell randomly from all cell states or just from neighborhood
        Cell rndCell = neighbours.get(0).get(rnd.nextInt(neighbours.get(0).size()));
        if(!rndCell.isRecrystalized())
            return cell;
        if (listOfColors.get(rndCell) != null)
            sameCellCount = listOfColors.get(rndCell).getValue();
        else
            sameCellCount = 0;
        energyAfter = (neighbours.get(0).size() - sameCellCount);

        deltaEnergy =  energyBefore - energyAfter;
        if (deltaEnergy > 0) {
            cellsEnergryCopy.set(cellEnergyIdx, 0.0);
            return rndCell;
        }
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
        Cell rndCell = board2D.getPrecomputedCells().get(rnd.nextInt(board2D.getPrecomputedCells().size()));
//        Cell rndCell = neighbours.get(0).get(rnd.nextInt(neighbours.get(0).size()));
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

    //Statest to generate must be taken for MC from unique states
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
        int idx = 0, recrystalizedCellIdx = 99;
        int countToGenerate = nucleationCountOrRate;

        if (!isRandomDistribution) {
            List<Integer> indexesCellList = board2D.edgeCells(neighborhood);
            Collections.shuffle(indexesCellList);
            while (countToGenerate > 0 && idx < indexesCellList.size()) {
                if (recrystalizedCellIdx < 0)
                    recrystalizedCellIdx = 99;

                if (!board2D.getCell(indexesCellList.get(idx)).isRecrystalized()){
                    board2D.setCell(indexesCellList.get(idx), precomputedRecrystalizedCells.get(recrystalizedCellIdx));
                    board2D.setCellEnergy(indexesCellList.get(idx), 0.0);
                    --countToGenerate;
                    --recrystalizedCellIdx;
                }
                ++idx;
            }
        } else {
            List<Integer> indexesCellList = new ArrayList<>();
            IntStream.range(0, board2D.width * board2D.height).forEach(indexesCellList::add);
            Collections.shuffle(indexesCellList);
            while (countToGenerate > 0 && idx < indexesCellList.size()) {
                if (recrystalizedCellIdx < 0)
                    recrystalizedCellIdx = 99;

                if (!board2D.getCell(indexesCellList.get(idx)).isRecrystalized()){
                    board2D.setCell(indexesCellList.get(idx), precomputedRecrystalizedCells.get(recrystalizedCellIdx));
                    board2D.setCellEnergy(indexesCellList.get(idx), 0.0);
                    --countToGenerate;
                    --recrystalizedCellIdx;
                }
                ++idx;
            }
        }

        syncNextBoard();
    }
}
