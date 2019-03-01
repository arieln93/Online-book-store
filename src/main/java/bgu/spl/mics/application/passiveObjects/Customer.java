package bgu.spl.mics.application.passiveObjects;

import bgu.spl.mics.application.BookStoreRunner;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

/**
 * Passive data-object representing a customer of the store.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You may add fields and methods to this class as you see fit (including public methods).
 */
public class Customer implements Serializable {
    private int id;
    private String name;
    private String address;
    private int distance;
    private Vector<OrderReceipt> receiptList;
    private LinkedList<OrderPair> orderSchedule;
    private CreditCard creditCard;

    public Customer(int id, String name, String address, int distance, LinkedList<OrderPair> orderSchedule, CreditCard creditCard) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.distance = distance;
        this.orderSchedule = orderSchedule;
        this.creditCard = creditCard;
        this.receiptList = new Vector<>();
    }

    public List<OrderReceipt> getReceiptList() {
        return receiptList;
    }

    public LinkedList<OrderPair> getOrderSchedule() {
        return orderSchedule;
    }

    public CreditCard getCreditCard() {
        return creditCard;
    }

    /**
     * Retrieves the name of the customer.
     */
    public String getName() {
        return this.name;
    }

    /**
     * Retrieves the ID of the customer  .
     */
    public int getId() {
        return this.id;
    }

    /**
     * Retrieves the address of the customer.
     */
    public String getAddress() {
        return this.address;
    }

    /**
     * Retrieves the distance of the customer from the store.
     */
    public int getDistance() {
        return this.distance;
    }


    /**
     * Retrieves a list of receipts for the purchases this customer has made.
     * <p>
     *
     * @return A list of receipts.
     */
    public List<OrderReceipt> getCustomerReceiptList() {
        if (this.receiptList==null) {
            return new ArrayList<OrderReceipt>();
        }
        else {
            return new ArrayList<>(this.receiptList);
        }
    }

    /**
     * Retrieves the amount of money left on this customers credit card.
     * <p>
     *
     * @return Amount of money left.
     */
    public int getAvailableCreditAmount() {
        return creditCard.getAmount();
    }

    /**
     * Retrieves this customers credit card serial number.
     */
    public int getCreditNumber() {
        return creditCard.getNumber();
    }


    /**
     * Checks if the customer have enough money to pay the amount.
     * <p>
     *
     * @return true if the customer have enough credit. false if not.
     */
    public boolean canAfford(int amount) {
        if ((this.creditCard.amount - amount) >= 0) {
            return true;
        } else{
            return false;
        }

    }

    public synchronized void charge(int amount) {
        if (canAfford(amount)) {
            this.creditCard.amount -= amount;
        }
    }

    public void addRecipe(OrderReceipt receipt) {
        if (this.receiptList==null){
            this.receiptList= this.receiptList = new Vector<>();
        }
        this.receiptList.add(receipt);
    }

    /**
     * Inner class, representing the customer's credit card.
     */
    public class CreditCard implements Serializable{
        private int number;
        private int amount;

        public CreditCard(int number, int amount) {
            this.number = number;
            this.amount = amount;
        }


        public int getNumber() {
            return number;
        }

        public int getAmount() {
            return amount;
        }

    }

    /**
     * Inner class, representing an order that the customer would like to
     * have in the specified tick.
     */
    public class OrderPair implements Serializable {
        private String bookTitle;
        private int tick;

        public OrderPair(String bookTitle, int tick) {
            this.bookTitle = bookTitle;
            this.tick = tick;
        }

        public String getBookTitle() {
            return bookTitle;
        }

        public int getTick() {
            return tick;
        }

    }

}
