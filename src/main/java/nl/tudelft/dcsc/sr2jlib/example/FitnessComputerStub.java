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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.logging.Level;
import java.util.logging.Logger;
import nl.tudelft.dcsc.sr2jlib.fitness.Fitness;
import nl.tudelft.dcsc.sr2jlib.fitness.FitnessComputerInstance;
import nl.tudelft.dcsc.sr2jlib.grammar.Grammar;

/**
 *
 * The stab realizing a demo fitness computer realization
 *
 * @author <a href="mailto:ivan.zapreev@gmail.com"> Dr. Ivan S. Zapreev </a>
 */
public class FitnessComputerStub extends FitnessComputerInstance {

    //Stores the reference to the logger
    private static final Logger LOGGER = Logger.getLogger(FitnessComputerStub.class.getName());

    /**
     * The basic constructor.
     *
     */
    public FitnessComputerStub() {
    }

    /**
     * Computes an individual dof fitness
     *
     * Here we check that the value of the function at point (1.0, ..., 1.0) is
     * within [-0.25,+0.25]
     *
     * @param fn the dof function
     * @return the fitness
     */
    private double compute_fitness(final int mgr_id, final int dof_idx, final Method fn) {
        //Obtain the number of variables for the given manager and dof
        //This is for the case it is not known at this point, one could
        //have also realize/initialize the fitness computer class with
        //this knowledge/data beforehand.
        final int num_vars = Grammar.inst(mgr_id, dof_idx).get_num_vars();
        //Initialize the argument
        final double[] args = new double[num_vars];
        for (int idx = 0; idx < num_vars; ++idx) {
            args[idx] = 1.0;
        }

        //Compute the fitness
        double ftn = 0.0;
        try {
            double value = (Double) fn.invoke(null, new Object[]{args});
            ftn = ((value >= -0.25) && (value <= +0.25)) ? 1.0 : 0.0;
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
            LOGGER.log(Level.SEVERE, "Falied to compute the fitness", ex);
        }

        return ftn;
    }

    @Override
    public Fitness compute_fitness(int mgr_id, Method[] vf)
            throws IllegalStateException, IllegalArgumentException,
            ClassNotFoundException, IllegalAccessException,
            InvocationTargetException {
        //Here vf[] stores the vector function of the individual generated
        //by the process manager identified by mgr_id, in this stub we assume
        //a single ProcessManager instance and thus this parameter is ignored
        double ftn = 0.0;

        //Compute the fitness as the dof fitness vector length
        for (int dof_idx = 0; dof_idx < vf.length; ++dof_idx) {
            ftn += Math.pow(compute_fitness(mgr_id, dof_idx, vf[dof_idx]), 2);
        }
        return new Fitness(Math.sqrt(ftn));
    }

}
