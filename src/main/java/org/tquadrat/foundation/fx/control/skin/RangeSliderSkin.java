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

import static java.lang.Double.max;
import static javafx.geometry.Orientation.HORIZONTAL;
import static javafx.scene.layout.Region.USE_COMPUTED_SIZE;
import static org.apiguardian.api.API.Status.INTERNAL;
import static org.apiguardian.api.API.Status.STABLE;
import static org.tquadrat.foundation.fx.FXUtils.clamp;
import static org.tquadrat.foundation.fx.FXUtils.nearest;
import static org.tquadrat.foundation.fx.control.skin.RangeSliderSkin.FocusedChild.HIGH_THUMB;
import static org.tquadrat.foundation.fx.control.skin.RangeSliderSkin.FocusedChild.LOW_THUMB;
import static org.tquadrat.foundation.fx.control.skin.RangeSliderSkin.FocusedChild.NONE;
import static org.tquadrat.foundation.fx.control.skin.RangeSliderSkin.FocusedChild.RANGE_BAR;
import static org.tquadrat.foundation.fx.internal.ControlUtils.focusNextSibling;
import static org.tquadrat.foundation.fx.internal.ControlUtils.focusPreviousSibling;
import static org.tquadrat.foundation.lang.Objects.isNull;
import static org.tquadrat.foundation.lang.Objects.nonNull;
import static org.tquadrat.foundation.lang.Objects.requireNonNullArgument;

import org.apiguardian.api.API;
import org.tquadrat.foundation.annotation.ClassVersion;
import org.tquadrat.foundation.fx.control.RangeSlider;
import javafx.beans.binding.ObjectBinding;
import javafx.event.EventHandler;
import javafx.geometry.Orientation;
import javafx.geometry.Point2D;
import javafx.geometry.Side;
import javafx.scene.Cursor;
import javafx.scene.chart.NumberAxis;
import javafx.scene.control.Skin;
import javafx.scene.control.SkinBase;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.util.Callback;

/**
 *  The skin for instances of
 *  {@link RangeSlider}.
 *
 *  @extauthor Thomas Thrien - thomas.thrien@tquadrat.org
 *  @inspired  {@href https://controlsfx.github.io/ ControlsFX Project}
 *  @version $Id: RangeSliderSkin.java 1114 2024-03-12 23:07:59Z tquadrat $
 *  @since 0.4.6
 *
 *  @UMLGraph.link
 */
@SuppressWarnings( {"ClassWithTooManyFields", "ClassWithTooManyMethods", "OverlyComplexClass"} )
@ClassVersion( sourceVersion = "$Id: RangeSliderSkin.java 1114 2024-03-12 23:07:59Z tquadrat $" )
@API( status = STABLE, since = "0.4.6" )
public class RangeSliderSkin extends SkinBase<RangeSlider>
{
        /*---------------*\
    ====** Inner Classes **====================================================
        \*---------------*/
    /**
     *  The indicators for the focus owners.
     *
     *  @extauthor Thomas Thrien - thomas.thrien@tquadrat.org
     *  @inspired  {@href https://controlsfx.github.io/ ControlsFX Project}
     *  @version $Id: RangeSliderSkin.java 1114 2024-03-12 23:07:59Z tquadrat $
     *  @since 0.4.6
     *
     *  @UMLGraph.link
     */
    @SuppressWarnings( "ProtectedInnerClass" )
    @ClassVersion( sourceVersion = "$Id: RangeSliderSkin.java 1114 2024-03-12 23:07:59Z tquadrat $" )
    @API( status = INTERNAL, since = "0.4.6" )
    protected static enum FocusedChild
    {
            /*------------------*\
        ====** Enum Declaration **=============================================
            \*------------------*/
        /**
         *  The focus is on the low thumb.
         */
        LOW_THUMB,

        /**
         *  The focus is on the high thump.
         */
        HIGH_THUMB,

        /**
         *  The focus is on the range bar.
         */
        RANGE_BAR,

        /**
         *  None of the elements of the
         *  {@link RangeSlider}
         *  does currently have the focus.
         */
        NONE
    }
    //  enum FocusedChild

    /**
     *  The implementation of
     *  {@link StackPane}
     *  that is used for the thumbs of a
     *  {@link RangeSlider}
     *  instance.
     *
     *  @extauthor Thomas Thrien - thomas.thrien@tquadrat.org
     *  @inspired  {@href https://controlsfx.github.io/ ControlsFX Project}
     *  @version $Id: RangeSliderSkin.java 1114 2024-03-12 23:07:59Z tquadrat $
     *  @since 0.4.6
     *
     *  @UMLGraph.link
     */
    @ClassVersion( sourceVersion = "$Id: RangeSliderSkin.java 1114 2024-03-12 23:07:59Z tquadrat $" )
    @API( status = INTERNAL, since = "0.4.6" )
    private static final class ThumbPane extends StackPane
    {
            /*--------------*\
        ====** Constructors **=================================================
            \*--------------*/
        /**
         *  Creates a new instance of {@code ThumbPane}.
         */
        public ThumbPane() { super(); }

            /*---------*\
        ====** Methods **======================================================
            \*---------*/
        /**
         *  Sets the focus.
         *
         *  @param  flag    {@code true} if this instance has the focus,
         *      {@code false} if not.
         */
        public final void setFocus( final boolean flag ) { setFocused( flag ); }
    }
    //  class ThumbPane

        /*------------*\
    ====** Attributes **=======================================================
        \*------------*/
    /**
     *  The current focus owner.
     */
    private FocusedChild m_CurrentFocus = LOW_THUMB;

    /**
     *  The high thumb itself.
     */
    private ThumbPane m_HighThumb;

    /**
     *  The low thumb itself.
     */
    private ThumbPane m_LowThumb;

    /**
     *  The position for the low thumb.
     */
    private double m_LowThumbPos;

    /**
     *  The orientation for the {@code RangeSlider}.
     */
    private Orientation m_Orientation;

    /**
     *  Used as a temp value for low and high thumbs.
     */
    private double m_PreDragPos;

