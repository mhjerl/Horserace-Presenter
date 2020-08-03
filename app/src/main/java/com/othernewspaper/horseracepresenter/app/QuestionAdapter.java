package com.othernewspaper.horseracepresenter.app;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.othernewspaper.horseracepresenter.R;

import java.util.ArrayList;

public class QuestionAdapter extends RecyclerView.Adapter<QuestionAdapter.QuestionViewHolder> {

    private Context mContext;
    private ArrayList<QuestionItem> questionsList;
    private OnItemClickListener mListener;

    public QuestionAdapter(Context mContext, ArrayList<QuestionItem> questionsList) {
        this.mContext = mContext;
        this.questionsList = questionsList;
    }

    public void setOnItemClickListener(OnItemClickListener mListener) {
        this.mListener = mListener;
    }

    @NonNull
    @Override
    public QuestionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.questions_row, parent, false);
        return new QuestionViewHolder(v, mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull QuestionViewHolder holder, int position) {
        QuestionItem currentItem = questionsList.get(position);
        holder.tvQuestion.setText(currentItem.getQuestion());
    }

    @Override
    public int getItemCount() {
        return questionsList.size();
    }

    public interface OnItemClickListener {
        void onEditItem(int position);
        void onDeleteItem(int position);
    }

    public static class QuestionViewHolder extends RecyclerView.ViewHolder {

        public TextView tvQuestion;
        public ImageButton ibEdit;
        public ImageButton ibDelete;

        public QuestionViewHolder(@NonNull View itemView, final OnItemClickListener onItemClickListener) {
            super(itemView);
            tvQuestion = itemView.findViewById(R.id.tv_question);
            ibEdit = itemView.findViewById(R.id.ib_edit_question);
            ibDelete = itemView.findViewById(R.id.ib_delete_question);

            ibEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(onItemClickListener != null) {
                        int position = getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION) {
                            onItemClickListener.onEditItem(position);
                        }
                    }

                }
            });

            ibDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(onItemClickListener != null) {
                        int position = getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION) {
                            onItemClickListener.onDeleteItem(position);
                        }
                    }

                }
            });
        }
    }

}