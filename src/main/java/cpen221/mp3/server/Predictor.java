package cpen221.mp3.server;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Objects;

public class Predictor {

    // Rep Invariants:
    //
    // 1. Positive Entity ID: The entityId must be a positive integer.
    // 2. Non-negative N: The n must be non-negative.
    // 3. Non-null Starting Values: The startingValues must not be null.

    // Abstraction function:
    // Maps the internal state of the Predictor class to the abstract representation of a predictor.
    //
    // Entity ID: entityId represents the unique identifier of the entity associated with the predictor.
    // N: n represents the number of values to be predicted
    // Starting Values: startingValues is a list containing the initial values for prediction.

    private int entityId;

    /**
     * Number of values to predict
     */
    private int n;

    /**
     * Starting values to predict from
     */
    private List<Double> startingValues;

    public Predictor(int entityId, int n, List<Double> startingValues) {
        this.entityId = entityId;
        this.n = n;
        this.startingValues = startingValues;
    }

    /**
     * Predicts patterns in non-alternating sequences
     * @param actualValues list of values to base predictons on
     * @param n number of values to predict
     * @return predicted values
     */
    public static List<Double> extrapolate(List<Double> actualValues, double n) {
        ArrayList<Double> predictedValues = new ArrayList<Double>();

        List<Double> forecasts = new ArrayList<Double>();
        List<Double> centerMovingAverages = new ArrayList<Double>();
        List<Double> predictions = new ArrayList<Double>();

        List<Integer> xvals = new ArrayList<Integer>();
        List<Double> yvals = new ArrayList<Double>();

        forecasts.add(actualValues.get(0));

        for (int i = 1; i < actualValues.size(); i++) {
            forecasts.add(getAverage(actualValues.get(i), actualValues.get(i - 1)));
        }

        for (int i = 0; i < forecasts.size() - 1; i++) {
            centerMovingAverages.add(getAverage(forecasts.get(i), forecasts.get(i + 1)));
            xvals.add(i);
            yvals.add(getAverage(forecasts.get(i), forecasts.get(i + 1)));
        }

        double slope = getSlope(centerMovingAverages.size(), xvals, yvals);
        double yInt = getB(slope, centerMovingAverages.size(), xvals, yvals);
        double prediction = actualValues.get(0);

        for (int x = centerMovingAverages.size(); x < centerMovingAverages.size() + n + 10; x++) {
            predictions.add(slope*x + yInt);
        }

        for (int i = 1; i <= n; i++) {
            predictedValues.add(predictions.get(i));
        }

        return predictedValues;
    }

    private static double getAverage(double ... values) {
        double sum = 0.0;
        double total = 0.0;
        for (double d : values) {
            sum += d;
            total++;
        }
        return sum/total;
    }

    private static double getSlope(int n, List<Integer> xVals, List<Double> yVals) {
        double xSummation = 0.0;
        double ySummation = 0.0;
        double xySummation = 0.0;
        double xSqSummation = 0.0;

        double slope;

        for (int i = 0; i < xVals.size(); i++) {
            xSummation += xVals.get(i);
            ySummation += yVals.get(i);
            xySummation += xVals.get(i) * yVals.get(i);
            xSqSummation += Math.pow(xVals.get(i), 2);
        }

        slope = ((n * xySummation) - (xSummation * ySummation))/ ((n * xSqSummation) - Math.pow(xSummation, 2));

        return slope;
    }

    private static double getB(double m, int n, List<Integer> xVals, List<Double> yVals) {
        double xSummation = 0.0;
        double ySummation = 0.0;

        for (int i = 0; i < xVals.size(); i++) {
            xSummation += xVals.get(i);
            ySummation += yVals.get(i);
        }

        return (ySummation - m * xSummation)/ n;
    }

    public static List<Double> alternatePrediction(List<Double> startingValues, int n) {
        double val1 = startingValues.get(0);
        double val2 = startingValues.get(1);
        double lastVal = startingValues.get(startingValues.size() - 1);
        ArrayList<Double> predictList = new ArrayList<>();
        if (lastVal == val1) {
            if (n % 2 == 0) {
                for (int i = 0; i < n / 2; i++) {
                    predictList.add(val2);
                    predictList.add(val1);
                }
            } else {
                for (int i = 0; i < (n - 1) / 2; i++) {
                    predictList.add(val2);
                    predictList.add(val1);
                }
                predictList.add(val2);
            }
        } else {
            if (n % 2 == 0) {
                for (int i = 0; i < n / 2; i++) {
                    predictList.add(val1);
                    predictList.add(val2);
                }
            } else {
                for (int i = 0; i < (n- 1 ) / 2; i++) {
                    predictList.add(val1);
                    predictList.add(val2);
                }
                predictList.add(val1);
            }
        }
        return predictList;
    }

    public static List<Boolean> alternateBoolean(List<Boolean> startingValues, int n) {
        boolean val1 = startingValues.get(0);
        boolean val2 = startingValues.get(1);
        boolean lastVal = startingValues.get(startingValues.size() - 1);
        ArrayList<Boolean> predictList = new ArrayList<>();
        if (lastVal == val1) {
            if (n % 2 == 0) {
                for (int i = 0; i < n / 2; i++) {
                    predictList.add(val2);
                    predictList.add(val1);
                }
            } else {
                for (int i = 0; i < (n - 1) / 2; i++) {
                    predictList.add(val2);
                    predictList.add(val1);
                }
                predictList.add(val2);
            }
        } else {
            if (n % 2 == 0) {
                for (int i = 0; i < n / 2; i++) {
                    predictList.add(val1);
                    predictList.add(val2);
                }
            } else {
                for (int i = 0; i < (n - 1) / 2; i++) {
                    predictList.add(val1);
                    predictList.add(val2);
                }
                predictList.add(val1);
            }
        }
        return predictList;
    }



    public static List<Double> findPattern(List<Double> startingValues, int n) {
        if (startingValues.size() == 1) {
            List<Double> prediction = new ArrayList<>();
            for (int i = 0; i < n; i++) {
                prediction.add(startingValues.get(0));
            }
            return prediction;
        } else if (startingValues.size() == 2) {
            List<Double> prediction = new ArrayList<>();
            for (int i = 0; i < n; i++) {
                prediction.add(startingValues.get(i % 2));
            }
            return prediction;
        } else if (startingValues.size() == 3) {
            return extrapolate(startingValues, n);
        }
        List<Double> longestPattern = new ArrayList<>();
        List<Double> currentPattern = new ArrayList<>();
        for (int i = startingValues.size() - 4; i < startingValues.size() - 3; i++) {
            if (startingValues.get(i).equals(startingValues.get(i + 2)) && startingValues.get(i + 1).equals(startingValues.get(i + 3)) &&
                    !Objects.equals(startingValues.get(i), startingValues.get(i + 1))) {
                currentPattern.add(startingValues.get(i));
                currentPattern.add(startingValues.get(i + 1));
                currentPattern.add(startingValues.get(i + 2));
                currentPattern.add(startingValues.get(i + 3));
            } else {
                if (currentPattern.size() > longestPattern.size()) {
                    longestPattern = new ArrayList<>(currentPattern);
                }
                currentPattern.clear();
            }
        }
        if (currentPattern.size() >= longestPattern.size()) {
            longestPattern = new ArrayList<>(currentPattern);
        }
        if (longestPattern.size() > 3) {
            return alternatePrediction(longestPattern, n);
        } else {
            return extrapolate(startingValues, n);
        }

    }

    /**
     * Predicts the next n numbers
     * @return next n numbers
     */
    public List<Double> predict() {
        return findPattern(startingValues, n);
    }
}