    /**
     *  The
     *  {@link #m_PreDragPos}
     *  in skin coordinates.
     */
    private Point2D m_PreDragThumbPoint;

    /**
     *  The bar between the two thumbs, can be dragged.
     */
    private StackPane m_RangeBar;

    /**
     *  The end of the range.
     */
    private double m_RangeEnd;

    /**
     *  The start of the range.
     */
    private double m_RangeStart;

    /**
     *  The callback for the value selection.
     */
    private Callback<Void,FocusedChild> m_SelectedValue;

    /**
     *  The flag that indicates whether the tick marks are shown or not.
     */
    private boolean m_ShowTickMarks;

    /**
     *  The height of the thumbs.
     */
    @SuppressWarnings( "FieldCanBeLocal" )
    private double m_ThumbHeight;

    /**
     *  The width of the thumbs.
     */
    private double m_ThumbWidth;

    /**
     *  The tick line.
     */
    private NumberAxis m_TickLine = null;

    /**
     *  The container that represents the slider track.
     */
    private StackPane m_Track;

    /**
     *  The length of the track.
     */
    private double m_TrackLength;

    /**
     *  The start of the track.
     */
    private double m_TrackStart;

    /**
     *  The width of the gap between the slider track and the tick line.
     */
    @SuppressWarnings( {"MagicNumber", "FieldMayBeFinal"} )
    private double m_TrackToTickGap = 2.0;

        /*--------------*\
    ====** Constructors **=====================================================
        \*--------------*/
    /**
     *  Creates a new instance of {@code RangeSliderSkin}.
     *
     *  @param  control The control for which this Skin should attach to.
     */
    @SuppressWarnings( {"OverlyLongMethod", "OverlyComplexMethod"} )
    public RangeSliderSkin( final RangeSlider control )
    {
        super( requireNonNullArgument( control, "control" ) );

        m_Orientation = getSkinnable().getOrientation();

        initLowThumb();
        initHighThumb();
        initRangeBar();

        registerChangeListener( control.lowValueProperty(), $ ->
        {
            positionLowThumb();
            if( isHorizontal() )
            {
                m_RangeBar.resizeRelocate( m_RangeStart, m_RangeBar.getLayoutY(), m_RangeEnd - m_RangeStart, m_RangeBar.getHeight() );
            }
            else
            {
                m_RangeBar.resize( m_RangeBar.getWidth(), m_RangeEnd - m_RangeStart );
            }
        } );
        registerChangeListener( control.highValueProperty(), $ ->
        {
            positionHighThumb();
            if( isHorizontal() )
            {
                m_RangeBar.resize( m_RangeEnd - m_RangeStart, m_RangeBar.getHeight() );
            }
            else
            {
                m_RangeBar.resizeRelocate( m_RangeBar.getLayoutX(), m_RangeStart, m_RangeBar.getWidth(), m_RangeEnd - m_RangeStart );
            }
        } );
        registerChangeListener( control.minProperty(), $ ->
        {
            if( m_ShowTickMarks && nonNull( m_TickLine ) )
            {
                m_TickLine.setLowerBound( getSkinnable().getMin() );
            }
            getSkinnable().requestLayout();
        } );
        registerChangeListener( control.maxProperty(), $ ->
        {
            if( m_ShowTickMarks && nonNull( m_TickLine ) )
            {
                m_TickLine.setUpperBound( getSkinnable().getMax() );
            }
            getSkinnable().requestLayout();
        } );
        registerChangeListener( control.orientationProperty(), $ ->
        {
            m_Orientation = getSkinnable().getOrientation();
            if( m_ShowTickMarks && nonNull( m_TickLine ) )
            {
                m_TickLine.setSide( isHorizontal() ? Side.BOTTOM : Side.RIGHT );
            }
            getSkinnable().requestLayout();
        } );
        registerChangeListener( control.showTickMarksProperty(),
            $ -> setShowTickMarks( getSkinnable().isShowTickMarks(), getSkinnable().isShowTickLabels() ) );
        registerChangeListener( control.showTickLabelsProperty(),
            $ -> setShowTickMarks( getSkinnable().isShowTickMarks(), getSkinnable().isShowTickLabels() ) );
        registerChangeListener( control.majorTickUnitProperty(), $ ->
        {
            if( nonNull( m_TickLine ) )
            {
                m_TickLine.setTickUnit( getSkinnable().getMajorTickUnit() );
                getSkinnable().requestLayout();
            }
        } );
        registerChangeListener( control.minorTickCountProperty(), $ ->
        {
            if( nonNull( m_TickLine ) )
            {
                m_TickLine.setMinorTickCount( Integer.max( getSkinnable().getMinorTickCount(),0 ) + 1 );
                getSkinnable().requestLayout();
            }
        } );

        //noinspection LambdaParameterNamingConvention
        m_LowThumb.focusedProperty().addListener( ($1,$2,hasFocus) ->
        {
            if( hasFocus ) m_CurrentFocus = LOW_THUMB;
        } );
        //noinspection LambdaParameterNamingConvention
        m_HighThumb.focusedProperty().addListener( ($1,$2,hasFocus) ->
        {
            if( hasFocus ) m_CurrentFocus = HIGH_THUMB;
        } );
        //noinspection LambdaParameterNamingConvention
        m_RangeBar.focusedProperty().addListener( ($1,$2,hasFocus) ->
        {
            if( hasFocus ) m_CurrentFocus = RANGE_BAR;
        } );
        //noinspection LambdaParameterNamingConvention
        control.focusedProperty().addListener( ($1,$2,hasFocus) ->
        {
            if( hasFocus )
            {
                m_LowThumb.setFocus( true );
            }
            else
            {
                m_LowThumb.setFocus( false );
                m_HighThumb.setFocus( false );
                m_CurrentFocus = NONE;
            }
        } );

        @SuppressWarnings( "OverlyLongLambda" )
        final EventHandler<KeyEvent> keyPressEventHandler = event ->
        {
            switch( event.getCode() )
            {
                case TAB ->
                {
                    if( m_LowThumb.isFocused() )
                    {
                        if( event.isShiftDown() )
                        {
                            focusPreviousSibling( getSkinnable() );
                        }
                        else
                        {
                            m_LowThumb.setFocus( false );
                            m_HighThumb.setFocus( true );
                        }
                        event.consume();
                    }
                    else if( m_HighThumb.isFocused() )
                    {
                        if( event.isShiftDown() )
                        {
                            m_HighThumb.setFocus( false );
                            m_LowThumb.setFocus( true );
                        }
                        else
                        {
                            focusNextSibling( getSkinnable() );
                        }
                        event.consume();
                    }
                }   //  case TAB

                case LEFT, KP_LEFT ->
                {
                    if( getSkinnable().getOrientation() == HORIZONTAL )
                    {
                        rtl( getSkinnable(), this::incrementValue, this::decrementValue );
                    }
                }

                case RIGHT, KP_RIGHT ->
                {
                    if( getSkinnable().getOrientation() == HORIZONTAL )
                    {
                        rtl( getSkinnable(), this::decrementValue, this::incrementValue );
                    }
                }

                case DOWN, KP_DOWN ->
                {
                    if( getSkinnable().getOrientation() == Orientation.VERTICAL )
                    {
                        decrementValue();
                    }
                }

                case UP, KP_UP ->
                {
                    if( getSkinnable().getOrientation() == Orientation.VERTICAL )
                    {
                        incrementValue();
                    }
                }

                default -> {}
            }
            event.consume();
        };

        @SuppressWarnings( "OverlyLongLambda" )
        final EventHandler<KeyEvent> keyReleaseEventHandler = event ->
        {
            switch( event.getCode() )
            {
                case HOME -> home();
                case END -> end();
                default -> {}
            }
            event.consume();
        };

        getSkinnable().addEventHandler( KeyEvent.KEY_PRESSED, keyPressEventHandler );
        getSkinnable().addEventHandler( KeyEvent.KEY_RELEASED, keyReleaseEventHandler );

        /*
         * Set up a callback to indicate which thumb is currently selected
         * (via enum).
         */
        setSelectedValue( $ -> m_CurrentFocus );
    }   //  RangeSliderSkin()

