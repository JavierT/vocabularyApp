package es.jtresaco.apps.vocabularyquiz;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;

public class VocabularyAct extends AppCompatActivity
        implements MenuFrag.OnItemSelectedListener{

    private static final String LOG_TAG="VocabularyAct";
    private Fragment mCurrentFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vocabulary);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.voc_toolbar);
        setSupportActionBar(myToolbar);
        Log.d(LOG_TAG, "onCreate VocabularyAct");

        View loginFormView = findViewById(R.id.login_form);
        View progressView = findViewById(R.id.login_progress);

        // Check that the activity is using the layout version with
        // the fragment_container FrameLayout
        if (findViewById(R.id.fragment_form) != null) {
            Log.d(LOG_TAG, "fragment_container is not null");
            // However, if we're being restored from a previous state,
            // then we don't need to do anything and should return or else
            // we could end up with overlapping fragments.
            /*if (savedInstanceState != null) {
                Log.d(LOG_TAG, "savedInstanceState is not null");
                return;
            }*/

            // Create a new Fragment to be placed in the activity layout
            mCurrentFragment = new MenuFrag();
            mCurrentFragment.setArguments(getIntent().getExtras());
            Log.d(LOG_TAG, "creating fragment and starting trans");
            // Add the fragment to the 'fragment_container' FrameLayout
            getFragmentManager().beginTransaction()
                    .add(R.id.fragment_form, mCurrentFragment)
                    // Add this transaction to the back stack
                    .addToBackStack("menu")
                    .commit();
        }
    }


    // This is implementing the `OnItemSelectedListener` interface methods
    public void onMainActionSelected(MenuFrag.ACTIONS action) {
        if (action == MenuFrag.ACTIONS.TEST) {
            mCurrentFragment = new PlayFrag();
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.replace(R.id.fragment_form, mCurrentFragment);
            ft.commit();
        } else if(action == MenuFrag.ACTIONS.NEWWORD) {
            mCurrentFragment = new NewWordFrag();
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.replace(R.id.fragment_form, mCurrentFragment);
            ft.commit();
        }
    }

}
