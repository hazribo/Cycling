package cycling;

import java.io.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

/**
 * CyclingPortalImpl is a fully-functioning implementor
 * of the MiniCyclingPortal interface.
 *
 * @author Harry Gardner
 */

public class CyclingPortalImpl implements MiniCyclingPortal {

    static HashMap<Integer, HashMap<String, Object>> races;
    static HashMap<Integer, HashMap<String, Object>> stages;
    static HashMap<Integer, HashMap<String, Object>> checkpoints;
    static HashMap<Integer, HashMap<String, Object>> teams;
    static HashMap<Integer, HashMap<String, Object>> riders;
    static HashMap<Integer, HashMap<Integer, LocalTime[]>> allResults;
    static HashMap<Integer, int[]> mountainPoints;

    public CyclingPortalImpl() {
        races = new HashMap<>();
        stages = new HashMap<>();
        checkpoints = new HashMap<>();
        teams = new HashMap<>();
        riders = new HashMap<>();
        allResults = new HashMap<>();
        mountainPoints = new HashMap<>();
    }

    @Override
    public int[] getRaceIds() {
        return IDHandler.getIdsFromHashMap(races);
    }

    @Override
    public int createRace(String name, String description) throws IllegalNameException, InvalidNameException {

        if (name == null) {
            throw new InvalidNameException("Invalid race name.");
        }

        // Iterate through list of race names to ensure no duplicate name:
        for (HashMap<String, Object> raceDetails : races.values()) {
            String existingName = (String) raceDetails.get("name");
            if (existingName.equals(name)) {
                throw new IllegalNameException("Name already in use: " + name);
            }
        }

        HashMap<String, Object> raceDetails = new HashMap<>();
        raceDetails.put("name", name);
        raceDetails.put("description", description);
        raceDetails.put("totalLength", 0.0);
        raceDetails.put("stageIds", new ArrayList<Integer>());
        ArrayList<Integer> stageIds = (ArrayList<Integer>) raceDetails.get("stageIds");
        raceDetails.put("numStages", stageIds.size());

        //Create the raceId:
        int raceId = IDHandler.newId(races);

        assert raceId >= 0 : "Race ID must not be negative";
        races.put(raceId, raceDetails);

        return raceId;
    }

    @Override
    public String viewRaceDetails(int raceId) throws IDNotRecognisedException {
        if (!races.containsKey(raceId)) {
            throw new IDNotRecognisedException("ID does not exist: " + raceId);
        }

        HashMap<String, Object> raceDetails = races.get(raceId);
        Object name = raceDetails.get("name");
        Object description = raceDetails.get("description");
        Object numStages = raceDetails.get("numStages");
        Object totalLength = raceDetails.get("totalLength");

        String stats = ("Name: " + name
                + "\nDesc: " + description
                + "\nNumber of stages: " + numStages
                + "\nTotal stage length: " + totalLength);

        System.out.println(stats);
        return stats;
    }

    @Override
    public void removeRaceById(int raceId) throws IDNotRecognisedException {
        if (!races.containsKey(raceId)) {
            throw new IDNotRecognisedException("ID does not exist: " + raceId);
        }

        //Retrieve raceDetails:
        HashMap<String, Object> raceDetails = races.get(raceId);

        //Get all stageIds:
        ArrayList<Integer> stageIds = (ArrayList<Integer>) raceDetails.get("stageIds");
        // Iterate through all stageIds, running clearStage:
        for (int stageId : stageIds) {
            StageHandler.clearStage(stageId);
        }

        //Remove from all arrays/hashmaps:
        races.remove(raceId, raceDetails);
    }

    @Override
    public int getNumberOfStages(int raceId) throws IDNotRecognisedException {
        //Ensure raceId is valid (raceId exists in races):
        if (!races.containsKey(raceId)) {
            throw new IDNotRecognisedException("ID does not exist: " + raceId);
        }

        //Retrieve raceDetails with raceId:
        HashMap<String, Object> raceDetails = races.get(raceId);

        //Retrieve numStages value, cast Object to Integer and return value:
        return (int) raceDetails.get("numStages");
    }

