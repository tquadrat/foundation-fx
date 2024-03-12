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
import javafx.scene.control.MenuItem;
import javafx.stage.Window;

/**
 *  Some useful utility function for the work with JavaFX.
 *
 *  @extauthor Thomas Thrien - thomas.thrien@tquadrat.org
 *  @version $Id: FXUtils.java 1112 2024-03-10 14:16:51Z tquadrat $
 *  @since 0.4.2
 *
 *  @UMLGraph.link
 */
@ClassVersion( sourceVersion = "$Id: FXUtils.java 1112 2024-03-10 14:16:51Z tquadrat $" )
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
     *  <p>{@summary Clamps the given value to be strictly between the
     *  {@code min} and {@code max} values.}</p>
     *  <p>Basically, this method does the same as
     *  {@link Math#clamp(double, double, double)},
     *  only the sequence of the arguments is different.</p>
     *
     *  @param  min The lower border.
     *  @param  value   The value.
     *  @param  max The upper border.
     *  @return The value if it is greater than {@code min} and less than
     *      {@code max}, {@code min}, when it is less than {@code min}, or
     *      {@code max} when it is greater than that.
     *  @throws IllegalArgumentException {@code min} is greater than {@code max}.
     *
     *  @since 0.4.6
     */
    @API( status = STABLE, since = "0.4.6" )
    public static final double clamp( final double min, final double value, final double max ) throws IllegalArgumentException
    {
        final var retValue = Math.clamp( value, min, max );

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  clamp()

    /**
     *  <p>{@summary Returns either {@code less} or {@code more} depending on
     *  which one is closer to {@code value}.} If {@code value} is perfectly
     *  between them, then either may be returned.</p>
     *
     *  @param  less    The lower value.
     *  @param  value   The reference value.
     *  @param  more    The upper value.
     *  @return The value that is closer to the reference.
     *
     *  @since 0.4.6
     */
    @API( status = STABLE, since = "0.4.6" )
    public static final double nearest( final double less, final double value, final double more )
    {
        final var lessDiff = value - less;
        final var moreDiff = more - value;
        final var retValue = lessDiff < moreDiff ? less : more;

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  nearest()

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

            /*
             * When the event is triggered from a menu item, this is either
             * part of a menu or a popup window. In both cases it has the same
             * path to the window that owns it.
             */
            case final MenuItem menuItem -> menuItem.getParentPopup()
                .getOwnerWindow();

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