# Online-book-store
A synchronized Micro-Service framework and an online books store application on top of it.
The application manages online book orders while considering book availability in the inventory, handling payment and deliveries using proper Multi-Threading approach.

### Goals
The goal of the following project is to practice concurrent programming on the Java 8 environment.
This project requires a good understanding of Java Threads, Java Synchronization, Lambdas, and Callbacks.
Also, practice of using JUNIT tests as part of the TDD approach.

## Main objects

### Future
A Future object represents a promised result - an object that will eventually be resolved to hold a result of
some operation. The class allows retrieving the result once it is available. Future<T> has the following
methods:
* T get(): Retrieves the result of the operation. This method waits for the computation to complete in the case that it has not yet been completed.
* resolve(T result): called upon the completion of the computation, this method sets the result of the operation to a new value.
* isDone(): returns true if this object has been resolved.
* T get(long timeout, TimeUnit unit): Retrieves the result of the operation if available. If not, waits for at most the given time unit for the computation to complete, and then retrieves its result, if available.

### Message
A data-object which is passed between Micro-Services as means of communication.
The Message interface is a Marker interface. That is, it is used only to mark other types of objects as messages. 

### Broadcas
A Marker interface extending Message. When sending Broadcast messages using the Message-Bus it will be received by all the subscribers of this Broadcast-message type (the message Class).

### Event<T>
A marker interface extending Message. A Micro-Service that sends an Event message expects to be notified when the Micro-Service that processes the event has completed processing it. The event has a generic type variable T, which indicates its expected result type (should be passed back to the sending Micro-Service). The Micro-Service that has received the event must call the method ‘Complete’ of the Message-Bus once it has completed treating the event, in order to resolve the result of the event.

### MessageBus
The Message-Bus is a shared object used for communication between MicroServices. It should be implemented as a thread-safe singleton, as it is shared between all the MicroServices in the system.
The Message-Bus manages the queues of the Micro-Services. It creates a queue for each MicroService using the ‘register’ method. When the Micro-Service calls the ‘unregister’ method of the Message-Bus, the Message-Bus should remove its queue and clean all references related to that Micro-Service. Once the queue is created, a Micro-Service can take the next message in the queue using the ‘awaitMessage’ method. The ‘awaitMessage’ method is blocking, that is, if there are no messages available in the Micro-Service queue, it should wait until a message becomes available.
* register: a Micro-Service calls this method in order to register itself. This method should create a queue for the Micro-Service in the Message-Bus.
* subscribeEvent: A Micro-Service calls this method in order to subscribe itself for some type of event (the specific class type of the event is passed as a parameter).
* subscribeBroadcast: A Micro-Service calls this method in order to subscribe itself for some type of broadcast message (The specific class type of the event is passed as a parameter).
* sendBroadcast: A Micro-Service calls this method in order to add a broadcast message to the queues of all Micro-Services which subscribed to receive this specific message type.
* Future<T> sendEvent(Event<T> e): A Micro-Service calls this method in order to add the event e to the message queue of one of the Micro-Services which have subscribed to receive events of type e.getClass(). The messages are added in a round-robin fashion. This
method returns a Future object - from this object the sending Micro-Service can retrieves the result of processing the event once it is completed. If there is no suitable Micro-Service, should return null.
* void complete(Event<T> e, T result): A Micro-Service calls this method in order to notify the Message-Bus that the event was handled, and providing the result of handling the request. The Future object associated with event e should be resolved to the result given as a parameter.
* unregister: A Micro-Service calls this method in order to unregister itself. Should remove the message queue allocated to the Micro Service and clean all the references related to this Message-Bus.
* awaitMessage(Microservice m): A Micro-Service calls this method in order to take a message from its allocated queue. This method is blocking (waits until there is an available message and returns it).
  
### MicroService
The MicroService is an abstract class that any Micro-Service in the system must extend. The abstract MicroService class is responsible to get and manipulate the singleton MessageBus instance. Derived classes of MicroService should never directly touch the Message-Bus. Instead, they have a set of internal protected wrapping methods they can use. When subscribing to message types, the derived class also supplies a callback function.
The MicroService stores this callback function together with the type of the message it is related to. The Micro-Service is a Runnable (i.e., suitable to be executed in a thread). The run method implements a message loop. When a new message is taken from the queue, the Micro-Service will invoke the appropriate callback function.
When the Micro-Service starts executing the run method, it registers itself with the Message-Bus, and then calls the abstract initialize method. The initialize method allows derived classes to perform any required initialization code (e.g., subscribe to messages). Once the initialization code completes, the actual message-loop should start. The Micro-Service should fetch messages from its message queue using the Message-Bus’s awaitMessage method. For each message it should execute the corresponding callback. The MicroService class also contains a terminate method that should signal the message-loop that it should end. Each Micro-Service contains a name given to it in construction time (the name is not guaranteed to be unique).

