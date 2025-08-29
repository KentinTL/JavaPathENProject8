package com.openclassrooms.tourguide;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import gpsUtil.location.Location;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import gpsUtil.GpsUtil;
import gpsUtil.location.Attraction;
import gpsUtil.location.VisitedLocation;
import rewardCentral.RewardCentral;
import com.openclassrooms.tourguide.helper.InternalTestHelper;
import com.openclassrooms.tourguide.service.RewardsService;
import com.openclassrooms.tourguide.service.TourGuideService;
import com.openclassrooms.tourguide.user.User;
import com.openclassrooms.tourguide.user.UserReward;

public class TestRewardsService {
	private RewardsService rewardsService;

	@BeforeEach
	public void setup() {
		rewardsService = new RewardsService(null, null); // on passe null pour gpsUtil et RewardCentral ici si inutilisés
	}

	@Test
	public void userGetRewards() {
		GpsUtil gpsUtil = new GpsUtil();
		RewardsService rewardsService = new RewardsService(gpsUtil, new RewardCentral());

		InternalTestHelper.setInternalUserNumber(0);
		TourGuideService tourGuideService = new TourGuideService(gpsUtil, rewardsService);

		User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
		Attraction attraction = gpsUtil.getAttractions().get(0);
		user.addToVisitedLocations(new VisitedLocation(user.getUserId(), attraction, new Date()));
		tourGuideService.trackUserLocation(user);
		List<UserReward> userRewards = user.getUserRewards();
		tourGuideService.tracker.stopTracking();
		assertTrue(userRewards.size() == 1);
	}

	@Test
	public void isWithinAttractionProximity() {
		GpsUtil gpsUtil = new GpsUtil();
		RewardsService rewardsService = new RewardsService(gpsUtil, new RewardCentral());
		Attraction attraction = gpsUtil.getAttractions().get(0);
		assertTrue(rewardsService.isWithinAttractionProximity(attraction, attraction));
	}

	@Test
	public void nearAllAttractions() {

		GpsUtil gpsUtil = new GpsUtil();
		RewardsService rewardsService = new RewardsService(gpsUtil, new RewardCentral());
		rewardsService.setProximityBuffer(Integer.MAX_VALUE);

		InternalTestHelper.setInternalUserNumber(1);
		TourGuideService tourGuideService = new TourGuideService(gpsUtil, rewardsService);

		rewardsService.calculateRewards(tourGuideService.getAllUsers().get(0));
		List<UserReward> userRewards = tourGuideService.getUserRewards(tourGuideService.getAllUsers().get(0));
		tourGuideService.tracker.stopTracking();

		assertEquals(gpsUtil.getAttractions().size(), userRewards.size());
	}

	@Test
	public void testFindClosestAttractions() {
		Location userLocation = new Location(40.0, -75.0);

		Attraction a1 = new Attraction("Attraction1", "City1", "State1", 40.0, -75.0); // distance 0
		Attraction a2 = new Attraction("Attraction2", "City2", "State2", 41.0, -75.0); // plus loin
		Attraction a3 = new Attraction("Attraction3", "City3", "State3", 39.0, -75.0); // plus loin
		Attraction a4 = new Attraction("Attraction4", "City4", "State4", 40.0, -76.0);
		Attraction a5 = new Attraction("Attraction5", "City5", "State5", 42.0, -75.0);
		Attraction a6 = new Attraction("Attraction6", "City6", "State6", 38.0, -75.0);

		List<Attraction> attractions = Arrays.asList(a6, a3, a5, a2, a4, a1);

		List<Attraction> closest = rewardsService.findClosestAttractions(userLocation, attractions, 5);

		assertEquals(5, closest.size());
		assertEquals("Attraction1", closest.get(0).attractionName);
	}
}
