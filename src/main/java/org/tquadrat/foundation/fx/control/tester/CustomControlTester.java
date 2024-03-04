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

import static java.lang.System.err;
import static java.lang.System.out;
import static javafx.scene.layout.Region.USE_COMPUTED_SIZE;
import static org.apiguardian.api.API.Status.STABLE;
import static org.tquadrat.foundation.lang.Objects.requireNonNullArgument;
import static org.tquadrat.foundation.lang.Objects.requireNotBlankArgument;
import static org.tquadrat.foundation.util.UniqueIdUtils.timebasedUUID;

import org.apiguardian.api.API;
import org.tquadrat.foundation.annotation.ClassVersion;
import org.tquadrat.foundation.annotation.ProgramClass;
import org.tquadrat.foundation.fx.control.ErrorDisplay;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

/**
 *  Test bed for custom controls.
 *
 *  @extauthor Thomas Thrien - thomas.thrien@tquadrat.org
 *  @version $Id: CustomControlTester.java 1110 2024-03-04 15:26:06Z tquadrat $
 *  @since 0.0.1
 *
 *  @UMLGraph.link
 */
@ClassVersion( sourceVersion = "$Id: CustomControlTester.java 1110 2024-03-04 15:26:06Z tquadrat $" )
@API( status = STABLE, since = "0.0.1" )
@ProgramClass
public final class CustomControlTester extends Application
{
        /*---------------*\
    ====** Inner Classes **====================================================
        \*---------------*/

        /*-----------*\
    ====** Constants **========================================================
        \*-----------*/
    /**
     *  The contents for the message.
     */
    public static final String [] contents = { "one ", "two ", "three ", "four ", "five ", "six ", "seven ", "eight ", "nine ", "ten " };

        /*------------*\
    ====** Attributes **=======================================================
        \*------------*/
    /**
     *  The error display.
     */
    private ErrorDisplay m_ErrorDisplay;

    /**
     *  Counter.
     */
    private int m_Counter = 0;

    /**
     *  The message.
     */
    private final StringBuilder m_Buffer = new StringBuilder( "zero " );

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
     *  Creates a new instance of {@code UserDialogTester}.
     */
    public CustomControlTester() { /* Just exists */ }

        /*---------*\
    ====** Methods **==========================================================
        \*---------*/
    /**
     *  Creates a button and configures it.
     *
     *  @param  text    The button text.
     *  @param  eventHandler    The event handler for the new button.
     *  @return The new button.
     */
    private final Button createButton( final String text, final EventHandler<ActionEvent> eventHandler )
    {
        final var retValue = new Button( requireNotBlankArgument( text, "text" ) );
        retValue.setOnAction( requireNonNullArgument( eventHandler, "eventHandler" ) );

        final var buttonWidth = 100.0;
        final var buttonHeight = 30.0;
        final var spacing = 7.5;

        retValue.setMaxHeight( buttonHeight );
        retValue.setMinHeight( buttonHeight );
        retValue.setPrefHeight( buttonHeight );
        retValue.setMaxWidth( buttonWidth );
        retValue.setMinWidth( buttonWidth );
        retValue.setPrefWidth( buttonWidth );
        retValue.setPadding( new Insets( spacing ) );

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  createButton()

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
     *  Responds to the AddMessage button.
     *
     *  @param  event   The action event.
     */
    private final void onAddMessage( final ActionEvent event )
    {
        final var id = timebasedUUID().toString();
        final var pos = m_Counter++ % contents.length;
        m_Buffer.append( contents [pos] );
        final var message = m_Buffer.toString();
        m_ErrorDisplay.addMessage( id, message );
    }   //  onAddMessage()

    /**
     *  {@inheritDoc}
     */
    @Override
    public void start( final Stage primaryStage ) throws Exception
    {
        //---* Compose the scene *---------------------------------------------
        final var spacing = 7.5;
        final var root = new HBox();
        root.setSpacing( spacing );
        root.setPadding( new Insets( spacing ) );

        m_ErrorDisplay = new ErrorDisplay();
        root.getChildren().add( createButton( "Add Message", this::onAddMessage ) );
        root.getChildren().add( m_ErrorDisplay );
        m_ErrorDisplay.setMinSize( 500.0, 300.0 );
        m_ErrorDisplay.setPrefSize( USE_COMPUTED_SIZE, USE_COMPUTED_SIZE );
        m_ErrorDisplay.setMaxSize( Double.MAX_VALUE, Double.MAX_VALUE );
        m_ErrorDisplay.addMessage( m_Buffer.toString() );
        m_ErrorDisplay.setTooltip( new Tooltip( "The error display" ) );

        final var scene = new Scene( root, -1, -1 );
        primaryStage.setScene( scene );
        primaryStage.centerOnScreen();
        primaryStage.setOnCloseRequest( event -> out.println( "Done!" ) );

        //---* Show the stage *--------------------------------------------
        primaryStage.show();
    }   //  start()
}
//  class CustomControlTester

/*
 *  End of File
 */