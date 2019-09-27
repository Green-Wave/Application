package com.example.greenwave;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;

import android.widget.ImageView;
import android.widget.TextView;

public class Suggested_speed extends AppCompatActivity {
    AnimationDrawable up_animation;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suggested_speed);

        int current_speed=15;
        int suggested_speed = 17;
        TextView tv = (TextView)findViewById(R.id.my_text_view);
        tv.setText("Your speed"+current_speed);
        ImageView imageView = (ImageView)findViewById(R.id.image);
        imageView.setBackgroundResource(R.drawable.animation_arrows);
        up_animation = (AnimationDrawable)imageView.getBackground();
    }

    @Override
    public void onWindowFocusChanged (boolean hasFocus){
        super.onWindowFocusChanged(hasFocus);
        up_animation.start();
    }
}
