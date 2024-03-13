/*
 * ============================================================================
 * Copyright © 2002-2024 by Thomas Thrien.
 * All Rights Reserved.
 * ============================================================================
 * Licensed to the public under the agreements of the GNU Lesser General Public
 * License, version 3.0 (the "License"). You may obtain a copy of the License at
 *
 *      http://www.gnu.org/licenses/lgpl.html
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */

package org.tquadrat.foundation.fx.control.tester;

import static java.lang.String.format;
import static java.lang.System.err;
import static java.lang.System.out;
import static javafx.beans.binding.Bindings.createStringBinding;
import static javafx.scene.layout.Region.USE_COMPUTED_SIZE;
import static org.apiguardian.api.API.Status.EXPERIMENTAL;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;

import org.apiguardian.api.API;
import org.tquadrat.foundation.annotation.ClassVersion;
import org.tquadrat.foundation.annotation.ProgramClass;
import org.tquadrat.foundation.fx.control.RangeSlider;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 *  Test bed for the custom control
 *  {@link RangeSlider}.
 *
 *  @extauthor Thomas Thrien - thomas.thrien@tquadrat.org
 *  @version $Id: RangeSliderTester.java 1113 2024-03-12 02:01:14Z tquadrat $
 *  @since 0.4.6
 *
 *  @UMLGraph.link
 */
