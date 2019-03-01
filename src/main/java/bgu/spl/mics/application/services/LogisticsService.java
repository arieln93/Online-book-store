package bgu.spl.mics.application.services;

import bgu.spl.mics.Future;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.BookStoreRunner;
import bgu.spl.mics.application.messages.AequireVehicleEvent;
import bgu.spl.mics.application.messages.DeliveryEvent;
import bgu.spl.mics.application.messages.ReleaseVehicleEvent;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.passiveObjects.DeliveryVehicle;

/**
 * Logistic service in charge of delivering books that have been purchased to customers.
 * Handles {@link DeliveryEvent}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link ResourcesHolder}, {@link MoneyRegister}, {@link Inventory}.
 * 
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class LogisticsService extends MicroService {

	public LogisticsService(String name) {
		super(name);
		// TODO Implement this
	}

	@Override
	protected void initialize() {
		subscribeBroadcast(TickBroadcast.class, tick->{
			if (tick.getTick()==tick.getDuration()) {
				terminate();
			}});
		subscribeEvent(DeliveryEvent.class,ev->{
			Future<DeliveryVehicle> vehicleFuture = sendEvent(new AequireVehicleEvent());
			if (vehicleFuture!=null) {
				if (vehicleFuture.get()!=null) {
					// if it got a vehicle, preform the delivery, then release it.
					vehicleFuture.get().deliver(ev.getAddress(), ev.getDistance());
					Future<Boolean> booleanFuture = sendEvent(new ReleaseVehicleEvent(vehicleFuture.get()));
					if (booleanFuture != null) {
						complete(ev, booleanFuture.get());
					}
				}
			}

		});


	}

}
