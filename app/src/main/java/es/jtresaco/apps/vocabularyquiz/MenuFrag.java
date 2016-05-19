package es.jtresaco.apps.vocabularyquiz;


import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.annotation.TargetApi;

/**
 * A placeholder fragment containing a simple view.
 */
public class MenuFrag extends Fragment {

    private static final String LOG_TAG="MenuFrag";

    public MenuFrag() {
    }

    public enum ACTIONS {NEWWORD, TEST}

    // Define the mCallback of the interface type
    // mCallback will the activity instance containing fragment
    private OnItemSelectedListener mCallback;

    // Define the events that the fragment will use to communicate
    public interface OnItemSelectedListener {
        // This can be any number of events to be sent to the activity
        void onMainActionSelected(ACTIONS action);
    }


    //

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(LOG_TAG, LOG_TAG  + "onViewCreated");
        return inflater.inflate(R.layout.fragment_vocabulary, container, false);
    }

    // This event is triggered soon after onCreateView().
    // Any view setup should occur here.  E.g., view lookups and attaching view listeners.
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        Log.d(LOG_TAG, LOG_TAG  + "onViewCreated");
        // Setup any handles to view objects here
        Button startTest = (Button) view.findViewById(R.id.btnTest);
        startTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCallback.onMainActionSelected(ACTIONS.TEST);
            }
        });

        // Setup any handles to view objects here
        Button addWord = (Button) view.findViewById(R.id.btnAdd);
        addWord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCallback.onMainActionSelected(ACTIONS.NEWWORD);
            }
        });

        Bundle bundle=getArguments();
        String username = bundle.getString(LoginAct.PRM_USER);
        TextView hi = (TextView) view.findViewById(R.id.txtwelcome);
        hi.setText(getString(R.string.hello) + username);

    }

    // Store the mCallback (activity) that will have events fired once the fragment is attached
    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        onAttachToContext(context);
    }
    @SuppressWarnings("deprecation")
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            onAttachToContext(activity);
        }
    }
    protected void onAttachToContext(Context context) {
        //do what you want / cast fragment listener
        try {
            mCallback = (OnItemSelectedListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement HomeListener");
        }
    }



}
