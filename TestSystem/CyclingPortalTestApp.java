import cycling.*;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;

/**
 * A short program to illustrate an app testing some minimal functionality of a
 * concrete implementation of the CyclingPortal interface -- note you
 * will want to increase these checks, and run it on your CyclingPortalImpl class
 * (not the BadCyclingPortal class).
 *
 * 
 * @author Diogo Pacheco
 * @version 2.0
 */
public class CyclingPortalTestApp {

	/**
	 * Test method.Á
	 * 
	 * @param args not used
	 */
	public static void main(String[] args) throws InvalidNameException, IllegalNameException, IDNotRecognisedException {
		System.out.println("The system compiled and started the execution...");

		// TODO replace BadMiniCyclingPortalImpl with CyclingPortalImpl
		CyclingPortalImpl portal1 = new CyclingPortalImpl();
		MiniCyclingPortal portal2 = new CyclingPortalImpl();

        assert (portal1.getRaceIds().length == 0)
				: "Initial Portal not empty as required or not returning an empty array.";
		assert (portal1.getTeams().length == 0)
				: "Initial Portal not empty as required or not returning an empty array.";


        assert (portal1.getTeams().length == 1)
				: "Portal1 should have one team.";

		assert (portal2.getTeams().length == 1)
				: "Portal2 should have one team.";

		try {
			// Create a race
			int raceId = portal1.createRace("Tour de France", "The most famous cycling race in the world");
			System.out.println("Race created with ID: " + raceId);

			// Add a stage to the race
			int stageId = portal1.addStageToRace(raceId, "Mountain Stage", "A challenging mountain stage", 150.5,
					LocalDateTime.now(), StageType.MEDIUM_MOUNTAIN);
			System.out.println("Stage added to race with ID: " + stageId);

			// Add checkpoints to the stage
			int climbCheckpointId = portal1.addCategorizedClimbToStage(stageId, 50.0, CheckpointType.HC, 8.0,
					10.0);
			System.out.println("Checkpoint created with ID: " + climbCheckpointId);
			int climbCheckpointId2 = portal1.addCategorizedClimbToStage(stageId, 70.0, CheckpointType.C1, 8.0,
					15.0);
			System.out.println("Checkpoint created with ID: " + climbCheckpointId2);
			int sprintCheckpointId = portal1.addIntermediateSprintToStage(stageId, 100.0);
			System.out.println("Checkpoint created with ID: " + sprintCheckpointId);

			// Get race stages
			int[] raceStages = portal1.getRaceStages(raceId);
			System.out.println("Stages in race: " + Arrays.toString(raceStages));

			// Get stage length
			double stageLength = portal1.getStageLength(stageId);
			System.out.println("Stage length: " + stageLength + " km");

			// View race details
			String raceDetails = portal1.viewRaceDetails(raceId);
			System.out.println("Race details: " + raceDetails);

			// Create a team
			int teamId = portal1.createTeam("Team Sky", "Professional cycling team");
			System.out.println("Team created with ID: " + teamId);

			// Create a rider
			int riderId = portal1.createRider(teamId, "Chris Froome", 1985);
			System.out.println("Rider created with ID: " + riderId);

			System.out.println(Arrays.toString(portal1.getTeamRiders(teamId)));
			// Create another rider
			int riderId2 = portal1.createRider(teamId, "Chris Noome", 1985);
			System.out.println("Rider created with ID: " + riderId2);

			System.out.println(Arrays.toString(portal1.getTeamRiders(teamId)));
			// Create another rider
			int riderId3 = portal1.createRider(teamId, "Chris Boome", 1985);
			System.out.println("Rider created with ID: " + riderId3);

			// Remove rider results in stage
			portal1.deleteRiderResultsInStage(stageId, riderId);
			System.out.println("Results for rider in stage deleted");

			portal1.concludeStagePreparation(stageId);

			// Register rider results in stage
			portal1.registerRiderResultsInStage(stageId, riderId2, LocalTime.of(8, 0, 33), LocalTime.of(8, 28, 34),
					LocalTime.of(10, 0, 2), LocalTime.of(12, 31, 6), LocalTime.of(13, 31, 6), LocalTime.of(14, 38, 6));
			System.out.println("Results registered for rider in stage");

			// Register rider results in stage
			portal1.registerRiderResultsInStage(stageId, riderId, LocalTime.of(8, 0, 32), LocalTime.of(8, 30, 57),
					LocalTime.of(10, 0, 1), LocalTime.of(12, 45, 7), LocalTime.of(13, 31, 6), LocalTime.of(14, 37, 6));
			System.out.println("Results registered for rider in stage");

			// Register rider results in stage
			portal1.registerRiderResultsInStage(stageId, riderId3, LocalTime.of(8, 0, 34), LocalTime.of(8, 29, 34),
					LocalTime.of(10, 0, 3), LocalTime.of(12, 37, 6), LocalTime.of(13, 31, 6), LocalTime.of(14, 36, 6));
			System.out.println("Results registered for rider in stage");

			// Get rider results in stage
			LocalTime[] riderResults = portal1.getRiderResultsInStage(stageId, riderId);
			System.out.println("Results for rider in stage: " + Arrays.toString(riderResults));

			// Get rider results in stage
			LocalTime[] riderResults2 = portal1.getRiderResultsInStage(stageId, riderId2);
			System.out.println("Results for rider in stage: " + Arrays.toString(riderResults2));

			// Get rider results in stage
			LocalTime[] riderResults3 = portal1.getRiderResultsInStage(stageId, riderId3);
			System.out.println("Results for rider in stage: " + Arrays.toString(riderResults3));

			// Get ranked adjusted elapsed times in stage
			LocalTime[] rankedAdjustedElapsedTimes = portal1.getRankedAdjustedElapsedTimesInStage(stageId);
			System.out.println("Ranked adjusted elapsed times in stage: " + Arrays.toString(rankedAdjustedElapsedTimes));

			int[] results = portal1.getRidersRankInStage(stageId);
			System.out.println("Riders rank in stage: " + Arrays.toString(results));

			// Save cycling portal to file
			portal1.saveCyclingPortal("cycling_portal_data.ser");

			portal1.getRaceIds();
			portal1.getTeams();

			int[] points = portal1.getRidersPointsInStage(stageId);
			System.out.println("Points in stage " + stageId + ": " + Arrays.toString(points));

			portal1.getRidersMountainPointsInStage(stageId);

			// Remove team
			portal1.removeTeam(teamId);
			System.out.println("Team removed: " + teamId);

			// Remove race by ID
			portal1.removeRaceById(raceId);
			System.out.println("Race removed: " + raceId);

			portal1.getRaceIds();
			portal1.getTeams();

			// Load cycling portal from file
			portal1.loadCyclingPortal("cycling_portal_data.ser");

			portal1.getRaceIds();
			portal1.getTeams();

			// Erase cycling portal
			portal1.eraseCyclingPortal();

			portal1.getRaceIds();
			portal1.getTeams();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
