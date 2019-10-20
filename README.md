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
