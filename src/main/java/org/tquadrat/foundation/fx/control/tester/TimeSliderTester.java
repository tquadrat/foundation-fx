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

import java.time.OffsetDateTime;

import org.apiguardian.api.API;
import org.tquadrat.foundation.annotation.ClassVersion;
import org.tquadrat.foundation.annotation.ProgramClass;
import org.tquadrat.foundation.fx.control.RangeSlider;
import org.tquadrat.foundation.fx.control.TimeSlider;
import org.tquadrat.foundation.fx.control.TimeSlider.Granularity;
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
 *  {@link TimeSlider}.
 *
 *  @extauthor Thomas Thrien - thomas.thrien@tquadrat.org
 *  @version $Id: TimeSliderTester.java 1114 2024-03-12 23:07:59Z tquadrat $
 *  @since 0.4.6
 *
 *  @UMLGraph.link
 */
@SuppressWarnings( "UseOfSystemOutOrSystemErr" )
@ClassVersion( sourceVersion = "$Id: TimeSliderTester.java 1114 2024-03-12 23:07:59Z tquadrat $" )
@API( status = EXPERIMENTAL, since = "0.4.6" )
@ProgramClass
public final class TimeSliderTester extends Application
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
     *  Creates a new instance of {@code TimeSliderTester}.
     */
    public TimeSliderTester() { /* Just exists */ }

        /*---------*\
    ====** Methods **==========================================================
        \*---------*/
    /**
     *  Creates a simple
     *  {@link RangeSlider}.
     *
     *  @param  root    The parent node.
     */
    private final void createAdvancedTimeSlider( final Pane root )
    {
        final var lowValue = OffsetDateTime.now()
            .withHour( 8 )
            .withMinute( 0 )
            .withSecond( 0 )
            .withNano( 0 )
            .toOffsetTime();
        final var highValue = OffsetDateTime.now()
            .withHour( 17 )
            .withMinute( 0 )
            .withSecond( 0 )
            .withNano( 0 )
            .toOffsetTime();

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

        final var timeSlider = new TimeSlider();

        timeSlider.setLowValue( lowValue );
        timeSlider.setHighValue( highValue );
        timeSlider.setGranularity( Granularity.ONE_HOUR );

        timeSlider.setMinWidth( 600.0 );
        timeSlider.setPrefWidth( USE_COMPUTED_SIZE );
        timeSlider.setMaxWidth( Double.MAX_VALUE );
        sliderBox.getChildren().add( timeSlider );
        min.textProperty().set( format( "%s", timeSlider.getMin() ) );
        max.textProperty().set( format( "%s", timeSlider.getMax() ) );

        final var fieldBox = new HBox();
        fieldBox.setSpacing( m_Spacing );
        fieldBox.setPadding( new Insets( m_Spacing ) );
        root.getChildren().add( fieldBox );

        final var low = new TextField();
        final var lowValueBinding = createStringBinding( () -> format( "%s", timeSlider.getLowValue() ), timeSlider.lowValueProperty() );
        low.textProperty().bind( lowValueBinding );
        final var range = new TextField();
        final var durationBinding = createStringBinding( () -> format( "%s", timeSlider.getDuration() ), timeSlider.durationProperty() );
        range.textProperty().bind( durationBinding );
        final var high = new TextField();
        final var highValueBinding = createStringBinding( () -> format( "%s", timeSlider.getHighValue() ), timeSlider.highValueProperty() );
        high.textProperty().bind( highValueBinding );
        fieldBox.getChildren().addAll( low, range, high );
    }   //  createAdvanceRangeSlider()

    /**
     *  Creates a simple
     *  {@link RangeSlider}.
     *
     *  @param  root    The parent node.
     */
    private final void createDefaultTimeSlider( final Pane root )
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

        final var timeSlider = new TimeSlider();

        timeSlider.setMinWidth( 600.0 );
        timeSlider.setPrefWidth( USE_COMPUTED_SIZE );
        timeSlider.setMaxWidth( Double.MAX_VALUE );
        sliderBox.getChildren().add( timeSlider );
        min.textProperty().set( format( "%s", timeSlider.getMin() ) );
        max.textProperty().set( format( "%s", timeSlider.getMax() ) );

        final var fieldBox = new HBox();
        fieldBox.setSpacing( m_Spacing );
        fieldBox.setPadding( new Insets( m_Spacing ) );
        root.getChildren().add( fieldBox );

        final var low = new TextField();
        final var lowValueBinding = createStringBinding( () -> format( "%s", timeSlider.getLowValue() ), timeSlider.lowValueProperty() );
        low.textProperty().bind( lowValueBinding );
        final var range = new TextField();
        final var durationBinding = createStringBinding( () -> format( "%s", timeSlider.getDuration() ), timeSlider.durationProperty() );
        range.textProperty().bind( durationBinding );
        final var high = new TextField();
        final var highValueBinding = createStringBinding( () -> format( "%s", timeSlider.getHighValue() ), timeSlider.highValueProperty() );
        high.textProperty().bind( highValueBinding );
        fieldBox.getChildren().addAll( low, range, high );
    }   //  createSimpleRangeSlider()

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

        createDefaultTimeSlider( root );
        createAdvancedTimeSlider( root );

        final var scene = new Scene( root, -1, -1 );
        primaryStage.setScene( scene );
        primaryStage.centerOnScreen();
        primaryStage.setOnCloseRequest( $ -> out.println( "Done!" ) );

        //---* Show the stage *--------------------------------------------
        primaryStage.show();
    }   //  start()
}
//  class TimeSliderTester

/*
 *  End of File
 */