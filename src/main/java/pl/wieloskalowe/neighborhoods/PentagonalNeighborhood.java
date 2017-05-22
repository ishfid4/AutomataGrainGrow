package pl.wieloskalowe.neighborhoods;

import pl.wieloskalowe.cell.CellCoordinates;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

/**
 * Created by ishfi on 13.05.2017.
 */
public class PentagonalNeighborhood implements Neighborhood{
    public enum version{
        RIGHT, LEFT, RANDOM
    }

    private version ver;
    private Random random = new Random();

    public PentagonalNeighborhood(version ver) {
        this.ver = ver;
    }

    @Override
    public Set<CellCoordinates> cellNeighbors(CellCoordinates cellCoordinates) {
        switch (ver) {
            case RIGHT:
                return rightNH(cellCoordinates);
            case LEFT:
                return leftNH(cellCoordinates);
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

        for (int x = 0; x <= 1; x++){
            for (int y = -1; y <= 1; y++) {
               if (x == 0 && y ==0){
                   continue;
               } else {
                  neighbours.add(new CellCoordinates(cellCoordinates.getX() + x, cellCoordinates.getY() + y));
               }
            }
        }

        return neighbours;
    }

    private Set<CellCoordinates> leftNH(CellCoordinates cellCoordinates) {
        Set<CellCoordinates> neighbours = new HashSet<>();

        for (int x = -1; x <= 0; x++){
            for (int y = -1; y <= 1; y++) {
                if (x == 0 && y ==0){
                    continue;
                } else {
                    neighbours.add(new CellCoordinates(cellCoordinates.getX() + x, cellCoordinates.getY() + y));
                }
            }
        }

        return neighbours;
    }
}
