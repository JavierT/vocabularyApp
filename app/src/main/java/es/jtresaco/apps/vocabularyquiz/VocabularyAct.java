package es.jtresaco.apps.vocabularyquiz;

import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;

public class VocabularyAct extends AppCompatActivity
        implements MenuFrag.OnItemSelectedListener {

    private static final String LOG_TAG="VocabularyAct";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vocabulary);
        Log.d(LOG_TAG, "onCreate VocabularyAct");

        // Check that the activity is using the layout version with
        // the fragment_container FrameLayout
        if (findViewById(R.id.fragment_container_voc) != null) {
            Log.d(LOG_TAG, "fragment_container is not null");
            // However, if we're being restored from a previous state,
            // then we don't need to do anything and should return or else
            // we could end up with overlapping fragments.
            /*if (savedInstanceState != null) {
                Log.d(LOG_TAG, "savedInstanceState is not null");
                return;
            }*/

            // Create a new Fragment to be placed in the activity layout
            MenuFrag firstFragment = new MenuFrag();
            // In case this activity was started with special instructions from an
            // Intent, pass the Intent's extras to the fragment as arguments
            firstFragment.setArguments(getIntent().getExtras());
            Log.d(LOG_TAG, "creating fragment and starting trans");
            // Add the fragment to the 'fragment_container' FrameLayout
            getFragmentManager().beginTransaction()
                    .add(R.id.fragment_container_voc, firstFragment).commit();
        }


    }

    // This is implementing the `OnItemSelectedListener` interface methods
    @Override
    public void onMainActionSelected(MenuFrag.ACTIONS action) {
        if (action == MenuFrag.ACTIONS.TEST) {
            // TODO
        } else if(action == MenuFrag.ACTIONS.NEWWORD) {

            FragmentTransaction ft = getFragmentManager().beginTransaction();
            //TODO: Change for NEWWORD FRAG
            ft.replace(R.id.fragment_container_voc, new WordFrag());
            ft.commit();
        }
    }

}
