package pl.wieloskalowe;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Created by ishfi on 02.05.2017.
 */
public class GameOfLifeTest {
    boolean[][][] oscilator = {
            {
                    {false,true,false},
                    {false,true,false},
                    {false,true,false},
            },
            {
                    {false,false,false},
                    {true,true,true},
                    {false,false,false},
            },
            {
                    {false,false,false},
                    {false,true,false},
                    {false,false,false},
            },
    };

    boolean[][][] wrap = {
            {
                    {true,true,false,true},
                    {false,false,false,false},
                    {false,false,false,false},
                    {false,false,false,false},
            },
            {
                    {true,false,false,false},
                    {true,false,false,false},
                    {false,false,false,false},
                    {true,false,false,false}
            },
    };
    
    @Test
    public void oscilatorFalseOutterCell() throws Exception {
        Board2D board2D = new Board2D(3, 3, new CellBinary(false));

        board2D.getCell(new CellCoordinates(0,1)).setState(true);
        board2D.getCell(new CellCoordinates(1,1)).setState(true);
        board2D.getCell(new CellCoordinates(2,1)).setState(true);

        MooreNeighborhood mooreNeighborhood = new MooreNeighborhood(1,3,3, false);
        GameOfLife gol = new GameOfLife(board2D, mooreNeighborhood);
        gol.oneIteration();

        checkCorrectness(oscilator[1],gol.getBoard(),3,3);

        gol.oneIteration();
        checkCorrectness(oscilator[0],gol.getBoard(),3,3);
    }

    @Test
    public void oscilatorTrueOutterCell() throws Exception {
        Board2D board2D = new Board2D(3, 3, new CellBinary(true));

        board2D.getCell(new CellCoordinates(0,1)).setState(true);
        board2D.getCell(new CellCoordinates(1,1)).setState(true);
        board2D.getCell(new CellCoordinates(2,1)).setState(true);

        MooreNeighborhood mooreNeighborhood = new MooreNeighborhood(1,3,3, false);
        GameOfLife gol = new GameOfLife(board2D, mooreNeighborhood);
        gol.oneIteration();

        checkCorrectness(oscilator[2],gol.getBoard(),3,3);
    }

    @Test
    public void wrap() throws Exception {
        Board2D board2D = new Board2D(4, 4, new CellBinary(false));

        board2D.getCell(new CellCoordinates(0,1)).setState(true);
        board2D.getCell(new CellCoordinates(0,0)).setState(true);
        board2D.getCell(new CellCoordinates(0,3)).setState(true);

        MooreNeighborhood mooreNeighborhood = new MooreNeighborhood(1,4,4, true);
        GameOfLife gol = new GameOfLife(board2D, mooreNeighborhood);
        gol.oneIteration();

        checkCorrectness(wrap[1],gol.getBoard(),4,4);
    }

    private void checkCorrectness(boolean[][] expected, Board2D board2D, int width, int height) {
        for (int i = 0; i < width; i++){
            for (int j = 0; j < height; j++) {
                assertEquals(expected[i][j],board2D.getCell(new CellCoordinates(i,j)).isAlive());
            }
        }
    }
}
