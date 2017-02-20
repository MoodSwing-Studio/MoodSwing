package com.example.moodswing.moodswing_000;

import java.util.ArrayList;

/**
 * Created by Fred on 2017-02-18.
 */

public class Participant extends User {
    private FollowingList followingList = new FollowingList();
    private FollowerList followerList = new FollowerList();

    private ArrayList<MoodEvent> moodEvents = new ArrayList<>();


    public Participant(String username){
        this.username = username;
    }


    // --- MoodEvent methods

    public void addMoodEvent(EmotionalState emotionalState, String trigger, SocialSituation socialSituation,
                             String photoLocation, boolean saveLocation) {
        moodEvents.add(new MoodEvent(emotionalState, trigger, socialSituation, photoLocation, saveLocation));
    }

    // --- end MoodEvent methods


    // --- Following methods ---

    public void followParticipant(Participant receivingParticipant){
        followingList.followParticipant(receivingParticipant, this);
    }

    public void followRequestApproved(Participant receivingParticipant){
        followingList.approveFollowRequest(receivingParticipant);
    }

    public void cancelFollowRequest(Participant receivingParticipant){
        followingList.removeParticipant(receivingParticipant);
    }

    // --- end Following methods ---


    // --- Follower methods ---

    public void createFollowerRequest(Participant requestingParticipant){
        followerList.createRequest(requestingParticipant, this);
    }

    public void approveFollowerRequest(Participant requestingParticipant){
        followerList.confirmRequest(requestingParticipant, this);
    }

    public void declineFollowerRequest(Participant requestingParticipant){
        followerList.declineRequest(requestingParticipant, this);
    }

    public void removeFollowerRequest(Participant requestingParticipant){
        followerList.removeFollowRequest(requestingParticipant);
    }

    // --- end Follower methods ---


    // --- getters and setters

    public ArrayList<Participant> getPendingFollowing() {
        return followingList.getPending();
    }

    public ArrayList<Participant> getFollowing() {
        return followingList.getFollowing();
    }

    public ArrayList<Participant> getPendingFollowers() {
        return followerList.getPending();
    }

    public ArrayList<Participant> getFollowers() {
        return followerList.getFollowers();
    }

    // --- end getters and setters
}
