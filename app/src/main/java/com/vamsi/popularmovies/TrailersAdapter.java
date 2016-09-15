package com.vamsi.popularmovies;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.vamsi.popularmovies.Modal.Trailer;

import java.util.List;

/**
 * Created by Vamsi Smart on 08-09-2016.
 */

public class TrailersAdapter extends RecyclerView.Adapter<TrailersAdapter.ViewHolder> {
    private List<Trailer> trailers;
    private Context context;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivThumb;
        TextView tvName,tvType;
        public ViewHolder(View v) {
            super(v);

            ivThumb = (ImageView) v.findViewById(R.id.ivstTrailer);
            tvName = (TextView) v.findViewById(R.id.tvTrailerName);
            tvType = (TextView) v.findViewById(R.id.tvTrailerType);

        }
    }

    public TrailersAdapter(Context context,List<Trailer> trailers) {
        this.context = context;
        this.trailers = trailers;

    }

    @Override
    public TrailersAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(context).inflate(R.layout.single_trailers, parent, false);

        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        final Trailer trailer = trailers.get(position);

        Picasso.with(context).load(String.format(Globals.youtube_thumb,trailer.getKey())).into(holder.ivThumb);

        holder.tvName.setText(""+trailer.getName());
        holder.tvType.setText(""+trailer.getType());

        holder.ivThumb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                    try {

                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + trailer.getKey()));
                        context.startActivity(intent);

                    } catch (ActivityNotFoundException ex) {

                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(Globals.youtube_url + trailer.getKey()));
                        context.startActivity(intent);

                    }


            }
        });

    }

    @Override
    public int getItemCount() {
        return trailers.size();
    }
}
