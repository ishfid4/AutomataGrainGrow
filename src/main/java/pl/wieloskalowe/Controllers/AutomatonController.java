package pl.wieloskalowe.Controllers;

import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import pl.wieloskalowe.*;


/**
 * Created by ishfi on 03.05.2017.
 */
public class AutomatonController {
    @FXML private Label errorLabel;
    @FXML private TextField widthField;
    @FXML private TextField heightField;
    @FXML private TextField radiusField;
    @FXML private AnchorPane anchorPaneForCanvas;
    @FXML private Canvas canvas;
    private double cellWidth, cellHeight;
    private boolean started = false;
    private Board2D board2D;
    private MooreNeighborhood mooreNeighborhood;
    private GameOfLife gameOfLife;
    private GraphicsContext graphicsContext;
    private Ticker ticker;

    @FXML public void initialize() {
        graphicsContext = canvas.getGraphicsContext2D();

        canvas.addEventHandler(MouseEvent.MOUSE_CLICKED,
                t -> {
                    if (t.getClickCount() == 1) {
                        changeCellState(t.getX(), t.getY());
                        drawBoard();
                    }
                });
    }

    @FXML public void setClicked(){
        if(widthField.getText().isEmpty())
            Platform.runLater(() -> errorLabel.setText("Invalid width!"));
        else if (heightField.getText().isEmpty())
            Platform.runLater(() -> errorLabel.setText("Invalid height!"));
        else if (radiusField.getText().isEmpty())
            Platform.runLater(() -> errorLabel.setText("Invalid radius!"));
        else {
            Platform.runLater(() -> errorLabel.setText(""));

            setUpAutomaton(Integer.parseInt(widthField.getText()), Integer.parseInt(heightField.getText()), Integer.parseInt(radiusField.getText()));

            ticker = new Ticker(gameOfLife);

            canvas.setWidth(anchorPaneForCanvas.getWidth());
            canvas.setHeight(anchorPaneForCanvas.getHeight());

            cellHeight = anchorPaneForCanvas.getHeight() / Integer.parseInt(heightField.getText());
            cellWidth = anchorPaneForCanvas.getWidth() / Integer.parseInt(widthField.getText());

            drawBoard();
        }
    }

    //TODO: Make changes showing in gui when its iterated by ticker

    @FXML public void startStopClicked() {
        this.started = !this.started;
        if (started)
            ticker.start();
        else
            ticker.stop();
    }

    @FXML public void iterateClicked() {
        gameOfLife.oneIteration();
        drawBoard();
    }

    private void changeCellState(double x, double y) {
        CellCoordinates cellCoordinates = new CellCoordinates((int)(x / cellWidth), (int)(y / cellHeight));

        gameOfLife.getBoard().getCell(cellCoordinates).nextState();
    }

    private void drawCell(int x, int y, boolean alive) {
        if (alive)
            graphicsContext.fillRect(x * cellWidth,y * cellHeight,cellWidth,cellHeight);
        else
            graphicsContext.strokeRect(x * cellWidth,y * cellHeight,cellWidth,cellHeight);
    }

    private void drawBoard() {
        graphicsContext.clearRect(0,0,anchorPaneForCanvas.getWidth(),anchorPaneForCanvas.getHeight());

        for (CellCoordinates cellCoordinates: gameOfLife.getBoard().getAllCoordinates()) {
            drawCell(cellCoordinates.getX(),cellCoordinates.getY(),gameOfLife.getBoard().getCell(cellCoordinates).isAlive());
        }
    }

    private void setUpAutomaton(int width, int height, int radius){
        board2D = new Board2D(width, height,new CellBinary(false));
        mooreNeighborhood = new MooreNeighborhood(radius, width,height, false);
        gameOfLife = new GameOfLife(board2D, mooreNeighborhood);
    }
}
