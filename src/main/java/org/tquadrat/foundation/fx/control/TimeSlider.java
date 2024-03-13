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

package org.tquadrat.foundation.fx.control;

import static java.lang.Boolean.FALSE;
import static java.util.Locale.ROOT;
import static javafx.beans.binding.Bindings.createObjectBinding;
import static org.apiguardian.api.API.Status.INTERNAL;
import static org.apiguardian.api.API.Status.STABLE;
import static org.tquadrat.foundation.fx.control.RangeSlider.StyleableProperties.SNAP_TO_TICKS;
import static org.tquadrat.foundation.fx.control.TimeSlider.StyleableProperties.GRANULARITY;
import static org.tquadrat.foundation.fx.control.TimeSlider.StyleableProperties.TIME_ZONE;
import static org.tquadrat.foundation.lang.Objects.mapFromNull;
import static org.tquadrat.foundation.lang.Objects.nonNull;

import java.time.Duration;
import java.time.LocalDate;
import java.time.OffsetTime;
import java.time.ZoneId;
import java.util.List;

import org.apiguardian.api.API;
import org.tquadrat.foundation.annotation.ClassVersion;
import org.tquadrat.foundation.annotation.UtilityClass;
import org.tquadrat.foundation.exception.PrivateConstructorForStaticClassCalledError;
import org.tquadrat.foundation.fx.control.skin.TimeSliderSkin;
import org.tquadrat.foundation.fx.css.TimeZoneConverter;
import org.tquadrat.foundation.fx.internal.FoundationFXControl;
import org.tquadrat.foundation.lang.Objects;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.css.CssMetaData;
import javafx.css.ParsedValue;
import javafx.css.SimpleStyleableBooleanProperty;
import javafx.css.SimpleStyleableObjectProperty;
import javafx.css.StyleConverter;
import javafx.css.Styleable;
import javafx.css.StyleableBooleanProperty;
import javafx.css.StyleableObjectProperty;
import javafx.css.StyleableProperty;
import javafx.css.converter.BooleanConverter;
import javafx.scene.control.Skin;
import javafx.scene.text.Font;

/**
 *  <p>{@summary A {@code TimeSlider} is basically a
 *  {@link RangeSlider}
 *  for the input of times and durations.}</p>
 *  <p>In fact, this implementation wraps a {@code RangeSlider} instance that
 *  is configured in a special way.</p>
 *
 *  @extauthor Thomas Thrien - thomas.thrien@tquadrat.org
 *  @version $Id: TimeSlider.java 1115 2024-03-12 23:43:18Z tquadrat $
 *  @since 0.4.6
 *
 *  @UMLGraph.link
 */
@SuppressWarnings( "ClassWithTooManyMethods" )
@ClassVersion( sourceVersion = "$Id: TimeSlider.java 1115 2024-03-12 23:43:18Z tquadrat $" )
@API( status = STABLE, since = "0.4.6" )
public final class TimeSlider extends FoundationFXControl
{
        /*---------------*\
    ====** Inner Classes **====================================================
        \*---------------*/
    /**
     *  The granularity for the
     *  {@link TimeSlider}.
     *
     *  @extauthor Thomas Thrien - thomas.thrien@tquadrat.org
     *  @version $Id: TimeSlider.java 1115 2024-03-12 23:43:18Z tquadrat $
     *  @since 0.4.6
     */
    @ClassVersion( sourceVersion = "$Id: TimeSlider.java 1115 2024-03-12 23:43:18Z tquadrat $" )
    @API( status = STABLE, since = "0.4.6" )
    public static enum Granularity
    {
            /*------------------*\
        ====** Enum Declaration **=============================================
            \*------------------*/
        /**
         *  Only full hours can be selected.
         */
        ONE_HOUR( -1 ),

        /**
         *  Half hour steps are possible.
         */
        HALF_HOUR( 1 ),

        /**
         *  20 minutes steps can be selected.
         */
        TWENTY_MINUTES( 2 ),

