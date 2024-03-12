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

package org.tquadrat.foundation.fx.control;

import static javafx.collections.FXCollections.observableMap;
import static javafx.collections.FXCollections.unmodifiableObservableMap;
import static org.apiguardian.api.API.Status.INTERNAL;
import static org.apiguardian.api.API.Status.STABLE;
import static org.tquadrat.foundation.lang.CommonConstants.EMPTY_STRING;
import static org.tquadrat.foundation.lang.Objects.nonNull;
import static org.tquadrat.foundation.lang.Objects.requireNonNullArgument;
import static org.tquadrat.foundation.util.StringUtils.isNotEmptyOrBlank;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;

import org.apiguardian.api.API;
import org.tquadrat.foundation.annotation.ClassVersion;
import org.tquadrat.foundation.fx.control.skin.ErrorDisplaySkin;
import org.tquadrat.foundation.fx.internal.FoundationFXControl;
import javafx.beans.Observable;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.MapProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyMapProperty;
import javafx.beans.property.SimpleMapProperty;
import javafx.collections.ObservableMap;
import javafx.scene.control.Skin;

/**
 *  <p>{@summary A control to display multiple error messages inside a
 *  window.}</p>
 *  <p>The error messages will be added through a call to
 *  {@link #addMessage(String,String) addMessage()}
 *  and they can be removed again by calling
 *  {@link #removeMessage(String) removeMessage()}. The id identifies the
 *  respective message.</p>
 *  <p>The messages are displayed in the sequence they were added.</p>
 *  <p>To automate the display of error messages, you can create a message
 *  trigger, by calling
 *  {@link #addMessageTrigger(String,Supplier,BooleanBinding) addMessageTrigger()}.
 *  The id of the trigger is also the id of the message that is controlled
 *  through the message trigger.</p>
 *  <p>The messages itself are displayed through instances of
 *  {@link javafx.scene.control.Label Label}
 *  that has the CSS Style Class
 *  {@value #STYLE_CLASS_MessageDisplayLabel}.</p>
 *
 *  @note The minimum height for an {@code ErrorDisplay} control is 55.0. The
 *      vertical scrollbar does not work properly for smaller values.
 *
 *  @extauthor Thomas Thrien - thomas.thrien@tquadrat.org
 *  @version $Id: ErrorDisplay.java 1112 2024-03-10 14:16:51Z tquadrat $
 *  @since 0.4.3
 *
 *  @UMLGraph.link
 */
@ClassVersion( sourceVersion = "$Id: ErrorDisplay.java 1112 2024-03-10 14:16:51Z tquadrat $" )
@API( status = STABLE, since = "0.4.3" )
public final class ErrorDisplay extends FoundationFXControl
{
        /*---------------*\
    ====** Inner Classes **====================================================
        \*---------------*/
    /**
     *  <p>{@summary Wraps a
     *  {@link BooleanBinding}
     *  with a message.}</p>
     *  <p>Each time the binding invalidates, it will be verified again and the
     *  error message will be added or removed from the error display.</p>
     *
     *  @extauthor Thomas Thrien - thomas.thrien@tquadrat.org
     *  @version $Id: ErrorDisplay.java 1112 2024-03-10 14:16:51Z tquadrat $
     *  @since 0.4.3
     *
     *  @UMLGraph.link
     */
    @ClassVersion( sourceVersion = "$Id: ErrorDisplay.java 1112 2024-03-10 14:16:51Z tquadrat $" )
    @API( status = INTERNAL, since = "0.4.3" )
    private final class MessageTrigger
    {
            /*------------*\
        ====** Attributes **===================================================
            \*------------*/
        /**
         *  The binding.
         */
        private final BooleanBinding m_Binding;

        /**
         *  The message id.
         */
        private final String m_Id;

        /**
         *  The supplier for the message text.
         */
        private final Supplier<String> m_MessageSupplier;

