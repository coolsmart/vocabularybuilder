package com.example.rv720.vocabulary;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;
import java.util.Locale;

import static java.lang.Integer.parseInt;


public class vocabularycard extends AppCompatActivity {
    SQLiteDatabase vocabularyDB = null;
    Cursor mycursor;
    TextView vocabulary,meaning,memorycode,dummy,pronunciation;
    CheckBox ismemorised,showmemorised,showsentence;
    Switch voiceswitch=null;
    int sayac=1, numberofrecords,lastrecordid, numberofrecords,memorisednumber;
    TextToSpeech tts;
    String selectedlanguage="GERMAN";
    ImageView image;
    RadioButton all;
    Boolean dur=true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vocabularykart);

        this.getSupportActionBar().hide();

        vocabularyDB = this.openOrCreateDatabase("Vocabulary", MODE_PRIVATE, null);

        vocabulary = (TextView) findViewById(R.id.id_vocabulary);
        pronunciation = (TextView) findViewById(R.id.id_pronunciation);
        meaning = (TextView) findViewById(R.id.id_meaning);
        memorycode = (TextView) findViewById(R.id.id_memorycode);
        ismemorised = (CheckBox) findViewById(R.id.id_checkbox);
        showmemorised = (CheckBox) findViewById(R.id.id_showmemorised);
        showsentence = (CheckBox) findViewById(R.id.id_showsentence);
        image = (ImageView) findViewById(R.id.id_image);
        all = (RadioButton) findViewById(R.id.id_all);
        voiceswitch = (Switch) findViewById(R.id.id_voice);

        dummy = (TextView) findViewById(R.id.id_vocabularysayisi);

        try { 
          mycursor = vocabularyDB.rawQuery("SELECT * FROM Vocabulary", null);
     	    numberofrecords=mycursor.getCount();
    	    mycursor = vocabularyDB.rawQuery("SELECT * FROM Vocabulary WHERE ismemorised=1", null);
    	    memorisednumber=mycursor.getCount();
          mycursor = vocabularyDB.rawQuery("SELECT * FROM Vocabulary WHERE ismemorised=0 ORDER BY id DESC", null);
    	    mycursor.moveToFirst();
          lastrecordid = parseInt(mycursor.getString(0)); //id ye gore azalan siraladigimiz icin ilk kayit aslinda son id oluyor
          numberofrecords = mycursor.getCount();
          kayitlarigetir();}
        catch(Throwable e) {   
        Toast.makeText(this, "no vocabulary found..", Toast.LENGTH_SHORT).show();
        }

        ismemorised.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                String vocabulary_dummy = mycursor.getString(1);
                if (isChecked) {
                    vocabularyDB.execSQL("UPDATE Vocabulary SET ismemorised=1 WHERE vocabulary="+ "'" + vocabulary_dummy + "'"+ ";");
                    }
                    else {
                    vocabularyDB.execSQL("UPDATE Vocabulary SET ismemorised=0 WHERE vocabulary=" + "'" + vocabulary_dummy + "'" + ";");
                }
            }
        });

        showmemorised.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) { //ezberlenenler dahil goster


                if (isChecked) {
                    all.setChecked(true);
                    showmemorised.setChecked(true);
                    mycursor = vocabularyDB.rawQuery("SELECT * FROM Vocabulary ORDER BY ismemorised DESC, id DESC", null);
                    if (showsentence.isChecked()) {
                        mycursor = vocabularyDB.rawQuery("SELECT * FROM Vocabulary WHERE memorycode LIKE '%*%' ORDER BY ismemorised DESC, id DESC", null);
                    }
                    mycursor.moveToFirst();
                    lastrecordid = parseInt(mycursor.getString(0)); 
                    numberofrecords = mycursor.getCount();
                    sayac=1;

                    getrecords();
                }
                else {
                    mycursor = vocabularyDB.rawQuery("SELECT * FROM Vocabulary WHERE ismemorised=0 ORDER BY id DESC", null);

                    if (showsentence.isChecked()) {
                        mycursor = vocabularyDB.rawQuery("SELECT * FROM Vocabulary WHERE (memorycode LIKE '%*%') AND (ismemorised=0) ORDER BY id DESC", null);
                    }

                    mycursor.moveToFirst();
                    lastrecordid = parseInt(mycursor.getString(0)); 
                    numberofrecords = mycursor.getCount();
                    sayac=1;

                    kayitlarigetir();
                }
            }
        });

     showsentence.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    all.setChecked(true);
                    showmemorised.setChecked(true);
                    mycursor = vocabularyDB.rawQuery("SELECT * FROM Vocabulary WHERE memorycode LIKE '%*%' ORDER BY ismemorised DESC, id DESC", null);
                    mycursor.moveToFirst();
                    lastrecordid = parseInt(mycursor.getString(0)); 
                    numberofrecords = mycursor.getCount();
                    sayac=1;
                    kayitlarigetir();
                }
                else {
                    mycursor = vocabularyDB.rawQuery("SELECT * FROM Vocabulary WHERE ismemorised=0 ORDER BY id DESC", null);
                    mycursor.moveToFirst();
                    lastrecordid = parseInt(mycursor.getString(0));
                    numberofrecords = mycursor.getCount();
                    sayac=1;
                    showmemorised.setChecked(false);
                    kayitlarigetir();
                }
            }
        });

        voiceswitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // TODO Auto-generated method stub
                if (!buttonView.isChecked())
                {dur=true;}
                else {dur=false;}
            }
        });
       //--------swipe----------------
        View rootView = findViewById(R.id.id_layout);
        rootView.setOnTouchListener(new onSwipeTouchListener(vocabularykart.this) {
            @Override
            public void onSwipeLeft() {
                sonraki2();
            }
            public void onSwipeRight() {
                onceki2();
            }
        });
         //----------repetition period---------------------

        RadioGroup radioGroup = (RadioGroup) findViewById(R.id.id_tekrarradiogrup);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override

            public void onCheckedChanged(RadioGroup group, int checkedId) {
                String mySQL;
                showmemorised.setChecked(false);
                if(checkedId == R.id.id_day) {
                    try {
                    mySQL="SELECT * FROM Vocabulary WHERE date=date('now','-1 day') ORDER BY id DESC";
                    tekrarkayitlarinigetir(mySQL);}
                    catch(Throwable e) {
                     Toast.makeText(getApplicationContext(), "No word found..", Toast.LENGTH_SHORT).show();
                    }
                    }
                else if (checkedId == R.id.id_hafta) {
                    try {
                        mySQL="SELECT * FROM Vocabulary WHERE date=date('now','-7 day') ORDER BY id DESC";
                        tekrarkayitlarinigetir(mySQL);}
                    catch(Throwable e) {
                        Toast.makeText(getApplicationContext(), "No word found....", Toast.LENGTH_SHORT).show();
                    }
                }
                else if (checkedId == R.id.id_month){
                    try {
                        mySQL="SELECT * FROM Vocabulary WHERE date=date('now','-30 day') ORDER BY id DESC";
                        tekrarkayitlarinigetir(mySQL);}
                    catch(Throwable e) {
                        Toast.makeText(getApplicationContext(), "No word found..", Toast.LENGTH_SHORT).show();
                    }
                }
                else if (checkedId == R.id.id_today){
                    try {
                        mySQL="SELECT * FROM Vocabulary WHERE date=date('now','0 day') ORDER BY id DESC";
                        tekrarkayitlarinigetir(mySQL);}
                    catch(Throwable e) {
                        Toast.makeText(getApplicationContext(), "No word found..", Toast.LENGTH_SHORT).show();
                    }
                }
                else{
                    mySQL="SELECT * FROM Vocabulary WHERE ismemorised=0 ORDER BY id DESC";
                    tekrarkayitlarinigetir(mySQL);
                }

            }

       //-------------------------------------

        public void getrepetitionrecords(String mySQL){
        mycursor = vocabularyDB.rawQuery(mySQL, null);
        mycursor.moveToFirst();
        lastrecordid = parseInt(mycursor.getString(0));
        numberofrecords = mycursor.getCount();
        sayac=1;
        kayitlarigetir();
        }
});  
   }

