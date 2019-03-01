package java;

import bgu.spl.mics.application.passiveObjects.BookInventoryInfo;
import bgu.spl.mics.application.passiveObjects.Inventory;
import bgu.spl.mics.application.passiveObjects.OrderResult;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class InventoryTest {

    private Inventory inventory = Inventory.getInstance();
    private BookInventoryInfo book1 = new BookInventoryInfo("Book1", 69, 1);
    private BookInventoryInfo book2 = new BookInventoryInfo("Book2", 100, 0);
    private BookInventoryInfo booksToLoad[] = {book1, book2};

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {

    }

    @Test
    public void getInstance() {
        assertTrue("Error: no instance",inventory!=null);
        assertTrue("Error: more then ine instance created",Inventory.getInstance()==inventory);
    }

    @Test
    public void load() {
        inventory.load(booksToLoad);
        assertTrue("Error: book suppose to be in store",inventory.checkAvailabiltyAndGetPrice("Book1") == 69);
        assertTrue("Error: book suppose to be in store",inventory.checkAvailabiltyAndGetPrice("Book2") == 100);
        assertTrue("Error: book is not suppose to be in store",inventory.checkAvailabiltyAndGetPrice("Book3") == -1);
    }

    @Test
    public void take() {
        assertTrue("Error: suppose to return book",inventory.take("Book1") == OrderResult.SUCCESSFULLY_TAKEN);
        assertTrue("Error: amount not update", book1.getAmountInInventory() == 0);
        assertTrue("Error: the amount is 0, not suppose to return book",inventory.take("Book2") == OrderResult.NOT_IN_STOCK);
        assertTrue("Error: amount changed, not needed",book2.getAmountInInventory() == 0);
        assertTrue("Error: returned a book which does not exist in store",inventory.take("Book3") == OrderResult.NOT_IN_STOCK);
    }

    @Test
    public void checkAvailabiltyAndGetPrice_Exist() {
        int actual = inventory.checkAvailabiltyAndGetPrice("Book1");
        int expected = 69;
        assertTrue("Error could'nt find book", actual == expected);
        actual = inventory.checkAvailabiltyAndGetPrice("Book2");
        expected = -1;
        assertTrue("dose'nt suppose to find a book, should return -1 ", actual == expected);
    }
    @Test
    public void checkAvailabiltyAndGetPrice_notExist() {
        int actual = inventory.checkAvailabiltyAndGetPrice("Book2");
        int expected = -1;
        assertTrue("dose'nt suppose to find a book, should return -1 ", actual == expected);
    }

    @Test
    public void printInventoryToFile() {
    }
}