    @Override
    public int addStageToRace(int raceId, String stageName, String description, double length, LocalDateTime startTime,
                              StageType type)
            throws IDNotRecognisedException, IllegalNameException, InvalidNameException, InvalidLengthException {

        // Throws IDNotRecognisedException if ID doesn't exist:
        if (!races.containsKey(raceId)) {
            throw new IDNotRecognisedException("ID does not exist: " + raceId);
        }
        // Throws IllegalNameException if stageName is null, empty, or over 30 characters:
        if (stageName == null || stageName.isEmpty() || stageName.length() > 30) {
            throw new IllegalNameException("Invalid stage name.");
        }
        // Throws InvalidLengthException if stage length is under 5.0:
        if (length < 5.0) {
            throw new InvalidLengthException("Stage length too short (less than 5km).");
        }

        // Get race details:
        HashMap<String, Object> raceDetails = races.get(raceId);


        // Retrieve totalLength from raceDetails, increase by length of new stage:
        double total = (double) raceDetails.get("totalLength") + length;

        // Create stage ID:
        int stageId = IDHandler.newId(stages);
        assert stageId >= 0 : "Stage ID must not be negative";

        // Add stageId to list of all stage IDs, then update raceDetails:
        ArrayList<Integer> stageIds = (ArrayList<Integer>) raceDetails.get("stageIds");
        stageIds.add(stageId);
        raceDetails.put("stageIds", stageIds);

        // Retrieve stageDetails, initialise if non-existent:
        HashMap<String, Object> stageDetails = stages.get(stageId);
        if (stageDetails == null) {
            stageDetails = new HashMap<>();
        }
        // Throw error if new stage name already exists:
        if (stageDetails.containsValue(stageName)) {
            throw new InvalidNameException("Name already used: " + stageName);
        }

        // Append all values to stageDetails:
        stageDetails.put("stageName", stageName);
        stageDetails.put("stageId", stageId);
        stageDetails.put("description", description);
        stageDetails.put("startTime", startTime);
        stageDetails.put("stageType", type);
        stageDetails.put("stageLength", length);
        stageDetails.put("checkpointIds", new ArrayList<Integer>());
        stageDetails.put("waiting", false);
        // Append this to HashMap of all existing stages:
        stages.put(stageId, stageDetails);

        // Update numStages, totalLength, and stages values in raceDetails:
        raceDetails.replace("numStages", stageIds.size());
        raceDetails.replace("totalLength", total);

        return stageId;
    }

    @Override
    public int[] getRaceStages(int raceId) throws IDNotRecognisedException {
        // Throws IDNotRecognisedException if ID doesn't exist:
        if (!races.containsKey(raceId)) {
            throw new IDNotRecognisedException("ID does not exist: " + raceId);
        }

        HashMap<String, Object> raceDetails = races.get(raceId);
        ArrayList<Integer> stageIds = (ArrayList<Integer>) raceDetails.get("stageIds");

        int[] ids = IDHandler.getIdsFromArray(stageIds);

        System.out.println(Arrays.toString(ids));
        return ids;
    }

    @Override
    public double getStageLength(int stageId) throws IDNotRecognisedException {
        // Throws IDNotRecognisedException if ID doesn't exist:
        if (!stages.containsKey(stageId)) {
            throw new IDNotRecognisedException("ID does not exist: " + stageId);
        }

        // Retrieve stageDetails using the stage ID:
        HashMap<String, Object> stageDetails = stages.get(stageId);
        // Retrieve stageLength, cast to double (to allow printing to system):
        double length = (double) stageDetails.get("stageLength");

        System.out.println(length);
        return length;
    }

    @Override
    public void removeStageById(int stageId) throws IDNotRecognisedException {
        if (!stages.containsKey(stageId)) {
            throw new IDNotRecognisedException("ID does not exist: " + stageId);
        }

        // Retrieve stageDetails using the stage ID:
        HashMap<String, Object> stageDetails = stages.get(stageId);

        // Use raceDetails to find all races that use the to-be-removed stage:
        List<Integer> raceIdsWithStage = new ArrayList<>();
        for (Integer raceId : races.keySet()) {
            HashMap<String, Object> raceDetails = races.get(raceId);
            ArrayList<Integer> stageIds = (ArrayList<Integer>) raceDetails.get("stageIds");
            if (stageIds.contains(stageId)) {
                raceIdsWithStage.add(raceId);
            }
        }

        // Iterate through all applicable races, deducting race length/amount values:
        for (Integer i : raceIdsWithStage) {
            // Retrieve raceId, raceDetails:
            int raceId = i;
            HashMap<String, Object> raceDetails = races.get(raceId);

            // Retrieve totalLength, stages from raceDetails:
            double totalLength = (double) raceDetails.get("totalLength");
            ArrayList<Integer> stageIds = (ArrayList<Integer>) raceDetails.get("stageIds");

            // Retrieve stage length from stageDetails:
            double length = (double) stageDetails.get("stageLength");

            // Deduct values from the variables:
            totalLength -= length;
            stageIds.remove((Integer) stageId);

            // Replace the variables in their original locations:
            raceDetails.put("numStages", stageIds.size());
            raceDetails.put("totalLength", totalLength);
            raceDetails.put("stageIds", stageIds);
            races.put(raceId, raceDetails);
        }

        // Delete the stage:
        stages.remove(stageId);
        System.out.println("\nStage removed: " + stageId + "\n");

    }

