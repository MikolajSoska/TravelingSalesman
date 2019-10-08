package sample;

import javafx.application.Platform;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.Random;

public class Controller {
    public Canvas canvas;
    public TextField numberOfCities;
    public TextField numberOfIteration;
    public TextField populationSize;
    public TextField numberOfCrossovers;
    public Label iteration;
    public Label pathLength;

    private GraphicsContext gc;
    private ArrayList<City> cities;

    public void initialize() {
        gc = canvas.getGraphicsContext2D();
    }

    public void generateCities() {
        Random random = new Random();
        cities = new ArrayList<>();
        for(int i = 0; i < Integer.parseInt(numberOfCities.getText()); i ++){
            City city = new City(random.nextInt((int) canvas.getWidth()), random.nextInt((int) canvas.getHeight()));
            cities.add(city);
        }
        drawCities();
    }

    public void drawCities(){
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        gc.setFill(Color.BLACK);
        for(City city : cities){
            gc.fillOval(city.getX()-5, city.getY()-5, 10, 10);
        }
    }

    public void startAlgorithm() {
        if(cities == null)
            System.out.println("Nie wygenerowano miast");
        else {
            Thread ts = new Thread(new TravelingSalesman(cities, Integer.parseInt(populationSize.getText()), Integer.parseInt(numberOfIteration.getText()), Integer.parseInt(numberOfCrossovers.getText()),this));
            ts.start();
        }
    }

    public void drawPath(Path path){
        drawCities();
        for(int i = 1; i < cities.size(); i++){
            gc.strokeLine(path.getCity(i).getX(), path.getCity(i).getY(), path.getCity(i-1).getX(), path.getCity(i-1).getY());
        }
        gc.strokeLine(path.getCity(0).getX(), path.getCity(0).getY(), path.getCity(cities.size() - 1).getX(), path.getCity(cities.size() - 1).getY());
    }

    public void setIteration(Controller controller, int iteration){
        Platform.runLater(() -> controller.iteration.setText(String.valueOf(iteration)));
    }

    public void setPathLength(Controller controller, int pathLength){
        Platform.runLater(() -> controller.pathLength.setText(String.valueOf(pathLength)));
    }
}
