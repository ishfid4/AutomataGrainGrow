package pl.wieloskalowe.cell;

import javafx.animation.AnimationTimer;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.ImageView;
import pl.wieloskalowe.*;

import java.util.concurrent.atomic.AtomicReference;

public abstract class CellDrawer extends AnimationTimer {
    private final ImageView imageView;
    private int cellsWidth, cellsHeight;
    private String automatonType;
    private final AtomicReference<Board2D> board2DAtomicReferenc = new AtomicReference<Board2D>(null);

    public CellDrawer(ImageView imageView) {
        this.imageView = imageView;
    }

    public void setUpData(int cellsWidth, int cellsHeight, String automatonType) {
        this.cellsWidth = cellsWidth;
        this.cellsHeight = cellsHeight;
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
            redraw(imageView, boardToDraw, automatonType, cellsWidth, cellsHeight);
    }

    protected abstract void redraw(ImageView imageView, Board2D board2D, String automatonType,
                                   int cellsWidth, int cellsHeight);

}