    @Override
    public int addCategorizedClimbToStage(int stageId, Double location, CheckpointType type, Double averageGradient,
                                          Double length) throws IDNotRecognisedException, InvalidLocationException, InvalidStageStateException,
            InvalidStageTypeException {
        if (!stages.containsKey(stageId)) {
            throw new IDNotRecognisedException("ID does not exist: " + stageId);
        }

        // Retrieve stageDetails using the stage ID:
        HashMap<String, Object> stageDetails = stages.get(stageId);

        // Ensure stage isn't waiting for results:
        StageHandler.isNotWaiting(stageDetails);
        // Ensure not time trial:
        StageHandler.isTimeTrialStage(stageDetails);

        // Retrieve stageLength, ensure location is valid:
        double stageLength = (double) stageDetails.get("stageLength");
        if (location > stageLength | location <= 0) {
            throw new InvalidLocationException("Invalid location.");
        }

        // Create checkpoint ID:
        int checkpointId = IDHandler.newId(checkpoints);
        assert checkpointId >= 0 : "Checkpoint ID must not be negative";

        // Add ID to list of IDs:
        ArrayList<Integer> checkpointIds = IDHandler.addIdToArray(checkpoints, checkpointId, stageDetails, "checkpointIds");
        stageDetails.put("checkpointIds", checkpointIds);

        // Retrieve checkpointDetails, initialise if non-existent:
        HashMap<String, Object> checkpointDetails = checkpoints.get(checkpointId);
        if (checkpointDetails == null) {
            checkpointDetails = new HashMap<>();
        }

        // Append all values to checkpointDetails:
        checkpointDetails.put("type", "climb");
        checkpointDetails.put("category", type);
        checkpointDetails.put("averageGradient", averageGradient);
        checkpointDetails.put("length", length);
        checkpointDetails.put("startLocation", (double) location - length);

        // Append this to HashMap of all existing stages:
        checkpoints.put(checkpointId, checkpointDetails);

        return checkpointId;
    }

    @Override
    public int addIntermediateSprintToStage(int stageId, double location) throws IDNotRecognisedException,
            InvalidLocationException, InvalidStageStateException, InvalidStageTypeException {
        if (!stages.containsKey(stageId)) {
            throw new IDNotRecognisedException("ID does not exist: " + stageId);
        }

        // Retrieve stageDetails using the stage ID:
        HashMap<String, Object> stageDetails = stages.get(stageId);

        // Ensure stage isn't waiting for results:
        StageHandler.isNotWaiting(stageDetails);
        // Ensure not time trial:
        StageHandler.isTimeTrialStage(stageDetails);

        // Retrieve stageLength, ensure location is valid:
        double stageLength = (double) stageDetails.get("stageLength");
        if (location > stageLength | location <= 0) {
            throw new InvalidLocationException("Invalid location.");
        }

        // Create checkpoint ID:
        int checkpointId = IDHandler.newId(checkpoints);
        assert checkpointId >= 0 : "Checkpoint ID must not be negative";

        // Add ID to list of IDs:
        ArrayList<Integer> checkpointIds = IDHandler.addIdToArray(checkpoints, checkpointId, stageDetails, "checkpointIds");
        stageDetails.put("checkpointIds", checkpointIds);

        // Retrieve checkpointDetails, initialise if non-existent:
        HashMap<String, Object> checkpointDetails = checkpoints.get(checkpointId);
        if (checkpointDetails == null) {
            checkpointDetails = new HashMap<>();
        }

        // Append all values to checkpointDetails:
        checkpointDetails.put("type", "sprint");
        checkpointDetails.put("category", null);
        checkpointDetails.put("averageGradient", null);
        checkpointDetails.put("length", location);
        checkpointDetails.put("startLocation", 0.0);

        // Append this to HashMap of all existing stages:
        checkpoints.put(checkpointId, checkpointDetails);

        return checkpointId;
    }