## Passive Objects (Application)
This section contains a list of passive classes (a.k.a., non-runnable classes):
* BookInventoryInfo
An object which represents information about a single book in the store. It contains the following fields:
All the callbacks that belong to the micro-service must be executed inside its own message-loop. Registration, Initialization, and Unregistration of the Micro-Service must be executed inside its run method.
* OrderReceipt
An object representing a receipt that should be sent to a customer after buying a book (when the customers OrderBookEvent has been completed).
* Inventory
This object is implemented as a thread safe singleton. The Inventory object holds a collection of BookInventoryInfo: One for each book the store offers. Only the following methods should be publicly available from the store:
* DeliveryVehicle
this object represents a delivery vehicle in the system.
Contains the method deliver which gets as parameter the address of the customer and the distance from the store and simulates delivery by calling to sleep with the required number of milliseconds for delivery.
* MoneyRegister
This object holds a list of receipt issued by the store. This class should be implemented as a thread
safe singleton.
* ResourcesHolder
Holds a collection of DeliveryVehicle.
* Customer
Contains id number of the customer, name, address, credit card and more.

### Messages:
* BookOrderEvent
- An event that is sent when a client of the store wishes to buy a book. Its expected response
type is an OrderReceipt. In the case that the order was not completed successfully, null should
be returned as the event result.
- Processing: if the book is available in the inventory then the book should be taken, and the
credit card of the customer should be charged. If there is not enough money in the credit card,
the order should be discarded, and the book should not be taken from the inventory.
- Sent by the WebAPI service, the WebAPI waits for this event to complete to get the result.
The event is sent to a SellingService to handle it.
Note: there might be several orders of the same customer processed concurrently by
different micro-services.
* TickBroadcast
A broadcast messages that is sent at every passed clock tick. This message must contain the current tick (int).
* DeliveryEvent
- An event that is sent when the BookOrderEvent is successfully completed and a delivery is
required.
- Processing: should try to acquire a delivery vehicle. If the acquisition succeeds, then should
call the method deliver of the acquired delivery vehicle and wait until it completes. Otherwise,
should wait the vehicle becomes available.
- It is sent to the LogisticsService once the order is successfully completed. The sender does not
need to wait on the event since it does not return a value.
Important note: You may create new types of messages as you see fit.

## Active Objects (Micro-Services)
Micro-services MUTS NOT know each other. A micro-service must not hold a reference to other micro-services, neither get a reference to another micro-service.
* TimeService (There is only one instance of this service).
This Micro-Service is our global system timer (handles the clock ticks in the system). It is
responsible for counting how much clock ticks passed since its initial execution and notify every
other Micro-Service (that is interested) about it using the TickBroadcast. The TimeService receives
the number of milliseconds each clock tick takes (speed:int) together with the number of ticks
before termination (duration:int) as constructor arguments. The TimeService stops sending
TickBroadcast messages after the duration ends. Be careful that you are not blocking the event
loop of the timer Micro-Service. You can use the Timer class in java to help you with that. The
current time always start from 1.
* APIService:
This Micro-Service describes one client connected to the application. The APIService expects to
get the following arguments to its constructor:
- orderSchedule: List – contains the orders that the client needs to make (every order
has a corresponding time tick to send the OrderBookEvent). The list is not guaranteed
to be sorted. The APIService will send the OrderBookEvent on the tick specified on
the schedule (each order contains one book only, that is, orders on the same tick are
supposed to be processed by different SellingService (in case there is more than one).
* SellingService:
This Micro-Service handles OrderBookEvent. It holds a reference MoneyRegister object.
* ResourceService: this Micro-Service holds a reference to the ResourcesHolder instance.
* InventoryService: this Micro-Service holds a reference to the Inventory instance.
* LogisticsService: this Micro-Service handles the DeliveryEvent. 