@SuppressWarnings( "UseOfSystemOutOrSystemErr" )
@ClassVersion( sourceVersion = "$Id: RangeSliderTester.java 1113 2024-03-12 02:01:14Z tquadrat $" )
@API( status = EXPERIMENTAL, since = "0.4.6" )
@ProgramClass
public final class RangeSliderTester extends Application
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
    /**
     *  The spacing.
     */
     private final double m_Spacing = 7.5;

        /*------------------------*\
    ====** Static Initialisations **===========================================
        \*------------------------*/
    /**
     *  The flag that tracks the assertion on/off status for this package.
     */
    private static boolean m_AssertionOn;

    static
    {
        //---* Determine the assertion status *--------------------------------
        m_AssertionOn = false;
        //noinspection AssertWithSideEffects,PointlessBooleanExpression,NestedAssignment
        assert (m_AssertionOn = true) == true : "Assertion is switched off";
    }

        /*--------------*\
    ====** Constructors **=====================================================
        \*--------------*/
    /**
     *  Creates a new instance of {@code RangeSliderTester}.
     */
    public RangeSliderTester() { /* Just exists */ }

        /*---------*\
    ====** Methods **==========================================================
        \*---------*/
    /**
     *  Creates a simple
     *  {@link RangeSlider}.
     *
     *  @param  root    The parent node.
     */
    private final void createSimpleRangeSlider( final Pane root )
    {
        final var paramBox = new HBox();
        paramBox.setSpacing( m_Spacing );
        paramBox.setPadding( new Insets( m_Spacing ) );
        root.getChildren().add( paramBox );
        final var min = new TextField();
        final var max = new TextField();
        paramBox.getChildren().addAll( min, max );

        final var sliderBox = new HBox();
        sliderBox.setSpacing( m_Spacing );
        sliderBox.setPadding( new Insets( m_Spacing ) );
        root.getChildren().add( sliderBox );

        final var rangeSlider = new RangeSlider( 0.0, 100.0, 25.0, 75.0 );
        rangeSlider.setMinWidth( 600.0 );
        rangeSlider.setPrefWidth( USE_COMPUTED_SIZE );
        rangeSlider.setMaxWidth( Double.MAX_VALUE );
        rangeSlider.setMajorTickUnit( 10.0 );
        rangeSlider.setMinorTickCount( 9 );
        rangeSlider.setSnapToTicks( true );
        rangeSlider.setShowTickLabels( true );
        rangeSlider.setShowTickMarks( true );
        sliderBox.getChildren().add( rangeSlider );
        min.textProperty().set( format( "%.2f", rangeSlider.getMin() ) );
        max.textProperty().set( format( "%.2f", rangeSlider.getMax() ) );

        final var fieldBox = new HBox();
        fieldBox.setSpacing( m_Spacing );
        fieldBox.setPadding( new Insets( m_Spacing ) );
        root.getChildren().add( fieldBox );

        final var low = new TextField();
        final var lowValueBinding = createStringBinding( () -> format( "%.2f", rangeSlider.getLowValue() ), rangeSlider.lowValueProperty() );
        low.textProperty().bind( lowValueBinding );
        final var range = new TextField();
        final var rangeValueBinding = createStringBinding( () -> format( "%.2f", rangeSlider.getHighValue() - rangeSlider.getLowValue() ), rangeSlider.lowValueProperty(), rangeSlider.highValueProperty() );
        range.textProperty().bind( rangeValueBinding );
        final var high = new TextField();
        final var highValueBinding = createStringBinding( () -> format( "%.2f", rangeSlider.getHighValue() ), rangeSlider.highValueProperty() );
        high.textProperty().bind( highValueBinding );
        fieldBox.getChildren().addAll( low, range, high );
    }   //  createSimpleRangeSlider()

    /**
     *  Creates a simple
     *  {@link RangeSlider}.
     *
     *  @param  root    The parent node.
     */
    private final void createTimeRangeSlider( final Pane root )
    {
        final var paramBox = new HBox();
        paramBox.setSpacing( m_Spacing );
        paramBox.setPadding( new Insets( m_Spacing ) );
        root.getChildren().add( paramBox );
        final var min = new TextField();
        final var max = new TextField();
        paramBox.getChildren().addAll( min, max );

        final var sliderBox = new HBox();
        sliderBox.setSpacing( m_Spacing );
        sliderBox.setPadding( new Insets( m_Spacing ) );
        root.getChildren().add( sliderBox );

        final var day = LocalDate.now();
        final var timeZone = ZoneId.systemDefault();
        final var timeFormatter = new DateTimeFormatterBuilder()
            .appendValue( ChronoField.HOUR_OF_DAY, 2 )
            .appendLiteral( ':' )
            .appendValue( ChronoField.MINUTE_OF_HOUR, 2 )
            .toFormatter();

        final var stringConverter = new javafx.util.StringConverter<Number>()
        {
            /**
             *  {@inheritDoc}
             */
            @Override
            public final Number fromString( final String s ) { return 0.0D; }

            /**
             *  {@inheritDoc}
             */
            @Override
            public final String toString( final Number number )
            {
                final var time = LocalTime.ofSecondOfDay( number.intValue() * 60 );
                final var offsetDateTime = ZonedDateTime.of( day, time, timeZone ).toOffsetDateTime();
                final var retValue = timeFormatter.format( offsetDateTime );

                //---* Done *----------------------------------------------------------
                return retValue;
            }   //  toString()
        };

        final var rangeSlider = new RangeSlider( 0.0, 24.0 * 60.0 - 0.01, 8.0 * 60.0, 17.0 * 60.0 );
        rangeSlider.setMinWidth( 600.0 );
        rangeSlider.setPrefWidth( USE_COMPUTED_SIZE );
        rangeSlider.setMaxWidth( Double.MAX_VALUE );
        rangeSlider.setMajorTickUnit( 60.0 );
        rangeSlider.setMinorTickCount( 3 );
        rangeSlider.setSnapToTicks( true );
        rangeSlider.setShowTickLabels( true );
        rangeSlider.setShowTickMarks( true );
        rangeSlider.setLabelFormatter( stringConverter );
        sliderBox.getChildren().add( rangeSlider );
        min.textProperty().set( format( "%.2f", rangeSlider.getMin() ) );
        max.textProperty().set( format( "%.2f", rangeSlider.getMax() ) );

        final var fieldBox = new HBox();
        fieldBox.setSpacing( m_Spacing );
        fieldBox.setPadding( new Insets( m_Spacing ) );
        root.getChildren().add( fieldBox );

        final var low = new TextField();
        final var lowValueBinding = createStringBinding( () -> stringConverter.toString( rangeSlider.getLowValue() ), rangeSlider.lowValueProperty() );
        low.textProperty().bind( lowValueBinding );
        final var range = new TextField();
        final var rangeValueBinding = createStringBinding( () -> stringConverter.toString( rangeSlider.getHighValue() - rangeSlider.getLowValue() ), rangeSlider.lowValueProperty(), rangeSlider.highValueProperty() );
        range.textProperty().bind( rangeValueBinding );
        final var high = new TextField();
        final var highValueBinding = createStringBinding( () -> stringConverter.toString( rangeSlider.getHighValue() ), rangeSlider.highValueProperty() );
        high.textProperty().bind( highValueBinding );
        fieldBox.getChildren().addAll( low, range, high );
    }   //  createTimeRangeSlider()

    /**
     *  The program entry point.
     *
     *  @param  args    The command line arguments.
     */
    public static final void main( final String... args )
    {
        out.printf( "Assertion is %s%n".formatted( m_AssertionOn ? "ON" : "OFF" ) );

        try
        {
            launch( args );
        }
        catch( final Throwable t )
        {
            t.printStackTrace( err );
        }
    }   //  main()

    /**
     *  {@inheritDoc}
     */
    @SuppressWarnings( "ProhibitedExceptionDeclared" )
    @Override
    public void start( final Stage primaryStage ) throws Exception
    {
        //---* Compose the scene *---------------------------------------------
        final var root = new VBox();

        createSimpleRangeSlider( root );
        createTimeRangeSlider( root );

        final var scene = new Scene( root, -1, -1 );
        primaryStage.setScene( scene );
        primaryStage.centerOnScreen();
        primaryStage.setOnCloseRequest( $ -> out.println( "Done!" ) );

        //---* Show the stage *--------------------------------------------
        primaryStage.show();
    }   //  start()
}
//  class RangeSliderTester

/*
 *  End of File
 */