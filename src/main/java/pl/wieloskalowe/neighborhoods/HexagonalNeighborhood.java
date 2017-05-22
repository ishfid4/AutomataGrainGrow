package pl.wieloskalowe.neighborhoods;

import pl.wieloskalowe.cell.CellCoordinates;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

/**
 * Created by ishfi on 13.05.2017.
 */
public class HexagonalNeighborhood implements Neighborhood {
    public enum version{
        RIGHT, LEFT, RANDOM
    }

    private HexagonalNeighborhood.version ver;
    private Random random = new Random();

    public HexagonalNeighborhood(HexagonalNeighborhood.version ver) {
        this.ver = ver;
    }

    @Override
    public Set<CellCoordinates> cellNeighbors(CellCoordinates cellCoordinates) {
        switch (ver) {
            case LEFT:
                return leftNH(cellCoordinates);
            case RIGHT:
                return rightNH(cellCoordinates);
            case RANDOM:
                if (random.nextInt(100) % 2 == 0)
                    return rightNH(cellCoordinates);
                else
                    return leftNH(cellCoordinates);
            default:
                return rightNH(cellCoordinates);
        }
    }

    private Set<CellCoordinates> rightNH(CellCoordinates cellCoordinates) {
        Set<CellCoordinates> neighbours = new HashSet<>();

        for (int x = -1; x <= 1; x++) {
            for (int y = -1; y <= 1; y++) {
                if (x != y) {
                    neighbours.add(new CellCoordinates(cellCoordinates.getX() + x, cellCoordinates.getY() + y));
                }
            }
        }

        return neighbours;
    }

    private Set<CellCoordinates> leftNH(CellCoordinates cellCoordinates) {
        Set<CellCoordinates> neighbours = new HashSet<>();

        for (int x = -1; x <= 1; x++) {
            for (int y = -1; y <= 1; y++) {
                if ((x != 0 || y != 0) && (x != 1 || y != -1) && (x != -1 || y != 1)) {
                    neighbours.add(new CellCoordinates(cellCoordinates.getX() + x, cellCoordinates.getY() + y));
                }
            }
        }

        return neighbours;
    }
}
