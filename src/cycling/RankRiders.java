package cycling;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RankRiders {
    public static HashMap<int[], LocalTime[]> rank(int stageId, HashMap<Integer, HashMap<Integer, LocalTime[]>> allResults, HashMap<Integer, HashMap<String, Object>> stages, CyclingPortalImpl cyclingPortal) throws IDNotRecognisedException {
        if (!stages.containsKey(stageId)) {
            throw new IDNotRecognisedException("Stage ID does not exist: " + stageId);
        }
        // Retrieve all results for the stage:
        HashMap<Integer, HashMap<Integer, LocalTime[]>> riderResults = allResults;

        // Create the sortedResults HashMap:
        HashMap<LocalTime, Integer> sortedResults = new HashMap<>();

        // Iterate through riderResults to calculate total elapsed time for each rider:
        List<HashMap<Integer, LocalTime[]>> entryList = new ArrayList<>(riderResults.values());
        LocalTime[] allTotalElapsed = new LocalTime[entryList.size() + 1];
        for (int i = 0; i < entryList.size(); i++) {
            HashMap<Integer, LocalTime[]> entry = entryList.get(i);
            int riderId = entry.keySet().iterator().next();

            // Calculate total elapsed time for the rider:
            LocalTime totalElapsedTime = cyclingPortal.getRiderAdjustedElapsedTimeInStage(stageId, riderId);

            // Store in sortedResults and allTotalElapsed:
            sortedResults.put(totalElapsedTime, riderId);
            allTotalElapsed[i] = totalElapsedTime;
        }

        // Create an array to store the sorted rider IDs:
        int[] rankedRiders = new int[sortedResults.size()];
        int index = 0;

        // Iterate through the sortedResults HashMap to add to the array:
        for (int riderId : sortedResults.values()) {
            rankedRiders[index++] = riderId;
        }

        HashMap<int[], LocalTime[]> riderRankAndTime = new HashMap<>();
        riderRankAndTime.put(rankedRiders, allTotalElapsed);

        return riderRankAndTime;
    }
}
