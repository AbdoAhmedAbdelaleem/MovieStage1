package com.example.abdo.moviesstage1;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Abdo on 9/19/2017.
 */

public class GridAdapter extends ArrayAdapter<MovieEntry> {
    ArrayList<MovieEntry> Data;
    Context Context;

    public GridAdapter(Context context, ArrayList<MovieEntry> data) {
        super(context, 0, data);
        this.Context = context;
        this.Data = data;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(this.Context).inflate(R.layout.grid_item, parent, false);
        }
        Bitmap posterImage = this.Data.get(position).moviePosterIMG;
        String imgSURlString=this.Data.get(position).moviePosterPath;
        ImageView imgView = convertView.findViewById(R.id.gridImage);
        Picasso.with(convertView.getContext()).load(imgSURlString).into(imgView);
        return convertView;
    }
}