            /*--------------*\
        ====** Constructors **=================================================
            \*--------------*/
        /**
         *  <p>{@summary Creates a new instance of {@code MessageTrigger}.}</p>
         *  <p>The message will be displayed when the given instance of
         *  {@link BooleanBinding}
         *  evaluates to {@code true}.</p>
         *
         *  @param  id  The message id.
         *  @param  messageSupplier The supplier for the message text.
         *  @param  binding The binding that controls the appearance of the
         *      message.
         */
        public MessageTrigger( final String id, final Supplier<String> messageSupplier, final BooleanBinding binding )
        {
            m_Id = requireNonNullArgument( id, "id" );
            m_MessageSupplier = requireNonNullArgument( messageSupplier, "messageSupplier" );
            m_Binding = requireNonNullArgument( binding, "binding" );

            m_Binding.addListener( this::triggerMessage );
        }   //  MessageTrigger()

            /*---------*\
        ====** Methods **======================================================
            \*---------*/
        /**
         *  Disables this message trigger in preparation of its disposal.
         */
        public final void disable()
        {
            m_Binding.removeListener( this::triggerMessage );
        }   //  disable()

        /**
         *  The invalidation listener that updates the error display.
         *
         *  @param  observable  The observable that became invalid.
         */
        private final void triggerMessage( final Observable observable )
        {
            assert observable == m_Binding;

            if( m_Binding.get() )
            {
                addMessage( m_Id, m_MessageSupplier.get() );
            }
            else
            {
                removeMessage( m_Id );
            }
        }   //  triggerMesssage()
    }
    //  class MessageTrigger

        /*-----------*\
    ====** Constants **========================================================
        \*-----------*/
    /**
     *  The style class for the
     *  {@link javafx.scene.control.Label}
     *  instances that show the messages: {@value}.
     */
    public static final String STYLE_CLASS_MessageDisplayLabel = "errorDisplay";

        /*------------*\
    ====** Attributes **=======================================================
        \*------------*/
    /**
     *  <p>{@summary The error messages to display.}</p>
     *  <p>The key is the message id, the value is the message text; only the
     *  text is shown.</p>
     */
    private final ObservableMap<String,String> m_Messages;

    /**
     *  The property for the messages.
     */
    private final MapProperty<String,String> m_MessagesProperty;

    /**
     *  The message triggers.
     */
    private final Map<String,MessageTrigger> m_MessageTriggers = new HashMap<>();

        /*--------------*\
    ====** Constructors **=====================================================
        \*--------------*/
    /**
     *  Creates a new instance of {@code ErrorDisplay}.
     */
    public ErrorDisplay()
    {
        super();

        //---* Initialise the attributes *-------------------------------------
        final Map<String,String> messages = new LinkedHashMap<>();
        m_Messages = observableMap( messages );
        //noinspection ThisEscapedInObjectConstruction
        m_MessagesProperty = new SimpleMapProperty<>( this, "messages", m_Messages );

        setSkin( createDefaultSkin() );
    }   //  ErrorDisplay()

        /*---------*\
    ====** Methods **==========================================================
        \*---------*/
    /**
     *  Adds a message to display.
     *
     *  @param  id  The id for the message; this allows to remove the message
     *      again later, when the error condition has been removed.
     *  @param  message This is the text to display.
     */
    public final void addMessage( final String id, final String message )
    {
        if( nonNull( id ) )
        {
            if( isNotEmptyOrBlank( message ) )
            {
                m_Messages.put( id, message );
            }
            else
            {
                m_Messages.remove( id );
            }
        }
    }   //  addMessage()

    /**
     *  Adds a message to display, using the empty string as the id for the
     *  message.
     *
     *  @param  message This is the text to display.
     */
    public final void addMessage( final String message )
    {
        addMessage( EMPTY_STRING, message );
    }   //  addMessage()

