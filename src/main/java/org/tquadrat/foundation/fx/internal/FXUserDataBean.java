/*
 * ============================================================================
 * Copyright © 2002-2022 by Thomas Thrien.
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

package org.tquadrat.foundation.fx.internal;

import static org.apiguardian.api.API.Status.INTERNAL;
import static org.tquadrat.foundation.lang.Objects.requireNonNullArgument;
import static org.tquadrat.foundation.lang.Objects.requireNotEmptyArgument;

import java.net.URL;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

import org.apiguardian.api.API;
import org.tquadrat.foundation.annotation.ClassVersion;
import javafx.application.Application;
import javafx.stage.Stage;

/**
 *  The abstract base class for user data beans that can be used with several
 *  JavaFX entities.
 *
 *  @param  <A> The class of the JavaFX application.
 *
 *  @version $Id: FXUserDataBean.java 984 2022-01-13 00:46:27Z tquadrat $
 *  @extauthor Thomas Thrien - thomas.thrien@tquadrat.org
 *  @UMLGraph.link
 *  @since 0.1.0
 */
@SuppressWarnings( "AbstractClassWithoutAbstractMethods" )
@ClassVersion( sourceVersion = "$Id: FXUserDataBean.java 984 2022-01-13 00:46:27Z tquadrat $" )
@API( status = INTERNAL, since = "0.1.0" )
public abstract sealed class FXUserDataBean<A extends Application>
    permits org.tquadrat.foundation.fx.SceneUserData
{
        /*------------*\
    ====** Attributes **=======================================================
        \*------------*/
    /**
     *  A reference to the application's main class.
     */
    private final A m_Application;

    /**
     *  The URL for the application's main CSS file.
     */
    @SuppressWarnings( "OptionalUsedAsFieldOrParameterType" )
    private Optional<URL> m_ApplicationCSS = Optional.empty();

    /**
     *  A reference to the application's primary stage.
     */
    private final Stage m_PrimaryStage;

    /**
     *  The additional properties.
     */
    private final Map<String,Object> m_Properties = new TreeMap<>();

        /*--------------*\
    ====** Constructors **=====================================================
        \*--------------*/
    /**
     *  Creates a new {@code FXUserDataBean} instance.
     *
     *  @param  application The reference for the application's main class.
     *  @param  primaryStage    The reference for the application's primary
     *      stage.
     */
    protected FXUserDataBean( final A application, final Stage primaryStage )
    {
        m_Application = requireNonNullArgument( application, "application" );
        m_PrimaryStage = requireNonNullArgument( primaryStage, "primaryStage" );
    }   //  FXUserDataBean()

        /*---------*\
    ====** Methods **==========================================================
        \*---------*/
    /**
     *  Returns the reference to the application's main class.
     *
     *  @return The application class.
     */
    public final A getApplication() { return m_Application; }

    /**
     *  Returns the URL for tha application's main CSS file.
     *
     *  @return An instance of
     *      {@link Optional}
     *      that holds the CSS URL.
     */
    public final Optional<URL> getApplicationCSS() { return m_ApplicationCSS; }

    /**
     *  Returns the reference to the application's primary stage.
     *
     *  @return The primary stage
     */
    public final Stage getPrimaryStage() { return m_PrimaryStage; }

    /**
     *  Returns the property with the given name.
     *
     *  @param  name    The name of the property.
     *  @return An instance of
     *      {@link Optional}
     *      that holds the property.
     */
    public final Optional<Object> getProperty( final String name )
    {
        final var retValue = Optional.ofNullable( m_Properties.get( requireNotEmptyArgument( name, "name" ) ) );

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  getProperty()

    /**
     *  Checks whether a property with the given name exists.
     *
     *  @param  name    The name of the property.
     *  @return {@code true} if there is a property with the given name (that
     *      can still be {@code null}), {@code false} otherwise.
     */
    public final boolean hasProperty( final String name ) { return m_Properties.containsKey( requireNotEmptyArgument( name, "name" ) ); }

    /**
     *  Removes the property with the given name; nothing happens if there is
     *  no property with the given name.
     *
     *  @param  name    The name of the property.
     */
    public final void removeProperty( final String name ) { m_Properties.remove( requireNotEmptyArgument( name, "name" ) ); }

    /**
     *  Sets the URL for the application's CSS file.
     *
     *  @param  cssURL  The URL for the CSS file.
     */
    public final void setApplicationCSS( final URL cssURL ) { m_ApplicationCSS = Optional.of( requireNonNullArgument( cssURL, "cssURL" ) ); }

    /**
     *  Sets the property with the given name to the given value.
     *
     *  @param  name    The name of the property.
     *  @param  value   The new value of the property; this can be
     *      {@code null}.
     */
    public final void setProperty( final String name, final Object value )
    {
        m_Properties.put( requireNotEmptyArgument( name, "name" ), value );
    }   //  setProperty()
}
//  class FXUserDataBean

/*
 *  End of File
 */