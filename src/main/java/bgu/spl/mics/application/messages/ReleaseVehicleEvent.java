package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.application.passiveObjects.DeliveryVehicle;

public class ReleaseVehicleEvent implements Event {
    private DeliveryVehicle vehicleToRelease;
    public ReleaseVehicleEvent(DeliveryVehicle vehicleToRelease){
        this.vehicleToRelease=vehicleToRelease;
    }
    public DeliveryVehicle getVehicleToRelease(){
        return this.vehicleToRelease;
    }
}
