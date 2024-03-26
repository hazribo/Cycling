package cycling;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class IDHandler {
    public static int newId(HashMap<Integer, HashMap<String, Object>> category) {
        // Initialize variable to track the last entry:
        HashMap.Entry<Integer, HashMap<String, Object>> lastEntry = null;
        // Iterate through the category to get its last entry:
        for (HashMap.Entry<Integer, HashMap<String, Object>> entry : category.entrySet()) {
            lastEntry = entry;
        }

        // Get the id from the last entry:
        int id;
        if (lastEntry != null) {
            int lastId = lastEntry.getKey();
            id = lastId + 1;
        } else {
            id = 1;
        }

        return id;
    }
    public static ArrayList<Integer> addIdToArray(HashMap<Integer, HashMap<String, Object>> category, int id, HashMap<String, Object> stageDetails, String categoryString) {
        ArrayList<Integer> allIds = (ArrayList<Integer>) stageDetails.get(categoryString);
        allIds.add(id);
        System.out.println(allIds);

        return allIds;
    }
    public static int[] getIdsFromHashMap(HashMap<Integer, HashMap<String, Object>> category) {
        ArrayList<Integer> allIds = new ArrayList<>(category.keySet());
        int[] ids = new int[allIds.size()];
        for (int i = 0; i < allIds.size(); i++) {
            ids[i] = allIds.get(i);
        }
        System.out.println(Arrays.toString(ids));
        return ids;
    }
    public static int[] getIdsFromArray(ArrayList<Integer> category) {
        int[] ids = new int[category.size()];
        for (int i = 0; i < category.size(); i++) {
            ids[i] = category.get(i);
        }
        System.out.println(Arrays.toString(ids));
        return ids;
    }
}
