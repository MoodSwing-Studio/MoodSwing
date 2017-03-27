package com.ualberta.cmput301w17t22.moodswing;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * The MainActivity of the MoodSwing app. This screen will display the map view of mood events,
 * the mood feed, and have a floating toolbar to navigate the user to the other functionalities of
 * the app.
 * <p/>
 * Hexadecimal color codes:
 * dark magenta: #66023C
 * background: e0b0ff
 */
public class MainActivity extends AppCompatActivity implements MSView<MoodSwing>  {

    /** The main toolbar of the app that lets users navigate to the other parts of the app. */
    Toolbar mainToolbar;

    /**
     * The welcome text for the app. Currently displays the username and the Jest id. Used mainly
     * for testing right now.
     */
    TextView welcomeText;

    private Participant mainParticipant;
    /**
     *
     */
    private MoodEventAdapter moodFeedAdapter;

    // displays text when MoodFeed is empty
    TextView emptyFeed;

    // listview that holds the mood feed
    private ListView moodFeedListView;

    ArrayList<MoodEvent> moodFeedEvents = new ArrayList<MoodEvent>();

    Spinner filterSpinner;

    ElasticSearchController elasticSearchController;

    ArrayList<String> filter_strings = new ArrayList<>();

    // The trigger word that the user will search for in filter
    private String triggerWord = "";