    @Override
    public void removeCheckpoint(int checkpointId) throws IDNotRecognisedException, InvalidStageStateException {
        if (!checkpoints.containsKey(checkpointId)) {
            throw new IDNotRecognisedException("ID does not exist: " + checkpointId);
        }

        // Get list of (all) stage(s) using said checkpoint:
        List<Integer> stageIdsWithCheckpoint = new ArrayList<>();
        for (Integer stageId : stages.keySet()) {
            HashMap<String, Object> stageDetails = stages.get(stageId);
            ArrayList<Integer> checkpointIds = (ArrayList<Integer>) stageDetails.get("checkpointIds");
            if (checkpointIds.contains(checkpointId)) {
                stageIdsWithCheckpoint.add(stageId);
            }
        }

        // Iterate through all applicable races:
        for (Integer stageId : stageIdsWithCheckpoint) {
            // Retrieve stageId, stageDetails:
            HashMap<String, Object> stageDetails = stages.get(stageId);

            // Ensure stage isn't waiting for results:
            StageHandler.isNotWaiting(stageDetails);

            // Retrieve list of checkpoints in stage, remove checkpointId:
            ArrayList<Integer> checkpointIds = (ArrayList<Integer>) stageDetails.get("checkpointIds");
            checkpointIds.remove(Integer.valueOf(checkpointId));

            // Replace checkpointIds in their original locations:
            stageDetails.replace("checkpointIds", checkpointIds);
            stages.replace(stageId, stageDetails);
        }

        // Delete the checkpoint:
        checkpoints.remove(checkpointId);
        System.out.println("\nCheckpoint removed: " + checkpointId);
    }

    @Override
    public void concludeStagePreparation(int stageId) throws IDNotRecognisedException, InvalidStageStateException {
        if (!stages.containsKey(stageId)) {
            throw new IDNotRecognisedException("ID does not exist: " + stageId);
        }
        HashMap<String, Object> stageDetails = stages.get(stageId);

        // Ensure stage isn't waiting for results:
        StageHandler.isNotWaiting(stageDetails);

        // Set waiting as true, as stage preparation is concluded:
        boolean waiting = true;
        stageDetails.replace("waiting", waiting);

    }

    @Override
    public int[] getStageCheckpoints(int stageId) throws IDNotRecognisedException {
        if (!stages.containsKey(stageId)) {
            throw new IDNotRecognisedException("ID does not exist: " + stageId);
        }
        HashMap<String, Object> stageDetails = stages.get(stageId);
        ArrayList<Integer> checkpointIds = (ArrayList<Integer>) stageDetails.get("checkpointIds");

        // Get an ArrayList for the locations of the checkpoints:
        ArrayList<Double> locations = new ArrayList<>();
        HashMap<Double, Integer> checkpointLocation = new HashMap<>();
        for (Integer checkpointId : checkpointIds) {
            HashMap<String, Object> checkpointDetails = checkpoints.get(checkpointId);
            double location = (double) checkpointDetails.get("startLocation");
            locations.add(location);
            checkpointLocation.put(location, checkpointId);
        }

        // Sort the locations ArrayList:
        Collections.sort(locations);

        int[] sortedIds = new int[locations.size()];
        for (int i = 0; i < locations.size(); i++) {
            double location = locations.get(i);
            int checkpointId = checkpointLocation.get(location);
            sortedIds[i] = checkpointId;
        }

        System.out.println("IDs: " + Arrays.toString(sortedIds));
        return sortedIds;
    }

