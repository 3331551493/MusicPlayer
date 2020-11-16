package com.example.musicplayer.adapter;

import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musicplayer.R;
import com.example.musicplayer.bean.Music;

import java.util.List;

public class MusicListAdapter extends RecyclerView.Adapter<MusicListAdapter.MusicListViewHolder> {
    private static final String TAG = "MusicListAdapter";
    private List<Music> mValues;
    private OnMusicListItemClickedListener musicListItemClickedListener;


    public void setMusicListItemClickedListener(OnMusicListItemClickedListener musicListItemClickedListener) {
        this.musicListItemClickedListener = musicListItemClickedListener;
    }


    public MusicListAdapter(List<Music> musicList) {
        mValues = musicList;
    }

    @NonNull
    @Override
    public MusicListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.music_item, parent, false);
        return new MusicListAdapter.MusicListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MusicListViewHolder holder, int position) {
        Music music = mValues.get(position);
        holder.mNameView.setMovementMethod(LinkMovementMethod.getInstance());
        holder.mNameView.setText(music.getName());
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public interface OnMusicListItemClickedListener {
        void onClick(int position);
    }

    class MusicListViewHolder extends RecyclerView.ViewHolder {
        private final TextView mNameView;

        public MusicListViewHolder(@NonNull View itemView) {
            super(itemView);
            mNameView = (TextView) itemView.findViewById(R.id.item_music_name);
        }
    }
}