        /**
         *  Quarter hour steps are possible.
         */
        QUARTER_HOUR( 3 ),

        /**
         *  10 minutes steps can be selected.
         */
        TEN_MINUTES( 5 ),

        /**
         *  5 minutes steps can be selected.
         */
        FIVE_MINUTES( 11 ),

        /**
         *  One minute steps can be selected.
         */
        ONE_MINUTE( 59 );


            /*------------*\
        ====** Attributes **===================================================
            \*------------*/
        /**
         *  The minor tick count for this granularity.
         */
        private final int m_MinorTickCount;

            /*--------------*\
        ====** Constructors **=================================================
            \*--------------*/
        /**
         *  Creates a new instance of {@code Granularity}.
         *
         *  @param  tickCount   The minor tick count for this granularity.
         */
        private Granularity( final int tickCount )
        {
            m_MinorTickCount = tickCount;
        }   //  Granularity()

            /*---------*\
        ====** Methods **======================================================
            \*---------*/
        /**
         *  Returns the minor tick count for this granularity.
         *
         *  @return The tick count.
         */
        public final int getMinorTickCount() { return m_MinorTickCount; }
    }
    //  enum Granularity

    /**
     *  An implementation of
     *  {@link javafx.css.StyleConverter}
     *  for the
     *  {@link Granularity}.
     *
     *  @extauthor Thomas Thrien - thomas.thrien@tquadrat.org
     *  @version $Id: TimeZoneConverter.java 1114 2024-03-12 23:07:59Z tquadrat $
     *  @since 0.4.6
     *
     *  @UMLGraph.link
     */
    @ClassVersion( sourceVersion = "$Id: TimeZoneConverter.java 1114 2024-03-12 23:07:59Z tquadrat $" )
    @API( status = INTERNAL, since = "0.4.6" )
    private static final class GranularityConverter extends StyleConverter<String,Granularity>
    {
            /*--------------*\
        ====** Constructors **=================================================
            \*--------------*/
        /**
         *  Creates a new instance of {@code GranularityConverter}.
         */
        public GranularityConverter() { /* Just exists */ }

            /*---------*\
        ====** Methods **======================================================
            \*---------*/
        /**
         *  {@inheritDoc}
         */
        @Override
        public final Granularity convert( final ParsedValue<String,Granularity> value, final Font font )
        {
            Granularity retValue;
            try
            {
                retValue = Granularity.valueOf( ((String) value.getValue()).toUpperCase( ROOT ) );
            }
            catch( final IllegalArgumentException ignored )
            {
                retValue = Granularity.QUARTER_HOUR;
            }

            //---* Done *------------------------------------------------------
            return retValue;
        }   //  convert()

        /**
         *  {@inheritDoc}
         */
        @Override
        public final String toString() { return "GranularityConverter";  }
    }
    //  class TimeZoneConverter

    /**
     *  The styleable properties for
     *  {@link TimeSlider}.
     *
     *  @extauthor Thomas Thrien - thomas.thrien@tquadrat.org
     *  @version $Id: TimeSlider.java 1115 2024-03-12 23:43:18Z tquadrat $
     *  @since 0.4.6
     */
    @SuppressWarnings( {"ProtectedInnerClass", "InnerClassTooDeeplyNested", "AnonymousInnerClass"} )
    @UtilityClass
    @ClassVersion( sourceVersion = "$Id: TimeSlider.java 1115 2024-03-12 23:43:18Z tquadrat $" )
    @API( status = STABLE, since = "0.4.6" )
    protected static final class StyleableProperties
    {
            /*------------------------*\
        ====** Static Initialisations **=======================================
            \*------------------------*/
        /**
         *  The CSS attribute for {@code GRANULARITY}.
         *
         *  @see #granularityProperty()
         */
        public static final CssMetaData<TimeSlider,Granularity> GRANULARITY = new CssMetaData<>( "-fx-granularity", new GranularityConverter(), Granularity.QUARTER_HOUR )
        {
            /**
             *  {@inheritDoc}
             */
            @Override
            public final StyleableProperty<Granularity> getStyleableProperty( final TimeSlider styleable ) { return styleable.m_GranularityProperty; }

            /**
             *  {@inheritDoc}
             */
            @Override
            public final boolean isSettable( final TimeSlider styleable ) { return !styleable.m_GranularityProperty.isBound(); }
        };

