/*
 * ============================================================================
 *  Copyright © 2002-2023 by Thomas Thrien.
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

import static org.apiguardian.api.API.Status.STABLE;
import static org.tquadrat.foundation.lang.Objects.isNull;
import static org.tquadrat.foundation.lang.Objects.requireNonNullArgument;
import static org.tquadrat.foundation.lang.Objects.requireNotBlankArgument;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.UnaryOperator;

import org.apiguardian.api.API;
import org.tquadrat.foundation.annotation.ClassVersion;
import org.tquadrat.foundation.fx.control.ErrorDisplay;
import org.tquadrat.foundation.lang.GenericStringConverter;
import org.tquadrat.foundation.lang.Stringer;
import javafx.util.StringConverter;

/**
 *  <p>{@summary An implementation of
 *  {@link org.tquadrat.foundation.lang.StringConverter org.tquadrat.foundation.lang.StringConverter}
 *  (the <i>Foundation</i> {@code StringConverter}) that extends
 *  {@link StringConverter javafx.util.StringConverter}
 *  (the JavaFX {@code StringConverter}).} It delegates the transformation work
 *  to the <i>Foundation</i> {@code StringConverter} instance that is provided
 *  to the
 *  {@link #FXStringConverter(org.tquadrat.foundation.lang.StringConverter) constructor}.</p>
 *  <p>To reuse an existing JavaFX {@code StringConverter}, you can use the
 *  method
 *  {@link #wrap(StringConverter)}.</p>
 *  <p>To get just a <i>Foundation</i> {@code StringConverter}, you can use
 *  this code (here {@code BigInteger} is used as an example):</p>
 *  <pre><code>
 *      final var x = new BigIntegerStringConverter();
 *      final var c = new GenericStringConverter&lt;BigInteger&gt;( s -> isNull( s ) ? null : x.fromString( s.toString() ), x::toString );
 *  </code></pre>
 *  <p>If you need to build a String converter from scratch that should serve
 *  both purposes, write your own class that extends
 *  {@link StringConverter javafx.util.StringConverter}
 *  and implements
 *  {@link org.tquadrat.foundation.lang.StringConverter org.tquadrat.foundation.lang.StringConverter},
 *  and then implement the methods accordingly. Keep in mind that the method
 *  signature for {@code fromString()} differs for both the abstract class and
 *  the interface because of the different argument type (String vs.
 *  CharSequence).</p>
 *  <p>When a reference to an instance of
 *  {@link org.tquadrat.foundation.fx.control.ErrorDisplay}
 *  is provided to the constructor, an error messsage is displayed when the
 *  {@code fromString()} conversion fails.</p>
 *
 *  @note The method {@code fromString()} of a JavaFX {@code StringConverter}
 *      may always return {@code null}, for each and every argument, but this
 *      is not allowed for an implementation of the method with the same name
 *      for a <i>Foundation</i> {@code StringConverter}.
 *
 *  @param <T>  The target type for the conversion.
 *
 *  @version $Id: FXStringConverter.java 1110 2024-03-04 15:26:06Z tquadrat $
 *  @extauthor Thomas Thrien - thomas.thrien@tquadrat.org
 *  @UMLGraph.link
 *  @since 0.4.3
 *
 *  @see GenericStringConverter
 */
@ClassVersion( sourceVersion = "$Id: FXStringConverter.java 1110 2024-03-04 15:26:06Z tquadrat $" )
@API( status = STABLE, since = "0.4.3" )
public final class FXStringConverter<T> extends StringConverter<T> implements org.tquadrat.foundation.lang.StringConverter<T>
{
        /*------------*\
    ====** Attributes **=======================================================
        \*------------*/
    /**
     *  The reference to the error display.
     */
    @SuppressWarnings( "OptionalUsedAsFieldOrParameterType" )
    private final Optional<ErrorDisplay> m_ErrorDisplay;

    /**
     *  The function that composes the error messsage.
     */
    private final UnaryOperator<String> m_MessageComposer;

    /**
     *  The message id.
     */
    private final String m_MessageId;

    /**
     *  The <i>Foundation</i>
     *  {@link org.tquadrat.foundation.lang.StringConverter StringConverter}
     *  instance.
     *
     *  @serial
     */
    private final org.tquadrat.foundation.lang.StringConverter<T> m_StringConverter;

