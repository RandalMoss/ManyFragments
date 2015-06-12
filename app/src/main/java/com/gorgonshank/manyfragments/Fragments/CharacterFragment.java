package com.gorgonshank.manyfragments.Fragments;

import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.gorgonshank.manyfragments.Data.CharacterData;
import com.gorgonshank.manyfragments.R;

import java.io.IOException;

public class CharacterFragment extends Fragment implements Hideable {

    private static final String TAG = "CharacterFragment";

    private TextView characterName, characterHP;
    private ImageView characterPortrait;
    private MediaPlayer mp;
    private CheckBox check;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.character_fragment, container, false);

        characterName = (TextView) v.findViewById(R.id.characterName);
        String cName = "Fighter";
        characterName.append(cName);

        characterHP = (TextView) v.findViewById(R.id.characterHP);
        String cHP = CharacterData.getHit_points() + "/" + CharacterData.getMax_hit_points();
        characterHP.append(cHP);

        characterPortrait = (ImageView) v.findViewById(R.id.characterPortrait);
        characterPortrait.setImageResource(R.drawable.fighter);

        Button add = (Button) v.findViewById(R.id.increase_hit_points_button);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                long tempHP = CharacterData.getHit_points();
                tempHP +=500;

                if(tempHP > CharacterData.getMax_hit_points()) {
                    tempHP = CharacterData.getMax_hit_points();
                } else if(tempHP < 0) {
                    tempHP = 0;
                }

                String text = "HP: " + tempHP + "/" + CharacterData.getMax_hit_points();

                playSound("chaching.ogg");

                CharacterData.setHit_points(tempHP);
                characterHP.setText(text);
            }
        });

        Button subtract = (Button) v.findViewById(R.id.decrease_hit_points_button);
        subtract.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                long tempHP = CharacterData.getHit_points();
                tempHP -=100;

                if(tempHP > CharacterData.getMax_hit_points()) {
                    tempHP = CharacterData.getMax_hit_points();
                } else if(tempHP < 0) {
                    tempHP = 0;
                }

                String text = "HP: " + tempHP + "/" + CharacterData.getMax_hit_points();

                playSound("smack.wav");

                CharacterData.setHit_points(tempHP);
                characterHP.setText(text);
            }
        });

        check = (CheckBox) v.findViewById(R.id.checkBox);
        check.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //is chkIos checked?
                if (((CheckBox) v).isChecked()) {
                    playSound("character_sound.mid");
                } else {
                    mp.stop();
                }

            }
        });

        return v;
    }

    public void playSound(String fileName) {

        //String fileName = "";
        AssetFileDescriptor afd = null;

        try{
            afd = getActivity().getAssets().openFd(fileName);
        } catch(IOException e) {
            Log.i("Error", "Assets file I/O Error");
        }

        mp = new MediaPlayer();
        try{
            mp.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(),afd.getLength());
            mp.prepare();
            mp.start();
        } catch (IOException e) {
            Log.i("Error", "Media player I/O Exception");
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        // Make sure that we are currently visible
        if (this.isVisible()) {
            // Method to change things when coming into focus
            onShowFragment();
            if (!isVisibleToUser) {
                // Method to change things when leaving focus
                onHideFragment();
            }
        }
    }

    public void onShowFragment() {

        characterHP = (TextView) getView().findViewById(R.id.characterHP);
        String text = "HP: " + CharacterData.getHit_points() + "/" + CharacterData.getMax_hit_points();
        characterHP.setText(text);
    }

    public void onHideFragment() {

    }






    public static CharacterFragment newInstance(String text) {

        CharacterFragment f = new CharacterFragment();
        Bundle b = new Bundle();
        b.putString("msg", text);

        f.setArguments(b);



        return f;
    }
}