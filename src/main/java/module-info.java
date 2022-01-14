/*
 * ============================================================================
 * Copyright © 2002-2020 by Thomas Thrien.
 * All Rights Reserved.
 * ============================================================================
 *
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

/**
 *  The module for the JavaFX extensions by the <i>Foundation</i> Library.
 *
 *  @version $Id: module-info.java 984 2022-01-13 00:46:27Z tquadrat $
 *
 *  @todo task.list
 */
module org.tquadrat.foundation.fx
{
    requires java.base;
    requires org.tquadrat.foundation.util;

    requires transitive javafx.base;
    requires transitive javafx.graphics;

    //---* Common Use *--------------------------------------------------------
    exports org.tquadrat.foundation.fx;
}

/*
 *  End of File
 */