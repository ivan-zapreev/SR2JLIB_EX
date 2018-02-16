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

import java.util.logging.Logger;
import java.util.logging.Level;
import nl.tudelft.dcsc.sr2jlib.grid.GridObserver;
import nl.tudelft.dcsc.sr2jlib.grid.Individual;

/**
 *
 * An example empty implementation for the sake of testing
 *
 * @author Dr. Ivan S. Zapreev
 */
public class GridObserverStub implements GridObserver {

    //Stores the reference to the logger
    private static final Logger LOGGER = Logger.getLogger(GridObserverStub.class.getName());

    @Override
    public void start_observing() {
        //Here one can start the UI elements which visualize the individuals'
        //grid or fitness and also do some other initialization
        LOGGER.log(Level.INFO, "Grid observations are started!");
    }

    @Override
    public void add_individual(Individual ind) {
        //Here one could re-compute the mean fitness values and check if the 
        //desired fitness is reached. Also an update of the related UI elements
        //is in order.
        LOGGER.log(Level.INFO, "Adding new individual: {0}", ind);
    }

    @Override
    public void kill_individual(Individual ind) {
        //Here one could re-compute the mean fitness values. Also an update of
        //the related UI elements is in order.
        LOGGER.log(Level.INFO, "Killing old individual: {0}", ind);
    }

    @Override
    public void stop_observing() {
        //Here one can stop the UI elements which visualize the individuals'
        //grid or fitness and also do some other finilizing work
        LOGGER.log(Level.INFO, "Grid observations are stopped!");
    }

}
