package com.gorgonshank.manyfragments.Main;

import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.gorgonshank.manyfragments.Battle.EnemyData;
import com.gorgonshank.manyfragments.Data.CharacterData;
import com.gorgonshank.manyfragments.Data.Constants;
import com.gorgonshank.manyfragments.R;
import com.gorgonshank.manyfragments.Sprite.Sprite;

import java.io.IOException;

public class BattleActivity extends ActionBarActivity{

    private ImageView characterPortrait;
    private ImageView enemyPortrait;
    private Button attack, run, heal, special;
    private TextView charHP_TextView, enemyHP_TextView, combat_text, skill_points_TextView;
    private ScrollView scroll;
    private MediaPlayer mp;
    private LinearLayout battle_background;
    private LinearLayout commands;
    private LinearLayout special_commands;

    private EnemyData combatant;
    private long character_HP, enemy_HP, character_max_HP, enemy_max_HP;
    private int end = 0;
    private Sprite passedSprite;

    private Button skill_1, skill_2, skill_3, go_back;

    private long skill_points;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_battle);

        Intent activity = getIntent();
        passedSprite = (Sprite)activity.getSerializableExtra("Sprite");
        Log.i("Info", passedSprite.getName());
        passedSprite.getDrawableFromName();

        commands = (LinearLayout) findViewById(R.id.commands);
        special_commands = (LinearLayout) findViewById(R.id.special);

        int Min = 0;
        int Max = 1;
        int decision = Min + (int)(Math.random() * ((Max - Min) + 1));

        enemyPortrait = (ImageView) findViewById(R.id.enemy);

        if(decision == 0) {
            battle_background = (LinearLayout) findViewById(R.id.battle_background);
            selectBackground(battle_background);
            playSound("FF6_05_Battle_Theme.mid");
            mp.setLooping(true);
            combatant = new EnemyData();
            enemyPortrait.setImageDrawable(passedSprite.getDrawable());
            Log.i("EnemyData Stats", "HP: " + combatant.getHp() + " Attack: " + combatant.getAttack() + " Defence: " + combatant.getDefence() + "");
        } else if(decision == 1) {
            battle_background = (LinearLayout) findViewById(R.id.battle_background);
            battle_background.setBackgroundResource(R.drawable.battle_background_boss);
            playSound("FF6_48_The_Fierce_Battle.mid");
            mp.setLooping(true);
            combatant = new EnemyData(2);
            enemyPortrait.setImageDrawable(passedSprite.getDrawable());
        }

        characterPortrait = (ImageView) findViewById(R.id.character);
        characterPortrait.setImageResource(R.drawable.fighter);

        charHP_TextView = (TextView) findViewById(R.id.char_HP);
        charHP_TextView.setText("HP: " + CharacterData.getHit_points() + "/" + CharacterData.getMax_hit_points());

        character_HP = CharacterData.getHit_points();
        character_max_HP = CharacterData.getMax_hit_points();

        skill_points = CharacterData.getSkill_points();
        skill_points_TextView = (TextView) findViewById(R.id.skill_text);
        skill_points_TextView.setText("Skill Points: " + skill_points + " / " + CharacterData.getMax_skill_points());

        enemyHP_TextView = (TextView) findViewById(R.id.enemy_HP);
        enemyHP_TextView.setText("EnemyData: " + combatant.getHp() + "/" + combatant.getMaxHP());

        enemy_HP = combatant.getHp();
        enemy_max_HP = combatant.getMaxHP();

        scroll = (ScrollView) findViewById(R.id.scroll);
        combat_text = (TextView) findViewById(R.id.combat_text);

        attack = (Button) findViewById(R.id.attack_button);
        attack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                long damage = (CharacterData.getAttack() - combatant.getDefence());

                if(damage > 0) {
                    combat_text.append("Player did " + damage + " to enemy.\n");
                    scroll.post(new Runnable() {public void run() {scroll.fullScroll(View.FOCUS_DOWN);}   });
                    enemy_HP -= damage;
                    playSound("sword_attack.wav");
                    if(enemy_HP < 0) {
                        enemy_HP = 0;
                    }
                } else {
                    combat_text.append("Player did not do damage to enemy.\n");
                    scroll.post(new Runnable() {public void run() {scroll.fullScroll(View.FOCUS_DOWN);}   });
                }
                enemyHP_TextView.setText("EnemyData: " + enemy_HP + "/" + enemy_max_HP);
                combatant.setHp(enemy_HP);

                skill_points += 1;

                if(skill_points > CharacterData.getMax_skill_points()) {
                    skill_points = CharacterData.getMax_skill_points();
                }

                skill_points_TextView.setText("Skill Points: " + skill_points + " / " + CharacterData.getMax_skill_points());

                //addDelay();

                checkVictoryOrLoss(combatant);
                enemyTurn(combatant);
            }
        });

        run = (Button) findViewById(R.id.run_button);
        run.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int Min = 0;
                int Max = 2;
                int decision = Min + (int)(Math.random() * ((Max - Min) + 1));
                if(decision == 0) {
                    combat_text.append("Ran away.\n");
                    scroll.post(new Runnable() {public void run() {scroll.fullScroll(View.FOCUS_DOWN);}   });

                    Intent mainIntent = new Intent(BattleActivity.this, MainActivity.class);
                    Toast toast = Toast.makeText(getApplicationContext(), "Ran Away" , Toast.LENGTH_LONG);
                    toast.show();

                    end = 0;

                    startActivity(mainIntent);
                    BattleActivity.this.finish();


                } else {
                    combat_text.append("Failed to run away.\n");
                    scroll.post(new Runnable() {public void run() {scroll.fullScroll(View.FOCUS_DOWN);}   });
                    //addDelay();
                    enemyTurn(combatant);
                }
            }
        });

        heal = (Button) findViewById(R.id.heal_button);
        heal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                long healing = 100l;
                character_HP += healing;

                if(character_HP > character_max_HP) {
                    character_HP = character_max_HP;
                }

                CharacterData.setHit_points(character_HP);
                charHP_TextView.setText("HP: " + character_HP + "/" + character_max_HP);
                combat_text.append("Player recovered " + healing + " HP.\n");
                scroll.post(new Runnable() {public void run() {scroll.fullScroll(View.FOCUS_DOWN);}   });
                playSound("heal.mp3");

                //addDelay();

                checkVictoryOrLoss(combatant);
                enemyTurn(combatant);
            }
        });

        special = (Button) findViewById(R.id.special_button);
        special.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                commands.setVisibility(LinearLayout.GONE);
                special_commands.setVisibility(LinearLayout.VISIBLE);
            }
        });

        skill_1 = (Button) findViewById(R.id.Pierce);
        skill_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(skill_points >= Constants.PIERCE_COST) {
                    long damage = (CharacterData.getAttack());

                    combat_text.append("Player uses Pierce, doing " + damage + " damage ignoring defense.\n");
                    scroll.post(new Runnable() {public void run() {scroll.fullScroll(View.FOCUS_DOWN);}   });
                    enemy_HP -= damage;
                    playSound("pierce.wav");
                    if(enemy_HP < 0) {
                        enemy_HP = 0;
                    }
                    enemyHP_TextView.setText("EnemyData: " + enemy_HP + "/" + enemy_max_HP);
                    combatant.setHp(enemy_HP);

                    skill_points -= Constants.PIERCE_COST;
                    skill_points_TextView.setText("Skill Points: " + skill_points + " / " + CharacterData.getMax_skill_points());

                    //addDelay();

                    checkVictoryOrLoss(combatant);
                    enemyTurn(combatant);
                } else {
                    combat_text.append("Thrust skill requires: " + Constants.PIERCE_COST + " to use.\n");
                    scroll.post(new Runnable() {public void run() {scroll.fullScroll(View.FOCUS_DOWN);}   });
                }
            }
        });

        skill_2 = (Button) findViewById(R.id.charge);
        skill_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                skill_points += Constants.CHARGE_SKILL;

                playSound("charge.ogg");
                if(skill_points > CharacterData.getMax_skill_points()) {
                    skill_points = CharacterData.getMax_skill_points();
                }

                skill_points_TextView.setText("Skill Points: " + skill_points + " / " + CharacterData.getMax_skill_points());
                combat_text.append("Player uses charge skill gaining " + Constants.CHARGE_SKILL + " skill points.\n");
                scroll.post(new Runnable() {public void run() {scroll.fullScroll(View.FOCUS_DOWN);}   });

                //addDelay();

                checkVictoryOrLoss(combatant);
                enemyTurn(combatant);
            }
        });

        skill_3 = (Button) findViewById(R.id.super_combo);
        skill_3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(skill_points >= Constants.SUPER_COMBO_COST) {
                    long damage = ((CharacterData.getAttack()*5) - combatant.getDefence());

                    combat_text.append("Player uses Super Combo, doing " + damage + " damage.\n");
                    scroll.post(new Runnable() {public void run() {scroll.fullScroll(View.FOCUS_DOWN);}   });
                    enemy_HP -= damage;
                    playSound("super_combo.mp3");
                    if(enemy_HP < 0) {
                        enemy_HP = 0;
                    }
                    enemyHP_TextView.setText("EnemyData: " + enemy_HP + "/" + enemy_max_HP);
                    combatant.setHp(enemy_HP);

                    skill_points -= Constants.SUPER_COMBO_COST;
                    skill_points_TextView.setText("Skill Points: " + skill_points + " / " + CharacterData.getMax_skill_points());

                    //addDelay();

                    checkVictoryOrLoss(combatant);
                    enemyTurn(combatant);
                } else {
                    combat_text.append("Super Combo skill requires: " + Constants.SUPER_COMBO_COST + " to use.\n");
                    scroll.post(new Runnable() {public void run() {scroll.fullScroll(View.FOCUS_DOWN);}   });
                }
            }
        });

        go_back = (Button) findViewById(R.id.back);
        go_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                special_commands.setVisibility(LinearLayout.GONE);
                commands.setVisibility(LinearLayout.VISIBLE);
            }
        });

    }

    public void buttonsDeselectable() {
        attack.setEnabled(false);
        heal.setEnabled(false);
        special.setEnabled(false);
        skill_1.setEnabled(false);
        skill_2.setEnabled(false);
        skill_3.setEnabled(false);
    }

    public void buttonsSelectable() {
        attack.setEnabled(true);
        heal.setEnabled(true);
        special.setEnabled(true);
        skill_1.setEnabled(true);
        skill_2.setEnabled(true);
        skill_3.setEnabled(true);
    }

    public void addDelay() {
        buttonsDeselectable();
        try{
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Log.i("Battle", "Didn't work");
        }
        buttonsSelectable();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if(end == 1) {
            mp.stop();
            playSound("FF6_06_Fanfare.mid");
        } else if(end == 2) {
            mp.stop();
            playSound("down.ogg");
            try{
                Thread.sleep(1000);
            } catch(InterruptedException e) {

            }

            playSound("laugh.wav");

            try{
                Thread.sleep(1000);
            } catch(InterruptedException e) {

            }

            playSound("FF6_50_Dark_World.mid");


        } else {
            mp.stop();
            playSound("run_away.mp3");
        }
    }

    public void selectBackground(LinearLayout battle_background) {
        int Min = 0;
        int Max = 3;
        int decision = Min + (int)(Math.random() * ((Max - Min) + 1));

        if(decision == 0) {
            battle_background.setBackgroundResource(R.drawable.battle_background_desert);
        } else if(decision == 1) {
            battle_background.setBackgroundResource(R.drawable.battle_background_forest);
        } else if(decision == 2) {
            battle_background.setBackgroundResource(R.drawable.battle_background_mountain);
        } else if(decision == 3) {
            battle_background.setBackgroundResource(R.drawable.battle_background_plains);
        }
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

    public void enemyTurn(EnemyData combatant) {

        int Min = 0;
        int Max = 2;
        int decision = Min + (int)(Math.random() * ((Max - Min) + 1));

        if(combatant.getHp() > 0) {

            if(decision == 0) {
                long damage = (combatant.getAttack() - CharacterData.getDefense());

                if(damage > 0) {
                    combat_text.append("EnemyData did " + damage + " damage to player.\n\n");
                    scroll.fullScroll(ScrollView.FOCUS_DOWN);
                    character_HP -= damage;
                    if(character_HP < 0) {
                        character_HP = 0;
                    }
                } else {
                    combat_text.append("EnemyData did not do damage to player.\n\n");
                    scroll.post(new Runnable() {public void run() {scroll.fullScroll(View.FOCUS_DOWN);}   });
                }
                charHP_TextView.setText("HP: " + character_HP + "/" + character_max_HP);
                CharacterData.setHit_points(character_HP);
                playSound("damage.wav");

            } else if(decision == 1) {
                combat_text.append("EnemyData is loafing around.\n\n");
                scroll.post(new Runnable() {public void run() {scroll.fullScroll(View.FOCUS_DOWN);}   });
            } else if(decision == 2) {
                long heal = 100;
                enemy_HP = combatant.getHp();
                enemy_HP += heal;

                if(enemy_HP > combatant.getMaxHP()) {
                    enemy_HP = combatant.getMaxHP();
                }

                combat_text.append("EnemyData healed itself for " + heal + " HP.\n\n");
                scroll.post(new Runnable() {public void run() {scroll.fullScroll(View.FOCUS_DOWN);}   });
                enemyHP_TextView.setText("EnemyData: " + enemy_HP + "/" + enemy_max_HP);
                combatant.setHp(enemy_HP);
                playSound("heal.mp3");
            }

        }

        checkVictoryOrLoss(combatant);
    }

    public void checkVictoryOrLoss(EnemyData combatant) {
        if(combatant.getHp() <= 0) {
            Intent mainIntent = new Intent(BattleActivity.this, MainActivity.class);
            mainIntent.putExtra("experience", combatant.getExperience());
            //mainIntent.putExtra("loot", combatant.getLoot());
            Toast toast = Toast.makeText(getApplicationContext(), "You win " + " loot: " + combatant.getLoot(), Toast.LENGTH_LONG);
            toast.show();

            end = 1;

            startActivity(mainIntent);
            BattleActivity.this.finish();
        } else if(CharacterData.getHit_points() <= 0) {
            Intent mainIntent = new Intent(BattleActivity.this, MainActivity.class);
            Toast toast = Toast.makeText(getApplicationContext(), "You lose.", Toast.LENGTH_LONG);
            toast.show();

            end = 2;

            startActivity(mainIntent);
            BattleActivity.this.finish();
        }
    }

}
