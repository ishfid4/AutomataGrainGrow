package pl.wieloskalowe.controls;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import pl.wieloskalowe.Board2D;
import pl.wieloskalowe.cell.Cell;
import pl.wieloskalowe.cell.CellDrawer;

import java.awt.image.BufferedImage;

public class MImageView extends ImageView {
    private int cellsWidth, cellsHeight;
    private double viewWidth, viewHeight;
    private String automatonType;
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

    public void setAutomatonType(String automatonType) {
        this.automatonType = automatonType;
    }

    private final CellDrawer redrawTask = new CellDrawer(this) {
        @Override
        protected void redraw(ImageView imageView, Board2D board2D, String automatonType,
                              int cellsWidth, int cellsHeight) {
            setCellsHeight(cellsHeight);
            setCellsWidth(cellsWidth);

            if (automatonType.equals("NaiveGrainGrow") || automatonType.equals("FourRulesGrainGrow")) {
                for (int x = 0; x < cellsWidth; x++){
                    for (int y = 0; y < cellsHeight; y++) {
                        Cell cell =  board2D.getCell(x,y);
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
        redrawTask.setUpData(cellsWidth,cellsHeight,automatonType);
        redrawTask.requestRedraw(board2D);
    }

}
