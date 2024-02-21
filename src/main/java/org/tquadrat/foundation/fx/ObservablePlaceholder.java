/*
 * ============================================================================
 * Copyright © 2002-2024 by Thomas Thrien.
 * All Rights Reserved.
 * ============================================================================
 * Licensed to the public under the agreements of the GNU Lesser General Public
 * License, version 3.0 (the "License"). You may obtain a copy of the License at
 *
 *      http://www.gnu.org/licenses/lgpl.html
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */

package org.tquadrat.foundation.fx;

import static org.apiguardian.api.API.Status.STABLE;

import org.apiguardian.api.API;
import org.tquadrat.foundation.annotation.ClassVersion;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;

/**
 *  <p>{@summary An implementation of the
 *  {@link Observable}
 *  interface that just serves as a placeholder.}</p>
 *  <p>As an instance of this class will never change its state, it will never
 *  call any of the registered listeners; consequently, a listener added by a
 *  call to
 *  {@link #addListener(InvalidationListener)}
 *  will not even be stored somewhere, so that a call to
 *  {@link #removeListener(InvalidationListener)}
 *  won't do anything either.</p>
 *  <p>It is useful when creating bindings that do not change their status.</p>
 *
 *  @extauthor Thomas Thrien - thomas.thrien@tquadrat.org
 *  @version $Id: HexUtils.java 747 2020-12-01 12:40:38Z tquadrat $
 *  @since 0.4.2
 *
 *  @UMLGraph.link
 */
@ClassVersion( sourceVersion = "$Id: HexUtils.java 747 2020-12-01 12:40:38Z tquadrat $" )
@API( status = STABLE, since = "0.4.2" )
public final class ObservablePlaceholder implements Observable
{
        /*--------------*\
    ====** Constructors **=====================================================
        \*--------------*/
    /**
     *  Creates a new instance of {@code ObservablePlaceholder}.
     */
    public ObservablePlaceholder() { /* Just exists */ }

        /*---------*\
    ====** Methods **==========================================================
        \*---------*/
    /**
     *  {@inheritDoc}
     */
    @Override
    public final void addListener( final InvalidationListener listener ) { /* Does nothing */ }

    /**
     *  {@inheritDoc}
     */
    @Override
    public final void removeListener( final InvalidationListener listener ) { /* Does nothing */ }
}
//  class ObservablePlaceholder

/*
 *  End of File
 */