package bgu.spl.mics.application.services;

import bgu.spl.mics.Future;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.BookStoreRunner;
import bgu.spl.mics.application.messages.*;
import bgu.spl.mics.application.passiveObjects.DeliveryVehicle;
import bgu.spl.mics.application.passiveObjects.MoneyRegister;
import bgu.spl.mics.application.passiveObjects.OrderReceipt;
import bgu.spl.mics.application.passiveObjects.OrderResult;

/**
 * Selling service in charge of taking orders from customers.
 * Holds a reference to the {@link MoneyRegister} singleton of the store.
 * Handles {@link BookOrderEvent}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link ResourcesHolder}, {@link Inventory}.
 * <p>
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class SellingService extends MicroService {
    private MoneyRegister moneyRegister = MoneyRegister.getInstance();
    private int lastTick;

    public SellingService(String name) {
        super(name);
    }

    @Override
    protected void initialize() {
        subscribeBroadcast(TickBroadcast.class, tick -> {
            if (tick.getTick()==tick.getDuration()) {
                terminate();
            }
            else {
                this.lastTick = tick.getTick();
            }

        });
        subscribeEvent(BookOrderEvent.class, bookOrderEvent -> {
            boolean chargeSuccess=false;
            int newID=bookOrderEvent.getId();
            Future<Integer> future1 = (Future<Integer>) sendEvent(new CheckAvailabilityEvent(bookOrderEvent.getBook()));
            // first, try to check if the book is available.
            if (future1 != null) {
                int result = future1.get();
                if (result != -1) {
                    // if the book is available, try to charge the customer.
                    synchronized (bookOrderEvent.getCustomer()) {
                        if (bookOrderEvent.getCustomer().canAfford(result)) {
                            // if the customer can afford the payment, try to take the book. then, if the operation successed, charge the customer.
                            Future<OrderResult> take = sendEvent(new TakeBookEvent(bookOrderEvent.getBook()));
                            if (take.get() == OrderResult.SUCCESSFULLY_TAKEN) {
                                moneyRegister.chargeCreditCard(bookOrderEvent.getCustomer(), result);
                                chargeSuccess = true;
                            } } }
                    // if the payment was successful, make a receipt for the customer.
                    if (chargeSuccess==true){
                            int proccessTick = this.lastTick;
                            // making the receipt object, then sending the receipt to the MoneyRegister and the customer.
                            OrderReceipt receipt = new OrderReceipt(newID, bookOrderEvent.getCustomer().getId(), result, this.lastTick, bookOrderEvent.getOrderedTick(), proccessTick, this.getName(), bookOrderEvent.getBook());
                            moneyRegister.file(receipt);
                            bookOrderEvent.getCustomer().addRecipe(receipt);
                            complete(bookOrderEvent,receipt);
                            // now make a delivery of the book to the customer.
                            Future<Boolean> booleanFuture = sendEvent(new DeliveryEvent(bookOrderEvent.getCustomer().getAddress(), bookOrderEvent.getCustomer().getDistance()));
                        } } }
            if (chargeSuccess==false){
                complete(bookOrderEvent, null);
            } });
    }

}
