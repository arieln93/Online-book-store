package bgu.spl.mics.application.services;

import bgu.spl.mics.Future;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.AequireVehicleEvent;
import bgu.spl.mics.application.messages.ReleaseVehicleEvent;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.passiveObjects.DeliveryVehicle;
import bgu.spl.mics.application.passiveObjects.ResourcesHolder;

import java.util.concurrent.TimeUnit;

/**
 * ResourceService is in charge of the store resources - the delivery vehicles.
 * Holds a reference to the {link ResourceHolder} singleton of the store.
 * This class may not hold references for objects which it is not responsible for:
 * {link MoneyRegister}, {link Inventory}.
 * <p>
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class ResourceService extends MicroService {
    private ResourcesHolder resourcesHolder = ResourcesHolder.getInstance();
    private boolean finish;

    public ResourceService(String name) {
        super(name);
        this.finish=false;
    }

    @Override
    protected void initialize() {
        subscribeBroadcast(TickBroadcast.class, tick -> {
            if (tick.getTick()==tick.getDuration()) {
                this.finish=true;
                terminate();
            }
        });
        subscribeEvent(AequireVehicleEvent.class, ev -> {
            Future<DeliveryVehicle> f = resourcesHolder.acquireVehicle();
            complete(ev, f.get());
        });
        subscribeEvent(ReleaseVehicleEvent.class, ev -> {
            resourcesHolder.releaseVehicle(ev.getVehicleToRelease());
            complete(ev, true);
        });





    }

}
