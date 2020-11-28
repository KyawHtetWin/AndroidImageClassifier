package com.hfad.faceclassifier.HelperClasses;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.hfad.faceclassifier.Database.Hairstyle;
import com.hfad.faceclassifier.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class HairstyleImagesAdapter extends RecyclerView.Adapter<HairstyleImagesAdapter.ViewHolder> {

    // Each view has face shape of hairstyle and image
    private ArrayList<Hairstyle> hairstyles;

    private Listener listener;
    public interface Listener {
        void onClick(int position);
    }

    // Activities and Fragments use this method to registers a listener
    public void setListener(Listener listener){
        this.listener = listener;
    }

    // Constructor
    public HairstyleImagesAdapter(ArrayList<Hairstyle> hairstyles) {
        this.hairstyles = hairstyles;
    }

    // Defines ViewHolder as inner class (Specify which view should be used for each data item)
    public static class ViewHolder extends RecyclerView.ViewHolder {

        private CardView cardView;

        public ViewHolder(CardView v) {
            super(v);
            cardView = v;
        }
    }

    // Tells adapter the number of data items in RecyclerView
    @Override
    public int getItemCount() { return hairstyles.size(); }

    // Gets called when RV requires a new view holder (i.e Tells how to construct our view holders)
    @Override
    public HairstyleImagesAdapter.ViewHolder onCreateViewHolder(
            // parent is RV itself
            ViewGroup parent, int viewType){
        // Get a LayoutInflater object and use it to turn the layout into a CardView.
        CardView cv = (CardView) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_hairstyle_image, parent, false);
        return new ViewHolder(cv);
    }

    // The RV calls this method when it wants to use(or reuse) a view holder for a new piece of
    // data
    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {

        CardView cardView = holder.cardView;

        // Get current hairstyle
        Hairstyle currentHairStyle = hairstyles.get(position);

        // Set the image view in Cardview
        ImageView hairstyleImg = cardView.findViewById(R.id.hairstyle_image);

        Picasso.get()
                .load(currentHairStyle.getImageURL())
                .placeholder(R.mipmap.ic_launcher_round)
                .fit()
                .centerInside()
                .into(hairstyleImg);

        //hairstyleImg.setImageResource(currentHairStyle.getImageResourceId());
        TextView textView = cardView.findViewById(R.id.face_shape_info);
        textView.setText(currentHairStyle.getFaceshape());

        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onClick(position);
                }
            }
        });
    }

    public void filtered(ArrayList<Hairstyle> filteredHairStyles){
        hairstyles = filteredHairStyles;
        notifyDataSetChanged();
    }
}
