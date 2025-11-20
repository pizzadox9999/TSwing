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
 * @author Dmitry A. Durnev, Michael Danilov
 */
package org.teavm.classlib.java.awt;

import java.util.EventListener;
import java.util.EventObject;
import java.util.Hashtable;
import org.teavm.classlib.java.awt.event.TActionEvent;
import org.teavm.classlib.java.awt.event.TActionListener;
import org.teavm.classlib.java.awt.event.TAdjustmentEvent;
import org.teavm.classlib.java.awt.event.TAdjustmentListener;
import org.teavm.classlib.java.awt.event.TComponentEvent;
import org.teavm.classlib.java.awt.event.TComponentListener;
import org.teavm.classlib.java.awt.event.TContainerEvent;
import org.teavm.classlib.java.awt.event.TContainerListener;
import org.teavm.classlib.java.awt.event.TFocusEvent;
import org.teavm.classlib.java.awt.event.TFocusListener;
import org.teavm.classlib.java.awt.event.THierarchyBoundsListener;
import org.teavm.classlib.java.awt.event.THierarchyEvent;
import org.teavm.classlib.java.awt.event.THierarchyListener;
import org.teavm.classlib.java.awt.event.TInputMethodEvent;
import org.teavm.classlib.java.awt.event.TInputMethodListener;
import org.teavm.classlib.java.awt.event.TInvocationEvent;
import org.teavm.classlib.java.awt.event.TItemEvent;
import org.teavm.classlib.java.awt.event.TItemListener;
import org.teavm.classlib.java.awt.event.TKeyEvent;
import org.teavm.classlib.java.awt.event.TKeyListener;
import org.teavm.classlib.java.awt.event.TMouseEvent;
import org.teavm.classlib.java.awt.event.TMouseListener;
import org.teavm.classlib.java.awt.event.TMouseMotionListener;
import org.teavm.classlib.java.awt.event.TMouseWheelListener;
import org.teavm.classlib.java.awt.event.TPaintEvent;
import org.teavm.classlib.java.awt.event.TTextEvent;
import org.teavm.classlib.java.awt.event.TTextListener;
import org.teavm.classlib.java.awt.event.TWindowEvent;
import org.teavm.classlib.java.awt.event.TWindowFocusListener;
import org.teavm.classlib.java.awt.event.TWindowListener;
import org.teavm.classlib.java.awt.event.TWindowStateListener;

public abstract class TAWTEvent extends EventObject {
    private static final long serialVersionUID = -1825314779160409405L;

    public static final long COMPONENT_EVENT_MASK = 1;

    public static final long CONTAINER_EVENT_MASK = 2;

    public static final long FOCUS_EVENT_MASK = 4;

    public static final long KEY_EVENT_MASK = 8;

    public static final long MOUSE_EVENT_MASK = 16;

    public static final long MOUSE_MOTION_EVENT_MASK = 32;

    public static final long WINDOW_EVENT_MASK = 64;

    public static final long ACTION_EVENT_MASK = 128;

    public static final long ADJUSTMENT_EVENT_MASK = 256;

    public static final long ITEM_EVENT_MASK = 512;

    public static final long TEXT_EVENT_MASK = 1024;

    public static final long INPUT_METHOD_EVENT_MASK = 2048;

    public static final long PAINT_EVENT_MASK = 8192;

    public static final long INVOCATION_EVENT_MASK = 16384;

    public static final long HIERARCHY_EVENT_MASK = 32768;

    public static final long HIERARCHY_BOUNDS_EVENT_MASK = 65536;

    public static final long MOUSE_WHEEL_EVENT_MASK = 131072;

    public static final long WINDOW_STATE_EVENT_MASK = 262144;

    public static final long WINDOW_FOCUS_EVENT_MASK = 524288;

    public static final int RESERVED_ID_MAX = 1999;

    private static final Hashtable<Integer, EventDescriptor> eventsMap = new Hashtable<Integer, EventDescriptor>();

    private static EventConverter converter;

    protected int id;

    protected boolean consumed;

    boolean dispatchedByKFM;
    
    transient boolean isPosted;

