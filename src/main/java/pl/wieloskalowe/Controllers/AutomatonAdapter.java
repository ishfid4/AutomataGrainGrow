package pl.wieloskalowe.Controllers;

import pl.wieloskalowe.Automaton;
import pl.wieloskalowe.GameOfLife;
import pl.wieloskalowe.Neighborhood;

import java.util.Observable;

/**
 * Created by ishfi on 04.05.2017.
 */
public class AutomatonAdapter extends Observable{
    private Automaton automaton;
    private Neighborhood neighborhood;
    int width, height;


    public AutomatonAdapter() {

    }
}
