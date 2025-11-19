/*
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package org.teavm.classlib.java.awt.dnd.peer;

import java.awt.Cursor;
import java.awt.Image;
import java.awt.Point;
import org.teavm.classlib.java.awt.dnd.TDragSourceContext;
import org.teavm.classlib.java.awt.dnd.TInvalidDnDOperationException;

public interface TDragSourceContextPeer {

    void startDrag(TDragSourceContext dsc, Cursor c, Image di, Point ioff)
            throws TInvalidDnDOperationException;

    Cursor getCursor();

    void setCursor(Cursor c) throws TInvalidDnDOperationException;

    void transferablesFlavorsChanged();

}
