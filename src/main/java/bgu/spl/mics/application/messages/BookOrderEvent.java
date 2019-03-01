package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.application.passiveObjects.Customer;

public class BookOrderEvent implements Event {
    private int id;
    private Customer customer;
    private String bookToOrder;
    private int orderedTick;

    public BookOrderEvent(int id,Customer customer, String bookToOrder, int orderedTick){
        this.customer=customer;
        this.bookToOrder = bookToOrder;
        this.orderedTick = orderedTick;
        this.id = id;
    }
    public Customer getCustomer(){
        return this.customer;
    }
    public String getBook(){
        return this.bookToOrder;
    }
    public int getOrderedTick(){
        return this.orderedTick;
    }

    public int getId() {
        return id;
    }
}
