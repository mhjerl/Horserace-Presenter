package com.othernewspaper.horseracepresenter;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NavUtils;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.othernewspaper.horseracepresenter.R;
import com.othernewspaper.horseracepresenter.app.AppConfig;
import com.othernewspaper.horseracepresenter.app.AppController;
import com.othernewspaper.horseracepresenter.app.QuestionAdapter;
import com.othernewspaper.horseracepresenter.app.QuestionItem;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class QuestionsActivity extends AppCompatActivity {

    private static final String TAG = QuestionsActivity.class.getSimpleName();
    Toolbar toolbar;
    private RecyclerView rvQuestions;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ArrayList<QuestionItem> questionsList;
    private QuestionAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_questions);
        toolbar = findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        swipeRefreshLayout = findViewById(R.id.questions_swipe_layout);
        questionsList = new ArrayList<>();
        rvQuestions = findViewById(R.id.rv_questions);
        adapter = new QuestionAdapter(getApplicationContext(), questionsList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        rvQuestions.setLayoutManager(mLayoutManager);
        rvQuestions.addItemDecoration(new QuestionsActivity.SpacingItemDecoration(dpToPx(10)));
        rvQuestions.setItemAnimator(new DefaultItemAnimator());
        rvQuestions.setAdapter(adapter);
        prepareQuestions();
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                prepareQuestions();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
        adapter.setOnItemClickListener(new QuestionAdapter.OnItemClickListener() {
            @Override
            public void onEditItem(int position) {
                Intent i = new Intent(QuestionsActivity.this, AddQuestionActivity.class);
                i.putExtra("ACTION", "EDIT");
                i.putExtra("QUESTION", questionsList.get(position));
                i.putExtra("QUESTION_NO", position);
                startActivity(i);
            }

            @Override
            public void onDeleteItem(final int position) {

                AlertDialog.Builder builder = new AlertDialog.Builder(QuestionsActivity.this);
                //Uncomment the below code to Set the message and title from the strings.xml file
                builder.setMessage("Are you sure to delete this question?")
                        .setTitle("Delete Question")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                deleteQuestion(questionsList.get(position).getId(), position);
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

    private void deleteQuestion(final int qId, final int position) {

        // Tag used to cancel the request
        String tag_string_req = "req_delete_question";
        StringRequest strReq = new StringRequest(Request.Method.POST, AppConfig.URL_PRESENTER,
                new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.e(TAG, response);
                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    // Check for error node in json
                    if (!error) {
                        removeItem(position);
                        Toast.makeText(getApplicationContext(), "Question deleted successfully!", Toast.LENGTH_LONG).show();

                    } else {
                        String errorMsg = jObj.getString("error_msg");
                        Log.e(TAG, errorMsg);
                        Toast.makeText(getApplicationContext(), errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    Log.e(TAG, e.getMessage());
                    Toast.makeText(getApplicationContext(),
                            "Please check your internet connection!", Toast.LENGTH_LONG).show();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, error.getMessage());
                Toast.makeText(getApplicationContext(),
                        "Please check your internet connection!", Toast.LENGTH_LONG).show();

            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("action", "delete_question");
                params.put("question_id", Integer.toString(qId));
                return params;
            }

        };
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);

    }

    private void removeItem(int position) {
        questionsList.remove(position);
        adapter.notifyItemRemoved(position);
    }

    private void prepareQuestions() {
        AppController.getInstance().cancelPendingRequests("request_load_questions");
        // Tag used to cancel the request
        String tag_string_req = "request_load_questions";
        StringRequest strReq = new StringRequest(Request.Method.GET, AppConfig.URL_PRESENTER, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.e(TAG, response);
                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    // Check for error node in json
                    if (!error) {
                        questionsList.clear();
                        JSONArray questions = jObj.getJSONArray("questions");
                        for (int i = 0; i < questions.length(); i++) {
                            JSONObject jQuestion = questions.getJSONObject(i);
                            QuestionItem questionItem = new QuestionItem();
                            questionItem.setId(jQuestion.getInt("id"));
                            questionItem.setQuestion(jQuestion.getString("question"));
                            questionItem.setOption_a(jQuestion.getString("option_a"));
                            questionItem.setOption_b(jQuestion.getString("option_b"));
                            questionItem.setOption_c(jQuestion.getString("option_c"));
                            questionItem.setOption_d(jQuestion.getString("option_d"));
                            questionItem.setTime(jQuestion.getInt("question_time"));
                            questionItem.setAns(jQuestion.getString("ans"));
                            questionsList.add(questionItem);
                        }
                        adapter.notifyDataSetChanged();
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
        });
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    /**
     * Converting dp to pixel
     */
    private int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add_question:
                Intent i = new Intent(QuestionsActivity.this, AddQuestionActivity.class);
                i.putExtra("ACTION", "ADD");
                startActivity(i);
                return true;
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(QuestionsActivity.this);
                return true;
            default:

        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * RecyclerView item decoration - give equal margin around grid item
     */
    public class SpacingItemDecoration extends RecyclerView.ItemDecoration {

        private int spacing;

        public SpacingItemDecoration(int spacing) {
            this.spacing = spacing;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            //int position = parent.getChildAdapterPosition(view); // item position
            outRect.left = spacing; // spacing - column * ((1f / spanCount) * spacing)
            outRect.right = spacing;// (column + 1) * ((1f / spanCount) * spacing)
            outRect.top = spacing;
            outRect.bottom = spacing; // item bottom

        }
    }
}