/*
 * ============================================================================
 *  Copyright © 2002-2022 by Thomas Thrien.
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

package org.tquadrat.foundation.fx;

import static org.apiguardian.api.API.Status.STABLE;
import static org.tquadrat.foundation.lang.Objects.isNull;
import static org.tquadrat.foundation.lang.Objects.requireNonNullArgument;

import java.util.function.Function;

import org.apiguardian.api.API;
import org.tquadrat.foundation.annotation.ClassVersion;
import org.tquadrat.foundation.lang.GenericStringConverter;
import org.tquadrat.foundation.lang.Stringer;

/**
 *  <p>{@summary An implementation of
 *  {@link org.tquadrat.foundation.lang.StringConverter}
 *  that extends
 *  {@link javafx.util.StringConverter}.}
 *  It delegates the transformation work to the <i>Foundation</i>
 *  {@code StringConverter} instance that is provided to the
 *  {@link #FXStringConverter(org.tquadrat.foundation.lang.StringConverter) constructor}.</p>
 *  <p>To reuse an existing JavaFX {@code StringConverter}, you can use the
 *  method
 *  {@link #wrap(javafx.util.StringConverter)}.</p>
 *  <p>To get just a <i>Foundation</i> {@code StringConverter}, you can use
 *  this code (here {@code BigInteger} is used as an example):</p>
 *  <pre><code>
 *      final var x = new BigIntegerStringConverter();
 *      final var c = new GenericStringConverter&lt;BigInteger&gt;( s -> isNull( s ) ? null : x.fromString( s.toString() ), x::toString );
 *  </code></pre>
 *  <p>If you need to build a String converter from scratch that should serve
 *  both purposes, write your own class that extends
 *  {@link javafx.util.StringConverter}
 *  and implements
 *  {@link org.tquadrat.foundation.lang.StringConverter},
 *  and then implement the methods accordingly. Keep in mind that the method
 *  signature for {@code fromString()} differs for both the abstract method and
 *  the interface because of the different argument type (String vs.
 *  CharSequence).</p>
 *
 *  @param <T>  The target type for the conversion.
 *
 *  @version $Id: FXStringConverter.java 984 2022-01-13 00:46:27Z tquadrat $
 *  @extauthor Thomas Thrien - thomas.thrien@tquadrat.org
 *  @UMLGraph.link
 *  @since 0.1.0
 *
 *  @see GenericStringConverter
 */
@ClassVersion( sourceVersion = "$Id: FXStringConverter.java 984 2022-01-13 00:46:27Z tquadrat $" )
@API( status = STABLE, since = "0.1.0" )
public final class FXStringConverter<T> extends javafx.util.StringConverter<T> implements org.tquadrat.foundation.lang.StringConverter<T>
{
        /*------------*\
    ====** Attributes **=======================================================
        \*------------*/
    /**
     *  The <i>Foundation</i>
     *  {@link org.tquadrat.foundation.lang.StringConverter StringConverter}
     *  instance.
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
        final var retValue = isNull( source ) ? null : m_StringConverter.fromString( source );

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
     *  Creates an instance of {@code FXStringConverter} from an instance of
     *  {@link javafx.util.StringConverter}.
     *
     *  @param  <C> The subject class.
     *  @param  stringConverter The instance of
     *      {@link javafx.util.StringConverter}.
     *  @return The new instance.
     */
    @SuppressWarnings( "UseOfConcreteClass" )
    public static final <C> FXStringConverter<C> wrap( final javafx.util.StringConverter<C> stringConverter )
    {
        final var stringer = (Stringer<C>) s -> isNull( s ) ? null : stringConverter.toString( s );
        final var parser = (Function<CharSequence, C>) s -> isNull( s ) ? null : stringConverter.fromString( s.toString() );

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