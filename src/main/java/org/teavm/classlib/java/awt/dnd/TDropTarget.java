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
/**
 * @author Michael Danilov, Pavel Dolgov
 */
package org.teavm.classlib.java.awt.dnd;

import java.awt.Component;
import java.awt.GraphicsEnvironment;
import java.awt.HeadlessException;
import java.awt.Point;
import java.awt.datatransfer.FlavorMap;
import java.awt.datatransfer.SystemFlavorMap;
//import java.awt.dnd.peer.DropTargetContextPeer;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
//import java.awt.peer.ComponentPeer;
import java.io.Serializable;
import java.util.TooManyListenersException;

import org.apache.harmony.awt.datatransfer.DTK;
import org.apache.harmony.awt.internal.nls.Messages;
import org.teavm.classlib.java.awt.dnd.peer.TDropTargetContextPeer;
import org.teavm.classlib.java.awt.peer.TComponentPeer;
//import org.apache.harmony.luni.util.NotImplementedException;

public class TDropTarget implements TDropTargetListener, Serializable {

    private static final long serialVersionUID = -6283860791671019047L;

    protected static class DropTargetAutoScroller implements ActionListener {
        Component component;
        Point point;

        protected DropTargetAutoScroller(Component c, Point p) {
            component = c;
            point = p;
        }

        protected void stop() /*throws NotImplementedException*/ {
        }

        protected void updateLocation(Point newLocn) /*throws NotImplementedException*/ {
            point = (Point)newLocn.clone();
        }

        public void actionPerformed(ActionEvent e) /*throws NotImplementedException*/{
        }

    }
    
    Component component;
    boolean active;
    int actions;
    FlavorMap flavorMap;
    TDropTargetListener dropTargetListener;
    final TDropTargetContext context;
    DropTargetAutoScroller autoScroller;

    public TDropTarget(Component c, int ops, TDropTargetListener dtl, 
            boolean act, FlavorMap fm) throws HeadlessException {
        if (GraphicsEnvironment.isHeadless()) {
            throw new HeadlessException();
        }
        context = createDropTargetContext();
        setDefaultActionsImpl(ops);
        active = (c != null) ? act : true;
        dropTargetListener = dtl;
        flavorMap = (fm != null) ? fm : SystemFlavorMap.getDefaultFlavorMap();
        component = c;
        if (c != null) {
            c.setDropTarget(this);
        }
    }

    public TDropTarget(Component c, TDropTargetListener dtl)
            throws HeadlessException {
        this(c, TDnDConstants.ACTION_COPY_OR_MOVE, dtl, true, null);
    }

    public TDropTarget(Component c, int ops, TDropTargetListener dtl, boolean act)
            throws HeadlessException {
        this(c, ops, dtl, act, null);
    }

    public TDropTarget(Component c, int ops, TDropTargetListener dtl)
            throws HeadlessException {
        this(c, ops, dtl, true, null);
    }

    public TDropTarget() throws HeadlessException {
        this(null, TDnDConstants.ACTION_COPY_OR_MOVE, null, true, null);
    }

    protected DropTargetAutoScroller createDropTargetAutoScroller(
            Component c, Point p) {
        return new DropTargetAutoScroller(c, p);
    }

    public void removeNotify(TComponentPeer peer) /*throws NotImplementedException*/ {
        context.removeNotify();
    }

    public void addNotify(TComponentPeer peer) /*throws NotImplementedException*/ {
        if(component == null) {
            return;
        }
        
        DTK dtk = DTK.getDTK();
        TDropTargetContextPeer dtp = dtk.createDropTargetContextPeer(context);
        context.addNotify(dtp);
    }

    public synchronized void removeDropTargetListener(TDropTargetListener dtl) {
        if (dtl == null || dropTargetListener == null) {
            return;
        }
        if (dtl != dropTargetListener) {
            // awt.175=Listener mismatch
            throw new IllegalArgumentException(Messages.getString("awt.175")); //$NON-NLS-1$
        }
        dropTargetListener = null;
    }

