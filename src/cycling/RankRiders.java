package cycling;

import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

/**
 * RankRiders covers the functions for ranking all riders,
 * based on their overall race time.
 *
 * @author Harry Gardner
 */

public class RankRiders extends CyclingPortalImpl {
    /**
     * Ranks the riders based on their times in the stage.
     *
     * @param stageId the ID of the stage.
     * @return a HashMap of the sorted rider IDs and the accompanying sorted LocalTime[] array.
     */
    public static HashMap<int[], LocalTime[]> rank(int stageId) throws IDNotRecognisedException {
        if (!stages.containsKey(stageId)) {
            throw new IDNotRecognisedException("Stage ID does not exist: " + stageId);
        }

        // Retrieve all results for the stage:
        HashMap<Integer, LocalTime[]> riderResults = allResults.get(stageId);

        // Create a list to store rider IDs and their corresponding total elapsed time
        List<Map.Entry<Integer, LocalTime[]>> riderTimePairs = getEntries(stageId, riderResults);

        // Retrieve the sorted rider IDs
        int[] rankedRiders = new int[riderTimePairs.size()];
        LocalTime[] allTotalElapsed = new LocalTime[riderTimePairs.size()];
        for (int i = 0; i < riderTimePairs.size(); i++) {
            Map.Entry<Integer, LocalTime[]> pair = riderTimePairs.get(i);
            rankedRiders[i] = pair.getKey();
            allTotalElapsed[i] = getRiderAdjElapTimeCopy(stageId, pair.getKey());
        }

        HashMap<int[], LocalTime[]> riderRankAndTime = new HashMap<>();
        riderRankAndTime.put(rankedRiders, allTotalElapsed);
        System.out.println(Arrays.toString(allTotalElapsed));

        return riderRankAndTime;
    }

    /**
     * Works with rank to get entries based on every two checkpoint times.
     *
     * @param stageId       the ID of the stage.
     * @param riderResults  the results for the rider in said stage.
     * @return a List of the riders by RiderId and LocalTime.
     */
    private static List<Map.Entry<Integer, LocalTime[]>> getEntries(int stageId, HashMap<Integer, LocalTime[]> riderResults) throws IDNotRecognisedException {
        if (!stages.containsKey(stageId)) {
            throw new IDNotRecognisedException("Stage ID does not exist: " + stageId);
        }

        // Create new list for the rider time pairs:
        List<Map.Entry<Integer, LocalTime[]>> riderTimePairs = new ArrayList<>(riderResults.entrySet());

        // Sort the list based on total elapsed time
        riderTimePairs.sort((o1, o2) -> {
            LocalTime totalElapsedTime1;
            try {
                totalElapsedTime1 = getRiderAdjElapTimeCopy(stageId, o1.getKey());
            } catch (IDNotRecognisedException e) {
                throw new RuntimeException(e);
            }
            LocalTime totalElapsedTime2;
            try {
                totalElapsedTime2 = getRiderAdjElapTimeCopy(stageId, o2.getKey());
            } catch (IDNotRecognisedException e) {
                throw new RuntimeException(e);
            }
            return totalElapsedTime1.compareTo(totalElapsedTime2);
        });
        return riderTimePairs;
    }

    /**
     * A copy of CyclingPortalImpl's getRiderAdjustedElapsedTimesInStage().
     * in order to avoid a cyclical inheritance.
     *
     * @param stageId  the ID of the stage.
     * @param riderId  the ID of the rider.
     * @return the rider's total elapsed time.
     */
    public static LocalTime getRiderAdjElapTimeCopy(int stageId, int riderId) throws IDNotRecognisedException {
        if (!riders.containsKey(riderId)) {
            throw new IDNotRecognisedException("Rider ID does not exist: " + riderId);
        }

        // Retrieve the rider's LocalTime[] array from their results:
        HashMap<String, Object> riderDetails = riders.get(riderId);
        HashMap<Integer, LocalTime[]> resultDetails = (HashMap<Integer, LocalTime[]>) riderDetails.get("results");
        LocalTime[] allTimes = resultDetails.get(stageId);

        //Create totalElapsedTime, storing the time in hours, minutes, seconds:
        LocalTime totalElapsedTime = LocalTime.of(0, 0, 0);
        //Loop through this array, taking every two times to calculate the time between:
        for (int i = 0; i < allTimes.length; i += 2) {
            // Check if there are at least two more times in the array:
            if (i + 1 < allTimes.length) {
                // Calculate the difference between the consecutive times
                long elapsedTimeSeconds = ChronoUnit.SECONDS.between(allTimes[i], allTimes[i + 1]);
                // Add the difference to totalElapsedTime:
                totalElapsedTime = totalElapsedTime.plusSeconds(elapsedTimeSeconds);
            }
        }

        return totalElapsedTime;
    }
}

