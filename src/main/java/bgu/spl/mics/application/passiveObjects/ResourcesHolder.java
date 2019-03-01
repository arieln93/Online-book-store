package bgu.spl.mics.application.passiveObjects;

import bgu.spl.mics.Future;
import bgu.spl.mics.application.BookStoreRunner;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * Passive object representing the resource manager.
 * You must not alter any of the given public methods of this class.
 * <p>
 * This class must be implemented safely as a thread-safe singleton.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You can add ONLY private methods and fields to this class.
 */
public class ResourcesHolder {

	private LinkedBlockingQueue<DeliveryVehicle> vehicleQ;
	private static class holder{
		private static ResourcesHolder instance = new ResourcesHolder();
	}
	/**
     * Retrieves the single instance of this class.
     */
	private ResourcesHolder(){

		this.vehicleQ = new LinkedBlockingQueue<>();
	}
	public static ResourcesHolder getInstance() {
		return holder.instance;
	}
	
	/**
     * Tries to acquire a vehicle and gives a future object which will
     * resolve to a vehicle.
     * <p>
     * @return 	{@link Future<DeliveryVehicle>} object which will resolve to a 
     * 			{@link DeliveryVehicle} when completed.   
     */
	public synchronized Future<DeliveryVehicle> acquireVehicle() {
			Future<DeliveryVehicle> vehicleFuture= new Future<>();
			try {
				// Trying to poll a vehicle, if there are no available vehicles,
				// wait for the first one that will get back to the line.
				vehicleFuture.resolve(this.vehicleQ.poll());
				if (vehicleFuture.get() != null) {
				}
				else {
					while (vehicleFuture.get() != null) {
						wait();
						vehicleFuture.resolve(this.vehicleQ.poll());
					}
				}
			}
			catch (InterruptedException ex){}
		return vehicleFuture;
	}
	
	/**
     * Releases a specified vehicle, opening it again for the possibility of
     * acquisition.
     * <p>
     * @param vehicle	{@link DeliveryVehicle} to be released.
     */
	public synchronized void releaseVehicle(DeliveryVehicle vehicle) {
		try {
			this.vehicleQ.put(vehicle);
			notify();
		}
		catch (InterruptedException ex){}
	}
	
	/**
     * Receives a collection of vehicles and stores them.
     * <p>
     * @param vehicles	Array of {@link DeliveryVehicle} instances to store.
     */
	public void load(DeliveryVehicle[] vehicles) {
		this.vehicleQ = new LinkedBlockingQueue<>();
		for (int i=0; i<vehicles.length;i++){
			this.vehicleQ.add(vehicles[i]);
		}
	}
}
