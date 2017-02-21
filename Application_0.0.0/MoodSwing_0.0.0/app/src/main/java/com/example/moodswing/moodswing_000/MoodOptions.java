package com.example.moodswing.moodswing_000;

import android.graphics.Color;

/**
 * Created by Fred on 2017-02-18.
 */

//This will likely end up being implemented in a sub-class or inside a controller.
public class MoodOptions {
    private EmotionalState emotionalStates[] = new EmotionalState[8];
    private SocialSituation socialSituations[] = new SocialSituation[3];

    public MoodOptions(){
        emotionalStates[0] = new EmotionalState("anger", "somewhere", Color.BLACK);
        emotionalStates[1] = new EmotionalState("confusion", "somewhere", Color.BLUE);
        emotionalStates[2] = new EmotionalState("disgust", "somewhere", Color.CYAN);
        emotionalStates[3] = new EmotionalState("fear", "somewhere", Color.GRAY);
        emotionalStates[4] = new EmotionalState("happiness", "@/drawable/emoticonHappy", Color.GREEN);
        emotionalStates[5] = new EmotionalState("sadness", "@/drawable/emoticonSad", Color.MAGENTA);
        emotionalStates[6] = new EmotionalState("shame", "somewhere", Color.RED);
        emotionalStates[7] = new EmotionalState("surprise", "@/drawable/emoticonSurprised", Color.YELLOW);

        socialSituations[0] = new SocialSituation("alone", "@/drawable/socialSituationAlone");
        socialSituations[1] = new SocialSituation("crowd", "@/drawable/socialSituationCrowd");
        socialSituations[2] = new SocialSituation("party", "@/drawable/socialSituationParty");
    }
}
