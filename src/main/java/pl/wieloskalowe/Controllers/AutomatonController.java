package pl.wieloskalowe.Controllers;

import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import pl.wieloskalowe.Board2D;
import pl.wieloskalowe.automaton.*;
import pl.wieloskalowe.cell.Cell;
import pl.wieloskalowe.controls.MImageView;
import pl.wieloskalowe.neighborhoods.*;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class AutomatonController implements Observer{
    @FXML private Label errorLabel;
    @FXML private TextField widthField, heightField, cellCountField, stateCountField, inclusionsCountField, inclusionSizeField, probability4RuleGrowField;
    @FXML private AnchorPane anchorPaneForCanvas;
    @FXML private MImageView imageView;
    @FXML private ComboBox neighborhoodComboBox, automatonTypeComboBox, inclusionsComboBox;
    private int cellsWidth, cellsHeight;
    private boolean started = false;
    private Ticker ticker;
    private AutomatonAdapter automatonAdapter;
    private final FileChooser fileChooser = new FileChooser();

    @FXML public void initialize() {
        neighborhoodComboBox.setValue(neighborhoodComboBox.getItems().get(0));
        automatonTypeComboBox.setValue(automatonTypeComboBox.getItems().get(0));
        inclusionsComboBox.setValue(inclusionsComboBox.getItems().get(0));
    }

    @FXML public void generateClicked() {
        if (cellCountField.getText().isEmpty() || stateCountField.getText().isEmpty())
            Platform.runLater(() -> errorLabel.setText("Invalid cell count!"));
        else {
            Platform.runLater(() -> errorLabel.setText(""));

            Random random = new Random();

                int cellCount = Integer.parseInt(cellCountField.getText());
                int stateCount = Integer.parseInt(stateCountField.getText());

                List<Cell> precomputedCells = automatonAdapter.getBoard().precomputeCells(stateCount);

                while(cellCount > 0) {
                    for (int i = 0; i < stateCount; ++i) {
                        int x = random.nextInt(Integer.parseInt(widthField.getText()));
                        int y = random.nextInt(Integer.parseInt(heightField.getText()));
                        automatonAdapter.getBoard().setCell(x, y, precomputedCells.get(i));
                        automatonAdapter.getAutomaton().syncNextBoard();
                        --cellCount;
                    }
                }

                automatonAdapter.refresh();
        }
    }

    @FXML public void setClicked(){
        if(widthField.getText().isEmpty())
            Platform.runLater(() -> errorLabel.setText("Invalid width!"));
        else if (heightField.getText().isEmpty())
            Platform.runLater(() -> errorLabel.setText("Invalid height!"));
        else {
            Platform.runLater(() -> errorLabel.setText(""));
            cellsHeight = Integer.parseInt(heightField.getText());
            cellsWidth = Integer.parseInt(widthField.getText());
            imageView.setAutomatonType(automatonTypeComboBox.getValue().toString());
            imageView.setBoardParameters(cellsWidth, cellsHeight);
            imageView.setViewDimentions(anchorPaneForCanvas.getWidth(), anchorPaneForCanvas.getHeight());

            setUpAutomaton(cellsWidth, cellsHeight,false);

            automatonAdapter.addObserver(this);

            ticker = new Ticker(automatonAdapter);

            imageView.onDataRecived(automatonAdapter.getBoard());
        }
    }

    @FXML public void setUp2StepClicked() {
        ((TwoStepNaiveGrainGrow)automatonAdapter.getAutomaton())
                .getReadyToFuck(3,  Integer.parseInt(stateCountField.getText()),
                        Integer.parseInt(cellCountField.getText()));
        automatonAdapter.refresh();
    }

    @FXML public void showCellsBoundariesClicked() {
        if (automatonAdapter.getBoard().isFilled())
        {
            List<Integer> cellsOnEdge = automatonAdapter.getBoard().edgeCells(automatonAdapter.getAutomaton()
                    .getNeighborhood());
            //Redrow

        }
//        automatonAdapter.refresh();
    }

    @FXML public void startClicked() {
        this.started = !this.started;
        if (started)
            ticker.start();
    }

    @FXML public void iterateClicked() {
        automatonAdapter.nextAutomatonState();
    }

    @FXML public void saveToBMPClicked(){
        if (imageView.getImage() == null) {
            Platform.runLater(() -> errorLabel.setText("No image created!"));
        } else {
            configureFileChooser(fileChooser);
            File file = fileChooser.showSaveDialog(null);
            try {
                ImageIO.write(imageView.getBuffImg(), "BMP", file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML public void saveToCSVClicked() throws IOException {
        configureFileChooser(fileChooser);
        File file = fileChooser.showSaveDialog(null);
        file.createNewFile();
        FileWriter writer = new FileWriter(file);

        Board2D board2D = automatonAdapter.getBoard();
        writer.write(cellsWidth + " " + cellsHeight + "\n");
        for (int x = 0; x < board2D.getWidth(); x++) {
            for (int y = 0; y < board2D.getHeight(); y++) {
                Cell cell = board2D.getCell(x, y);
                writer.write(x + " " + y + " " + cell.isAlive() + " " + cell.getColor() + "\n");
            }
        }

        writer.flush();
        writer.close();
    }

    @FXML public void importFromCSVClicekd() throws IOException {
        configureFileChooser(fileChooser);
        File file = fileChooser.showOpenDialog(null);
        Scanner scanner = new Scanner(file);
        String[] dimensions = scanner.nextLine().split(" ");
        setUpAutomaton(Integer.parseInt(dimensions[0]),Integer.parseInt(dimensions[1]), false);

        imageView.setAutomatonType(automatonTypeComboBox.getValue().toString());
        imageView.setBoardParameters(Integer.parseInt(dimensions[0]), Integer.parseInt(dimensions[1]));
        imageView.setViewDimentions(anchorPaneForCanvas.getWidth(), anchorPaneForCanvas.getHeight());
        automatonAdapter.addObserver(this);
        ticker = new Ticker(automatonAdapter);
        imageView.onDataRecived(automatonAdapter.getBoard());

        while (scanner.hasNext()) {
            String[] cell = scanner.nextLine().split(" ");
            automatonAdapter.importCell(Integer.parseInt(cell[0]),Integer.parseInt(cell[1]),
                    Color.web(cell[3]));
        }
    }

    @FXML public void importFromBMPClicekd() {
        configureFileChooser(fileChooser);
        File file = fileChooser.showOpenDialog(null);
        Image image = null;
        try {
            image = SwingFXUtils.toFXImage(ImageIO.read(file), null);
        } catch (IOException e) {
            e.printStackTrace();
        }
        imageView.setViewDimentions(anchorPaneForCanvas.getWidth(), anchorPaneForCanvas.getHeight());
        imageView.setImage(image);
        imageView.setFitHeight(anchorPaneForCanvas.getHeight());
    }

    @FXML public void addInclusionsClicekd() {
        if(inclusionsCountField.getText().isEmpty())
            Platform.runLater(() -> errorLabel.setText("Invalid inclusion count!"));
        else if (inclusionSizeField.getText().isEmpty())
            Platform.runLater(() -> errorLabel.setText("Invalid inclusion size!"));
        else {
            Platform.runLater(() -> errorLabel.setText(""));

            int radius = Integer.parseInt(inclusionSizeField.getText());
            int inclusionCount = Integer.parseInt(inclusionsCountField.getText());
            List<Integer> inclusionsCoordsToSet = new ArrayList<>();

            if (automatonAdapter.getBoard().isFilled())
            {
                List<Integer> cellsOnEdge = automatonAdapter.getBoard().edgeCells(automatonAdapter.getAutomaton()
                        .getNeighborhood());
                Random random = new Random();
                for(int i = 0; i < inclusionCount; ++i) {
                    int randomCellOnEdgePos = cellsOnEdge.get(random.nextInt(cellsOnEdge.size()));
                    inclusionsCoordsToSet.add(randomCellOnEdgePos);
                }
            }

            if (inclusionsCoordsToSet.isEmpty()) {
                Random random = new Random();
                for(int i = 0; i < inclusionCount; ++i) {
                    inclusionsCoordsToSet.add(random.nextInt(cellsWidth * cellsHeight));
                }
            }

            if (inclusionsComboBox.getValue().equals("Square"))
                automatonAdapter.getBoard().setInclusions(inclusionsCoordsToSet, new MooreNeighborhood(radius));

            if (inclusionsComboBox.getValue().equals("Circular"))
                automatonAdapter.getBoard().setInclusions(inclusionsCoordsToSet, new CircleNeighborhood(radius));

            automatonAdapter.refresh();
        }
    }

    private void setUpAutomaton(int width, int height, boolean afternaiveGrow){
        Neighborhood neighborhood = new MooreNeighborhood(1);

        if (neighborhoodComboBox.getValue().equals("Moore"))
            neighborhood = new MooreNeighborhood(1);
        if (neighborhoodComboBox.getValue().equals("VonNeuman"))
            neighborhood = new VonNeumanNeighborhood(1);
        if (neighborhoodComboBox.getValue().equals("CornersOfMoore"))
            neighborhood = new CornersOfMooreNeighborhood(1);
        if (neighborhoodComboBox.getValue().equals("Circular"))
            neighborhood = new CircleNeighborhood(6);

        if (automatonTypeComboBox.getValue().equals("NaiveGrainGrow")) {
            Board2D board2D = new Board2D(width, height, new Cell(), new Cell());
            Automaton automaton = new NaiveGrainGrow(board2D, neighborhood);
            automatonAdapter = new AutomatonAdapter(automaton);
        }

        if (automatonTypeComboBox.getValue().equals("FourRulesGrainGrow")) {
            Board2D board2D = new Board2D(width, height, new Cell(), new Cell());
            Automaton automaton = new FourRulesGrainGrow(board2D, new MooreNeighborhood(1));
            automatonAdapter = new AutomatonAdapter(automaton);
            if (!probability4RuleGrowField.getText().isEmpty())
                ((FourRulesGrainGrow) automaton).setPobability(Integer.parseInt(probability4RuleGrowField.getText()));
            else
                Platform.runLater(() -> errorLabel.setText("EMPTY PROBABILITY - SET 10%"));
        }

        if (automatonTypeComboBox.getValue().equals("2StepNaiveGrainGrow")) {
            Board2D board2D = new Board2D(width, height, new Cell(), new Cell());
            Automaton automaton = new TwoStepNaiveGrainGrow(board2D, neighborhood);
            automatonAdapter = new AutomatonAdapter(automaton);
        }

//        if (automatonTypeComboBox.getValue().equals("MonteCarlo")) {
//            Board2D board2D = new Board2D(width, height, new CellGrain(), new CellGrain());
//            Automaton automaton = new MonteCarlo(board2D, neighborhood, afternaiveGrow);
//            automatonAdapter = new AutomatonAdapter(automaton);
//        }
}

    @Override
    public synchronized void update(Observable o, Object arg) {
        imageView.onDataRecived(automatonAdapter.getBoard());
    }

    private static void configureFileChooser(
            final FileChooser fileChooser) {
        fileChooser.setTitle("View Files");
        fileChooser.setInitialDirectory(
                new File(System.getProperty("user.home"))
        );
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("All Filrs", "*.*"),
                new FileChooser.ExtensionFilter("CSV", "*.csv"),
                new FileChooser.ExtensionFilter("BMP", "*.bmp")
        );
    }
}
