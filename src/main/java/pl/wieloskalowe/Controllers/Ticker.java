package pl.wieloskalowe.Controllers;

import pl.wieloskalowe.automaton.AutomatonAdapter;

import java.util.Timer;
import java.util.TimerTask;

public class Ticker {
    private final Timer timer = new Timer("Ticker-timer");
    private TimerTask task = null;
    private int rate = 0;
    private int iterations = 0;
    private AutomatonAdapter automatonAdapter;

    public Ticker(AutomatonAdapter automatonAdapter) {
        this.automatonAdapter = automatonAdapter;
    }

    private synchronized TimerTask createTickTask(Timer t) {
        return new TimerTask() {
            @Override
            public void run() {
                while(automatonAdapter.timesNotChanged <= 100 || automatonAdapter.boardChanged || iterations == 0){
                    iterations++;
                    automatonAdapter.nextAutomatonState();
//                    task = createTickTask(timer);
//                    timer.schedule(task, rate);
//                }else{
                }

                System.out.println("Iterations count: " + iterations +" Ende");
                stop();
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
