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

import static java.lang.Math.round;
import static java.time.temporal.ChronoField.HOUR_OF_DAY;
import static java.time.temporal.ChronoField.MINUTE_OF_HOUR;
import static javafx.beans.binding.Bindings.createDoubleBinding;
import static javafx.beans.binding.Bindings.createIntegerBinding;
import static javafx.beans.binding.Bindings.createObjectBinding;
import static org.apiguardian.api.API.Status.INTERNAL;
import static org.apiguardian.api.API.Status.STABLE;
import static org.tquadrat.foundation.lang.CommonConstants.EMPTY_STRING;
import static org.tquadrat.foundation.lang.Objects.nonNull;
import static org.tquadrat.foundation.lang.Objects.requireNonNullArgument;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;

import org.apiguardian.api.API;
import org.tquadrat.foundation.annotation.ClassVersion;
import org.tquadrat.foundation.fx.control.RangeSlider;
import org.tquadrat.foundation.fx.control.TimeSlider;
import javafx.beans.InvalidationListener;
import javafx.beans.property.ObjectProperty;
import javafx.beans.value.ObservableBooleanValue;
import javafx.scene.control.SkinBase;
import javafx.util.StringConverter;

/**
 *  The default skin for instances of
 *  {@link TimeSlider}.
 *
 *  @extauthor Thomas Thrien - thomas.thrien@tquadrat.org
 *  @version $Id: TimeSliderSkin.java 1115 2024-03-12 23:43:18Z tquadrat $
 *  @since 0.4.6
 *
 *  @UMLGraph.link
 */
