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
 * @author Michael Danilov
 */
package org.teavm.classlib.java.awt.dnd;

import java.awt.*;
import java.awt.datatransfer.Transferable;
//import java.awt.dnd.peer.DragSourceContextPeer;
import java.io.Serializable;
import java.util.TooManyListenersException;

import org.apache.harmony.awt.internal.nls.Messages;
import org.teavm.classlib.java.awt.dnd.peer.TDragSourceContextPeer;

public class TDragSourceContext implements TDragSourceListener,
        TDragSourceMotionListener, Serializable
{

    private static final long serialVersionUID = -115407898692194719L;

    protected static final int DEFAULT = 0;

    protected static final int ENTER = 1;

    protected static final int OVER = 2;

    protected static final int CHANGED = 3;

    private static final int EXIT = DEFAULT;

    private final TDragSource dragSource;
    private final TDragGestureEvent trigger;
    private final Transferable transferable;
    private final Component component;
    private final TDragSourceContextPeer peer;

    private int sourceAction;
    private TDragSourceListener listener;
    private Cursor cursor;
    private boolean defaultCursor;
    private int lastTargetAction;
    private int lastStatus;

    public TDragSourceContext(TDragSourceContextPeer dscp, TDragGestureEvent trigger,
            Cursor dragCursor, Image dragImage, Point offset,
            Transferable t, TDragSourceListener dsl)
    {
        if (dscp == null) {
            // awt.179=Context peer is null.
            throw new NullPointerException(Messages.getString("awt.179")); //$NON-NLS-1$
        }
        if (trigger == null) {
            // awt.17A=Trigger event is null.
            throw new NullPointerException(Messages.getString("awt.17A")); //$NON-NLS-1$
        }
        if (trigger.getDragAction() == TDnDConstants.ACTION_NONE) {
            // awt.17B=Can't init ACTION_NONE drag.
            throw new RuntimeException(Messages.getString("awt.17B")); //$NON-NLS-1$
        }
        if ((dragImage != null) && (offset == null)) {
            // awt.17C=Image offset is null.
            throw new NullPointerException(Messages.getString("awt.17C")); //$NON-NLS-1$
        }
        if (t == null) {
            // awt.17D=Transferable is null.
            throw new NullPointerException(Messages.getString("awt.17D")); //$NON-NLS-1$
        }
        if (trigger.getComponent() == null) {
            // awt.17E=Component associated with the trigger event is null.
            throw new IllegalArgumentException(Messages.getString("awt.17E")); //$NON-NLS-1$
        }
        if (trigger.getDragSource() == null) {
            // awt.17F=DragSource for the trigger event is null.
            throw new IllegalArgumentException(Messages.getString("awt.17F")); //$NON-NLS-1$
        }
        if (trigger.getSourceAsDragGestureRecognizer().getSourceActions()
                == TDnDConstants.ACTION_NONE)
        {
            // awt.180=Source actions for the DragGestureRecognizer associated with the trigger event are equal to DnDConstants.ACTION_NONE.
            throw new IllegalArgumentException(Messages.getString("awt.180")); //$NON-NLS-1$
        }

        this.trigger = trigger;
        transferable = t;
        dragSource = trigger.getDragSource();
        sourceAction = trigger.getDragAction();
        component = trigger.getComponent();
        peer = dscp;

        try {
            addDragSourceListener(dsl);
        } catch (TooManyListenersException e) {
        }
        lastTargetAction = TDnDConstants.ACTION_NONE;
        lastStatus = DEFAULT;
        setCursor(dragCursor);
    }

    public TDragGestureEvent getTrigger() {
        return trigger;
    }

    public Transferable getTransferable() {
        return transferable;
    }

    public TDragSource getDragSource() {
        return dragSource;
    }

    public int getSourceActions() {
        return sourceAction;
    }

    public Component getComponent() {
        return component;
    }

    public Cursor getCursor() {
        return cursor;
    }

    public synchronized void setCursor(Cursor c) {
        cursor = c;

        defaultCursor = (cursor == null);
        if (defaultCursor) {
            updateCurrentCursor(sourceAction, lastTargetAction, lastStatus);
        } else {
            peer.setCursor(cursor);
        }
    }

    public synchronized void addDragSourceListener(TDragSourceListener dsl) throws TooManyListenersException {
        if (dsl == null) {
            return;
        }
        if (dsl == this) {
            // awt.181=Attempt to register context as its listener.
            throw new IllegalArgumentException(Messages.getString("awt.181")); //$NON-NLS-1$
        }
        if (listener != null) {
            // awt.173=One listener is already exist.
            throw new TooManyListenersException(Messages.getString("awt.173")); //$NON-NLS-1$
        }

        listener = dsl;
    }

    public synchronized void removeDragSourceListener(TDragSourceListener dsl) {
        if (listener != dsl) {
            // awt.182=dsl is not current listener.
            throw new IllegalArgumentException(Messages.getString("awt.182")); //$NON-NLS-1$
        }

        listener = null;
    }

    protected synchronized void updateCurrentCursor(int dropOp, int targetAct, int status) {
        if (!defaultCursor) {
            return;
        }
        if ((status < DEFAULT) || (status > CHANGED)) {
            // awt.183=Invalid status.
            throw new RuntimeException(Messages.getString("awt.183")); //$NON-NLS-1$
        }

        int possibleOps = dropOp & ((status == DEFAULT) ? TDnDConstants.ACTION_NONE : targetAct);
        int theOperation;
        boolean opEnabled;

        if (possibleOps == TDnDConstants.ACTION_NONE) {
            theOperation = findBestAction(dropOp);
            opEnabled = false;
        } else {
            theOperation = findBestAction(possibleOps);
            opEnabled = true;
        }

        peer.setCursor(findCursor(theOperation, opEnabled));
    }

    private void updateCursor(int dropOp, int targetAct, int status) {
        lastTargetAction = targetAct;
        lastStatus = status;

        updateCurrentCursor(dropOp, targetAct, status);
    }

    private int findBestAction(int actions) {
        if ((actions & TDnDConstants.ACTION_MOVE) != 0) {
            return TDnDConstants.ACTION_MOVE;
        } else if ((actions & TDnDConstants.ACTION_COPY) != 0) {
            return TDnDConstants.ACTION_COPY;
        } else  if ((actions & TDnDConstants.ACTION_LINK) != 0) {
            return TDnDConstants.ACTION_LINK;
        } else {
            return TDnDConstants.ACTION_MOVE;
        }
    }

    private Cursor findCursor(int action, boolean enabled) {
        switch (action) {
        case TDnDConstants.ACTION_MOVE:
            return (enabled ? TDragSource.DefaultMoveDrop : TDragSource.DefaultMoveNoDrop);
        case TDnDConstants.ACTION_COPY:
            return (enabled ? TDragSource.DefaultCopyDrop : TDragSource.DefaultCopyNoDrop);
        case TDnDConstants.ACTION_LINK:
            return (enabled ? TDragSource.DefaultLinkDrop : TDragSource.DefaultLinkNoDrop);
        default:
            // awt.184=Invalid action.
            throw new RuntimeException(Messages.getString("awt.184")); //$NON-NLS-1$
        }
    }

    public void transferablesFlavorsChanged() {
        peer.transferablesFlavorsChanged();
    }

    public void dragEnter(TDragSourceDragEvent dsde) {
        if (listener != null) {
            listener.dragEnter(dsde);
        }
        updateCursor(sourceAction, dsde.getTargetActions(), ENTER);
    }

    public void dragOver(TDragSourceDragEvent dsde) {
        if (listener != null) {
            listener.dragOver(dsde);
        }
        updateCursor(sourceAction, dsde.getTargetActions(), OVER);
    }

    public void dropActionChanged(TDragSourceDragEvent dsde) {
        sourceAction = dsde.getDropAction();
        if (listener != null) {
            listener.dropActionChanged(dsde);
        }
        updateCursor(sourceAction, dsde.getTargetActions(), CHANGED);
    }

    public void dragExit(TDragSourceEvent dse) {
        if (listener != null) {
            listener.dragExit(dse);
        }
        updateCursor(sourceAction, TDnDConstants.ACTION_NONE, EXIT);
    }

    public void dragDropEnd(TDragSourceDropEvent dsde) {
        if (listener != null) {
            listener.dragDropEnd(dsde);
        }
    }

    public void dragMouseMoved(TDragSourceDragEvent dsde) {
        TDragSourceMotionListener[] listeners = dragSource.getDragSourceMotionListeners();

        for (TDragSourceMotionListener element : listeners) {
            element.dragMouseMoved(dsde);
        }
    }

}
