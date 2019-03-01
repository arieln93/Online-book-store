package bgu.spl.mics.application.passiveObjects;


import bgu.spl.mics.application.BookStoreRunner;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Passive object representing the store finance management.
 * It should hold a list of receipts issued by the store.
 * <p>
 * This class must be implemented safely as a thread-safe singleton.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You can add ONLY private fields and methods to this class as you see fit.
 */
public class MoneyRegister implements Serializable {
    private ConcurrentLinkedQueue<OrderReceipt> receiptList;
    private AtomicInteger totalEarnings;

    private static class holder {
        private static MoneyRegister instance = new MoneyRegister();
    }

    private MoneyRegister() {
        this.totalEarnings = new AtomicInteger();
        this.totalEarnings.set(0);
        this.receiptList = new ConcurrentLinkedQueue<>();
    }

    /**
     * Retrieves the single instance of this class.
     */
    public static MoneyRegister getInstance() {
        return holder.instance;
    }

    /**
     * Saves an order receipt in the money register.
     * <p>
     *
     * @param r The receipt to save in the money register.
     */
    public void file(OrderReceipt r) {
        this.receiptList.add(r);
        this.totalEarnings.addAndGet(r.getPrice());
    }

    /**
     * Retrieves the current total earnings of the store.
     */
    public int getTotalEarnings() {
        return this.totalEarnings.get();
    }

    /**
     * Charges the credit card of the customer a certain amount of money.
     * <p>
     *
     * @param amount amount to charge
     */
    public void chargeCreditCard(Customer c, int amount) {
        c.charge(amount);
    }

    /**
     * Prints to a file named @filename a serialized object List<OrderReceipt> which holds all the order receipts
     * currently in the MoneyRegister
     * This method is called by the main method in order to generate the output..
     */
    public void printOrderReceipts(String filename) {
        LinkedList<OrderReceipt> orderReceiptsList = new LinkedList<>();
        for (OrderReceipt r: this.receiptList){
            orderReceiptsList.add(r);
        }
        BookStoreRunner.printToFile(filename, orderReceiptsList);
    }
}
