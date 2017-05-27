package pl.wieloskalowe.Controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.util.Pair;
import pl.wieloskalowe.*;
import pl.wieloskalowe.automaton.*;
import pl.wieloskalowe.cell.CellBinary;
import pl.wieloskalowe.cell.CellCoordinates;
import pl.wieloskalowe.cell.CellGrain;
import pl.wieloskalowe.controls.MCanvas;
import pl.wieloskalowe.neighborhoods.*;

import java.util.*;

import static java.lang.Math.sqrt;
import static java.lang.Math.toRadians;


/**
 * Created by ishfi on 03.05.2017.
 */
public class AutomatonController implements Observer{
    @FXML private Label errorLabel;
    @FXML private TextField widthField, heightField, radiusField, generateRadiusField, cellCountField, stateCountField;
    @FXML private AnchorPane anchorPaneForCanvas;
    @FXML private MCanvas canvas;
    @FXML private ComboBox neighborhoodComboBox, automatonTypeComboBox, generationComboBox;
    @FXML private CheckBox wrapCheckBox, afterNaiveGrow;
    private double cellWidth, cellHeight;
    private boolean started = false;
    private Ticker ticker;
    private AutomatonAdapter automatonAdapter;

    @FXML public void initialize() {
        neighborhoodComboBox.setValue(neighborhoodComboBox.getItems().get(0));
        automatonTypeComboBox.setValue(automatonTypeComboBox.getItems().get(0));
        generationComboBox.setValue(generationComboBox.getItems().get(0));

        canvas.addEventHandler(MouseEvent.MOUSE_CLICKED,
                t -> {
                    if (t.getClickCount() == 1) {
                        changeCellState(t.getX(), t.getY());
                    }
                });
    }

    @FXML public void generateClicked() {
        if (generationComboBox.getValue().equals("Randomly populate board")){
            if (stateCountField.getText().isEmpty())
                Platform.runLater(() -> errorLabel.setText("Invalid state count!"));
            else {
                Platform.runLater(() -> errorLabel.setText(""));
                List<Color> colorList = new ArrayList<>();
                Random random = new Random();

                for (int i = 0; i < Integer.parseInt(stateCountField.getText()); i++){
                    boolean wrongColor = false;
                    Color color = Color.color(random.nextDouble(), random.nextDouble(), random.nextDouble());
                    if (color.equals(Color.color(1,1,1)))
                        wrongColor = true;

                    for (Color aColor : colorList) {
                        if (color.equals(aColor))
                            wrongColor = true;
                    }

                    if (wrongColor)
                        i--;
                    else
                        colorList.add(color);
                }

                int x = Integer.parseInt(widthField.getText());
                int y = Integer.parseInt(heightField.getText());

                for (int i = 0; i < x; i++) {
                    for (int j = 0; j < y; j++) {
                        setGrainState(i, j,
                                colorList.get(random.nextInt(colorList.size())));
                    }
                }
            }
        }

        if (cellCountField.getText().isEmpty())
            Platform.runLater(() -> errorLabel.setText("Invalid cell count!"));
        else {
            Platform.runLater(() -> errorLabel.setText(""));

            Random random = new Random();

            if (generationComboBox.getValue().equals("Random")) {
                for (int i = 0; i < Integer.parseInt(cellCountField.getText()); i++) {
                    changeCellState(random.nextInt(Integer.parseInt(widthField.getText())),
                            random.nextInt(Integer.parseInt(heightField.getText())));
                }
            }

            // TODO: Probably not work as expected should be circe radius
            if (generationComboBox.getValue().equals("Random with radius")){
                int radius = Integer.parseInt(generateRadiusField.getText());
                boolean wrongRange = false;
                List<Pair<Integer,Integer>> checkList = new ArrayList<>();
                List<Pair<Integer,Integer>> cordsToChangeState = new ArrayList<>();

                for (int i = 0; i < Integer.parseInt(cellCountField.getText()); i++) {
                    int tX = random.nextInt(Integer.parseInt(widthField.getText()));
                    int tY = random.nextInt(Integer.parseInt(heightField.getText()));

                    for (Pair<Integer,Integer> p: checkList) {
                        if (tX == p.getKey() && tY == p.getValue())
                            wrongRange = true;
                    }
                    for (Pair<Integer,Integer> p: cordsToChangeState) {
                        if (tX == p.getKey() && tY == p.getValue())
                            wrongRange = true;
                    }

                    if (!wrongRange) {

                        for (int x = 0; x <= radius; x++) {
                            int y = -radius + x;

                            for (; y <= radius - x; y++) {
                                if (tX == x && tY == y) continue;

                                checkList.add(new Pair<>(tX + x, tY + y));
                                if (x != 0) {
                                    checkList.add(new Pair<>(tX - x, tY + y));
                                }
                            }
                        }

                        cordsToChangeState.add(new Pair<>(tX,tY));
                    } else {
                        i--;
                        wrongRange = false;
                    }
                }

                for (Pair<Integer,Integer> p: cordsToChangeState) {
                    changeCellState(p.getKey(), p.getValue());
                }
            }

            //TODO: too large offset on right and bottom edge
            if (generationComboBox.getValue().equals("Equally spread")){
                int sqCellCount = (int) Math.floor(sqrt(Double.parseDouble(cellCountField.getText())));

                int x = Integer.parseInt(widthField.getText()) / sqCellCount;
                int y = Integer.parseInt(heightField.getText()) / sqCellCount;

                for (int i = 0; i < sqCellCount; i++) {
                    for (int j = 0; j < sqCellCount; j++) {
                        changeCellState(x * i, y * j);
                    }
                }
            }
        }
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

            canvas.setWidth(anchorPaneForCanvas.getWidth());
            canvas.setHeight(anchorPaneForCanvas.getHeight());

            cellHeight = anchorPaneForCanvas.getHeight() / Integer.parseInt(heightField.getText());
            cellWidth = anchorPaneForCanvas.getWidth() / Integer.parseInt(widthField.getText());

            canvas.setAutomatonType(automatonTypeComboBox.getValue().toString());
            canvas.setCellHeight(cellHeight);
            canvas.setCellWidth(cellWidth);

            setUpAutomaton(Integer.parseInt(widthField.getText()), Integer.parseInt(heightField.getText()),
                    Integer.parseInt(radiusField.getText()),afterNaiveGrow.isSelected());

            automatonAdapter.addObserver(this);

            ticker = new Ticker(automatonAdapter);

            canvas.onDataRecived(automatonAdapter.getBoard());
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
        CellCoordinates cellCoordinates = new CellCoordinates((int)x,(int)y);

        automatonAdapter.changeCellState(cellCoordinates);
    }

