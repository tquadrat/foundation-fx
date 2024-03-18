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
import static java.lang.Double.max;
import static java.lang.Double.min;
import static javafx.geometry.Orientation.HORIZONTAL;
import static javafx.geometry.Orientation.VERTICAL;
import static org.apiguardian.api.API.Status.STABLE;
import static org.tquadrat.foundation.fx.FXUtils.clamp;
import static org.tquadrat.foundation.fx.FXUtils.nearest;
import static org.tquadrat.foundation.fx.control.RangeSlider.StyleableProperties.BLOCK_INCREMENT;
import static org.tquadrat.foundation.fx.control.RangeSlider.StyleableProperties.ORIENTATION;
import static org.tquadrat.foundation.fx.control.RangeSlider.StyleableProperties.SHOW_TICK_LABELS;
import static org.tquadrat.foundation.fx.control.RangeSlider.StyleableProperties.SHOW_TICK_MARKS;
import static org.tquadrat.foundation.fx.control.RangeSlider.StyleableProperties.SNAP_TO_TICKS;
import static org.tquadrat.foundation.lang.Objects.requireValidDoubleArgument;

import java.util.List;

import org.apiguardian.api.API;
import org.tquadrat.foundation.annotation.ClassVersion;
import org.tquadrat.foundation.annotation.UtilityClass;
import org.tquadrat.foundation.exception.PrivateConstructorForStaticClassCalledError;
import org.tquadrat.foundation.fx.control.skin.RangeSliderSkin;
import org.tquadrat.foundation.fx.internal.FoundationFXControl;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.css.CssMetaData;
import javafx.css.PseudoClass;
import javafx.css.SimpleStyleableBooleanProperty;
import javafx.css.SimpleStyleableDoubleProperty;
import javafx.css.SimpleStyleableIntegerProperty;
import javafx.css.SimpleStyleableObjectProperty;
import javafx.css.StyleOrigin;
import javafx.css.Styleable;
import javafx.css.StyleableBooleanProperty;
import javafx.css.StyleableDoubleProperty;
import javafx.css.StyleableIntegerProperty;
import javafx.css.StyleableObjectProperty;
import javafx.css.StyleableProperty;
import javafx.css.converter.BooleanConverter;
import javafx.css.converter.EnumConverter;
import javafx.css.converter.SizeConverter;
import javafx.geometry.Orientation;
import javafx.scene.control.Skin;
import javafx.util.StringConverter;

/**
 *  <p>{@summary The {@code RangeSlider} control is simply a JavaFX
 *  {@link javafx.scene.control.Slider}
 *  control with support for two 'thumbs', rather than one.} A thumb is the
 *  non-technical name for the draggable area inside the
 *  {@code Slider}/{@code RangeSlider} that allows for a value to be set.</p>
 *  <p>Because the RangeSlider has two thumbs, it also has a few additional
 *  rules and user interactions:</p>
 *  <ol>
 *    <li>The 'lower value' thumb can not move past the 'higher value'
 *      thumb.</li>
 *    <li>Whereas the
 *      {@link javafx.scene.control.Slider}
 *      control only has one
 *      {@link javafx.scene.control.Slider#valueProperty() value}
 *      property, the {@code RangeSlider} has a
 *      {@link #lowValueProperty() low value}
 *      and a
 *      {@link #highValueProperty() high value}
 *      property, not surprisingly represented by the 'low value' and 'high
 *      value' thumbs.</li>
 *    <li>The area between the low and high values represents the allowable
 *      range. For example, if the low value is 2 and the high value is 8, then
 *      the allowable range is between 2 and 8.</li>
 *    <li>The allowable range area is rendered differently. This area is able to
 *      be dragged with mouse/touch input to allow for the entire range to be
 *      modified. For example, following on from the previous example of the
 *      allowable range being between 2 and 8, if the user drags the range bar
 *      to the right, the low value will adjust to 3, and the high value 9, and
 *      so on until the user stops adjusting.</li>
 *   </ol>
 *
 *  <h2>Code Samples</h2>
 *  <p>Instantiating a {@code RangeSlider} is simple. The first decision is to
 *  decide whether a horizontal or a vertical track is more appropriate. By
 *  default {@code RangeSlider} instances are horizontal, but this can be
 *  changed by setting the
 *  {@link #orientationProperty() orientation}
 *  property.</p>
 *  <p>Once the orientation is determined, the next most important decision is
 *  to determine what the
 *  {@link #minProperty() min}/{@link #maxProperty() max}
 *  and default
 *  {@link #lowValueProperty() low}/{@link #highValueProperty() high}
 *  values are. The {@code min}/{@code max} values represent the smallest and
 *  largest legal values for the thumbs to be set to, whereas the
 *  {@code low}/{@code high} values represent where the thumbs are currently,
 *  within the bounds of the {@code min}/{@code max} values. Because all four
 *  values are required in all circumstances, they are all required parameters
 *  to instantiate a {@code RangeSlider}: the
 *  {@linkplain #RangeSlider(double,double,double,double) constructor}
 *  takes four doubles, representing {@code min}, {@code max}, {@code lowValue}
 *  and {@code highValue} (in that order).</p>
 *  <p>For example, here is a simple horizontal {@code RangeSlider} that has a
 *  minimum value of 0, a maximum value of 100, a low value of 10 and a high
 *  value of 90:</p>
 *  <pre>{@code final RangeSlider hSlider = new RangeSlider( 0, 100, 10, 90 );}</pre>
 *  <p>To create a vertical slider, simply do the following:</p>
 *  <pre>{@code  final RangeSlider vSlider = new RangeSlider( 0, 200, 30, 150 );
 *  vSlider.setOrientation( Orientation.VERTICAL );}</pre>
 *  <p>This code creates a vertical {@code RangeSlider} with a {@code min}
 *  value of 0, a {@code max} value of 200, a {@code low} value of 30, and a
 *  {@code high} value of 150.</p>
 *
 *  @see javafx.scene.control.Slider
 *
 *  @extauthor Thomas Thrien - thomas.thrien@tquadrat.org
 *  @inspired  {@href https://controlsfx.github.io/ ControlsFX Project}
 *  @version $Id: RangeSlider.java 1121 2024-03-16 16:51:23Z tquadrat $
 *  @since 0.4.6
 *
 *  @UMLGraph.link
 */
