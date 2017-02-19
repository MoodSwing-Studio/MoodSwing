package com.example.moodswing.moodswing_000;

import java.security.InvalidParameterException;
import java.util.ArrayList;

/**
 * Created by Fred on 2017-02-18.
 */

public class FollowingList {
    private ArrayList<Participant> pending = new ArrayList<Participant>();
    private ArrayList<Participant> following = new ArrayList<Participant>();
    //TODO: implement blocking if time allows. This is beyond the project specification.
    //this collection will not have a getter.
    private ArrayList<Participant> blocked = new ArrayList<Participant>();

    public FollowingList(){}

    public void addParticipant(Participant participant){
        if(pending.contains(participant) || following.contains(participant)){
            throw new InvalidParameterException();
        }
        //TODO: create new follow request
        //participant.requestFollow(); //need to pass requesting participant
        pending.add(participant);
    }

    public boolean removeParticipant(Participant participant){
        return(pending.remove(participant) | following.remove(participant));
    }

    public boolean declineParticipant(Participant participant){
        return removeParticipant(participant);
    }

    public void approveParticipant(Participant participant){
        if(pending.contains(participant)){
            pending.remove(participant);
            if(following.contains(participant)){
                throw new InvalidParameterException();
            }
            else{
                following.add(participant);
            }
        }
        else{
            throw new InvalidParameterException();
        }
    }

    public ArrayList<Participant> getPending() {
        return pending;
    }

    public ArrayList<Participant> getFollowing() {
        return following;
    }
}
