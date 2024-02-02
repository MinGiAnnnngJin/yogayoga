package com.example.yogayoga;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private TextToSpeech textToSpeech;
    private ActivityResultLauncher<Intent> voiceRecognitionLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // TTS 객체 초기화
        textToSpeech = new TextToSpeech(this, status -> {
            if (status == TextToSpeech.SUCCESS) {
                int result = textToSpeech.setLanguage(Locale.KOREA);
                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Log.e("TTS", "This Language is not supported");
                } else {
                    // TTS 준비가 되면 speak 메소드 호출
                    speak();
                }
            } else {
                Log.e("TTS", "Initialization Failed!");
            }
        });

        // 음성 인식 결과를 처리하는 ActivityResultLauncher 초기화
        voiceRecognitionLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        ArrayList<String> matches = result.getData().getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                        if (matches != null && !matches.isEmpty()) {
                            String firstMatch = matches.get(0);
                            if ("시작".equalsIgnoreCase(firstMatch)) {
                                // "시작"이라는 단어를 인식했을 때 MainActivity2로 이동
                                Intent intent = new Intent(MainActivity.this, MainActivity2.class);
                                startActivity(intent);
                            }
                        }
                    }
                });
        Button startButton = findViewById(R.id.btn);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 메인 액티비티2로 이동하는 Intent 생성
                Intent intent = new Intent(MainActivity.this, MainActivity2.class);

                // Intent를 사용하여 메인 액티비티2로 이동
                startActivity(intent);
            }
        });
    }

    private void speak() {
        String text = "안녕하세요, 오늘의 요가 동작은 비라바드라아사나2 동작과 브르크 사사나 동작 입니다. 요가 시작을 위해 '시작'이라고 말씀해주시거나 아래의 시작 버튼을 눌러주세요.";
        textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, "TTS1");

        textToSpeech.setOnUtteranceProgressListener(new UtteranceProgressListener() {
            @Override
            public void onStart(String utteranceId) {
                // 말하기 시작했을 때
            }

            @Override
            public void onDone(String utteranceId) {
                // 말하기 완료했을 때
                runOnUiThread(() -> startVoiceRecognition());
            }

            @Override
            public void onError(String utteranceId) {
                // 말하기 중 에러 발생했을 때
            }
        });
    }

    private void startVoiceRecognition() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "말씀해주세요");
        voiceRecognitionLauncher.launch(intent);
    }

    @Override
    protected void onDestroy() {
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
        super.onDestroy();
    }
}