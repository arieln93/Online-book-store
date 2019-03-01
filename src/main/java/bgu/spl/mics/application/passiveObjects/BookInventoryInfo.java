package bgu.spl.mics.application.passiveObjects;

import bgu.spl.mics.application.BookStoreRunner;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Passive data-object representing a information about a certain book in the inventory.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You may add fields and methods to this class as you see fit (including public methods).
 */
public class BookInventoryInfo {
    private String bookTitle;
    private int amount;
    private int price;

    public BookInventoryInfo(String title, int price, int amount) {
        bookTitle = title;
        this.amount= amount;
        this.price = price;
    }

    /**
     * Retrieves the title of this book.
     * <p>
     *
     * @return The title of this book.
     */
    public String getBookTitle() {
        return bookTitle;
    }

    /**
     * Retrieves the amount of books of this type in the inventory.
     * <p>
     *
     * @return amount of available books.
     */
    public int getAmountInInventory() {
        return amount;
    }

    /**
     * Retrieves the price for  book.
     * <p>
     *
     * @return the price of the book.
     */
    public int getPrice() {
        return price;
    }

    /**
     * Trying to get one book from the stock.
     * <p>
     *
     * @return true if the operation successeded, false if not.
     */
    public synchronized boolean takeOne() {
        if (this.amount>0) {
            this.amount--;
            return true;
        }
        else
            return false;
    }

}