        /**
         *  The CSS attribute for {@code SNAP_TO_TICKS}.
         *
         *  @see #snapToTicksProperty()
         */
        public static final CssMetaData<TimeSlider,Boolean> SNAP_TO_TICKS = new CssMetaData<>( "-fx-snap-to-ticks", BooleanConverter.getInstance(), FALSE )
        {
            /**
             *  {@inheritDoc}
             */
            @Override
            public final StyleableProperty<Boolean> getStyleableProperty( final TimeSlider styleable ) { return styleable.m_SnapToTicksProperty; }

            /**
             *  {@inheritDoc}
             */
            @Override
            public final boolean isSettable( final TimeSlider styleable ) { return !styleable.m_SnapToTicksProperty.isBound(); }
        };

        /**
         *  The CSS attribute for the {@code TIME_ZONE}.
         *
         *  @see #timeZoneProperty()
         */
        public static final CssMetaData<TimeSlider,ZoneId> TIME_ZONE = new CssMetaData<>( "-fx-timezone", TimeZoneConverter.getInstance(), ZoneId.systemDefault() )
        {
            /**
             *  {@inheritDoc}
             */
            @Override
            public final StyleableProperty<ZoneId> getStyleableProperty( final TimeSlider styleable ) { return styleable.m_TimeZoneProperty; }

            /**
             *  {@inheritDoc}
             */
            @Override
            public final boolean isSettable( final TimeSlider styleable ) { return !styleable.m_TimeZoneProperty.isBound(); }
        };

        /**
         *  The CSS attributes for
         *  {@link TimeSlider}.
         */
        @SuppressWarnings( "StaticCollection" )
        public static final List<CssMetaData<? extends Styleable, ?>> STYLEABLES = List.of( GRANULARITY, SNAP_TO_TICKS, TIME_ZONE );

            /*--------------*\
        ====** Constructors **=================================================
            \*--------------*/
        /**
         *  No instance allowed for this class!
         */
        private StyleableProperties() { throw new PrivateConstructorForStaticClassCalledError( RangeSlider.StyleableProperties.class ); }
    }
    //  class StyleableProperties

        /*-----------*\
    ====** Constants **========================================================
        \*-----------*/

        /*------------*\
    ====** Attributes **=======================================================
        \*------------*/
    /**
     *  The property for the day for that the times should be set.
     */
    @SuppressWarnings( "ThisEscapedInObjectConstruction" )
    private final ObjectProperty<LocalDate> m_DayProperty = new SimpleObjectProperty<>( this, "day", LocalDate.now() );

    /**
     *  <p>{@summary The property that holds the duration of the time period
     *  between
     *  {@linkplain #getLowValue() low}
     *  and
     *  {@linkplain #getHighValue() high}.}</p>
     */
    @SuppressWarnings( "ThisEscapedInObjectConstruction" )
    private final ObjectProperty<Duration> m_DurationProperty = new SimpleObjectProperty<>( this, "duration" );

    /**
     *  <p>{@summary The property that holds the granularity for the
     *  {@code TimeSlider}.} The granularity determines the steps size for the
     *  time selection.
     */
    @SuppressWarnings( "ThisEscapedInObjectConstruction" )
    private final StyleableObjectProperty<Granularity> m_GranularityProperty = new SimpleStyleableObjectProperty<>( GRANULARITY, this, "granularity", Granularity.QUARTER_HOUR );

