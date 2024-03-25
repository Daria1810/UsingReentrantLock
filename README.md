Simulate a sensor system monitoring a house. Sensor and Dispatcher are Threads. 
Sensor produces a SensorEvent at random times and adds the event to the EventQueue 
which is implemented as a queue. The type of event depends on the type of the sensor that produced it. 
Dispatcher consumes events by removing them from the EventQueue.
