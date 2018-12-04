package pl.wieloskalowe.Controllers;

import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
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
    @FXML private TextField widthField, heightField, cellCountField, stateCountField, inclusionsCountField,
            inclusionSizeField, probability4RuleGrowField, grainBoundaryEnergyTextField, maxStepsTextField,
            fixedNumberOfStatesField, uniqueStatesTextField, cellCount2ndStepTextField, nucleationsCountTextField;
    @FXML private AnchorPane anchorPaneForCanvas;
    @FXML private MImageView imageView;
    @FXML private ComboBox energyDistributionComboBox, neighborhoodComboBox, automatonTypeComboBox, inclusionsComboBox, structureType2StepGrowComboBox;
    @FXML private CheckBox isNuclationRateCheckBox;
    private int cellsWidth, cellsHeight;
    private MImageView imageEnergyView;
    private boolean started = false;
    private Thread executorThread;
    private AutomatonAdapter automatonAdapter;
    private final FileChooser fileChooser = new FileChooser();

    @FXML public void initialize() {
        neighborhoodComboBox.setValue(neighborhoodComboBox.getItems().get(0));
        automatonTypeComboBox.setValue(automatonTypeComboBox.getItems().get(0));
        inclusionsComboBox.setValue(inclusionsComboBox.getItems().get(0));
        structureType2StepGrowComboBox.setValue(structureType2StepGrowComboBox.getItems().get(0));
//        uniqueStatesTextField.setVisible(false);
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
                    int x = random.nextInt(Integer.parseInt(widthField.getText()));
                    int y = random.nextInt(Integer.parseInt(heightField.getText()));
                    int idx = random.nextInt(Integer.parseInt(stateCountField.getText()));
                    automatonAdapter.getBoard().setCell(x, y, precomputedCells.get(idx));
                    --cellCount;
            }

            automatonAdapter.getAutomaton().syncNextBoard();
            automatonAdapter.refresh();
        }
    }

    @FXML public void populateBoardMCClicked() {
        if (uniqueStatesTextField.getText().isEmpty())
            Platform.runLater(() -> errorLabel.setText("Invalid MC states count!"));
        else if (!automatonTypeComboBox.getValue().equals("MonteCarlo") && !automatonTypeComboBox.getValue().equals("2StepMC-MC") && !automatonTypeComboBox.getValue().equals("2StepMC-NGG") && !automatonTypeComboBox.getValue().equals("RecrystalizationMC") && !automatonTypeComboBox.getValue().equals("RecrystalizationNGG"))
            Platform.runLater(() -> errorLabel.setText("Wrong automaton type - this is for MonteCarlo"));
        else {
            Platform.runLater(() -> errorLabel.setText(""));

            Random random = new Random();

            int stateCount = Integer.parseInt(uniqueStatesTextField.getText());

            List<Cell> precomputedCells = automatonAdapter.getBoard().precomputeCells(stateCount);

            for(int x = 0; x < Integer.parseInt(widthField.getText()); ++x){
                for(int y = 0; y < Integer.parseInt(heightField.getText()); ++y){
                    automatonAdapter.getBoard().setCell(x, y, precomputedCells.get(random.nextInt(stateCount)));
                }
            }

            automatonAdapter.getAutomaton().syncNextBoard();
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
            imageView.setDrawingType(automatonTypeComboBox.getValue().toString());
            imageView.setBoardParameters(cellsWidth, cellsHeight);
            imageView.setViewDimentions(anchorPaneForCanvas.getWidth(), anchorPaneForCanvas.getHeight());

            setUpAutomaton(cellsWidth, cellsHeight,false);

            automatonAdapter.addObserver(this);

            imageView.onDataRecived(automatonAdapter.getBoard());

            //TODO move to different button function or sth || make checking for letters or customize NumberTextField
            if (automatonTypeComboBox.getValue().equals("MonteCarlo")) {
                if (grainBoundaryEnergyTextField.getText().isEmpty())
                    Platform.runLater(() -> errorLabel.setText("EMPTY ENERGY - SET 0.2"));
                else {
                    Platform.runLater(() -> errorLabel.setText(""));
                    ((MonteCarlo)automatonAdapter.getAutomaton()).setGrainBoundaryEnergy(Double.parseDouble(grainBoundaryEnergyTextField.getText()));
                }
            }
            if (automatonTypeComboBox.getValue().equals("2StepMC-MC")) {
                if (grainBoundaryEnergyTextField.getText().isEmpty())
                    Platform.runLater(() -> errorLabel.setText("EMPTY ENERGY - SET 0.2"));
                else {
                    Platform.runLater(() -> errorLabel.setText(""));
                    ((TwoStep)automatonAdapter.getAutomaton()).setGrainBoundaryEnergy(Double.parseDouble(grainBoundaryEnergyTextField.getText()));
                }
            }
            if (automatonTypeComboBox.getValue().equals("2StepNGG-MC")) {
                if (grainBoundaryEnergyTextField.getText().isEmpty())
                    Platform.runLater(() -> errorLabel.setText("EMPTY ENERGY - SET 0.2"));
                else {
                    Platform.runLater(() -> errorLabel.setText(""));
                    ((TwoStep)automatonAdapter.getAutomaton()).setGrainBoundaryEnergy(Double.parseDouble(grainBoundaryEnergyTextField.getText()));
                }
            }
        }
    }

    @FXML public void showEnergyClicked() {
        Platform.runLater(() -> {
            Stage dialogStage = new Stage();
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.setMinHeight(300+50);
            dialogStage.setMinWidth(300+50);
            imageEnergyView = new MImageView();
            VBox vbox = new VBox(imageEnergyView);
            vbox.setAlignment(Pos.CENTER);
            vbox.setPadding(new Insets(5));

            imageEnergyView.setDrawingType("Energy");
            imageEnergyView.setBoardParameters(300,300);
            imageEnergyView.onDataRecived(automatonAdapter.getBoard());

            dialogStage.setScene(new Scene(vbox));
            dialogStage.show();
        });
    }

    @Deprecated
    @FXML public void setUp2StepClicked() {
        if (automatonTypeComboBox.getValue().equals("2StepMC-NGG") || automatonTypeComboBox.getValue().equals("2StepNGG-NGG")) {
            if (fixedNumberOfStatesField.getText().isEmpty() || stateCountField.getText().isEmpty() || cellCount2ndStepTextField.getText().isEmpty())
                Platform.runLater(() -> errorLabel.setText("Fill required fields"));
            else {
                Platform.runLater(() -> errorLabel.setText(""));

                if (structureType2StepGrowComboBox.getValue().equals("Substructure"))
                    ((TwoStep) automatonAdapter.getAutomaton())
                            .get2ndStepReady(Integer.parseInt(fixedNumberOfStatesField.getText()), Integer.parseInt(stateCountField.getText()),
                                    Integer.parseInt(cellCount2ndStepTextField.getText()), false);

                if (structureType2StepGrowComboBox.getValue().equals("DualPhase"))
                    ((TwoStep) automatonAdapter.getAutomaton())
                            .get2ndStepReady(Integer.parseInt(fixedNumberOfStatesField.getText()), Integer.parseInt(stateCountField.getText()),
                                    Integer.parseInt(cellCount2ndStepTextField.getText()), true);

                automatonAdapter.refresh();
            }
        }

        if (automatonTypeComboBox.getValue().equals("2StepMC-MC") || automatonTypeComboBox.getValue().equals("2StepNGG-MC")) {
            if (fixedNumberOfStatesField.getText().isEmpty() || stateCountField.getText().isEmpty() || cellCount2ndStepTextField.getText().isEmpty())
                Platform.runLater(() -> errorLabel.setText("Fill required fields"));
            else {
                Platform.runLater(() -> errorLabel.setText(""));

                if (structureType2StepGrowComboBox.getValue().equals("Substructure"))
                    ((TwoStep) automatonAdapter.getAutomaton())
                            .get2ndStepReady(Integer.parseInt(fixedNumberOfStatesField.getText()), Integer.parseInt(uniqueStatesTextField.getText()),
                                    Integer.parseInt(cellCount2ndStepTextField.getText()), false);

                if (structureType2StepGrowComboBox.getValue().equals("DualPhase"))
                    ((TwoStep) automatonAdapter.getAutomaton())
                            .get2ndStepReady(Integer.parseInt(fixedNumberOfStatesField.getText()), Integer.parseInt(uniqueStatesTextField.getText()),
                                    Integer.parseInt(cellCount2ndStepTextField.getText()), true);

                automatonAdapter.refresh();
            }
        }
    }

    @FXML public void setUpRecrystalizationButton(){
        if (automatonTypeComboBox.getValue().equals("RecrystalizationMC") || automatonTypeComboBox.getValue().equals("RecrystalizationNGG")) {
            if (false) //TODO checking values for recryst
                Platform.runLater(() -> errorLabel.setText("Fill required fields"));
            else {
                Platform.runLater(() -> errorLabel.setText(""));

                if (energyDistributionComboBox.getValue().equals("Homogenous"))
                    ((Recrystalization) automatonAdapter.getAutomaton())
                            .get2ndStepReady(Integer.parseInt(nucleationsCountTextField.getText()), isNuclationRateCheckBox.isSelected(), false);

                if (energyDistributionComboBox.getValue().equals("Heterogenous"))
                    ((Recrystalization) automatonAdapter.getAutomaton())
                            .get2ndStepReady(Integer.parseInt(nucleationsCountTextField.getText()), isNuclationRateCheckBox.isSelected(), true);

                automatonAdapter.refresh();
            }
        }
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
        if (maxStepsTextField.getText().isEmpty())
            executorThread = new SimualtionExecutor(automatonAdapter);
        else
            executorThread = new SimualtionExecutor(automatonAdapter, Integer.parseInt(maxStepsTextField.getText()));

        executorThread.start();
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

        imageView.setDrawingType(automatonTypeComboBox.getValue().toString());
        imageView.setBoardParameters(Integer.parseInt(dimensions[0]), Integer.parseInt(dimensions[1]));
        imageView.setViewDimentions(anchorPaneForCanvas.getWidth(), anchorPaneForCanvas.getHeight());
        automatonAdapter.addObserver(this);
        executorThread = new SimualtionExecutor(automatonAdapter);
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

        if (automatonTypeComboBox.getValue().equals("MonteCarlo")) {
            Board2D board2D = new Board2D(width, height, new Cell(), new Cell());
            Automaton automaton = new MonteCarlo(board2D, neighborhood);
            automatonAdapter = new AutomatonAdapter(automaton);
        }

        if (automatonTypeComboBox.getValue().equals("RecrystalizationMC")) {
            Board2D board2D = new Board2D(width, height, new Cell(), new Cell());
            Automaton automaton = new Recrystalization(board2D, neighborhood, false);
            automatonAdapter = new AutomatonAdapter(automaton);
        }

        if (automatonTypeComboBox.getValue().equals("RecrystalizationNGG")) {
            Board2D board2D = new Board2D(width, height, new Cell(), new Cell());
            Automaton automaton = new Recrystalization(board2D, neighborhood, true);
            automatonAdapter = new AutomatonAdapter(automaton);
        }

        if (automatonTypeComboBox.getValue().equals("2StepMC-MC")) {
            Board2D board2D = new Board2D(width, height, new Cell(), new Cell());
            Automaton automaton = new TwoStep(board2D, neighborhood, false, false);
            automatonAdapter = new AutomatonAdapter(automaton);
        }

        if (automatonTypeComboBox.getValue().equals("2StepNGG-MC")) {
            Board2D board2D = new Board2D(width, height, new Cell(), new Cell());
            Automaton automaton = new TwoStep(board2D, neighborhood, true, false);
            automatonAdapter = new AutomatonAdapter(automaton);
        }

        if (automatonTypeComboBox.getValue().equals("2StepMC-NGG")) {
            Board2D board2D = new Board2D(width, height, new Cell(), new Cell());
            Automaton automaton = new TwoStep(board2D, neighborhood, false, true);
            automatonAdapter = new AutomatonAdapter(automaton);
        }
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
