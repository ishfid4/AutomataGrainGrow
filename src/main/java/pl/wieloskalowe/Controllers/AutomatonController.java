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
import javafx.util.Pair;
import pl.wieloskalowe.*;
import pl.wieloskalowe.automaton.*;
import pl.wieloskalowe.cell.Cell;
import pl.wieloskalowe.cell.CellCoordinates;
import pl.wieloskalowe.controls.MImageView;
import pl.wieloskalowe.neighborhoods.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;
import static java.lang.Math.sqrt;

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
            String IMAGE_FILE = "Board.bmp"; //todo choose save place
            try {
                ImageIO.write(imageView.getBuffImg(), "BMP", new File(IMAGE_FILE));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML public void saveToCSVClicked() throws IOException {
        File file = new File("Board.csv"); //todo choose save place
        file.createNewFile();
        FileWriter writer = new FileWriter(file);

        Board2D board2D = automatonAdapter.getBoard();
        writer.write(cellsWidth + " " + cellsHeight + "\n");
        for (CellCoordinates cellCoordinates : board2D.getAllCoordinates()) {
            Cell cell = board2D.getCell(cellCoordinates);
            writer.write(cellCoordinates.getX() + " " + cellCoordinates.getY() + " " + cell.isAlive() + " " + cell.getColor() + "\n");
        }

        writer.flush();
        writer.close();
    }

    @FXML public void importFromCSVClicekd() throws IOException { //todo choose import file
        File file = new File("Board.csv");
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
            automatonAdapter.setCellState(new CellCoordinates(Integer.parseInt(cell[0]),Integer.parseInt(cell[1])),
                    Color.web(cell[3]));
        }
    }

    @FXML public void importFromBMPClicekd() {
        Image image = new Image("Board.bmp"); //Todo chose import file
        imageView.setViewDimentions(anchorPaneForCanvas.getWidth(), anchorPaneForCanvas.getHeight());
        imageView.setImage(image);
        imageView.setFitHeight(anchorPaneForCanvas.getHeight());
    }

    @FXML public void addInclusionsPClicekd() {
        if(inclusionsCountField.getText().isEmpty())
            Platform.runLater(() -> errorLabel.setText("Invalid inclusion count!"));
        else if (inclusionSizeField.getText().isEmpty())
            Platform.runLater(() -> errorLabel.setText("Invalid inclusion size!"));
        else {
            Platform.runLater(() -> errorLabel.setText(""));

            ArrayList<CellCoordinates> cellCoordinates2 = new ArrayList<>();
            boolean boardFilled = false;
            for (CellCoordinates cc: automatonAdapter.getBoard().getAllCoordinates()) {
                Neighborhood neighborhood = new VonNeumanNeighborhood(1);
                Set<CellCoordinates> neighbours = neighborhood.cellNeighbors(cc);
                Color cellColor = Color.color(1, 1, 1);

                Map<Color, Integer> listOfColors = new HashMap<>();
                int maxCount = 0;

                for (CellCoordinates cellCoordinates : neighbours) {
                    cellColor = automatonAdapter.getBoard().getCell(cellCoordinates).getColor();
                     if (!cellColor.equals(Color.color(1, 1, 1))) {
                        if (listOfColors.containsKey(cellColor)) {
                            int tmp = listOfColors.get(cellColor);
                            tmp++;
                            listOfColors.replace(cellColor, tmp);
                        } else {
                            listOfColors.put(cellColor, 1);
                        }
                    }
                }

                for (Color col : listOfColors.keySet()) {
                    if (listOfColors.get(col) >= maxCount) {
                        maxCount = listOfColors.get(col);
                    }
                }

                if (listOfColors.size() > 1) {
                    cellCoordinates2.add(cc);
                    automatonAdapter.getBoard().getCell(cc).setOnEdge(true);
                    boardFilled = true;
                }
            }

            Random random = new Random();
            int radius = Integer.parseInt(inclusionSizeField.getText());
            int inclusionCount = Integer.parseInt(inclusionsCountField.getText());

            for (int i = 0; i < inclusionCount; i++)
            {
                CellCoordinates cellCoordinates1;
                if (boardFilled)
                {
                    int idx = random.nextInt(cellCoordinates2.size());
                    cellCoordinates1 = cellCoordinates2.get(idx);
                }else {
                    cellCoordinates1 = new CellCoordinates(random.nextInt(Integer.parseInt(widthField.getText())),
                            random.nextInt(Integer.parseInt(heightField.getText())));
                }

                if (inclusionsComboBox.getValue().equals("Square"))
                {
                    Neighborhood neighborhood = new MooreNeighborhood(radius);
                    Set<CellCoordinates> neighbours = neighborhood.cellNeighbors(cellCoordinates1);
                    for (CellCoordinates cc: neighbours) {

                        createInclusion(cc.getX(),cc.getY());
                    }
                }

                if (inclusionsComboBox.getValue().equals("Circular")) //Todo circle not square
                {
                    Neighborhood neighborhood = new VonNeumanNeighborhood(radius);
                    Set<CellCoordinates> neighbours = neighborhood.cellNeighbors(cellCoordinates1);
                    for (CellCoordinates cc: neighbours) {
                        createInclusion(cc.getX(),cc.getY());
                    }
                }

                createInclusion(cellCoordinates1.getX(), cellCoordinates1.getY());
            }
        }
    }

    private void createInclusion(double x, double y){
        if ((x >= 0 && x < Integer.parseInt(widthField.getText())) && (y >= 0 && y < Integer.parseInt(heightField.getText()))){
            CellCoordinates cellCoordinates = new CellCoordinates((int)x,(int)y);

            automatonAdapter.setCellState(cellCoordinates, true);
        }
    }

    private void changeCellState(double x, double y) {
        CellCoordinates cellCoordinates = new CellCoordinates((int)x,(int)y);

        automatonAdapter.changeCellState(cellCoordinates);
    }

    private void setGrainState(double x, double y, Color color) {
        CellCoordinates cellCoordinates = new CellCoordinates((int)x, (int)y);

        automatonAdapter.setCellState(cellCoordinates, color);
    }

    private void setUpAutomaton(int width, int height, boolean afternaiveGrow){
        Neighborhood neighborhood = new MooreNeighborhood(1);

        if (automatonTypeComboBox.getValue().equals("NaiveGrainGrow")) {
            Board2D board2D = new Board2D(width, height, new Cell(), new Cell());
            Automaton automaton = new NaiveGrainGrow(board2D, neighborhood);
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
}
