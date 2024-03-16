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

package org.tquadrat.foundation.fx.css;

import static org.apiguardian.api.API.Status.INTERNAL;
import static org.apiguardian.api.API.Status.STABLE;
import static org.tquadrat.foundation.util.DateTimeUtils.getZoneIdAliasMap;
import static org.tquadrat.foundation.util.DateTimeUtils.retrieveCachedZoneId;

import java.time.ZoneId;

import org.apiguardian.api.API;
import org.tquadrat.foundation.annotation.ClassVersion;
import org.tquadrat.foundation.annotation.UtilityClass;
import org.tquadrat.foundation.exception.PrivateConstructorForStaticClassCalledError;
import javafx.css.ParsedValue;
import javafx.css.StyleConverter;
import javafx.scene.text.Font;

/**
 *  An implementation of
 *  {@link javafx.css.StyleConverter}
 *  for time zones (more precise, for
 *  {@link ZoneId}s).
 *
 *  @extauthor Thomas Thrien - thomas.thrien@tquadrat.org
 *  @version $Id: TimeZoneConverter.java 1116 2024-03-13 15:44:33Z tquadrat $
 *  @since 0.4.6
 *
 *  @UMLGraph.link
 */
@ClassVersion( sourceVersion = "$Id: TimeZoneConverter.java 1116 2024-03-13 15:44:33Z tquadrat $" )
@API( status = STABLE, since = "0.4.6" )
public final class TimeZoneConverter extends StyleConverter<String,ZoneId>
{
        /*---------------*\
    ====** Inner Classes **====================================================
        \*---------------*/
    /**
     *  The instance holder for this class.
     *
     *  @extauthor Thomas Thrien - thomas.thrien@tquadrat.org
     *  @version $Id: TimeZoneConverter.java 1116 2024-03-13 15:44:33Z tquadrat $
     *  @since 0.4.6
     */
    @ClassVersion( sourceVersion = "$Id: TimeZoneConverter.java 1116 2024-03-13 15:44:33Z tquadrat $" )
    @API( status = INTERNAL, since = "0.4.6" )
    @UtilityClass
    private static final class Holder
    {
            /*------------------------*\
        ====** Static Initialisations **===========================================
            \*------------------------*/
        /**
         *  The one and only instance of
         *  {@link TimeZoneConverter}.
         */
        static final TimeZoneConverter INSTANCE = new TimeZoneConverter();

            /*--------------*\
        ====** Constructors **=====================================================
            \*--------------*/
        /**
         *  No instance allowed for this class!
         */
        private Holder() { throw new PrivateConstructorForStaticClassCalledError( Holder.class ); }
    }
    //  class Holder

        /*--------------*\
    ====** Constructors **=====================================================
        \*--------------*/
    /**
     *  Creates a new instance of {@code TimeZoneConverter}.
     */
    private TimeZoneConverter() { /* Just exists */ }

        /*---------*\
    ====** Methods **==========================================================
        \*---------*/
    /**
     *  {@inheritDoc}
     */
    @Override
    public final ZoneId convert( final ParsedValue<String,ZoneId> value, final Font font )
    {
        final var retValue = retrieveCachedZoneId( (String) value.getValue(), getZoneIdAliasMap() );

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  convert()

    /**
     *  Returns a reference to the one and only instance of
     *   {@code TimeZoneConverter}.
     *
     *  @return The time zone converter.
     */
    public static final StyleConverter<String,ZoneId> getInstance() { return Holder.INSTANCE; }

    /**
     *  {@inheritDoc}
     */
    @Override
    public final String toString() { return "TimeZoneConverter";  }
}
//  class TimeZoneConverter

/*
 *  End of File
 */