package pl.wieloskalowe.automaton;

import javafx.scene.paint.Color;
import javafx.util.Pair;
import pl.wieloskalowe.Board2D;
import pl.wieloskalowe.CoordinatesWrapper;
import pl.wieloskalowe.cell.Cell;
import pl.wieloskalowe.neighborhoods.CornersOfMooreNeighborhood;
import pl.wieloskalowe.neighborhoods.Neighborhood;
import pl.wieloskalowe.neighborhoods.VonNeumanNeighborhood;

import java.util.*;

@Deprecated
public class FourRulesGrainGrow extends Automaton {
    public FourRulesGrainGrow(Board2D board2D, Neighborhood neighborhood) {
        super(board2D, neighborhood);
    }

    public FourRulesGrainGrow(Board2D board2D, Neighborhood neighborhood, CoordinatesWrapper coordinatesWrapper) {
        super(board2D, neighborhood, coordinatesWrapper);
    }

    //TODO Only on edge cells should be processed?/or cell dead
    //TODO przenieść sprawdzanie reguły przejscia do pojedynczej komórki ->
    @Override
    protected Cell getNextCellState(Cell cell,  List<Cell> neighbours) { //Always starting will be moore
        if (cell.isAlive()) {
            return cell;
        }

        if ((cell).isInclusion()) {
            return cell;
        }

        Color rndColorFromMoore;

        Color cellColor = Color.WHITE;

        Map<Color, Integer> listOfColors = new HashMap<>();

        /* First rule */
        Map<Color, Integer> listOfMooreColors = new HashMap<>();
        for (Cell c : neighbours) {
            cellColor = c.getColor();
            if (cellColor != Color.WHITE) {
                if (listOfMooreColors.containsKey(cellColor)) {
                    int tmp = listOfMooreColors.get(cellColor);
                    tmp++;
                    listOfMooreColors.replace(cellColor, tmp);
                } else {
                    listOfMooreColors.put(cellColor, 1);
                }
            }
        }

        for (Color col : listOfMooreColors.keySet()) {
            if (listOfMooreColors.get(col) >= 5) {
                cellColor = col;
                return new Cell(true, cellColor);
            }
        }

        /* Second rule */
        Neighborhood vonNeuman = new VonNeumanNeighborhood(1);
        ArrayList<Pair<Integer, Integer>> coordinatesNeighbours =  vonNeuman.cellNeighbors(this.currentX, this.currentY);

        ArrayList<Cell> cellsNeighbors = new ArrayList<>(coordinatesNeighbours.size());
        for (Pair<Integer, Integer> coordinatesNeighbour : coordinatesNeighbours) {
            cellsNeighbors.add(board2D.getCell(coordinatesNeighbour.getKey(), coordinatesNeighbour.getValue()));
        }

        listOfColors = new HashMap<>();
        for (Cell c : neighbours) {
            cellColor = c.getColor();
            if (cellColor != Color.WHITE) {
                if (listOfColors.containsKey(cellColor)) {
                    int tmp = listOfColors.get(cellColor);
                    tmp++;
                    listOfColors.replace(cellColor, tmp);
                } else {
                    listOfColors.put(cellColor, 1);
                }
            }
        }

        for (Color col : listOfColors.keySet()) {
            if (listOfColors.get(col) == 3) {
                cellColor = col;
                return new Cell(true, cellColor);
            }
        }

        /* Third rule */
        Neighborhood cornersOfMoore = new CornersOfMooreNeighborhood(1);
        coordinatesNeighbours =  cornersOfMoore.cellNeighbors(this.currentX, this.currentY);

        cellsNeighbors = new ArrayList<>(coordinatesNeighbours.size());
        for (Pair<Integer, Integer> coordinatesNeighbour : coordinatesNeighbours) {
            cellsNeighbors.add(board2D.getCell(coordinatesNeighbour.getKey(), coordinatesNeighbour.getValue()));
        }

        listOfColors = new HashMap<>();
        for (Cell c : neighbours) {
            cellColor = c.getColor();
            if (cellColor != Color.WHITE) {
                if (listOfColors.containsKey(cellColor)) {
                    int tmp = listOfColors.get(cellColor);
                    tmp++;
                    listOfColors.replace(cellColor, tmp);
                } else {
                    listOfColors.put(cellColor, 1);
                }
            }
        }

        for (Color col : listOfColors.keySet()) {
            if (listOfColors.get(col) == 3) {
                cellColor = col;
                return new Cell(true, cellColor);
            }
        }

        /* For Fourth rule */
        Random rnd = new Random();
        int rndNum;

        for (Color col : listOfMooreColors.keySet()) {
            rndNum = rnd.nextInt(100);
            if (rndNum == 10 || rndNum == 20 || rndNum == 30 || rndNum == 40) { //TODO: change that, we should specify probability
                rndColorFromMoore = col;
                return new Cell(true, rndColorFromMoore);
            }
        }

        return cell;
    }
}
