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

package org.tquadrat.foundation.fx.control.skin;

import static java.time.temporal.ChronoField.HOUR_OF_DAY;
import static java.time.temporal.ChronoField.MINUTE_OF_HOUR;
import static javafx.beans.binding.Bindings.createDoubleBinding;
import static javafx.beans.binding.Bindings.createIntegerBinding;
import static org.apiguardian.api.API.Status.INTERNAL;
import static org.apiguardian.api.API.Status.STABLE;
import static org.tquadrat.foundation.lang.CommonConstants.EMPTY_STRING;
import static org.tquadrat.foundation.lang.Objects.nonNull;
import static org.tquadrat.foundation.lang.Objects.requireNonNullArgument;

import java.time.Instant;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;

import org.apiguardian.api.API;
import org.tquadrat.foundation.annotation.ClassVersion;
import org.tquadrat.foundation.fx.control.RangeSlider;
import org.tquadrat.foundation.fx.control.TimeSlider;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.scene.control.SkinBase;
import javafx.util.StringConverter;

/**
 *  The default skin for instances of
 *  {@link TimeSlider}.
 *
 *  @extauthor Thomas Thrien - thomas.thrien@tquadrat.org
 *  @version $Id: TimeSliderSkin.java 1121 2024-03-16 16:51:23Z tquadrat $
 *  @since 0.4.6
 *
 *  @UMLGraph.link
 */
@ClassVersion( sourceVersion = "$Id: TimeSliderSkin.java 1121 2024-03-16 16:51:23Z tquadrat $" )
@API( status = STABLE, since = "0.4.6" )
public class TimeSliderSkin extends SkinBase<TimeSlider>
{
        /*---------------*\
    ====** Inner Classes **====================================================
        \*---------------*/
    /**
     *  <p>{@summary The
     *  {@link #toString(Object)}
     *  method of this implementation of
     *  {@link StringConverter}
     *  takes a number representing the number of seconds  since the beginning
     *  of the epoch, converts it to an instance of
     *  {@link OffsetDateTime},
     *  takes the
     *  {@linkplain OffsetTime time}
     *  portion of it and converts that to a
     *  {@link String}.}</p>
     *  <p>The method
     *  {@link #fromString(String)}
     *  will do nothing; therefore this implementation of
     *  {@code StringConverter} is incomplete.</p>
     *
     *  @extauthor Thomas Thrien - thomas.thrien@tquadrat.org
     *  @version $Id: TimeSliderSkin.java 1121 2024-03-16 16:51:23Z tquadrat $
     *  @since 0.4.6
     *
     *  @UMLGraph.link
     */
    @ClassVersion( sourceVersion = "$Id: TimeSliderSkin.java 1121 2024-03-16 16:51:23Z tquadrat $" )
    @API( status = INTERNAL, since = "0.4.6" )
    private final class OffsetTimeConverter extends StringConverter<Number>
    {
            /*------------------------*\
        ====** Static Initialisations **===========================================
            \*------------------------*/
        /**
         *  The
         *  {@link java.time.format.DateTimeFormatter}
         *  that is used to convert the
         *  {@link OffsetTime}
         *  instances to Strings.
         */
        private static final DateTimeFormatter m_TimeFormatter;

        static
        {
            m_TimeFormatter = new DateTimeFormatterBuilder()
                .appendValue( HOUR_OF_DAY, 2 )
                .appendLiteral( ':' )
                .appendValue( MINUTE_OF_HOUR, 2 )
                .toFormatter();
        }

            /*--------------*\
        ====** Constructors **=================================================
            \*--------------*/
        /**
         *  Creates a new instance of {@code OffsetTimeConverter}.
         */
        public OffsetTimeConverter() { /* Just exists */ }

            /*---------*\
        ====** Methods **======================================================
            \*---------*/
        /**
         *  {@inheritDoc}
         */
        @Override
        public final Number fromString( final String s ) { return null; }

        /**
         *  {@inheritDoc}
         */
        @Override
        public final String toString( final Number number )
        {
            var retValue = EMPTY_STRING;
            if( nonNull( number ) )
            {
                final var timeSlider = getSkinnable();
                final var offsetTime = Instant.ofEpochSecond( number.longValue() )
                    .atZone( timeSlider.getTimeZone() )
                    .toOffsetDateTime()
                    .toOffsetTime();
                retValue = m_TimeFormatter.format( offsetTime );
            }

            //---* Done *----------------------------------------------------------
            return retValue;
        }   //  toString()
    }
    //  class OffsetTimeConverter

        /*-----------*\
    ====** Constants **========================================================
        \*-----------*/
    /**
     *  Nearly 24h in seconds: {@value}.
     */
    public static final double TWENTY_FOUR_HOURS = 86_399.0;

        /*------------*\
    ====** Attributes **=======================================================
        \*------------*/
    /**
     *  The
     *  {@link RangeSlider}
     *  instance that does the work for the {@code TimeSlider}.
     */
    private final RangeSlider m_Content;

