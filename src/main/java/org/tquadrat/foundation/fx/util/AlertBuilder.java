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

package org.tquadrat.foundation.fx.util;

import static javafx.scene.control.ButtonType.OK;
import static org.apiguardian.api.API.Status.STABLE;
import static org.tquadrat.foundation.lang.Objects.requireNonNullArgument;

import org.apiguardian.api.API;
import org.tquadrat.foundation.annotation.ClassVersion;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogEvent;
import javafx.scene.control.DialogPane;
import javafx.stage.Modality;
import javafx.stage.StageStyle;
import javafx.stage.Window;
import javafx.util.Callback;

/**
 *  This class provides a fluent API to build an
 *  {@link Alert}.
 *
 *  @extauthor Thomas Thrien - thomas.thrien@tquadrat.org
 *  @version $Id: AlertBuilder.java 1110 2024-03-04 15:26:06Z tquadrat $
 *  @since 0.4.1
 *
 *  @UMLGraph.link
 */
@SuppressWarnings( "ClassWithTooManyMethods" )
@ClassVersion( sourceVersion = "$Id: AlertBuilder.java 1110 2024-03-04 15:26:06Z tquadrat $" )
@API( status = STABLE, since = "0.4.1" )
public class AlertBuilder
{
        /*------------*\
    ====** Attributes **=======================================================
        \*------------*/
    /**
     *  The alert instance to build.
     */
    private final Alert m_Alert;

    /**
     *  Flag that prevents the re-use of the builder.
     */
    private boolean m_IsBuilt;

        /*--------------*\
    ====** Constructors **=====================================================
        \*--------------*/
    /**
     *  Creates a new instance of {@code AlertBuilder}.
     *
     *  @param  type    The type of the new alert.
     */
    public AlertBuilder( final AlertType type )
    {
        m_Alert = new Alert( requireNonNullArgument( type, "type" ) );
        m_IsBuilt = false;
    }   //  AlertBuilder()

    /**
     *  Creates a new instance of {@code AlertBuilder}.
     *
     *  @param  type    The type of the new alert.
     *  @param  owner   The parent window for the alert.
     */
    public AlertBuilder( final AlertType type, final Window owner )
    {
        this( type );
        setOwner( owner );
    }   //  AlertBuilder()

        /*---------*\
    ====** Methods **==========================================================
        \*---------*/
    /**
     *  <p>{@summary Registers an event filter for the new alert.}</p>
     *
     *  @param  <E> The event class of the filer.
     *  @param  eventType   The type of the events received by the filter.
     *  @param  eventFilter The event filter.
     *  @return The builder reference.
     *  @throws IllegalStateException   The method
     *      {@link #build()}
     *      was already called on this builder instance.
     *
     *  @see Alert#addEventFilter(EventType,EventHandler)
     */
    public final <E extends Event> AlertBuilder addEventFilter( final EventType<E> eventType, final EventHandler<? super E> eventFilter ) throws IllegalStateException
    {
        checkIsBuilt();
        m_Alert.addEventFilter( eventType, eventFilter );

        //---* Done *----------------------------------------------------------
        return this;
    }   //  addEventFilter()

    /**
     *  <p>{@summary Registers an even filter for the new alert.}</p>
     *
     *  @param  <E> The event class of the filter.
     *  @param  eventType   The type of the events received by the filter.
     *  @param  eventHandler    The event handler.
     *  @return The builder reference.
     *  @throws IllegalStateException   The method
     *      {@link #build()}
     *      was already called on this builder instance.
     *
     *  @see Alert#addEventHandler(EventType,EventHandler)
     */
    public final <E extends Event> AlertBuilder addEventHandler( final EventType<E> eventType, final EventHandler<? super E> eventHandler ) throws IllegalStateException
    {
        checkIsBuilt();
        m_Alert.addEventHandler( eventType, eventHandler );

        //---* Done *----------------------------------------------------------
        return this;
    }   //  addEventFilter()

    /**
     *  Returns the alert instance.
     *
     *  @return The alert.
     *  @throws IllegalStateException   The method {@code build()} was already
     *      called previously on this builder instance.
     */
    public final Alert build() throws IllegalStateException
    {
        checkIsBuilt();
        m_IsBuilt = true;

        //---* Done *----------------------------------------------------------
        return m_Alert;
    }   //  build()