    @Override
    public int createTeam(String name, String description) throws IllegalNameException, InvalidNameException {
        if (name == null) {
            throw new InvalidNameException("Invalid team name.");
        }

        // Iterate through list of team names to ensure no duplicate name:
        for (HashMap<String, Object> teamDetails : teams.values()) {
            String existingName = (String) teamDetails.get("name");
            if (existingName.equals(name)) {
                throw new IllegalNameException("Name already in use: " + name);
            }
        }

        HashMap<String, Object> teamDetails = new HashMap<>();
        teamDetails.put("name", name);
        teamDetails.put("description", description);
        teamDetails.put("riderIds", new ArrayList<Integer>());

        // Create the teamId:
        int teamId = IDHandler.newId(teams);
        assert teamId >= 0 : "Team ID must not be negative";

        // Put teamId with teamDetails into teams:
        teams.put(teamId, teamDetails);

        return teamId;
    }

    @Override
    public void removeTeam(int teamId) throws IDNotRecognisedException {
        if (!teams.containsKey(teamId)) {
            throw new IDNotRecognisedException("ID does not exist: " + teamId);
        }

        //Retrieve teamDetails:
        HashMap<String, Object> teamDetails = teams.get(teamId);

        //Remove from all arrays/hashmaps:
        teams.remove(teamId, teamDetails);
    }

    @Override
    public int[] getTeams() {
        return IDHandler.getIdsFromHashMap(teams);
    }

    @Override
    public int[] getTeamRiders(int teamId) throws IDNotRecognisedException {
        if (!teams.containsKey(teamId)) {
            throw new IDNotRecognisedException("ID does not exist: " + teamId);
        }

        HashMap<String, Object> teamDetails = teams.get(teamId);
        ArrayList<Integer> riderIds = (ArrayList<Integer>) teamDetails.get("riderIds");

        return IDHandler.getIdsFromArray(riderIds);
    }

    @Override
    public int createRider(int teamID, String name, int yearOfBirth)
            throws IDNotRecognisedException, IllegalArgumentException {
        // Fix error in interface that I'm not allowed to change:
        int teamId;
        teamId = teamID;

        if (!teams.containsKey(teamId)) {
            throw new IDNotRecognisedException("ID does not exist: " + teamId);
        }

        if (name == null | yearOfBirth < 1900) {
            throw new IllegalArgumentException("Invalid rider name.");
        }

        HashMap<String, Object> riderDetails = new HashMap<>();
        riderDetails.put("name", name);
        riderDetails.put("yearOfBirth", yearOfBirth);
        riderDetails.put("results", new HashMap<Integer, LocalTime[]>());

        // Create rider ID:
        int riderId = IDHandler.newId(riders);
        assert riderId >= 0 : "Rider ID must not be negative";

        // Add rider ID to team riders, update HashMap:
        HashMap<String, Object> teamDetails = teams.get(teamId);
        ArrayList<Integer> existingRiders = (ArrayList<Integer>) teamDetails.get("riderIds");
        existingRiders.add(riderId);
        teamDetails.put("riderIds", existingRiders);
        teams.put(teamId, teamDetails);

        // Put riderId with riderDetails into riders:
        riders.put(riderId, riderDetails);

        return riderId;
    }

    @Override
    public void removeRider(int riderId) throws IDNotRecognisedException {
        if (!riders.containsKey(riderId)) {
            throw new IDNotRecognisedException("ID does not exist: " + riderId);
        }

        //Retrieve riderDetails:
        HashMap<String, Object> riderDetails = riders.get(riderId);

        //Remove from all arrays/hashmaps:
        riders.remove(riderId, riderDetails);

        // Update rider results:

    }

    @Override
    public void registerRiderResultsInStage(int stageId, int riderId, LocalTime... checkpoints)
            throws IDNotRecognisedException, DuplicatedResultException, InvalidCheckpointTimesException,
            InvalidStageStateException {
        if (!stages.containsKey(stageId)) {
            throw new IDNotRecognisedException("Stage ID does not exist: " + stageId);
        }
        if (!riders.containsKey(riderId)) {
            throw new IDNotRecognisedException("Rider ID does not exist: " + riderId);
        }

        //Ensure no duplicate result is being recorded:
        HashMap<String, Object> riderDetails = riders.get(riderId);
        HashMap<Integer, LocalTime[]> resultDetails = (HashMap<Integer, LocalTime[]>) riderDetails.get("results");
        if (resultDetails.containsKey(stageId)) {
            throw new DuplicatedResultException("This rider already has results for stage" + stageId);
        }

        HashMap<String, Object> stageDetails = stages.get(stageId);
        // Ensure stage *is* waiting for results:
        StageHandler.isWaiting(stageDetails);

        ArrayList<Integer> checkpointIds = (ArrayList<Integer>) stageDetails.get("checkpointIds");
        int checkpointNum = checkpointIds.size();
        if (checkpoints.length != checkpointNum * 2) {
            throw new InvalidCheckpointTimesException("Invalid checkpoint times.");
        }

        // If no errors, apply results:
        resultDetails.put(stageId, checkpoints);

        // Add to list of all results:
        HashMap<Integer, LocalTime[]> riderResults;
        if (allResults.containsKey(stageId)) {
            riderResults = allResults.get(stageId);
        } else {
            riderResults = new HashMap<>();
        }
        riderResults.put(riderId, checkpoints);
        allResults.put(stageId, riderResults);
    }