    /**
     *  <p>{@summary The high value property.} It represents the current
     *  position of the high value thumb, and is within the allowable range as
     *  specified by the
     *  {@link #minProperty() min}
     *  and
     *  {@link #maxProperty() max}
     *  properties.</p>
     */
    @SuppressWarnings( "ThisEscapedInObjectConstruction" )
    private final ObjectProperty<OffsetTime> m_HighValueProperty = new SimpleObjectProperty<>( this, "highValue" );

    /**
     *  <p>{@summary The low value property.} It represents the current
     *  position of the low value thumb, and is within the allowable range as
     *  specified by the
     *  {@link #minProperty() min}
     *  and
     *  {@link #maxProperty() max}
     *  properties.</p>
     */
    @SuppressWarnings( "ThisEscapedInObjectConstruction" )
    private final ObjectProperty<OffsetTime> m_LowValueProperty = new SimpleObjectProperty<>(this, "lowValue" );

    /**
     *  The property for the maximum value of this {@code TimeSlider}.
     */
    @SuppressWarnings( "AnonymousInnerClass" )
    private final ObjectProperty<OffsetTime> m_MaxProperty = new SimpleObjectProperty<>(this, "max" )
    {
        /**
         *  {@inheritDoc}
         */
        @Override
        protected final void invalidated()
        {
            final var min = getMin();
            if( Objects.isNull( min ) || get().isBefore( min ) ) setMin( get() );
        }   //  invalidated()
    };

    /**
     *  The property for the maximum value of this {@code TimeSlider}.
     */
    @SuppressWarnings( "AnonymousInnerClass" )
    private final ObjectProperty<OffsetTime> m_MinProperty = new SimpleObjectProperty<>( this, "min" )
    {
        /**
         *  {@inheritDoc}
         */
        @Override
        protected final void invalidated()
        {
            final var max = getMax();
            if( Objects.isNull( max ) || get().isAfter( max ) ) setMax( get() );
        }   //  invalidated()
    };

    /**
     *  The property for the flag that controls whether the thumbs will snap to
     *  the tick marks.
     */
    @SuppressWarnings( "ThisEscapedInObjectConstruction" )
    private final StyleableBooleanProperty m_SnapToTicksProperty = new SimpleStyleableBooleanProperty( SNAP_TO_TICKS, this, "snapToTicks", true );

    /**
     *  The property for the time zone that is used to calculate the offset for
     *  the times.
     */
    @SuppressWarnings( "ThisEscapedInObjectConstruction" )
    private final StyleableObjectProperty<ZoneId> m_TimeZoneProperty = new SimpleStyleableObjectProperty<>( TIME_ZONE,this, "timeZone", ZoneId.systemDefault() );

        /*------------------------*\
    ====** Static Initialisations **===========================================
        \*------------------------*/

        /*--------------*\
    ====** Constructors **=====================================================
        \*--------------*/
    /**
     *  Creates a new instance of {@code TimeSlider}.
     */
    public TimeSlider()
    {
        this( LocalDate.now().atStartOfDay( ZoneId.systemDefault() ).toOffsetDateTime().toOffsetTime(), LocalDate.now().atStartOfDay( ZoneId.systemDefault() ).plusDays( 1 ).minusSeconds( 1 ).toOffsetDateTime().toOffsetTime() );
    }   //  TimeSlider()

    /**
     *  Creates a new instance of {@code TimeSlider}.
     *
     *  @param  min The minimum time.
     *  @param  max The maximum time.
     *
     */
    public TimeSlider( final OffsetTime min, final OffsetTime max )
    {
        super();

        //---* Apply the arguments *-------------------------------------------
        setMax( max );
        setMin( min );

        //---* Set the defaults *----------------------------------------------
        m_HighValueProperty.set( max );
        m_LowValueProperty.set( min );

        //---* Create and apply the bindings *---------------------------------
        final var durationBinding = createObjectBinding( () -> Duration.between( getLowValue().atDate( getDay() ), getHighValue().atDate( getDay() ) ), m_LowValueProperty, m_HighValueProperty, m_DayProperty );
        m_DurationProperty.bind( durationBinding );

        setSkin( createDefaultSkin() );
    }   //  TimeSlider()