@SuppressWarnings( {"ClassWithTooManyFields", "ClassWithTooManyMethods"} )
@ClassVersion( sourceVersion = "$Id: RangeSlider.java 1121 2024-03-16 16:51:23Z tquadrat $" )
@API( status = STABLE, since = "0.4.6" )
public final class RangeSlider extends FoundationFXControl
{
        /*---------------*\
    ====** Inner Classes **====================================================
        \*---------------*/
    /**
     *  The styleable properties for
     *  {@link RangeSlider}.
     *
     *  @extauthor Thomas Thrien - thomas.thrien@tquadrat.org
     *  @inspired  {@href https://controlsfx.github.io/ ControlsFX Project}
     *  @version $Id: RangeSlider.java 1121 2024-03-16 16:51:23Z tquadrat $
     *  @since 0.4.6
     */
    @SuppressWarnings( {"ProtectedInnerClass", "InnerClassTooDeeplyNested", "AnonymousInnerClass"} )
    @UtilityClass
    @ClassVersion( sourceVersion = "$Id: RangeSlider.java 1121 2024-03-16 16:51:23Z tquadrat $" )
    @API( status = STABLE, since = "0.4.6" )
    protected static final class StyleableProperties
    {
            /*------------------------*\
        ====** Static Initialisations **=======================================
            \*------------------------*/
        /**
         *  The CSS attribute for the {@code BLOCK_INCREMENT}.
         *
         *  @see #blockIncrementProperty()
         */
        public static final CssMetaData<RangeSlider,Number> BLOCK_INCREMENT = new CssMetaData<>( "-fx-block-increment", SizeConverter.getInstance(), 10.0 )
        {
            /**
             *  {@inheritDoc}
             */
            @Override
            public final StyleableProperty<Number> getStyleableProperty( final RangeSlider styleable ) { return styleable.m_BlockIncrementProperty; }

            /**
             *  {@inheritDoc}
             */
            @Override
            public final boolean isSettable( final RangeSlider styleable ) { return !styleable.m_BlockIncrementProperty.isBound(); }
        };

        /**
         *  The CSS attribute for the {@code MAJOR_TICK_UNIT}.
         *
         *  @see #majorTickUnitProperty()
         */
        public static final CssMetaData<RangeSlider,Number> MAJOR_TICK_UNIT = new CssMetaData<>( "-fx-major-tick-unit", SizeConverter.getInstance(), 25.0 )
        {
            /**
             *  {@inheritDoc}
             */
            @Override
            public StyleableProperty<Number> getStyleableProperty( final RangeSlider styleable ) { return styleable.m_MajorTickUnitProperty; }

            /**
             *  {@inheritDoc}
             */
            @Override
            public final boolean isSettable( final RangeSlider styleable ) { return !styleable.m_MajorTickUnitProperty.isBound();
            }
        };

        /**
         *  The CSS attribute for the {@code MAJOR_TICK_UNIT}.
         *
         *  @see #minorTickCountProperty()
         */
        @SuppressWarnings( "OverlyComplexAnonymousInnerClass" )
        public static final CssMetaData<RangeSlider,Number> MINOR_TICK_COUNT = new CssMetaData<>( "-fx-minor-tick-count", SizeConverter.getInstance(), 3 )
        {
            /**
             *  {@inheritDoc}
             */
            @Override
            public final StyleableProperty<Number> getStyleableProperty( final RangeSlider styleable ) { return styleable.m_MinorTickCountProperty; }

            /**
             *  {@inheritDoc}
             */
            @Override
            public final boolean isSettable( final RangeSlider styleable )
            {
                return styleable.m_MinorTickCountProperty == null || !styleable.m_MinorTickCountProperty.isBound();
            }

            /**
             *  {@inheritDoc}
             */
            @SuppressWarnings( "deprecation" )
            @Override
            public final void set( final RangeSlider styleable, final Number value, final StyleOrigin origin )
            {
                super.set( styleable, value.intValue(), origin );
            }   //  set()
        };

        /**
         *  The CSS attribute for the {@code ORIENTATION}.
         *
         *  @see #orientationProperty()
         */
        public static final CssMetaData<RangeSlider,Orientation> ORIENTATION = new CssMetaData<>( "-fx-orientation", new EnumConverter<>( Orientation.class ), HORIZONTAL )
        {
            /**
             *  {@inheritDoc}
             */
            @Override
            public final Orientation getInitialValue( final RangeSlider styleable )
            {
                //---* A vertical RangeSlider should remain vertical *---------
                return styleable.getOrientation();
            }   //  getInitialValue()

            /**
             *  {@inheritDoc}
             */
            @Override
            public final StyleableProperty<Orientation> getStyleableProperty( final RangeSlider styleable )
            {
                return styleable.m_OrientationProperty;
            }   //  getStyleableProperty()

            /**
             *  {@inheritDoc}
             */
            @Override
            public final boolean isSettable( final RangeSlider styleable ) { return !styleable.m_OrientationProperty.isBound(); }
        };

