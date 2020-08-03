package com.othernewspaper.horseracepresenter;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.othernewspaper.horseracepresenter.R;
import com.othernewspaper.horseracepresenter.app.AppConfig;
import com.othernewspaper.horseracepresenter.app.AppController;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    AppCompatButton btnQuizQuestions, btnStartSession, btnClearSession, btnStartQuiz;
    TextView tvStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnQuizQuestions = findViewById(R.id.btn_quiz_questions);
        btnStartSession = findViewById(R.id.btn_start_session);
        btnClearSession = findViewById(R.id.btn_clear_session);
        btnStartQuiz = findViewById(R.id.btn_start_quiz);
        tvStatus = findViewById(R.id.tv_current_status);
        btnQuizQuestions.setOnClickListener(new MainActivity.MyClickListener());
        btnStartSession.setOnClickListener(new MainActivity.MyClickListener());
        btnClearSession.setOnClickListener(new MainActivity.MyClickListener());
        btnStartQuiz.setOnClickListener(new MainActivity.MyClickListener());

    }

    private void quizQuestions() {
        Intent i = new Intent(MainActivity.this, QuestionsActivity.class);
        startActivity(i);
    }

    private void startClearSession(final int action) {
        String title = "Start Session";
        String message = "Are you sure to start new session? It will also clear all the previous sessions data.";
        if (action == 0) {
            title = "Clear Session";
            message = "Are you sure to clear the old session data?";
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        //Uncomment the below code to Set the message and title from the strings.xml file
        builder.setTitle(title).setMessage(message).setCancelable(false).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                startClearSessionData(action);
            }
        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                //  Action for 'NO' Button
                dialog.cancel();
            }
        });
        //Creating dialog box
        final AlertDialog alert = builder.create();
        alert.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                alert.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.white));
                alert.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.white));

            }
        });
        alert.show();

    }

    private void startClearSessionData(final int action) {
        AppController.getInstance().cancelPendingRequests("request_start_clear_session");
        // Tag used to cancel the request
        String tag_string_req = "request_start_clear_session";
        StringRequest strReq = new StringRequest(Request.Method.POST, AppConfig.URL_PRESENTER, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.e(TAG, response);
                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    // Check for error node in json
                    if (!error) {
                        if (action == 1) {
                            Toast.makeText(getApplicationContext(), "New session started.", Toast.LENGTH_LONG).show();
                            tvStatus.setText("New Session Created.");
                        } else {
                            Toast.makeText(getApplicationContext(), "Previous session cleared.", Toast.LENGTH_LONG).show();
                            tvStatus.setText("Previous Session Cleared.");
                        }

                    } else {
                        String errorMsg = jObj.getString("error_msg");
                        Log.e(TAG, errorMsg);
                        Toast.makeText(getApplicationContext(), errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    Log.e(TAG, e.getMessage());
                    Toast.makeText(getApplicationContext(), "Please check your internet connection!", Toast.LENGTH_LONG).show();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, error.getMessage());
                Toast.makeText(getApplicationContext(), "Please check your internet connection!", Toast.LENGTH_LONG).show();

            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                if (action == 1) {
                    params.put("action", "start_session");
                } else {
                    params.put("action", "clear_session");
                }
                return params;
            }

        };
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);

    }

    private void startQuiz() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        //Uncomment the below code to Set the message and title from the strings.xml file
        builder.setTitle("Start Quiz").setMessage(("Kindly make sure all the participants are ready and progress screen " + "is ready with all the participants")).setCancelable(false).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                startQuizForUsers();
            }
        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                //  Action for 'NO' Button
                dialog.cancel();
            }
        });
        //Creating dialog box
        final AlertDialog alert = builder.create();
        alert.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                alert.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.white));
                alert.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.white));

            }
        });
        alert.show();
    }

    private void startQuizForUsers() {
        AppController.getInstance().cancelPendingRequests("request_start_quiz");
        // Tag used to cancel the request
        String tag_string_req = "request_start_quiz";
        StringRequest strReq = new StringRequest(Request.Method.POST, AppConfig.URL_PRESENTER, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.e(TAG, response);
                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    // Check for error node in json
                    if (!error) {
                        int sessionStatus = jObj.getInt("session_status");
                        if (sessionStatus == 2) {
                            Toast.makeText(getApplicationContext(), "Please create new session before starting the new quiz", Toast.LENGTH_LONG).show();
                        } else {
                            tvStatus.setText("Quiz Started.");
                        }
                    } else {
                        String errorMsg = jObj.getString("error_msg");
                        Log.e(TAG, errorMsg);
                        Toast.makeText(getApplicationContext(), errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    Log.e(TAG, e.getMessage());
                    Toast.makeText(getApplicationContext(), "Please check your internet connection!", Toast.LENGTH_LONG).show();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, error.getMessage());
                Toast.makeText(getApplicationContext(), "Please check your internet connection!", Toast.LENGTH_LONG).show();

            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("action", "start_quiz");
                return params;
            }

        };
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);

    }

    private class MyClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_quiz_questions:
                    quizQuestions();
                    break;
                case R.id.btn_start_session:
                    startClearSession(1);
                    break;
                case R.id.btn_clear_session:
                    startClearSession(0);
                    break;
                case R.id.btn_start_quiz:
                    startQuiz();
                    break;
            }

        }
    }
}