//----------------------------------------------
    public void getrecords() {
        vocabulary.setText(mycursor.getString(1));
        pronunciation.setText(mycursor.getString(2));
        meaning.setText(mycursor.getString(3));
        memorycode.setText(mycursor.getString(4));
        dummy.setText(sayac+"/"+numberofrecords+"  ("+numberofrecords+"/"+memorisednumber+"✓)");

        final int ismemorised_dummy = parseInt(mycursor.getString(5));

        if (ismemorised_dummy == 1) {
            ismemorised.setChecked(true);
        } else {ismemorised.setChecked(false);}

        Picasso.with(vocabularykard.this)
                .load("file:///storage/sdcard0/vocabularyimage/"+mycursor.getString(1)+".jpg")
                .memoryPolicy(MemoryPolicy.NO_CACHE)
                .into(image);
        if (ismemorised.isChecked()){
        vocabulary.setText(mycursor.getString(1)+"✓");
        vocabulary.setBackgroundColor(Color.parseColor("#FFDAFF8F"));}
        else {vocabulary.setBackgroundColor(Color.parseColor("#FFFFD48F"));}

    }
//----------------------------------------------
    public void next(View v){

        if (sayac<numberofrecords){
            mycursor.moveToNext();
            sayac++;
            getrecords();}
    }
    public void next2(){

        if (sayac<numberofrecords){
            mycursor.moveToNext();
            sayac++;
            getrecords();
        }
    }

