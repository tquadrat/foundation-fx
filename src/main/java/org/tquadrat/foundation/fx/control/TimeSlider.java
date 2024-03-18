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
import static org.tquadrat.foundation.lang.Objects.requireNonNullArgument;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

import org.apiguardian.api.API;
import org.tquadrat.foundation.annotation.ClassVersion;
import org.tquadrat.foundation.annotation.UtilityClass;
import org.tquadrat.foundation.exception.PrivateConstructorForStaticClassCalledError;
import org.tquadrat.foundation.fx.control.skin.TimeSliderSkin;
import org.tquadrat.foundation.fx.css.TimeZoneConverter;
import org.tquadrat.foundation.fx.internal.FoundationFXControl;
import org.tquadrat.foundation.lang.Objects;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.Property;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
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
 *  @version $Id: TimeSlider.java 1121 2024-03-16 16:51:23Z tquadrat $
 *  @since 0.4.6
 *
 *  @UMLGraph.link
 */
@SuppressWarnings( {"ClassWithTooManyMethods", "ClassWithTooManyConstructors", "ClassWithTooManyFields"} )
@ClassVersion( sourceVersion = "$Id: TimeSlider.java 1121 2024-03-16 16:51:23Z tquadrat $" )
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
     *  @version $Id: TimeSlider.java 1121 2024-03-16 16:51:23Z tquadrat $
     *  @since 0.4.6
     */
    @ClassVersion( sourceVersion = "$Id: TimeSlider.java 1121 2024-03-16 16:51:23Z tquadrat $" )
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
     *  @version $Id: TimeSlider.java 1121 2024-03-16 16:51:23Z tquadrat $
     *  @since 0.4.6
     *
     *  @UMLGraph.link
     */
    @ClassVersion( sourceVersion = "$Id: TimeSlider.java 1121 2024-03-16 16:51:23Z tquadrat $" )
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
     *  @version $Id: TimeSlider.java 1121 2024-03-16 16:51:23Z tquadrat $
     *  @since 0.4.6
     */
    @SuppressWarnings( {"ProtectedInnerClass", "InnerClassTooDeeplyNested", "AnonymousInnerClass"} )
    @UtilityClass
    @ClassVersion( sourceVersion = "$Id: TimeSlider.java 1121 2024-03-16 16:51:23Z tquadrat $" )
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
     *  <p>{@summary The property for the day for that the times should be set.}
     *  The value for this property may not be {@code null}.</p>
     */
    @SuppressWarnings( "AnonymousInnerClass" )
    private final ObjectProperty<LocalDate> m_DayProperty = new SimpleObjectProperty<>( this, "day", LocalDate.now() )
    {
        /**
         *  {@inheritDoc}
         */
        @Override
        public final void set( final LocalDate newValue )
        {
            super.set( requireNonNullArgument( newValue, "newValue" ) );
        }   //  set()
    };

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
     *  {@link #minDisplayProperty() min}
     *  and
     *  {@link #maxDisplayProperty() max}
     *  properties.</p>
     */
    @SuppressWarnings( "ThisEscapedInObjectConstruction" )
    private final ObjectProperty<OffsetTime> m_HighValueProperty = new SimpleObjectProperty<>( this, "highValue" );

    /**
     *  <p>{@summary The low value property.} It represents the current
     *  position of the low value thumb, and is within the allowable range as
     *  specified by the
     *  {@link #minDisplayProperty() min}
     *  and
     *  {@link #maxDisplayProperty() max}
     *  properties.</p>
     */
    @SuppressWarnings( "ThisEscapedInObjectConstruction" )
    private final ObjectProperty<OffsetTime> m_LowValueProperty = new SimpleObjectProperty<>(this, "lowValue" );

    /**
     *  The property for the maximum displayed value of this
     *  {@code TimeSlider}.
     */
    @SuppressWarnings( "AnonymousInnerClass" )
    private final ObjectProperty<LocalTime> m_MaxDisplayProperty = new SimpleObjectProperty<>(this, "maxDisplay" )
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
     *  <p>{@summary The property for the maximum value of this
     *  {@code TimeSlider}.}</p>
     *  <p>This property is fixed bound to the properties
     *  {@link #maxDisplayProperty()},
     *  {@link #dayProperty()}
     *  {@link #timeZoneProperty()}.</p>
     */
    @SuppressWarnings( "ThisEscapedInObjectConstruction" )
    private final ObjectProperty<ZonedDateTime> m_MaxValueProperty = new SimpleObjectProperty<>(this, "maxValue" );

    /**
     *  The binding that updates the internal
     *  {@link #m_MaxValueProperty}.
     */
    private final ObjectBinding<ZonedDateTime> m_MaxValueBinding;

    /**
     *  The property for the minimum displayed value of this
     *  {@code TimeSlider}.
     */
    @SuppressWarnings( "AnonymousInnerClass" )
    private final ObjectProperty<LocalTime> m_MinDisplayProperty = new SimpleObjectProperty<>( this, "minDisplay" )
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
     *  <p>{@summary The property for the minimum value of this
     *  {@code TimeSlider}.}</p>
     *  <p>This property is fixed bound to the properties
     *  {@link #minDisplayProperty()},
     *  {@link #dayProperty()}
     *  {@link #timeZoneProperty()}.</p>
     */
    @SuppressWarnings( "ThisEscapedInObjectConstruction" )
    private final ObjectProperty<ZonedDateTime> m_MinValueProperty = new SimpleObjectProperty<>( this, "minValue" );

    /**
     *  The binding that updates the internal
     *  {@link #m_MinValueProperty}.
     */
    private final ObjectBinding<ZonedDateTime> m_MinValueBinding;

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
     *  <p>{@summary Creates a new instance of {@code TimeSlider} for the
     *  current day and the default time zone.} The time range will be set from
     *  00:00 to 23:59.</p>
     *  <p>Once created, the time zone  for the {@code TimeSlider} cannot be
     *  modified.</p>
     */
    public TimeSlider()
    {
        this( LocalDate.now() );
    }   //  TimeSlider()

    /**
     *  <p>{@summary Creates a new instance of {@code TimeSlider} for the given
     *  day and the default time zone.} The time range will be set from 00:00
     *  to 23:59.</p>
     *  <p>Once created, the time zone  for the {@code TimeSlider} cannot be
     *  modified.</p>
     *
     *  @param  day The day for the time slider.
     */
    public TimeSlider( final LocalDate day )
    {
        this( requireNonNullArgument( day, "day" ), ZoneId.systemDefault() );
    }   //  TimeSlider()

    /**
     *  <p>{@summary Creates a new instance of {@code TimeSlider} for the given
     *  day and the given time zone.} The time range will be set from 00:00
     *  to 23:59.</p>
     *  <p>Once created, the time zone  for the {@code TimeSlider} cannot be
     *  modified.</p>
     *
     *  @param  day The day for the time slider.
     *  @param  timeZone    The time zone.
     */
    public TimeSlider( final LocalDate day, final ZoneId timeZone )
    {
        this( requireNonNullArgument( day, "day" ), requireNonNullArgument( timeZone, "timeZone" ), LocalTime.MIN, LocalTime.MAX.withSecond( 0 ).withNano( 0 ) );
    }   //  TimeSlider()

    /**
     *  <p>{@summary Creates a new instance of {@code TimeSlider} for the
     *  current day and the default time zone.}</p>
     *  <p>Once created, the time zone  for the {@code TimeSlider} cannot be
     *  modified.</p>
     *
     *  @param  min The minimum displayed time for the slider.
     *  @param  max The maximum displayed time for the slider.
     *
     */
    public TimeSlider( final LocalTime min, final LocalTime max )
    {
        this( LocalDate.now(), ZoneId.systemDefault(), requireNonNullArgument( min, "min" ), requireNonNullArgument( max, "max" ) );
    }   //  TimeSlider()

    /**
     *  <p>{@summary Creates a new instance of {@code TimeSlider} for the
     *  given day and the default time zone.}</p>
     *  <p>Once created, the time zone  for the {@code TimeSlider} cannot be
     *  modified.</p>
     *
     *  @param  day The day for the time slider.
     *  @param  min The minimum displayed time for the slider.
     *  @param  max The maximum displayed time for the slider.
     *
     */
    public TimeSlider( final LocalDate day, final LocalTime min, final LocalTime max )
    {
        this( requireNonNullArgument( day, "day" ), ZoneId.systemDefault(), requireNonNullArgument( min, "min" ), requireNonNullArgument( max, "max" ) );
    }   //  TimeSlider()

    /**
     *  <p>{@summary Creates a new instance of {@code TimeSlider} for the
     *  given day and the given time zone.}</p>
     *  <p>Once created, the time zone  for the {@code TimeSlider} cannot be
     *  modified.</p>
     *
     *  @param  day The day for the time slider.
     *  @param  timeZone    The time zone.
     *  @param  min The minimum displayed time for the slider.
     *  @param  max The maximum displayed time for the slider.
     *
     */
    public TimeSlider( final LocalDate day, final ZoneId timeZone, final LocalTime min, final LocalTime max )
    {
        super();

        //---* Apply the arguments *-------------------------------------------
        setDay( day );
        m_TimeZoneProperty.set( requireNonNullArgument( timeZone, "timeZone" ) );
        setMax( requireNonNullArgument( max, "max" ) );
        setMin( requireNonNullArgument( min, "min" ) );

        m_MaxValueProperty.set( day.atTime( max ).atZone( timeZone ) );
        m_MinValueProperty.set( day.atTime( min ).atZone( timeZone ) );

        //---* Set the defaults *----------------------------------------------
        setHighValue( m_MaxValueProperty.get().toOffsetDateTime().toOffsetTime() );
        setLowValue( m_MinValueProperty.get().toOffsetDateTime().toOffsetTime() );

        //---* Create and apply the bindings *---------------------------------
        m_MinValueBinding = createObjectBinding( () -> getDay().atTime( getMin() ).atZone( getTimeZone() ), m_MinDisplayProperty );
        m_MinValueProperty.bind( m_MinValueBinding );

        m_MaxValueBinding = createObjectBinding( () -> getDay().atTime( getMax() ).atZone( getTimeZone() ), m_MaxDisplayProperty );
        m_MaxValueProperty.bind( m_MaxValueBinding );

        m_DayProperty.addListener( this::dayChangeListener );

        final var durationBinding = createObjectBinding( () -> Duration.between( getLowValue().atDate( getDay() ), getHighValue().atDate( getDay() ) ), m_LowValueProperty, m_HighValueProperty );
        m_DurationProperty.bind( durationBinding );

        //---* Apply the skin *------------------------------------------------
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
        final var retValue = new TimeSliderSkin( this );

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  createDefaultSkin()

    /**
     *  The implementation of
     *  {@link ChangeListener}
     *  that responds on changes to the
     *  {@linkplain #dayProperty() day property}.
     *
     *  @param  observable    The observable.
     *  @param  oldValue    The old value.
     *  @param  newValue    The new value.
     */
    @SuppressWarnings( "TypeParameterExtendsFinalClass" )
    private final void dayChangeListener( final ObservableValue<? extends LocalDate> observable, final LocalDate oldValue, final LocalDate newValue )
    {
        if( !Objects.equals( oldValue, newValue ) )
        {
            final var lowValue = m_LowValueProperty.get();
            final var highValue = m_HighValueProperty.get();

            if( oldValue.isBefore( newValue ) )
            {
                m_MaxValueBinding.invalidate();
                m_MaxValueProperty.get();
                m_MinValueBinding.invalidate();
                m_MinValueProperty.get();
            }
            else
            {
                m_MinValueBinding.invalidate();
                m_MinValueProperty.get();
                m_MaxValueBinding.invalidate();
                m_MaxValueProperty.get();
            }

            setHighValue( highValue );
            setLowValue( lowValue );
        }
    }   //  dayChangeListener()

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
     *  <p>{@summary Returns the maximum displayed value for this
     *  {@code TimeSlider}.}</p>
     *  <p>23:59:59 is returned if the maximum value has never been set
     *  explicitly.</p>
     *
     *  @return The maximum value for this time slider.
     */
    public final LocalTime getMax() { return m_MaxDisplayProperty.get(); }

    /**
     *  <p>{@summary Returns the minimum displayed value for this
     *  {@code TimeSlider}.}</p>
     *  <p>00:00:00 is returned if the minimum has never been set
     *  explicitly.</p>
     *
     *  @return The minimum value for this time slider.
     */
    public final LocalTime getMin() { return m_MinDisplayProperty.get(); }

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
     *  {@link #minDisplayProperty() min}
     *  and
     *  {@link #maxDisplayProperty() max}
     *  properties.</p>
     *
     *  @return The property reference.
     */
    public final ObjectProperty<OffsetTime> highValueProperty() { return m_HighValueProperty; }

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
     *  {@link #minDisplayProperty() min}
     *  and
     *  {@link #maxDisplayProperty() max}
     *  properties.</p>
     *
     *  @return The property reference.
     */
    public final ObjectProperty<OffsetTime> lowValueProperty() { return m_LowValueProperty; }

    /**
     *  <p>{@summary Returns a reference to the property that holds the maximum
     *  displayed value for this {@code TimeSlider}.}</p>
     *
     *  @return The property reference.
     */
    public final ObjectProperty<LocalTime> maxDisplayProperty() { return m_MaxDisplayProperty; }

    /**
     *  <p>{@summary Returns a reference to the property that holds the maximum
     *  value for this {@code TimeSlider}.}</p>
     *
     *  @return The property reference.
     */
    public final ReadOnlyObjectProperty<ZonedDateTime> maxValueProperty() { return m_MaxValueProperty; }

    /**
     *  <p>{@summary Returns a reference to the property that holds the minimum
     *  value for this {@code TimeSlider}.}</p>
     *
     *  @return The property reference.
     */
    public final Property<LocalTime> minDisplayProperty() { return m_MinDisplayProperty; }

    /**
     *  <p>{@summary Returns a reference to the property that holds the minimum
     *  for this {@code TimeSlider}.}</p>
     *
     *  @return The property reference.
     */
    public final ReadOnlyObjectProperty<ZonedDateTime> minValueProperty() { return m_MinValueProperty; }

    /**
     *  Sets the day for this {@code TimeSlider}.
     *
     *  @param  day The day.
     */
    public final void setDay( final LocalDate day ) { m_DayProperty.set( requireNonNullArgument( day, "day" ) ); }

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
     *  {@link #minDisplayProperty() min}
     *  and
     *  {@link #maxDisplayProperty() max}
     *  properties.
     *
     *  @param  highValue   The value.
     */
    public final void setHighValue( final OffsetTime highValue ) { m_HighValueProperty.set( highValue ); }

    /**
     *  Sets the high value for this {@code TimeSlider}, which may or may not
     *  be clamped to be within the allowable range as specified by the
     *  {@link #minDisplayProperty() min}
     *  and
     *  {@link #maxDisplayProperty() max}
     *  properties.
     *
     *  @param  highValue   The value.
     */
    public final void setHighValue( final LocalTime highValue )
    {
        final var offsetTime = getDay()
            .atTime( requireNonNullArgument( highValue, "highValue" ) )
            .atZone( getTimeZone() )
            .toOffsetDateTime()
            .toOffsetTime();
        setHighValue( offsetTime );
    }   //  setHighValue()

    /**
     *  Sets the low value for this {@code TimeSlider}, which may or may not be
     *  clamped to be within the allowable range as specified by the
     *  {@link #minDisplayProperty() min}
     *  and
     *  {@link #maxDisplayProperty() max}
     *  properties.
     *
     *  @param  lowValue The value.
     */
    public final void setLowValue( final OffsetTime lowValue ) { m_LowValueProperty.set( lowValue ); }

    /**
     *  Sets the low value for this {@code TimeSlider}, which may or may not be
     *  clamped to be within the allowable range as specified by the
     *  {@link #minDisplayProperty() min}
     *  and
     *  {@link #maxDisplayProperty() max}
     *  properties.
     *
     *  @param  lowValue The value.
     */
    public final void setLowValue( final LocalTime lowValue )
    {
        final var offsetTime = getDay()
            .atTime( requireNonNullArgument( lowValue, "lowValue" ) )
            .atZone( getTimeZone() )
            .toOffsetDateTime()
            .toOffsetTime();
        setLowValue( offsetTime );
    }   //  setLowValue()

    /**
     *  Sets the maximum displayed value for this {@code TimeSlider}.
     *
     *  @param  max The new value.
     */
    public final void setMax( final LocalTime max ) { m_MaxDisplayProperty.set( max ); }

    /**
     *  Sets the minimum displayed value for this  {@code TimeSlider}.
     *
     *  @param  min The new value
     */
    public final void setMin( final LocalTime min ) { m_MinDisplayProperty.set( min ); }

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
    public final ReadOnlyObjectProperty<ZoneId> timeZoneProperty() { return m_TimeZoneProperty; }
}
//  class TimeSlider

/*
 *  End of File
 */