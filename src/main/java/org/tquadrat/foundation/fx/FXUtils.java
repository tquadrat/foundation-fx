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

package org.tquadrat.foundation.fx;

import static org.apiguardian.api.API.Status.STABLE;
import static org.tquadrat.foundation.lang.Objects.requireNonNullArgument;

import org.apiguardian.api.API;
import org.tquadrat.foundation.annotation.ClassVersion;
import org.tquadrat.foundation.annotation.UtilityClass;
import org.tquadrat.foundation.exception.PrivateConstructorForStaticClassCalledError;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.stage.Window;

/**
 *  Some useful utility function for the work with JavaFX.
 *
 *  @extauthor Thomas Thrien - thomas.thrien@tquadrat.org
 *  @version $Id: HexUtils.java 747 2020-12-01 12:40:38Z tquadrat $
 *  @since 0.4.2
 *
 *  @UMLGraph.link
 */
@ClassVersion( sourceVersion = "$Id: HexUtils.java 747 2020-12-01 12:40:38Z tquadrat $" )
@API( status = STABLE, since = "0.4.2" )
@UtilityClass
public final class FXUtils
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
    private FXUtils() { throw new PrivateConstructorForStaticClassCalledError( FXUtils.class ); }

        /*---------*\
    ====** Methods **==========================================================
        \*---------*/
    /**
     *  Retrieves the window owning the
     *  {@link Button}
     *  that fired the given
     *  {@link ActionEvent}.
     *
     *  @param  event   The action event.
     *  @return The owning window.
     *  @throws IllegalArgumentException    The event was not fired by a
     *      {@code Button}.
     */
    public static final Window retrieveOwner( final ActionEvent event ) throws IllegalArgumentException
    {
        final var retValue = switch( requireNonNullArgument( event, "event" ).getSource() )
        {
            case null -> throw new IllegalArgumentException( "Event source is null" );

            /*
             * When the triggering event originates from a button, that button
             * is part of a scene (perhaps defined by an FXML file or created
             * from scratch). So we just walk up the scenes to the root, and
             * from there we take the Window that owns that button. That is
             * quite often a Stage.
             */
            case final Button button -> button.getScene()
                .getRoot()
                .getScene()
                .getWindow();

            default -> throw new IllegalArgumentException( "Inappropriate event source" );
        };

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  retrieveOwner()
}
//  class FXUtils

/*
 *  End of File
 */