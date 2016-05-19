package es.jtresaco.apps.vocabularyquiz;


import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class PlayFrag extends Fragment {
    private ArrayList<Word> mWords;
    private LoadWordsTask mLoadTask;

    public PlayFrag() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mWords = new ArrayList<>();

        return inflater.inflate(R.layout.fragment_play, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        final EditText mUserWord = (EditText) view.findViewById(R.id.etxtResult);
        final TextView mOriginalWord = (TextView) view.findViewById(R.id.txtGuessWord);
        mLoadTask = new LoadWordsTask(mOriginalWord);
        mLoadTask.execute((Void) null);

        // Buttons handlers
        final Button btnHint = (Button) view.findViewById(R.id.btnHint);
        btnHint.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(mUserWord.getText().toString().startsWith(mWords.get(0).getOriginal())) {
                    mUserWord.setText(mOriginalWord.getText().subSequence(0,mUserWord.getText().length()));
                } else {
                    mUserWord.setText(mOriginalWord.getText().subSequence(0,1));
                }
            }
        });

        final Button btnCheck = (Button) view.findViewById(R.id.btnNext);
        btnCheck.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(btnHint.getText().toString().equalsIgnoreCase(getString(R.string.next))) {
                    // new word
                    mUserWord.setText("");
                    // Delete used word
                    mWords.remove(0);
                    // Use a new one
                    mOriginalWord.setText(mWords.get(0).getOriginal());
                    if(mWords.size()==1) {
                        // Load five more...
                        mLoadTask = new LoadWordsTask(mOriginalWord);
                        mLoadTask.execute((Void) null);
                    }
                } else {
                    if (mUserWord.getText().toString().equals(mWords.get(0).getTranslation()) ||
                            (mWords.get(0).hasTranslationAlt() && mUserWord.getText().toString().equals(mWords.get(0).getTranslationAlt()))) {
                        // Good to go!
                        Toast.makeText(getActivity().getBaseContext(), R.string.word_success, Toast.LENGTH_SHORT).show();
                        btnHint.setText(getString(R.string.next));
                        // Raise interface from main activity to increase user score
                    } else {
                        // Show error, or give more info, as the developer wants...
                        Toast.makeText(getActivity().getBaseContext(), R.string.word_error, Toast.LENGTH_SHORT).show();
                        mUserWord.setTextColor(Color.RED);
                    }
                }

            }
        });
        super.onViewCreated(view, savedInstanceState);
    }

    public class LoadWordsTask extends AsyncTask<Void, Void, JSONObject> {


        private TextView mlblOriginal;

        LoadWordsTask(TextView label) {
            mlblOriginal = label;
        }

        @Override
        protected JSONObject doInBackground(Void... params) {
            JSONObject response = null;
            try {
                JSONObject data = new JSONObject();
                data.put("action", DBRequest.ACTION_GETWORD);
                data.put("amount", 5);
                data.put("lang","fr");
                response = DBRequest.send(data);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return response;
        }

        @Override
        protected void onPostExecute(final JSONObject response) {
            //showProgress(false);
            String status;
            boolean error = response==null;
            try {
                if(!error) {
                    Log.d("Database","response is " + response.toString());
                    status = response.getString("status");
                    if (status.equalsIgnoreCase("OK")) {
                        // FIll the views with the word and save the other one
                        JSONArray arrayW = response.getJSONArray("words");
                        for (int i = 0; i < arrayW.length(); i++) {
                            JSONObject obW = arrayW.getJSONObject(i);
                            if(obW== null || !obW.has("original") || !obW.has("translation")) {
                                error = true;
                                break;
                            }
                            Word newWord = new Word();
                            newWord.setOriginal(obW.getString("original"));
                            newWord.setTranslation(obW.getString("translation"));
                            newWord.setLesson((obW.has("lesson")? obW.getString("lesson"):getString(R.string.unknown)));
                            if(obW.has("originalAlt")) newWord.setOriginalAlt(obW.getString("originalAlt"));
                            if(obW.has("translationAlt")) newWord.setTranslationAlt(obW.getString("translationAlt"));
                            mWords.add(newWord);
                        }
                        // Populate the label for the user to guess only if it's empty
                        if(mlblOriginal.getText().length()==0)
                            mlblOriginal.setText(mWords.get(0).getOriginal());
                    } else {
                        error = true;
                    }
                } else {
                    Log.d("Database","response is null");
                }
            } catch (JSONException e) {
                e.printStackTrace();
                error = true;
            }
            Log.d("Database","status, error is " + (error?"true":"false"));
            if(error) {
                Toast.makeText(getActivity().getBaseContext(),R.string.error_loading_word, Toast.LENGTH_LONG).show();
            }

        }

    }



}
