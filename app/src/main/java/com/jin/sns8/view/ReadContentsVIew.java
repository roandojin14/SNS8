package com.jin.sns8.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.ui.PlayerView;
import com.jin.sns8.PostInfo;
import com.jin.sns8.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

public class ReadContentsVIew extends LinearLayout {
    private Context context;
    private LayoutInflater layoutInflater;
    private ArrayList<SimpleExoPlayer> playerArrayList = new ArrayList<>();
    private int moreIndex = -1;

    public ReadContentsVIew(Context context) {
        super(context);
        this.context = context;
        initView();
    }

    public ReadContentsVIew(Context context, @Nullable AttributeSet attributeSet) {
        super(context, attributeSet);
        this.context = context;
        initView();
    }

    private void initView(){
        setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        setOrientation(LinearLayout.VERTICAL);
        layoutInflater = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        layoutInflater.inflate(R.layout.view_post, this, true);
    }

    public void setMoreIndex(int moreIndex){
        this.moreIndex = moreIndex;
    }

    public void setPostInfo(PostInfo postInfo){
        TextView createdAtTextView = findViewById(R.id.createAtTextView);
        createdAtTextView.setText(new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(postInfo.getCreatedAt()));

        LinearLayout contentsLayout = findViewById(R.id.contentsLayout);
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        ArrayList<String> contentsList = postInfo.getContents();
        ArrayList<String> formatList = postInfo.getFormats();

        for (int i = 0; i < contentsList.size(); i++) {
            if (i == moreIndex) {
                TextView textView = new TextView(context);
                textView.setLayoutParams(layoutParams);
                textView.setText("더보기...");
                contentsLayout.addView(textView);
                break;
            }

            String contents = contentsList.get(i);
            String formats = formatList.get(i);

            if(formats.equals("image")){
                ImageView imageView = (ImageView)layoutInflater.inflate(R.layout.view_contents_image, this, false);
                contentsLayout.addView(imageView);
                Glide.with(this).load(contents).override(1000).thumbnail(0.1f).into(imageView);
            }else if(formats.equals("video")){
                PlayerView playerView = (PlayerView)layoutInflater.inflate(R.layout.view_contents_image, this, false);
                ExoPlayer player = new ExoPlayer.Builder(context).build();
                // Bind the player to the view.
                playerView.setPlayer(player);

                // Build the media item.
                MediaItem mediaItem = MediaItem.fromUri(contents);
                // Set the media item to be played.
                player.setMediaItem(mediaItem);
                // Prepare the player.
                player.prepare();
                // Start the playback.
                player.play();

            }else{
                TextView textView = (TextView) layoutInflater.inflate(R.layout.view_contents_text, this, false);
                textView.setText(contents);
                contentsLayout.addView(textView);
            }
        }
    }

    public ArrayList<SimpleExoPlayer> getPlayerArrayList() {
        return playerArrayList;
    }
}