        /**
         *  The CSS attribute for {@code SHOW_TICK_LABELS}.
         *
         *  @see #showTickLabelsProperty()
         */
        public static final CssMetaData<RangeSlider,Boolean> SHOW_TICK_LABELS = new CssMetaData<>( "-fx-show-tick-labels", BooleanConverter.getInstance(), FALSE )
        {
            /**
             *  {@inheritDoc}
             */
            @Override
            public final StyleableProperty<Boolean> getStyleableProperty( final RangeSlider styleable ) { return styleable.m_ShowTickLabelsProperty; }

            /**
             *  {@inheritDoc}
             */
            @Override
            public final boolean isSettable( final RangeSlider styleable ) { return !styleable.m_ShowTickLabelsProperty.isBound(); }
        };

        /**
         *  The CSS attribute for {@code SHOW_TICK_MARKS}.
         *
         *  @see #showTickMarksProperty()
         */
        public static final CssMetaData<RangeSlider,Boolean> SHOW_TICK_MARKS = new CssMetaData<>( "-fx-show-tick-marks", BooleanConverter.getInstance(), FALSE )
        {
            /**
             *  {@inheritDoc}
             */
            @Override
            public final StyleableProperty<Boolean> getStyleableProperty( final RangeSlider styleable ) { return styleable.m_ShowTickMarksProperty; }

            /**
             *  {@inheritDoc}
             */
            @Override
            public final boolean isSettable( final RangeSlider styleable ) { return !styleable.m_ShowTickMarksProperty.isBound(); }
        };

        /**
         *  The CSS attribute for {@code SNAP_TO_TICKS}.
         *
         *  @see #snapToTicksProperty()
         */
        public static final CssMetaData<RangeSlider,Boolean> SNAP_TO_TICKS = new CssMetaData<>( "-fx-snap-to-ticks", BooleanConverter.getInstance(), FALSE )
        {
            /**
             *  {@inheritDoc}
             */
            @Override
            public final StyleableProperty<Boolean> getStyleableProperty( final RangeSlider styleable ) { return styleable.m_SnapToTicksProperty; }

            /**
             *  {@inheritDoc}
             */
            @Override
            public final boolean isSettable( final RangeSlider styleable ) { return !styleable.m_SnapToTicksProperty.isBound(); }
        };

        /**
         *  The CSS attributes for
         *  {@link RangeSlider}.
         */
        @SuppressWarnings( "StaticCollection" )
        public static final List<CssMetaData<? extends Styleable, ?>> STYLEABLES = List.of( BLOCK_INCREMENT, MAJOR_TICK_UNIT, MINOR_TICK_COUNT, ORIENTATION, SHOW_TICK_LABELS, SHOW_TICK_MARKS, SNAP_TO_TICKS );

            /*--------------*\
        ====** Constructors **=================================================
            \*--------------*/
        /**
         *  No instance allowed for this class!
         */
        private StyleableProperties() { throw new PrivateConstructorForStaticClassCalledError( StyleableProperties.class ); }
    }
    //  class StyleableProperties

        /*-----------*\
    ====** Constants **========================================================
        \*-----------*/
    /**
     *  The default style class for {@code RangeSlider} instances: {@value}.
     */
    public static final String DEFAULT_STYLE_CLASS = "range-slider"; //$NON-NLS-1$

        /*------------*\
    ====** Attributes **=======================================================
        \*------------*/
    /**
     *  The property for the amount by which to adjust the slider if the track
     *  of the slider is clicked.
     */
    @SuppressWarnings( "ThisEscapedInObjectConstruction" )
    private final StyleableDoubleProperty m_BlockIncrementProperty = new SimpleStyleableDoubleProperty( BLOCK_INCREMENT, this, "blockIncrement", 10.0 );

    /**
     *  <p>{@summary The property that indicates a change to the high value of
     *  this {@code RangeSlider}.}</p>
     */
    @SuppressWarnings( "ThisEscapedInObjectConstruction" )
    private final BooleanProperty m_HighValueChangingProperty = new SimpleBooleanProperty(this, "highValueChanging", false);

    /**
     *  <p>{@summary The high value property.} It represents the current
     *  position of the high value thumb, and is within the allowable range as
     *  specified by the
     *  {@link #minProperty() min}
     *  and
     *  {@link #maxProperty() max}
     *  properties. By default this value is 100.</p>
     */
    @SuppressWarnings( "AnonymousInnerClass" )
    private final DoubleProperty m_HighValueProperty = new SimpleDoubleProperty( this, "highValue", 100.0D )
    {
        /**
         *  {@inheritDoc}
         */
        @Override
        protected final void invalidated() { adjustHighValues(); }
    };

    /**
     * <p>{@summary The property that indicates a change to the low value of
     * this {@code RangeSlider}.}</p>
     */
    @SuppressWarnings( "ThisEscapedInObjectConstruction" )
    private final BooleanProperty m_LowValueChangingProperty = new SimpleBooleanProperty( this, "lowValueChanging", false );

