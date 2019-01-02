package pl.wieloskalowe.automaton;

import pl.wieloskalowe.Board2D;
import pl.wieloskalowe.CoordinatesWrapper;
import pl.wieloskalowe.cell.Cell;
import pl.wieloskalowe.neighborhoods.Neighborhood;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class MonteCarlo extends Automaton {
    private double grainBoundaryEnergy = 0.2;

    public MonteCarlo(Board2D board2D, Neighborhood neighborhood) {
        super(board2D, neighborhood);
    }

    public MonteCarlo(Board2D board2D, Neighborhood neighborhood, CoordinatesWrapper coordinatesWrapper) {
        super(board2D, neighborhood, coordinatesWrapper);
    }

    @Override
    public boolean oneIteration() {
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

            Cell nextCell = getNextCellState(board2D.getCell(idx), neighborhoods);
            if (current != nextCell) {
                board2D.setCell(idx, nextCell);
                boardChanged = true;
            }
        });

        return boardChanged;
    }

    @Override
    protected Cell getNextCellState(Cell cell, List<List<Cell>> neighbours) {
        Cell inclusionCell = board2D.getInclusionCell();
        Cell initialCell = board2D.getInitialCell();
        double energyBefore, energyAfter, deltaEnergy;
        Random rnd = new Random();

        if(cell == inclusionCell) return cell;
        if(cell.isFixedState()) return cell;

        if(neighbours.get(0).stream().allMatch(c -> c == cell || c == inclusionCell || c == initialCell || c.isFixedState())) {
            return cell;
        }

        energyBefore = grainBoundaryEnergy * getCellEnergy(cell, neighbours.get(0));

//        Here we can take cell randomly from all cell states or just from neighborhood
        Cell rndCell = board2D.getPrecomputedCells().get(rnd.nextInt(board2D.getPrecomputedCells().size()));
//        Cell rndCell = neighbours.get(0).get(rnd.nextInt(neighbours.get(0).size()));
        energyAfter = grainBoundaryEnergy * getCellEnergy(rndCell, neighbours.get(0));

        deltaEnergy = energyAfter - energyBefore;
        if (deltaEnergy <= 0)
            return rndCell;
        else
            return cell;
    }

    private long getCellEnergy(Cell currentCell, List<Cell> neighbours) {
        return neighbours.stream().filter(cell -> cell != currentCell).count();
    }

    public void setGrainBoundaryEnergy(double grainBoundaryEnergy) {
        this.grainBoundaryEnergy = grainBoundaryEnergy;
    }
}
