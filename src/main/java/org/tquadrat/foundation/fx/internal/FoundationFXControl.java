/*
 * ============================================================================
 *  Copyright © 2002-2024 by Thomas Thrien.
 *  All Rights Reserved.
 * ============================================================================
 *  Licensed to the public under the agreements of the GNU Lesser General Public
 *  License, version 3.0 (the "License"). You may obtain a copy of the License at
 *
 *       http://www.gnu.org/licenses/lgpl.html
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 *  WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 *  License for the specific language governing permissions and limitations
 *  under the License.
 */

package org.tquadrat.foundation.fx.internal;

import static org.apiguardian.api.API.Status.INTERNAL;
import static org.tquadrat.foundation.lang.Objects.isNull;
import static org.tquadrat.foundation.lang.Objects.requireNonNullArgument;
import static org.tquadrat.foundation.lang.Objects.requireNotBlankArgument;

import org.apiguardian.api.API;
import org.tquadrat.foundation.annotation.ClassVersion;
import javafx.scene.control.Control;

/**
 *  <p>{@summary The abstract base class for the custom controls introduced by
 *  the Foundation FX project.}</p>
 *
 *  @extauthor Thomas Thrien - thomas.thrien@tquadrat.org
 *  @inspired  {@href https://controlsfx.github.io/ ControlsFX Project}
 *  @version $Id: FoundationFXControl.java 1112 2024-03-10 14:16:51Z tquadrat $
 *  @since 0.4.6
 *
 *  @UMLGraph.link
 */
@ClassVersion( sourceVersion = "$Id: FoundationFXControl.java 1112 2024-03-10 14:16:51Z tquadrat $" )
@API( status = INTERNAL, since = "0.4.6" )
public abstract class FoundationFXControl extends Control
{
        /*------------*\
    ====** Attributes **=======================================================
        \*------------*/
    /**
     *  The cache URL for the <i>User Agent Stylesheet</i>.
     *
     *  @see #getUserAgentStylesheet(Class,String)
     */
    private String m_Stylesheet;

        /*--------------*\
    ====** Constructors **=====================================================
        \*--------------*/
    /**
     *  Creates a new instance of {@code FoundationFXControl}.
     */
    protected FoundationFXControl() { /* Just exists */ }

        /*---------*\
    ====** Methods **==========================================================
        \*---------*/
    /**
     *  <p>{@summary A helper method that ensures that the resource based
     *  lookup of the <i>User Agent Stylesheet</i> only happens once.} It
     *  caches the external form of the resource URL.</p>
     *
     *  @param  lookupClass The class that is used for the resource lookup.
     *  @param  fileName    The name of the user agent stylesheet.
     *  @return The external form of the URL for user agent stylesheet (the
     *      path).
     */
    protected final String getUserAgentStylesheet( final Class<?> lookupClass, final String fileName)
    {
        if( isNull( m_Stylesheet ) )
        {
            m_Stylesheet = requireNonNullArgument( lookupClass, "lookupClass" ).getResource( requireNotBlankArgument( fileName, "fileName" ) ).toExternalForm();
        }

        //---* Done *----------------------------------------------------------
        return m_Stylesheet;
    }   //  getUserAgentStylesheet()
}
//  class FoundationFXControl