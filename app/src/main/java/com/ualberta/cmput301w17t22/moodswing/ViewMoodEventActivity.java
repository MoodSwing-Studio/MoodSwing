package com.ualberta.cmput301w17t22.moodswing;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


/**
 * Activity is launched when app user chooses to view a mood event. Launched from both
 * MoodHistoryActivity to view a mood event in the mood history, and from MainActivity, to view
 * a mood event in the mood feed.
 */
public class ViewMoodEventActivity extends AppCompatActivity implements MSView<MoodSwing> {

    /** The position of the mood event being viewed in the participant's mood history. */
    private int position;

    /** TextView that holds the user's username. */
    TextView usernameTextView;

    /** TextView that holds the mood event's emotional state. */
    TextView emotionalStateTextView;

    /** TextView that holds the mood event's social situation. */
    TextView socialSituationTextView;

    /** TextView that holds the mood event's trigger text. */
    TextView triggerTextView;
    TextView triggerPrefixTextView;

    /** ImageView that holds the appropriate image for the emotional state. */
    ImageView emotionalStateImageView;

    /** ImageView that holds the appropriate image for the social situation. */
    ImageView socialSituationImageView;

    /** ImageView that holds the appropriate image for the user uploaded image. */
    ImageView imageImageView;

    /** The edit button that allows the user to edit the mood event if it is their own. */
    Button editMoodEventButton;

    /** The button that allows the user to delete the mood event if it is their own. */
    Button deleteMoodEventButton;

    /** The mainParticipant (current user) of the app. */
    Participant mainParticipant;

    /** The mood event currently being viewed.*/
    MoodEvent moodEvent;

    /** Boolean we use to check onDestroy whether the mood event being viewed was chosen to be
     * deleted. */
    boolean delete;