    static {
        eventsMap.put(new Integer(TKeyEvent.KEY_TYPED),
                new EventDescriptor(KEY_EVENT_MASK, TKeyListener.class));
        eventsMap.put(new Integer(TKeyEvent.KEY_PRESSED),
                new EventDescriptor(KEY_EVENT_MASK, TKeyListener.class));
        eventsMap.put(new Integer(TKeyEvent.KEY_RELEASED),
                new EventDescriptor(KEY_EVENT_MASK, TKeyListener.class));
        eventsMap.put(new Integer(TMouseEvent.MOUSE_CLICKED),
                new EventDescriptor(MOUSE_EVENT_MASK, TMouseListener.class));
        eventsMap.put(new Integer(TMouseEvent.MOUSE_PRESSED),
                new EventDescriptor(MOUSE_EVENT_MASK, TMouseListener.class));
        eventsMap.put(new Integer(TMouseEvent.MOUSE_RELEASED),
                new EventDescriptor(MOUSE_EVENT_MASK, TMouseListener.class));
        eventsMap.put(new Integer(TMouseEvent.MOUSE_MOVED),
                new EventDescriptor(MOUSE_MOTION_EVENT_MASK, TMouseMotionListener.class));
        eventsMap.put(new Integer(TMouseEvent.MOUSE_ENTERED),
                new EventDescriptor(MOUSE_EVENT_MASK, TMouseListener.class));
        eventsMap.put(new Integer(TMouseEvent.MOUSE_EXITED),
                new EventDescriptor(MOUSE_EVENT_MASK, TMouseListener.class));
        eventsMap.put(new Integer(TMouseEvent.MOUSE_DRAGGED),
                new EventDescriptor(MOUSE_MOTION_EVENT_MASK, TMouseMotionListener.class));
        eventsMap.put(new Integer(TMouseEvent.MOUSE_WHEEL),
                new EventDescriptor(MOUSE_WHEEL_EVENT_MASK, TMouseWheelListener.class));
        eventsMap.put(new Integer(TComponentEvent.COMPONENT_MOVED),
                new EventDescriptor(COMPONENT_EVENT_MASK, TComponentListener.class));
        eventsMap.put(new Integer(TComponentEvent.COMPONENT_RESIZED),
                new EventDescriptor(COMPONENT_EVENT_MASK, TComponentListener.class));
        eventsMap.put(new Integer(TComponentEvent.COMPONENT_SHOWN),
                new EventDescriptor(COMPONENT_EVENT_MASK, TComponentListener.class));
        eventsMap.put(new Integer(TComponentEvent.COMPONENT_HIDDEN),
                new EventDescriptor(COMPONENT_EVENT_MASK, TComponentListener.class));
        eventsMap.put(new Integer(TFocusEvent.FOCUS_GAINED),
                new EventDescriptor(FOCUS_EVENT_MASK, TFocusListener.class));
        eventsMap.put(new Integer(TFocusEvent.FOCUS_LOST),
                new EventDescriptor(FOCUS_EVENT_MASK, TFocusListener.class));
        eventsMap.put(new Integer(TPaintEvent.PAINT),
                new EventDescriptor(PAINT_EVENT_MASK, null));
        eventsMap.put(new Integer(TPaintEvent.UPDATE),
                new EventDescriptor(PAINT_EVENT_MASK, null));
        eventsMap.put(new Integer(TWindowEvent.WINDOW_OPENED),
                new EventDescriptor(WINDOW_EVENT_MASK, TWindowListener.class));
        eventsMap.put(new Integer(TWindowEvent.WINDOW_CLOSING),
                new EventDescriptor(WINDOW_EVENT_MASK, TWindowListener.class));
        eventsMap.put(new Integer(TWindowEvent.WINDOW_CLOSED),
                new EventDescriptor(WINDOW_EVENT_MASK, TWindowListener.class));
        eventsMap.put(new Integer(TWindowEvent.WINDOW_DEICONIFIED),
                new EventDescriptor(WINDOW_EVENT_MASK, TWindowListener.class));
        eventsMap.put(new Integer(TWindowEvent.WINDOW_ICONIFIED),
                new EventDescriptor(WINDOW_EVENT_MASK, TWindowListener.class));
        eventsMap.put(new Integer(TWindowEvent.WINDOW_STATE_CHANGED),
                new EventDescriptor(WINDOW_STATE_EVENT_MASK, TWindowStateListener.class));
        eventsMap.put(new Integer(TWindowEvent.WINDOW_LOST_FOCUS),
                new EventDescriptor(WINDOW_FOCUS_EVENT_MASK, TWindowFocusListener.class));
        eventsMap.put(new Integer(TWindowEvent.WINDOW_GAINED_FOCUS),
                new EventDescriptor(WINDOW_FOCUS_EVENT_MASK, TWindowFocusListener.class));
        eventsMap.put(new Integer(TWindowEvent.WINDOW_DEACTIVATED),
                new EventDescriptor(WINDOW_EVENT_MASK, TWindowListener.class));
        eventsMap.put(new Integer(TWindowEvent.WINDOW_ACTIVATED),
                new EventDescriptor(WINDOW_EVENT_MASK, TWindowListener.class));
        eventsMap.put(new Integer(THierarchyEvent.HIERARCHY_CHANGED),
                new EventDescriptor(HIERARCHY_EVENT_MASK, THierarchyListener.class));
        eventsMap.put(new Integer(THierarchyEvent.ANCESTOR_MOVED),
                new EventDescriptor(HIERARCHY_BOUNDS_EVENT_MASK, THierarchyBoundsListener.class));
        eventsMap.put(new Integer(THierarchyEvent.ANCESTOR_RESIZED),
                new EventDescriptor(HIERARCHY_BOUNDS_EVENT_MASK, THierarchyBoundsListener.class));
        eventsMap.put(new Integer(TContainerEvent.COMPONENT_ADDED),
                new EventDescriptor(CONTAINER_EVENT_MASK, TContainerListener.class));
        eventsMap.put(new Integer(TContainerEvent.COMPONENT_REMOVED),
                new EventDescriptor(CONTAINER_EVENT_MASK, TContainerListener.class));
        eventsMap.put(new Integer(TInputMethodEvent.INPUT_METHOD_TEXT_CHANGED),
                new EventDescriptor(INPUT_METHOD_EVENT_MASK, TInputMethodListener.class));
        eventsMap.put(new Integer(TInputMethodEvent.CARET_POSITION_CHANGED),
                new EventDescriptor(INPUT_METHOD_EVENT_MASK, TInputMethodListener.class));
        eventsMap.put(new Integer(TInvocationEvent.INVOCATION_DEFAULT),
                new EventDescriptor(INVOCATION_EVENT_MASK, null));
        eventsMap.put(new Integer(TItemEvent.ITEM_STATE_CHANGED),
                new EventDescriptor(ITEM_EVENT_MASK, TItemListener.class));
        eventsMap.put(new Integer(TTextEvent.TEXT_VALUE_CHANGED),
                new EventDescriptor(TEXT_EVENT_MASK, TTextListener.class));
        eventsMap.put(new Integer(TActionEvent.ACTION_PERFORMED),
                new EventDescriptor(ACTION_EVENT_MASK, TActionListener.class));
        eventsMap.put(new Integer(TAdjustmentEvent.ADJUSTMENT_VALUE_CHANGED),
                new EventDescriptor(ADJUSTMENT_EVENT_MASK, TAdjustmentListener.class));
        converter = new EventConverter();
    }