    /**
     * Called on opening of activity for the first time.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize all the widgets of the app.
        initialize();

        setSupportActionBar(mainToolbar);

        // Get MoodSwingController.
        MoodSwingController moodSwingController =
                MoodSwingApplication.getMoodSwingController();
        elasticSearchController =
                MoodSwingApplication.getElasticSearchController();

        // Load MainParticipant.
        Participant mainParticipant = moodSwingController.getMainParticipant();

        // Set the welcome text appropriately.
        welcomeText.setText("Welcome user \"" + mainParticipant.getUsername() +
                "\" with ID \"" + mainParticipant.getId() + "\"");




        moodFeedListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                // Get the MoodSwingController.
                MoodSwingController moodSwingController =
                        MoodSwingApplication.getMoodSwingController();

                // Report the position of the mood event being viewed to the main model.
                //Log.d("help", String.valueOf(position));
                moodSwingController.setMoodFeedPosition(position);

                // Launch the ViewMoodEventActivity.
                Intent intent = new Intent(MainActivity.this, ViewMoodEventActivity.class);
                // Tell the view mood event activity we are coming from the mood history.
                intent.putExtra("MoodListType", "MoodFeed");
                startActivity(intent);
            }
        });


    }


    @Override
    protected void onStart(){
        super.onStart();

        loadMoodSwing();

        moodFeedAdapter = new MoodEventAdapter(this, moodFeedEvents);

        moodFeedListView.setAdapter(moodFeedAdapter);


        // handles when a different thing is selected in filter spinner
        // place in onStart deliberately to minimize calls to setOnItemSelectedListener
        // This will always call when the spinner is initialized.
        filterSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //Toast.makeText(MainActivity.this, filter_strings.get(position)+"selected", Toast.LENGTH_SHORT);
                String selected = filterSpinner.getSelectedItem().toString();

                switch (selected) {
                    case "No Filter":       // no filter selected
                        Toast.makeText(MainActivity.this, selected, Toast.LENGTH_SHORT).show();

                        // remove filters
                        break;
                    case "Recent Week":     // sort by recent week
                        // to add some sort of maker when Recent Week selected. Remove on other options
                        //filter_strings.set(filter_strings.indexOf("Recent Week"), "Recent Week*");

                        // filter by recent week here
                        break;
                    case "By Emotion":      // sort by emotion
                        Dialog dialog = new Dialog(MainActivity.this);
                        dialog.setContentView(R.layout.emotional_state_dialogue_box);
                        dialog.setTitle("This is my custom dialog box");
                        dialog.setCancelable(true);
                        // there are a lot of settings, for dialog, check them all out!
                        // set up radiobutton
                        RadioButton happinessRB = (RadioButton) dialog.findViewById(R.id.happinessRadioButton);
                        RadioButton angerRB = (RadioButton) dialog.findViewById(R.id.angerRadioButton);
                        RadioButton disgustRB = (RadioButton) dialog.findViewById(R.id.disgustRadioButton);
                        RadioButton confusionRB = (RadioButton) dialog.findViewById(R.id.confusionRadioButton);
                        RadioButton fearRB = (RadioButton) dialog.findViewById(R.id.fearRadioButton);
                        RadioButton sadnessRB = (RadioButton) dialog.findViewById(R.id.sadnessRadioButton);
                        RadioButton shameRB = (RadioButton) dialog.findViewById(R.id.shameRadioButton);
                        RadioButton surpriseRB = (RadioButton) dialog.findViewById(R.id.surpriseRadioButton);

                        // now that the dialog is set up, it's time to show it
                        dialog.show();

                        break;
                    default:                // sort by trigger
                        // from http://stackoverflow.com/questions/10903754/input-text-dialog-android on 3/27
                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this, R.style.DialogTheme);


                        // Method for displaying an edittext nicely in an alertdialog adapted from
                        // http://stackoverflow.com/questions/27774414/add-bigger-margin-to-edittext-in-android-alertdialog
                        // on 3/25/2017.

                        // Create edittext to take user input.
                        final EditText triggerEditText = new EditText(MainActivity.this);
                        // Set custom edittext shape.
                        triggerEditText.setBackgroundResource(R.drawable.dialog_edittext_shape);
                        // Set some padding from the left.
                        triggerEditText.setPadding(6, 0, 6, 0);

                        // Create a container for the edittext.
                        LinearLayout triggerEditTextContainer = new LinearLayout(MainActivity.this);
                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.WRAP_CONTENT);
                        // Set the margins for the edittext.
                        params.leftMargin = getResources().getDimensionPixelSize(R.dimen.dialog_margin);
                        params.rightMargin = getResources().getDimensionPixelSize(R.dimen.dialog_margin);

                        // Set the parameters to the edittext and add it to the container.
                        triggerEditText.setLayoutParams(params);
                        triggerEditTextContainer.addView(triggerEditText);

                        builder.setTitle("Enter Trigger Word:")
                                .setMessage("(max one word)")
                                .setCancelable(true)
                                .setView(triggerEditTextContainer);


                        // Set up the buttons
                        builder.setPositiveButton("Filter", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                triggerWord = triggerEditText.getText().toString();
                            }
                        });
                        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });

                        builder.show();


                        break;
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Toast.makeText(MainActivity.this, "nothing selected", Toast.LENGTH_SHORT).show();
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
    }

    /**
     * Inflates the menu. Connects the menu_main_activity.xml to the
     * menu_main_activity in activity_main.xml.
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_main_activity, menu);
        return true;
    }




    /**
     * This method handles clicks on menu items from the overflow menu.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            case R.id.homeToolBarButton:
                // User chose the "Home" item, should navigate to MainActivity.

                return true;

            case R.id.followToolBarButton:
                // User chose the "Follower & Following" action, should navigate to the
                // follower/following activity
                intent = new Intent(MainActivity.this, MainFollowActivity.class);
                startActivity(intent);
                return true;

            case R.id.newMoodEventToolBarButton:
                // User chose the "New Mood Event" item, should navigate the NewMoodEventActivity.
                intent = new Intent(MainActivity.this, NewMoodEventActivity.class);
                startActivity(intent);
                return true;

            case R.id.moodHistoryToolBarButton:
                // User chose the "View Mood History" item, should navigate to the
                // MoodHistoryActivity.
                intent = new Intent(MainActivity.this, MoodHistoryActivity.class);
                startActivity(intent);
                return true;

            case R.id.blockUserToolBarButton:
                // User chose the "Block User" item, should navigate to the
                // BlockUserActivity.
                intent = new Intent(MainActivity.this, BlockUserActivity.class);
                startActivity(intent);
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Initializes all the widgets for this activity.
     */
    public void initialize() {
        mainToolbar = (Toolbar) findViewById(R.id.mainToolBar);
        mainToolbar.setTitle("");
        welcomeText = (TextView)findViewById(R.id.mainWelcomeText);
        moodFeedListView  = (ListView) findViewById(R.id.MoodFeedListView);
        emptyFeed = (TextView) findViewById(R.id.emptyMoodFeed);
        filterSpinner = (Spinner) findViewById(R.id.filterSpinnerMoodFeed);

        // these will populate the filter spinner

        filter_strings.add("No Filter");
        filter_strings.add("Recent Week");
        filter_strings.add("By Emotion");
        filter_strings.add("By Trigger");

        // for our custom spinner options and settings
        ArrayAdapter<String> filteradapter = new ArrayAdapter<String>(this, R.layout.filter_spinner, filter_strings);
        filterSpinner.setAdapter(filteradapter);

        // Add this View to the main Model class.
        MoodSwingController moodSwingController = MoodSwingApplication.getMoodSwingController();
        moodSwingController.addView(this);
    }
    public void loadMoodSwing() {
        // Get the main Model and get the main participant, and the main participant's mood history.
        moodFeedEvents.clear();
        MoodSwingController moodSwingController = MoodSwingApplication.getMoodSwingController();

        mainParticipant = moodSwingController.getMainParticipant();
        ArrayList<String> mainParticipantFollowingList = mainParticipant.getFollowing();

        int i;
        int size =0 ;
        if(mainParticipantFollowingList.isEmpty() == false){
            size = mainParticipantFollowingList.size();
        }
        // add most recent mood event of all those we're following
         for(i =0; i < size ; i++){
             Participant followingParticipant = elasticSearchController.getParticipantByUsername(mainParticipantFollowingList.get(i));
             if(followingParticipant.getMostRecentMoodEvent() != null){
                 // make sure the followed person as at least one mood event
                 moodFeedEvents.add(followingParticipant.getMostRecentMoodEvent());
             }
         }


        Collections.sort(moodFeedEvents, new Comparator<MoodEvent>() {
            @Override
            public int compare(MoodEvent o1, MoodEvent o2) {
                return o2.getDate().compareTo(o1.getDate());
            }
        });
        moodSwingController.setMoodFeed(moodFeedEvents);

        // Initialize array adapter.
        moodFeedAdapter = new MoodEventAdapter(this, moodFeedEvents);
        moodFeedListView.setEmptyView(findViewById(R.id.emptyMoodFeed));
        moodFeedListView.setAdapter(moodFeedAdapter);
    }

    /**
     * This is called when the Model tells this View to update because of some change in the Model.
     * @param moodSwing
     */
    public void update(MoodSwing moodSwing) {
        loadMoodSwing();
    }
}
