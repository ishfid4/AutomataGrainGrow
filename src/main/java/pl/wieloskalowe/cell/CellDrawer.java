package pl.wieloskalowe.cell;

import javafx.animation.AnimationTimer;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import pl.wieloskalowe.*;

import java.util.concurrent.atomic.AtomicReference;

public abstract class CellDrawer extends AnimationTimer {
    private final Canvas canvas;
    private double cellWidth, cellHeight;
    private String automatonType;
    private final AtomicReference<Board2D> board2DAtomicReferenc = new AtomicReference<Board2D>(null);

    public CellDrawer(Canvas canvas) {
        this.canvas = canvas;
    }

    public void setUpData(double cellWidth, double cellHeight, String automatonType) {
        this.cellWidth = cellWidth;
        this.cellHeight = cellHeight;
        this.automatonType = automatonType;
    }

    public void requestRedraw(Board2D board2D) {
        board2DAtomicReferenc.set(board2D);
        start();
    }


    @Override
    public void handle(long now) {
        Board2D boardToDraw = board2DAtomicReferenc.getAndSet(null);
        if (boardToDraw != null)
            redraw(canvas.getGraphicsContext2D(), boardToDraw, automatonType, cellWidth, cellHeight, canvas.getHeight(), canvas.getWidth());
    }

    protected abstract void redraw(GraphicsContext graphicsContext, Board2D board2D, String automatonType,
                                   double cellWidth, double cellHeight, double canvasHeight, double canvasWidth);

}

