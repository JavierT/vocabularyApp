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
    private boolean mLoadingWord;

    public PlayFrag() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mWords = new ArrayList<>();
        mLoadingWord = true;
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
                if(mWords == null || mWords.size()==0) return;
                String userinput = mUserWord.getText().toString().toLowerCase().trim();
                if(userinput.startsWith(mWords.get(0).getTranslation())) {
                    mUserWord.setText(mWords.get(0).getTranslation().subSequence(0,userinput.length()));
                } else {
                    mUserWord.setText(mWords.get(0).getTranslation().subSequence(0,1));
                }
            }
        });

        final Button btnCheck = (Button) view.findViewById(R.id.btnNext);
        btnCheck.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(mWords == null || mWords.size()==0) return;
                Log.d("Checking", "Button is" + btnCheck.getText().toString());
                if(btnCheck.getText().toString().equals(getString(R.string.next))) {
                    // new word
                    btnCheck.setText(getString(R.string.check));
                    mUserWord.setText("");
                    mUserWord.setTextColor(Color.BLACK);
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
                    String userinput = mUserWord.getText().toString();
                    if(userinput.length() == 0) return;
                    userinput = userinput.trim();
                    // maybe it's good to do toLowerCase(Locale.fr)....
                    Log.d("Checking", "comparing "+userinput+ " with " + mWords.get(0).getTranslation());
                    if (userinput.equalsIgnoreCase(mWords.get(0).getTranslation()) ||
                            (mWords.get(0).hasTranslationAlt() && userinput.equalsIgnoreCase(mWords.get(0).getTranslationAlt()))) {
                        Toast.makeText(getActivity().getBaseContext(), R.string.word_success, Toast.LENGTH_SHORT).show();
                        mUserWord.setTextColor(Color.GREEN);
                        btnCheck.setText(getString(R.string.next));
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

    @Override
    public void onStart() {
        mLoadingWord = true;
        super.onStart();
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
                            Log.d("Words", "w: " + obW.toString());
                            if(!obW.has("original") || !obW.has("translation")) {
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
                        if(!error && mLoadingWord) {
                            mLoadingWord = false;
                            Log.d("Words", "setting new word : " + mWords.get(0).getOriginal());
                            mlblOriginal.setText(mWords.get(0).getOriginal());
                        }
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