    public TAWTEvent(TEvent event) {
        this(event.target, event.id);
    }

    public TAWTEvent(Object source, int id) {
        super(source);
        this.id = id;
        consumed = false;
    }

    public int getID() {
        return id;
    }

    public void setSource(Object newSource) {
        source = newSource;
    }

    @Override
    public String toString() {
        /* The format is based on 1.5 release behavior 
         * which can be revealed by the following code:
         * 
         * AWTEvent event = new AWTEvent(new Component(){}, 1){};
         * System.out.println(event);
         */
        String name = ""; //$NON-NLS-1$
        if (source instanceof TComponent && (source != null)) {
            TComponent comp = (TComponent) getSource();
            name = comp.getName();
            if (name == null) {
                name = ""; //$NON-NLS-1$
            }
        }
        return (getClass().getName() + "[" + paramString() + "]" //$NON-NLS-1$ //$NON-NLS-2$
                + " on " + (name.length() > 0 ? name : source)); //$NON-NLS-1$
    }

    public String paramString() {
        //nothing to implement: all event types must override this method
        return ""; //$NON-NLS-1$
    }

    protected boolean isConsumed() {
        return consumed;
    }

    protected void consume() {
       consumed = true;
    }

    /**
     * Convert AWTEvent object to deprecated TEvent object
     *
     * @return new TEvent object which is a converted AWTEvent object or null
     *         if the conversion is not possible
     */
    TEvent getEvent() {

        if (id == TActionEvent.ACTION_PERFORMED) {
            TActionEvent ae = (TActionEvent) this;
            return converter.convertTActionEvent(ae);

        } else if (id == TAdjustmentEvent.ADJUSTMENT_VALUE_CHANGED) {
            TAdjustmentEvent ae = (TAdjustmentEvent) this;
            return converter.convertTAdjustmentEvent(ae);

        } else if (id == TComponentEvent.COMPONENT_MOVED
                && source instanceof TWindow) {
            //the only type of Component events is COMPONENT_MOVED on window
            TComponentEvent ce = (TComponentEvent) this;
            return converter.convertTComponentEvent(ce);

        } else if (id >= TFocusEvent.FOCUS_FIRST && id <= TFocusEvent.FOCUS_LAST) {
            //nothing to convert
        } else if (id == TItemEvent.ITEM_STATE_CHANGED) {
            TItemEvent ie = (TItemEvent) this;
            return converter.convertTItemEvent(ie);

        } else if (id == TKeyEvent.KEY_PRESSED || id == TKeyEvent.KEY_RELEASED) {
            TKeyEvent ke = (TKeyEvent) this;
            return converter.convertTKeyEvent(ke);
        } else if (id >= TMouseEvent.MOUSE_FIRST && id <= TMouseEvent.MOUSE_LAST) {
            TMouseEvent me = (TMouseEvent) this;
            return converter.convertTMouseEvent(me);
        } else if (id == TWindowEvent.WINDOW_CLOSING
                || id == TWindowEvent.WINDOW_ICONIFIED
                || id == TWindowEvent.WINDOW_DEICONIFIED) {
            //nothing to convert
        } else {
            return null;
        }

        return new TEvent(source, id, null);
    }


