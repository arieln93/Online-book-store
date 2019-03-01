package bgu.spl.mics.application.passiveObjects;


import bgu.spl.mics.application.BookStoreRunner;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Passive data-object representing the store inventory.
 * It holds a collection of {@link BookInventoryInfo} for all the
 * books in the store.
 * <p>
 * This class must be implemented safely as a thread-safe singleton.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You can add ONLY private fields and methods to this class as you see fit.
 */
public class Inventory implements Serializable {
    /**
     * Ariel: Maybe the array/list of books should be thread-safe.
     */
    private ConcurrentLinkedQueue<BookInventoryInfo> bookList;

    private static class holder {
        private static Inventory instance = new Inventory();
    }

    private Inventory() {
        this.bookList = new ConcurrentLinkedQueue<>();
    }

    /**
     * Retrieves the single instance of this class.
     */
    public static Inventory getInstance() {
        return holder.instance;
    }


    /**
     * Initializes the store inventory. This method adds all the items given to the store
     * inventory.
     * <p>
     *
     * @param inventory Data structure containing all data necessary for initialization
     *                  of the inventory.
     */
    public void load(BookInventoryInfo[] inventory) {
        this.bookList = new ConcurrentLinkedQueue<>();
        for (int i = 0; i < inventory.length; i++) {
            this.bookList.add(inventory[i]);
        }

    }

    /**
     * Attempts to take one book from the store.
     * <p>
     *
     * @param book Name of the book to take from the store
     * @return an {@link Enum} with options NOT_IN_STOCK and SUCCESSFULLY_TAKEN.
     * The first should not change the state of the inventory while the
     * second should reduce by one the number of books of the desired type.
     */
    public OrderResult take(String book) {
            for (BookInventoryInfo b : bookList) {
                if (b.getBookTitle().compareTo(book)==0) {
                    if (b.getAmountInInventory() > 0) {
                        if(b.takeOne()) {
                            return OrderResult.SUCCESSFULLY_TAKEN;
                        }
                    }
                }
            }
            return OrderResult.NOT_IN_STOCK;
    }


    /**
     * Checks if a certain book is available in the inventory.
     * <p>
     *
     * @param book Name of the book.
     * @return the price of the book if it is available, -1 otherwise.
     */
    public int checkAvailabiltyAndGetPrice(String book) {
        for (BookInventoryInfo b : bookList) {
            if (b.getBookTitle().compareTo(book) == 0) {
                if (b.getAmountInInventory() > 0) {
                    return b.getPrice();
                }
            }
        }
        return -1;
    }

    /**
     * <p>
     * Prints to a file name @filename a serialized object HashMap<String,Integer> which is a Map of all the books in the inventory. The keys of the Map (type {@link String})
     * should be the titles of the books while the values (type {@link Integer}) should be
     * their respective available amount in the inventory.
     * This method is called by the main method in order to generate the output.
     */
    public synchronized void printInventoryToFile(String filename) {
        HashMap bookHashMap = new HashMap<String, Integer>();
        for (BookInventoryInfo b: bookList){
            bookHashMap.put(b.getBookTitle(),b.getAmountInInventory());
        }
        BookStoreRunner.printToFile(filename, bookHashMap);
    }
}