        /*---------*\
    ====** Methods **==========================================================
        \*---------*/
    /**
     *  {@inheritDoc}
     */
    @Override
    protected final Skin<?> createDefaultSkin()
    {
        final var retValue = new TimeSliderSkin( this, m_LowValueProperty, m_HighValueProperty );

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  createDefaultSkin()

    /**
     *  <p>{@summary Returns a reference to the property that holds the day for
     *  the times.}</p>
     *
     *  @return The property reference.
     */
    public final ObjectProperty<LocalDate> dayProperty() { return m_DayProperty; }

    /**
     *  <p>{@summary Returns a reference to the property that holds the
     *  duration of the time period between
     *  {@linkplain #getLowValue() low}
     *  and
     *  {@linkplain #getHighValue() high}.}</p>
     *
     *  @return The property reference.
     */
    public final ReadOnlyObjectProperty<Duration> durationProperty(){ return m_DurationProperty; }

    /**
     *  Returns the day for the times.
     *
     *  @return The day.
     */
    public final LocalDate getDay() { return m_DayProperty.get(); }

    /**
     *  Returns the duration for the time slider.
     *
     *  @return The duration.
     */
    public final Duration getDuration() { return m_DurationProperty.get(); }

    /**
     *  Returns the granularity for the time slider.
     *
     *  @return The granularity.
     */
    public final Granularity getGranularity() { return m_GranularityProperty.get(); }

    /**
     *  Returns the current high value for the time slider.
     *
     *  @return The high value.
     */
    public final OffsetTime getHighValue() { return m_HighValueProperty.get(); }

    /**
     *  Returns the current low value for the range slider.
     *
     *  @return The low value.
     */
    public final OffsetTime getLowValue() { return m_LowValueProperty.get(); }

    /**
     *  <p>{@summary Returns the maximum value for this
     *  {@code TimeSlider}.}</p>
     *  <p>23:59:59 is returned if the maximum value has never been set
     *  explicitly.</p>
     *
     *  @return The maximum value for this time slider.
     */
    public final OffsetTime getMax() { return m_MaxProperty.get(); }

    /**
     *  <p>{@summary Returns the minimum value for this
     *  {@code TimeSlider}.}</p>
     *  <p>00:00:00 is returned if the minimum has never been set
     *  explicitly.</p>
     *
     *  @return The minimum value for this time slider.
     */
    public final OffsetTime getMin() { return m_MinProperty.get(); }

    /**
     *  Returns the time zone that is used to calculate the offset for the
     *  times.
     *
     *  @return The time zone.
     */
    public final ZoneId getTimeZone() { return m_TimeZoneProperty.get(); }

    /**
     *  <p>{@summary Returns a reference to the property that holds the
     *  granularity for the {@code TimeSlider}.} The granularity determines the
     *  steps size for the time selection.
     *
     *  @return The property reference.
     */
    public final StyleableObjectProperty<Granularity> granularityProperty() { return m_GranularityProperty; }

    /**
     *  <p>{@summary Returns a reference to the property that holds the high
     *  value.}</p>
     *  <p>The high value property represents the current position of the high
     *  value thumb, and is within the allowable range as specified by the
     *  {@link #minProperty() min}
     *  and
     *  {@link #maxProperty() max}
     *  properties.</p>
     *
     *  @return The property reference.
     */
    public final ReadOnlyObjectProperty<OffsetTime> highValueProperty() { return m_HighValueProperty; }

    /**
     *  Returns the flag that controls whether the thumbs will snap to the tick
     *  marks.
     *
     *  @return {@code true} if the thumbs will snap to the tick marks,
     *      otherwise {@code false}.
     *
     *  @see #snapToTicksProperty()
     */
    public final boolean isSnapToTicks() { return m_SnapToTicksProperty.get(); }

    /**
     *  <p>{@summary Returns a reference to the property that holds the low
     *  value.}</p>
     *  <p>The low value property represents the current position of the low
     *  value thumb, and is within the allowable range as specified by the
     *  {@link #minProperty() min}
     *  and
     *  {@link #maxProperty() max}
     *  properties.</p>
     *
     *  @return The property reference.
     */
    public final ReadOnlyObjectProperty<OffsetTime> lowValueProperty() { return m_LowValueProperty; }

    /**
     *  <p>{@summary Returns a reference to the property that holds the maximum
     *  value for this {@code TimeSlider}.}</p>
     *
     *  @return The property reference.
     */
    public final ObjectProperty<OffsetTime> maxProperty() { return m_MaxProperty; }

    /**
     *  <p>{@summary Returns a reference to the property that holds the minimum
     *  value for this {@code TimeSlider}.}</p>
     *
     *  @return The property reference.
     */
    public final ObjectProperty<OffsetTime> minProperty() { return m_MinProperty; }

    /**
     *  Sets the granularity for this {@code TimeSlider}.
     *
     *  @param  granularity The granularity.
     */
    public final void setGranularity( final Granularity granularity )
    {
        m_GranularityProperty.set( mapFromNull( granularity, Granularity.QUARTER_HOUR ) );
    }   //  setGranularity()

    /**
     *  Sets the high value for this {@code TimeSlider}, which may or may not
     *  be clamped to be within the allowable range as specified by the
     *  {@link #minProperty() min}
     *  and
     *  {@link #maxProperty() max}
     *  properties.
     *
     *  @param  highValue   The value.
     */
    public final void setHighValue( final OffsetTime highValue )
    {
        if( nonNull( highValue ) )
        {
            final var skin = (TimeSliderSkin) getSkin();
            skin.setHighValue( highValue );
        }
    }   //  setHighValue()

    /**
     *  Sets the low value for this {@code TimeSlider}, which may or may not be
     *  clamped to be within the allowable range as specified by the
     *  {@link #minProperty() min}
     *  and
     *  {@link #maxProperty() max}
     *  properties.
     *
     *  @param  lowValue The value.
     */
    public final void setLowValue( final OffsetTime lowValue )
    {
        if( nonNull( lowValue ) )
        {
            final var skin = (TimeSliderSkin) getSkin();
            skin.setLowValue( lowValue );
        }
    }   //  setLowValue()

    /**
     *  Sets the maximum value for this {@code TimeSlider}.
     *
     *  @param  max The new value.
     */
    public final void setMax( final OffsetTime max ) { m_MaxProperty.set( max ); }

    /**
     *  Sets the minimum value for this  {@code TimeSlider}.
     *
     *  @param  min The new value
     */
    public final void setMin( final OffsetTime min ) { m_MinProperty.set( min ); }

    /**
     *  Sets the flag that controls whether the thumbs will snap to the tick
     *  marks.
     *
     *  @param  flag    {@code true} if the thumbs snaps to the tick marks,
     *      {@code false} if not.
     *
     *  @see #snapToTicksProperty()
     */
    public final void setSnapToTicks( final boolean flag ) { m_SnapToTicksProperty.set( flag ); }

    /**
     *  <p>{@summary Returns a reference to the property that holds the flag
     *  that indicates whether the
     *  {@linkplain #lowValueProperty() low value}/{@linkplain #highValueProperty() high value}
     *  thumbs of the {@code TimeSlider} should always be aligned with the
     *  tick marks.} This is honored even if the tick marks are not shown.</p>
     *
     *  @return The property reference.
     */
    public final BooleanProperty snapToTicksProperty() { return m_SnapToTicksProperty; }

    /**
     *  <p>{@summary Returns a reference to the property that holds the time
     *  zone that is used to determine the offset for the times.}</p>
     *
     *  @return The property reference.
     */
    public final ObjectProperty<ZoneId> timeZoneProperty() { return m_TimeZoneProperty; }
}
//  class TimeSlider

/*
 *  End of File
 */