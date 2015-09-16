package gx.timer;

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
    private Timer timer ;//= new Timer();
    private Timer timer_allTime; //;= new Timer();
    private Timer timer_allTime_helper ;//= new Timer();

    private TimerTask task = null;
    private TimerTask task_alltime = null;
    private TimerTask task_alltime_helper = null;

    int rid[] = new int[]{R.raw.half_time,R.raw.time_reached,R.raw.time_end};
    SoundPool sndPool_reach;


    Handler handler= new Handler() {
        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            Log.d("Handler","Time reached");
            sndPool_reach.play(1,1, 1, 0, 0, 1);
            super.handleMessage(msg);
        }
    };;
    Handler handler_alltime= new Handler() {
        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            Log.d("Handler_alltime","Time reached");
            sndPool_reach.play(2, 1, 1, 0, 0, 1);
            timer_allTime.cancel();
            task_alltime.cancel();
            timer_allTime_helper.schedule(task_alltime_helper,(long)( Double.parseDouble(totaltime)* 60 * 1000 / 2));
            super.handleMessage(msg);
        }
    };;
    Handler handler_alltime_helper= new Handler() {
        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            Log.d("Handler_endtime","Time reached");
//            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
//            Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
//            r.play();
            sndPool_reach.play(3, 1, 1, 0, 0, 1);
            timer_allTime_helper.cancel();
            task_alltime_helper.cancel();
            super.handleMessage(msg);
        }
    };;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editText = (EditText)findViewById(R.id.editText); //interval edittext
        editText2 = (EditText)findViewById(R.id.editText2);  //total time edittext
        btn_begin = (Button)findViewById(R.id.btn_begin);
        btn_stop = (Button)findViewById(R.id.btn_stop);

        //sndPool_half = new SoundPool(1, AudioManager.STREAM_SYSTEM,5);
        sndPool_reach = new SoundPool(3, AudioManager.STREAM_SYSTEM,5);
       // getSndPool_end = new SoundPool(1, AudioManager.STREAM_SYSTEM,5);



        btn_begin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this,"Time begin!",Toast.LENGTH_LONG).show();
                interval = editText.getText().toString();
                totaltime = editText2.getText().toString();

                sndPool_reach.load(MainActivity.this, rid[1], 1);  //reach
                sndPool_reach.load(MainActivity.this, rid[0], 1);  //half time
                sndPool_reach.load(MainActivity.this, rid[2], 1);  //end

                if(interval.equals("") && totaltime.equals("")){
                    Toast.makeText(MainActivity.this,"wrong interval!",Toast.LENGTH_LONG).show();
                }
                if (!interval.equals("") && totaltime.equals("")) {
                    double i_t = Double.parseDouble(interval);
                    Log.d("i_t", i_t + "");
                    timer = new Timer();
                    task = new TimerTask() {
                        @Override
                        public void run() {
                            // TODO Auto-generated method stub
                            Message message = new Message();
                            message.what = 1;
                            handler.sendMessage(message);
                        }
                    };
                    timer.schedule(task, (long) (i_t * 60 * 1000), (long) (i_t * 60 * 1000));
                    //sndPool_reach.load(MainActivity.this, rid[1], 1);
                }
                if(interval.equals("") && !totaltime.equals("")){
                    double t_t =Double.parseDouble(totaltime);
                    Log.d("t_t", t_t + "");
                    timer_allTime = new Timer();
                    timer_allTime_helper = new Timer();
                    task_alltime = new TimerTask() {
                        @Override
                        public void run() {
                            Message message = new Message();
                            message.what = 1;
                            handler_alltime.sendMessage(message);
                        }
                    };
                    task_alltime_helper = new TimerTask() {
                        @Override
                        public void run() {
                            Message message = new Message();
                            message.what = 1;
                            handler_alltime_helper.sendMessage(message);
                        }
                    };
                    timer_allTime.schedule(task_alltime,(long)(t_t * 60 * 1000 / 2));

                }
                if(!interval.equals("") && ! totaltime.equals("")){

                }
            }
        });
        btn_stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(timer != null) {
                    timer.cancel();
                }
                if(task != null){
                task.cancel();}
                if(task_alltime != null){
                    task_alltime.cancel();
                }
                if(task_alltime_helper !=null){
                    task_alltime_helper.cancel();
                }
                editText.setText("");
                editText2.setText("");
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
