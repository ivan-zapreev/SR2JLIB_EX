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

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.logging.Level;
import nl.tudelft.dcsc.sr2jlib.grid.GridObserver;
import nl.tudelft.dcsc.sr2jlib.grid.Individual;

/**
 * An example empty implementation for the sake of testing
 *
 * @author <a href="mailto:ivan.zapreev@gmail.com"> Dr. Ivan S. Zapreev </a>
 */
public class GridObserverStub implements GridObserver {

    //Stores the reference to the logger
    private static final Logger LOGGER = Logger.getLogger(GridObserverStub.class.getName());

    private final List<Individual> m_best_inds = new ArrayList<>();
    protected boolean m_is_observing = false;

    @Override
    public synchronized void start_observing() {
        //Here one can start the UI elements which visualize the individuals'
        //grid or fitness and also do some other initialization
        LOGGER.log(Level.INFO, "Grid observations are started!");
        m_is_observing = true;
    }

    @Override
    public synchronized void set(Individual ind) {
        if (m_is_observing) {
            //Here one could re-compute the mean fitness values and check if the 
            //desired fitness is reached. Also an update of the related UI elements
            //is in order.
            LOGGER.log(Level.INFO, "Adding new individual: {0}", ind);

            //Below we keep track of the best fit individuals
            if (m_best_inds.isEmpty()) {
                m_best_inds.add(ind);
            } else {
                final Individual max_ind = m_best_inds.get(0);
                if (max_ind.is_equal(ind)) {
                    m_best_inds.add(ind);
                } else {
                    if (max_ind.is_less(ind)) {
                        m_best_inds.clear();
                        m_best_inds.add(ind);
                    }
                }
            }
        }
    }

    @Override
    public synchronized void remove(Individual ind) {
        //Here one could re-compute the mean fitness values. Also an update of
        //the related UI elements is in order.
        LOGGER.log(Level.INFO, "Killing old individual: {0}", ind);
    }

    @Override
    public synchronized void stop_observing() {
        //Here one can stop the UI elements which visualize the individuals'
        //grid or fitness and also do some other finilizing work
        LOGGER.log(Level.INFO, "Grid observations are stopped!");
        m_is_observing = false;
    }

    @Override
    public synchronized List<Individual> get_best_fit_ind() {
        return new ArrayList<>(m_best_inds);
    }

}
