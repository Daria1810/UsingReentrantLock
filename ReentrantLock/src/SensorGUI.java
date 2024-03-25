import java.awt.*;
import java.awt.event.*;
public class SensorGUI extends Frame {
    private final EventQueue eventQueue;
    private final Sensor[] sensors;
    private final TextArea textArea;

    public SensorGUI(EventQueue eventQueue) {
        this.eventQueue = eventQueue;
        this.sensors = new Sensor[4];
        this.textArea = new TextArea();
        initComponents();
    }

    private void initComponents() {
        setLayout(new FlowLayout());

        add(textArea);

        for (int i = 0; i < 4; i++) {
            final int finalI = i;
            Button button = new Button("Start Sensor " + (finalI + 1));
            add(button);
            button.addActionListener(new ActionListener() {
                private boolean isRunning = false;

                @Override
                public void actionPerformed(ActionEvent e) {
                    if (isRunning) {
                        button.setLabel("Start Sensor " + (finalI + 1));
                        sensors[finalI].interrupt();
                        isRunning = false;
                    } else {
                        button.setLabel("Pause Sensor " + (finalI + 1));
                        sensors[finalI] = new Sensor(finalI + 1, eventQueue);
                        sensors[finalI].start();
                        isRunning = true;
                    }
                }
            });
        }

        Dispatcher dispatcher = new Dispatcher(eventQueue);
        dispatcher.start();

        setSize(300, 200);
        setTitle("Sensor System");
        setVisible(true);
    }
}