package cycling;

import java.util.HashMap;

public class StageHandler {
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
}

