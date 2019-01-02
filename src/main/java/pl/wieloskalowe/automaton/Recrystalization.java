package pl.wieloskalowe.automaton;

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
    NaiveGrainGrow naiveGrainGrow = null;
    MonteCarlo monteCarlo = null;

    public Recrystalization(Board2D board2D, Neighborhood neighborhood, boolean isFirstStepNaiveGrainGrow) {
        super(board2D, neighborhood);
        this.fixedCells = new ArrayList<>();
        this.isFirstStepNaiveGrainGrow = isFirstStepNaiveGrainGrow;
        if (isFirstStepNaiveGrainGrow)
            naiveGrainGrow = new NaiveGrainGrow(board2D, neighborhood);
        else
            monteCarlo = new MonteCarlo(board2D, neighborhood);
    }

    @Override
    protected Cell getNextCellState(Cell cell, List<List<Cell>> neighbours) {
        return null; //TODO make throw exception
    }

    @Override
    public boolean oneIteration() {
        boolean boardChanged = false;
        if (!secondStep && isFirstStepNaiveGrainGrow) {
            naiveGrainGrow.syncBoards(board2D);
            boardChanged = naiveGrainGrow.oneIteration();
            syncBoards(naiveGrainGrow.getBoard());
        }

        if (!secondStep && !isFirstStepNaiveGrainGrow) {
            monteCarlo.syncBoards(board2D);
            boardChanged = monteCarlo.oneIteration();
            syncBoards(monteCarlo.getBoard());
        }

       if (secondStep) {
           if (isNucleationRate) {
               generateNecluation(board2D.getPrecomputedRecrystalizedCells());
           }
           syncNextBoard();
           this.cellsEnergryCopy = board2D.getCellsEnergy();
           boardChanged = oneIterationMonteCarloRecryst();
       }

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
                    board2D.getCell(coords[0] * board2D.width + coords[1])).collect(Collectors.toCollection(ArrayList::new));
            neighborhoods.add(neighborPos);

            Cell nextCell = nextCellStateMonteCarloRecryst(board2D.getCell(idx), neighborhoods, idx);
            if (current != nextCell) {
                board2D.setCell(idx, nextCell);
                boardChanged = true;
            }
        });

        board2D.setCellsEnergy(cellsEnergryCopy);

        return boardChanged;
    }

    private Cell nextCellStateMonteCarloRecryst(Cell cell, List<List<Cell>> neighbours, int cellEnergyIdx) {
        Cell inclusionCell = board2D.getInclusionCell();
        Cell initialCell = board2D.getInitialCell();
        double energyBefore, energyAfter, deltaEnergy;
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

        energyBefore = getCellEnergy(cell, neighbours.get(0)) + cellsEnergryCopy.get(cellEnergyIdx);

//        Here we can take cell randomly from all cell states or just from neighborhood
        Cell rndCell = neighbours.get(0).get(rnd.nextInt(neighbours.get(0).size()));
        if(!rndCell.isRecrystalized())
            return cell;

        energyAfter = getCellEnergy(rndCell, neighbours.get(0));

        deltaEnergy =  energyAfter - energyBefore;
        if (deltaEnergy < 0) {
            cellsEnergryCopy.set(cellEnergyIdx, 0.0);
            return rndCell;
        }
        else
            return cell;
    }

    private long getCellEnergy(Cell currentCell, List<Cell> neighbours) {
        return neighbours.stream().filter(cell -> cell != currentCell).count();
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