        /*--------------*\
    ====** Constructors **=====================================================
        \*--------------*/
    /**
     *  Creates a new instance of {@code FXStringConverter}.
     *
     *  @param  stringConverter The <i>Foundation</i>
     *      {@link org.tquadrat.foundation.lang.StringConverter StringConverter}
     *      instance that does the work.
     */
    public FXStringConverter( final org.tquadrat.foundation.lang.StringConverter<T> stringConverter )
    {
        super();

        m_StringConverter = requireNonNullArgument( stringConverter, "stringConverter" );
        m_ErrorDisplay = Optional.empty();
        m_MessageComposer = null;
        m_MessageId = null;
    }   //  FXStringConverter()

    /**
     *  Creates a new instance of {@code FXStringConverter}.
     *
     *  @param  stringConverter The <i>Foundation</i>
     *      {@link org.tquadrat.foundation.lang.StringConverter StringConverter}
     *      instance that does the work.
     *  @param  errorDisplay    The reference to the error display control that
     *      should display the error messages.
     *  @param  messageComposer The function that creates the error message to
     *      display.
     *  @param messageId    The message id (refer to
     *      {@link ErrorDisplay#addMessage(String, String) ErrorDisplay.addMessage()}).
     */
    public FXStringConverter( final org.tquadrat.foundation.lang.StringConverter<T> stringConverter, final ErrorDisplay errorDisplay, final UnaryOperator<String> messageComposer, final String messageId )
    {
        super();

        m_StringConverter = requireNonNullArgument( stringConverter, "stringConverter" );
        m_ErrorDisplay = Optional.of( requireNonNullArgument( errorDisplay, "errorDisplay" ) );
        m_MessageComposer = requireNonNullArgument( messageComposer, "messageComposer" );
        m_MessageId = requireNotBlankArgument( messageId, "messageId" );
    }   //  FXStringConverter()

        /*---------*\
    ====** Methods **==========================================================
        \*---------*/
    /**
     *  {@inheritDoc}
     */
    @SuppressWarnings( "PublicMethodNotExposedInInterface" )
    @Override
    public final T fromString( final String source )
    {
        final var retValue = isNull( source ) ? null : fromString( (CharSequence) source );

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  fromString()

    /**
     *  {@inheritDoc}
     */
    @Override
    public final T fromString( final CharSequence source )
    {
        T retValue;
        try
        {
            retValue = isNull( source ) ? null : m_StringConverter.fromString( source );
            m_ErrorDisplay.ifPresent( errorDisplay -> errorDisplay.removeMessage( m_MessageId ) );
        }
        catch( final IllegalArgumentException e )
        {
            m_ErrorDisplay.ifPresentOrElse( errorDisplay -> errorDisplay.addMessage( m_MessageId, m_MessageComposer.apply( source.toString() ) ), () -> {throw e;} );
            retValue = null;
        }

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  fromString()

    /**
     *  {@inheritDoc}
     */
    @Override
    public final String toString( final T source )
    {
        final var retValue = isNull( source ) ? null : m_StringConverter.toString( source );

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  toString()

    /**
     *  <p>{@summary Creates an instance of {@code FXStringConverter} from an
     *  instance of {@link StringConverter javafx.util.StringConverter}.}</p>
     *  <p>Keep in mind that the implementation of the method
     *  {@link StringConverter#fromString(String) javafx.util.StringConverter.fromString()}
     *  may return {@code null} for all input arguments, but that this
     *  behaviour is not valid for an implementation of the method
     *  {@link org.tquadrat.foundation.lang.StringConverter#fromString(CharSequence) org.tquadrat.foundation.lang.StringConverter.fromString()}.</p>
     *
     *  @param  <C> The subject class.
     *  @param  stringConverter The instance of
     *      {@link StringConverter}.
     *  @return The new instance.
     */
    public static final <C> FXStringConverter<C> wrap( final StringConverter<C> stringConverter )
    {
        final var stringer = (Stringer<C>) (value -> isNull( value ) ? null : stringConverter.toString( value ));
        final var parser = (Function<CharSequence, C>) (charSequence -> isNull( charSequence ) ? null : stringConverter.fromString( charSequence.toString() ));

        final var converter = new GenericStringConverter<>( parser, stringer );

        final var retValue = new FXStringConverter<>( converter );

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  wrap()
}
//  class FXStringConverter

/*
 *  End of File
 */