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

package org.teavm.classlib.java.awt.dnd;

/**
 * A wrapper for array of drag source listeners
 */
class TDragSourceMulticaster implements TDragSourceListener {

    private final TDragSourceListener[] listeners;

    TDragSourceMulticaster(TDragSourceListener[] listeners) {
        this.listeners = listeners;
    }

    public void dragExit(TDragSourceEvent dsde) {
        for (TDragSourceListener element : listeners) {
            element.dragExit(dsde);
        }
    }

    public void dragDropEnd(TDragSourceDropEvent dsde) {
        for (TDragSourceListener element : listeners) {
            element.dragDropEnd(dsde);
        }
    }

    public void dropActionChanged(TDragSourceDragEvent dsde) {
        for (TDragSourceListener element : listeners) {
            element.dropActionChanged(dsde);
        }
    }

    public void dragOver(TDragSourceDragEvent dsde) {
        for (TDragSourceListener element : listeners) {
            element.dragOver(dsde);
        }
    }

    public void dragEnter(TDragSourceDragEvent dsde) {
        for (TDragSourceListener element : listeners) {
            element.dragEnter(dsde);
        }
    }

}
