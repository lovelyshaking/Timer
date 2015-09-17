package gx.timer;

import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

public class TimingService extends Service {
    private static String Tag = "TimingService";
    private Timer timer ;//= new Timer();
    private Timer timer_allTime; //;= new Timer();
    private Timer timer_allTime_helper ;//= new Timer();

    private TimerTask task = null;
    private TimerTask task_alltime = null;
    private TimerTask task_alltime_helper = null;

    int rid[] = new int[]{R.raw.half_time,R.raw.time_reached,R.raw.time_end};
    SoundPool sndPool_reach;


    private String interval = "";
    private String totaltime = "";

    Handler handler= new Handler() {
        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            Log.d("Handler", "Time reached");
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
            stopSelf();
            super.handleMessage(msg);
        }
    };;
    public TimingService(){
        super();
    }
   @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.

        throw new UnsupportedOperationException("Not yet implemented");

    }
    @Override
    public void onCreate(){
        Log.d(Tag,"On create");
        super.onCreate();
        sndPool_reach = new SoundPool(3, AudioManager.STREAM_SYSTEM,5);
        sndPool_reach.load(this, rid[1], 1);  //reach
        sndPool_reach.load(this, rid[0], 1);  //half time
        sndPool_reach.load(this, rid[2], 1);  //end
    }
    @Override
    public void onDestroy(){
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
        stopSelf();
        super.onDestroy();
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        if(intent == null) {
            Log.i(Tag, "onStartCommand null" );
            return START_STICKY;
        }
        String who = intent.getAction();
        Log.i(Tag, "onStartCommand by: "+ who);

        Log.d(Tag,"On create");
        int type = intent.getIntExtra("type",0);
        interval = intent.getStringExtra("interval");
        totaltime = intent.getStringExtra("totaltime");
        switch (type){
            case 0:  //2 parameter are both wrong
                Log.d(Tag,"error when get type");
                break;
            case 1: //looper timer
                Toast.makeText(this,"Timer begin",Toast.LENGTH_LONG).show();
                Log.d(Tag+"  recevice type",type+"");
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
                break;
            case 2: //timer once
                Toast.makeText(this,"Timer begin",Toast.LENGTH_LONG).show();
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
                timer_allTime.schedule(task_alltime, (long) (t_t * 60 * 1000 / 2));
                break;
            case 3:  //both
                Toast.makeText(this,"Timer begin",Toast.LENGTH_LONG).show();
                Log.d(Tag + "  recevice type", type + "");
                double alli_t = Double.parseDouble(interval);
                Log.d("alli_t", alli_t + "");
                double allt_t =Double.parseDouble(totaltime);
                Log.d("allt_t", allt_t + "");
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
                timer_allTime.schedule(task_alltime, (long) (allt_t * 60 * 1000 / 2));
                timer.schedule(task, (long) (alli_t * 60 * 1000), (long) (alli_t * 60 * 1000));
                break;
            default:

        }
        return START_STICKY;
    }
}
