/**
 * This file is part of LibOSM by Marcus Wolschon <a href="mailto:Marcus@Wolscon.biz">Marcus@Wolscon.biz</a>.
 * You can purchase support for a sensible hourly rate or
 * a commercial license of this file (unless modified by others) by contacting him directly.
 *
 *  LibOSM is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  LibOSM is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with LibOSM.  If not, see <http://www.gnu.org/licenses/>.
 *
 ***********************************
 * Editing this file:
 *  -For consistent code-quality this file should be checked with the
 *   checkstyle-ruleset enclosed in this project.
 *  -After the design of this file has settled it should get it's own
 *   JUnit-Test that shall be executed regularly. It is best to write
 *   the test-case BEFORE writing this class and to run it on every build
 *   as a regression-test.
 */
package org.openstreetmap.osm.data;

import org.openstreetmap.osmosis.core.domain.v0_6.Node;
import org.openstreetmap.osmosis.core.domain.v0_6.Way;
import org.openstreetmap.osmosis.core.domain.v0_6.Relation;


/**
 * Instances of this interface can filter a
 * Set of Nodes or Ways.
 * @author <a href="mailto:Marcus@Wolschon.biz">Marcus Wolschon</a>
 */
public interface Selector {

    /**
     * @param aNode the Node to test.
     * @param aMap the map we operate on.
     * @return true if we are allowed to use that Node.
     */
    boolean isAllowed(final IDataSet aMap, final Node aNode);

    /**
     * @param aWay the way to test.
     * @param aMap the map we operate on.
     * @return true if we are allowed to use that way.
     */
    boolean isAllowed(final IDataSet aMap, final Way aWay);
    /**
     * @param aRelation the relation to test.
     * @param aMap the map we operate on.
     * @return true if we are allowed to use that relaiton.
     */
    boolean isAllowed(final IDataSet aMap, final Relation aRelation);

}
