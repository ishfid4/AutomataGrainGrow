package pl.wieloskalowe.Controllers;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import pl.wieloskalowe.Board2D;
import pl.wieloskalowe.Cell;
import pl.wieloskalowe.CellCoordinates;
import pl.wieloskalowe.CellGrain;

/**
 * Created by ishfi on 21.05.2017.
 */
public class CellDrawer {
    private GraphicsContext graphicsContext;
    private double cellWidth, cellHeight;
    private String automatonType;
    private double canvasWidth, canvasHeight;

    public CellDrawer(Canvas canvas, double cellWidth, double cellHeight, String automatonType) {
        this.graphicsContext = canvas.getGraphicsContext2D();
        this.canvasWidth = canvas.getWidth();
        this.canvasHeight = canvas.getHeight();
        this.cellWidth = cellWidth;
        this.cellHeight = cellHeight;
        this.automatonType = automatonType;
    }

    private void drawBinaryCell(int x, int y, boolean alive) {
        if (alive) {
            graphicsContext.fillRect(x * cellWidth, y * cellHeight, cellWidth, cellHeight);
        } else
            graphicsContext.strokeRect(x * cellWidth,y * cellHeight,cellWidth,cellHeight);
    }

    private void drawGrainCell(int x, int y, boolean alive, Color color) {
        if (alive) {
            graphicsContext.setFill(color);
            graphicsContext.fillRect(x * cellWidth, y * cellHeight, cellWidth, cellHeight);
        } else
            graphicsContext.strokeRect(x * cellWidth,y * cellHeight,cellWidth,cellHeight);
    }

    public synchronized void drawBoard(Board2D board2D) {
        graphicsContext.clearRect(0,0,canvasWidth,canvasHeight);

        if (automatonType.equals("GameOfLife")) {
            for (CellCoordinates cellCoordinates : board2D.getAllCoordinates()) {
                Cell cell = board2D.getCell(cellCoordinates);
                drawBinaryCell(cellCoordinates.getX(), cellCoordinates.getY(), cell.isAlive());
            }
//            return graphicsContext;
        }

        if (automatonType.equals("NaiveGrainGrow")) {
            for (CellCoordinates cellCoordinates : board2D.getAllCoordinates()) {
                CellGrain cell = (CellGrain) board2D.getCell(cellCoordinates);
                drawGrainCell(cellCoordinates.getX(), cellCoordinates.getY(), cell.isAlive(), cell.getColor());
            }
//            return graphicsContext;
        }

//        return null;
    }
}

