package com.example.rv720.vocabulary;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import java.util.Locale;

public class save extends AppCompatActivity {
    SQLiteDatabase vocabularyDB = null;
    EditText vocabulary, pronunciation, meaning, memorycode,id;
    CheckBox ismemorised,die,der,das,issentence;
    Cursor mycursor;
    TextToSpeech tts;
    ImageView delete,image;
    String link;
    InputMethodManager keyboard;


    private static final int PICK_IMAGE_REQUEST = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.save);

        this.getSupportActionBar().hide();

        vocabulary = (EditText) findViewById(R.id.id_vocabulary);
        pronunciation = (EditText) findViewById(R.id.id_pronunciation);
        meaning = (EditText) findViewById(R.id.id_meaning);
        memorycode = (EditText) findViewById(R.id.id_memorycode);
        id = (EditText) findViewById(R.id.id_id);
        ismemorised=(CheckBox) findViewById(R.id.id_checkbox);
        issentence=(CheckBox) findViewById(R.id.id_sentence);
        die=(CheckBox) findViewById(R.id.id_die);
        der=(CheckBox) findViewById(R.id.id_der);
        das=(CheckBox) findViewById(R.id.id_das);
        delete=(ImageView)findViewById(R.id.id_delete);

        ismemorised.setVisibility(View.INVISIBLE);
  
        vocabularyDB = this.openOrCreateDatabase("Vocabulary", MODE_PRIVATE, null);

        keyboard = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        keyboard.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);

        GradientDrawable gd = new GradientDrawable();
        gd.setStroke(5,Color.BLACK);
        gd.setColor(Color.parseColor("#FFFCFABB"));
        vocabulary.setBackground(gd);

        GradientDrawable gd3 = new GradientDrawable();
        gd3.setColor(Color.parseColor("#FFFDE2B5"));
        gd3.setStroke(2,Color.GRAY);
        meaning.setBackground(gd3);

        }

//---------------save to database-----------------------------

    public void save(View v) {
        String vocabulary2, pronunciation2, meaning2, memorycode2;
        int id2;

        vocabulary2 = vocabulary.getText().toString();
        vocabulary2 = vocabulary2.replace("'","");
        pronunciation2 = pronunciation.getText().toString();
        pronunciation2 = pronunciation2.replace("'","");
        meaning2 = meaning.getText().toString();
        meaning2 = meaning2.replace("'","");
        memorycode2 = memorycode.getText().toString();
        memorycode2 = memorycode2.replace("'","");

        if (die.isChecked()){vocabulary2="die "+vocabulary2.substring(0, 1).toUpperCase()+ vocabulary2.substring(1).toLowerCase();}
        if (der.isChecked()){vocabulary2="der "+vocabulary2.substring(0, 1).toUpperCase()+ vocabulary2.substring(1).toLowerCase(); }
        if (das.isChecked()){vocabulary2="das "+vocabulary2.substring(0, 1).toUpperCase()+ vocabulary2.substring(1).toLowerCase(); }

        if (issentence.isChecked()){
            if(!memorycode2.contains("*"))
            {memorycode2 = "*"+memorycode.getText().toString();}
        }

        keyboard.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), 0);

        if(!TextUtils.isEmpty(vocabulary.getText().toString()))
        {
        vocabularyDB.execSQL("INSERT INTO Vocabulary (vocabulary, pronunciation, meaning, memorycode) VALUES ('" + vocabulary2 + "', '" + pronunciation2 + "', '" + meaning2 + "', '" + memorycode2 + "');");
        Toast.makeText(this, "Saved..", Toast.LENGTH_SHORT).show();
        vocabulary.setText("");
        pronunciation.setText("");
        meaning.setText("");
        memorycode.setText("");
        die.setChecked(false);
        der.setChecked(false);
        das.setChecked(false);
        issentence.setChecked(false);
    }}


