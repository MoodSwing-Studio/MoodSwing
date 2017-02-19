package com.example.moodswing.moodswing_000;

import android.graphics.Picture;
import android.location.Location;

import java.util.Date;

/**
 * Created by Fred on 2017-02-18.
 */

public class MoodEvent {
    private EmotionalState emotionalState;
    private Date date;
    private String trigger;
    private SocialSituation socialSituation;
    private String photoLocation;
    private Location location;

    //pass null for unused parameters
    //TODO: finish handling of null (or empty) inputs
    public MoodEvent(EmotionalState emotionalState, String trigger, SocialSituation socialSituation,
                     String photoLocation, Location location) {
        this.emotionalState = emotionalState;
        //automatically selects current date
        this.date = new Date();
        if(trigger != null) {
            this.trigger = trigger;
        }
        else{
            this.trigger = "";
        }
        this.socialSituation = socialSituation;
        this.photoLocation = photoLocation;
        this.location = location;
    }

    public EmotionalState getEmotionalState() {
        return emotionalState;
    }

    public void setEmotionalState(EmotionalState emotionalState) {
        this.emotionalState = emotionalState;
    }

    public Date getDate() {
        return date;
    }

    public String getTrigger() {
        return trigger;
    }

    public void setTrigger(String trigger) {
        this.trigger = trigger;
    }

    public SocialSituation getSocialSituation() {
        return socialSituation;
    }

    public void setSocialSituation(SocialSituation socialSituation) {
        this.socialSituation = socialSituation;
    }

    public String getPhotoLocation() {
        return photoLocation;
    }

    public void setPhotoLocation(String photoLocation) {
        this.photoLocation = photoLocation;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }
}