    /**
     * Initialize all the widgets for the Activity, and add this View to the main Model's list of
     * Views.
     */
    public void initialize(){

        // Initialize the text views.
        usernameTextView =
                (TextView) findViewById(R.id.usernameTextView_ViewMoodEventActivity);
        emotionalStateTextView =
                (TextView) findViewById(R.id.emotionalStateTextView_ViewMoodEventActivity);
        socialSituationTextView =
                (TextView) findViewById(R.id.socialSituationTextView_ViewMoodEventActivity);
        triggerTextView =
                (TextView) findViewById(R.id.triggerTextView_ViewMoodEventActivity);

        triggerPrefixTextView =
                (TextView) findViewById(R.id.triggerPrefixTextView);

        // Initialize the image views.
        emotionalStateImageView =
                (ImageView) findViewById(R.id.emotionalStateImageView_ViewMoodEventActivity);
        socialSituationImageView =
                (ImageView) findViewById(R.id.socialSituationImageView_ViewMoodEventActivity);
        imageImageView =
                (ImageView) findViewById(R.id.imageImageView_ViewMoodEventActivity);

        // Initialize the buttons.
        editMoodEventButton =
                (Button) findViewById(R.id.editMoodEventButton_ViewMoodEventActivity);
        deleteMoodEventButton =
                (Button) findViewById(R.id.deleteMoodEventButton_ViewMoodEventActivity);

        // Add this View to the main Model class.
        MoodSwingController moodSwingController = MoodSwingApplication.getMoodSwingController();
        moodSwingController.addView(this);
    }
    /** Called when the activity is first created.
     * In this, we get the position of the mood event, get the mood event itself, and initialize
     * all of the widgets.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_mood_event);

        // Always initialize delete to equal false when viewing a mood event for the first time.
        delete = false;

        // Initialize all the widgets, and add this View to the main Model class.
        initialize();
    }

    /**
     * This runs when the activity is returned to. i.e. from the EditMoodEventActivity.
     * In this, we convert the moodEventJson into a MoodEvent object, and load in all the
     * appropriate values and display them on the activity.
     */
    protected void onStart(){
        super.onStart();

        // Load the mood event based on the position stored in the main model class's
        // moodHistoryPosition variable.
        loadFromMoodSwing();

        // Load the values from the mood event being viewed into the widgets of the activity.
        loadFromMoodEvent();

        // Check if the user own's this mood event, and dissapear or appear the edit and
        // delete buttons appropriately.
        if (moodEvent.getOriginalPoster().equals(mainParticipant.getUsername())) {
            editMoodEventButton.setVisibility(View.VISIBLE);
            deleteMoodEventButton.setVisibility(View.VISIBLE);
        } else {
            editMoodEventButton.setVisibility(View.GONE);
            deleteMoodEventButton.setVisibility(View.GONE);
        }

        // Edit button press, starts the EditMoodEventActivity.
        editMoodEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Launch the ViewMoodEventActivity.
                Intent intent = new Intent(ViewMoodEventActivity.this, EditMoodEventActivity.class);
                startActivity(intent);
            }
        });

        // Delete button press. Launches a dialog box that asks the user to confirm deletion,
        // then deletes the mood event from the participants mood history.
        deleteMoodEventButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick (View v) {
                // Create the alert dialog builder with the right theme.
                AlertDialog.Builder adb =
                        new AlertDialog.Builder(ViewMoodEventActivity.this, R.style.DialogTheme);

                // Set the message and the title.
                adb.setMessage("Are you sure you want to delete this Mood Event?")
                        .setTitle("Delete Mood Event?")
                        .setCancelable(true);

                // Create the positive button for the alert dialog.
                adb.setPositiveButton("Confirm", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which){
                        // Set delete to true and close the activity.
                        delete = true;
                        finish();
                    }
                });

                // Create the negative button for the alert dialog.
                adb.setNegativeButton("Cancel", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which){
                        // Do nothing, cancel the dialog box.
                        dialog.cancel();
                    }
                });

                // Build and show the dialog box.
                AlertDialog dialog = adb.create();
                dialog.show();
            }
        });
    }

    /**
     * Called when the Activity is finish()'d or otherwise closes. Removes this View from the main
     * Model's list of Views.
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Remove this View from the main Model class' list of Views.
        MoodSwingController moodSwingController = MoodSwingApplication.getMoodSwingController();
        moodSwingController.removeView(this);

        // Delete the mood event if delete is true.
        if (delete) {
            // Delete the mood event.
            moodSwingController.deleteMoodEventFromMainParticipantByPosition(position);
            // Inform the user that the mood event was deleted.
            Toast.makeText(ViewMoodEventActivity.this,
                    "Mood Event deleted!",
                    Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Function that loads the needed information for viewing the mood event from the
     * main model class MoodSwing.
     */
    private void loadFromMoodSwing() {
        // Get the moodSwingController from the application class.
        MoodSwingController moodSwingController = MoodSwingApplication.getMoodSwingController();

        // Get the main participant from the model.
        mainParticipant = moodSwingController.getMainParticipant();

        // If this activity was launched from the MoodHistoryActivity, get the position
        // and mood event from there.

        if (getIntent().getStringExtra("MoodListType").equals("MoodHistory")) {
            position = moodSwingController.getMoodHistoryPosition();
            moodEvent = mainParticipant.getMoodHistory().get(position);
            Log.i("MoodSwing", String.valueOf(moodEvent.getEmotionalState().getDrawableId()));

            // If this activity was launched from the MainActivity, get the position
            // and mood event from the mood feed instead.
        } else if (getIntent().getStringExtra("MoodListType").equals("MoodFeed")) {
            position = moodSwingController.getMoodFeedPosition();
            moodEvent = moodSwingController.getMoodFeed().get(position);
        }
    }

    /**
     * Loads the values from the mood event being viewed into the widgets of the activity.
     */
    private void loadFromMoodEvent() {
        Log.i("MoodSwing", String.valueOf(moodEvent.getEmotionalState().getDrawableId()));
        // Load values from MoodEvent into the simple text fields.
        usernameTextView.setText(moodEvent.getOriginalPoster());

        if (moodEvent.getTrigger().isEmpty()) {
            //Toast.makeText(ViewMoodEventActivity.this, "empty", Toast.LENGTH_SHORT).show();
            triggerTextView.setVisibility(View.GONE);
            triggerPrefixTextView.setVisibility(View.GONE);
        } else {
            triggerTextView.setText(moodEvent.getTrigger());
            triggerTextView.setVisibility(View.VISIBLE);
            triggerPrefixTextView.setVisibility(View.VISIBLE);
            //Toast.makeText(ViewMoodEventActivity.this, moodEvent.getTrigger(), Toast.LENGTH_SHORT).show();
            //triggerTextView.setTextColor(getResources().getColor(R.color.colorPrimary));
        }


        // Set the image and text for the appropriate Mood Event.
        emotionalStateTextView.setText(moodEvent.getEmotionalState().getDescription());
        switch (moodEvent.getEmotionalState().getDescription()) {
            case "Anger":
                emotionalStateTextView.setTextColor(getResources().getColor(R.color.angry));
                break;
            case "Sadness":
                emotionalStateTextView.setTextColor(getResources().getColor(R.color.sad));
                break;
            case "Disgust":
                emotionalStateTextView.setTextColor(getResources().getColor(R.color.disgusted));
                break;
            case "Shame":
                emotionalStateTextView.setTextColor(getResources().getColor(R.color.ashamed));
                break;
            case "Happiness":
                emotionalStateTextView.setTextColor(getResources().getColor(R.color.happy));
                break;
            case "Surprise":
                emotionalStateTextView.setTextColor(getResources().getColor(R.color.surprised));
                break;
            case "Confusion":
                emotionalStateTextView.setTextColor(getResources().getColor(R.color.confused));
                break;
            case "Fear":
                emotionalStateTextView.setTextColor(getResources().getColor(R.color.fearful));
                break;
            default:
                emotionalStateTextView.setTextColor(getResources().getColor(R.color.white));
                break;
        }


        emotionalStateImageView.setImageDrawable(getDrawable(
                    moodEvent.getEmotionalState().getDrawableId()));



        // Set the image and text for the appropriate social situation.
        if (moodEvent.getSocialSituation().getDescription() == null){
            socialSituationTextView.setVisibility(View.GONE);
            socialSituationImageView.setVisibility(View.GONE);
        } else {
            socialSituationTextView.setText(moodEvent.getSocialSituation().getDescription());
            socialSituationImageView.setImageDrawable(getDrawable(
                    moodEvent.getSocialSituation().getDrawableId()));
            socialSituationTextView.setVisibility(View.VISIBLE);
            socialSituationImageView.setVisibility(View.VISIBLE);
        }


        // Disable the inspection for the visibility resource type, so that we can pass in
        // a variable for the visibility. Taken from
        // http://stackoverflow.com/questions/26139115/not-able-to-dynamically-set-the-setvisibility-parameter
        // on 3/13/2017.
        //noinspection ResourceType
        socialSituationImageView.setVisibility(moodEvent.getSocialSituation().getVisibility());

        //Set the image from the MoodEvent image into the view.
      // imageImageView.setVisibility(moodEvent.getImage().);
        if(moodEvent.getImage() != null) {
            imageImageView.setImageBitmap(moodEvent.getImage());
            imageImageView.setVisibility(View.VISIBLE);
        } else {
            imageImageView.setVisibility(View.GONE);
        }

    }



    /**
     * Refreshes this view to display current information.
     */
    public void update(MoodSwing moodSwing){
        loadFromMoodSwing();
        loadFromMoodEvent();
    }

}
