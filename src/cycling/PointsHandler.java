package cycling;

/**
 * PointsHandler covers the functions for
 * allocating points depending on the StageType
 * and allocating points for each mountain stage,
 * depending on the CheckpointType.
 *
 * @author Harry Gardner
 */

public class PointsHandler extends CyclingPortalImpl {
    public static int[] getStagePoints(int[] riderIds, StageType stageType) {
        // Create new array for riders points:
        int[] points = new int[riderIds.length];
        int[] pointAllocation;

        // Retrieve the correct point allocation depending on stageType:
        if (stageType == StageType.FLAT) {
            pointAllocation = new int[]{50, 30, 20, 18, 16, 14, 12, 10, 8, 7, 6, 5, 4, 3, 2};
        }
        else if (stageType == StageType.MEDIUM_MOUNTAIN) {
            pointAllocation = new int[]{30, 25, 22, 19, 17, 15, 13, 11, 9, 7, 6, 5, 4, 3, 2};
        }
        else if (stageType == StageType.HIGH_MOUNTAIN || stageType == StageType.TT) {
            pointAllocation = new int[]{20, 17, 15, 13, 11, 10, 9, 8, 7, 6, 5, 4, 3, 2, 1};
        }
        else {
            throw new IllegalArgumentException("stageType not valid.");
        }

        // Iterate through all rider IDs, allocate points if applicable:
        for (int i = 0; i < riderIds.length; i++) {
            if (i <= 15) {
                points[i] = pointAllocation[i];
            } else {
                points[i] = 0;
            }
        }

        return points;
    }
    public static int[] getMountainPoints(int[] riderIds, CheckpointType checkpointType) {
        // Create new array for riders points:
        int[] points = new int[riderIds.length];
        int[] pointAllocation;

        // Retrieve the correct point allocation depending on checkpointType:
        if (checkpointType == CheckpointType.HC) {
            pointAllocation = new int[]{20, 15, 12, 10, 8, 6, 4, 2};
        } else if (checkpointType == CheckpointType.C1) {
            pointAllocation = new int[]{10, 8, 6, 4, 2, 1, 0, 0};
        } else if (checkpointType == CheckpointType.C2) {
            pointAllocation = new int[]{5, 3, 2, 1, 0, 0, 0, 0};
        } else if (checkpointType == CheckpointType.C3) {
            pointAllocation = new int[]{2, 1, 0, 0, 0, 0, 0, 0};
        } else if (checkpointType == CheckpointType.C4) {
            pointAllocation = new int[]{1, 0, 0, 0, 0, 0, 0, 0};
        } else {
            throw new IllegalArgumentException("checkpointType not valid.");
        }

        // Iterate through all rider IDs, allocate points if applicable:
        for (int i = 0; i < riderIds.length; i++) {
            if (i <= 8) {
                points[i] = pointAllocation[i];
            } else {
                points[i] = 0;
            }
        }

        return points;
    }
}