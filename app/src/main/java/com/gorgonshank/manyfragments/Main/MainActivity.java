package com.gorgonshank.manyfragments.Main;

import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;

import com.gorgonshank.manyfragments.Data.CharacterData;
import com.gorgonshank.manyfragments.Fragments.BarcodeFragment;
import com.gorgonshank.manyfragments.Fragments.CharacterEquipmentFragment;
import com.gorgonshank.manyfragments.Fragments.CharacterFragment;
import com.gorgonshank.manyfragments.Fragments.EquippedFragment;
import com.gorgonshank.manyfragments.Fragments.ThirdFragment;
import com.gorgonshank.manyfragments.JSON.JSONReader;
import com.gorgonshank.manyfragments.JSON.JSONWriter;
import com.gorgonshank.manyfragments.R;
import com.gorgonshank.manyfragments.Sprite.Armor;
import com.gorgonshank.manyfragments.Sprite.Weapon;

import org.json.simple.JSONObject;

import java.io.FileNotFoundException;
import java.io.IOException;

public class MainActivity extends FragmentActivity {

    private MediaPlayer mp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if(!SpriteGenerator.hasLoaded){
            SpriteGenerator.initDrawables(this.getResources(), 200, 200);
            SpriteGenerator.hasLoaded = true;
        }
        initializeCharacterData();

        ViewPager pager = (ViewPager) findViewById(R.id.viewPager);
        pager.setPageTransformer(true, new ZoomOutPageTransformer());
        pager.setOffscreenPageLimit(2);
        pager.setAdapter(new MyPagerAdapter(getSupportFragmentManager()));
    }

    public void initializeCharacterData() {

        // Read persistent JSON Data
        JSONReader JSONReader = null;

        try{
            JSONReader = new JSONReader(getApplicationContext().getFilesDir());
        } catch (FileNotFoundException e){
            Log.i("Error", "IOException at startup.");
        }

        JSONObject JSON = JSONReader.getMyObject();

        // Set Static Class Data Here
        long hp = (Long) JSON.get("hit_points");

        JSONObject armorJSON = (JSONObject) JSON.get("armor");
        long longHp = (long)armorJSON.get("hp");
        long longDefense = (long)armorJSON.get("defense");

        Armor armor = new Armor();
        armor.setHp(Integer.parseInt(longHp + ""));
        armor.setDefense(Integer.parseInt(longDefense + ""));

        armorJSON = (JSONObject) JSON.get("gloves");
        longHp = (long)armorJSON.get("hp");
        longDefense = (long)armorJSON.get("defense");

        Armor gloves = new Armor();
        armor.setHp(Integer.parseInt(longHp + ""));
        armor.setDefense(Integer.parseInt(longDefense + ""));


        armorJSON = (JSONObject) JSON.get("shoes");
        longHp = (long)armorJSON.get("hp");
        longDefense = (long)armorJSON.get("defense");

        Armor shoes = new Armor();
        armor.setHp(Integer.parseInt(longHp + ""));
        armor.setDefense(Integer.parseInt(longDefense + ""));

        JSONObject weaponJSON = (JSONObject) JSON.get("weapon1");
        long longAttack = (long)weaponJSON.get("attack");

        Weapon weapon1 = new Weapon();
        weapon1.setAttack(Integer.parseInt(longAttack + ""));

        weaponJSON = (JSONObject) JSON.get("weapon2");
        longAttack = (long)weaponJSON.get("attack");

        Weapon weapon2 = new Weapon();
        weapon2.setAttack(Integer.parseInt(longAttack + ""));

        long defense = (long) JSON.get("defense");
        long attack = (long) JSON.get("attack");
        long maxHp = (long) JSON.get("max_hit_points");
        CharacterData.setHit_points(hp);
        CharacterData.setArmor(armor);
        CharacterData.setGloves(gloves);
        CharacterData.setShoes(shoes);
        CharacterData.setWeapon1(weapon1);
        CharacterData.setWeapon2(weapon2);
        CharacterData.setDefense(defense);
        CharacterData.setAttack(attack);
        CharacterData.setMax_hit_points(maxHp);
        CharacterData.getArmor().setSprite(CharacterData.getArmor().getDrawableFromName());
        CharacterData.getShoes().setSprite(CharacterData.getShoes().getDrawableFromName());
        CharacterData.getGloves().setSprite(CharacterData.getGloves().getDrawableFromName());
        CharacterData.getWeapon1().setSprite(CharacterData.getWeapon1().getDrawableFromName());
        CharacterData.getWeapon2().setSprite(CharacterData.getWeapon2().getDrawableFromName());
    }

    public void playSound(String fileName) {

        //String fileName = "";
        AssetFileDescriptor afd = null;

        try{
            afd = getAssets().openFd(fileName);
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
    protected void onDestroy() {
        super.onDestroy();

        // Persist the Data through our JSON Object
        CharacterData cd = new CharacterData();
        JSONWriter JSONWriter = null;

        try{
            JSONWriter = new JSONWriter(cd, getApplicationContext().getFilesDir());
        } catch(IOException e) {
            Log.i("Error", "IOException");
        }

        playSound("game_save.mp3");
    }

    private class MyPagerAdapter extends FragmentPagerAdapter {

        // Set titles for tabs
        private String tabtitles[] = new String[] {"Character Screen", "Fight Here! :)", "Character Current Equipment", "Equip Here"};

        // If you add a new tab, must change this variable
        private static final int NUMBER_OF_TABS = 4;

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        // Create each new fragment here
        @Override
        public Fragment getItem(int pos) {
            switch(pos) {
                case 0: return CharacterFragment.newInstance("Character Fragment1");
                case 1: return BarcodeFragment.newInstance("BarcodeFragment");
                case 2: return CharacterEquipmentFragment.newInstance("Character Equipment");
                case 3: return EquippedFragment.newInstance("Equipped Fragment");


                default: return ThirdFragment.newInstance("ThirdFragment, Default");
            }
        }

        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }

        @Override
        public int getCount() {
            return NUMBER_OF_TABS;
        }

        public CharSequence getPageTitle(int position) {
            return tabtitles[position];
        }
    }




}