    @Override
    public LocalTime[] getRiderResultsInStage(int stageId, int riderId) throws IDNotRecognisedException {
        if (!stages.containsKey(stageId)) {
            throw new IDNotRecognisedException("Stage ID does not exist: " + stageId);
        }
        if (!riders.containsKey(riderId)) {
            throw new IDNotRecognisedException("Rider ID does not exist: " + riderId);
        }

        HashMap<String, Object> riderDetails = riders.get(riderId);
        HashMap<Integer, LocalTime[]> resultDetails = (HashMap<Integer, LocalTime[]>) riderDetails.get("results");

        return resultDetails.get(stageId);
    }


    public LocalTime getRiderAdjustedElapsedTimeInStage(int stageId, int riderId) throws IDNotRecognisedException {
        if (!stages.containsKey(stageId)) {
            throw new IDNotRecognisedException("Stage ID does not exist: " + stageId);
        }
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

    @Override
    public void deleteRiderResultsInStage(int stageId, int riderId) throws IDNotRecognisedException {
        if (!stages.containsKey(stageId)) {
            throw new IDNotRecognisedException("Stage ID does not exist: " + stageId);
        }
        if (!riders.containsKey(riderId)) {
            throw new IDNotRecognisedException("Rider ID does not exist: " + riderId);
        }

        //Retrieve riderDetails to get all existing results::
        HashMap<String, Object> riderDetails = riders.get(riderId);
        HashMap<Integer, LocalTime> allRiderResults = (HashMap<Integer, LocalTime>) riderDetails.get("results");
        //Remove results attributed to stageId:
        allRiderResults.remove(stageId);
        //Update results:
        riderDetails.put("results", allRiderResults);
    }

    @Override
    public int[] getRidersRankInStage(int stageId) throws IDNotRecognisedException {
        if (!stages.containsKey(stageId)) {
            throw new IDNotRecognisedException("Stage ID does not exist: " + stageId);
        }

        // Rank all riders by LocalTime[] with their ID:
        HashMap<int[], LocalTime[]> riderResults = RankRiders.rank(stageId);

        // Use .iterator().next() on the keySet to retrieve the first (only) value in int[]:
        return riderResults.keySet().iterator().next();
    }

    @Override
    public LocalTime[] getRankedAdjustedElapsedTimesInStage(int stageId) throws IDNotRecognisedException {
        if (!stages.containsKey(stageId)) {
            throw new IDNotRecognisedException("Stage ID does not exist: " + stageId);
        }

        // Rank all riders by LocalTime[] with their ID:
        HashMap<int[], LocalTime[]> riderResults = RankRiders.rank(stageId);

        // Use .iterator().next() on the values to retrieve the first (only) value in LocalTime[]:
        return riderResults.values().iterator().next();
    }

    @Override
    public int[] getRidersPointsInStage(int stageId) throws IDNotRecognisedException {
        if (!stages.containsKey(stageId)) {
            throw new IDNotRecognisedException("Stage ID does not exist: " + stageId);
        }

        // Get the ranked list of riders for the stage
        int[] rankedRiders = getRidersRankInStage(stageId);

        // Get stageDetails to get the type of stage:
        HashMap<String, Object> stageDetails = stages.get(stageId);
        StageType stageType = (StageType) stageDetails.get("stageType");

        // Pass both of these through to PointsHandler:
        return PointsHandler.getStagePoints(rankedRiders, stageType);
    }

    @Override
    public int[] getRidersMountainPointsInStage(int stageId) throws IDNotRecognisedException {
        // Initialise necessary maps/arrays:
        HashMap<String, Object> stageDetails = stages.get(stageId);
        ArrayList<Integer> checkpointIds = (ArrayList<Integer>) stageDetails.get("checkpointIds");
        HashMap<Integer, LocalTime[]> results = allResults.get(stageId);

        HashMap<Integer, int[]> mountainPoints = new HashMap<>(); // Store mountain points for each stage

        for (int checkpointId : checkpointIds) {
            HashMap<String, Object> checkpointDetails = checkpoints.get(checkpointId);
            String type = (String) checkpointDetails.get("type");
            if (type.equals("climb")) {
                int timesToCheck = (checkpointId * 2) - 1;
                HashMap<Integer, Long> riderTimes = new HashMap<>(); // Create a new map for each checkpoint
                for (Integer riderId : results.keySet()) {
                    LocalTime[] times = results.get(riderId);
                    LocalTime startTime = times[timesToCheck - 1];
                    LocalTime endTime = times[timesToCheck];
                    Duration duration = Duration.between(startTime, endTime);
                    long seconds = duration.getSeconds(); // Get the duration in seconds
                    riderTimes.put(riderId, seconds);
                }

                // Convert riderTimes HashMap to a list of entries:
                List<Map.Entry<Integer, Long>> entries = new ArrayList<>(riderTimes.entrySet());

                // Sort the list based on the value (seconds):
                entries.sort(Map.Entry.comparingByValue());

                // Extract sorted riderIds:
                int[] riderIds = new int[riderTimes.size()];
                int index = 0;
                for (Map.Entry<Integer, Long> entry : entries) {
                    Integer riderId = entry.getKey();
                    riderIds[index++] = riderId;
                }

                // get checkpointType, send to PointsHandler.getMountainPoints with riderIds:
                CheckpointType checkpointType = (CheckpointType) checkpointDetails.get("category");
                int[] newMountainPoints = PointsHandler.getMountainPoints(riderIds, checkpointType);

                // Store points, or add to already stored mountain points if applicable:
                if (mountainPoints.containsKey(stageId)) {
                    int[] currentPoints = mountainPoints.get(stageId);
                    for (int i = 0; i < currentPoints.length; i++) {
                        currentPoints[i] += newMountainPoints[i];
                    }
                    mountainPoints.put(stageId, currentPoints);
                } else {
                    mountainPoints.put(stageId, newMountainPoints);
                }
            }
        }
        System.out.println("Mountain points for stage " + stageId + ": " + Arrays.toString(mountainPoints.get(stageId)));

        // Return overall points for all mountain checkpoints:
        return mountainPoints.get(stageId);
    }

    @Override
    public void eraseCyclingPortal() {
        //Erase all data:
        races.clear();
        stages.clear();
        checkpoints.clear();
        teams.clear();
        riders.clear();
        allResults.clear();

        System.out.println("Cycling portal erased.");
    }

    @Override
    public void saveCyclingPortal(String filename) throws IOException {
        try (FileOutputStream fileOutputStream = new FileOutputStream(filename);
             ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream)) {

            // Write the objects to the file
            objectOutputStream.writeObject(races);
            objectOutputStream.writeObject(stages);
            objectOutputStream.writeObject(checkpoints);
            objectOutputStream.writeObject(teams);
            objectOutputStream.writeObject(riders);
            objectOutputStream.writeObject(allResults);
            System.out.println("Cycling portal saved to file: " + filename);
        }
    }

    @Override
    public void loadCyclingPortal(String filename) throws IOException, ClassNotFoundException {
        try (FileInputStream fileInputStream = new FileInputStream(filename);
             ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream)) {

            // Load objects from the file, replace in current memory:
            races = (HashMap<Integer, HashMap<String, Object>>) objectInputStream.readObject();
            stages = (HashMap<Integer, HashMap<String, Object>>) objectInputStream.readObject();
            checkpoints = (HashMap<Integer, HashMap<String, Object>>) objectInputStream.readObject();
            teams = (HashMap<Integer, HashMap<String, Object>>) objectInputStream.readObject();
            riders = (HashMap<Integer, HashMap<String, Object>>) objectInputStream.readObject();
            allResults = (HashMap<Integer, HashMap<Integer, LocalTime[]>>) objectInputStream.readObject();

            System.out.println("Cycling portal loaded from file: " + filename);
        }
    }
}