    /*--------------*\
    ====** Constructors **=====================================================
        \*--------------*/
    /**
     *  Creates a new instance of {@code TimeSliderSkin}.
     *
     *  @param  control The reference for the control.
     */
    public TimeSliderSkin( final TimeSlider control )
    {
        super( requireNonNullArgument( control, "control" ) );

        //---* Create the children and add them *------------------------------
        final var min = convertZonedDateTimeToSeconds( control.minValueProperty() );
        final var max = convertZonedDateTimeToSeconds( control.maxValueProperty() );
        final var lowValue = convertZonedDateTimeToSeconds( composeZonedDateTime( control.getDay(), control.getLowValue(), control.getTimeZone() ) );
        final var highValue = convertZonedDateTimeToSeconds( composeZonedDateTime( control.getDay(), control.getHighValue(), control.getTimeZone() ) );
        m_Content = new RangeSlider( min, max, lowValue, highValue );
        getChildren().add( m_Content );

        //---* Bind the children *---------------------------------------------
        final var maxValueBinding = createDoubleBinding( () -> convertZonedDateTimeToSeconds( getSkinnable().maxValueProperty() ), getSkinnable().maxValueProperty() );
        m_Content.maxProperty().bind( maxValueBinding );

        final var minValueBinding = createDoubleBinding( () -> convertZonedDateTimeToSeconds( getSkinnable().minValueProperty() ), getSkinnable().minValueProperty() );
        m_Content.minProperty().bind( minValueBinding );

        final var minorTickCountBinding = createIntegerBinding( () -> control.getGranularity().getMinorTickCount(), control.granularityProperty() );
        m_Content.minorTickCountProperty().bind( minorTickCountBinding );

        m_Content.snapToTicksProperty().bind( control.snapToTicksProperty() );

        m_Content.lowValueProperty().addListener( ($,oldValue,newValue) ->
        {
           if( nonNull( newValue ) && !newValue.equals( oldValue ) )
           {
               getSkinnable().lowValueProperty().set( convertSecondsToOffsetTime( newValue.longValue() ) );
           }
        });
        m_Content.highValueProperty().addListener( ($,oldValue,newValue) ->
        {
           if( nonNull( newValue ) && !newValue.equals( oldValue ) )
           {
               getSkinnable().highValueProperty().set( convertSecondsToOffsetTime( newValue.longValue() ) );
           }
        });

        control.lowValueProperty().addListener( $ ->
        {
            final var timeSlider = getSkinnable();
            final var day = timeSlider.getDay();
            final var timeZone = timeSlider.getTimeZone();
            final var time = timeSlider.getLowValue();
            m_Content.setLowValue( (double) day.atTime( time ).atZoneSameInstant( timeZone ).toEpochSecond() );
        });

        control.highValueProperty().addListener( $ ->
        {
            final var timeSlider = getSkinnable();
            final var day = timeSlider.getDay();
            final var timeZone = timeSlider.getTimeZone();
            final var time = timeSlider.getHighValue();
            m_Content.setHighValue( (double) day.atTime( time ).atZoneSameInstant( timeZone ).toEpochSecond() );
        });

        //---* Configure the range slider *------------------------------------
        final var timeConverter = new OffsetTimeConverter();
        m_Content.setLabelFormatter( timeConverter );
        //noinspection MagicNumber
        m_Content.setMajorTickUnit( 3_600.0 ); // one hour
        m_Content.setShowTickLabels( true );
        m_Content.setShowTickMarks( true );
    }   //  TimeSliderSkin()

        /*---------*\
    ====** Methods **==========================================================
        \*---------*/
    /**
     *  Composes an instance of
     *  {@link ZonedDateTime}
     *  from the given components.
     *
     *  @param  day The day.
     *  @param  time    The time.
     *  @param  timeZone    The time zone.
     *  @return The zoned date time instance.
     */
    private final ZonedDateTime composeZonedDateTime( final LocalDate day, final OffsetTime time, final ZoneId timeZone )
    {
        final var retValue = day.atTime( time ).atZoneSameInstant( timeZone );

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  composeZonedDateTime()

    /**
     *  Converts the given
     *  {@link ZonedDateTime}
     *  to seconds since the start of the epoch.
     *
     *  @param  dateTime    The time and date.
     *  @return The seconds since the epoch.
     */
    private final double convertZonedDateTimeToSeconds( final ZonedDateTime dateTime )
    {
        final var retValue = (double) dateTime.toEpochSecond();

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  convertZonedDateTimeToSeconds()

    /**
     *  Converts the
     *  {@link ZonedDateTime}
     *  value of the given
     *  {@link ObjectProperty}
     *  to seconds since the start of the epoch.
     *
     *  @param  dateTimeProperty    The property with the time and date.
     *  @return The seconds since the epoch.
     */
    private final double convertZonedDateTimeToSeconds( final ReadOnlyObjectProperty<ZonedDateTime> dateTimeProperty )
    {
        final var retValue = convertZonedDateTimeToSeconds( requireNonNullArgument( dateTimeProperty, "dateTimeProperty" ).get() );

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  convertZonedDateTimeToSeconds()

    /**
     *  Convert the given {@code long} value, representing the seconds since
     *  the start of the epoch, to an instance of
     *  {@link OffsetTime}.
     *
     *  @param  seconds The seconds.
     *  @return The offset time.
     */
    private final OffsetTime convertSecondsToOffsetTime( final long seconds )
    {
        final var retValue = Instant.ofEpochSecond( seconds )
            .atZone( getSkinnable().getTimeZone() )
            .toOffsetDateTime()
            .toOffsetTime();

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  convertSecondsToOffsetTime()
}
//  class TimeSliderSkin

/*
 *  End of File
 */