    /**
     *  <p>{@summary The low value property.} It represents the current
     *  position of the low value thumb, and is within the allowable range as
     *  specified by the
     *  {@link #minProperty() min}
     *  and
     *  {@link #maxProperty() max}
     *  properties. By default this value is 0.</p>
     */
    @SuppressWarnings( "AnonymousInnerClass" )
    private final DoubleProperty m_LowValueProperty = new SimpleDoubleProperty(this, "lowValue", 0.0D)
    {
        /**
         *  {@inheritDoc}
         */
        @Override
        protected final void invalidated() { adjustLowValues(); }
    };

    /**
     *  The property for the unit distance between major tick marks.
     */
    @SuppressWarnings( {"AnonymousInnerClass"} )
    private final StyleableDoubleProperty m_MajorTickUnitProperty = new SimpleStyleableDoubleProperty( StyleableProperties.MAJOR_TICK_UNIT, this, "majorTickUnit", 25.0 )
    {
        /**
         *  {@inheritDoc}
         */
        @Override
        public final void invalidated()
        {
            if( get() <= 0 )
            {
                throw new IllegalArgumentException( "MajorTickUnit cannot be less than or equal to 0." );
            }
        }   //  invalidated()
    };

    /**
     *  The property for the maximum value of this {@code RangeSlider}.
     */
    @SuppressWarnings( "AnonymousInnerClass" )
    private final DoubleProperty m_MaxProperty = new SimpleDoubleProperty(this, "max", 100.0D )
    {
        /**
         *  {@inheritDoc}
         */
        @Override
        protected final void invalidated()
        {
            if( get() < getMin() && !m_MinProperty.isBound() ) setMin( get() );
            adjustValues();
        }   //  invalidated()
    };

    /**
     *  The property for the number of minor ticks to place between any two
     *  major ticks.
     */
    @SuppressWarnings( "ThisEscapedInObjectConstruction" )
    private final StyleableIntegerProperty m_MinorTickCountProperty = new SimpleStyleableIntegerProperty( RangeSlider.StyleableProperties.MINOR_TICK_COUNT, this, "minorTickCount", 3 );

    /**
     *  The property for the maximum value of this {@code RangeSlider}.
     */
    @SuppressWarnings( "AnonymousInnerClass" )
    private final DoubleProperty m_MinProperty = new SimpleDoubleProperty( this, "min", 0.0D )
    {
        /**
         *  {@inheritDoc}
         */
        @Override
        protected final void invalidated()
        {
            if( get() > getMax() && !m_MaxProperty.isBound() ) setMax( get() );
            adjustValues();
        }   //  invalidated()
    };

    /**
     *  The property that holds the orientation of this {@code RangeSlider}.
     */
    @SuppressWarnings( {"AnonymousInnerClass"} )
    private final StyleableObjectProperty<Orientation> m_OrientationProperty = new SimpleStyleableObjectProperty<>( ORIENTATION, this, "orientation", HORIZONTAL )
    {
        /**
         * {@inheritDoc}
         */
        @Override
        protected final void invalidated()
        {
            final var vertical = (get() == VERTICAL);
            pseudoClassStateChanged( VERTICAL_PSEUDOCLASS_STATE, vertical );
            pseudoClassStateChanged( HORIZONTAL_PSEUDOCLASS_STATE, !vertical );
        }   //  invalidated()
    };

    /**
     *  The property for the flag that indicates whether the labels for the
     *  tick marks are shown or not.
     */
    @SuppressWarnings( "ThisEscapedInObjectConstruction" )
    private final StyleableBooleanProperty m_ShowTickLabelsProperty = new SimpleStyleableBooleanProperty( SHOW_TICK_LABELS, this, "showTickLabels", false );

    /**
     *  The property that holds the flag that specifies whether the
     *  {@link Skin}
     *  implementation should show tick marks for this {@code RangeSlider}.
     */
    @SuppressWarnings( "ThisEscapedInObjectConstruction" )
    private final StyleableBooleanProperty m_ShowTickMarksProperty = new SimpleStyleableBooleanProperty( SHOW_TICK_MARKS, this, "showTickMarks", false );

    /**
     *  The property for the flag that controls whether the thumbs will snap to
     *  the tick marks.
     */
    @SuppressWarnings( "ThisEscapedInObjectConstruction" )
    private final StyleableBooleanProperty m_SnapToTicksProperty = new SimpleStyleableBooleanProperty( SNAP_TO_TICKS, this, "snapToTicks", false );

    /**
     *  The property for the tick label formatter.
     */
    @SuppressWarnings( "ThisEscapedInObjectConstruction" )
    private final ObjectProperty<StringConverter<Number>> m_TickLabelFormatterProperty = new SimpleObjectProperty<>( this, "labelFormatter" );

        /*------------------------*\
    ====** Static Initialisations **===========================================
        \*------------------------*/
    /**
     *  The CSS pseudo class for the horizontal orientation of a
     *  {@code RangeSlider}.
     */
    private static final PseudoClass HORIZONTAL_PSEUDOCLASS_STATE = PseudoClass.getPseudoClass("horizontal");

    /**
     *  The CSS pseudo class for the vertical orientation of a
     *  {@code RangeSlider}.
     */
    private static final PseudoClass VERTICAL_PSEUDOCLASS_STATE = PseudoClass.getPseudoClass("vertical");

