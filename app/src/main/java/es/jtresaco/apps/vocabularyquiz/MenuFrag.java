package es.jtresaco.apps.vocabularyquiz;


import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

/**
 * A placeholder fragment containing a simple view.
 */
public class MenuFrag extends Fragment {

    public MenuFrag() {
    }

    public enum ACTIONS {NEWWORD, TEST};

    // Define the listener of the interface type
    // listener will the activity instance containing fragment
    private OnItemSelectedListener listener;

    // Define the events that the fragment will use to communicate
    public interface OnItemSelectedListener {
        // This can be any number of events to be sent to the activity
        public void onMainActionSelected(ACTIONS action);
    }


    //

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_vocabulary, container, false);


    }

    // This event is triggered soon after onCreateView().
    // Any view setup should occur here.  E.g., view lookups and attaching view listeners.
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        // Setup any handles to view objects here
        Button startTest = (Button) view.findViewById(R.id.btnTest);
        startTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO: Open add word fragment
                listener.onMainActionSelected(ACTIONS.TEST);
            }
        });

        // Setup any handles to view objects here
        Button addWord = (Button) view.findViewById(R.id.btnTest);
        startTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO: Open add word fragment
                listener.onMainActionSelected(ACTIONS.NEWWORD);
            }
        });
        Bundle bundle=getArguments();

        //here is your list array
        String username = bundle.getString(LoginAct.PRM_USER);
        TextView hi = (TextView) view.findViewById(R.id.txtwelcome);
        hi.setText(hi.getText() + username);

    }

    // Store the listener (activity) that will have events fired once the fragment is attached
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnItemSelectedListener) {
            listener = (OnItemSelectedListener) context;
        } else {
            throw new ClassCastException(context.toString()
                    + " must implement MyListFragment.OnItemSelectedListener");
        }
    }
}
