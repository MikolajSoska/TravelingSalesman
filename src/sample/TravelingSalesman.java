package sample;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Random;

/**
 * Klasa służy do przeprowadzenia działania algorytmu Komiwojażera
 */
public class TravelingSalesman implements Runnable {
    private ArrayList<City> cities; //lista miast
    private ArrayList<Path> population; //populacja
    private int numberOfCities; //numer miast
    private int populationSize; //dany rozmiar populacji
    private int numberOfIterations; //liczba iteracji algorytmu
    private int numberOfCrossovers; //liczba krzyzowan w jednej iteracji
    private Controller controller; //kontroler

    /**
     * Konstruktor
     * @param cities wygenerowana lista miast
     * @param populationSize roziar populacji
     * @param numberOfIterations liczba iteracji
     * @param numberOfCrossovers liczba krzyzowan
     * @param controller kontroler
     */
    TravelingSalesman(ArrayList<City> cities, int populationSize, int numberOfIterations, int numberOfCrossovers, Controller controller){
        this.cities = cities;
        this.numberOfCities = cities.size();
        this.populationSize = populationSize;
        this.numberOfIterations = numberOfIterations;
        this.numberOfCrossovers = numberOfCrossovers;
        this.controller = controller;
        population = new ArrayList<>(populationSize);
    }

    /**
     * Metoda słuzy wygenerowaniu początkowej populacji
     */
    private void generatePopulation(){
        for(int i = 0; i < populationSize; i++){
            population.add(new Path(cities));
        }
    }

    /**
     * Funkcja krzyżowania
     */
    private void crossover(){
        population.sort(Comparator.comparingDouble(Path::getLength)); //posortowanie populacji wedlug dlugosci drogi
        Path parent1 = population.get(0); //rodzic o najkrotszej drodze
        Path parent2 = population.get(1); //rodzic drugi w kolejnosci
        City child1[] = new City[numberOfCities]; //pierwszy potomek
        City child2[] = new City[numberOfCities]; //drugi potomek
        Random random = new Random();

        // operator krzyzowania OX
        int beginIndex = random.nextInt(numberOfCities + 1); //pierwsze miejsce podzialu
        int endIndex = random.nextInt(numberOfCities  + 1); //drugie miejsce podzialu

        while (endIndex == beginIndex) //gdy wylosujemy te same liczby
            endIndex = random.nextInt(numberOfCities + 1);

        if(beginIndex > endIndex){ //zamieniamy zeby beginIndex byl pierwszy
            int temp = beginIndex;
            beginIndex = endIndex;
            endIndex = temp;
        }

        // wypelniamy tablice pomiedzy miejscami podzialu
        for(int i = beginIndex; i < endIndex; i++){
            child1[i] = parent1.getCity(i);
            child2[i] = parent2.getCity(i);
        }

        //zapelniamy pozostale miejsca
        fillChild(beginIndex, endIndex, child1, parent2);
        fillChild(beginIndex, endIndex, child2, parent1);

        //5% prawdopodobnienstwa na wystapienie mutacji
        if(random.nextInt(20) == 0) {
            mutation(child1, beginIndex, endIndex);
            mutation(child2, beginIndex, endIndex);
        }

        population.add(new Path(child1)); //dodajemy dziecko1 do populacji
        population.add(new Path(child2)); //dodajemy dziecko2 do populacji
    }

    /**
     * Funkcja uzupelnia tablice miast dziecka za pomoca operatora OX
     * @param beginIndex indeks poczatkowy
     * @param endIndex indeks koncowy
     * @param child dziecko
     * @param parent rodzic
     */
    private void fillChild(int beginIndex, int endIndex, City child[], Path parent){
        for(int i = endIndex, j = endIndex; i != beginIndex; i++){
            if(i == numberOfCities){
                i = -1;
                continue;
            }
            if(j == numberOfCities)
                j = 0;

            while(childContains(child, parent.getCity(j))){
                if(++j == numberOfCities)
                    j = 0;
            }
            child[i] = parent.getCity(j++);
        }
    }

    /**
     * Funkcja sprawdza czy dane miasto znajduje się w dziecku
     * @param child dziecko
     * @param city miasto
     * @return true jesli miasto znajduje sie w dziecku, false przeciwnie
     */
    private boolean childContains(City child[], City city){
        for(int i = 0; i < numberOfCities; i++)
            if(child[i] != null && child[i].isEqual(city))
                return true;
        return false;
    }

    /**
     * Funkcja usuwa najgorsze osobniki w populacji przywracajac jej rozmiar do poczatkowego
     */
    private void selection(){
        population.sort(Comparator.comparingDouble(Path::getLength));
        while (population.size() > populationSize)
            population.remove(population.size() - 1);
    }

    /**
     * Mutacja, wymieniamy dwa losowe miasta miejscami
     * @param path dziecko ktore zostanie zmutowane
     */
    private void mutation(City path[], int beginIndex, int endIndex){
        City tab[] = new City[endIndex - beginIndex];

        for(int i = beginIndex, k = 0; i < endIndex; i++, k++)
            tab[k] = path[i];
        for(int i = endIndex - 1, k = 0; i >= beginIndex; i--,k++)
            path[i] = tab[k];
    }

    /**
     * Głowna funkcja, ktora steruje dzialaniem algorytmu
     */
    @Override
    public void run() {
        generatePopulation(); //generujemy populacje
        double sleepTime = 200; //poczatkowa wartosc czekania
        double diff = sleepTime/numberOfIterations; //liczba, o ktora bedziemy zmniejszac wartosc oczekiwania
        for(int i = 0; i < numberOfIterations; i++) {
            for (int j = 0; j < numberOfCrossovers; j++)
                crossover(); //krzyzowanie
            selection(); //selekcja
            controller.drawPath(population.get(0)); //rysujemy najlepsza sciezke w danej iteracji
            controller.setIteration(controller, i+1);
            controller.setPathLength(controller, (int) population.get(0).getLength());
            try {
                Thread.sleep((long) sleepTime);
                sleepTime -= diff;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