//----------------------------------------------
    public void previous(View v){
        int mevcutid=parseInt(mycursor.getString(0));
        if (lastrecordid!=mevcutid){
            mycursor.moveToPrevious();
            sayac--;
            getrecords();}
    }
    public void previous2(){
        int mevcutid=parseInt(mycursor.getString(0));
        if (lastrecordid!=mevcutid){
            mycursor.moveToPrevious();
            sayac--;
            getrecords();}
    }
    //----------------------------------------------
    public void voice(){

           tts=new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {

            @Override
            public void onInit(int status) {
                int result;

                String sayi = Integer.toString(sayac);
                result = tts.setLanguage(Locale.getDefault());
                tts.speak(sayi, TextToSpeech.QUEUE_ADD, null);
                if (!dur) {
                    for (int l = 1; l <= 3; l++) {
                        result = tts.setLanguage(Locale.GERMANY);
                        tts.speak(mycursor.getString(1), TextToSpeech.QUEUE_ADD, null);
                        //result = tts.setLanguage(Locale.US);
                        result=tts.setLanguage(Locale.getDefault());
                        tts.speak(mycursor.getString(3), TextToSpeech.QUEUE_ADD, null);
                        if(!mycursor.getString(4).contains("*")) 
                        { if(mycursor.getString(4).contains("-")) 
                          { result = tts.setLanguage(Locale.getDefault());
                            String okunacakisim=mycursor.getString(4).substring(0, mycursor.getString(4).indexOf("-"));
                            tts.speak(okunacakisim, TextToSpeech.QUEUE_ADD, null);}
                            else {
                            result = tts.setLanguage(Locale.getDefault());
                            tts.speak(mycursor.getString(4), TextToSpeech.QUEUE_ADD, null);
                        }
                        }
                        if (dur){tts.stop();}
                    }
                }
            }
        });
    }
//------------------------------------
    public void selectedword(View v) {

                Intent myintent = new Intent(getApplicationContext(), selectedword.class);
                String myvocabularyid = mycursor.getString(0);
                myintent.putExtra("secilenvocabularyid", myvocabularyid);
                startActivity(myintent);
    }
    //------------------------------------
    @Override
    public void onBackPressed()
    {
        dur=true;
        voiceswitch.setChecked(false);
        mycursor.close();
        finish();
    }
//----------------------------------------------

    public void vocabularyvoice(View v){
    final Handler myhandler = new Handler();
    sayac=0;

        Runnable task = new Runnable() {
        @Override
        public void run() {
            if (!stop) {
                myhandler.postDelayed(this, 20000);
                voice();
               if (sayac>=1){
                   next2();
                   if(mycursor.getString(4).contains("*")){next2();}
               }
               else {
                   sayac++;
                   getrecords();}
            }
            else
                {myhandler.removeCallbacksAndMessages(null);
                tts.stop();} 
        }
    };
      myhandler.post(task);
    }

    //----------------------------------------------
    public void savelastword(View v){
        SharedPreferences.Editor myeditor = getSharedPreferences("MyPref", MODE_PRIVATE).edit();
        //myeditor.putString("name", "Elena");

        if (voiceswitch.isChecked()){ 
            int voicevocabularyid=parseInt(mycursor.getString(0));
            myeditor.putInt("voicevocabularyid",voicevocabularyid );
            Toast.makeText(this, "Saved..", Toast.LENGTH_SHORT).show();
        }
        else { 
        int vocabularyid=parseInt(mycursor.getString(0));
        myeditor.putInt("vocabularyid",vocabularyid );
        Toast.makeText(this, "Saved..", Toast.LENGTH_SHORT).show();
        }
        myeditor.commit();
    }

    public void golastsaved(View v){
        int vocabularyid;
        SharedPreferences myprefs = getSharedPreferences("MyPref", MODE_PRIVATE);
        //String name = myprefs.getString("name", "No name defined");

        if (voiceswitch.isChecked()){ 
            vocabularyidsi = myprefs.getInt("voicevocabularyid", 0);}
        else {vocabularyidsi = myprefs.getInt("vocabularyid", 0);} 
            mycursor.moveToFirst();
            do {
                if ( parseInt(mycursor.getString(0))== vocabularyid)
                {break;}
                sayac++;
            } while (mycursor.moveToNext());

        getrecords();
    }


}
