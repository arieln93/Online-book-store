package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.BookStoreRunner;
import bgu.spl.mics.application.messages.CheckAvailabilityEvent;
import bgu.spl.mics.application.messages.TakeBookEvent;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.passiveObjects.BookInventoryInfo;
import bgu.spl.mics.application.passiveObjects.Inventory;

import javax.swing.text.TabableView;
import java.util.LinkedList;

/**
 * InventoryService is in charge of the book inventory and stock.
 * Holds a reference to the {@link Inventory} singleton of the store.
 * This class may not hold references for objects which it is not responsible for:
 * {@link ResourcesHolder}, {@link MoneyRegister}.
 * 
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */

public class InventoryService extends MicroService{
	private Inventory inventory = Inventory.getInstance();

	/**
	 * Ariel: The constructor should get a list of books from the main, the books
			are given to us through the JSON file that we need to read.
			The JSON file includes a list of books, with the following information:
			name, initial amount and price.
	 		We need to load those books to the passive object- Inventory.

			This is the only Thead (MicroService) who is in charge of the Inventory.
	 */
	public InventoryService(String name) {
		super(name);
	}

	/**
	 * Ariel: Here, we need to subscribe the InventoryService to the Events and Broadcasts that he
	 * 		will be interested to get, and set the CallBacks (what do we want to do with those
	 * 		Events and Broadcasts.
	 * 		For now, i think we need to subscribe to:
	 * 		-
	 * 		-
	 */
	@Override
	protected void initialize() {
		subscribeBroadcast(TickBroadcast.class, tick->{
			if (tick.getTick()==tick.getDuration()) {
				terminate();
			}
		});
		subscribeEvent(CheckAvailabilityEvent.class, ev->{
			complete(ev,inventory.checkAvailabiltyAndGetPrice(ev.getBookName()));
		});
		subscribeEvent(TakeBookEvent.class,ev->{
			complete(ev, inventory.take(ev.getBookName()));
		});


	}

}
