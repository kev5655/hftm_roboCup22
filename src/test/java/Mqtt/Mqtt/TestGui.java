package Mqtt.Mqtt;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class TestGui implements Runnable {

    public TestGui() {

        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(interupptRun, 0, 5, TimeUnit.SECONDS);
    }

    final Runnable interupptRun = () -> {
        //ObstacleMsg obMsg = new ObstacleMsg();
    };

    @Override
    public void run() {

    }

    public static void main(String[] args) {
        new TestGui();
    }
}