    private void setGrainState(double x, double y, Color color) {
        CellCoordinates cellCoordinates = new CellCoordinates((int)x, (int)y);

        automatonAdapter.setCellState(cellCoordinates, color);
    }

    private void setUpAutomaton(int width, int height, int radius, boolean afternaiveGrow){
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

        if (automatonTypeComboBox.getValue().equals("Recrystalization") && wrapCheckBox.isSelected()) {
            Board2D board2D = new Board2D(width, height, new CellGrain(), new CellGrain());
            coordinatesWrapper = new CoordinatesWrapper(width,height);
            Automaton automaton = new Recrystallization(board2D, neighborhood, coordinatesWrapper);
            automatonAdapter = new AutomatonAdapter(automaton);
        }
        if (automatonTypeComboBox.getValue().equals("Recrystalization") && !wrapCheckBox.isSelected()) {
            Board2D board2D = new Board2D(width, height, new CellGrain(), new CellGrain());
            Automaton automaton = new Recrystallization(board2D, neighborhood);
            automatonAdapter = new AutomatonAdapter(automaton);
        }

        if (automatonTypeComboBox.getValue().equals("MonteCarlo") && wrapCheckBox.isSelected()) {
            Board2D board2D = new Board2D(width, height, new CellGrain(), new CellGrain());
            coordinatesWrapper = new CoordinatesWrapper(width,height);
            Automaton automaton = new MonteCarlo(board2D, neighborhood, afternaiveGrow, coordinatesWrapper);
            automatonAdapter = new AutomatonAdapter(automaton);
        }
        if (automatonTypeComboBox.getValue().equals("MonteCarlo") && !wrapCheckBox.isSelected()) {
            Board2D board2D = new Board2D(width, height, new CellGrain(), new CellGrain());
            Automaton automaton = new MonteCarlo(board2D, neighborhood, afternaiveGrow);
            automatonAdapter = new AutomatonAdapter(automaton);
        }
}

    @Override
    public synchronized void update(Observable o, Object arg) {
        canvas.onDataRecived(automatonAdapter.getBoard());
    }
}
