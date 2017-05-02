package pl.wieloskalowe;

/**
 * Created by ishfi on 02.05.2017.
 */
public class Main {

    public static void main(String[] args) {

        Board2D board2D = new Board2D(3, 3,new CellBinary(false));
        MooreNeighborhood mooreNeighborhood = new MooreNeighborhood(1, 3,3, false);
        GameOfLife gameOfLife = new GameOfLife(board2D, mooreNeighborhood);
        gameOfLife.oneIteration();

        System.out.println("Git Gud");
    }
}
