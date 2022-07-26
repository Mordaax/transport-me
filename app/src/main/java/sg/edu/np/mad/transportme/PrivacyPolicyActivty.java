package sg.edu.np.mad.transportme;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;

public class PrivacyPolicyActivty extends Activity {
    WebView web;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy_policy_activty);

        web =(WebView)findViewById(R.id.webView);
        web.loadUrl("https://westwq.github.io/MADPrivacy/");
        ImageView menuback = findViewById(R.id.menu_icon);
        menuback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

}