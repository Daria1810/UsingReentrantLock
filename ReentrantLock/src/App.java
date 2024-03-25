import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.Condition;

class EventQueue { //shared class
    int[] a = new int[10]; 
    private Queue<SensorEvent> eventQueue = new LinkedList<>();
    private Lock lock = new ReentrantLock();
    private Condition notFull = lock.newCondition();
    private Condition notEmpty = lock.newCondition();

    private static final int MAX_CAPACITY = 10;

    public void addEvent(SensorEvent event) {
        lock.lock();
        try {
            while (eventQueue.size() == MAX_CAPACITY) {
                notFull.await();
            }
            eventQueue.add(event);
            notEmpty.signal();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            lock.unlock();
        }
    }

    public SensorEvent removeEvent() {
        lock.lock();
        try {
            while (eventQueue.isEmpty()) {
                notEmpty.await();
            }
            SensorEvent event = eventQueue.poll();
            notFull.signal();
            return event;
        } catch (InterruptedException e) {
            e.printStackTrace();
            return null;
        } finally {
            lock.unlock();
        }
    }

    public boolean isEmpty() {
        return eventQueue.isEmpty();
    }

}

class SensorEvent {
    private int source;
    private long time;
    private String type;

    public SensorEvent(int source, long time, String type) {
        this.source = source;
        this.time = time;
        this.type = type;
    }

    public int getSource() {
        return source;
    }

    public long getTime() {
        return time;
    }

    public String getType() {
        return type;
    }
}

class Sensor extends Thread {
    private int id;
    private EventQueue eventQueue;
    private Random random;
    

    public Sensor(int id, EventQueue eventQueue) {
        this.id = id;
        this.eventQueue = eventQueue;
        this.random = new Random();

    }

    @Override
    public void run() {
        while (true) {
            try {
                Thread.sleep(random.nextInt(2000));
                String eventType = generateEventType();
                SensorEvent event = new SensorEvent(id, System.currentTimeMillis(), eventType);
                eventQueue.addEvent(event);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } 
        }
    }

    private String generateEventType() {
        if (id == 1) {
            return "Motion";
        } else if (id == 2) {
            return "Temperature";
        } else if (id == 3) {
            return "Smoke";
        } else {
            return "Unknown";
        }
    }
}

class Dispatcher extends Thread {
    private EventQueue eventQueue;

    public Dispatcher(EventQueue eventQueue) {
        this.eventQueue = eventQueue;
        
    }

    @Override
    public void run() {
        while (true) {
            try {
                    SensorEvent event = eventQueue.removeEvent();
                    consumeEvent(event);
                    System.out.println(event);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void consumeEvent(SensorEvent event) {
        try {
            Thread.sleep(50);
            System.out.println(
                    "Source: " + event.getSource() + ", Time: " + event.getTime() + ", Type: " + event.getType());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

public class App {
    public static void main(String[] args) {
        EventQueue eventQueue = new EventQueue();
        Sensor sensor1 = new Sensor(1, eventQueue);
        Sensor sensor2 = new Sensor(2, eventQueue);
        Sensor sensor3 = new Sensor(3, eventQueue);
        Sensor sensor4 = new Sensor(4, eventQueue);
        Dispatcher dispatcher = new Dispatcher(eventQueue);

        sensor1.start();
        sensor2.start();
        sensor3.start();
        sensor4.start();
        dispatcher.start();

        // EventQueue eventQueue = new EventQueue();
        // new SensorGUI(eventQueue);
    }
}
