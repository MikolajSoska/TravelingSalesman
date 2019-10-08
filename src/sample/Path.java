package sample;

import java.util.ArrayList;
import java.util.Collections;

public class Path {
    private City path[];
    private double length;

    Path(ArrayList<City> cities){
        path = new City[cities.size()];
        Collections.shuffle(cities);

        path[0] = cities.get(0);
        for(int i = 1; i < cities.size(); i++){
            path[i] = cities.get(i);
            length += path[i].computeDistance(path[i-1]);
        }
        length += path[0].computeDistance(path[path.length - 1]);
    }

    Path(City path[]){
        this.path = path;
        for(int i = 1; i < path.length; i++)
            length += path[i].computeDistance(path[i - 1]);
        length += path[0].computeDistance(path[path.length - 1]);
    }

    public double getLength() {
        return length;
    }

    public City getCity(int position){
        return path[position];
    }
}
