/* 
 * Copyright (C) 2018 Dr. Ivan S. Zapreev <ivan.zapreev@gmail.com>
 *
 *  Visit my Linked-in profile:
 *     https://nl.linkedin.com/in/zapreevis
 *  Visit my GitHub:
 *     https://github.com/ivan-zapreev
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package nl.tudelft.dcsc.sr2jlib.example;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import nl.tudelft.dcsc.sr2jlib.grid.Individual;
import nl.tudelft.dcsc.sr2jlib.ProcessManager;
import nl.tudelft.dcsc.sr2jlib.ProcessManagerConfig;
import nl.tudelft.dcsc.sr2jlib.SelectionType;
import nl.tudelft.dcsc.sr2jlib.err.ErrorListener;
import nl.tudelft.dcsc.sr2jlib.err.ErrorManager;
import nl.tudelft.dcsc.sr2jlib.fitness.FitnessManager;
import nl.tudelft.dcsc.sr2jlib.grammar.Grammar;
import nl.tudelft.dcsc.sr2jlib.grammar.GrammarConfig;
import nl.tudelft.dcsc.sr2jlib.grammar.expr.Expression;

/**
 * This example class shows how the SR2JLIB can be used as a batch Symbolic
 * Regression tool.
 *
 * @author <a href="mailto:ivan.zapreev@gmail.com"> Dr. Ivan S. Zapreev </a>
 */
public class BasicExample {

    //The synchronization object
    private final Object SYNCHRONIZER = new Object();
    //The flag indicating that there is no individuals generated yet
    private boolean no_individuals = true;

    /**
     * Notifies the waiting main thread that the process manager has finished.
     */
    private void stop_manager_finished() {
        synchronized (SYNCHRONIZER) {
            no_individuals = false;
            SYNCHRONIZER.notify();
        }
    }

    /**
     * Notifies the waiting main thread that a sufficiently fit individual is
     * generated and we can stop the process manager.
     */
    private void stop_fit_found(Individual ind) {
        if (ind.get_fitness().get_fitness() == 1.0) {
            synchronized (SYNCHRONIZER) {
                no_individuals = false;
                SYNCHRONIZER.notify();
            }
        }
    }

    /**
     * Waits until the process manager generates a sufficiently fit individual.
     */
    private void wait_fit_found() {
        try {
            synchronized (SYNCHRONIZER) {
                while (no_individuals) {
                    LOGGER.log(Level.INFO, "Waiting for the individuals!");
                    SYNCHRONIZER.wait(100);
                }
            }
        } catch (InterruptedException ex) {
            LOGGER.log(Level.SEVERE, "Exception while sleeping in a test case", ex);
        }
    }

    //Stores the reference to the logger
    private static final Logger LOGGER = Logger.getLogger(GridObserverStub.class.getName());

    //Stores the grammatica text 
    private static final String GRAMMAR_TEXT
            = "R:=[x1](P);[$signum(x1)](P)@0.25;[-x1](R)@0.1;[x1+x2](R,R)@4;[x1*x2](R,R)@4\n"
            + "P:=[x1](D);[1/x1](D);[x1](V)@2;[-x1](P)@0.1;[x1+x2](P,P)@4;[x1*x2](P,P)@4";

    //Stores the number of arguments, per dimension, for the vector function
    private static final int[] NUM_DOF_VARS = new int[]{2, 1};
    //Stores the number of dimensions of the vector function
    private static final int NUM_VF_DOFS = NUM_DOF_VARS.length;

    //Stores the process manager id
    private static final int PROCESS_MANAGER_ID = 0;

    //Stores an instance of the process manager
    private ProcessManager m_manager = null;

    /**
     * The basic constructor
     */
    public BasicExample() {
    }

    /**
     * Instantiate and register grammars. It is possible to use the same grammar
     * for different process managers and dimensions by supplying it for the
     * grammar registering function with different process manager and dof id
     * values. In this example we will only have process manager breeding 2D
     * individuals. The first and second dofs of the latter follow the same
     * grammar except for the former having two variable and the latter having
     * one.
     */
    public void setup_grammar() {
        Grammar.clear_grammars();
        for (int dof_idx = 0; dof_idx < NUM_VF_DOFS; ++dof_idx) {
            //Instantiate the dof grammar config file
            final GrammarConfig g_cfg = new GrammarConfig(GRAMMAR_TEXT,
                    300, 0.5, NUM_DOF_VARS[dof_idx], 0.5, 2.0, false, 100, 0.5);
            //Instantiate the dof grammar
            final Grammar grammar = Grammar.create_grammar(g_cfg);
            //Register the dof grammar for the given process manager and dof index
            Grammar.register_grammar(PROCESS_MANAGER_ID, dof_idx, grammar);
        }
        Grammar.prepare_grammars();
    }

