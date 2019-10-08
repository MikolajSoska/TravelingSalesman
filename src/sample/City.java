package sample;

public class City {
    private int x;
    private int y;

    City(int x, int y){
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public double computeDistance(City city){
        return Math.sqrt((x-city.getX())*(x-city.getX()) + (y-city.getY())*(y-city.getY()));
    }

    public boolean isEqual(City city){
        return x == city.getX() && y == city.getY();
    }
}