        /*--------------*\
    ====** Constructors **=====================================================
        \*--------------*/
    /**
     * Creates a new {@code RangeSlider} instance using default values
     * of 0.0, 0.25, 0.75 and 1.0 for min/lowValue/highValue/max, respectively.
     */
    @SuppressWarnings( "MagicNumber" )
    public RangeSlider()
    {
        this(0, 1.0, 0.25, 0.75);
    }   //  RangeSlider()

    /**
     *  Instantiates a default, horizontal {@code RangeSlider} instance with
     *  the specified min/max/low/high values.
     *
     *  @param  min The minimum allowable value that the {@code RangeSlider}
     *      will allow.
     *  @param  max The maximum allowable value that the {@code RangeSlider}
     *      will allow.
     *  @param  lowValue    The initial value for the low value in the
     *      {@code RangeSlider}.
     *  @param  highValue   The initial value for the high value in the
     *      {@code RangeSlider}.
     */
    public RangeSlider( final double min, final double max, final double lowValue, final double highValue )
    {
        getStyleClass().setAll( DEFAULT_STYLE_CLASS );

        setMax( max );
        setMin( min );
        adjustValues();
        setLowValue( lowValue );
        setHighValue( highValue );
    }   //  RangeSlider()

        /*---------*\
    ====** Methods **==========================================================
        \*---------*/
    /**
     *  <p>{@summary Adjusts
     *  {@linkplain #highValueProperty() highValue}
     *  to match the given {@code newHigh}, or as closely as possible within
     *  the constraints imposed by the
     *  {@link #minProperty() min}
     *  and
     *  {@link #maxProperty() max}
     *  properties.} This method also takes into account the
     *  {@link #snapToTicksProperty() snapToTicks} flag, which is the main
     *  difference between {@code adjustHighValue()} and
     *  {@link #setHighValue(double) setHighValue()}.</p>
     *
     *  @param  newHigh The new value.
     */
    public final void adjustHighValue( final double newHigh )
    {
        final var d1 = getMin();
        final var d2 = getMax();
        if( d2 > d1 )
        {
            final var value = min( max( newHigh, d1 ), d2 );
            setHighValue( snapValueToTicks( value ) );
        }
    }   //  adjustHighValue()

    /**
     *  Adjusts the high value.
     *
     *  @see #adjustValues()
     */
    private final void adjustHighValues()
    {
        if( (getHighValue() < getMin()) || (getHighValue() > getMax()) )
        {
            setHighValue( clamp( getMin(), getHighValue(), getMax() ) );
        }
        else if( (getHighValue() < getLowValue()) && ((getLowValue() >= getMin()) && (getLowValue() <= getMax())) )
        {
            setHighValue( clamp(getLowValue(), getHighValue(), getMax() ) );
        }
    }   //  adjustHighValues()

    /**
     *  <p>{@summary Adjusts the
     *  {@linkplain #lowValueProperty() lowValue}
     *  to match the given {@code newLow}, or as closely as possible within
     *  the constraints imposed by the
     *  {@link #minProperty() min}
     *  and
     *  {@link #maxProperty() max}
     *  properties.} This method also takes into account the
     *  {@link #snapToTicksProperty() snapToTicks}
     *  flag, which is the main difference between {@code adjustLowValue()} and
     *  {@link #setLowValue(double) setLowValue()}.</p>
     *
     *  @param  newLow  The new value.
     */
    public final void adjustLowValue( final double newLow )
    {
        final var d1 = getMin();
        final var d2 = getMax();
        if( d2 > d1 )
        {
            final var value = min( max( newLow, d1 ), d2 );
            setLowValue( snapValueToTicks( value ) );
        }
    }   //  adjustLowValue()

    /**
     *  Adjusts the low value.
     *
     *  @see #adjustValues()
     */
    private final void adjustLowValues()
    {
        /*
         * We first look if the LowValue is between the min and max.
         */
        if( (getLowValue() < getMin()) || (getLowValue() > getMax()) )
        {
            final var value = clamp( getMin(), getLowValue(), getMax() );
            setLowValue( value );
            /*
             * If the LowValue seems right, we check if it's not superior to
             * HighValue ONLY if the highValue itself is right. Because it may
             * happen that the highValue has not yet been computed and is
             * wrong, and therefore force the lowValue to change in a wrong way
             * which may end up in an infinite loop.
             */
        }
        else if( (getLowValue() >= getHighValue()) && ((getHighValue() >= getMin()) && (getHighValue() <= getMax())) )
        {
            final var value = clamp( getMin(), getLowValue(), getHighValue() );
            setLowValue( value );
        }
    }   //  adjustLowValues()

    /**
     *  Ensures that {@code min} is always &lt;&nbsp;{@code max}, that the
     *  current {@code value} is always somewhere between the two, and that if
     *  {@code snapToTicks} is set then the {@code value} will always be set to
     *  align with a tick mark.
     */
    private final void adjustValues()
    {
        adjustLowValues();
        adjustHighValues();
    }   //  adjustValues()

    /**
     *  <p>{@summary Returns a reference to the property that holds the amount
     *  by which to adjust the slider if the track of the slider is
     *  clicked.} This is used when manipulating the slider position using
     *  keys. If
     *  {@link #snapToTicksProperty() snapToTicks}
     *  is {@code true} then the nearest tick mark to the adjusted value will
     *  be used.</p>
     *
     *  @return The property reference.
     */
    public final DoubleProperty blockIncrementProperty() { return m_BlockIncrementProperty; }

