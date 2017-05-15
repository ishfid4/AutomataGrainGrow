package pl.wieloskalowe.Controllers;

import pl.wieloskalowe.Automaton;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by ishfi on 03.05.2017.
 */
public class Ticker {
    private final Timer timer = new Timer("Ticker-timer");
    private TimerTask task = null;
    private int rate = 300;
    private AutomatonAdapter automatonAdapter;

    public Ticker(AutomatonAdapter automatonAdapter) {
        this.automatonAdapter = automatonAdapter;
    }

    private TimerTask createTickTask() {
        return new TimerTask() {
            @Override
            public void run() {
                automatonAdapter.nextAutomatonState();
            }
        };
    }

    public synchronized void start() {
        if(task != null)
            task.cancel();
        task = createTickTask();
        timer.scheduleAtFixedRate(task, rate, rate);
    }

    public synchronized void stop() {
        task.cancel();
        task = null;
    }

}