    /**
     * Configure the process manager and supplies it with an example grid
     * observer implementation. In this example there is just one process
     * manager that has 2D vector function individuals where the first and
     * second dimension functions have 2 and 1 arguments respectively. Each
     * process manager has an individual thread pool and grid so population
     * breeding in two different managers are independent of each other. In this
     * example we only have one manager.
     */
    public void prepare_manager() {
        //Prepare configuration object
        final ProcessManagerConfig config = new ProcessManagerConfig(
                PROCESS_MANAGER_ID, 0.1, 20, 10000, NUM_VF_DOFS, 30, 30, 1, 1,
                SelectionType.VALUE, false, false, 0, 0, new GridObserverStub() {
            @Override
            public synchronized void set(Individual ind) {
                super.set(ind);
                //Check if the fit individual is found
                stop_fit_found(ind);
            }
        }, (mgr) -> {
                    //Note: Here one can add marking this process manager is finished.
                    //It is useful when several process managers running in parallel.
                    LOGGER.log(Level.INFO,
                            "The ProcessManager-{0} has stopped!", PROCESS_MANAGER_ID);
                    stop_manager_finished();
                });
        //Initialize the process manager variable
        m_manager = new ProcessManager(config);
    }

    /**
     * Instantiate and register the fitness computer. This is a compulsory step
     * which one can easily overlook! The fitness computer is responsible for
     * implementing fitness computations which are problem specific. In this
     * example we provide a FitnessComputerStub realizing some basic fitness
     * computation. Note that the fitness computer is shared between all running
     * instances of process managers.
     */
    public void prepare_ftn_computer() {
        FitnessManager.set_inst(new FitnessComputerStub());
    }

    /**
     * Instantiate and register the error listener
     */
    public void prepare_err_listener() {
        //Implementing the error listener is optional but is recommended as in
        //case when a grammar is broken some individuals may fail to compile.
        //The corresponding exceptions and errors will be reported through this
        //interface so that they can be handled, e.g. by stopping the manager.
        //The default implementation of the error listener used only does logging.
        ErrorManager.inst().set_listener(new ErrorListener() {
            @Override
            public void error(String msg, Exception ex) {
                LOGGER.log(Level.SEVERE, msg, ex);
                m_manager.stop(10, null);
            }
        });
    }

    /**
     * Start and stop the manager as soon as there is at least one individual
     * added. In real-life application the manager will be stopped once a
     * sufficiently fit individual is produced which is to be done from the grid
     * observer: @see nl.tudelft.dcsc.sr2jlib.grid.GridObserver
     */
    public void execute() {
        //Start the process manager
        LOGGER.log(Level.INFO, "Starting the manager");
        m_manager.start();

        //Wait until a fit individual is found
        wait_fit_found();

        //Stop the process manager
        LOGGER.log(Level.INFO, "Stopping the manager");
        m_manager.stop(10, null);

        //Log the individual
        List<Individual> inds = m_manager.get_best_fit_ind();
        LOGGER.log(Level.INFO, "**************************************************");
        LOGGER.log(Level.INFO, "Found {0} fit individuals: ", inds.size());
        int ind_idx = 0;
        for (Individual ind : inds) {
            List<Expression> exprs = ind.get_expr_list();
            LOGGER.log(Level.INFO, ">>>>>>>");
            LOGGER.log(Level.INFO, "Individual #{0} ({1}) has {2} dofs",
                    new Object[]{ind_idx, ind, exprs.size()});
            int exp_idx = 0;
            for (Expression expr : exprs) {
                LOGGER.log(Level.INFO, "Individual #{0} dof {1} expression is: {2}",
                        new Object[]{ind_idx, exp_idx, expr.to_text()});
                exp_idx++;
            }
            ind_idx++;
        }
    }

    public static void main(String[] args) {
        //Instantiate the example class
        final BasicExample example = new BasicExample();

        //Set up grammar(s)
        example.setup_grammar();

        //Prepare the process manager(s)
        example.prepare_manager();

        //Prepare the fitness computer
        example.prepare_ftn_computer();

        //Prepare the error listener
        example.prepare_err_listener();

        //Execute symbolic regression
        example.execute();
    }
}
