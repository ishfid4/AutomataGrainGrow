package pl.wieloskalowe.Controllers;

import pl.wieloskalowe.automaton.AutomatonAdapter;

import java.util.Timer;
import java.util.TimerTask;

public class Ticker {
    private final Timer timer = new Timer("Ticker-timer");
    private TimerTask task = null;
    private int rate = 10;
    private AutomatonAdapter automatonAdapter;

    public Ticker(AutomatonAdapter automatonAdapter) {
        this.automatonAdapter = automatonAdapter;
    }

    private synchronized TimerTask createTickTask(Timer t) {
        return new TimerTask() {
            @Override
            public void run() {
                automatonAdapter.nextAutomatonState();
                task = createTickTask(timer);
                timer.schedule(task, rate);
            }
        };
    }

    public synchronized void start() {
        if(task != null)
            task.cancel();
        task = createTickTask(timer);
        timer.schedule(task, rate);
    }

    public synchronized void stop() {
        task.cancel();
        task = null;
    }

}
