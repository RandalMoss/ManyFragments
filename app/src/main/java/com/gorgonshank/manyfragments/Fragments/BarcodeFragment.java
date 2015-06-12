package com.gorgonshank.manyfragments.Fragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.gorgonshank.manyfragments.Main.BarcodeActivity;
import com.gorgonshank.manyfragments.Main.BattleActivity;
import com.gorgonshank.manyfragments.Main.SpriteGenerator;
import com.gorgonshank.manyfragments.R;
import com.gorgonshank.manyfragments.Sprite.*;
import com.gorgonshank.manyfragments.Sprite.Character;

import java.math.BigInteger;
import java.util.ArrayList;

public class BarcodeFragment extends Fragment {

    ImageView canvas;
    Button scanButton;
    TextView item_textview;
    ArrayList<Drawable> drawables = new ArrayList<Drawable>();
    int drawableWidth = 200;
    int drawableHeight = 200;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.barcode_fragment, container, false);

        canvas = (ImageView)v.findViewById(R.id.canvas);

        scanButton = (Button)v.findViewById(R.id.scanButton);
        scanButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), BarcodeActivity.class);
                startActivity(intent);
                getActivity().finish();

            }
        });

        item_textview = (TextView)v.findViewById(R.id.item);

        Intent bcActivity = getActivity().getIntent();
        String barcode = bcActivity.getStringExtra("barcode");
        if(barcode != null){
            Log.i("Info", "Barcode is " + barcode);
            BigInteger bigBarcode = new BigInteger(barcode);
            Sprite stuff = SpriteGenerator.generateSprite(bigBarcode.intValue());
            Log.i("Sprite", stuff.getClass().toString());
            canvas.setImageDrawable(stuff.getDrawable());
            item_textview.setText(stuff.toString());

            // This is the condition where we determine which method to send it to
            Log.i("Info", stuff.getClass().getName() + "");

            if(stuff.getClass().getName().equals("com.gorgonshank.manyfragments.Sprite.Enemy")) {
                Intent intent = new Intent(getActivity(), BattleActivity.class);
                Log.i("Info", "Yay it worked and we got here!");
                intent.putExtra("Sprite", stuff);
                startActivity(intent);
                getActivity().finish();
            }
        }
        else{
            Log.i("Info", "Barcode is null");
        }

        String passed_barcode = bcActivity.getStringExtra("passed_barcode");

        if(passed_barcode != null){
            BigInteger bigBarcode = new BigInteger(passed_barcode);
            Sprite stuff = SpriteGenerator.generateSprite(bigBarcode.intValue());
            canvas.setImageDrawable(stuff.getDrawable());
            item_textview.setText(stuff.toString());
        }
        else{
            Log.i("Info", "passed_barcode is null");
        }

        /*Long loot = bcActivity.getLongExtra("loot", 100l);

        if(loot != 100l && loot != null){
            Log.i("Info", "loot is " + loot);
            Integer stuff = loot.intValue();
            Log.i("Info", "loot is " + stuff);
            canvas.setImageDrawable(drawables.get(stuff));
        }
        else{
            Log.i("Info", "Loot is null");
        }*/



        return v;
    }

    public static BarcodeFragment newInstance(String text) {

        BarcodeFragment f = new BarcodeFragment();
        Bundle b = new Bundle();
        b.putString("msg", text);

        f.setArguments(b);

        return f;
    }
}