@ClassVersion( sourceVersion = "$Id: TimeSliderSkin.java 1115 2024-03-12 23:43:18Z tquadrat $" )
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
     *  @version $Id: TimeSliderSkin.java 1115 2024-03-12 23:43:18Z tquadrat $
     *  @since 0.4.6
     *
     *  @UMLGraph.link
     */
    @ClassVersion( sourceVersion = "$Id: TimeSliderSkin.java 1115 2024-03-12 23:43:18Z tquadrat $" )
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

    /**
     *  The
     *  {@link StringConverter}
     *  instance that is used to convert the values to time strings.
     */
    private final OffsetTimeConverter m_TimeConverter;

        /*------------------------*\
    ====** Static Initialisations **===========================================
        \*------------------------*/

        /*--------------*\
    ====** Constructors **=====================================================
        \*--------------*/
    /**
     *  Creates a new instance of {@code TimeSliderSkin}.
     *
     *  @param  control The reference for the control.
     *  @param  lowValueProperty   The low value property of the control.
     *  @param  highValueProperty   The high value property of the control.
     */
    public TimeSliderSkin( final TimeSlider control, final ObjectProperty<OffsetTime> lowValueProperty, final ObjectProperty<OffsetTime> highValueProperty )
    {
        super( requireNonNullArgument( control, "control" ) );

        final var initialHighValue = requireNonNullArgument( highValueProperty, "highValueProperty" ).get();
        final var initialLowValue = requireNonNullArgument( lowValueProperty, "lowValueProperty" ).get();

        //---* Create the children and add them *------------------------------
        m_Content = new RangeSlider();
        getChildren().add( m_Content );

        //---* Create the bindings *-------------------------------------------
        final var highValueBinding = createObjectBinding( () -> convertSecondsToOffsetTime( m_Content.getHighValue() ), m_Content.highValueProperty(), control.timeZoneProperty() );
        final var lowValueBinding = createObjectBinding( () -> convertSecondsToOffsetTime( m_Content.getLowValue() ), m_Content.lowValueProperty(), control.timeZoneProperty() );

        @SuppressWarnings( "OverlyLongLambda" )
        final var maxBinding = createDoubleBinding( () ->
        {
            final var day = control.getDay();
            final var max = control.getMax();
            final Instant instant;
            if( nonNull( max ) )
            {
                instant = max.atDate( day ).toInstant();
            }
            else
            {
                instant = day.atStartOfDay( control.getTimeZone() ).plusDays( 1 ).toInstant();
            }
            return Double.valueOf( (double) instant.getEpochSecond() );
        }, control.dayProperty(), control.maxProperty() );

        @SuppressWarnings( "OverlyLongLambda" )
        final var minBinding = createDoubleBinding( () ->
        {
            final var day = control.getDay();
            final var min = control.getMin();
            final Instant instant;
            if( nonNull( min ) )
            {
                instant = min.atDate( day ).toInstant();
            }
            else
            {
                instant = day.atStartOfDay( control.getTimeZone() ).toInstant();
            }
            return Double.valueOf( (double) instant.getEpochSecond() );
        }, control.dayProperty(), control.minProperty() );

        final var minorTickCountBinding = createIntegerBinding( () -> control.getGranularity().getMinorTickCount(), control.granularityProperty() );

        //---* Apply the bindings *--------------------------------------------
        highValueProperty.bind( highValueBinding );
        lowValueProperty.bind( lowValueBinding );

        m_Content.maxProperty().bind( maxBinding );
        m_Content.minProperty().bind( minBinding );
        m_Content.minorTickCountProperty().bind( minorTickCountBinding );

        //---* Create the listeners and apply them *---------------------------
        final InvalidationListener snapToTick = observable -> m_Content.setSnapToTicks( ((ObservableBooleanValue) observable).get() );
        control.snapToTicksProperty().addListener( snapToTick );

        //---* Set the initial values *----------------------------------------
        setHighValue( initialHighValue );
        setLowValue( initialLowValue );

        //---* Configure the range slider *------------------------------------
        m_TimeConverter = new OffsetTimeConverter();
        m_Content.setLabelFormatter( m_TimeConverter );
        m_Content.setSnapToTicks( control.isSnapToTicks() );
        //noinspection MagicNumber
        m_Content.setMajorTickUnit( 3_600.0 ); // one hour
        m_Content.setShowTickLabels( true );
        m_Content.setShowTickMarks( true );
    }   //  TimeSliderSkin()

        /*---------*\
    ====** Methods **==========================================================
        \*---------*/
    /**
     *  Convert the given {@code double} value, representing the seconds since
     *  the start of the epoch, to an instance of
     *  {@link OffsetTime}.
     *
     *  @param  seconds The seconds.
     *  @return The offset time.
     */
    private final OffsetTime convertSecondsToOffsetTime( final double seconds )
    {
        final var retValue = Instant.ofEpochSecond( round( seconds ) )
            .atZone( getSkinnable().getTimeZone() )
            .toOffsetDateTime()
            .toOffsetTime();

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  convertSecondsToOffsetTime()

    /**
     *  <p>{@summary Sets the high value for the slider.}</p>
     *  <p>The method
     *  {@link TimeSlider#setHighValue(OffsetTime)}
     *  cannot set the property
     *  {@link TimeSlider#highValueProperty()}
     *  directly, because that is bound to
     *  {@link RangeSlider#highValueProperty() m_Content.highValueProperty()}.</p>
     *
     *  @param  highValue The value.
     */
    public final void setHighValue( final OffsetTime highValue )
    {
        if( nonNull( highValue ) )
        {
            final var timeSlider = getSkinnable();
            final var day = timeSlider.getDay();
            final var instant = highValue.atDate( day ).toInstant();
            m_Content.setHighValue( (double) instant.getEpochSecond() );
        }
    }   //  setHighValue()

    /**
     *  <p>{@summary Sets the low value for the slider.}</p>
     *  <p>The method
     *  {@link TimeSlider#setLowValue(OffsetTime)}
     *  cannot set the property
     *  {@link TimeSlider#lowValueProperty()}
     *  directly, because that is bound to
     *  {@link RangeSlider#lowValueProperty() m_Content.lowValueProperty()}.</p>
     *
     *  @param  lowValue The value.
     */
    public final void setLowValue( final OffsetTime lowValue )
    {
        if( nonNull( lowValue ) )
        {
            final var timeSlider = getSkinnable();
            final var day = timeSlider.getDay();
            final var instant = lowValue.atDate( day ).toInstant();
            m_Content.setLowValue( (double) instant.getEpochSecond() );
        }
    }   //  setLowValue()
}
//  class TimeSliderSkin

/*
 *  End of File
 */