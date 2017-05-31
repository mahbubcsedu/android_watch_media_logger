package mahbub1.umbc.eclipse.androidwearsensordata;

/**
 * Created by mahbub on 3/14/17.
 */


import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class MyDisplayActivity extends Activity {

    private TextView mTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display);
        mTextView = (TextView) findViewById(R.id.text);
    }
}