    /**
     *  Throws an
     *  {@link IllegalStateException}
     *  when
     *  {@link #m_IsBuilt}
     *  is {@code true}.
     *
     *  @throws IllegalStateException
     *      {@link #m_IsBuilt}
     *      is {@code true}.
     */
    private final void checkIsBuilt() throws IllegalStateException
    {
        if( m_IsBuilt ) throw new IllegalStateException( "Illegal Builder state" );
    }   //  checkIsBuilt()

    /**
     *  Builds the alert, calls
     *  {@link Alert#showAndWait() showAndWait()}
     *  on it and returns the result.
     *
     *  @return {@code true} if the alert was terminated with the
     *      {@linkplain ButtonType#OK Ok button}, {@code false} in any other
     *      case.
     *  @throws IllegalStateException   The method
     *      {@link #build()}
     *      was already called on this builder instance.
     *
     *  @since 0.4.2
     */
    @SuppressWarnings( "BooleanMethodNameMustStartWithQuestion" )
    @API( status = STABLE, since = "0.4.1" )
    public final boolean execute() throws IllegalStateException
    {
        final var retValue = build().showAndWait()
            .filter( result -> result == OK )
            .isPresent();

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  execute()

    /**
     *  <p>{@summary Sets the content text for the new alert.}</p>
     *  <p>This operation can be called repeatedly; each consecutive call will
     *  overwrite the value set by the previous one.</p>
     *
     *  @param  s   The content text; can be {@code null}.
     *  @return The builder reference.
     *  @throws IllegalStateException   The method
     *      {@link #build()}
     *      was already called on this builder instance.
     *
     *  @see Alert#setContentText(String)
     */
    public final AlertBuilder setContentText( final String s ) throws IllegalStateException
    {
        checkIsBuilt();
        m_Alert.setContentText( s );

        //---* Done *----------------------------------------------------------
        return this;
    }   //  setContentText()

    /**
     *  <p>{@summary Sets the
     *  {@link DialogPane }
     *  for the new alert.}</p>
     *  <p>This operation can be called repeatedly; each consecutive call will
     *  overwrite the value set by the previous one.</p>
     *
     *  @param  dialogPane   The dialog pane; can be {@code null}.
     *  @return The builder reference.
     *  @throws IllegalStateException   The method
     *      {@link #build()}
     *      was already called on this builder instance.
     *
     *  @see Alert#setDialogPane(DialogPane)
     */
    public final AlertBuilder setDialogPane( final DialogPane dialogPane ) throws IllegalStateException
    {
        checkIsBuilt();
        m_Alert.setDialogPane( dialogPane );

        //---* Done *----------------------------------------------------------
        return this;
    }   //  setDialogPane()

    /**
     *  <p>{@summary Sets the window dimensions for the new alert.}</p>
     *  <p>This operation can be called repeatedly; each consecutive call will
     *  overwrite the values set by the previous one.</p>
     *
     *  @param  width   The window width.
     *  @param  height  The window height.
     *  @return The builder reference.
     *  @throws IllegalStateException   The method
     *      {@link #build()}
     *      was already called on this builder instance.
     *
     *  @see Alert#setHeight(double)
     *  @see Alert#setWidth(double)
     */
    public final AlertBuilder setDimensions( final double width, final double height ) throws IllegalStateException
    {
        setHeight( height );
        setWidth( width );

        //---* Done *----------------------------------------------------------
        return this;
    }   //  setDimensions()

    /**
     *  <p>{@summary Sets the graphics for the new alert.}</p>
     *  <p>This operation can be called repeatedly; each consecutive call will
     *  overwrite the value set by the previous one.</p>
     *
     *  @param  node    The graphics; can be {@code null}.
     *  @return The builder reference.
     *  @throws IllegalStateException   The method
     *      {@link #build()}
     *      was already called on this builder instance.
     *
     *  @see Alert#setGraphic(Node)
     */
    public final AlertBuilder setGraphic( final Node node ) throws IllegalStateException
    {
        checkIsBuilt();
        m_Alert.setGraphic( node );

        //---* Done *----------------------------------------------------------
        return this;
    }   //  setGraphic()

    /**
     *  <p>{@summary Sets the header text for the new alert.}</p>
     *  <p>This operation can be called repeatedly; each consecutive call will
     *  overwrite the value set by the previous one.</p>
     *
     *  @param  s   The text for the header; can be {@code null}.
     *  @return The builder reference.
     *  @throws IllegalStateException   The method
     *      {@link #build()}
     *      was already called on this builder instance.
     *
     *  @see Alert#setHeaderText(String)
     */
    public final AlertBuilder setHeaderText( final String s ) throws IllegalStateException
    {
        checkIsBuilt();
        m_Alert.setHeaderText( s );

        //---* Done *----------------------------------------------------------
        return this;
    }   //  setHeaderText()

    /**
     *  <p>{@summary Sets the window height for the new alert.}</p>
     *  <p>This operation can be called repeatedly; each consecutive call will
     *  overwrite the value set by the previous one.</p>
     *
     *  @param  height  The window height.
     *  @return The builder reference.
     *  @throws IllegalStateException   The method
     *      {@link #build()}
     *      was already called on this builder instance.
     *
     *  @see Alert#setHeight(double)
     */
    public final AlertBuilder setHeight( final double height ) throws IllegalStateException
    {
        checkIsBuilt();
        m_Alert.setHeight( height );

        //---* Done *----------------------------------------------------------
        return this;
    }   //  setHeight()

    /**
     *  <p>{@summary Sets the
     *  {@link Modality }
     *  for the new alert.}</p>
     *
     *  @param  modality    The modality.
     *  @return The builder reference.
     *  @throws IllegalStateException   The method
     *      {@link #build()}
     *      was already called on this builder instance.
     *
     *  @see Alert#initModality(Modality)
     */
    public final AlertBuilder setModality( final Modality modality ) throws IllegalStateException
    {
        checkIsBuilt();
        m_Alert.initModality( requireNonNullArgument( modality, "modality" ) );

        //---* Done *----------------------------------------------------------
        return this;
    }   //  setModality()

    /**
     *  <p>{@summary Sets the 'OnCloseRequest' event handler for the new
     *  alert.}</p>
     *  <p>This operation can be called repeatedly; each consecutive call will
     *  overwrite the value set by the previous one.</p>
     *
     *  @param  eventHandler    The event handler
     *  @return The builder reference.
     *  @throws IllegalStateException   The method
     *      {@link #build()}
     *      was already called on this builder instance.
     *
     *  @see Alert#setOnCloseRequest(EventHandler)
     */
    public final AlertBuilder setOnCloseRequest( final EventHandler<DialogEvent> eventHandler ) throws IllegalStateException
    {
        checkIsBuilt();
        m_Alert.setOnCloseRequest( eventHandler );

        //---* Done *----------------------------------------------------------
        return this;
    }   //  setOnCloseRequest()

    /**
     *  <p>{@summary Sets the 'OnHidden' event handler for the new alert.}</p>
     *  <p>This operation can be called repeatedly; each consecutive call will
     *  overwrite the value set by the previous one.</p>
     *
     *  @param  eventHandler    The event handler
     *  @return The builder reference.
     *  @throws IllegalStateException   The method
     *      {@link #build()}
     *      was already called on this builder instance.
     *
     *  @see Alert#setOnHidden(EventHandler)
     */
    public final AlertBuilder setOnHidden( final EventHandler<DialogEvent> eventHandler ) throws IllegalStateException
    {
        checkIsBuilt();
        m_Alert.setOnHidden( eventHandler );

        //---* Done *----------------------------------------------------------
        return this;
    }   //  setHidden()}

    /**
     *  <p>{@summary Sets the 'OnHiding' event handler for the new alert.}</p>
     *  <p>This operation can be called repeatedly; each consecutive call will
     *  overwrite the value set by the previous one.</p>
     *
     *  @param  eventHandler    The event handler
     *  @return The builder reference.
     *  @throws IllegalStateException   The method
     *      {@link #build()}
     *      was already called on this builder instance.
     *
     *  @see Alert#setOnHiding(EventHandler)
     */
    public final AlertBuilder setOnHiding( final EventHandler<DialogEvent> eventHandler ) throws IllegalStateException
    {
        checkIsBuilt();
        m_Alert.setOnHiding( eventHandler );

        //---* Done *----------------------------------------------------------
        return this;
    }   //  setHiding()}

    /**
     *  <p>{@summary Sets the 'OnShowing' event handler for the new alert.}</p>
     *  <p>This operation can be called repeatedly; each consecutive call will
     *  overwrite the value set by the previous one.</p>
     *
     *  @param  eventHandler    The event handler
     *  @return The builder reference.
     *  @throws IllegalStateException   The method
     *      {@link #build()}
     *      was already called on this builder instance.
     *
     *  @see Alert#setOnShowing(EventHandler)
     */
    public final AlertBuilder setOnShowing( final EventHandler<DialogEvent> eventHandler ) throws IllegalStateException
    {
        checkIsBuilt();
        m_Alert.setOnShowing( eventHandler );

        //---* Done *----------------------------------------------------------
        return this;
    }   //  setOnShowing()

    /**
     *  <p>{@summary Sets the 'OnShown' event handler for the new alert.}</p>
     *  <p>This operation can be called repeatedly; each consecutive call will
     *  overwrite the value set by the previous one.</p>
     *
     *  @param  eventHandler    The event handler
     *  @return The builder reference.
     *  @throws IllegalStateException   The method
     *      {@link #build()}
     *      was already called on this builder instance.
     *
     *  @see Alert#setOnShown(EventHandler)
     */
    public final AlertBuilder setOnShown( final EventHandler<DialogEvent> eventHandler ) throws IllegalStateException
    {
        checkIsBuilt();
        m_Alert.setOnShown( eventHandler );

        //---* Done *----------------------------------------------------------
        return this;
    }   //  setOnShown()

    /**
     *  Sets the parent window for the new alert.
     *
     *  @param  owner   The parent window.
     *  @return The builder reference.
     *  @throws IllegalStateException   The method
     *      {@link #build()}
     *      was already called on this builder instance.
     *
     *  @see Alert#initOwner(Window)
     *
     *  @since 0.4.2
     */
    @API( status = STABLE, since = "0.4.1" )
    public final AlertBuilder setOwner( final Window owner ) throws IllegalStateException
    {
        checkIsBuilt();
        m_Alert.initOwner( owner );

        //---* Done *----------------------------------------------------------
        return this;
    }   //  setOwner()

    /**
     *  <p>{@summary Sets the position for the new alert.}</p>
     *  <p>This operation can be called repeatedly; each consecutive call will
     *  overwrite the values set by the previous one.</p>
     *
     *  @param  x   The x position for the alert window.
     *  @param  y   The y position for the alert window.
     *  @return The builder reference.
     *  @throws IllegalStateException   The method
     *      {@link #build()}
     *      was already called on this builder instance.
     *
     *  @see Alert#setX(double)
     *  @see Alert#setY(double)
     */
    public final AlertBuilder setPos( final double x, final double y ) throws IllegalStateException
    {
        setX( x );
        setY( y );

        //---* Done *----------------------------------------------------------
        return this;
    }   //  setPos()

    /**
     *  <p>{@summary Sets the flag that indicates whether the window for the
     *  new alert can be resized.}</p>
     *  <p>This operation can be called repeatedly; each consecutive call will
     *  overwrite the value set by the previous one.</p>
     *
     *  @param  flag    {@code true} for a resizeable window, _false for a
     *      window with fixed sizes.
     *  @return The builder reference.
     *  @throws IllegalStateException   The method
     *      {@link #build()}
     *      was already called on this builder instance.
     *
     *  @see Alert#setResizable(boolean)
     */
    public final AlertBuilder setResizable( final boolean flag ) throws IllegalStateException
    {
        checkIsBuilt();
        m_Alert.setResizable( flag );

        //---* Done *----------------------------------------------------------
        return this;
    }   //  setResizable()

    /**
     *  <p>{@summary Sets the result converter for the new alert.}</p>
     *  <p>This operation can be called repeatedly; each consecutive call will
     *  overwrite the value set by the previous one.</p>
     *  <p>Setting a result converter may have an impact on the behaviour of
     *  {@link #execute()}.</p>
     *
     *  @param  callback    The result converter; can be {@code null}.
     *  @return The builder reference.
     *  @throws IllegalStateException   The method
     *      {@link #build()}
     *      was already called on this builder instance.
     *
     *  @see Alert#setResultConverter(Callback)
     */
    public final AlertBuilder setResultConverter( final Callback<ButtonType,ButtonType> callback ) throws IllegalStateException
    {
        checkIsBuilt();
        m_Alert.setResultConverter( callback );

        //---* Done *----------------------------------------------------------
        return this;
    }   //  setResultConverter()

    /**
     *  <p>{@summary Sets the
     *  {@linkplain StageStyle style}
     *  for the new alert.}</p>
     *
     *  @param  style   The style.
     *  @return The builder reference.
     *  @throws IllegalStateException   The method
     *      {@link #build()}
     *      was already called on this builder instance.
     *
     *  @see Alert#initStyle(StageStyle)
     */
    public final AlertBuilder setStyle( final StageStyle style ) throws IllegalStateException
    {
        checkIsBuilt();
        m_Alert.initStyle( requireNonNullArgument( style, "style" ) );

        //---* Done *----------------------------------------------------------
        return this;
    }   //  setStyle()

    /**
     *  <p>{@summary Sets the window title for the new alert.}</p>
     *  <p>This operation can be called repeatedly; each consecutive call will
     *  overwrite the value set by the previous one.</p>
     *
     *  @param  s   The window title; can be {@code null}.
     *  @return The builder reference.
     *  @throws IllegalStateException   The method
     *      {@link #build()}
     *      was already called on this builder instance.
     *
     *  @see Alert#setTitle(String)
     */
    public final AlertBuilder setTitle( final String s ) throws IllegalStateException
    {
        checkIsBuilt();
        m_Alert.setTitle( s );

        //---* Done *----------------------------------------------------------
        return this;
    }   //  setTitle()

    /**
     *  <p>{@summary Sets the window width for the new alert.}</p>
     *  <p>This operation can be called repeatedly; each consecutive call will
     *  overwrite the value set by the previous one.</p>
     *
     *  @param  width   The window width.
     *  @return The builder reference.
     *  @throws IllegalStateException   The method
     *      {@link #build()}
     *      was already called on this builder instance.
     *
     *  @see Alert#setWidth(double)
     */
    public final AlertBuilder setWidth( final double width ) throws IllegalStateException
    {
        checkIsBuilt();
        m_Alert.setWidth( width );

        //---* Done *----------------------------------------------------------
        return this;
    }   //  setWidth()

    /**
     *  <p>{@summary Sets the x position for the new alert.}</p>
     *  <p>This operation can be called repeatedly; each consecutive call will
     *  overwrite the value set by the previous one.</p>
     *
     *  @param  x   The x position.
     *  @return The builder reference.
     *  @throws IllegalStateException   The method
     *      {@link #build()}
     *      was already called on this builder instance.
     *
     *  @see Alert#setX(double)
     */
    public final AlertBuilder setX( final double x ) throws IllegalStateException
    {
        checkIsBuilt();
        m_Alert.setX( x );

        //---* Done *----------------------------------------------------------
        return this;
    }   //  setX()

    /**
     *  <p>{@summary Sets the y position for the new alert.}</p>
     *  <p>This operation can be called repeatedly; each consecutive call will
     *  overwrite the value set by the previous one.</p>
     *
     *  @param  y   The y position.
     *  @return The builder reference.
     *  @throws IllegalStateException   The method
     *      {@link #build()}
     *      was already called on this builder instance.
     *
     *  @see Alert#setX(double)
     */
    public final AlertBuilder setY( final double y ) throws IllegalStateException
    {
        checkIsBuilt();
        m_Alert.setY( y );

        //---* Done *----------------------------------------------------------
        return this;
    }   //  setY()
}
//  class AlertBuilder

/*
 *  End of File
 */