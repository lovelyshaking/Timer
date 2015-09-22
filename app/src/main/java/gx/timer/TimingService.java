package gx.timer;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;


public class TimingService extends Service {
    private static String Tag = "TimingService";

    int rid[] = new int[]{R.raw.half_time,R.raw.time_reached,R.raw.time_end};
    static SoundPool sndPool_reach;


    private String interval = "";
    private String totaltime = "";

    Intent intent1 = new Intent("LOOPER_TIME");
    PendingIntent pi;

    Intent intent_all = new Intent("ALL_TIME");
    PendingIntent pi_all;

    Intent intent_half = new Intent("HALF_TIME");
    PendingIntent pi_half;

    AlarmManager am ;
    AlarmManager am_half;
    AlarmManager am_all;

    private BroadcastReceiver TimerReceiver = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            // TODO Auto-generated method stub
            Log.d(Tag, "broadcast received......................");
            String action = intent.getAction();
            if(action.equals("LOOPER_TIME")) {
                Log.d(Tag, "Looper time reached......................");
                sndPool_reach.play(1, 1, 1, 0, 0, 1);
            }
            else if (action.equals("ALL_TIME")){
                Log.d(Tag, "All time reached......................");
                sndPool_reach.play(3,1, 1, 0, 0, 1);
                stopSelf();
            }
            else if (action.equals("HALF_TIME")){
                Log.d(Tag, "Half time reached......................");
                sndPool_reach.play(2,1, 1, 0, 0, 1);
            }
        }
    };

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
        Log.d(Tag, "On create");
        super.onCreate();
        sndPool_reach = new SoundPool(3, AudioManager.STREAM_SYSTEM,5);
        sndPool_reach.load(this, rid[1], 1);  //reach
        sndPool_reach.load(this, rid[0], 1);  //half time
        sndPool_reach.load(this, rid[2], 1);  //end
        try {
            Thread.sleep(1000); // 给予初始化音乐文件足够时间
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        registerBoradcastReceiver();
         am = (AlarmManager)getSystemService(ALARM_SERVICE);
         am_half = (AlarmManager)getSystemService(ALARM_SERVICE);
         am_all = (AlarmManager)getSystemService(ALARM_SERVICE);
        Log.d(Tag,"registerBoradcastReceiver finish");
        Notification notification = new Notification(R.drawable.icon, "Timing",System.currentTimeMillis());
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
        notification.setLatestEventInfo(this, "Timing tools", "Tining going", pendingIntent);
        startForeground(1, notification);
    }
    @Override
    public void onDestroy(){
        am.cancel(pi);
        am_all.cancel(pi_all);
        am_half.cancel(pi_half);
        unregisterReceiver(TimerReceiver);
        Log.d(Tag,"Unregister");
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
        Log.i(Tag, "onStartCommand by: " + who);


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
                pi =  PendingIntent.getBroadcast(this, 0, intent1, 0);
                Log.d(Tag,"Get alarmmanager finish");
                Log.d(Tag, System.currentTimeMillis()+"");
                am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis()+(long) (i_t * 60 * 1000),(long) (i_t * 60 * 1000), pi);
                Log.d(Tag, "alarmmanager repeat begin");
                break;
            case 2: //timer once
                Toast.makeText(this,"Timer begin",Toast.LENGTH_LONG).show();
                double t_t =Double.parseDouble(totaltime);
                Log.d("t_t", t_t + "");
                Log.d(Tag + "  recevice type", "type = " + type + "");
                pi_all = PendingIntent.getBroadcast(this,0,intent_all,0);
                pi_half =  PendingIntent.getBroadcast(this,0,intent_half,0);
                am_half.set(AlarmManager.RTC_WAKEUP,System.currentTimeMillis()+(long) (t_t * 60 * 1000 / 2),pi_half);
                am_all.set(AlarmManager.RTC_WAKEUP,System.currentTimeMillis()+(long) (t_t * 60 * 1000 ),pi_all);
                break;
            case 3:  //both
                Toast.makeText(this,"Timer begin",Toast.LENGTH_LONG).show();
                Log.d(Tag + "  recevice type", type + "");
                double alli_t = Double.parseDouble(interval);
                Log.d("alli_t", alli_t + "");
                double allt_t =Double.parseDouble(totaltime);
                Log.d("allt_t", allt_t + "");

                pi =  PendingIntent.getBroadcast(this, 0, intent1, 0);
                pi_all = PendingIntent.getBroadcast(this,0,intent_all,0);
                pi_half =  PendingIntent.getBroadcast(this,0,intent_half,0);

                am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis()+(long) (alli_t * 60 * 1000),(long) (alli_t * 60 * 1000), pi);
                am_half.set(AlarmManager.RTC_WAKEUP,System.currentTimeMillis()+(long) (allt_t * 60 * 1000 / 2),pi_half);
                am_all.set(AlarmManager.RTC_WAKEUP,System.currentTimeMillis()+(long) (allt_t * 60 * 1000 ),pi_all);

                break;
            default:

        }
        return START_STICKY;
    }
    public void registerBoradcastReceiver() {
        IntentFilter myIntentFilter = new IntentFilter();
        myIntentFilter.addAction("LOOPER_TIME");
        myIntentFilter.addAction("ALL_TIME");
        myIntentFilter.addAction("HALF_TIME");
        registerReceiver(TimerReceiver, myIntentFilter);
    }
}
