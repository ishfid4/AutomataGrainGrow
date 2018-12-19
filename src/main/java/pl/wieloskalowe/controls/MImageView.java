package pl.wieloskalowe.controls;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import pl.wieloskalowe.Board2D;
import pl.wieloskalowe.automaton.Recrystalization;
import pl.wieloskalowe.cell.Cell;
import pl.wieloskalowe.cell.CellDrawer;

import java.awt.image.BufferedImage;

public class MImageView extends ImageView {
    private int cellsWidth, cellsHeight;
    private double viewWidth, viewHeight;
    private String drawingType;
    private static BufferedImage buffImg;

    public void setBoardParameters (int cellsWidth, int cellsHeight) {
        setCellsWidth(cellsWidth);
        setCellsHeight(cellsHeight);
        buffImg = new BufferedImage(cellsWidth, cellsHeight, BufferedImage.TYPE_INT_RGB);
    }

    public void setViewDimentions(double viewWidth, double viewHeight) {
        this.viewHeight = viewHeight;
        this.viewWidth = viewWidth;
    }

    public static BufferedImage getBuffImg() {
        return buffImg;
    }

    private void setCellsWidth(int cellsWidth) {
        this.cellsWidth = cellsWidth;
    }

    private  void setCellsHeight(int cellsHeight) {
        this.cellsHeight = cellsHeight;
    }

    public void setDrawingType(String drawingType) {
        this.drawingType = drawingType;
    }

    private final CellDrawer redrawTask = new CellDrawer(this) {
        @Override
        protected void redraw(ImageView imageView, Board2D board2D, String drawingType,
                              int cellsWidth, int cellsHeight) {
            setCellsHeight(cellsHeight);
            setCellsWidth(cellsWidth);
            Color color;
            if (drawingType.equals("Energy")){
                for (int x = 0; x < cellsHeight; x++) {
                    for (int y = 0; y < cellsWidth; y++) {
                        Cell cell = board2D.getCell(x, y);
                        Double energy = board2D.getCellEnergy(x, y);
                        if (energy >= 0.0 && energy <= 1.0)
                            color = board2D.getEnergyColor().get(0);
                        else if (energy > 1.0 && energy <= 2.0)
                            color = board2D.getEnergyColor().get(1);
                        else if (energy > 2.0 && energy <= 3.0)
                            color = board2D.getEnergyColor().get(2);
                        else if (energy > 3.0 && energy <= 4.0)
                            color = board2D.getEnergyColor().get(3);
                        else if (energy > 4.0 && energy <= 5.0)
                            color = board2D.getEnergyColor().get(4);
                        else if (energy > 5.0 && energy <= 6.0)
                            color = board2D.getEnergyColor().get(5);
                        else if (energy > 6.0)
                            color = board2D.getEnergyColor().get(6);
                        else
                            color = cell.getColor();

                        drawGrainCell(buffImg, x, y, cell.isAlive(), color, cell.isOnEdge());
                    }
                }
            } else {
                for (int x = 0; x < cellsHeight; x++) {
                    for (int y = 0; y < cellsWidth; y++) {
                        Cell cell = board2D.getCell(x, y);
                        drawGrainCell(buffImg, x, y, cell.isAlive(), cell.getColor(), cell.isOnEdge());
                    }
                }
            }
            imageView.setImage(SwingFXUtils.toFXImage(buffImg, null));
            imageView.setFitHeight(viewHeight);
            imageView.setPreserveRatio(true);
            imageView.setCache(true);
        }
    };

    private void drawGrainCell(BufferedImage buffImg,
                               int x, int y, boolean alive, Color color, boolean onEdge) {
        if (alive) {
            int rgb = (int)(color.getRed() * 255);
            rgb = (rgb << 8) + (int)(color.getGreen() * 255);
            rgb = (rgb << 8) + (int) (color.getBlue() * 255);
            buffImg.setRGB(x, y, rgb);
        } else
            buffImg.setRGB(x, y, 0xFFFFFF);

        //TODO: need to be removed
//        if(onEdge){
//            graphicsContext.setFill(Color.color(1,1,1));
//            graphicsContext.fillRect(x * cellWidth, y * cellHeight, cellWidth, cellHeight);
//        }
    }

    public void onDataRecived(Board2D board2D) {
        redrawTask.setUpData(cellsWidth,cellsHeight, drawingType);
        redrawTask.requestRedraw(board2D);
    }

}
