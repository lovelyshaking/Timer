package gx.timer;

import android.content.Intent;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import java.util.Timer;
import java.util.TimerTask;


public class MainActivity extends ActionBarActivity {

    private EditText editText;
    private EditText editText2;
    private Button btn_begin;
    private Button btn_stop;
    private String interval = "";
    private String totaltime = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editText = (EditText)findViewById(R.id.editText); //interval edittext
        editText2 = (EditText)findViewById(R.id.editText2);  //total time edittext
        btn_begin = (Button)findViewById(R.id.btn_begin);
        btn_stop = (Button)findViewById(R.id.btn_stop);


        btn_begin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(MainActivity.this,"Time begin!",Toast.LENGTH_LONG).show();
                interval = editText.getText().toString();
                totaltime = editText2.getText().toString();

                if(interval.equals("") && totaltime.equals("")){
                    Toast.makeText(MainActivity.this,"wrong interval!",Toast.LENGTH_LONG).show();
                }
                if (!interval.equals("") && totaltime.equals("")) {
                    Intent intent = new Intent(MainActivity.this,TimingService.class);
                    intent.setAction(MainActivity.class.getName());;
                    int type = 1;
                    intent.putExtra("type",type);
                    intent.putExtra("interval",interval);
                    Log.d("MainActivity","StartService type 1");
                    startService(intent);
                }
                if(interval.equals("") && !totaltime.equals("")){
                    Intent intent = new Intent(MainActivity.this,TimingService.class);
                    int type = 2;
                    intent.putExtra("type",type);
                    intent.putExtra("totaltime",totaltime);
                    Log.d("MainActivity", "StartService type2");
                    startService(intent);
                }
                if(!interval.equals("") && !totaltime.equals("")){
                    if(Double.parseDouble(interval)>Double.parseDouble(totaltime)){
                        Toast.makeText(MainActivity.this,"wrong time parameter",Toast.LENGTH_SHORT).show();
                        editText.setText("");
                        editText2.setText("");
                    }
                    else {
                        Intent intent = new Intent(MainActivity.this, TimingService.class);
                        int type = 3;
                        intent.putExtra("type", type);
                        intent.putExtra("totaltime", totaltime);
                        intent.putExtra("interval", interval);
                        Log.d("MainActivity", "StartService type3");
                        startService(intent);
                    }
                }
            }
        });
        btn_stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editText.setText("");
                editText2.setText("");
                stopService(new Intent(MainActivity.this,TimingService.class));
            }
        });

    }

    @Override
    public void onDestroy(){
        //sndPool_reach.release();
        super.onDestroy();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
