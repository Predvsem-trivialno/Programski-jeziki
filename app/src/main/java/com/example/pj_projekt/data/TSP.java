package com.example.pj_projekt.data;

import android.util.Log;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class TSP {

    enum DistanceType { EUCLIDEAN, WEIGHTED }
    enum DataType { duration, distance }

    public class City {
        public int index;
        public double x, y;
    }

    public class Tour {

        double distance;
        int dimension;
        City[] path;

        public Tour(Tour tour) {
            distance = tour.distance;
            dimension = tour.dimension;
            path = tour.path.clone();
        }

        public Tour(int dimension) {
            this.dimension = dimension;
            path = new City[dimension];
            distance = Double.MAX_VALUE;
        }

        public Tour clone() {
            return new Tour(this);
        }

        public double getDistance() {
            return distance;
        }

        public void setDistance(double distance) {
            this.distance = distance;
        }

        public City[] getPath() {
            return path;
        }

        public void setPath(City[] path) {
            this.path = path.clone();
        }

        public void setCity(int index, City city) {
            path[index] = city;
            distance = Double.MAX_VALUE;
        }
    }

    String name;
    City start;
    List<City> cities = new ArrayList<>();
    int numberOfCities;
    double[][] weights;
    DistanceType distanceType = DistanceType.EUCLIDEAN;
    DataType dataType;
    int numberOfEvaluations, maxEvaluations;

    public TSP(ArrayList<Location> locations, String dataType, int maxEvaluations) {
        if(dataType.equals("Time")) this.dataType = DataType.duration;
        else this.dataType = DataType.distance;

        numberOfEvaluations = 0;
        this.maxEvaluations = maxEvaluations;

        loadAPIData(locations);
    }

    public void evaluate(Tour tour) {
        double distance = 0;
        distance += calculateDistance(start, tour.getPath()[0]);
        for (int index = 0; index < numberOfCities; index++) {
            if (index + 1 < numberOfCities)
                distance += calculateDistance(tour.getPath()[index], tour.getPath()[index + 1]);
            else
                distance += calculateDistance(tour.getPath()[index], start);
        }
        tour.setDistance(distance);
        numberOfEvaluations++;
    }

    private double calculateDistance(City from, City to) {
        double value;
        if(distanceType==DistanceType.EUCLIDEAN) {
            value = Math.sqrt(Math.pow(from.x - to.x, 2.0) + Math.pow(from.y - to.y, 2.0));
        } else if(distanceType==DistanceType.WEIGHTED) {
            value = weights[from.index][to.index];
        } else {
            value = Double.MAX_VALUE;
        }
        return value;
    }

    public Tour generateTour() {
        Tour randomTour = new Tour(numberOfCities);
        List<Integer> usedCities = new ArrayList<>();
        for(int i=0;i<numberOfCities;i++){
            int randomIndex;
            do {
                randomIndex = RandomUtils.nextInt(0,numberOfCities);
            } while (usedCities.contains(randomIndex));
            usedCities.add(randomIndex);
            randomTour.setCity(i, cities.get(randomIndex));
        }
        return randomTour;
    }

    private void loadAPIData(ArrayList<Location> locations) {
        String API_KEY = "5b3ce3597851110001cf62486a80c5511bc44de08bf4cbf48cfe28cd";
        ArrayList<ArrayList<Double>> coordinatePairs = new ArrayList<>();
        numberOfCities = locations.size();
        for (int i = 0; i < numberOfCities; i++) {
            City newCity = new City();
            newCity.index = locations.get(i).getIndex();
            newCity.x = locations.get(i).getCoordLat();
            newCity.y = locations.get(i).getCoordLong();
            cities.add(newCity);
            if (i == 0) start = newCity;

            ArrayList<Double> coordinatePair = new ArrayList<>();
            coordinatePair.add(locations.get(i).getCoordLong());
            coordinatePair.add(locations.get(i).getCoordLat());
            coordinatePairs.add(coordinatePair);
        }

        Gson gson = new Gson();
        ArrayList<String> metric = new ArrayList<>();
        metric.add(dataType.toString());
        MatrixCall matrixCall = new MatrixCall(metric, coordinatePairs);
        String jsonString = gson.toJson(matrixCall);

        OkHttpClient client = new OkHttpClient();
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody formBody = RequestBody.create(jsonString, JSON);
        Log.i("JSON STRING", jsonString);

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                Request request = new Request.Builder()
                        .url("https://api.openrouteservice.org/v2/matrix/driving-car")
                        .header("Authorization", API_KEY)
                        .header("Accept", "application/json, application/geo+json, application/gpx+xml, img/png; charset=utf-8")
                        .header("Content-Type", "application/json; charset=utf-8")
                        .post(formBody)
                        .build();

                try {
                    Response response = client.newCall(request).execute();
                    Log.i("MATRIX RESPONSE CODE", String.valueOf(response.code()));
                    Log.i("MATRIX", response.body().string());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }});

        t.start();
        try {
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void loadFile(String path) {
        InputStream inputStream = TSP.class.getClassLoader().getResourceAsStream(path);
        if(inputStream == null) {
            System.err.println("File "+path+" not found!");
            return;
        }

        List<String> lines = new ArrayList<>();
        try(BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {

            String line = br.readLine();
            while (line != null) {
                lines.add(line);
                line = br.readLine();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        int lineCount = 0;
        for(String line : lines){
            lineCount++;
            if(line.contains(":")){
                String[] lineElements = line.strip().split(":");
                String variable = lineElements[0].strip();
                String value = lineElements[1].strip();
                switch (variable) {
                    case "NAME":
                        name = value;
                        break;
                    case "DIMENSION":
                        numberOfCities = Integer.parseInt(value);
                        break;
                    case "EDGE_WEIGHT_TYPE":
                        if (value.equals("EUC_2D")) {
                            distanceType = DistanceType.EUCLIDEAN;
                        } else if (value.equals("EXPLICIT")) {
                            distanceType = DistanceType.WEIGHTED;
                        } else {
                            System.err.println("EDGE WEIGHT TYPE NOT SUPPORTED!");
                            return;
                        }
                        break;
                    case "EDGE_WEIGHT_FORMAT":
                        if ("FULL_MATRIX".equals(value)) {
                            System.out.println("Using FULL_MATRIX format.");
                        } else {
                            System.err.println("EDGE WEIGHT FORMAT NOT SUPPORTED!");
                            return;
                        }
                        break;
                }
            } else {
                break;
            }
        }
        lines.subList(0, lineCount-1).clear();
        lines.remove(lines.size() - 1);
        if(distanceType == DistanceType.EUCLIDEAN) {
            lines.remove(0);
            for(int i = 0; i < lines.size(); i++) {
                String[] lineElements = lines.get(i).trim().replaceAll(" +", " ").split(" ");
                City newCity = new City();
                newCity.index = i;
                newCity.x = Double.parseDouble(lineElements[1]);
                newCity.y = Double.parseDouble(lineElements[2]);
                if(i == 0) {
                    start = newCity;
                }
                cities.add(newCity);
            }
        }
        else if(distanceType == DistanceType.WEIGHTED){
            int startSplit = 0, endSplit = 0;
            int counter = 0;
            for(String line : lines){
                if (line.equals("EDGE_WEIGHT_SECTION")) {
                    startSplit = counter + 1;
                } else if (line.equals("DISPLAY_DATA_SECTION")){
                    endSplit = counter;
                }
                counter++;
            }

            List<String> matrixString = new ArrayList<>();
            for(int i = startSplit; i < endSplit; i++) {
                matrixString.add(lines.get(i));
            }

            weights = new double[matrixString.size()][matrixString.size()];
            for(int i = 0; i < matrixString.size(); i++) {
                matrixString.set(i, matrixString.get(i).trim().replaceAll(" +", " "));
                String[] matrixLineElements = matrixString.get(i).split(" ");
                for (int j = 0; j < matrixLineElements.length; j++) {
                    weights[i][j] = Double.parseDouble(matrixLineElements[j]);
                }
            }

            startSplit = endSplit + 1;
            endSplit = lines.size();

            List<String> dataSection = new ArrayList<>();
            for(int i = startSplit; i < endSplit; i++) {
                dataSection.add(lines.get(i).trim().replaceAll(" +", " "));
            }

            for(int i = 0; i < dataSection.size(); i++) {
                String[] dataSectionLineElements = dataSection.get(i).split(" ");
                City newCity = new City();
                newCity.index = i;
                newCity.x = Double.parseDouble(dataSectionLineElements[1]);
                newCity.y = Double.parseDouble(dataSectionLineElements[2]);

                if(i == 0) start = newCity;
                cities.add(newCity);
            }
        }
        else {
            System.err.println("EDGE WEIGHT TYPE NOT SUPPORTED!");
        }
    }

    public int getMaxEvaluations() {
        return maxEvaluations;
    }

    public int getNumberOfEvaluations() {
        return numberOfEvaluations;
    }
}

 // Tako bi potekalo pridobivanje koordinat za posamezna mesta s pomočjo API klicev
        /*for(int i = 0; i < numberOfCities; i++) {
            Response response = client.target("https://api.openrouteservice.org/geocode/search?api_key=" + API_KEY + "&text=" + ZIPCodesData.get(i) + "%20" + citiesData.get(i) + "%20" + addressData.get(i) + "&boundary.country=SI")
                    .request(MediaType.TEXT_PLAIN_TYPE)
                    .header("Accept", "application/json, application/geo+json, application/gpx+xml, img/png; charset=utf-8")
                    .get();

            String responseBody = response.readEntity(String.class);

            try {
                JSONObject responseObject = (JSONObject) parser.parse(responseBody);
                JSONArray innerObject = (JSONArray) responseObject.get("features");
                JSONObject geometryObject = (JSONObject) innerObject.get(0);
                JSONObject geometryInnerObject = (JSONObject) geometryObject.get("geometry");
                List<Double> coordinates = (List<Double>) geometryInnerObject.get("coordinates");
                longitudeData.add(coordinates.get(0));
                latitudeData.add(coordinates.get(1));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }*/
