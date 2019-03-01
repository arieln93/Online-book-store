package bgu.spl.mics.application.messages;

import bgu.spl.mics.Broadcast;
import bgu.spl.mics.Message;

public class TickBroadcast implements Broadcast {

    private final int tick;
    private final int duration;

    public TickBroadcast(int tick,int duration){
        this.tick=tick;
        this.duration=duration;

    }
    public int getTick(){return this.tick;}
    public int getDuration(){return this.duration;}

}
