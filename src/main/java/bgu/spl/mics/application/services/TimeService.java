package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.TickBroadcast;

import java.util.Timer;
import java.util.TimerTask;

/**
 * TimeService is the global system timer There is only one instance of this micro-service.
 * It keeps track of the amount of ticks passed since initialization and notifies
 * all other micro-services about the current time tick using {link Tick Broadcast}.
 * This class may not hold references for objects which it is not responsible for:
 * {link ResourcesHolder}, {link MoneyRegister}, {link Inventory}.
 * <p>
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class TimeService extends MicroService {

    private int speed;
    private int duration;
    private int tick;
    private boolean terminateTime;

    public TimeService(int speed, int duration) {
        super("timeService");
        this.speed = speed;
        this.duration = duration;
        tick = 1;
    }

    public int getSpeed() {
        return speed;
    }
    public int getDuration(){
        return duration;
    }

    @Override
    protected void initialize() {
        Thread timerThread = Thread.currentThread();
        Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                if (tick<=duration){
                    sendBroadcast(new TickBroadcast(tick,duration));
                    tick++;
                }
                else{
                    timer.cancel();
                    timer.purge();
                    terminate();
                    timerThread.interrupt();
                }
            }
        };
       timer.schedule(task,0,speed);

    }

}
