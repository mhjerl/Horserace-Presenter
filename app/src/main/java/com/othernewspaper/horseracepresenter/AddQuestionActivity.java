package com.othernewspaper.horseracepresenter;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.Group;

import com.othernewspaper.horseracepresenter.R;
import com.othernewspaper.horseracepresenter.app.AppConfig;
import com.othernewspaper.horseracepresenter.app.AppController;
import com.othernewspaper.horseracepresenter.app.QuestionItem;
import com.othernewspaper.horseracepresenter.app.UIValidation;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class AddQuestionActivity extends AppCompatActivity {

    AppCompatSpinner spQuestionTime, spCorrectOption;
    TextView tvQuestionNo;
    TextInputLayout tilQuestion, tilOptionA, tilOptionB, tilOptionC, tilOptionD;
    TextInputEditText etQuestion, etOptionA, etOptionB, etOptionC, etOptionD;
    AppCompatButton btnAddQuestion, btnSaveQuestion, btnDiscardChanges;
    Group grpEditBtns;
    Toolbar toolbar;
    ArrayAdapter<CharSequence> questionTimeAdapter;
    ArrayAdapter<CharSequence> correctOptionAdapter;

    private static final String TAG = AddQuestionActivity.class.getSimpleName();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_question);
        toolbar = findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        spQuestionTime = findViewById(R.id.sp_timer);
        spCorrectOption = findViewById(R.id.sp_correct_option);
        tvQuestionNo = findViewById(R.id.tv_question_no);
        tilQuestion = findViewById(R.id.til_question);
        tilOptionA = findViewById(R.id.til_option_a);
        tilOptionB = findViewById(R.id.til_option_b);
        tilOptionC = findViewById(R.id.til_option_c);
        tilOptionD = findViewById(R.id.til_option_d);
        etQuestion = findViewById(R.id.et_question);
        etOptionA = findViewById(R.id.et_option_a);
        etOptionB = findViewById(R.id.et_option_b);
        etOptionC = findViewById(R.id.et_option_c);
        etOptionD = findViewById(R.id.et_option_d);
        btnAddQuestion = findViewById(R.id.btn_add_question);
        btnSaveQuestion = findViewById(R.id.btn_save);
        btnDiscardChanges = findViewById(R.id.btn_discard);
        grpEditBtns = findViewById(R.id.editable_btn_grp);
        etQuestion.addTextChangedListener(new AddQuestionActivity.MyTextWatcher(etQuestion));
        etOptionA.addTextChangedListener(new AddQuestionActivity.MyTextWatcher(etOptionA));
        etOptionB.addTextChangedListener(new AddQuestionActivity.MyTextWatcher(etOptionB));
        etOptionC.addTextChangedListener(new AddQuestionActivity.MyTextWatcher(etOptionC));
        etOptionD.addTextChangedListener(new AddQuestionActivity.MyTextWatcher(etOptionD));
        questionTimeAdapter = ArrayAdapter.createFromResource(this, R.array.question_time_array, android.R.layout.simple_spinner_item);
        correctOptionAdapter = ArrayAdapter.createFromResource(this, R.array.correct_options_array, android.R.layout.simple_spinner_item);
        questionTimeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        correctOptionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spQuestionTime.setAdapter(questionTimeAdapter);
        spCorrectOption.setAdapter(correctOptionAdapter);
        Intent i = getIntent();
        if (i != null) {

            String action = i.getStringExtra("ACTION");
            if(action.equals("EDIT")) {
                QuestionItem question = i.getParcelableExtra("QUESTION");
                int qNo = i.getIntExtra("QUESTION_NO", 0);
                displayQuestion(question, qNo);
            } else if(action.equals("ADD")) {
                btnAddQuestion.setVisibility(View.VISIBLE);
                grpEditBtns.setVisibility(View.GONE);
            }
        }
        btnAddQuestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String question = etQuestion.getText().toString().trim();
                String optionA = etOptionA.getText().toString().trim();
                String optionB = etOptionB.getText().toString().trim();
                String optionC = etOptionC.getText().toString().trim();
                String optionD = etOptionD.getText().toString().trim();
                int time = Integer.parseInt(spQuestionTime.getSelectedItem().toString().split(" ")[0]);
                String correctOption = spCorrectOption.getSelectedItem().toString();
                if (validateInput()) {
                    UploadQuestion("ADD", 0, question, time, correctOption, optionA, optionB, optionC, optionD);
                } else {
                    Toast.makeText(getApplicationContext(), "Mandatory fields cannot be blank.", Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    private void displayQuestion(final QuestionItem questionItem, int qNo) {

        spQuestionTime.setSelection(questionTimeAdapter.getPosition(questionItem.getTime() + " " + "Sec"));
        String questionNo = "Question " + qNo++;
        tvQuestionNo.setText(questionNo);
        etQuestion.setText(questionItem.getQuestion());
        spCorrectOption.setSelection(correctOptionAdapter.getPosition(questionItem.getAns()));
        etOptionA.setText(questionItem.getOption_a());
        etOptionB.setText(questionItem.getOption_b());
        etOptionC.setText(questionItem.getOption_c());
        etOptionD.setText(questionItem.getOption_d());
        grpEditBtns.setVisibility(View.VISIBLE);
        btnAddQuestion.setVisibility(View.GONE);
        btnSaveQuestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int questionId = questionItem.getId();
                String question = etQuestion.getText().toString().trim();
                String optionA = etOptionA.getText().toString().trim();
                String optionB = etOptionB.getText().toString().trim();
                String optionC = etOptionC.getText().toString().trim();
                String optionD = etOptionD.getText().toString().trim();
                int time = Integer.parseInt(spQuestionTime.getSelectedItem().toString().split(" ")[0]);
                String correctOption = spCorrectOption.getSelectedItem().toString();
                if (validateInput()) {
                    UploadQuestion("EDIT", questionId, question, time, correctOption, optionA, optionB, optionC, optionD);
                } else {
                    Toast.makeText(getApplicationContext(), "Mandatory fields cannot be blank.", Toast.LENGTH_LONG).show();
                }

            }
        }); btnDiscardChanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(AddQuestionActivity.this);
                //Uncomment the below code to Set the message and title from the strings.xml file
                builder.setMessage("Are you sure to discard these changes?").setTitle("Discard Changes").setCancelable(false).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent i = new Intent(AddQuestionActivity.this, QuestionsActivity.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(i);
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
        });

    }

    private void UploadQuestion(final String action, final int questionId, final String question, final int time, final String correctOption, final String optionA, final String optionB, final String optionC, final String optionD) {
        // Tag used to cancel the request
        String tag_string_req = "req_upload_question";
        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_PRESENTER, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.e(TAG, response);
                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    // Check for error node in json
                    if (!error) {
                        if (action.equals("EDIT")) {
                            Toast.makeText(getApplicationContext(), "Question updated successfully.", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getApplicationContext(), "Question Added successfully.", Toast.LENGTH_SHORT).show();
                        }
                        Intent i = new Intent(AddQuestionActivity.this, QuestionsActivity.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(i);

                    } else {
                        String errorMsg = jObj.getString("error_msg");
                        Log.e(TAG, errorMsg);
                        Toast.makeText(getApplicationContext(), errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    Log.e(TAG, e.getMessage());
                    Toast.makeText(getApplicationContext(), "Please check your internet " + "connection!", Toast.LENGTH_LONG).show();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, error.getMessage());
                Toast.makeText(getApplicationContext(), "Please check your internet " + "connection!", Toast.LENGTH_LONG).show();

            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                if (action.equals("EDIT")) {
                    params.put("action", "edit_question");
                    params.put("question_id", Integer.toString(questionId));
                } else {
                    params.put("action", "add_question");
                }
                params.put("question", question);
                params.put("time", Integer.toString(time));
                params.put("correct_option", correctOption);
                params.put("option_a", optionA);
                params.put("option_b", optionB);
                params.put("option_c", optionC);
                params.put("option_d", optionD);
                return params;
            }

        };
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);

    }


    private boolean validateInput() {

        if(!validateQuestion(tilQuestion, etQuestion)) {
            return false;
        }
        if(!validateOption(tilOptionA, etOptionA, true)) {
            return false;
        }
        if(!validateOption(tilOptionB, etOptionB, true)) {
            return false;
        }
        if(!validateOption(tilOptionC, etOptionC, false)) {
            return false;
        }
        if(!validateOption(tilOptionD, etOptionD, false)) {
            return false;
        }

        return true;

    }

    private boolean validateQuestion(TextInputLayout textInputLayout, TextInputEditText editText) {
        String msg = UIValidation.questionValidate(Objects.requireNonNull(editText.getText()).toString(), true);
        if (!msg.equalsIgnoreCase("success")) {
            textInputLayout.setError(msg);
            return false;
        } else {
            textInputLayout.setError(null);
            return true;
        }
    }

    private boolean validateOption(TextInputLayout textInputLayout, TextInputEditText editText, boolean isBlankCheck) {
        String msg = UIValidation.optionValidate(Objects.requireNonNull(editText.getText()).toString(), isBlankCheck);
        if (!msg.equalsIgnoreCase("success")) {
            textInputLayout.setError(msg);
            return false;
        } else {
            textInputLayout.setError(null);
            return true;
        }
    }

    private class MyTextWatcher implements TextWatcher {

        private View view;

        MyTextWatcher(View view) {
            this.view = view;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            switch (view.getId()) {
                case R.id.et_question:
                    validateQuestion(tilQuestion, etQuestion);
                    break;
                case R.id.et_option_a:
                    validateOption(tilOptionA, etOptionA, true);
                    break;
                case R.id.et_option_b:
                    validateOption(tilOptionB, etOptionB, true);
                    break;
                case R.id.et_option_c:
                    validateOption(tilOptionC, etOptionC, false);
                    break;
                case R.id.et_option_d:
                    validateOption(tilOptionD, etOptionD, false);
                    break;
            }

        }

        @Override
        public void afterTextChanged(Editable s) {
        }
    }
}