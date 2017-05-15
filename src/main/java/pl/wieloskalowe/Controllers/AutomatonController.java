package pl.wieloskalowe.Controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import pl.wieloskalowe.*;
import pl.wieloskalowe.neighborhoods.*;

import java.util.Observable;
import java.util.Observer;


/**
 * Created by ishfi on 03.05.2017.
 */
public class AutomatonController implements Observer{
    @FXML private Label errorLabel;
    @FXML private TextField widthField;
    @FXML private TextField heightField;
    @FXML private TextField radiusField;
    @FXML private AnchorPane anchorPaneForCanvas;
    @FXML private Canvas canvas;
    @FXML private ComboBox neighborhoodComboBox, automatonTypeComboBox;
    @FXML private CheckBox wrapCheckBox;
    private double cellWidth, cellHeight;
    private boolean started = false;
    private GraphicsContext graphicsContext;
    private Ticker ticker;
    private AutomatonAdapter automatonAdapter;
    private String automatonChoice;

    @FXML public void initialize() {
        graphicsContext = canvas.getGraphicsContext2D();
        neighborhoodComboBox.setValue(neighborhoodComboBox.getItems().get(0));
        automatonTypeComboBox.setValue(automatonTypeComboBox.getItems().get(0));

        canvas.addEventHandler(MouseEvent.MOUSE_CLICKED,
                t -> {
                    if (t.getClickCount() == 1) {
                        changeCellState(t.getX(), t.getY());
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

            automatonAdapter.addObserver(this);

            ticker = new Ticker(automatonAdapter);

            canvas.setWidth(anchorPaneForCanvas.getWidth());
            canvas.setHeight(anchorPaneForCanvas.getHeight());

            cellHeight = anchorPaneForCanvas.getHeight() / Integer.parseInt(heightField.getText());
            cellWidth = anchorPaneForCanvas.getWidth() / Integer.parseInt(widthField.getText());

            automatonChoice = automatonTypeComboBox.getValue().toString();

            drawBoard();
        }
    }

    @FXML public void startStopClicked() {
        this.started = !this.started;
        if (started)
            ticker.start();
        else
            ticker.stop();
    }

    @FXML public void iterateClicked() {
        automatonAdapter.nextAutomatonState();
    }

    private void changeCellState(double x, double y) {
        CellCoordinates cellCoordinates = new CellCoordinates((int)(x / cellWidth), (int)(y / cellHeight));

        automatonAdapter.changeCellState(cellCoordinates);
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

    private void drawBoard() {
        graphicsContext.clearRect(0,0,anchorPaneForCanvas.getWidth(),anchorPaneForCanvas.getHeight());

        if (automatonChoice.equals("GameOfLife")) {
            for (CellCoordinates cellCoordinates : automatonAdapter.getBoard().getAllCoordinates()) {
                Cell cell = automatonAdapter.getBoard().getCell(cellCoordinates);
                drawBinaryCell(cellCoordinates.getX(), cellCoordinates.getY(), cell.isAlive());
            }
        }

        if (automatonChoice.equals("NaiveGrainGrow")) {
            for (CellCoordinates cellCoordinates : automatonAdapter.getBoard().getAllCoordinates()) {
                CellGrain cell = (CellGrain) automatonAdapter.getBoard().getCell(cellCoordinates);
                drawGrainCell(cellCoordinates.getX(), cellCoordinates.getY(), cell.isAlive(), cell.getColor());
            }
        }
    }

    private void setUpAutomaton(int width, int height, int radius){
        Neighborhood neighborhood = new MooreNeighborhood(radius);
        CoordinatesWrapper coordinatesWrapper;

        if (neighborhoodComboBox.getValue().equals("VonNeuman"))
            neighborhood = new VonNeumanNeighborhood(radius);

        if (neighborhoodComboBox.getValue().equals("Hexagonal_Left"))
            neighborhood = new HexagonalNeighborhood(HexagonalNeighborhood.version.LEFT);

        if (neighborhoodComboBox.getValue().equals("Hexagonal_Right"))
            neighborhood = new HexagonalNeighborhood(HexagonalNeighborhood.version.RIGHT);

        if (neighborhoodComboBox.getValue().equals("Hexagonal_Random"))
            neighborhood = new HexagonalNeighborhood(HexagonalNeighborhood.version.RANDOM);

        if (neighborhoodComboBox.getValue().equals("Pentagonal_Left"))
            neighborhood = new PentagonalNeighborhood(PentagonalNeighborhood.version.LEFT);

        if (neighborhoodComboBox.getValue().equals("Pentagonal_Right"))
            neighborhood = new PentagonalNeighborhood(PentagonalNeighborhood.version.RIGHT);

        if (neighborhoodComboBox.getValue().equals("Pentagonal_Random"))
            neighborhood = new PentagonalNeighborhood(PentagonalNeighborhood.version.RANDOM);

        if (automatonTypeComboBox.getValue().equals("GameOfLife") && wrapCheckBox.isSelected()) {
            Board2D board2D = new Board2D(width, height, new CellBinary(false), new CellBinary());
            coordinatesWrapper = new CoordinatesWrapper(width,height);
            Automaton automaton = new GameOfLife(board2D, neighborhood, coordinatesWrapper);
            automatonAdapter = new AutomatonAdapter(automaton);
        }
        if (automatonTypeComboBox.getValue().equals("GameOfLife") && !wrapCheckBox.isSelected()) {
            Board2D board2D = new Board2D(width, height, new CellBinary(false), new CellBinary());
            Automaton automaton = new GameOfLife(board2D, neighborhood);
            automatonAdapter = new AutomatonAdapter(automaton);
        }

        if (automatonTypeComboBox.getValue().equals("NaiveGrainGrow") && wrapCheckBox.isSelected()) {
            Board2D board2D = new Board2D(width, height, new CellGrain(), new CellGrain());
            coordinatesWrapper = new CoordinatesWrapper(width,height);
            Automaton automaton = new NaiveGrainGrow(board2D, neighborhood, coordinatesWrapper);
            automatonAdapter = new AutomatonAdapter(automaton);
        }
        if (automatonTypeComboBox.getValue().equals("NaiveGrainGrow") && !wrapCheckBox.isSelected()) {
            Board2D board2D = new Board2D(width, height, new CellGrain(), new CellGrain());
            Automaton automaton = new NaiveGrainGrow(board2D, neighborhood);
            automatonAdapter = new AutomatonAdapter(automaton);
        }
}

    @Override
    public void update(Observable o, Object arg) {
        drawBoard();
    }
}
