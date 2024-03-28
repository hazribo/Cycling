package cycling;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * StageHandler covers the functions for checking if
 * a stage is waiting or not,
 * checking if a stage is a time trial stage,
 * and clearing a stage and all accompanying checkpoints/results.
 *
 * @author Harry Gardner
 */
public class StageHandler extends CyclingPortalImpl {
    public static void isNotWaiting(HashMap<String, Object> stageDetails) throws InvalidStageStateException {
        boolean waiting = (boolean) stageDetails.get("waiting");
        if (waiting) {
            throw new InvalidStageStateException("Invalid: stage is not waiting for results.");
        }
    }
    public static void isWaiting(HashMap<String, Object> stageDetails) throws InvalidStageStateException {
        boolean waiting = (boolean) stageDetails.get("waiting");
        if (!waiting) {
            throw new InvalidStageStateException("Invalid: stage is not waiting for results.");
        }
    }
    public static void isTimeTrialStage(HashMap<String, Object> stageDetails) throws InvalidStageTypeException {
        StageType stageType = (StageType) stageDetails.get("stageType");
        if (stageType == StageType.TT) {
            throw new InvalidStageTypeException("Invalid: Categorized Climbs cannot be added to Time-trial stages.");
        }
    }
    public static void clearStage(int stageId) {
        HashMap<String, Object> stageDetails = stages.get(stageId);
        // Use stageDetails to get all checkpointIds:
        ArrayList<Integer> checkpointIds = (ArrayList<Integer>) stageDetails.get("checkpointIds");
        // Iterate through checkpoints, delete all:
        for (int checkpointId : checkpointIds) {
            checkpoints.remove(checkpointId);
        }
        // Remove results from allResults:
        allResults.remove(stageId);

        // Finally, delete the stage:
        stages.remove(stageId);
    }
}

