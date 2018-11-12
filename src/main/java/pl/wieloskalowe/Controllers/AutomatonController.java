package pl.wieloskalowe.Controllers;

import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import pl.wieloskalowe.Board2D;
import pl.wieloskalowe.automaton.Automaton;
import pl.wieloskalowe.automaton.AutomatonAdapter;
import pl.wieloskalowe.automaton.FourRulesGrainGrow;
import pl.wieloskalowe.automaton.NaiveGrainGrow;
import pl.wieloskalowe.cell.Cell;
import pl.wieloskalowe.controls.MImageView;
import pl.wieloskalowe.neighborhoods.CircleNeighborhood;
import pl.wieloskalowe.neighborhoods.MooreNeighborhood;
import pl.wieloskalowe.neighborhoods.Neighborhood;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class AutomatonController implements Observer{
    @FXML private Label errorLabel;
    @FXML private TextField widthField, heightField, generateRadiusField, cellCountField, inclusionsCountField, inclusionSizeField;
    @FXML private AnchorPane anchorPaneForCanvas;
    @FXML private MImageView imageView;
    @FXML private ComboBox neighborhoodComboBox, automatonTypeComboBox, generationComboBox, inclusionsComboBox;
    @FXML private CheckBox afterNaiveGrow;
    private int cellsWidth, cellsHeight;
    private boolean started = false;
    private Ticker ticker;
    private AutomatonAdapter automatonAdapter;
    private final FileChooser fileChooser = new FileChooser();

    @FXML public void initialize() {
        neighborhoodComboBox.setValue(neighborhoodComboBox.getItems().get(0));
        automatonTypeComboBox.setValue(automatonTypeComboBox.getItems().get(0));
        generationComboBox.setValue(generationComboBox.getItems().get(0));
        inclusionsComboBox.setValue(inclusionsComboBox.getItems().get(0));
    }

    @FXML public void generateClicked() {
        if (cellCountField.getText().isEmpty())
            Platform.runLater(() -> errorLabel.setText("Invalid cell count!"));
        else {
            Platform.runLater(() -> errorLabel.setText(""));

            Random random = new Random();

            if (generationComboBox.getValue().equals("Random")) {
                int stateCount = Integer.parseInt(cellCountField.getText());

                List<Cell> precomputedCells = automatonAdapter.getBoard().precomputeCells(stateCount);

                for(int i = 0; i < stateCount; ++i) {
                    int x = random.nextInt(Integer.parseInt(widthField.getText()));
                    int y = random.nextInt(Integer.parseInt(heightField.getText()));
                    automatonAdapter.getBoard().setCell(x, y, precomputedCells.get(i));
                    automatonAdapter.getAutomaton().syncNextBoard();
                }

                automatonAdapter.refresh();
            }

// DOES NOT WORK!!!!!!!!!!!
//            // TODO: Probably not work as expected should be circe radius
//            if (generationComboBox.getValue().equals("Random with radius")){
//                int radius = Integer.parseInt(generateRadiusField.getText());
//                boolean wrongRange = false;
//                List<Pair<Integer,Integer>> checkList = new ArrayList<>();
//                List<Pair<Integer,Integer>> cordsToChangeState = new ArrayList<>();
//
//                for (int i = 0; i < Integer.parseInt(cellCountField.getText()); i++) {
//                    int tX = random.nextInt(Integer.parseInt(widthField.getText()));
//                    int tY = random.nextInt(Integer.parseInt(heightField.getText()));
//
//                    for (Pair<Integer,Integer> p: checkList) {
//                        if (tX == p.getKey() && tY == p.getValue())
//                            wrongRange = true;
//                    }
//                    for (Pair<Integer,Integer> p: cordsToChangeState) {
//                        if (tX == p.getKey() && tY == p.getValue())
//                            wrongRange = true;
//                    }
//
//                    if (!wrongRange) {
//
//                        for (int x = 0; x <= radius; x++) {
//                            int y = -radius + x;
//
//                            for (; y <= radius - x; y++) {
//                                if (tX == x && tY == y) continue;
//
//                                checkList.add(new Pair<>(tX + x, tY + y));
//                                if (x != 0) {
//                                    checkList.add(new Pair<>(tX - x, tY + y));
//                                }
//                            }
//                        }
//
//                        cordsToChangeState.add(new Pair<>(tX,tY));
//                    } else {
//                        i--;
//                        wrongRange = false;
//                    }
//                }
//
//                for (Pair<Integer,Integer> p: cordsToChangeState) {
////                    changeCellState(p.getKey(), p.getValue());
//                }
//            }
//
//            //TODO: too large offset on right and b ottom edge
//            if (generationComboBox.getValue().equals("Equally spread")){
//                int sqCellCount = (int) Math.floor(sqrt(Double.parseDouble(cellCountField.getText())));
//
//                int x = Integer.parseInt(widthField.getText()) / sqCellCount;
//                int y = Integer.parseInt(heightField.getText()) / sqCellCount;
//
//                for (int i = 0; i < sqCellCount; i++) {
//                    for (int j = 0; j < sqCellCount; j++) {
////                        changeCellState(x * i, y * j);
//                    }
//                }
//            }
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

            setUpAutomaton(cellsWidth, cellsHeight,afterNaiveGrow.isSelected());

            automatonAdapter.addObserver(this);

            ticker = new Ticker(automatonAdapter);

            imageView.onDataRecived(automatonAdapter.getBoard());
        }
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

        if (automatonTypeComboBox.getValue().equals("NaiveGrainGrow")) {
            Board2D board2D = new Board2D(width, height, new Cell(), new Cell());
            Automaton automaton = new NaiveGrainGrow(board2D, neighborhood);
            automatonAdapter = new AutomatonAdapter(automaton);
        }

        if (automatonTypeComboBox.getValue().equals("FourRulesGrainGrow")) {
            Board2D board2D = new Board2D(width, height, new Cell(), new Cell());
            Automaton automaton = new FourRulesGrainGrow(board2D, neighborhood);
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