    static final class EventDescriptor {

        final long eventMask;

        final Class<? extends EventListener> listenerType;

        EventDescriptor(long eventMask, Class<? extends EventListener> listenerType) {
            this.eventMask = eventMask;
            this.listenerType = listenerType;
        }

    }
    static final class EventTypeLookup {
        private TAWTEvent lastEvent = null;
        private EventDescriptor lastEventDescriptor = null;

        EventDescriptor getEventDescriptor(TAWTEvent event) {
            synchronized (this) {
                if (event != lastEvent) {
                    lastEvent = event;
                    lastEventDescriptor = eventsMap.get(new Integer(event.id));
                }

                return lastEventDescriptor;
            }
        }

        long getEventMask(TAWTEvent event) {
            final EventDescriptor ed = getEventDescriptor(event);
            return ed == null ? -1 : ed.eventMask;
        }
    }

    static final class EventConverter {
        static final int OLD_MOD_MASK = TEvent.ALT_MASK | TEvent.CTRL_MASK
        | TEvent.META_MASK | TEvent.SHIFT_MASK;

        TEvent convertTActionEvent(TActionEvent ae) {
            TEvent evt = new TEvent(ae.getSource(), ae.getID(), ae.getActionCommand());
            evt.when = ae.getWhen();
            evt.modifiers = ae.getModifiers() & OLD_MOD_MASK;

           /* if (source instanceof Button) {
                arg = ((Button) source).getLabel();
            } else if (source instanceof Checkbox) {
                arg = new Boolean(((Checkbox) source).getState());
            } else if (source instanceof CheckboxMenuItem) {
                arg = ((CheckboxMenuItem) source).getLabel();
            } else if (source instanceof Choice) {
                arg = ((Choice) source).getSelectedItem();
            } else if (source instanceof List) {
                arg = ((List) source).getSelectedItem();
            } else if (source instanceof MenuItem) {
                arg = ((MenuItem) source).getLabel();
            } else if (source instanceof TextField) {
                arg = ((TextField) source).getText();
            }
*/
            return evt;
        }

