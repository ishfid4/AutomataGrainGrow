package pl.wieloskalowe.Controllers;

import pl.wieloskalowe.automaton.AutomatonAdapter;

public class SimualtionExecutor extends Thread {
    private int iterations = 0;
    private AutomatonAdapter automatonAdapter;
    private int maxStep = Integer.MAX_VALUE;

    public SimualtionExecutor(AutomatonAdapter automatonAdapter) {
        this.automatonAdapter = automatonAdapter;
    }

    public SimualtionExecutor(AutomatonAdapter automatonAdapter, int maxStep) {
        this.automatonAdapter = automatonAdapter;
        this.maxStep = maxStep;
    }

    public void setMaxStep(int maxStep) {
        this.maxStep = maxStep;
    }

    @Override
    public synchronized void run() {
        while(maxStep > 0 && (automatonAdapter.timesNotChanged <= 100 || automatonAdapter.boardChanged || iterations == 0)){
            iterations++;
            maxStep--;
            automatonAdapter.nextAutomatonState();
        }

        System.out.println("Iterations count: " + iterations +" Ende");
    }
}