    /**
     * {@inheritDoc}
     */
    @Override
    protected final Skin<?> createDefaultSkin()
    {
        final var retValue = new RangeSliderSkin( this );

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  createDefaultSkin()

    /**
     *  Decrements the
     *  {@linkplain #highValueProperty() high value}
     *  by the
     *  {@linkplain #blockIncrementProperty() block increment}
     *  amount.
     */
    public final void decrementHighValue() { adjustHighValue( getHighValue() - getBlockIncrement() ); }

    /**
     *  Decrements the
     *  {@linkplain #lowValueProperty() low value}
     *  by the
     *  {@linkplain #blockIncrementProperty() block increment}
     *  amount.
     */
    public final void decrementLowValue() { adjustLowValue( getLowValue() - getBlockIncrement() ); }

    /**
     *  Returns the amount by which to adjust the slider if the track of the
     *  slider is clicked.
     *
     *  @return The amount by which to adjust the slider if the track of the
     *      slider is clicked.
     *
     * @see #blockIncrementProperty()
     */
    public final double getBlockIncrement() { return m_BlockIncrementProperty.get(); }

    /**
     *  Gets the
     *  {@link CssMetaData}
     *  associated with this class, which may include the {@code CssMetaData}
     *  of its super classes.
     *
     *  @return The {@code CssMetaData} for this class.
     */
    @SuppressWarnings( "MethodOverridesStaticMethodOfSuperclass" )
    public static List<CssMetaData<? extends Styleable, ?>> getClassCssMetaData() { return StyleableProperties.STYLEABLES; }

    /**
     * {@inheritDoc}
     */
    @Override
    public final List<CssMetaData<? extends Styleable, ?>> getControlCssMetaData() { return getClassCssMetaData(); }

    /**
     *  Returns the current high value for the range slider.
     *
     *  @return The high value.
     */
    public final double getHighValue() { return m_HighValueProperty.get(); }

    /**
     *  Returns the tick label formatter.
     *
     *  @return The
     *      {@link StringConverter}
     *      that is used as the tick label formatter.
     */
    public final StringConverter<Number> getLabelFormatter(){ return m_TickLabelFormatterProperty.get(); }

    /**
     *  Returns the current low value for the range slider.
     *
     *  @return The low value.
     */
    public final double getLowValue() { return m_LowValueProperty.get(); }

    /**
     *  Returns the unit distance between major tick marks.
     *
     *  @return The unit distance between major tick marks.
     *
     *  @see #majorTickUnitProperty()
     */
    public final double getMajorTickUnit() { return m_MajorTickUnitProperty.get(); }

    /**
     *  <p>{@summary Returns the maximum value for this
     *  {@code RangeSlider}.}</p>
     *  <p>100 is returned if the maximum value has never been set.</p>
     *
     *  @return The maximum value for this range slider.
     */
    public final double getMax() { return m_MaxProperty.get(); }

    /**
     *  <p>{@summary Returns the minimum value for this
     *  {@code RangeSlider}.}</p>
     *  <p>0 is returned if the minimum has never been set.</p>
     *
     *  @return The minimum value for this range slider.
     */
    public final double getMin() { return m_MinProperty.get(); }

    /**
     *  Returns the number of minor ticks to place between any two major ticks.
     *
     *  @return The number of minor ticks to place between any two major ticks.
     *
     *  @see #minorTickCountProperty()
     */
    public final int getMinorTickCount() { return m_MinorTickCountProperty.get(); }

    /**
     *  Returns the orientation of the {@code RangeSlider}.
     *
     *  @return The orientation of the {@code RangeSlider}.
     *      {@link Orientation#HORIZONTAL} is returned by default.
     */
    public final Orientation getOrientation() { return m_OrientationProperty.get(); }

    /**
     * {@inheritDoc}
     */
    @Override
    public final String getUserAgentStylesheet()
    {
        final var retValue = getUserAgentStylesheet( getClass(), "RangeSlider.css" );

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  getUserAgentStylesheet()

    /**
     *  <p>{@summary Returns a reference to the property that indicates a
     *  change to the high value of this {@code RangeSlider}.}</p>
     *  <p>When the property is set to {@code true}, it indicates that the
     *  current high value of this {@code RangeSlider} is changing. It provides
     *  notification that the high value is changing. Once the high value is
     *  computed, it is set back to {@code false}.</p>
     *
     *  @return The property reference.
     */
    public final BooleanProperty highValueChangingProperty() { return m_HighValueChangingProperty; }

    /**
     *  <p>{@summary Returns a reference to the property that holds the high
     *  value.}</p>
     *  <p>The high value property represents the current position of the high
     *  value thumb, and is within the allowable range as specified by the
     *  {@link #minProperty() min}
     *  and
     *  {@link #maxProperty() max}
     *  properties. By default this value is 100.</p>
     *
     *  @return The property reference.
     */
    public final DoubleProperty highValueProperty() { return m_HighValueProperty; }

    /**
     *  Increments the
     *  {@linkplain #highValueProperty() high value}
     *  by the
     *  {@linkplain #blockIncrementProperty() block increment}
     *  amount.
     */
    public final void incrementHighValue() { adjustHighValue( getHighValue() + getBlockIncrement() ); }

    /**
     *  Increments the
     *  {@linkplain #lowValueProperty() low value}
     *  value by the
     *  {@linkplain #blockIncrementProperty() block increment}
     *  amount.
     */
    public final void incrementLowValue() { adjustLowValue( getLowValue() + getBlockIncrement() ); }

    /**
     *  Returns whether the high value of this {@code RangeSlider} is currently
     *  changing.
     *
     *  @return {@code true} if the high value is currently changing, otherwise
     *      {@code false}.
     */
    public final boolean isHighValueChanging() { return m_HighValueChangingProperty.get(); }

    /**
     *  Returns whether the low value of this {@code RangeSlider} is currently
     *  changing.
     *
     *  @return {@code true} if the low value is currently changing, otherwise
     *      {@code false}.
     */
    public final boolean isLowValueChanging() { return m_LowValueChangingProperty.get(); }

    /**
     *  Returns the flag indicating whether labels of tick marks are being
     *  shown.
     *
     *  @return {@code true} if the tick mark label are shown, otherwise
     *      {@code false}.
     */
    public final boolean isShowTickLabels() { return m_ShowTickLabelsProperty.get(); }

    /**
     *  Returns the flag indication whether the tick marks are shown.
     *
     *  @return {@code true} if the tick marks are shown, otherwise
     *      {@code false}.
     */
    public final boolean isShowTickMarks() { return m_ShowTickMarksProperty.get(); }

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
     *  <p>{@summary Returns a reference to the property that holds the
     *  {@link StringConverter}
     *  that is used to format the tick mark labels.}</p>
     *  <p>If {@code null}, a default will be used.</p>
     *
     *  @return The property reference.
     */
    public final ObjectProperty<StringConverter<Number>> labelFormatterProperty()
    {
        return m_TickLabelFormatterProperty;
    }   //  labelFormatterProperty()

    /**
     *  <p>{@summary Returns a reference to the property that indicates a
     *  change to the low value of this {@code RangeSlider}.}</p>
     *  <p>When the property is set to {@code true}, it indicates that the
     *  current low value of this {@code RangeSlider} is changing. It provides
     *  notification that the low value is changing. Once the low value is
     *  computed, the property is set back to {@code false}.</p>
     *
     *  @return The property reference.
     */
    public final BooleanProperty lowValueChangingProperty() { return m_LowValueChangingProperty; }

    /**
     *  <p>{@summary Returns a reference to the property that holds the low
     *  value.}</p>
     *  <p>The low value property represents the current position of the low
     *  value thumb, and is within the allowable range as specified by the
     *  {@link #minProperty() min}
     *  and
     *  {@link #maxProperty() max}
     *  properties. By default this value is 0.</p>
     *
     *  @return The property reference.
     */
    public final DoubleProperty lowValueProperty() { return m_LowValueProperty; }

    /**
     *  <p>{@summary Returns a reference to the property that holds the unit
     *  distance between major tick marks.} For example, if the
     *  {@linkplain #minProperty() min value}
     *  is 0 and the
     *  {@linkplain #maxProperty() max value}
     *  is 100 and the {@code majorTickUnit} is set to 25, then there would be
     *  5 tick marks: one at position 0, one at position 25, one at position
     *  50, one at position 75, and a final one at position 100.</p>
     *  <p>This value should be positive and should be a value less than the
     *  span. Out of range values are essentially the same as disabling tick
     *  marks.</p>
     *
     *  @return The property reference.
     */
    public final DoubleProperty majorTickUnitProperty() { return m_MajorTickUnitProperty; }

    /**
     *  <p>{@summary Returns a reference to the property that holds the maximum
     *  value for this {@code RangeSlider}.}</p>
     *
     *  @return The property reference.
     */
    public final DoubleProperty maxProperty() { return m_MaxProperty; }

    /**
     *  <p>{@summary Returns a reference to the property that holds the number
     *  of minor ticks to place between any two major ticks.} This number
     *  should be positive or zero. Out of range values will disable minor
     *  ticks, as will a value of zero.</p>
     *
     *  @return The property reference.
     */
    public final IntegerProperty minorTickCountProperty() { return m_MinorTickCountProperty; }

    /**
     *  <p>{@summary Returns a reference to the property that holds the minimum
     *  value for this {@code RangeSlider}.}</p>
     *
     *  @return The property reference.
     */
    public final DoubleProperty minProperty() { return m_MinProperty; }

    /**
     *  <p>{@summary Returns a reference to the property that holds the
     *  orientation of this {@code RangeSlider}.}</p>
     *  <p>The orientation can either be
     *  {@linkplain Orientation#HORIZONTAL horizontal}
     *  or
     *  {@linkplain Orientation#VERTICAL vertical}.</p>
     *
     *  @return The property reference.
     */
    public final ObjectProperty<Orientation> orientationProperty() { return m_OrientationProperty; }

    /**
     *  Sets the amount by which to adjust the slider if the track of the
     *  slider is clicked.
     *
     *  @param  value   The adjustment value.
     *
     *  @see #blockIncrementProperty()
     */
    public final void setBlockIncrement( final double value) { m_BlockIncrementProperty.set( value ); }

    /**
     *  Call this when high value is changing.
     *
     *  @param  flag    {@code true} if the high value is currently changing,
     *      {@code false} otherwise.
     */
    public final void setHighValueChanging( final boolean flag ) { m_HighValueChangingProperty.set( flag ); }

    /**
     *  Sets the high value for the range slider, which may or may not be
     *  clamped to be within the allowable range as specified by the
     *  {@link #minProperty() min}
     *  and
     *  {@link #maxProperty() max}
     *  properties.
     *
     *  @param high The value.
     */
    public final void setHighValue( final double high )
    {
        if( !m_HighValueProperty.isBound() ) m_HighValueProperty.set( high );
    }   //  setHighValue()

    /**
     *  Sets the tick label formatter.
     *
     *  @param  formatter   The
     *      {@link StringConverter}
     *      that is used to format the tick labels.
     */
    public final void setLabelFormatter( final StringConverter<Number> formatter )
    {
        m_TickLabelFormatterProperty.set( formatter );
    }   //  setLabelFormatter()

    /**
     *  Sets the low value for the range slider, which may or may not be
     *  clamped to be within the allowable range as specified by the
     *  {@link #minProperty() min}
     *  and
     *  {@link #maxProperty() max}
     *  properties.
     *
     *  @param  low The value.
     */
    public final void setLowValue( final double low )
    {
        if( !m_LowValueProperty.isBound() ) m_LowValueProperty.set( low );
    }   //  setLowValue()

    /**
     *  Call this when the low value is changing.
     *
     *  @param  flag {@code true} if the low value is changing, {@code false}
     *      otherwise.
     */
    public final void setLowValueChanging( final boolean flag ) { m_LowValueChangingProperty.set( flag ); }

    /**
     *  Sets the unit distance between major tick marks.
     *
     *  @param  tickUnit    The unit distance.
     *
     *  @see #majorTickUnitProperty()
     */
    public final void setMajorTickUnit( final double tickUnit )
    {
        m_MajorTickUnitProperty.set( requireValidDoubleArgument( tickUnit, "tickUnit", value -> value > 0.0, $ -> "MajorTickUnit cannot be less than or equal to 0." ) );
    }   //  setMajorTickUnit()

    /**
     *  Sets the maximum value for this {@code RangeSlider}.
     *
     *  @param  max The new value.
     */
    public final void setMax( final double max ) { m_MaxProperty.set( max ); }

    /**
     *  Sets the minimum value for this  {@code RangeSlider}.
     *
     *  @param  min The new value
     */
    public final void setMin( final double min ) { m_MinProperty.set( min ); }

    /**
     *  Sets the number of minor ticks to place between any two major ticks.
     *
     *  @param  numberOfTicks   The number of minor ticks; should be 0 or a
     *      positive number, out of range values will disable the feature.
     *
     *  @see #minorTickCountProperty()
     */
    public final void setMinorTickCount( final int numberOfTicks ) { m_MinorTickCountProperty.set( numberOfTicks ); }

    /**
     *  Sets the orientation of the {@code RangeSlider}.
     *
     *  @param  orientation The orientation.
     */
    public final void setOrientation( final Orientation orientation ) { m_OrientationProperty.set( orientation ); }

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
     *  Sets whether labels of tick marks should be shown or not.
     *
     *  @param  flag    {@code true} if the tick mark labels should be shown,
     *      {@code false} if not.
     */
    public final void setShowTickLabels( final boolean flag ) { m_ShowTickLabelsProperty.set( flag ); }

    /**
     *  Sets whether tick marks should be shown or not.
     *
     *  @param  flag    {@code true} if the tick marks are shown, {@code false}
     *      if not.
     */
    public final void setShowTickMarks( final boolean flag ) { m_ShowTickMarksProperty.set( flag ); }

    /**
     *  <p>{@summary Returns a reference to the property that holds the flag
     *  that indicates that the labels for tick marks should be shown.}</p>
     *  <p>Typically, a
     *  {@link Skin}
     *  implementation will only show labels if
     *  {@link #showTickMarksProperty() showTickMarks}
     *  is also {@code true}.</p>
     *
     *  @return The property reference.
     */
    public final BooleanProperty showTickLabelsProperty() { return m_ShowTickLabelsProperty; }

    /**
     *  <p>{@summary Returns a reference to the property that holds the flag
     *  that specifies whether the
     *  {@link Skin}
     *  implementation should show tick marks.}</p>
     *
     *  @return The property reference.
     */
    public final BooleanProperty showTickMarksProperty() { return m_ShowTickMarksProperty; }

    /**
     *  <p>{@summary Returns a reference to the property that holds the flag
     *  that indicates whether the
     *  {@linkplain #lowValueProperty() low value}/{@linkplain #highValueProperty() high value}
     *  thumbs of the {@code RangeSlider} should always be aligned with the
     *  tick marks.} This is honored even if the tick marks are not shown.</p>
     *
     *  @return The property reference.
     */
    public final BooleanProperty snapToTicksProperty() { return m_SnapToTicksProperty; }

    /**
     *  Aligns the given value with the nearest tick mark value.
     *
     *  @param  value   The value.
     *  @return The adjusted value.
     */
    private final double snapValueToTicks( final double value)
    {
        var d1 = value;
        if( isSnapToTicks() )
        {
            final double d2;
            if( getMinorTickCount() != 0 )
            {
                d2 = getMajorTickUnit() / (double) (Integer.max( getMinorTickCount(), 0 ) + 1 );
            }
            else
            {
                d2 = getMajorTickUnit();
            }
            @SuppressWarnings( {"LocalVariableNamingConvention", "NumericCastThatLosesPrecision"} )
            final var i = (int) ((d1 - getMin()) / d2);
            final var d3 = (double) i * d2 + getMin();
            final var d4 = (double) (i + 1) * d2 + getMin();
            d1 = nearest( d3, d1, d4 );
        }
        final var retValue = clamp( getMin(), d1, getMax() );

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  snapValueToTicks()
}
//  class RangeSlider

/*
 *  End of file
 */