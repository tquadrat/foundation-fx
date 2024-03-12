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

import static org.apiguardian.api.API.Status.STABLE;
import static org.tquadrat.foundation.lang.Objects.requireNonNullArgument;

import org.apiguardian.api.API;
import org.tquadrat.foundation.annotation.ClassVersion;
import org.tquadrat.foundation.annotation.UtilityClass;
import org.tquadrat.foundation.exception.PrivateConstructorForStaticClassCalledError;
import javafx.scene.Node;

/**
 *  Helper functions for the implementation of
 *  {@link javafx.scene.control.SkinBase Skin}s
 *  and
 *  {@link javafx.scene.control.Control Control}s.
 *
 *  @extauthor Thomas Thrien - thomas.thrien@tquadrat.org
 *  @version $Id: ControlUtils.java 1112 2024-03-10 14:16:51Z tquadrat $
 *  @since 0.4.6
 *
 *  @UMLGraph.link
 */
@ClassVersion( sourceVersion = "$Id: ControlUtils.java 1112 2024-03-10 14:16:51Z tquadrat $" )
@API( status = STABLE, since = "0.4.6" )
@UtilityClass
public final class ControlUtils
{
        /*---------------*\
    ====** Inner Classes **====================================================
        \*---------------*/

        /*-----------*\
    ====** Constants **========================================================
        \*-----------*/

        /*------------*\
    ====** Attributes **=======================================================
        \*------------*/

        /*------------------------*\
    ====** Static Initialisations **===========================================
        \*------------------------*/

        /*--------------*\
    ====** Constructors **=====================================================
        \*--------------*/
    /**
     *  No instance allowed for this class!
     */
    private ControlUtils() { throw new PrivateConstructorForStaticClassCalledError( ControlUtils.class ); }

        /*---------*\
    ====** Methods **==========================================================
        \*---------*/
    /**
     *  Moves the focus to the next node on the same hierarchy level.
     *
     *  @param  node    The node that currently has the focus.
     */
    public static final void focusNextSibling( final Node node )
    {
        final var children = requireNonNullArgument( node, "node" ).getParent().getChildrenUnmodifiable();
        final var index = children.indexOf( node );
        if( index < children.size() - 1 )
        {
            children.get( index + 1 ).requestFocus();
        }
        else
        {
            focusNextSibling( node.getParent() );
        }
    }   //  focusNextSibling()

    /**
     *  Moves the focus to the previous node on the same hierarchy level.
     *
     *  @param  node    The node that currently has the focus.
     */
    public static final void focusPreviousSibling( final Node node )
    {
        final var children = requireNonNullArgument( node, "node" ).getParent().getChildrenUnmodifiable();
        final var index = children.indexOf( node );
        if( index > 0 )
        {
            children.get( index - 1 ).requestFocus();
        }
        else
        {
            node.getParent().requestFocus();
        }
    }   //  focusPreviousSibling()
}
//  class ControlUtils

/*
 *  End of File
 */