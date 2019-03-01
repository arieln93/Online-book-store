package bgu.spl.mics;

import java.util.*;
import java.util.concurrent.*;

/**
 * The {@link MessageBusImpl class is the implementation of the MessageBus interface.
 * Write your implementation here!
 * Only private fields and methods can be added to this class.
 */
public class MessageBusImpl implements MessageBus {
	private ConcurrentHashMap<MicroService, LinkedBlockingQueue<Message>> microServiceQueue;
	private ConcurrentHashMap<Class<? extends Event>, ConcurrentLinkedQueue<MicroService>> eventSubscribers;
	private ConcurrentHashMap<Class<? extends Broadcast>, ConcurrentLinkedQueue<MicroService>> broadcastSubscribers;
	private ConcurrentHashMap<Event,Future> futuresByEvents;

	private static class holder{
		private static MessageBusImpl instance = new MessageBusImpl();
	}
	private MessageBusImpl(){
		this.microServiceQueue = new ConcurrentHashMap<>();
		this.eventSubscribers = new ConcurrentHashMap<>();
		this.broadcastSubscribers = new ConcurrentHashMap<>();
		this.futuresByEvents = new ConcurrentHashMap<>();
	}
	public static MessageBusImpl getInstance(){
		return holder.instance;
	}

	@Override
	public <T> void subscribeEvent(Class<? extends Event<T>> type, MicroService m) {
		synchronized (eventSubscribers) {
			if (!this.eventSubscribers.containsKey(type)) {
				this.eventSubscribers.put(type, new ConcurrentLinkedQueue<MicroService>());
			}
		}
		eventSubscribers.get(type).add(m);
	}

	@Override
	public void subscribeBroadcast(Class<? extends Broadcast> type, MicroService m) {
		synchronized (broadcastSubscribers) {
			if (!this.broadcastSubscribers.containsKey(type)) {
				this.broadcastSubscribers.put(type, new ConcurrentLinkedQueue<MicroService>());
			}
		}
		broadcastSubscribers.get(type).add(m);
	}

	@Override
	public <T> void complete(Event<T> e, T result) {
		if (this.futuresByEvents.containsKey(e)) {
			this.futuresByEvents.get(e).resolve(result);
		}
	}

	@Override
	public synchronized void sendBroadcast(Broadcast b) {
		if (this.broadcastSubscribers.containsKey(b.getClass())) {
			if (this.broadcastSubscribers.get(b.getClass()).size() > 0) {
				for (MicroService m : this.broadcastSubscribers.get(b.getClass())) {
					this.microServiceQueue.get(m).add(b);
				}
			}
		}
	}

	
	@Override
	public synchronized  <T> Future<T> sendEvent(Event<T> e) {
		if (this.eventSubscribers.containsKey(e.getClass())){
				if (this.eventSubscribers.get(e.getClass()).size()>0) {
					Future<T> f = new Future<>();
					this.futuresByEvents.putIfAbsent(e,f);
					MicroService m = this.eventSubscribers.get(e.getClass()).poll();
					this.microServiceQueue.get(m).add(e);
					this.eventSubscribers.get(e.getClass()).add(m);
					return f;
				}
			}
		return null;
	}

	@Override
	public void register(MicroService m) {
		this.microServiceQueue.putIfAbsent(m, new LinkedBlockingQueue<Message>());
	}

	@Override
	public synchronized void unregister(MicroService m) {
		if (this.microServiceQueue.containsKey(m)){
			this.microServiceQueue.remove(m);
		}
		for (Map.Entry<Class<? extends Broadcast>, ConcurrentLinkedQueue<MicroService>> e: broadcastSubscribers.entrySet()){
			if (e.getValue().contains(m)){
				e.getValue().remove(m);
			}
		}
		for (Map.Entry<Class<? extends Event>, ConcurrentLinkedQueue<MicroService>> e: eventSubscribers.entrySet()){
			if (e.getValue().contains(m)){
				e.getValue().remove(m);
			}
		}
	}

	@Override
	public Message awaitMessage(MicroService m) throws InterruptedException {
		return microServiceQueue.get(m).take();
	}
}