        /*---------*\
    ====** Methods **==========================================================
        \*---------*/
    /**
     *  Calculates the increment/decrement value that is used by
     *  {@link #incrementValue()}
     *  and
     *  {@link #decrementValue()}
     *  to move the thumps.
     *
     *  @return The increment value (that is also used decrement the position
     *      values for the thumbs).
     */
    private final double computeIncrement()
    {
        final var rangeSlider = getSkinnable();
        final double increment;
        if( rangeSlider.getMinorTickCount() != 0 )
        {
            increment = rangeSlider.getMajorTickUnit() / (max( (double) rangeSlider.getMinorTickCount(), 0.0 ) + 1);
        }
        else
        {
            increment = rangeSlider.getMajorTickUnit();
        }
        final var retValue = (rangeSlider.getBlockIncrement() > 0.0D) && (rangeSlider.getBlockIncrement() < increment)
            ? increment
            : rangeSlider.getBlockIncrement();

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  computeIncrement()

    /**
     *  {@inheritDoc}
     */
    @Override
    protected final double computeMaxHeight( final double width, final double topInset, final double rightInset, final double bottomInset, final double leftInset)
    {
        final var retValue = isHorizontal() ? getSkinnable().prefHeight( width ) : Double.MAX_VALUE;

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  computeMaxHeight()

    /**
     *  {@inheritDoc}
     */
    @Override
    protected final double computeMaxWidth( final double height, final double topInset, final double rightInset, final double bottomInset, final double leftInset )
    {
        final var retValue = isHorizontal() ? Double.MAX_VALUE : getSkinnable().prefWidth( USE_COMPUTED_SIZE );

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  computeMaxWidth()

    /**
     *  {@inheritDoc}
     */
    @Override
    protected final double computeMinHeight( final double width, final double topInset, final double rightInset, final double bottomInset, final double leftInset )
    {
        final var retValue = isHorizontal()
                             ? topInset + m_LowThumb.prefHeight( USE_COMPUTED_SIZE ) + bottomInset
                             : topInset + minTrackLength() + m_LowThumb.prefHeight( USE_COMPUTED_SIZE ) + bottomInset;

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  computeMinHeight()

    /**
     *  {@inheritDoc}
     */
    @Override
    protected final double computeMinWidth( final double height, final double topInset, final double rightInset, final double bottomInset, final double leftInset )
    {
        final var retValue = isHorizontal()
                             ? leftInset + minTrackLength() + m_LowThumb.minWidth( USE_COMPUTED_SIZE ) + rightInset
                             : leftInset + m_LowThumb.prefWidth( USE_COMPUTED_SIZE ) + rightInset;

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  computeMinWidth()

    /**
     *  {@inheritDoc}
     */
    @Override
    protected final double computePrefHeight( final double width, final double topInset, final double rightInset, final double bottomInset, final double leftInset )
    {
        final double retValue;
        if( isHorizontal() )
        {
            retValue = getSkinnable().getInsets().getTop()
                + max( m_LowThumb.prefHeight( USE_COMPUTED_SIZE ), m_Track.prefHeight( USE_COMPUTED_SIZE ) )
                + (m_ShowTickMarks ? m_TrackToTickGap + m_TickLine.prefHeight( USE_COMPUTED_SIZE ) : 0.0)
                + bottomInset;
        }
        else
        {
            retValue = m_ShowTickMarks ? max(140.0, m_TickLine.prefHeight(USE_COMPUTED_SIZE) ) : 140.0;
        }

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  computePrefHeight()

    /**
     *  {@inheritDoc}
     */
    @Override
    protected final double computePrefWidth( final double height, final double topInset, final double rightInset, final double bottomInset, final double leftInset )
    {
        final double retValue;
        if( isHorizontal() )
        {
            retValue = m_ShowTickMarks ? max( 140.0, m_TickLine.prefWidth( USE_COMPUTED_SIZE ) ) : 140.0;
        }
        else
        {
            retValue = leftInset
                + max( m_LowThumb.prefWidth( USE_COMPUTED_SIZE ), m_Track.prefWidth( USE_COMPUTED_SIZE ) )
                + (m_ShowTickMarks ? m_TrackToTickGap + m_TickLine.prefWidth( USE_COMPUTED_SIZE ) : 0.0)
                + rightInset;
        }

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  computePrefWidth()

    /**
     *  Adjusts the range bar's position after it was released.
     */
    private void confirmRange()
    {
        final var rangeSlider = getSkinnable();

        if( rangeSlider.isSnapToTicks() )
        {
            rangeSlider.setLowValue( snapValueToTicks( rangeSlider.getLowValue() ) );
        }
        rangeSlider.setLowValueChanging( false );
        if( rangeSlider.isSnapToTicks() )
        {
            rangeSlider.setHighValue( snapValueToTicks( rangeSlider.getHighValue() ) );
        }
        rangeSlider.setHighValueChanging( false );
    }   //  confirmRange()

    /**
     *  Moves the selected thumb in the direction to the
     *  {@link RangeSlider#getMin() min}
     *  value.
     */
    private final void decrementValue()
    {
        final var rangeSlider = getSkinnable();
        if( nonNull( m_SelectedValue ) )
        {
            if ( m_SelectedValue.call(null) == HIGH_THUMB)
            {
                if( rangeSlider.isSnapToTicks() )
                {
                    rangeSlider.adjustHighValue( rangeSlider.getHighValue() - computeIncrement() );
                }
                else
                {
                    rangeSlider.decrementHighValue();
                }
            }
            else
            {
                if( rangeSlider.isSnapToTicks() )
                {
                    rangeSlider.adjustLowValue( rangeSlider.getLowValue() - computeIncrement() );
                }
                else
                {
                    rangeSlider.decrementLowValue();
                }
            }
        }
    }   //  decrementValue()

    /**
     *  Responds to the END key.
     */
    private final void end()
    {
        final var rangeSlider = getSkinnable();
        rangeSlider.adjustHighValue( rangeSlider.getMax() );
    }   //  end()

    /**
     *  Returns the difference between
     *  {@link RangeSlider#getMax()}
     *  and
     *  {@link RangeSlider#getMin()},
     *  but if they have the same value, 1.0 is returned instead of 0.0 because
     *  otherwise the division where the result value can be used will return
     *  {@link Double#NaN}.
     *
     *  @return The difference.
     */
    private final double getMaxMinusMinNoZero()
    {
        final var rangeSlider = getSkinnable();
        final var retValue = Double.compare( rangeSlider.getMin(), rangeSlider.getMax() ) == 0 ? 1.0 : rangeSlider.getMax() - rangeSlider.getMin();

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  getMaxMinusMinNoZero()

    /**
     *  Updates the
     *  {@linkplain RangeSlider#highValueProperty() high value}
     *  based on the new position of the high thumb after it was dragged with
     *  the mouse.
     *
     *  @param  ignoredMouseEvent   The mouse event.
     *  @param  position    The mouse position on the track with 0.0 being the
     *      start of the track and 1.0 being the end.
     */
    private final void highThumbDragged( final MouseEvent ignoredMouseEvent, final double position )
    {
        final var rangeSliderlider = getSkinnable();
        rangeSliderlider.setHighValue( clamp( rangeSliderlider.getMin(), position * (rangeSliderlider.getMax() - rangeSliderlider.getMin()) + rangeSliderlider.getMin(), rangeSliderlider.getMax() ) );
    }   //  highThumbDragged()

    /**
     *  <p>{@summary Prepares the dragging of the high thumb.}</p>
     *  <p>When the high thumb is released,
     *  {@link RangeSlider#highValueChangingProperty()}
     *  is set to {@code false}.</p>
     *
     *  @param  ignoredMouseEvent   The mouse event.
     *  @param  ignoredPosition The new position.
     */
    @SuppressWarnings( "SameParameterValue" )
    private final void highThumbPressed( final MouseEvent ignoredMouseEvent, final double ignoredPosition )
    {
        final var rangeSlider = getSkinnable();
        if( !rangeSlider.isFocused() ) rangeSlider.requestFocus();
        rangeSlider.setHighValueChanging( true );
    }   //  highThumbPressed()

    /**
     *  Adjusts the
     *  {@linkplain RangeSlider#highValueProperty() high value}
     *  when the high thumb is released.
     *
     *  @param  mouseEvent  The mouse event.
     */
    private final void highThumbReleased( final MouseEvent mouseEvent )
    {
        final var rangeSlider = getSkinnable();
        if( rangeSlider.isSnapToTicks() )
        {
            rangeSlider.setHighValue( snapValueToTicks( rangeSlider.getHighValue() ) );
        }
        rangeSlider.setHighValueChanging( false );
    }   //  highThumbReleased()

    /**
     *  Responds to the HOME key.
     */
    private final void home()
    {
        final var rangeSlider = getSkinnable();
        rangeSlider.adjustHighValue( rangeSlider.getMin() );
    }   //  home()

    /**
     *  Moves the selected thumb in the direction to the
     *  {@link RangeSlider#getMax() max}
     *  value.
     */
    private final void incrementValue()
    {
        final var rangeSlider = getSkinnable();
        if( nonNull( m_SelectedValue ) )
        {
            if( m_SelectedValue.call(null) == HIGH_THUMB )
            {
                if( rangeSlider.isSnapToTicks() )
                {
                    rangeSlider.adjustHighValue( rangeSlider.getHighValue() + computeIncrement() );
                }
                else
                {
                    rangeSlider.incrementHighValue();
                }
            }
            else
            {
                if( rangeSlider.isSnapToTicks() )
                {
                    rangeSlider.adjustLowValue( rangeSlider.getLowValue() + computeIncrement() );
                }
                else
                {
                    rangeSlider.incrementLowValue();
                }
            }
        }
    }   //  incrementValue()

    /**
     *  <p>{@summary Initialises the high thumb.}</p>
     *  <p>Needs to be called after
     *  {@link #initLowThumb()}
     *  and before
     *  {@link #initRangeBar()}.</p>
     */
    private final void initHighThumb()
    {
        m_HighThumb = new ThumbPane();
        m_HighThumb.getStyleClass().setAll( "high-thumb" );
        if( !getChildren().contains( m_HighThumb ) ) getChildren().add( m_HighThumb );

        m_HighThumb.setOnMousePressed( e ->
        {
            m_LowThumb.setFocus( false );
            m_HighThumb.setFocus( true );
            highThumbPressed( e, 0.0D );
            m_PreDragThumbPoint = m_HighThumb.localToParent( e.getX(), e.getY() );
            m_PreDragPos = (getSkinnable().getHighValue() - getSkinnable().getMin()) / (getMaxMinusMinNoZero());
        } );
        m_HighThumb.setOnMouseReleased( this::highThumbReleased );
        //noinspection OverlyLongLambda
        m_HighThumb.setOnMouseDragged( mouseEvent ->
        {
            final var orientation = getSkinnable().getOrientation() == HORIZONTAL;
            final var trackLength = orientation ? m_Track.getWidth() : m_Track.getHeight();

            final var point2d = m_HighThumb.localToParent( mouseEvent.getX(), mouseEvent.getY() );
            if( isNull( m_PreDragThumbPoint ) )  m_PreDragThumbPoint = point2d;
            final var relativePosition = getSkinnable().getOrientation() == HORIZONTAL
                ? point2d.getX() - m_PreDragThumbPoint.getX()
                : -(point2d.getY() - m_PreDragThumbPoint.getY());
            highThumbDragged( mouseEvent, m_PreDragPos + relativePosition / trackLength );
        } );
    }   //  initHighThumb()

    /**
     *  <p>{@summary Initialises the low thumb.}</p>
     *  <p>Needs to be called before
     *  {@link #initHighThumb()}.</p>
     */
    private final void initLowThumb()
    {
        m_LowThumb = new ThumbPane();
        m_LowThumb.getStyleClass().setAll( "low-thumb" );
        m_LowThumb.setFocusTraversable( true );
        m_Track = new StackPane();
        m_Track.setFocusTraversable( false );
        m_Track.getStyleClass().setAll( "track" );

        getChildren().clear();
        getChildren().addAll( m_Track, m_LowThumb );
        setShowTickMarks( getSkinnable().isShowTickMarks(), getSkinnable().isShowTickLabels() );

        m_Track.setOnMousePressed( me ->
        {
            if( !m_LowThumb.isPressed() && !m_HighThumb.isPressed() )
            {
                if( isHorizontal() )
                {
                    trackPress( me, (me.getX() / m_TrackLength) );
                }
                else
                {
                    trackPress( me, (me.getY() / m_TrackLength) );
                }
            }
        } );

        m_LowThumb.setOnMousePressed( me ->
        {
            m_HighThumb.setFocus( false );
            m_LowThumb.setFocus( true );
            lowThumbPressed( me, 0.0f );
            m_PreDragThumbPoint = m_LowThumb.localToParent( me.getX(), me.getY() );
            m_PreDragPos = (getSkinnable().getLowValue() - getSkinnable().getMin()) / getMaxMinusMinNoZero();
        } );

        m_LowThumb.setOnMouseReleased( this::lowThumbReleased );

        m_LowThumb.setOnMouseDragged( mouseEvent ->
        {
            final var cur = m_LowThumb.localToParent( mouseEvent.getX(), mouseEvent.getY() );
            if( isNull( m_PreDragThumbPoint ) )  m_PreDragThumbPoint = cur;
            final var dragPos = isHorizontal() ? cur.getX() - m_PreDragThumbPoint.getX() : -(cur.getY() - m_PreDragThumbPoint.getY());
            lowThumbDragged( mouseEvent, m_PreDragPos + dragPos / m_TrackLength );
        } );
    }   //  initLowThumb()

    /**
     *  <p>{@summary Initialises the range bar.}</p>
     *  <p>Needs to be called after
     *  {@link #initHighThumb()}.</p>
     */
    private final void initRangeBar()
    {
        m_RangeBar = new StackPane();
        m_RangeBar.setFocusTraversable( false );
        //noinspection AnonymousInnerClass
        m_RangeBar.cursorProperty().bind( new ObjectBinding<>()
        {
            {
                bind( m_RangeBar.hoverProperty() );
            }

            /**
             *  {@inheritDoc}
             */
            @Override
            protected final Cursor computeValue()
            {
                return m_RangeBar.isHover() ? Cursor.HAND : Cursor.DEFAULT;
            }
        } );
        m_RangeBar.getStyleClass().setAll( "range-bar" );

        m_RangeBar.setOnMousePressed( e ->
        {
            m_RangeBar.requestFocus();
            m_PreDragPos = isHorizontal() ? e.getX() : -e.getY();
        } );

        m_RangeBar.setOnMouseDragged( e ->
        {
            final var delta = (isHorizontal() ? e.getX() : -e.getY()) - m_PreDragPos;
            moveRange( delta );
        } );

        m_RangeBar.setOnMouseReleased( $ -> confirmRange() );
        getChildren().add( m_RangeBar );
    }   //  initRangeBar()

    /**
     *  Checks whether the orientation of the
     *  {@link RangeSlider}
     *  is
     *  {@linkplain Orientation#HORIZONTAL horizontal}.
     *
     *  @return {@code true} if the orientation is
     *      {@link Orientation#HORIZONTAL},
     *      {@code false} when it is
     *      {@link Orientation#VERTICAL}.
     */
    private final boolean isHorizontal() { return isNull( m_Orientation ) || m_Orientation == HORIZONTAL; }

    /**
     *  {@inheritDoc}
     */
    @SuppressWarnings( {"OverlyComplexMethod", "MagicNumber"} )
    @Override
    protected final void layoutChildren( final double contentX, final double contentY, final double contentWidth, final double contentHeight )
    {
        //---* Resize thumb to preferred size *--------------------------------
        m_ThumbWidth = m_LowThumb.prefWidth( USE_COMPUTED_SIZE );
        m_ThumbHeight = m_LowThumb.prefHeight( USE_COMPUTED_SIZE );
        m_LowThumb.resize( m_ThumbWidth, m_ThumbHeight );

        /*
         * We assume there is a common radius for all corners on the track.
         */
        final var trackRadius = isNull( m_Track.getBackground() )
            ? 0.0
            : m_Track.getBackground().getFills().isEmpty()
                ? 0.0
                : m_Track.getBackground().getFills().getFirst().getRadii().getTopLeftHorizontalRadius();

        if( isHorizontal() )
        {
            final var tickLineHeight = m_ShowTickMarks ? m_TickLine.prefHeight( USE_COMPUTED_SIZE ) : 0.0;
            final var trackHeight = m_Track.prefHeight( USE_COMPUTED_SIZE );
            final var trackAreaHeight = max( trackHeight, m_ThumbHeight );
            final var totalHeightNeeded = trackAreaHeight  + ((m_ShowTickMarks) ? m_TrackToTickGap + tickLineHeight : 0.0);

            //---* Vertically center slider in available height *--------------
            final var startY = contentY + ((contentHeight - totalHeightNeeded) / 2.0);

            m_TrackLength = contentWidth - m_ThumbWidth;
            m_TrackStart = contentX + (m_ThumbWidth / 2.0);
            @SuppressWarnings( "NumericCastThatLosesPrecision" )
            final var trackTop = (double) ((int) (startY + ((trackAreaHeight - trackHeight) / 2.0)));
            //noinspection NumericCastThatLosesPrecision
            m_LowThumbPos = (double) ((int) (startY + ((trackAreaHeight - m_ThumbHeight) / 2.0)));

            positionLowThumb();

            //---* Now do the layout for the track *---------------------------
            m_Track.resizeRelocate( m_TrackStart - trackRadius, trackTop , m_TrackLength + trackRadius + trackRadius, trackHeight );

            positionHighThumb();

            //---* Do the range bar layout *-----------------------------------
            m_RangeBar.resizeRelocate( m_RangeStart, trackTop, m_RangeEnd - m_RangeStart, trackHeight );

            //---* Do the layout for the tick line *---------------------------
            if( m_ShowTickMarks )
            {
                m_TickLine.setLayoutX( m_TrackStart );
                m_TickLine.setLayoutY( trackTop + trackHeight + m_TrackToTickGap );
                m_TickLine.resize( m_TrackLength, tickLineHeight );
                m_TickLine.requestAxisLayout();
            }
            else
            {
                if( nonNull( m_TickLine ) )
                {
                    m_TickLine.resize(0,0 );
                    m_TickLine.requestAxisLayout();
                }
                m_TickLine = null;
            }
        }
        else
        {
            final var tickLineWidth = m_ShowTickMarks ? m_TickLine.prefWidth( USE_COMPUTED_SIZE ) : 0.0;
            final var trackWidth = m_Track.prefWidth( USE_COMPUTED_SIZE );
            final var trackAreaWidth = max( trackWidth, m_ThumbWidth );
            final var totalWidthNeeded = trackAreaWidth  + (m_ShowTickMarks ? m_TrackToTickGap + tickLineWidth : 0.0) ;

            //---* Horizontally center the slider in available width *---------
            final var startX = contentX + ((contentWidth - totalWidthNeeded) / 2.0);
            m_TrackLength = contentHeight - m_ThumbHeight;
            m_TrackStart = contentY + (m_ThumbHeight / 2.0);
            @SuppressWarnings( "NumericCastThatLosesPrecision" )
            final var trackLeft = (double) ((int) (startX + ((trackAreaWidth - trackWidth) / 2.0)));
            //noinspection NumericCastThatLosesPrecision
            m_LowThumbPos = (double) ((int) (startX + ((trackAreaWidth - m_ThumbWidth) / 2.0)));

            positionLowThumb();

            //---* Now do the layout for the track *---------------------------
            m_Track.resizeRelocate( trackLeft, m_TrackStart - trackRadius, trackWidth, m_TrackLength + trackRadius + trackRadius );

            positionHighThumb();

            //---* Do the range bar layout *-----------------------------------
            m_RangeBar.resizeRelocate( trackLeft, m_RangeStart, trackWidth, m_RangeEnd - m_RangeStart );

            //---* Do the layout for the tick line *---------------------------
            if( m_ShowTickMarks )
            {
                m_TickLine.setLayoutX( trackLeft + trackWidth + m_TrackToTickGap );
                m_TickLine.setLayoutY( m_TrackStart );
                m_TickLine.resize( tickLineWidth, m_TrackLength );
                m_TickLine.requestAxisLayout();
            }
            else
            {
                if( nonNull( m_TickLine ) )
                {
                    m_TickLine.resize( 0,0 );
                    m_TickLine.requestAxisLayout();
                }
                m_TickLine = null;
            }
        }
    }   //  layoutChildren()

    /**
     *  Updates the
     *  {@linkplain RangeSlider#lowValueProperty() low value}
     *  based on the new position of the low thumb after it was dragged with
     *  the mouse.
     *
     *  @param  ignoredMouseEvent   The mouse event.
     *  @param  position    The mouse position on the track with 0.0 being the
     *      start of the track and 1.0 being the end.
     */
    public final void lowThumbDragged( final MouseEvent ignoredMouseEvent, final double position )
    {
        final var rangeSlider = getSkinnable();
        final var newValue = clamp
            (
                rangeSlider.getMin(),
                (position * (rangeSlider.getMax() - rangeSlider.getMin())) + rangeSlider.getMin(),
                rangeSlider.getMax()
            );
        rangeSlider.setLowValue( newValue );
    }   //  lowThumbDragged()

    /**
     *  <p>{@summary Prepares the dragging of the low thumb.}</p>
     *  <p>When the low thumb is released,
     *  {@link RangeSlider#lowValueChangingProperty()}
     *  is set to {@code false}.</p>
     *
     *  @param  ignoredMouseEvent   The mouse event.
     *  @param  ignoredPosition The new position.
     */
    public void lowThumbPressed( final MouseEvent ignoredMouseEvent, final double ignoredPosition )
    {
        //---* If not already focused, request focus *-------------------------
        final var rangeSlider = getSkinnable();
        if( !rangeSlider.isFocused() )  rangeSlider.requestFocus();

        rangeSlider.setLowValueChanging( true );
    }   //  lowThumbPressed()

    /**
     *  Adjusts the
     *  {@linkplain RangeSlider#lowValueProperty() low value}
     *  when the low thumb is released.
     *
     *  @param  mouseEvent  The mouse event.
     */
    public final void lowThumbReleased( final MouseEvent mouseEvent )
    {
        final var rangeSlider = getSkinnable();
        if( rangeSlider.isSnapToTicks() )
        {
            rangeSlider.setLowValue( snapValueToTicks( rangeSlider.getLowValue() ) );
        }
        rangeSlider.setLowValueChanging( false );
    }   //  lowThumbReleased()

    /**
     *  Calculates the minimum length for the track.
     *
     *  @return The minimum track length.
     */
    private final double minTrackLength()
    {
        final var retValue = 2.0 * m_LowThumb.prefWidth( USE_COMPUTED_SIZE );

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  minTrackLength()

    /**
     *  Sets the new positions after a move of the range bar.
     *
     *  @param  position    The new position.
     */
    private final void moveRange( final double position )
    {
        final var rangeSlider = getSkinnable();
        final var min = rangeSlider.getMin();
        final var max = rangeSlider.getMax();
        final var lowValue = rangeSlider.getLowValue();
        final var newLowValue = clamp
            (
                min,
                lowValue + position * (max-min) / (isHorizontal() ? rangeSlider.getWidth(): rangeSlider.getHeight()),
                max
            );
        final var highValue = rangeSlider.getHighValue();
        final var newHighValue = clamp
            (
                min,
                highValue + position * (max-min) / (isHorizontal() ? rangeSlider.getWidth(): rangeSlider.getHeight() ),
                max
            );

        if( (newLowValue > min) && (newHighValue < max) )
        {
            rangeSlider.setLowValueChanging( true );
            rangeSlider.setHighValueChanging( true );
            rangeSlider.setLowValue( newLowValue);
            rangeSlider.setHighValue( newHighValue);
        }
    }   //  moveRange()

    /**
     *  Called whenever either
     *  {@link RangeSlider#getMin() min},
     *  {@link RangeSlider#getMax() max}
     *  or
     *  {@link RangeSlider#getHighValue() highValue}
     *  has changed, so that high thumb's
     *  {@link ThumbPane#setLayoutX(double)}
     *  and
     *  {@link ThumbPane#setLayoutY(double)}
     *  is recomputed.
     */
    private final void positionHighThumb()
    {
        final var rangeSlider = getSkinnable();
        final var horizontal = getSkinnable().getOrientation() == HORIZONTAL;

        final var thumbWidth = m_LowThumb.getWidth();
        final var thumbHeight = m_LowThumb.getHeight();
        m_HighThumb.resize( thumbWidth, thumbHeight );

        final var pad = 0.0d;
        var trackStart = horizontal ? m_Track.getLayoutX() : m_Track.getLayoutY();
        trackStart += pad;
        var trackLength = horizontal ? m_Track.getWidth() : m_Track.getHeight();
        trackLength -= 2 * pad;

        @SuppressWarnings( "OverlyComplexArithmeticExpression" )
        final var lx = horizontal
            ? trackStart + (trackLength * ((rangeSlider.getHighValue() - rangeSlider.getMin()) / (getMaxMinusMinNoZero())) - thumbWidth / 2.0 )
            : m_LowThumb.getLayoutX();
        final var ly = horizontal
            ? m_LowThumb.getLayoutY()
            : (getSkinnable().getInsets().getTop() + trackLength) - trackLength * ((rangeSlider.getHighValue() - rangeSlider.getMin()) / (getMaxMinusMinNoZero()));
        m_HighThumb.setLayoutX( lx );
        m_HighThumb.setLayoutY( ly );
        if( horizontal )
        {
            m_RangeEnd = lx;
        }
        else
        {
            m_RangeStart = ly + thumbHeight;
        }
    }   //  positionHighThumb()

    /**
     *  Called whenever either
     *  {@link RangeSlider#getMin() min},
     *  {@link RangeSlider#getMax() max}
     *  or
     *  {@link RangeSlider#getLowValue() lowValue}
     *  has changed, so that low thumb's
     *  {@link ThumbPane#setLayoutX(double)}
     *  and
     *  {@link ThumbPane#setLayoutY(double)}
     *  is recomputed.
     */
    private final void positionLowThumb()
    {
        final var rangeSlider = getSkinnable();
        final var horizontal = isHorizontal();
        @SuppressWarnings( "OverlyComplexArithmeticExpression" )
        final var lx = horizontal
            ? m_TrackStart + (((m_TrackLength * ((rangeSlider.getLowValue() - rangeSlider.getMin()) / (getMaxMinusMinNoZero()))) - m_ThumbWidth / 2.0))
            : m_LowThumbPos;
        final var ly = horizontal
            ? m_LowThumbPos
            : getSkinnable().getInsets().getTop() + m_TrackLength - (m_TrackLength * ((rangeSlider.getLowValue() - rangeSlider.getMin()) / getMaxMinusMinNoZero()));
        m_LowThumb.setLayoutX( lx );
        m_LowThumb.setLayoutY( ly );
        if( horizontal )
        {
            m_RangeStart = lx + m_ThumbWidth;
        }
        else
        {
            m_RangeEnd = ly;
        }
    }   //  positionLowThumb()

    /**
     *  Implements the inverted orientation.
     *
     *  @param  node    A reference to the node.
     *  @param  rtlMethod   The function that has to be used for an orientation
     *      from right to left.
     *  @param  nonRtlMethod    The function that has to be used for an
     *      orientation from left to right.
     */
    private final void rtl( final RangeSlider node, final Runnable rtlMethod, final Runnable nonRtlMethod )
    {
        switch( requireNonNullArgument( node, "node" ).getEffectiveNodeOrientation() )
        {
            case null -> throw new IllegalStateException( "Effective node orientation is null" );
            case RIGHT_TO_LEFT -> requireNonNullArgument( rtlMethod, "rtlMethod" ).run();
            case LEFT_TO_RIGHT -> requireNonNullArgument( nonRtlMethod, "nonRtlMethod" ).run();
            default -> throw new IllegalArgumentException( "Unexpected node orientation: %s".formatted( node.getEffectiveNodeOrientation().name() ) );
        }
    }   //  rtl()

    /**
     *  Set up a callback to indicate which thumb is currently selected
     *  (via enum).
     *
     *  @param  callback    The callback.
     */
    private void setSelectedValue( final Callback<Void,FocusedChild> callback ) { m_SelectedValue = callback; }

    /**
     *  <p>{@summary Shows tick marks and their labels.}</p>
     *  <p>When ticks or labels change their visibility, we have to compute the
     *  new visibility and to add the necessary objects. After this method
     *  returns, we must be sure to add the high thumb and the range bar.</p>
     *
     *  @param  ticksVisible    {@code true} if the tick marks are visible,
     *      {@code false} if not.
     *  @param  labelsVisible   {@code true} if the tick labels are visible,
     *      {@code false} if not.
     */
    private void setShowTickMarks( final boolean ticksVisible, final boolean labelsVisible)
    {
        m_ShowTickMarks = (ticksVisible || labelsVisible);
        final var rangeSlider = getSkinnable();
        if( m_ShowTickMarks )
        {
            if( isNull( m_TickLine ) )
            {
                m_TickLine = new NumberAxis();
                m_TickLine.setFocusTraversable( false );
                m_TickLine.tickLabelFormatterProperty().bind( getSkinnable().labelFormatterProperty() );
                m_TickLine.setAnimated( false );
                m_TickLine.setAutoRanging( false );
                m_TickLine.setSide( isHorizontal() ? Side.BOTTOM : Side.RIGHT );
                m_TickLine.setUpperBound( rangeSlider.getMax() );
                m_TickLine.setLowerBound( rangeSlider.getMin() );
                m_TickLine.setTickUnit( rangeSlider.getMajorTickUnit() );
                m_TickLine.setTickMarkVisible( ticksVisible );
                m_TickLine.setTickLabelsVisible( labelsVisible );
                m_TickLine.setMinorTickVisible( ticksVisible );

                /*
                 * We add 1 to the slider minor tick count since the axis draws
                 * one less minor ticks than the number given.
                 */
                m_TickLine.setMinorTickCount( Integer.max( rangeSlider.getMinorTickCount(), 0 ) + 1) ;
                getChildren().clear();
                getChildren().addAll( m_TickLine, m_Track, m_LowThumb );
            }
            else
            {
                m_TickLine.setTickLabelsVisible( labelsVisible );
                m_TickLine.setTickMarkVisible( ticksVisible );
                m_TickLine.setMinorTickVisible( ticksVisible );
            }
        }
        else
        {
            getChildren().clear();
            getChildren().addAll( m_Track, m_LowThumb );
            // m_TickLine = null;
        }

        getSkinnable().requestLayout();
    }   //  setShowTickMarks()

    /**
     *  Adjusts the position of a thumb to the nearest tick mark.
     *
     *  @param  calculatedPosition  The calculated raw position.
     *  @return The adjusted position.
     */
    private final double snapValueToTicks( final double calculatedPosition )
    {
        final var rangeSlider = getSkinnable();
        var input = calculatedPosition;
        final var d2 = (rangeSlider.getMinorTickCount() != 0)
            ? rangeSlider.getMajorTickUnit() / (max( (double) rangeSlider.getMinorTickCount(), 0.0 ) + 1)
            : rangeSlider.getMajorTickUnit();
        @SuppressWarnings( "NumericCastThatLosesPrecision" )
        final var leftTicks = (int) ((input - rangeSlider.getMin()) / d2);
        final var d3 = (double) leftTicks * d2 + rangeSlider.getMin();
        final var d4 = (double) (leftTicks + 1) * d2 + rangeSlider.getMin();
        input = nearest( d3, input, d4) ;
        final var retValue = clamp( rangeSlider.getMin(), input, rangeSlider.getMax() );

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  snapValueToTicks(

    /**
     *  Invoked by the
     *  {@link RangeSlider}'s
     *  {@link Skin}
     *  implementation whenever a mouse press occurs on the &quot;track&quot;
     *  of the slider. This will cause the thumb to be moved by some amount.
     *
     *  @param  ignoredMouseEvent   The mouse event.
     *  @param  position    The relative mouse position on the track, with 0.0
     *      being the start of the track and 1.0 being the end.
     */
    private final void trackPress( final MouseEvent ignoredMouseEvent, final double position )
    {

        final var rangeSlider = getSkinnable();
        //---* If not already focused, request focus *-------------------------
        if( !rangeSlider.isFocused() )  rangeSlider.requestFocus();

        if( nonNull( m_SelectedValue ) )
        {
            /*
             * Determine the percentage of the way between min and max
             * represented by this mouse event.
             */
            final double newPosition;
            if( isHorizontal() )
            {
                newPosition = position * (rangeSlider.getMax() - rangeSlider.getMin()) + rangeSlider.getMin();
            }
            else
            {
                newPosition = (1 - position) * (rangeSlider.getMax() - rangeSlider.getMin()) + rangeSlider.getMin();
            }

            /*
             * If the position is inferior to the current LowValue, this means
             * the user clicked on the track to move the low thumb. If not,
             * then it means the user wanted to move the high thumb.
             */
            if( newPosition < rangeSlider.getLowValue() )
            {
                rangeSlider.adjustLowValue( newPosition );
            }
            else
            {
                rangeSlider.adjustHighValue(newPosition);
            }
        }
    }   //  trackPress()
}
//  class RangeSliderSkin

/*
 *  End of File
 */