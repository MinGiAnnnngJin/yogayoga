package com.example.yogayoga;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;

import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Locale;

public class MainActivity2 extends AppCompatActivity {

    private TextToSpeech textToSpeech;
    private WebView webView;
    private Button exitButton;
    private TextView textView;
    private String[] steps = {
            "비라바드라아사나 자세는 근육을 강화하고 \n몸의 균형을 개선하는 데 \n도움을 주는 동작입니다.",
            "자세 설명을 시작하겠습니다. \n 도중 종료를 하고 싶으시면 \n 화면 아래의 종료 버튼을 눌러주세요.",
            "앞을 보고 발을 어깨 너비로 벌린 후",
            "엉덩이를 약간 뒤로 빼준 뒤",
            "양 팔을 펼쳐 양쪽 바깥쪽으로 펼치고",
            "손바닥은 아래를 향하게 합니다.",
            "왼쪽 발을 바깥쪽으로 90도로 돌리고",
            "오른쪽 발은 약간 오른쪽으로 돌려",
            "발 뒤꿈치와 왼쪽 발 중심선이 일직선이 되도록 조절합니다",
            "왼쪽 팔을 어깨 높이로 왼쪽 다리 방향으로 뻗어 유지하고",
            "오른쪽 팔은 편안하게 펴고 시선은 오른쪽 손바닥으로 향하게 합니다",
            "왼쪽 무릎을 90도로 구부려 왼쪽 허벅지가 바닥과 평행해지도록 한 뒤 유지합니다",
            "깊은 호흡을 하며 몸의 균형을 유지하고",
            "몸의 무게를 균형있게 분산하고 자세를 조절합니다",
            "천천히 원래 자세로 돌아옵니다."
    };
    private int currentStep = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        exitButton = findViewById(R.id.btn);
        textView = findViewById(R.id.txt);
        webView = (WebView) findViewById(R.id.webview); // XML 파일에서 WebView의 ID 확인
        webView.setWebViewClient(new WebViewClient()); // 앱 내부에서 웹 페이지를 로드하도록 설정
        // 웹 페이지 로드
        webView.loadUrl("http://localhost:5000/video"); // 로드하고 싶은 웹 페이지 URL
        textToSpeech = new TextToSpeech(this, status -> {
            if (status == TextToSpeech.SUCCESS) {
                textToSpeech.setLanguage(Locale.KOREAN);
                textToSpeech.setOnUtteranceProgressListener(new UtteranceProgressListener() {
                    @Override
                    public void onStart(String utteranceId) {
                    }

                    @Override
                    public void onDone(String utteranceId) {
                        currentStep++;
                        if (currentStep < steps.length) {
                            runOnUiThread(() -> speakStep(currentStep));
                        }
                    }

                    @Override
                    public void onError(String utteranceId) {
                    }
                });
                speakStep(currentStep);
            }
        });
        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 앱 종료
                finish();
            }
        });
    }


    private void speakStep(int step) {
        HashMap<String, String> params = new HashMap<>();
        params.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, String.valueOf(step));
        runOnUiThread(() -> {
            textView.setText(steps[step]);
        });
        textToSpeech.speak(steps[step], TextToSpeech.QUEUE_FLUSH, params);
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
