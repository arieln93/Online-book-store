package bgu.spl.mics.application.services;

import bgu.spl.mics.Future;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.BookStoreRunner;
import bgu.spl.mics.application.messages.BookOrderEvent;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.passiveObjects.Customer;
import bgu.spl.mics.application.passiveObjects.OrderReceipt;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * APIService is in charge of the connection between a client and the store.
 * It informs the store about desired purchases using {@link BookOrderEvent}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link ResourcesHolder}, {@link MoneyRegister}, {@link Inventory}.
 * 
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class APIService extends MicroService{
	private Customer customer;
	private LinkedList<Customer.OrderPair> orderSchedule;
	private HashMap<Future<OrderReceipt>,Boolean> results;
	private int nextId=0;

	public APIService(String name, Customer customer) {
		super(name);
		this.customer=customer;
		this.orderSchedule=customer.getOrderSchedule();
		this.results = new HashMap<>();
	}

	@Override
	protected void initialize() {
		subscribeBroadcast(TickBroadcast.class, tick->{
			if (tick.getTick()>=tick.getDuration()) {
				this.terminate();
			}
			else {
				int t = tick.getTick();
				for (Customer.OrderPair orderPair : this.orderSchedule) {
					if (orderPair.getTick()==tick.getTick()) {
						this.results.put(sendEvent(new BookOrderEvent(this.nextId,this.customer,orderPair.getBookTitle(),tick.getTick())),false);
						this.nextId++;
					}
				}
			}
			if (tick.getTick()==tick.getDuration()) {
				this.terminate();
			}
		});
	}



}
