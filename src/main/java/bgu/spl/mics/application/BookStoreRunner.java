package bgu.spl.mics.application;

import bgu.spl.mics.application.passiveObjects.*;
import bgu.spl.mics.application.services.*;
import com.google.gson.*;

import java.io.*;
import java.util.HashMap;
import java.util.Vector;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * This is the Main class of the application. You should parse the input file,
 * create the different instances of the objects, and run the system.
 * In the end, you should output serialized objects.
 */

public class BookStoreRunner implements Serializable {
    public static AtomicInteger count = new AtomicInteger(0);
    public static void main(String[] args) {

        JsonParser jsonParser = new JsonParser();
        try {

            //accessing the json file:
            JsonObject jsonObject = (JsonObject) jsonParser.parse(new FileReader(args[0]));
            loadBooks(jsonObject);
            loadVehicles(jsonObject);
            TimeService t = initializeTimeService(jsonObject);
            JsonObject servicesFromJson = jsonObject.getAsJsonObject("services");
            Customer[] customersArray = createCustomersArray(servicesFromJson);

            // creating vector of threads:
            Vector<Vector<Thread>> threads = new Vector<>();
            threads.add(creatingSellingsThreads(servicesFromJson));
            threads.add(creatingInventoryThreads(servicesFromJson));
            threads.add(creatingLogisticsThreads(servicesFromJson));
            threads.add(creatingResourcesThreads(servicesFromJson));
            threads.add(creatingAPIservicesThreads(customersArray));

            int services = 0;
            for (Vector<Thread> v: threads){
                services=services+v.size();
            }
            for (Vector<Thread> v: threads){
                for (Thread tt: v){
                    tt.start();
                }
            }

            // when all serviced finished initialization, start the time ticking:
            while (!(count.get() == services)) {

            }
                int speed = t.getSpeed();
                int duration = t.getDuration();
                TimeService timeService = new TimeService(speed, duration);
                Thread time = new Thread(timeService);
                time.start();

                // now wait for all the threads to finish:
                try{
                    time.join();
                    for (Vector<Thread> v: threads){
                        for (Thread tt: v){
                            tt.join();
                        }
                    }

                    // write object to file:
                    HashMap<Integer,Customer> customerHashMap = new HashMap();
                    for (int i=0;i<customersArray.length;i++){
                        customerHashMap.put(customersArray[i].getId(),customersArray[i]);
                    }
                    printToFile(args[1],customerHashMap);
                }
                catch (InterruptedException ex){}

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        // write objects to files:
        Inventory.getInstance().printInventoryToFile(args[2]);
        MoneyRegister.getInstance().printOrderReceipts(args[3]);
        printToFile(args[4],MoneyRegister.getInstance());
    }

    //creating books array (BookInventoryInfo) and load them
    private static void loadBooks(JsonObject jsonObject) {
        JsonArray booksFromJson = jsonObject.getAsJsonArray("initialInventory");
        BookInventoryInfo[] booksArray = new Gson().fromJson(booksFromJson, BookInventoryInfo[].class);
        Inventory.getInstance().load(booksArray);
    }

    //creating resources array and from that creating vehicles array (DeliveryVehicle) and load them
    private static void loadVehicles(JsonObject jsonObject) {
        JsonArray resourcesFromJson = jsonObject.getAsJsonArray("initialResources");
        JsonArray vehiclesFromJson = resourcesFromJson.get(0).getAsJsonObject().getAsJsonArray("vehicles");
        DeliveryVehicle[] vehiclesArray = new Gson().fromJson(vehiclesFromJson, DeliveryVehicle[].class);
        ResourcesHolder.getInstance().load(vehiclesArray);
    }

    //creating services json object and initializing all the sub-members
    private static TimeService initializeTimeService(JsonObject jsonObject) {
        JsonObject servicesFromJson = jsonObject.getAsJsonObject("services");
        JsonObject timeFromJson = servicesFromJson.getAsJsonObject("time");
        TimeService timeService = new Gson().fromJson(timeFromJson, TimeService.class);
        return timeService;
    }

    //creating customer array with all the sub classes
    private static Customer[] createCustomersArray(JsonObject servicesFromJson) {
        JsonArray customersFromJson = servicesFromJson.getAsJsonArray("customers");
        Customer[] customersArray = new Gson().fromJson(customersFromJson, Customer[].class);
        return customersArray;
    }

    private static Vector<Thread> creatingSellingsThreads(JsonObject servicesFromJson) {
        JsonPrimitive sellingServices = servicesFromJson.getAsJsonPrimitive("selling");
        Vector<Thread> v = new Vector<>(sellingServices.getAsInt());
        for (int i = 1; i <= sellingServices.getAsInt(); i++) {
            v.add(new Thread(new SellingService("sellingService " + i)));
        }
        return v;
    }

    private static Vector<Thread> creatingInventoryThreads(JsonObject servicesFromJson) {
        JsonPrimitive inventoryServices = servicesFromJson.getAsJsonPrimitive("inventoryService");
        Vector<Thread> v = new Vector<>(inventoryServices.getAsInt());
        for (int i = 1; i <= inventoryServices.getAsInt(); i++) {
            v.add(new Thread(new InventoryService("inventoryService " + i)));
        }
        return v;
    }

    private static Vector<Thread> creatingLogisticsThreads(JsonObject servicesFromJson) {
        JsonPrimitive logisticsServices = servicesFromJson.getAsJsonPrimitive("logistics");
        Vector<Thread> v = new Vector<>(logisticsServices.getAsInt());
        for (int i = 1; i <= logisticsServices.getAsInt(); i++) {
            v.add(new Thread(new LogisticsService("logistics " + i)));
        }
        return v;
    }

    private static Vector<Thread> creatingResourcesThreads(JsonObject servicesFromJson) {
        JsonPrimitive resourcesServices = servicesFromJson.getAsJsonPrimitive("resourcesService");
        Vector<Thread> v = new Vector<>(resourcesServices.getAsInt());
        for (int i = 1; i <= resourcesServices.getAsInt(); i++) {
            v.add(new Thread(new ResourceService("resourcesService " + i)));

        }
        return v;
    }

    private static Vector<Thread> creatingAPIservicesThreads(Customer[] customersArray) {
        Vector<Thread> v = new Vector<>(customersArray.length);
        for (int i = 1; i <= customersArray.length; i++) {
            v.add(new Thread(new APIService("APIService " + i,customersArray[i - 1])));

        }
        return v;
    }

    public static void printToFile(String fileName,Object objectToWrite)  {
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(fileName);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
            objectOutputStream.writeObject(objectToWrite);
            objectOutputStream.close();
            fileOutputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
