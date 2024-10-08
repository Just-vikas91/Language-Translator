package com.example.langtrans;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.ml.common.modeldownload.FirebaseModelDownloadConditions;
import com.google.firebase.ml.naturallanguage.FirebaseNaturalLanguage;
import com.google.firebase.ml.naturallanguage.languageid.FirebaseLanguageIdentification;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslateLanguage;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslator;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslatorOptions;

import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private TextInputEditText sourceEdt;
    private TextView translatedTV;
//    private ImageView switchButton;
    int languageCode = 0;
    String[] fromLanguages = {"English", "Afrikaans", "Arabic", "Belarusian", "Bulgarian", "Bengali", "Catalan"
            , "Czech","Chinese", "Danish", "German", "Greek", "Hindi", "Italian", "Japanese", "Kannada", "Korean", "Marathi", "Persian","Portuguese", "Russian"
            ,"Romanian", "Spanish", "Telugu","Tamil","Turkish","Thai", "Urdu","Ukrainian","Vietnamese", "Welsh"};
    String[] toLanguages = {"English", "Afrikaans", "Arabic", "Belarusian", "Bulgarian", "Bengali", "Catalan"
            , "Czech","Chinese", "Danish", "German", "Greek", "Hindi", "Italian", "Japanese", "Kannada", "Korean", "Marathi", "Persian","Portuguese", "Russian"
            ,"Romanian", "Spanish", "Telugu","Tamil","Turkish","Thai", "Urdu","Ukrainian","Vietnamese", "Welsh"};

    private static final int REQUEST_PERMISSION_CODE=1;
    int fromLanguageCode;
    int toLanguageCode = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Spinner fromSpinner = findViewById(R.id.idFromSpinner);
        Spinner toSpinner = findViewById(R.id.idToSpinner);
        sourceEdt = findViewById(R.id.idEdtSource);
        ImageView micIV = findViewById(R.id.idIVMic);
        MaterialButton translateBtn = findViewById(R.id.idBtnTranslate);
        translatedTV = findViewById(R.id.idTVTranslatedTV);

        fromSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                fromLanguageCode = getlanguageCode(fromLanguages[position]);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        ArrayAdapter fromAdapter = new ArrayAdapter(this, R.layout.spinner_item, fromLanguages);
        fromAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        fromSpinner.setAdapter(fromAdapter);

        toSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                toLanguageCode = getlanguageCode(toLanguages[position]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        ArrayAdapter toAdapter = new ArrayAdapter(this,R.layout.spinner_item,toLanguages);
        toAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        toSpinner.setAdapter(toAdapter);

        //switch button between two spinners
        

        translateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                translatedTV.setText("");
                if (sourceEdt.getText().toString().isEmpty()){
                    Toast.makeText(MainActivity.this,"Enter your text to translate",Toast.LENGTH_SHORT).show();
                } else if (fromLanguageCode == 0) {
                    Toast.makeText(MainActivity.this,"Select Source Language",Toast.LENGTH_SHORT).show();
                } else if (toLanguageCode == 0) {
                    Toast.makeText(MainActivity.this,"Select language to translate into",Toast.LENGTH_SHORT).show();
                }else{
                    translateText(fromLanguageCode,toLanguageCode,sourceEdt.getText().toString());
                }
            }
        });
     micIV.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             Intent i = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
             i.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
             i.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
             i.putExtra(RecognizerIntent.EXTRA_PROMPT,"Speak To convert Into text");
             try {
                 startActivityForResult(i,REQUEST_PERMISSION_CODE);
             }catch (Exception e){
                 e.printStackTrace();
                 Toast.makeText(MainActivity.this, e.getMessage(),Toast.LENGTH_SHORT).show();
             }
         }
     });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_PERMISSION_CODE) {
            if (resultCode == RESULT_OK && data != null){
                ArrayList<String> result =data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                sourceEdt.setText(result.get(0));
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private void translateText(int fromLanguageCode, int toLanguageCode, String source){
        translatedTV.setText("downloading modal");
        FirebaseTranslatorOptions options= new FirebaseTranslatorOptions.Builder()
                .setSourceLanguage(fromLanguageCode)
                .setTargetLanguage(toLanguageCode)
                .build();
        FirebaseTranslator translator = FirebaseNaturalLanguage.getInstance().getTranslator(options);

        FirebaseModelDownloadConditions conditions= new FirebaseModelDownloadConditions.Builder().build();
        translator.downloadModelIfNeeded(conditions).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                translatedTV.setText("Translating...");
                translator.translate(source).addOnSuccessListener(new OnSuccessListener<String>() {
                    @Override
                    public void onSuccess(String s) {
                        translatedTV.setText(s);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MainActivity.this, "Failed to translate"+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MainActivity.this, "Failed to download language modal"+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    public int getlanguageCode(String language){

        switch(language){
            case "English":
                languageCode = FirebaseTranslateLanguage.EN;
                break;
            case "Afrikaans":
                languageCode = FirebaseTranslateLanguage.AF;
                break;
            case "Arabic":
                languageCode = FirebaseTranslateLanguage.AR;
                break;
            case "Belarusian":
                languageCode = FirebaseTranslateLanguage.BE;
                break;
            case "Bulgarian":
                languageCode = FirebaseTranslateLanguage.BG;
                break;
            case "Bengali":
                languageCode = FirebaseTranslateLanguage.BN;
                break;
            case "Catalan":
                languageCode = FirebaseTranslateLanguage.CA;
                break;
            case "Czech":
                languageCode = FirebaseTranslateLanguage.CS;
                break;
            case "Danish":
                languageCode = FirebaseTranslateLanguage.DA;
                break;
            case "German":
                languageCode = FirebaseTranslateLanguage.DE;
                break;
            case "Greek":
                languageCode = FirebaseTranslateLanguage.EL;
                break;
            case "Welsh":
                languageCode = FirebaseTranslateLanguage.CY;
                break;
            case "Hindi":
                languageCode = FirebaseTranslateLanguage.HI;
                break;
            case "Urdu":
                languageCode = FirebaseTranslateLanguage.UR;
                break;
            case "Telugu":
                languageCode = FirebaseTranslateLanguage.TE;
                break;
            case "Spanish":
                languageCode = FirebaseTranslateLanguage.ES;
                break;
            case "Persian":
                languageCode = FirebaseTranslateLanguage.FA;
                break;
            case "Italian":
                languageCode = FirebaseTranslateLanguage.IT;
                break;
            case "Japanese":
                languageCode = FirebaseTranslateLanguage.JA;
                break;
            case "Kannada":
                languageCode = FirebaseTranslateLanguage.KN;
                break;
            case "Korean":
                languageCode = FirebaseTranslateLanguage.KO;
                break;
            case "Marathi":
                languageCode = FirebaseTranslateLanguage.MR;
                break;
            case "Russian":
                languageCode = FirebaseTranslateLanguage.RU;
                break;
            case "Romanian":
                languageCode = FirebaseTranslateLanguage.RO;
                break;
            case "Portuguese":
                languageCode = FirebaseTranslateLanguage.PT;
                break;
            case "Tamil":
                languageCode = FirebaseTranslateLanguage.TA;
                break;
            case "Thai":
                languageCode = FirebaseTranslateLanguage.TH;
                break;
            case "Turkish":
                languageCode = FirebaseTranslateLanguage.TR;
                break;
            case "Ukrainian":
                languageCode = FirebaseTranslateLanguage.UK;
                break;
            case "Vietnamese":
                languageCode = FirebaseTranslateLanguage.VI;
                break;
            case "Chinese":
                languageCode = FirebaseTranslateLanguage.ZH;
                break;

            default:
                languageCode=0;
        }
        return languageCode;
    }
}