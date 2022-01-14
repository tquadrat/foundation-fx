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

package org.tquadrat.foundation.fx;

import static org.apiguardian.api.API.Status.STABLE;
import static org.tquadrat.foundation.lang.Objects.requireNonNullArgument;

import java.util.Optional;
import java.util.function.BiFunction;

import org.apiguardian.api.API;
import org.tquadrat.foundation.annotation.ClassVersion;
import org.tquadrat.foundation.fx.internal.FXUserDataBean;
import javafx.application.Application;
import javafx.beans.NamedArg;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;

/**
 *  <p>{@summary The user data bean for instances of
 *  {@link Scene}.}</p>
 *  <p>A JavaFX {@code Scene} instance allows that arbitrary user data can be
 *  attached to it, using the method
 *  {@link Scene#setUserData(Object)}.
 *  This class provides an extensible container for this kind of data.</p>
 *  <p>Through the methods</p>
 *  <ul>
 *      <li>{@link #setProperty(String, Object) setProperty()}</li>
 *      <li>{@link #getProperty(String) getProperty()}</li>
 *      <li>{@link #removeProperty(String) removeProperty()}</li>
 *      <li>{@link #hasProperty(String) hasProperty()}</li>
 *  </ul>
 *  <p>it is already possible to store any type of data into the user data
 *  bean, but it can be easily extended for additional functionality.</p>
 *
 *  <h2>{@code createScene()} Factory Methods</h2>
 *  <p>The methods</p>
 *  <ul>
 *      <li>{@link #createScene(Application, Stage, Parent)}</li>
 *      <li>{@link #createScene(Application, Stage, Parent, Paint)}</li>
 *      <li>{@link #createScene(Application, Stage, Parent, double, double)}</li>
 *      <li>{@link #createScene(Application, Stage, Parent, double, double, Paint)}</li>
 *      <li>{@link #createScene(SceneUserData, Stage, Parent)}</li>
 *      <li>{@link #createScene(SceneUserData, Stage, Parent, Paint)}</li>
 *      <li>{@link #createScene(SceneUserData, Stage, Parent, double, double)}</li>
 *      <li>{@link #createScene(SceneUserData, Stage, Parent, double, double, Paint)}</li>
 *      <li>{@link #createScene(Application, Stage, Parent, BiFunction)}</li>
 *      <li>{@link #createScene(Application, Stage, Parent, BiFunction, Paint)}</li>
 *      <li>{@link #createScene(Application, Stage, Parent, BiFunction, double, double)}</li>
 *      <li>{@link #createScene(Application, Stage, Parent, BiFunction, double, double, Paint)}</li>
 *  </ul>
 *  <p>can be used to create a
 *  {@link Scene}
 *  instance together with the user data. Those methods that do <i>not</i> have
 *  a {@code supplier} argument will use an instance of {@code SceneUserData}
 *  (this class); if yu want to use an instance of a derived class, you have to
 *  implement the respective supplier and use one of the methods that take
 *  it.</p>
 *
 *  @param  <A> The class of the JavaFX application.
 *
 *  @version $Id: SceneUserData.java 989 2022-01-13 19:09:58Z tquadrat $
 *  @extauthor Thomas Thrien - thomas.thrien@tquadrat.org
 *  @UMLGraph.link
 *  @since 0.1.0
 */