        TEvent convertTAdjustmentEvent(TAdjustmentEvent ae) {
            //TODO: TEvent.SCROLL_BEGIN/SCROLL_END
            return new TEvent(ae.source, ae.id + ae.getAdjustmentType() - 1,
                    new Integer(ae.getValue()));
        }

        TEvent convertTComponentEvent(TComponentEvent ce) {
            TComponent comp = ce.getComponent();
            TEvent evt = new TEvent(comp, TEvent.WINDOW_MOVED, null);
            evt.x = comp.getX();
            evt.y = comp.getY();
            return evt;
        }

        TEvent convertTItemEvent(TItemEvent ie) {
            int oldId = ie.id + ie.getStateChange() - 1;
            Object source = ie.source;
            int idx = -1;
            if (source instanceof TList) {
                TList list = (TList) source;
                idx = list.getSelectedIndex();
            }
            else if (source instanceof TChoice) {
                TChoice choice = (TChoice) source;
                idx = choice.getSelectedIndex();
            }
            Object arg = idx >= 0 ? new Integer(idx) : null;
            return new TEvent(source, oldId, arg);
        }

        TEvent convertTKeyEvent(TKeyEvent ke) {
            int oldId = ke.id;
            //leave only old Event's modifiers

            int mod = ke.getModifiers() & OLD_MOD_MASK;
            TComponent comp = ke.getComponent();
            char keyChar = ke.getKeyChar();
            int keyCode = ke.getKeyCode();
            int key = convertKey(keyChar, keyCode);
            if (key >= TEvent.HOME && key <= TEvent.INSERT) {
                oldId += 2; //non-ASCII key -> action key
            }
            return new TEvent(comp, ke.getWhen(), oldId, 0, 0, key, mod);
        }

        TEvent convertTMouseEvent(TMouseEvent me) {
            int id = me.id;
            if (id != TMouseEvent.MOUSE_CLICKED) {
                TEvent evt = new TEvent(me.source, id, null);
                evt.x = me.getX();
                evt.y = me.getY();
                int mod = me.getModifiers();
                //in TEvent modifiers mean button number for mouse events:
                evt.modifiers = mod & (TEvent.ALT_MASK | TEvent.META_MASK);
                if (id == TMouseEvent.MOUSE_PRESSED) {
                    evt.clickCount = me.getClickCount();
                }
                return evt;
            }
            return null;
        }

        int convertKey(char keyChar, int keyCode) {
            int key;
            //F1 - F12
            if (keyCode >= TKeyEvent.VK_F1 && keyCode <= TKeyEvent.VK_F12) {
                key = TEvent.F1 + keyCode - TKeyEvent.VK_F1;
            } else {
                switch (keyCode) {
                default: //non-action key
                    key = keyChar;
                    break;
                //action keys:
                case TKeyEvent.VK_HOME:
                    key = TEvent.HOME;
                    break;
                case TKeyEvent.VK_END:
                    key = TEvent.END;
                    break;
                case TKeyEvent.VK_PAGE_UP:
                    key = TEvent.PGUP;
                    break;
                case TKeyEvent.VK_PAGE_DOWN:
                    key = TEvent.PGDN;
                    break;
                case TKeyEvent.VK_UP:
                    key = TEvent.UP;
                    break;
                case TKeyEvent.VK_DOWN:
                    key = TEvent.DOWN;
                    break;
                case TKeyEvent.VK_LEFT:
                    key = TEvent.LEFT;
                    break;
                case TKeyEvent.VK_RIGHT:
                    key = TEvent.RIGHT;
                    break;
                case TKeyEvent.VK_PRINTSCREEN:
                    key = TEvent.PRINT_SCREEN;
                    break;
                case TKeyEvent.VK_SCROLL_LOCK:
                    key = TEvent.SCROLL_LOCK;
                    break;
                case TKeyEvent.VK_CAPS_LOCK:
                    key = TEvent.CAPS_LOCK;
                    break;
                case TKeyEvent.VK_NUM_LOCK:
                    key = TEvent.NUM_LOCK;
                    break;
                case TKeyEvent.VK_PAUSE:
                    key = TEvent.PAUSE;
                    break;
                case TKeyEvent.VK_INSERT:
                    key = TEvent.INSERT;
                    break;
                }
            }
            return key;
        }

    }

}