//------export a text file---------------------------------------------------------------------

    public void savefile(View v) {
    String combinedString, dataString, x;
    mycursor = vocabularyDB.rawQuery("SELECT * FROM Vocabulary ORDER BY ismemorised,date DESC ", null);
    mycursor.moveToFirst();
    String columnString = "vocabulary pronunciation meaning memorycode";


        File file;
        File root = Environment.getExternalStorageDirectory();
            File dir = new File(root.getAbsolutePath());
            dir.mkdirs();
            file = new File(dir, "vocabularyYedek.txt");
            FileOutputStream out = null;
            try {
                out = new FileOutputStream(file);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            try {

                do {

                    if(TextUtils.isEmpty(mycursor.getString(4)))
                        {x="";}
                    else
                        {x=">"+mycursor.getString(4);}

                    if(mycursor.getInt(5)==1) { 
                        dataString = "✓ " + mycursor.getString(1) + " : "+mycursor.getString(3) + x;
                        }
                    else
                    {dataString = "* " + mycursor.getString(1) + " : "+mycursor.getString(3) + x;}
                    combinedString = dataString+"\r\n";
                    out.write(combinedString.getBytes());
                }while (mycursor.moveToNext());

            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
       // }

    Toast.makeText(this,"All words exported as a file", Toast.LENGTH_LONG).show();
    mycursor.close();
}

//------------------------------------------------------------------------------

    public void vocabularyvoice(View v) {

        if (!TextUtils.isEmpty(vocabulary.getText().toString())) {

            tts = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {

                @Override
                public void onInit(int status) {
                    // TODO Auto-generated method stub

                    int result = tts.setLanguage(Locale.GERMANY);
                    String vocabularym = vocabulary.getText().toString();
                    tts.speak(vocabularym, TextToSpeech.QUEUE_FLUSH, null);

                }
            });

        }
    }
//------------------------------------------------------------------------------

     public void selectimage(View v) {

        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
         switch(requestCode){
             case PICK_IMAGE_REQUEST:
                if(resultCode == RESULT_OK){
                    Uri selectedImageUri = data.getData();
                     link=selectedImageUri.toString();

                    link=selectedImageUri.getPath();

                    Toast.makeText(this,link, Toast.LENGTH_LONG).show();

                    try {
                         Bitmap bitmap= BitmapFactory.decodeStream(getContentResolver().openInputStream(selectedImageUri));
                         resim.setImageBitmap(bitmap);

                     } catch (IOException e) {
                         e.printStackTrace();
                    }
                }
                 break;
        }
     }


    //------------------------------------------

    public void webvocabulary(View v){

        //InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        keyboard.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), 0);
        WebView myWebView = (WebView) findViewById(R.id.id_webview);
        myWebView.setVisibility(View.VISIBLE);
        myWebView.setWebViewClient(new WebViewClient()); 
        String link="https://en.pons.com/translate?l=detr&q="+vocabulary.getText().toString();

        myWebView.loadUrl(link);

    }

    public void webvocabulary2(View v){

        keyboard.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), 0);
        WebView myWebView = (WebView) findViewById(R.id.id_webview);
        myWebView.setVisibility(View.VISIBLE);
        myWebView.setWebViewClient(new WebViewClient()); //link web tarayicida degil programda acilacak
        String link="https://tr.langenscheidt.com/almanca-turkce/"+vocabulary.getText().toString();
        myWebView.loadUrl(link);

    }

    public void webvocabulary3(View v){
        keyboard.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), 0);
        WebView myWebView = (WebView) findViewById(R.id.id_webview);
        myWebView.setVisibility(View.VISIBLE);
        myWebView.setWebViewClient(new WebViewClient()); //link web tarayicida degil programda acilacak
        String link="https://www.linguee.com/english-german/search?source=german&query="+vocabulary.getText().toString();
        myWebView.loadUrl(link);

    }

    //----------------------------------------
    public void webimage(View v){

        WebView myWebView = (WebView) findViewById(R.id.id_webview);
        myWebView.setVisibility(View.VISIBLE);
        myWebView.setWebViewClient(new WebViewClient()); //link web tarayicida degil programda acilacak
        String link="https://www.google.com.tr/search?q="+vocabulary.getText().toString()+"&source=lnms&tbm=isch&sa=X&ved=0ahUKEwj5n4_f9frXAhUBPBQKHbkgBbEQ_AUICigB&biw=1600&bih=790";

        myWebView.loadUrl(link);
        myWebView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView myWebView, String url) {
                myWebView.loadUrl(url);
                //Toast.makeText( getApplicationContext(),url, Toast.LENGTH_SHORT).show();
                return true;
            }
        });

    }

}
