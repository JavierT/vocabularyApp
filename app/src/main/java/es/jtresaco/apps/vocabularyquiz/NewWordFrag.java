package es.jtresaco.apps.vocabularyquiz;

import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class NewWordFrag extends Fragment {

    private SaveWordTask mSaveTask;

    public NewWordFrag() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_newword, container, false);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_addword, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        final LinearLayout layAltSpa = (LinearLayout) view.findViewById(R.id.layoutAltSpa);
        final TextView txtAltSpa = (TextView) view.findViewById(R.id.txtAltSpaTitle);
        final Button btnAltSpa = (Button) view.findViewById(R.id.btnAltSpa);
        btnAltSpa.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                layAltSpa.setVisibility(View.VISIBLE);
                txtAltSpa.setVisibility(View.VISIBLE);
            }
        });

        final LinearLayout layAltFre = (LinearLayout) view.findViewById(R.id.layoutAltFre);
        final TextView txtAltFre = (TextView) view.findViewById(R.id.txtAltFreTitle);
        final Button btnAltFre = (Button) view.findViewById(R.id.btnAltFre);
        btnAltFre.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                layAltFre.setVisibility(View.VISIBLE);
                txtAltFre.setVisibility(View.VISIBLE);
            }
        });

        final Button saveWord = (Button) view.findViewById(R.id.btnSave);
        final EditText frWord = (EditText) view.findViewById(R.id.etxtNewFreWord);
        final EditText esWord = (EditText) view.findViewById(R.id.etxtNewSpaWord);
        final EditText frAltWord = (EditText) view.findViewById(R.id.etxtAltFreWord);
        final EditText esAltWord = (EditText) view.findViewById(R.id.etxtAltSpaWord);
        final Spinner lessonSpi = (Spinner) view.findViewById(R.id.spinnerLesson);
        //Fill spinner
        LoadLessonsTask loadLessons = new LoadLessonsTask(lessonSpi);
        loadLessons.execute((Void) null);

        saveWord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Save in database
                if(lessonSpi.getSelectedItemId()==0 || frWord.getText().length() == 0 ||
                        esWord.getText().length() == 0) {
                    //Toast.makeText(getActivity().getBaseContext(), R.string.emptyFields, Toast.LENGTH_SHORT ).show();
                    frWord.setError(getText(R.string.errorEmptyField));
                    esWord.setError(getText(R.string.errorEmptyField));
                    return;
                }
                mSaveTask = new SaveWordTask(frWord, esWord,frAltWord, esAltWord, lessonSpi);
                mSaveTask.execute((Void) null);


            }
        });
        frWord.requestFocus();
        super.onViewCreated(view, savedInstanceState);
    }

    public class LoadLessonsTask extends AsyncTask<Void, Void, JSONObject> {

        private final Spinner mLesson;
        private JSONObject mData;
        LoadLessonsTask(Spinner lesson) {
            mLesson = lesson;
        }

        @Override
        protected void onPreExecute() {
            try {
                mData = new JSONObject();
                mData.put("action", DBRequest.ACTION_GETLESSONS);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        protected JSONObject doInBackground(Void... params) {
            JSONObject response=null;
            if(mData != null)
                response = DBRequest.send(mData);
            return response;
        }

        @Override
        protected void onPostExecute(final JSONObject response) {
            //showProgress(false);
            String status;
            ArrayAdapter<String> adapter;
            ArrayList<String> items = new ArrayList<>();
            boolean error = response==null;
            try {
                if(!error) {
                    Log.d("Database","response is " + response.toString());
                    status = response.getString("status");
                    if (status.equalsIgnoreCase("OK")) {
                        JSONArray arrayW = response.getJSONArray("lessons");
                        for (int i = 0; i < arrayW.length(); i++) {
                            JSONObject obW = arrayW.getJSONObject(i);
                            if(obW== null || !obW.has("name")) {
                                error = true;
                                break;
                            }
                            items.add(obW.getString("name"));
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
                items.add(getString(R.string.genericLesson));
            }
            adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, items);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            mLesson.setAdapter(adapter);
        }

    }

    public class SaveWordTask extends AsyncTask<Void, Void, JSONObject> {

        private final EditText mFr;
        private final EditText mEs;
        private final EditText mFrAlt;
        private final EditText mEsAlt;
        private final Spinner mLesson;
        private JSONObject mData;

        SaveWordTask(EditText fr, EditText es, EditText frAlt, EditText esAlt, Spinner lesson) {
            mFr = fr;
            mEs = es;
            mEsAlt = esAlt;
            mFrAlt = frAlt;
            mLesson = lesson;
        }

        @Override
        protected void onPreExecute() {
            try {
                mData = new JSONObject();
                mData.put("action", DBRequest.ACTION_ADDWORD);
                mData.put("fr", mFr.getText().toString().trim());
                mData.put("fralt", mFrAlt.getText()==null?"":mFrAlt.getText().toString().trim());
                mData.put("es", mEs.getText().toString().trim());
                mData.put("esalt", mFrAlt.getText()==null?"":mEsAlt.getText().toString().trim());
                mData.put("lesson", mLesson.isSelected() ? mLesson.getSelectedItemId() : 1);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        protected JSONObject doInBackground(Void... params) {
            JSONObject response=null;
            if(mData != null)
                response = DBRequest.send(mData);
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
                        Toast.makeText(getActivity().getBaseContext(), R.string.savedSuccess, Toast.LENGTH_SHORT ).show();
                        mFr.setText("");
                        mEs.setText("");
                        if(mFrAlt.getVisibility() == View.VISIBLE)
                            mFrAlt.setText("");
                        if(mEsAlt.getVisibility() == View.VISIBLE)
                            mEsAlt.setText("");
                        mFr.requestFocus();
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
                mFr.setError(getString(R.string.error_word_not_saved));
            }

        }

    }




}
