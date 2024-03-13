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

package org.tquadrat.foundation.fx.control.skin;

import static java.lang.Double.max;
import static javafx.scene.layout.Region.USE_COMPUTED_SIZE;
import static org.apiguardian.api.API.Status.STABLE;
import static org.tquadrat.foundation.lang.Objects.nonNull;
import static org.tquadrat.foundation.lang.Objects.requireNonNullArgument;

import java.util.Collection;

import org.apiguardian.api.API;
import org.tquadrat.foundation.annotation.ClassVersion;
import org.tquadrat.foundation.fx.control.ErrorDisplay;
import javafx.beans.Observable;
import javafx.collections.ObservableMap;
import javafx.scene.Node;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SkinBase;
import javafx.scene.control.skin.ScrollPaneSkin;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

/**
 *  {@summary The skin for the
 *  {@link ErrorDisplay }
 *  control.}
 *  <p>The icon that is displayed with an error message was taken from
 *  {@href https://www.iconfinder.com/icons/216514/warning_icon}.</p>
 *
 *
 *  @extauthor Thomas Thrien - thomas.thrien@tquadrat.org
 *  @version $Id: ErrorDisplaySkin.java 1114 2024-03-12 23:07:59Z tquadrat $
 *  @since 0.4.3
 *
 *  @UMLGraph.link
 */
@SuppressWarnings( "JavadocLinkAsPlainText" )
@ClassVersion( sourceVersion = "$Id: ErrorDisplaySkin.java 1114 2024-03-12 23:07:59Z tquadrat $" )
@API( status = STABLE, since = "0.4.3" )
public final class ErrorDisplaySkin extends SkinBase<ErrorDisplay>
{
        /*-----------*\
    ====** Constants **========================================================
        \*-----------*/
    /**
     *  The spacing for the message entries: {@value}.
     */
    public static final double MESSAGE_ENTRY_SPACING = 7.0;

    /**
     *  The minimum height for an
     *  {@link ErrorDisplay}
     *  control: {@value}.
     */
    public static final double MIN_HEIGHT = 55.0;

        /*------------*\
    ====** Attributes **=======================================================
        \*------------*/
    /**
     *  The node that is used to display the messages.
     */
    private final VBox m_Content;

    /**
     *  The icon for the message.
     */
    private final Image m_Icon;

        /*--------------*\
    ====** Constructors **=====================================================
        \*--------------*/
    /**
     *  Creates a new {@code ErrorDisplaySkin} instance, installing the
     *  necessary child nodes into the
     *  {@link Control}'s
     *  children list.
     *
     *  @param  control The control that this skin should be installed onto.
     */
    public ErrorDisplaySkin( final ErrorDisplay control )
    {
        super( requireNonNullArgument( control, "control" ) );

        //---* Get the icon for the messages *---------------------------------
        final var inputStream = control.getClass().getResourceAsStream( "ErrorDisplay.png" );
        m_Icon = nonNull( inputStream ) ? new Image( inputStream ) : null;

        //---* Create the children and add them *------------------------------
        m_Content = new VBox();
        m_Content.setSpacing( MESSAGE_ENTRY_SPACING );

        final var scrollPane = new ScrollPane( m_Content );
        scrollPane.setSkin( new ScrollPaneSkin( scrollPane ) );

        getChildren().add( scrollPane );

        //---* Set the listener that repaint the scroll pane on any change *---
        control.messagesProperty().addListener( this::messagesInvalidated );
    }   //  ErrorDisplaySkin()

        /*---------*\
    ====** Methods **==========================================================
        \*---------*/
    /**
     *  Calculates the preferred entry width based on the size of the control.
     *
     *  @return The preferred width for a new entry.
     */
    private double calcNewEntryWidth()
    {
        final var control = getControl();

        var value = control.getLayoutBounds().getWidth();
        if( value <= 0.0 ) value = getControl().getPrefWidth();
        if( value <= 0.0 ) value = getControl().getMinWidth();
        final var retValue = value < 0.0 ? USE_COMPUTED_SIZE : value - 20.0;

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  calcNewEntryWidth()

    /**
     *  {@inheritDoc}
     */
    @Override
    protected  final double computeMinHeight( final double width, final double topInset, final double rightInset, final double bottomInset, final double leftInset )
    {
        final var retValue = max( MIN_HEIGHT, super.computeMinHeight( width, topInset, rightInset, bottomInset, leftInset ) );

        //---* Done *----------------------------------------------------------
        return retValue;
    }   // computeMinHeight()

    /**
     *  Creates an entry.
     *
     *  @param  text    The message text.
     *  @return The entry node.
     */
    private final Node createEntry( final String text )
    {
        final var retValue = new Label( requireNonNullArgument( text, "text" ) );
        retValue.getStyleClass().clear();
        retValue.getStyleClass().add( ErrorDisplay.STYLE_CLASS_MessageDisplayLabel );
        retValue.setWrapText( true );
        retValue.setPrefWidth( calcNewEntryWidth() );

        if( nonNull( m_Icon ) )
        {
            //---* Add the icon *----------------------------------------------
            final var imageView = new ImageView( m_Icon );
            imageView.setFitHeight( 20.0 );
            imageView.setPreserveRatio( true );
            retValue.setGraphic( imageView );
        }

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  createEntry()

    /**
     *  Returns a reference to the control.
     *
     *  @return The control.
     */
    private final ErrorDisplay getControl() { return (ErrorDisplay) getNode(); }

    /**
     *  The invalidation listener for the messages property.
     *
     *  @param  source  The source.
     */
    @SuppressWarnings( {"rawtypes", "unchecked"} )
    private final void messagesInvalidated( final Observable source )
    {
        if( source instanceof final ObservableMap map )
        {
            m_Content.getChildren().clear();
            final Collection<Node> entries = ((ObservableMap<String,String>) map).values()
                .stream()
                .map( this::createEntry )
                .toList();
            m_Content.getChildren().addAll( entries );
        }
    }   //  messagesInvalidated()
}
//  class ErrorDisplaySkin

/*
 *  End of File
 */