    /**
     *  <p>{@summary Adds a message trigger.}</p>
     *  <p>The message provided by the given supplier will be displayed when
     *  the given instance of
     *  {@link BooleanBinding}
     *  evaluates to {@code true}.</p>
     *  <p>Use this to control the visibility of a particular message based on
     *  the status of the given binding.</p>
     *
     *  @param  id  The message id.
     *  @param  messageSupplier The supplier for the message text.
     *  @param  binding The binding that controls the appearance of the
     *      message.
     *
     *  @see BooleanBinding#get()
     */
    public final void addMessageTrigger( final String id, final Supplier<String> messageSupplier, final BooleanBinding binding )
    {
        var messageTrigger = m_MessageTriggers.get( requireNonNullArgument( id, "id" ) );
        if( nonNull( messageTrigger ) ) messageTrigger.disable();
        messageTrigger = new MessageTrigger( id, messageSupplier, binding );
        m_MessageTriggers.put( id, messageTrigger );
    }   //  addMessageTrigger()

    /**
     *  <p>{@summary Adds a message trigger, using the empty string as id.}</p>
     *  <p>Use this to control the visibility of a particular message based on
     *  the status of the given binding.</p>
     *
     *  @param  messageSupplier The supplier for the message text.
     *  @param  binding The binding that controls the appearance of the
     *      message.
     */
    public final void addMessageTrigger( final Supplier<String> messageSupplier, final BooleanBinding binding )
    {
        addMessageTrigger( EMPTY_STRING, messageSupplier, binding );
    }   //  addMessageTrigger()

    /**
     *  {@inheritDoc}
     */
    @Override
    protected final Skin<?> createDefaultSkin()
    {
        final var retValue = new ErrorDisplaySkin( this );

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  createDefaultSkin()

    /**
     *  Returns the reference to a boolean property that is {@code true} if
     *  currently no messages are displayed.
     *
     *  @return The property reference.
     */
    public final ReadOnlyBooleanProperty emptyProperty() { return m_MessagesProperty.emptyProperty(); }

    /**
     *  Returns the messages.
     *
     *  @return The messages.
     */
    public final Map<String,String> getMessages() { return unmodifiableObservableMap( m_Messages ); }

    /**
     *  Checks whether there are any messages to display.
     *
     *  @return {@code true} if there are no messages to show, {@code false}
     *      otherwise.
     */
    public final boolean isEmpty() { return m_Messages.isEmpty(); }

    /**
     *  Provides a reference to the messages property.
     *
     *  @return The property reference.
     */
    @SuppressWarnings( "AssignmentOrReturnOfFieldWithMutableType" )
    public final ReadOnlyMapProperty<String,String> messagesProperty() { return m_MessagesProperty; }

    /**
     *  <p>{@summary Removes the message with the given id.}</p>
     *  <p>If there is no message with that id, nothing happens.</p>
     *
     *  @param  id  The id for the message to remove.
     */
    public final void removeMessage( final String id )
    {
        if( nonNull( id ) )
        {
            m_Messages.remove( id );
        }
    }   //  removeMessage()

    /**
     *  <p>{@summary Removes the message with the empty string as its id.}</p>
     *  <p>If there is no message with that id, nothing happens.</p>
     */
    public final void removeMessage() { removeMessage( EMPTY_STRING ); }

    /**
     *  <p>{@summary Removes the message trigger with the given id.}</p>
     *  <p>If there is no message trigger with that id, nothing happens.</p>
     *
     *  @param  id  The id for the message trigger to remove.
     */
    public final void removeMessageTrigger( final String id )
    {
        if( nonNull( id ) )
        {
            final var messageTrigger = m_MessageTriggers.remove( id );
            if( nonNull( messageTrigger ) ) messageTrigger.disable();
        }
    }   //  removeMessageTrigger()

    /**
     *  <p>{@summary Removes the message trigger with the empty string as its
     *  id.}</p>
     *  <p>If there is no message trigger with that id, nothing happens.</p>
     */
    public final void removeMessageTrigger() { removeMessageTrigger( EMPTY_STRING );}
}
//  class ErrorDisplay

/*
 *  End of File
 */