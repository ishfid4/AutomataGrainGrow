package pl.wieloskalowe.cell;

public class CellCoordinates {
    private int x, y;

    public CellCoordinates(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CellCoordinates that = (CellCoordinates) o;

        if (x != that.x) return false;
        return y == that.y;
    }

    @Override
    public int hashCode() {
        int result = x % 0x7FFF + (y % 0x7FFF) * 0x7FFF;
        return result;
    }

    @Override
    public String toString() {
        return "CellCoordinates{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }
}
