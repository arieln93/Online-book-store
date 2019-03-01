package java;
import bgu.spl.mics.Future;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

public class FutureTest {

    private Future<Integer> f1;

    @Before
    public void setUp() throws Exception {
        f1=new Future<>();
    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void get() {
        f1.resolve(1);
        assertTrue(f1.get()==1);

    }

    @Test
    public void resolve() {
        f1.resolve(2);
        assertTrue("Error: object is suppose to be done",f1.isDone());
        assertTrue("Error: object is suppose to be 2",f1.get()==2);
    }

    @Test
    public void isDone() {
        f1.resolve(1);
        assertTrue("Error: object is suppose to be done",f1.isDone());
    }
    @Test
    public void isNotDone() {
        assertFalse("Error: object is not done yet",f1.isDone());
    }

    @Test
    public void get1() {
        f1.resolve(3);
        assertTrue(f1.get(5,TimeUnit.MILLISECONDS)==3);

    }
    @Test
    public void get1Null() {
        assertTrue(f1.get(5,TimeUnit.MILLISECONDS)==null);

    }
}