package com.hfad.faceclassifier.HelperClasses;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hfad.faceclassifier.ModelClasses.Hairstyle;
import com.hfad.faceclassifier.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class RecommendedHairStyleAdapter extends RecyclerView.Adapter<RecommendedHairStyleAdapter.
        ViewHolder> {

    private ArrayList<Hairstyle> mHairStyles;
    private OnItemClickListener mListener;

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mListener = listener;
    }

    public interface OnItemClickListener {
        void onFavoriteClick(int position, ImageView favoriteIconImg);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        // UI Elements in each view of RV
        public ImageView recHairStyleImg, recFavoriteIcon;
        public TextView recFaceShape, recScore;
        public RatingBar recRatingBar;

        public ViewHolder(View itemView, final OnItemClickListener listener) {
            super(itemView);
            recHairStyleImg = itemView.findViewById(R.id.rec_hairstyle_image);
            recFaceShape = itemView.findViewById(R.id.rec_face_shape);
            recScore = itemView.findViewById(R.id.rec_score);
            recRatingBar = itemView.findViewById(R.id.rec_ratingBar);
            recFavoriteIcon = itemView.findViewById(R.id.rec_favorite_icon);

            recFavoriteIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION)
                            listener.onFavoriteClick(position, recFavoriteIcon);
                    }
                }
            });
        }
    }

    public RecommendedHairStyleAdapter(ArrayList<Hairstyle> mHairStyles) {
        this.mHairStyles = mHairStyles;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recommended_hairstyle, parent, false);
        ViewHolder viewHolder = new ViewHolder(view, mListener);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Hairstyle currentHairStyle = mHairStyles.get(position);
        Picasso.get()
                .load(currentHairStyle.getImageURL())
                .placeholder(R.mipmap.ic_launcher_round)
                .fit()
                .centerInside()
                .into(holder.recHairStyleImg);

        //holder.recHairStyleImg.setImageResource(currentHairStyle.getImageResourceId());
        holder.recFaceShape.setText(currentHairStyle.getFaceshape());
        holder.recFavoriteIcon.setImageDrawable(holder.recFavoriteIcon.getContext().getResources()
                .getDrawable(currentHairStyle.getFavoriteIconResourceId())

        );
        // holder.recFavoriteIcon.setImageResource(currentHairStyle.getFavoriteIconResourceId());
    }

    @Override
    public int getItemCount() {
        return mHairStyles.size();
    }



}
