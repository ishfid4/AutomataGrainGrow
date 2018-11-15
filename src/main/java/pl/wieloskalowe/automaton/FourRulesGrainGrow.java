package pl.wieloskalowe.automaton;

import javafx.util.Pair;
import pl.wieloskalowe.Board2D;
import pl.wieloskalowe.cell.Cell;
import pl.wieloskalowe.neighborhoods.CornersOfMooreNeighborhood;
import pl.wieloskalowe.neighborhoods.Neighborhood;
import pl.wieloskalowe.neighborhoods.VonNeumanNeighborhood;

import java.util.*;

public class FourRulesGrainGrow extends Automaton {
    private int pobability;
    public FourRulesGrainGrow(Board2D board2D, Neighborhood neighborhood) {
        super(board2D, neighborhood);
        this.pobability = 10;
        VonNeumanNeighborhood vonNeumanNeighborhood = new VonNeumanNeighborhood(1);
        CornersOfMooreNeighborhood cornersOfMooreNeighborhood = new CornersOfMooreNeighborhood(1);
        for(int y = 0; y < board2D.height; ++y) {
            for(int x = 0; x < board2D.width; ++x) {
                vonNeumanNeighPos.add(vonNeumanNeighborhood.cellNeighbors(x, y));
                cornersOfMooreNeighPos.add(cornersOfMooreNeighborhood.cellNeighbors(x, y));
            }
        }
    }

    @Override
    protected Cell getNextCellState(Cell cell,  List<List<Cell>> neighbours) {
        Cell initialCell = board2D.getInitialCell();
        Cell inclusionCell = board2D.getInclusionCell();
        Map<Cell, Pair<Cell, Integer>> listOfColorsMoore = new HashMap<>();
        Map<Cell, Pair<Cell, Integer>> listOfColors;

        if(cell == inclusionCell) return cell;
        if(cell != initialCell) return cell;

        if(neighbours.stream().allMatch(c -> c == initialCell || c == inclusionCell)) {
            return cell;
        }

        /* First rule */
        neighbours.get(0).forEach(c -> {
            if (c != board2D.getInitialCell() && c != board2D.getInclusionCell()) {
                Pair<Cell, Integer> currentCount = listOfColorsMoore.getOrDefault(c, new Pair<>(cell, 0));
                listOfColorsMoore.put(c, new Pair<>(c, currentCount.getValue() + 1));
            }
        });

        if (!listOfColorsMoore.isEmpty()){
            Pair<Cell, Integer> winner = Collections.max(listOfColorsMoore.values(), Comparator.comparingInt(Pair::getValue));
            if(winner.getValue() >= 5)
                return winner.getKey();
        }

        /* Second rule */
        listOfColors = new HashMap<>();

        for (Cell c1 : neighbours.get(1)) {
            if (c1 != board2D.getInitialCell() && c1 != board2D.getInclusionCell()) {
                Pair<Cell, Integer> currentCount = listOfColors.getOrDefault(c1, new Pair<>(cell, 0));
                listOfColors.put(c1, new Pair<>(c1, currentCount.getValue() + 1));
            }
        }

        if (!listOfColors.isEmpty()){
            Pair<Cell, Integer> winner = Collections.max(listOfColors.values(), Comparator.comparingInt(Pair::getValue));
            if(winner.getValue() >= 3)
                return winner.getKey();
        }

        /* Third rule */
        listOfColors = new HashMap<>();

        for (Cell c : neighbours.get(2)) {
            if (c != board2D.getInitialCell() && c != board2D.getInclusionCell()) {
                Pair<Cell, Integer> currentCount = listOfColors.getOrDefault(c, new Pair<>(cell, 0));
                listOfColors.put(c, new Pair<>(c, currentCount.getValue() + 1));
            }
        }

        if (!listOfColors.isEmpty()){
            Pair<Cell, Integer> winner = Collections.max(listOfColors.values(), Comparator.comparingInt(Pair::getValue));
            if(winner.getValue() >= 3)
                return winner.getKey();
        }

        /* For Fourth rule */
        Random rnd = new Random();
        int rndNum = rnd.nextInt(101);
        Set<Cell> cellSet = listOfColorsMoore.keySet();
        int size = cellSet.size();
        if (rndNum <= pobability && size > 0) {
            int item = rnd.nextInt(size);
            int i = 0;
            for(Object obj : cellSet)
            {
                if (i == item)
                    return listOfColorsMoore.get(obj).getKey();
                i++;
            }
        }

        return cell;
    }

    public void setPobability(int pobability) {
        this.pobability = pobability;
    }
}
