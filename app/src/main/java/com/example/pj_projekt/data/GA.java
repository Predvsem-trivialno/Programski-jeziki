package com.example.pj_projekt.data;

import java.util.ArrayList;

public class GA {

    int popSize;
    double cr; //crossover probability
    double pm; //mutation probability

    ArrayList<TSP.Tour> population;
    ArrayList<TSP.Tour> offspring;

    public GA(int popSize, double cr, double pm) {
        this.popSize = popSize;
        this.cr = cr;
        this.pm = pm;
    }

    private TSP.Tour findBest(ArrayList<TSP.Tour> population){
        TSP.Tour max = null;
        double maxFitness = Double.MAX_VALUE;
        for (TSP.Tour tour : population) {
            if (tour.distance < maxFitness) {
                max = tour;
                maxFitness = tour.distance;
            }
        }
        assert max != null;
        return max.clone();
    }

    public TSP.Tour execute(TSP problem) {
        population = new ArrayList<>();
        offspring = new ArrayList<>();

        for (int i = 0; i < popSize; i++) {
            TSP.Tour newTour = problem.generateTour();
            problem.evaluate(newTour);
            population.add(newTour);
        }

        TSP.Tour best = findBest(population).clone();

        while (problem.getNumberOfEvaluations() < problem.getMaxEvaluations()) {
            //elitizem, dodaj najboljšega v offspring
            offspring.add(best.clone());
            while (offspring.size() < popSize) {
                TSP.Tour parent1;
                TSP.Tour parent2;
                do{
                    parent1 = tournamentSelection();
                    parent2 = tournamentSelection();
                } while(parent1 == parent2);

                if (RandomUtils.nextDouble() < cr) {
                    TSP.Tour[] children = pmx(parent1, parent2);
                    offspring.add(children[0]);
                    if (offspring.size() < popSize)
                        offspring.add(children[1]);
                } else {
                    offspring.add(parent1.clone());
                    if (offspring.size() < popSize)
                        offspring.add(parent2.clone());
                }
            }

            for (TSP.Tour off : offspring) {
                if (RandomUtils.nextDouble() < pm) {
                    swapMutation(off);
                }
            }

            for(TSP.Tour tour : offspring){
                problem.evaluate(tour);
            }
            best = findBest(offspring);

            population = new ArrayList<>(offspring);
            offspring.clear();
        }
        return best;
    }

    private void swapMutation(TSP.Tour off) {
        int index1, index2;
        do {
            index1 = RandomUtils.nextInt(off.dimension);
            index2 = RandomUtils.nextInt(off.dimension);
        } while (index1==index2);
        TSP.City temp = off.path[index1];
        off.setCity(index1,off.path[index2]);
        off.setCity(index2,temp);
    }

    private TSP.Tour[] pmx(TSP.Tour parent1, TSP.Tour parent2) {
        ArrayList<Integer> indexes1 = new ArrayList<>();
        ArrayList<Integer> indexes2 = new ArrayList<>();
        // Shranimo indexe mest iz poti posameznega starša
        for(int i=0;i<parent1.dimension;i++){
            indexes1.add(parent1.path[i].index);
            indexes2.add(parent2.path[i].index);
        }
        // Naključno določimo točki križanja
        int crossOverPoint1, crossOverPoint2;
        do {
            crossOverPoint1 = RandomUtils.nextInt(indexes1.size());
            crossOverPoint2 = RandomUtils.nextInt(indexes2.size());
        } while (crossOverPoint1==crossOverPoint2 || crossOverPoint1 > crossOverPoint2);
        // Pripravimo nove indexe
        ArrayList<Integer> newIndexes1 = new ArrayList<>();
        ArrayList<Integer> newIndexes2 = new ArrayList<>();
        for(int i=0;i<indexes1.size();i++){
            newIndexes1.add(-1);
            newIndexes2.add(-1);
        }
        // Napolnimo števila iz območja križanja direktno med nove indekse
        for(int i=crossOverPoint1;i<crossOverPoint2;i++){
            newIndexes1.set(i, indexes2.get(i));
            newIndexes2.set(i, indexes1.get(i));
        }
        // Napolnimo ostala števila, s katerimi ni kolizije znotraj območja križanja
        for(int i=0;i<indexes1.size();i++){
            if(newIndexes1.get(i) == -1 && !newIndexes1.contains(indexes1.get(i))){
                newIndexes1.set(i, indexes1.get(i));
            }
            if(newIndexes2.get(i) == -1 && !newIndexes2.contains(indexes2.get(i))){
                newIndexes2.set(i, indexes2.get(i));
            }
        }
        // Razrešimo kolizije po pravilu PMX
        for(int i=0;i<indexes1.size();i++){
            if(newIndexes1.get(i) == -1){
                int number = indexes1.get(i);
                while(true){
                    if(newIndexes1.contains(number)){
                        number = newIndexes2.get(newIndexes1.indexOf(number));
                    } else break;
                }
                newIndexes1.set(i,number);
            }
            if(newIndexes2.get(i) == -1){
                int number = indexes2.get(i);
                while(true){
                    if(newIndexes2.contains(number)){
                        number = newIndexes1.get(newIndexes2.indexOf(number));
                    } else break;
                }
                newIndexes2.set(i,number);
            }
        }
        // Pripravimo novi Tour, ki ga na koncu vrnemo kot rezultat križanja (potomec1, potomec1)
        TSP.Tour child1 = parent1.clone();
        TSP.Tour child2 = parent2.clone();
        for(int i=0;i<newIndexes1.size();i++){
            child1.setCity(newIndexes1.indexOf(parent1.path[i].index),parent1.path[i]);
            child2.setCity(newIndexes2.indexOf(parent2.path[i].index),parent2.path[i]);
        }
        TSP.Tour[] children = new TSP.Tour[2];
        children[0] = child1;
        children[1] = child2;
        return children;
    }

    private TSP.Tour tournamentSelection() {
        int index1, index2;
        do {
            index1 = RandomUtils.nextInt(popSize);
            index2 = RandomUtils.nextInt(popSize);
        } while (index1==index2);
        if(population.get(index1).distance < population.get(index2).distance){
            return population.get(index1).clone();
        } else {
            return population.get(index2).clone();
        }
    }
}
