package es.jtresaco.apps.vocabularyquiz;

import android.app.Fragment;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;


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
                mData.put("fr", mFr.getText());
                mData.put("fralt", mFrAlt.getText());
                mData.put("es", mEs.getText());
                mData.put("esalt", mEsAlt.getText());
                mData.put("lesson", mLesson.getSelectedItemId());
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
            showProgress(false);
            String status = null;
            boolean error = response==null;
            Log.d("Database","response is " + response.toString());
            try {
                if(!error) {
                    status = response.getString("status");
                    if (status.equalsIgnoreCase("OK")) {
                        Toast.makeText(getActivity().getBaseContext(), R.string.savedSuccess, Toast.LENGTH_SHORT ).show();
                        mFr.setText("");
                        mEs.setText("");
                        mFrAlt.setText("");
                        mEsAlt.setText("");
                        mFr.requestFocus();
                    } else {
                        error = true;
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
                error = true;
            }
            Log.d("Database","status, error is " + (error?"true":"false"));
            if(error) {
                mPasswordView.setError(getString(R.string.error_incorrect_password));
                mPasswordView.requestFocus();
            }

        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }




}