@ClassVersion( sourceVersion = "$Id: SceneUserData.java 989 2022-01-13 19:09:58Z tquadrat $" )
@API( status = STABLE, since = "0.1.0" )
public non-sealed class SceneUserData<A extends Application> extends FXUserDataBean<A>
{
        /*------------*\
    ====** Attributes **=======================================================
        \*------------*/
    /**
     *  The stage for this scene; this can be the same as the primary stage
     *  in cases where this scene is that for the application's main window.
     */
    private final Stage m_Stage;

        /*--------------*\
    ====** Constructors **=====================================================
        \*--------------*/
    /**
     *  Creates a new {@code SceneUserData} instance.
     *
     *  @param  application The reference for the application's main class.
     *  @param  primaryStage    The reference for the application's primary
     *      stage.
     */
    public SceneUserData( final A application, final Stage primaryStage )
    {
        this( application, primaryStage, primaryStage );
    }   //  SceneUserData()

    /**
     *  Creates a new {@code SceneUserData} instance.
     *
     *  @param  application The reference for the application's main class.
     *  @param  primaryStage    The reference for the application's primary
     *      stage.
     *  @param  currentStage    The reference to the stage for this scene.
     */
    public SceneUserData( final A application, final Stage primaryStage, final Stage currentStage )
    {
        super( application, primaryStage );

        m_Stage = requireNonNullArgument( currentStage, "currentStage" );
    }   //  SceneUserData()

    /**
     *  Creates a new {@code SceneUserData} instance and copies the references
     *  for the application, the primary and the current stage from the given
     *  user data bean (usually that from the application's main scene).
     *
     *  @param  userDataBean    The template user data bean.
     *  @param  currentStage    The reference to the stage for this scene.
     */
    @SuppressWarnings( "UseOfConcreteClass" )
    public SceneUserData( final SceneUserData<? extends A> userDataBean, final Stage currentStage )
    {
        this( requireNonNullArgument( userDataBean, "userDataBean" ).getApplication(), userDataBean.getPrimaryStage(), currentStage );
        userDataBean.getApplicationCSS().ifPresent( this::setApplicationCSS );
    }   //  SceneUserData()

        /*---------*\
    ====** Methods **==========================================================
        \*---------*/
    /**
     *  Creates a
     *  {@link Scene}
     *  for a specific root
     *  {@link Node}.
     *
     *  @param  <T> The type of the application's main class.
     *  @param  application The reference for the application's main class.
     *  @param  primaryStage    The reference for the application's primary
     *      stage.
     *  @param  root    The root node of the scene graph.
     *  @return The new scene instance.
     */
    @API( status = STABLE, since = "0.1.0" )
    public static final <T extends Application> Scene createScene( @NamedArg( "application" ) final T application, @NamedArg( "primaryStage" ) final Stage primaryStage, @NamedArg( "root" ) final Parent root )
    {
        final var retValue = createScene( application, primaryStage, root, SceneUserData::new );

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  createScene()

    /**
     *  Creates a
     *  {@link Scene}
     *  for a specific root
     *  {@link Node}.
     *
     *  @param  <T> The type of the application's main class.
     *  @param  application The reference for the application's main class.
     *  @param  primaryStage    The reference for the application's primary
     *      stage.
     *  @param  root    The root node of the scene graph.
     *  @param  supplier    The supplier for the {@code SceneUserData}
     *      instance.
     *  @return The new scene instance.
     */
    @API( status = STABLE, since = "0.1.0" )
    public static final <T extends Application> Scene createScene( @NamedArg( "application" ) final T application, @NamedArg( "primaryStage" ) final Stage primaryStage, @NamedArg( "root" ) final Parent root, @NamedArg( "supplier") final BiFunction<T, ? super Stage, ? extends SceneUserData<T>> supplier )
    {
        final var userData = requireNonNullArgument( supplier, "supplier" ).apply( requireNonNullArgument( application, "application" ), requireNonNullArgument( primaryStage, "primaryStage" ) );
        final var retValue = new Scene( requireNonNullArgument( root, "root" ) );
        primaryStage.setScene( retValue );
        retValue.setUserData( userData );

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  createScene()

    /**
     *  Creates a
     *  {@link Scene}
     *  for a specific root
     *  {@link Node}
     *  with a specific size.
     *
     *  @param  <T> The type of the application's main class.
     *  @param  application The reference for the application's main class.
     *  @param  primaryStage    The reference for the application's primary
     *      stage.
     *  @param  root    The root node of the scene graph.
     *  @param  width   The width of the scene.
     *  @param  height  The height of the scene.
     *  @return The new scene instance.
     */
    @API( status = STABLE, since = "0.1.0" )
    public static final <T extends Application> Scene createScene( @NamedArg( "application" ) final T application, @NamedArg( "primaryStage" ) final Stage primaryStage, @NamedArg( "root" ) final Parent root, @NamedArg( "width" ) final double width, @NamedArg( "height" ) final double height )
    {
        final var retValue = createScene( application, primaryStage, root, SceneUserData::new, width, height );

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  createScene()

    /**
     *  Creates a
     *  {@link Scene}
     *  for a specific root
     *  {@link Node}
     *  with a specific size.
     *
     *  @param  <T> The type of the application's main class.
     *  @param  application The reference for the application's main class.
     *  @param  primaryStage    The reference for the application's primary
     *      stage.
     *  @param  root    The root node of the scene graph.
     *  @param  supplier    The supplier for the {@code SceneUserData}
     *      instance.
     *  @param  width   The width of the scene.
     *  @param  height  The height of the scene.
     *  @return The new scene instance.
     */
    @API( status = STABLE, since = "0.1.0" )
    public static final <T extends Application> Scene createScene( @NamedArg( "application" ) final T application, @NamedArg( "primaryStage" ) final Stage primaryStage, @NamedArg( "root" ) final Parent root, @NamedArg( "supplier") final BiFunction<T, ? super Stage, ? extends SceneUserData<T>> supplier, @NamedArg( "width" ) final double width, @NamedArg( "height" ) final double height )
    {
        final var userData = requireNonNullArgument( supplier, "supplier" ).apply( requireNonNullArgument( application, "application" ), requireNonNullArgument( primaryStage, "primaryStage" ) );
        final var retValue = new Scene( requireNonNullArgument( root, "root" ), width, height );
        primaryStage.setScene( retValue );
        retValue.setUserData( userData );

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  createScene()

    /**
     *  Creates a
     *  {@link Scene}
     *  for a specific root
     *  {@link Node}
     *  with a fill.
     *
     *  @param  <T> The type of the application's main class.
     *  @param  application The reference for the application's main class.
     *  @param  primaryStage    The reference for the application's primary
     *      stage.
     *  @param  root    The root node of the scene graph.
     *  @param  fill    The fill.
     *  @return The new scene instance.
     */
    @API( status = STABLE, since = "0.1.0" )
    public static final <T extends Application> Scene createScene( @NamedArg( "application" ) final T application, @NamedArg( "primaryStage" ) final Stage primaryStage, @NamedArg( "root" ) final Parent root, @NamedArg( value = "fill", defaultValue = "WHITE" ) final Paint fill )
    {
        final var retValue = createScene( application, primaryStage, root, SceneUserData::new, fill );

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  createScene()

    /**
     *  Creates a
     *  {@link Scene}
     *  for a specific root
     *  {@link Node}
     *  with a fill.
     *
     *  @param  <T> The type of the application's main class.
     *  @param  application The reference for the application's main class.
     *  @param  primaryStage    The reference for the application's primary
     *      stage.
     *  @param  root    The root node of the scene graph.
     *  @param  supplier    The supplier for the {@code SceneUserData}
     *      instance.
     *  @param  fill    The fill.
     *  @return The new scene instance.
     */
    @API( status = STABLE, since = "0.1.0" )
    public static final <T extends Application> Scene createScene( @NamedArg( "application" ) final T application, @NamedArg( "primaryStage" ) final Stage primaryStage, @NamedArg( "root" ) final Parent root, @NamedArg( "supplier") final BiFunction<T, ? super Stage, ? extends SceneUserData<T>> supplier, @NamedArg( value = "fill", defaultValue = "WHITE" ) final Paint fill )
    {
        final var userData = requireNonNullArgument( supplier, "supplier" ).apply( requireNonNullArgument( application, "application" ), requireNonNullArgument( primaryStage, "primaryStage" ) );
        final var retValue = new Scene( requireNonNullArgument( root, "root" ), requireNonNullArgument( fill, "fill" ) );
        primaryStage.setScene( retValue );
        retValue.setUserData( userData );

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  createScene()

    /**
     *  Creates a
     *  {@link Scene}
     *  for a specific root
     *  {@link Node}
     *  with a specific size and fill.
     *
     *  @param  <T> The type of the application's main class.
     *  @param  application The reference for the application's main class.
     *  @param  primaryStage    The reference for the application's primary
     *      stage.
     *  @param  root    The root node of the scene graph.
     *  @param  width   The width of the scene.
     *  @param  height  The height of the scene.
     *  @param  fill    The fill.
     *  @return The new scene instance.
     */
    @API( status = STABLE, since = "0.1.0" )
    public static final <T extends Application> Scene createScene( @NamedArg( "application" ) final T application, @NamedArg( "primaryStage" ) final Stage primaryStage, @NamedArg( "root" ) final Parent root, @NamedArg( "width" ) final double width, @NamedArg( "height" ) final double height, @NamedArg( value = "fill", defaultValue = "WHITE" ) final Paint fill )
    {
        final var retValue = createScene( application, primaryStage, root, SceneUserData::new, width, height, fill );

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  createScene()

    /**
     *  Creates a
     *  {@link Scene}
     *  for a specific root
     *  {@link Node}
     *  with a specific size and fill.
     *
     *  @param  <T> The type of the application's main class.
     *  @param  application The reference for the application's main class.
     *  @param  primaryStage    The reference for the application's primary
     *      stage.
     *  @param  root    The root node of the scene graph.
     *  @param  supplier    The supplier for the {@code SceneUserData}
     *      instance.
     *  @param  width   The width of the scene.
     *  @param  height  The height of the scene.
     *  @param  fill    The fill.
     *  @return The new scene instance.
     */
    @API( status = STABLE, since = "0.1.0" )
    public static final <T extends Application> Scene createScene( @NamedArg( "application" ) final T application, @NamedArg( "primaryStage" ) final Stage primaryStage, @NamedArg( "root" ) final Parent root, @NamedArg( "supplier") final BiFunction<T, ? super Stage, ? extends SceneUserData<T>> supplier, @NamedArg( "width" ) final double width, @NamedArg( "height" ) final double height, @NamedArg( value = "fill", defaultValue = "WHITE" ) final Paint fill )
    {
        final var userData = requireNonNullArgument( supplier, "supplier" ).apply( requireNonNullArgument( application, "application" ), requireNonNullArgument( primaryStage, "primaryStage" ) );
        final var retValue = new Scene( requireNonNullArgument( root, "root" ), width, height, requireNonNullArgument( fill, "fill" ) );
        primaryStage.setScene( retValue );
        retValue.setUserData( userData );

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  createScene()

    /**
     *  Creates a
     *  {@link Scene}
     *  for a specific root
     *  {@link Node}.
     *
     *  @param  <T> The type of the application's main class.
     *  @param  templateDataBean    The user data from the application's main
     *      scene.
     *  @param  currentStage    The stage for the current scene.
     *  @param  root    The root node of the scene graph.
     *  @return The new scene instance.
     */
    @SuppressWarnings( "UseOfConcreteClass" )
    @API( status = STABLE, since = "0.1.0" )
    public static final <T extends Application> Scene createScene( @NamedArg( "templateDataBean" ) final SceneUserData<T> templateDataBean, @NamedArg( "currentStage" ) final Stage currentStage, @NamedArg( "root" ) final Parent root )
    {
        final var newUserDataBean = new SceneUserData<>( templateDataBean, currentStage );
        final var retValue = new Scene( requireNonNullArgument( root, "root" ) );
        currentStage.setScene( retValue );
        retValue.setUserData( newUserDataBean );
        newUserDataBean.getApplicationCSS().ifPresent( u -> retValue.getStylesheets().add( u.toExternalForm() ) );

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  createScene()

    /**
     *  Creates a
     *  {@link Scene}
     *  for a specific root
     *  {@link Node}
     *  with a specific size.
     *
     *  @param  <T> The type of the application's main class.
     *  @param  templateDataBean    The user data from the application's main
     *      scene.
     *  @param  currentStage    The stage for the current scene.
     *  @param  root    The root node of the scene graph.
     *  @param  width   The width of the scene.
     *  @param  height  The height of the scene.
     *  @return The new scene instance.
     */
    @SuppressWarnings( "UseOfConcreteClass" )
    @API( status = STABLE, since = "0.1.0" )
    public static final <T extends Application> Scene createScene( @NamedArg( "templateDataBean" ) final SceneUserData<T> templateDataBean, @NamedArg( "currentStage" ) final Stage currentStage, @NamedArg( "root" ) final Parent root, @NamedArg( "width" ) final double width, @NamedArg( "height" ) final double height )
    {
        final var newUserDataBean = new SceneUserData<>( templateDataBean, currentStage );
        final var retValue = new Scene( requireNonNullArgument( root, "root" ), width, height );
        currentStage.setScene( retValue );
        retValue.setUserData( newUserDataBean );
        newUserDataBean.getApplicationCSS().ifPresent( u -> retValue.getStylesheets().add( u.toExternalForm() ) );

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  createScene()

    /**
     *  Creates a
     *  {@link Scene}
     *  for a specific root
     *  {@link Node}
     *  with a fill.
     *
     *  @param  <T> The type of the application's main class.
     *  @param  templateDataBean    The user data from the application's main scene.
     *  @param  currentStage    The stage for the current scene.
     *  @param  root    The root node of the scene graph.
     *  @param  fill    The fill.
     *  @return The new scene instance.
     */
    @SuppressWarnings( "UseOfConcreteClass" )
    @API( status = STABLE, since = "0.1.0" )
    public static final <T extends Application> Scene createScene( @NamedArg( "templateDataBean" ) final SceneUserData<T> templateDataBean, @NamedArg( "currentStage" ) final Stage currentStage, @NamedArg( "root" ) final Parent root, @NamedArg( value = "fill", defaultValue = "WHITE" ) final Paint fill )
    {
        final var newUserDataBean = new SceneUserData<>( templateDataBean, currentStage );
        final var retValue = new Scene( requireNonNullArgument( root, "root" ), requireNonNullArgument( fill, "fill" ) );
        currentStage.setScene( retValue );
        retValue.setUserData( newUserDataBean );
        newUserDataBean.getApplicationCSS().ifPresent( u -> retValue.getStylesheets().add( u.toExternalForm() ) );

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  createScene()

    /**
     *  Creates a
     *  {@link Scene}
     *  for a specific root
     *  {@link Node}
     *  with a specific size and fill.
     *
     *  @param  <T> The type of the application's main class.
     *  @param  templateDataBean    The user data from the application's main scene.
     *  @param  currentStage    The stage for the current scene.
     *  @param  root    The root node of the scene graph.
     *  @param  width   The width of the scene.
     *  @param  height  The height of the scene.
     *  @param  fill    The fill.
     *  @return The new scene instance.
     */
    @SuppressWarnings( "UseOfConcreteClass" )
    @API( status = STABLE, since = "0.1.0" )
    public static final <T extends Application> Scene createScene( @NamedArg( "templateDataBean" ) final SceneUserData<T> templateDataBean, @NamedArg( "currentStage" ) final Stage currentStage, @NamedArg( "root" ) final Parent root, @NamedArg( "width" ) final double width, @NamedArg( "height" ) final double height, @NamedArg( value = "fill", defaultValue = "WHITE" ) final Paint fill )
    {
        final var newUserDataBean = new SceneUserData<>( templateDataBean, currentStage );
        final var retValue = new Scene( requireNonNullArgument( root, "root" ), width, height, requireNonNullArgument( fill, "fill" ) );
        currentStage.setScene( retValue );
        retValue.setUserData( newUserDataBean );
        newUserDataBean.getApplicationCSS().ifPresent( u -> retValue.getStylesheets().add( u.toExternalForm() ) );

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  createScene()

    /**
     *  Returns the stage for the current scene. If the current scene is the
     *  application's main scene, the returned stage is the primary stage, so
     *  that in this case
     *  {@link #getPrimaryStage()}
     *  will return the same value as this method.
     *
     *  @return The stage for the current scene.
     */
    public final Stage getStage() { return m_Stage; }

    /**
     *  Retrieves the user data from the given instance of
     *  {@link Scene}.
     *
     *  @param  <T> The type of the application's main class.
     *  @param  scene   The scene.
     *  @param  applicationClass    The type of the application's main class.
     *  @return An instance of
     *      {@link Optional}
     *      that holds the user data instance.
     */
    @SuppressWarnings( "UseOfConcreteClass" )
    public static final <T extends Application> Optional<SceneUserData<T>> retrieveUserData( final Scene scene, final Class<T> applicationClass )
    {
        Optional<SceneUserData<T>> retValue = Optional.empty();
        final var u = requireNonNullArgument( scene, "scene" ).getUserData();
        if( u instanceof SceneUserData userDataBean )
        {
            final var app = userDataBean.getApplication();
            if( applicationClass.isInstance( app ) )
            {
                @SuppressWarnings( "unchecked" )
                final var data = (SceneUserData<T>) u;
                retValue = Optional.of( data );
            }
        }

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  retrieveUserData()
}
//  class SceneUserData

/*
 *  End of File
 */