    public synchronized void addDropTargetListener(TDropTargetListener dtl)
            throws TooManyListenersException {
        if (dtl == null) {
            return;
        }
        if (dtl == this) {
            // awt.176=DropTarget cannot be added as listener to itself
            throw new IllegalArgumentException(Messages.getString("awt.176")); //$NON-NLS-1$
        }
        if (dropTargetListener != null) {
            throw new TooManyListenersException();
        }
        dropTargetListener = dtl;
    }

    public synchronized void dragExit(TDropTargetEvent dte) {
        if (!isActive()) {
            return;
        }
        if (dropTargetListener != null) {
            dropTargetListener.dragExit(dte);
        }
    }

    public synchronized void drop(TDropTargetDropEvent dtde) {
        if (!isActive()) {
            if (dtde == null) {
                throw new NullPointerException();
            }
            return;
        }
        if (dtde == null && dropTargetListener == null) {
            throw new NullPointerException();
        }
        if (dropTargetListener != null) {
            dropTargetListener.drop(dtde);
        }
    }

    public synchronized void dropActionChanged(TDropTargetDragEvent dtde) {
        if (!isActive()) {
            return;
        }
        if (dtde == null) {
            throw new NullPointerException();
        }
        if (dropTargetListener != null) {
            dropTargetListener.dropActionChanged(dtde);
        }
    }

    public synchronized void dragOver(TDropTargetDragEvent dtde) {
        if (!isActive()) {
            return;
        }
        if (dtde == null) {
            throw new NullPointerException();
        }
        if (dropTargetListener != null) {
            dropTargetListener.dragOver(dtde);
        }
    }

    public synchronized void dragEnter(TDropTargetDragEvent dtde) {
        if (!isActive()) {
            return;
        }
        if (dtde == null) {
            throw new NullPointerException();
        }
        if (dropTargetListener != null) {
            dropTargetListener.dragEnter(dtde);
        }
    }

    public TDropTargetContext getDropTargetContext() {
        return context;
    }

    protected TDropTargetContext createDropTargetContext() {
        return new TDropTargetContext(this);
    }

    public void setFlavorMap(FlavorMap fm) {
        synchronized (this) {
            flavorMap = (fm != null) ? fm : 
                SystemFlavorMap.getDefaultFlavorMap();
        }
    }

    public FlavorMap getFlavorMap() {
        synchronized (this) {
            return flavorMap;
        }
    }

    protected void updateAutoscroll(Point dragCursorLocn) {
        synchronized (this) {
            autoScroller.updateLocation(dragCursorLocn);
        }
    }

    protected void initializeAutoscrolling(Point p) {
        synchronized (this) {
            autoScroller = createDropTargetAutoScroller(component, p);
        }
    }

    public synchronized void setComponent(Component c) {
        if (component == c) {
            return;
        }
        Component oldComponent = component;
        component = c;
        if (oldComponent != null) {
            oldComponent.setDropTarget(null);
        }
        if (c != null) {
            c.setDropTarget(this);
        }
    }

    public synchronized Component getComponent() {
        return component;
    }

    public synchronized void setActive(boolean isActive) {
        synchronized (this) {
            active = isActive;
        }
    }

    public void setDefaultActions(int ops) {
        synchronized (this) {
            setDefaultActionsImpl(ops);
        }
    }

    private void setDefaultActionsImpl(int ops) {
        actions = ops & 
                (TDnDConstants.ACTION_LINK|TDnDConstants.ACTION_COPY_OR_MOVE);
    }

    public boolean isActive() {
        synchronized (this) {
            return active;
        }
    }

    protected void clearAutoscroll() {
        synchronized (this) {
            autoScroller = null;
        }
    }

    public int getDefaultActions() {
        synchronized (this) {
            return actions;
        }
    }

}
