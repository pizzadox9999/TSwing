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

package org.teavm.classlib.java.awt;

import org.teavm.classlib.java.awt.datatransfer.TClipboard;
import org.teavm.classlib.java.awt.dnd.TDragGestureEvent;
import org.teavm.classlib.java.awt.dnd.TDragGestureListener;
import org.teavm.classlib.java.awt.dnd.TDragGestureRecognizer;
import org.teavm.classlib.java.awt.dnd.TDragSource;
import org.teavm.classlib.java.awt.dnd.TInvalidDnDOperationException;
import org.teavm.classlib.java.awt.dnd.TMouseDragGestureRecognizer;
import org.teavm.classlib.java.awt.dnd.peer.TDragSourceContextPeer;
import org.teavm.classlib.java.awt.event.TAWTEventListener;
import org.teavm.classlib.java.awt.event.TAWTEventListenerProxy;
import org.teavm.classlib.java.awt.event.TInputEvent;
import org.teavm.classlib.java.awt.event.TKeyEvent;
import org.teavm.classlib.java.awt.im.TInputMethodHighlight;
import org.teavm.classlib.java.awt.image.TColorModel;
import org.teavm.classlib.java.awt.image.TImageObserver;
import org.teavm.classlib.java.awt.image.TImageProducer;
import org.teavm.classlib.java.awt.peer.TButtonPeer;
import org.teavm.classlib.java.awt.peer.TCanvasPeer;
import org.teavm.classlib.java.awt.peer.TCheckboxMenuItemPeer;
import org.teavm.classlib.java.awt.peer.TCheckboxPeer;
import org.teavm.classlib.java.awt.peer.TChoicePeer;
import org.teavm.classlib.java.awt.peer.TDialogPeer;
import org.teavm.classlib.java.awt.peer.TFileDialogPeer;
import org.teavm.classlib.java.awt.peer.TFontPeer;
import org.teavm.classlib.java.awt.peer.TFramePeer;
import org.teavm.classlib.java.awt.peer.TLabelPeer;
import org.teavm.classlib.java.awt.peer.TLightweightPeer;
import org.teavm.classlib.java.awt.peer.TListPeer;
import org.teavm.classlib.java.awt.peer.TMenuBarPeer;
import org.teavm.classlib.java.awt.peer.TMenuItemPeer;
import org.teavm.classlib.java.awt.peer.TMenuPeer;
import org.teavm.classlib.java.awt.peer.TMouseInfoPeer;
import org.teavm.classlib.java.awt.peer.TPanelPeer;
import org.teavm.classlib.java.awt.peer.TPopupMenuPeer;
import org.teavm.classlib.java.awt.peer.TScrollPanePeer;
import org.teavm.classlib.java.awt.peer.TScrollbarPeer;
import org.teavm.classlib.java.awt.peer.TTextAreaPeer;
import org.teavm.classlib.java.awt.peer.TTextFieldPeer;
import org.teavm.classlib.java.awt.peer.TWindowPeer;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.Collections;
import java.util.EventListener;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.Properties;
import java.util.ResourceBundle;

import org.apache.harmony.awt.ChoiceStyle;
import org.apache.harmony.awt.ComponentInternals;
import org.apache.harmony.awt.ContextStorage;
import org.apache.harmony.awt.MouseEventPreprocessor;
import org.apache.harmony.awt.ReadOnlyIterator;
import org.apache.harmony.awt.Theme;
import org.apache.harmony.awt.datatransfer.DTK;
import org.apache.harmony.awt.datatransfer.NativeTClipboard;
import org.apache.harmony.awt.gl.MultiRectArea;
import org.apache.harmony.awt.internal.nls.Messages;
import org.apache.harmony.awt.text.TextFieldKit;
import org.apache.harmony.awt.text.TextKit;
import org.apache.harmony.awt.wtk.CreationParams;
import org.apache.harmony.awt.wtk.GraphicsFactory;
import org.apache.harmony.awt.wtk.NativeCursor;
import org.apache.harmony.awt.wtk.NativeEventQueue;
import org.apache.harmony.awt.wtk.NativeEventThread;
import org.apache.harmony.awt.wtk.NativeMouseInfo;
import org.apache.harmony.awt.wtk.NativeWindow;
import org.apache.harmony.awt.wtk.ShutdownWatchdog;
import org.apache.harmony.awt.wtk.Synchronizer;
import org.apache.harmony.awt.wtk.WTK;
import org.apache.harmony.awt.wtk.WindowFactory;
import org.apache.harmony.luni.util.NotImplementedException;

public abstract class TToolkit {
    private static final String RECOURCE_PATH = "org.apache.harmony.awt.resources.AWTProperties"; //$NON-NLS-1$

    private static final ResourceBundle properties = loadResources(RECOURCE_PATH);

    TDispatcher dispatcher;

    private TEventQueueCore systemEventQueueCore;

    TEventDispatchThread dispatchThread;

    NativeEventThread nativeThread;

    protected AWTEventsManager awtEventsManager;

    /* key = nativeWindow, value = Component, should be Map<NativeWindow, Component> */
    private final Map<NativeWindow, Object> windowComponentMap = new HashMap<NativeWindow, Object>();

    /* key = nativeWindow, value = MenuComponent */
    private final Map<NativeWindow, Object> windowPopupMap = new HashMap<NativeWindow, Object>();

    private final Map<NativeWindow, Window> windowFocusProxyMap = new HashMap<NativeWindow, TWindow>();

    private class AWTTreeLock {
    }

    final Object awtTreeLock = new AWTTreeLock();

    private final Synchronizer synchronizer = ContextStorage.getSynchronizer();

    final ShutdownWatchdog shutdownWatchdog = new ShutdownWatchdog();

    final Theme theme = createTheme();

    final AutoNumber autoNumber = new AutoNumber();

    final AWTEvent.EventTypeLookup eventTypeLookup = new TAWTEvent.EventTypeLookup();

    final Frame.AllFrames allFrames = new TFrame.AllFrames();

    KeyboardFocusManager currentKeyboardFocusManager;

    MouseEventPreprocessor mouseEventPreprocessor;

    NativeTClipboard systemTClipboard = null;

    private NativeTClipboard systemSelection = null;

    private boolean bDynamicLayoutSet = true;

    /**
     * The set of desktop properties that user set directly.
     */
    private final HashSet<String> userPropSet = new HashSet<String>();

    protected final Map<String, Object> desktopProperties;

    protected final PropertyChangeSupport desktopPropsSupport;

    /**
     * For this component the native window is being created
     * It is used in the callback-driven window creation
     * (e.g. on Windows in the handler of WM_CREATE event)
     * to establish the connection between this component
     * and its native window
     */
    private Object recentNativeWindowComponent;

    final WindowList windows = new WindowList();

    private WTK wtk;

    DTK dtk;

    final class ComponentInternalsImpl extends ComponentInternals {
        @Override
        public NativeTWindow getNativeWindow(TComponent component) {
            lockAWT();
            try {
                return component != null ? component.getNativeWindow() : null;
            } finally {
                unlockAWT();
            }
        }

        @Override
        public void startMouseGrab(TWindow grabWindow, Runnable whenCanceled) {
            lockAWT();
            try {
                dispatcher.mouseGrabManager.startGrab(grabWindow, whenCanceled);
            } finally {
                unlockAWT();
            }
        }

        @Override
        public void endMouseGrab() {
            lockAWT();
            try {
                dispatcher.mouseGrabManager.endGrab();
            } finally {
                unlockAWT();
            }
        }

        @Override
        public TWindow attachNativeWindow(long nativeWindowId) {
            lockAWT();
            try {
                TWindow window = new TEmbeddedWindow(nativeWindowId);
                windowComponentMap.put(window.getNativeWindow(), window);
                windows.add(window);
                return window;
            } finally {
                unlockAWT();
            }
        }

        @Override
        public void makePopup(TWindow window) {
            lockAWT();
            try {
                window.setPopup(true);
            } finally {
                unlockAWT();
            }
        }

        @Override
        public void onDrawImage(TComponent comp, TImage image, TPoint destLocation,
                TDimension destSize, TRectangle source) {
            lockAWT();
            try {
                comp.onDrawImage(image, destLocation, destSize, source);
            } finally {
                unlockAWT();
            }
        }

        @Override
        public void setCaretPos(TComponent c, int x, int y) {
            c.setCaretPos(x, y);
        }

        @Override
        public void unsafeInvokeAndWait(Runnable runnable) throws InterruptedException,
                InvocationTargetException {
            TToolkit.this.unsafeInvokeAndWait(runnable);
        }

        @Override
        public TextKit getTextKit(TComponent comp) {
            lockAWT();
            try {
                return comp.getTextKit();
            } finally {
                unlockAWT();
            }
        }

        @Override
        public void setTextKit(TComponent comp, TextKit kit) {
            lockAWT();
            try {
                comp.setTextKit(kit);
            } finally {
                unlockAWT();
            }
        }

        @Override
        public TextFieldKit getTextFieldKit(TComponent comp) {
            lockAWT();
            try {
                return comp.getTextFieldKit();
            } finally {
                unlockAWT();
            }
        }

        @Override
        public void setTextFieldKit(TComponent comp, TextFieldKit kit) {
            lockAWT();
            try {
                comp.setTextFieldKit(kit);
            } finally {
                unlockAWT();
            }
        }

        @Override
        public void shutdown() {
            dispatchThread.shutdown();
        }

        @Override
        public void setMouseEventPreprocessor(MouseEventPreprocessor preprocessor) {
            lockAWT();
            try {
                mouseEventPreprocessor = preprocessor;
            } finally {
                unlockAWT();
            }
        }

        @Override
        public TChoice createCustomChoice(ChoiceStyle style) {
            return new TChoice(style);
        }

        @Override
        public TInsets getNativeInsets(TWindow w) {
            lockAWT();
            try {
                return (w != null) ? w.getNativeInsets() : new TInsets(0, 0, 0, 0);
            } finally {
                unlockAWT();
            }
        }

        @Override
        public MultiRectArea getRepaintRegion(TComponent c) {
            return c.repaintRegion;
        }

        @Override
        public MultiRectArea subtractPendingRepaintRegion(TComponent c, MultiRectArea mra) {
            lockAWT();
            try {
                TRedrawManager rm = c.getRedrawManager();
                if (rm == null) {
                    return null;
                }
                return rm.subtractPendingRepaintRegion(c, mra);
            } finally {
                unlockAWT();
            }
        }

        @Override
        public boolean wasPainted(TWindow w) {
            lockAWT();
            try {
                return w.painted;
            } finally {
                unlockAWT();
            }
        }

        @Override
        public MultiRectArea getObscuredRegion(TComponent c) {
            return c.getObscuredRegion(null);
        }

        @Override
        public void setDesktopProperty(String name, Object value) {
            TToolkit.this.setDesktopProperty(name, value);
        }

        @Override
        public void runModalLoop(TDialog dlg) {
            dlg.runModalLoop();
        }

        @Override
        public void endModalLoop(TDialog dlg) {
            dlg.endModalLoop();
        }

        @Override
        public void setVisibleFlag(TComponent comp, boolean visible) {
            comp.visible = visible;
        }

        @Override
        public void addObscuredRegions(MultiRectArea mra, TComponent c, TContainer container) {
            if (container != null) {
                container.addObscuredRegions(mra, c);
            }            
        }
    }

    /*
     * A lot of methods must throw HeadlessException
     * if <code>GraphicsEnvironment.isHeadless()</code> returns <code>true</code>.
     */
    static void checkHeadless() throws HeadlessException {
        if (TGraphicsEnvironment.getLocalGraphicsEnvironment().isHeadlessInstance())
            throw new THeadlessException();
    }

    final void lockAWT() {
        synchronizer.lock();
    }

    static final void staticLockAWT() {
        ContextStorage.getSynchronizer().lock();
    }

    final void unlockAWT() {
        synchronizer.unlock();
    }

    static final void staticUnlockAWT() {
        ContextStorage.getSynchronizer().unlock();
    }    

    /**
     * InvokeAndWait under AWT lock. W/o this method system can hang up.
     * Added to support modality (Dialog.show() & PopupMenu.show()) from
     * not event dispatch thread. Use in other cases is not recommended.
     *
     * Still can be called only for whole API methods that
     * cannot be called from other classes API methods.
     * Examples:
     *      show() for modal dialogs    - correct, only user can call it,
     *                                      directly or through setVisible(true)
     *      setBounds() for components  - incorrect, setBounds()
     *                                      can be called from layoutContainer()
     *                                      for layout managers
     */
    final void unsafeInvokeAndWait(Runnable runnable) throws InterruptedException,
            InvocationTargetException {
        synchronizer.storeStateAndFree();
        try {
            TEventQueue.invokeAndWait(runnable);
        } finally {
            synchronizer.lockAndRestoreState();
        }
    }

    final Synchronizer getSynchronizer() {
        return synchronizer;
    }

    final WTK getWTK() {
        return wtk;
    }

    public static String getProperty(String propName, String defVal) {
        if (propName == null) {
            // awt.7D=Property name is null
            throw new NullPointerException(Messages.getString("awt.7D")); //$NON-NLS-1$
        }
        staticLockAWT();
        try {
            String retVal = null;
            if (properties != null) {
                try {
                    retVal = properties.getString(propName);
                } catch (MissingResourceException e) {
                } catch (ClassCastException e) {
                }
            }
            return (retVal == null) ? defVal : retVal;
        } finally {
            staticUnlockAWT();
        }
    }

    public static TToolkit getDefaultToolkit() {
        synchronized (ContextStorage.getContextLock()) {
            if (ContextStorage.shutdownPending()) {
                return null;
            }
            TToolkit defToolkit = ContextStorage.getDefaultToolkit();
            if (defToolkit != null) {
                return defToolkit;
            }
            staticLockAWT();
            try {
                defToolkit = TGraphicsEnvironment.isHeadless() ?
                        new THeadlessToolkit() : new ToolkitImpl();
                ContextStorage.setDefaultToolkit(defToolkit);
                return defToolkit;
            } finally {
                staticUnlockAWT();
            }
            //TODO: read system property named awt.toolkit
            //and create an instance of the specified class,
            //by default use ToolkitImpl
        }
    }

    Font getDefaultFont() {
        return wtk.getSystemProperties().getDefaultFont();
    }

    private static ResourceBundle loadResources(String path) {
        try {
            return ResourceBundle.getBundle(path);
        } catch (MissingResourceException e) {
            return null;
        }
    }

    private static String getWTKClassName() {
        String osName = System.getProperty("os.name").toLowerCase(); //$NON-NLS-1$
        String packageBase = "org.apache.harmony.awt.wtk", win = "windows", lin = "linux"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        if (osName.startsWith(lin) || osName.startsWith("freebsd")) {
            return packageBase + "." + lin + ".LinuxWTK"; //$NON-NLS-1$ //$NON-NLS-2$
        }
        if (osName.startsWith(win)) {
            return packageBase + "." + win + ".WinWTK"; //$NON-NLS-1$ //$NON-NLS-2$
        }
        return null;
    }

    Component getComponentById(long id) {
        if (id == 0) {
            return null;
        }
        return (TComponent) windowComponentMap.get(getWindowFactory().getWindowById(id));
    }

    PopupBox getPopupBoxById(long id) {
        if (id == 0) {
            return null;
        }
        return (TPopupBox) windowPopupMap.get(getWindowFactory().getWindowById(id));
    }

    TWindow getFocusProxyOwnerById(long id) {
        if (id == 0) {
            return null;
        }
        return windowFocusProxyMap.get(getWindowFactory().getWindowById(id));
    }

    TWindowFactory getWindowFactory() {
        return wtk.getWindowFactory();
    }

    TGraphicsFactory getGraphicsFactory() {
        return wtk.getGraphicsFactory();
    }
    
    public TToolkit() {        
        desktopProperties = new HashMap<String, Object>();
        desktopPropsSupport = new PropertyChangeSupport(this);
        init();
    }

    void init() {
        lockAWT();
        try {
            ComponentInternals.setComponentInternals(new ComponentInternalsImpl());
            new TEventQueue(this); // create the system EventQueue
            dispatcher = new TDispatcher(this);
            final String className = getWTKClassName();
            awtEventsManager = new AWTEventsManager();
            dispatchThread = new TEventDispatchThread(this, dispatcher);
            nativeThread = new NativeEventThread();
            dtk = DTK.getDTK();
            NativeEventThread.Init init = new NativeEventThread.Init() {
                public WTK init() {
                    wtk = createWTK(className);
                    wtk.getNativeEventQueue().setShutdownWatchdog(shutdownWatchdog);
                    synchronizer.setEnvironment(wtk, dispatchThread);
                    ContextStorage.setWTK(wtk);
                    dtk.initDragAndDrop();
                    return wtk;
                }
            };
            nativeThread.start(init);
            dispatchThread.start();
            wtk.getNativeEventQueue().awake();
        } finally {
            unlockAWT();
        }
    }

    public abstract void sync();

    protected abstract TTextAreaPeer createTextArea(TTextArea a0) throws HeadlessException;

    public abstract int checkImage(Image a0, int a1, int a2, TImageObserver a3);

    public abstract TImage createImage(TImageProducer a0);

    public abstract TImage createImage(byte[] a0, int a1, int a2);

    public abstract TImage createImage(URL a0);

    public abstract TImage createImage(String a0);

    public abstract TColorModel getTColorModel() throws HeadlessException;

    /**
     * @deprecated
     */
    @Deprecated
    public abstract FontMetrics getFontMetrics(TFont font);

    public abstract boolean prepareImage(Image a0, int a1, int a2, TImageObserver a3);

    public abstract void beep();

    protected abstract TButtonPeer createButton(TButton a0) throws HeadlessException;

    protected abstract TCanvasPeer createCanvas(TCanvas a0);

    protected abstract TCheckboxPeer createCheckbox(TCheckbox a0) throws HeadlessException;

    protected abstract TCheckboxMenuItemPeer createCheckboxMenuItem(CheckboxMenuItem a0)
            throws HeadlessException;

    protected abstract TChoicePeer createChoice(TChoice a0) throws HeadlessException;

    protected abstract TDialogPeer createDialog(TDialog a0) throws HeadlessException;

    public abstract TDragSourceContextPeer createTDragSourceContextPeer(TDragGestureEvent a0)
            throws TInvalidDnDOperationException;

    protected abstract TFileDialogPeer createFileDialog(TFileDialog a0) throws HeadlessException;

    protected abstract TFramePeer createFrame(TFrame a0) throws HeadlessException;

    protected abstract TLabelPeer createLabel(TLabel a0) throws HeadlessException;

    protected abstract TListPeer createList(TList a0) throws HeadlessException;

    protected abstract TMenuPeer createMenu(TMenu a0) throws HeadlessException;

    protected abstract TMenuBarPeer createMenuBar(TMenuBar a0) throws HeadlessException;

    protected abstract TMenuItemPeer createMenuItem(TMenuItem a0) throws HeadlessException;

    protected abstract TPanelPeer createPanel(TPanel a0);

    protected abstract TPopupMenuPeer createPopupMenu(TPopupMenu a0) throws HeadlessException;

    protected abstract TScrollPanePeer createScrollPane(TScrollPane a0) throws HeadlessException;

    protected abstract TScrollbarPeer createScrollbar(TScrollbar a0) throws HeadlessException;

    protected abstract TTextFieldPeer createTextField(TTextField a0) throws HeadlessException;

    protected abstract TWindowPeer createWindow(TWindow a0) throws HeadlessException;

    /**
     * @deprecated
     */
    @Deprecated
    public abstract String[] getFontList();

    /**
     * @deprecated
     */
    @Deprecated
    protected abstract TFontPeer getTFontPeer(String a0, int a1);

    public abstract Image getImage(String a0);

    public abstract Image getImage(URL a0);

    public abstract PrintJob getPrintJob(Frame a0, String a1, Properties a2);

    public abstract int getScreenResolution() throws HeadlessException;

    public abstract Dimension getScreenSize() throws HeadlessException;

    public abstract TClipboard getSystemTClipboard() throws HeadlessException;

    protected abstract EventQueue getSystemEventQueueImpl();

    public abstract Map<java.awt.font.TextAttribute, ?> mapTInputMethodHighlight(
            TInputMethodHighlight highlight) throws HeadlessException;

    Map<java.awt.font.TextAttribute, ?> mapTInputMethodHighlightImpl(
            TInputMethodHighlight highlight) throws HeadlessException {
        HashMap<java.awt.font.TextAttribute, ?> map = new HashMap<java.awt.font.TextAttribute, Object>();
        wtk.getSystemProperties().mapTInputMethodHighlight(highlight, map);
        return Collections.<java.awt.font.TextAttribute, Object> unmodifiableMap(map);
    }

    public void addPropertyChangeListener(String propName, PropertyChangeListener l) {
        lockAWT();
        try {
            if (desktopProperties.isEmpty()) {
                initializeDesktopProperties();
            }
        } finally {
            unlockAWT();
        }
        if (l != null) { // there is no guarantee that null listener will not be added
            desktopPropsSupport.addPropertyChangeListener(propName, l);
        }
    }

    protected java.awt.peer.TMouseInfoPeer getTMouseInfoPeer() {
        return new TMouseInfoPeer() {
        };
    }

    protected TLightweightPeer createComponent(Component a0) throws NotImplementedException {
//        throw new NotImplementedException();
        throw new RuntimeException("Not Implemented");
    }

    public Image createImage(byte[] imagedata) {
        return createImage(imagedata, 0, imagedata.length);
    }

    protected static Container getNativeContainer(Component c) {
        staticLockAWT();
        try {
            //TODO: implement
            return c.getWindowAncestor();
        } finally {
            staticUnlockAWT();
        }
    }

    public PropertyChangeListener[] getPropertyChangeListeners() {
        return desktopPropsSupport.getPropertyChangeListeners();
    }

    public PropertyChangeListener[] getPropertyChangeListeners(String propName) {
        return desktopPropsSupport.getPropertyChangeListeners(propName);
    }

    public void removePropertyChangeListener(String propName, PropertyChangeListener l) {
        desktopPropsSupport.removePropertyChangeListener(propName, l);
    }

    public Cursor createCustomCursor(Image img, Point hotSpot, String name)
            throws IndexOutOfBoundsException, HeadlessException {
        lockAWT();
        try {
            int w = img.getWidth(null);
            int h = img.getHeight(null);

            if (w < 0 || h < 0) {
                // Fix for HARMONY-4491
                hotSpot.x = 0;
                hotSpot.y = 0;
            } else if (hotSpot.x < 0 || hotSpot.x >= w
                    || hotSpot.y < 0 || hotSpot.y >= h) {
                // awt.7E=invalid hotSpot
                throw new IndexOutOfBoundsException(Messages.getString("awt.7E")); //$NON-NLS-1$
            }
            return new Cursor(name, img, hotSpot);
        } finally {
            unlockAWT();
        }
    }

    @SuppressWarnings("unchecked")
    public <T extends TDragGestureRecognizer> T createTDragGestureRecognizer(
            Class<T> recognizerAbstractClass, TDragSource ds, Component c, int srcActions,
            TDragGestureListener dgl) {
        if (recognizerAbstractClass == null) {
            return null;
        }
        if (recognizerAbstractClass.isAssignableFrom(TMouseDragGestureRecognizer.class)) {
            return (T) new TDefaultTMouseDragGestureRecognizer(ds, c, srcActions, dgl);
        }
        return null;
    }

    public Dimension getBestCursorSize(int prefWidth, int prefHeight) throws HeadlessException {
        lockAWT();
        try {
            return wtk.getCursorFactory().getBestCursorSize(prefWidth, prefHeight);
        } finally {
            unlockAWT();
        }
    }

    public final Object getDesktopProperty(String propName) {
        lockAWT();
        try {
            if (desktopProperties.isEmpty()) {
                initializeDesktopProperties();
            }
            if (propName.equals("awt.dynamicLayoutSupported")) { //$NON-NLS-1$
                // dynamicLayoutSupported is special case
                return Boolean.valueOf(isDynamicLayoutActive());
            }
            Object val = desktopProperties.get(propName);
            if (val == null) {
                // try to lazily load prop value
                // just for compatibility, our lazilyLoad is empty
                val = lazilyLoadDesktopProperty(propName);
            }
            return val;
        } finally {
            unlockAWT();
        }
    }

    public boolean getLockingKeyState(int keyCode) throws UnsupportedOperationException {

        if (keyCode != TKeyEvent.VK_CAPS_LOCK &&
            keyCode != TKeyEvent.VK_NUM_LOCK &&
            keyCode != TKeyEvent.VK_SCROLL_LOCK &&
            keyCode != TKeyEvent.VK_KANA_LOCK) {
            throw new IllegalArgumentException();
        }

        return wtk.getLockingState(keyCode);
    }

    public int getMaximumCursorColors() throws HeadlessException {
        lockAWT();
        try {
            return wtk.getCursorFactory().getMaximumCursorColors();
        } finally {
            unlockAWT();
        }
    }

    public int getMenuShortcutKeyMask() throws HeadlessException {
        lockAWT();
        try {
            return TInputEvent.CTRL_MASK;
        } finally {
            unlockAWT();
        }
    }

    public PrintJob getPrintJob(Frame a0, String a1, JobAttributes a2, PageAttributes a3) throws org.apache.harmony.luni.util.NotImplementedException {
        lockAWT();
        try {
        } finally {
            unlockAWT();
        }
        throw new org.apache.harmony.luni.util.NotImplementedException();
    }

    public Insets getScreenInsets(GraphicsConfiguration gc) throws HeadlessException {
        if (gc == null) {
            throw new NullPointerException();
        }
        lockAWT();
        try {
            return new Insets(0, 0, 0, 0); //TODO: get real screen insets
        } finally {
            unlockAWT();
        }
    }

    public final EventQueue getSystemEventQueue() {
        SecurityManager sm = System.getSecurityManager();
        if (sm != null) {
            sm.checkAwtEventQueueAccess();
        }
        return getSystemEventQueueImpl();
    }

    EventQueueCore getSystemEventQueueCore() {
        return systemEventQueueCore;
    }

    void setSystemEventQueueCore(EventQueueCore core) {
        systemEventQueueCore = core;
    }

    public TClipboard getSystemSelection() throws HeadlessException {
        lockAWT();
        try {
            SecurityManager security = System.getSecurityManager();
            if (security != null) {
                security.checkSystemTClipboardAccess();
            }
            if (systemSelection == null) {
                systemSelection = dtk.getNativeSelection();
            }
            return systemSelection;
        } finally {
            unlockAWT();
        }
    }

    protected void initializeDesktopProperties() {
        lockAWT();
        try {
            wtk.getSystemProperties().init(desktopProperties);
        } finally {
            unlockAWT();
        }
    }

    public boolean isDynamicLayoutActive() throws HeadlessException {
        lockAWT();
        try {
            // always return true
            return true;
        } finally {
            unlockAWT();
        }
    }

    protected boolean isDynamicLayoutSet() throws HeadlessException {
        lockAWT();
        try {
            return bDynamicLayoutSet;
        } finally {
            unlockAWT();
        }
    }

    public boolean isFrameStateSupported(int state) throws HeadlessException {
        lockAWT();
        try {
            return wtk.getWindowFactory().isWindowStateSupported(state);
        } finally {
            unlockAWT();
        }
    }

    protected Object lazilyLoadDesktopProperty(String propName) {
        return null;
    }

    protected void loadSystemColors(int[] colors) throws HeadlessException {
        lockAWT();
        try {
        } finally {
            unlockAWT();
        }
    }

    protected final void setDesktopProperty(String propName, Object value) {
        Object oldVal;
        lockAWT();
        try {
            oldVal = getDesktopProperty(propName);
            userPropSet.add(propName);
            desktopProperties.put(propName, value);
        } finally {
            unlockAWT();
        }
        desktopPropsSupport.firePropertyChange(propName, oldVal, value);
    }

    public void setDynamicLayout(boolean dynamic) throws HeadlessException {
        lockAWT();
        try {
            bDynamicLayoutSet = dynamic;
        } finally {
            unlockAWT();
        }
    }

    public void setLockingKeyState(int keyCode, boolean on) throws UnsupportedOperationException  {

        if (keyCode != TKeyEvent.VK_CAPS_LOCK &&
            keyCode != TKeyEvent.VK_NUM_LOCK &&
            keyCode != TKeyEvent.VK_SCROLL_LOCK &&
            keyCode != TKeyEvent.VK_KANA_LOCK) {
            throw new IllegalArgumentException();
        }

        wtk.setLockingState(keyCode, on);
    }

    void onQueueEmpty() {
        if (windows.isEmpty()) {
            if (systemTClipboard != null) {
                systemTClipboard.onShutdown();
            }
            if (systemSelection != null) {
                systemSelection.onShutdown();
            }
            shutdownWatchdog.setWindowListEmpty(true);
        } else {
            for (Iterator<?> i = windows.iterator(); i.hasNext();) {
                ((TWindow) i.next()).redrawAll();
            }
        }
    }

    private WTK createWTK(String clsName) {
        WTK newWTK = null;
        try {
            newWTK = (WTK) Class.forName(clsName).newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return newWTK;
    }

    /**
     * Connect the component to its native window
     * This method is called after the synchronous window creation,
     * and also in the window creation callback if it exists (WM_CREATE on Windows)
     * Calling this method twice is OK because in second time it just does nothing.
     *
     * This is done this way because on Windows the native window gets a series of native
     * events before windowFactory.CreateWindow() returns, and the WinTWindow object should be created
     * to process them. The WM_CREATE message is guaranteed to be first in the series, so that the
     * the WM_CREATE handler creates the WinTWindow object and calls nativeWindowCreated()
     * for it.
     *
     * @param win - native window just created
     */
    void nativeWindowCreated(NativeTWindow win) {
        if (recentNativeWindowComponent == null) {
            return;
        }
        if (recentNativeWindowComponent instanceof TComponent) {
            windowComponentMap.put(win, recentNativeWindowComponent);
            ((TComponent) recentNativeWindowComponent).nativeWindowCreated(win);
        } else if (recentNativeWindowComponent instanceof TPopupBox) {
            windowPopupMap.put(win, recentNativeWindowComponent);
        }
        recentNativeWindowComponent = null;
    }

    /**
     * Connect the component to its native window
     * @param winId - id of native window just created
     */
    boolean onWindowCreated(long winId) {
        nativeWindowCreated(getWindowFactory().getWindowById(winId));
        return false;
    }

    NativeTWindow createEmbeddedNativeWindow(EmbeddedTWindow ew) {
        windows.add(ew);
        CreationParams cp = new CreationParams();
        cp.child = true;
        cp.disabled = false;
        cp.name = "EmbeddedWindow"; //$NON-NLS-1$
        cp.parentId = ew.nativeWindowId;
        cp.x = 0;
        cp.y = 0;
        TDimension size = getWindowFactory().getWindowSizeById(ew.nativeWindowId);
        cp.w = size.width;
        cp.h = size.height;
        recentNativeWindowComponent = ew;
        NativeTWindow win = getWindowFactory().createWindow(cp);
        nativeWindowCreated(win);
        shutdownWatchdog.setWindowListEmpty(false);
        return win;
    }

    NativeTWindow createNativeWindow(Component c) {
        if (c instanceof TWindow) {
            windows.add(c);
        }
        TComponent parent = null;
        TPoint location = c.getLocation();
        CreationParams cp = new CreationParams();
        cp.child = !(c instanceof TWindow);
        cp.disabled = !c.isEnabled();
        if (c instanceof TWindow) {
            TWindow w = (TWindow) c;
            cp.resizable = w.isResizable();
            cp.undecorated = w.isUndecorated();
            parent = w.getOwner();
            cp.locationByPlatform = w.locationByPlatform;
            if (c instanceof TFrame) {
                TFrame frame = (TFrame) c;
                int state = frame.getExtendedState();
                cp.name = frame.getTitle();
                cp.iconified = (state & TFrame.ICONIFIED) != 0;
                cp.maximizedState = 0;
                if ((state & TFrame.MAXIMIZED_BOTH) != 0) {
                    cp.maximizedState |= cp.MAXIMIZED;
                }
                if ((state & TFrame.MAXIMIZED_HORIZ) != 0) {
                    cp.maximizedState |= cp.MAXIMIZED_HORIZ;
                }
                if ((state & TFrame.MAXIMIZED_VERT) != 0) {
                    cp.maximizedState |= cp.MAXIMIZED_VERT;
                }
                cp.decorType = CreationParams.DECOR_TYPE_FRAME;
            } else if (c instanceof TDialog) {
                TDialog dlg = (TDialog) c;
                cp.name = dlg.getTitle();
                cp.decorType = CreationParams.DECOR_TYPE_DIALOG;
            } else if (w.isPopup()) {
                cp.decorType = CreationParams.DECOR_TYPE_POPUP;
            } else {
                cp.decorType = CreationParams.DECOR_TYPE_UNDECOR;
            }
        } else {
            parent = c.getHWAncestor();
            cp.name = c.getName();
            //set location relative to the nearest heavy weight ancestor
            location = TMouseDispatcher.convertPoint(c, 0, 0, parent);
        }
        if (parent != null) {
            NativeTWindow nativeParent = parent.getNativeWindow();
            if (nativeParent == null) {
                if (cp.child) {
                    return null; //component's window will be created when its parent is created ???
                }
                parent.mapToDisplay(true); //TODO: verify it
                nativeParent = parent.getNativeWindow();
            }
            cp.parentId = nativeParent.getId();
        }
        cp.x = location.x;
        cp.y = location.y;
        cp.w = c.getWidth();
        cp.h = c.getHeight();
        recentNativeWindowComponent = c;
        NativeTWindow win = getWindowFactory().createWindow(cp);
        nativeWindowCreated(win);
        if (c instanceof TWindow) {
            shutdownWatchdog.setWindowListEmpty(false);
        }
        return win;
    }

    void removeNativeWindow(NativeTWindow w) {
        TComponent comp = (TComponent) windowComponentMap.get(w);
        if ((comp != null) && (comp instanceof TWindow)) {
            windows.remove(comp);
        }
        windowComponentMap.remove(w);
    }

    NativeTWindow createPopupNativeWindow(PopupBox popup) {
        CreationParams cp = new CreationParams();
        cp.child = popup.isMenuBar();
        cp.disabled = false;
        cp.resizable = false;
        cp.undecorated = true;
        cp.iconified = false;
        cp.visible = false;
        cp.maximizedState = 0;
        cp.decorType = CreationParams.DECOR_TYPE_POPUP;
        NativeTWindow nativeParent;
        if (popup.getParent() != null) {
            nativeParent = popup.getParent().getNativeWindow();
        } else {
            nativeParent = popup.getOwner().getNativeWindow();
        }
        assert nativeParent != null;
        cp.parentId = nativeParent.getId();
        cp.x = popup.getLocation().x;
        cp.y = popup.getLocation().y;
        cp.w = popup.getSize().width;
        cp.h = popup.getSize().height;
        recentNativeWindowComponent = popup;
        NativeTWindow win = getWindowFactory().createWindow(cp);
        nativeWindowCreated(win);
        return win;
    }

    void removePopupNativeWindow(NativeTWindow w) {
        windowPopupMap.remove(w);
    }

    NativeTWindow createFocusProxyNativeWindow(TWindow owner) {
        CreationParams cp = new CreationParams();
        cp.child = true;
        cp.disabled = false;
        cp.resizable = false;
        cp.undecorated = true;
        cp.iconified = false;
        cp.visible = true;
        cp.maximizedState = 0;
        cp.decorType = CreationParams.DECOR_TYPE_NONE;
        cp.parentId = owner.getNativeWindow().getId();
        cp.x = -10;
        cp.y = -10;
        cp.w = 1;
        cp.h = 1;
        NativeTWindow win = getWindowFactory().createWindow(cp);
        windowFocusProxyMap.put(win, owner);
        return win;
    }

    void removeFocusProxyNativeWindow(NativeTWindow w) {
        windowFocusProxyMap.remove(w);
    }

    NativeEventQueue getNativeEventQueue() {
        return wtk.getNativeEventQueue();
    }

    Object getEventMonitor(){
	return wtk.getNativeEventQueue().getEventMonitor();	
    }

    /**
     * Returns a shared instance of implementation of org.apache.harmony.awt.wtk.NativeCursor
     * for current platform for
     * @param type - Java Cursor type
     * @return new instance of implementation of NativeCursor
     */
    NativeCursor createNativeCursor(int type) {
        return wtk.getCursorFactory().getCursor(type);
    }

    /**
     * Returns a shared instance of implementation of org.apache.harmony.awt.wtk.NativeCursor
     * for current platform for custom cursor
     * @param type - Java Cursor type
     * @return new instance of implementation of NativeCursor
     */
    NativeCursor createCustomNativeCursor(Image img, Point hotSpot, String name) {
        return wtk.getCursorFactory().createCustomCursor(img, hotSpot.x, hotSpot.y);
    }

    /**
     * Returns implementation of org.apache.harmony.awt.wtk.NativeMouseInfo
     * for current platform.
     * @return implementation of NativeMouseInfo
     */
    NativeMouseInfo getNativeMouseInfo() {
        return wtk.getNativeMouseInfo();
    }

    public void addTAWTEventListener(TAWTEventListener listener, long eventMask) {
        lockAWT();
        try {
            SecurityManager security = System.getSecurityManager();
            if (security != null) {
                security.checkPermission(awtEventsManager.permission);
            }
            awtEventsManager.addTAWTEventListener(listener, eventMask);
        } finally {
            unlockAWT();
        }
    }

    public void removeTAWTEventListener(TAWTEventListener listener) {
        lockAWT();
        try {
            SecurityManager security = System.getSecurityManager();
            if (security != null) {
                security.checkPermission(awtEventsManager.permission);
            }
            awtEventsManager.removeTAWTEventListener(listener);
        } finally {
            unlockAWT();
        }
    }

    public TAWTEventListener[] getTAWTEventListeners() {
        lockAWT();
        try {
            SecurityManager security = System.getSecurityManager();
            if (security != null) {
                security.checkPermission(awtEventsManager.permission);
            }
            return awtEventsManager.getTAWTEventListeners();
        } finally {
            unlockAWT();
        }
    }

    public TAWTEventListener[] getTAWTEventListeners(long eventMask) {
        lockAWT();
        try {
            SecurityManager security = System.getSecurityManager();
            if (security != null) {
                security.checkPermission(awtEventsManager.permission);
            }
            return awtEventsManager.getTAWTEventListeners(eventMask);
        } finally {
            unlockAWT();
        }
    }

    void dispatchAWTEvent(AWTEvent event) {
        awtEventsManager.dispatchAWTEvent(event);
    }

    private static Theme createTheme() {
        String osName = System.getProperty("os.name").toLowerCase(); //$NON-NLS-1$
        String packageBase = "org.apache.harmony.awt.theme", win = "windows", lin = "linux"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        String className = org.apache.harmony.awt.Utils.getSystemProperty("awt.theme"); //$NON-NLS-1$

        if (className == null) {
            if (osName.startsWith(lin)) {
                className = packageBase + "." + lin + ".LinuxTheme"; //$NON-NLS-1$ //$NON-NLS-2$
            } else if (osName.startsWith(win)) {
                className = packageBase + "." + win + ".WinTheme"; //$NON-NLS-1$ //$NON-NLS-2$
            }
        }
        if (className != null) {
            try {
                return (Theme) Class.forName(className).newInstance();
            } catch (Exception e) {
            }
        }
        return new Theme();
    }

    final class AWTEventsManager {
        TAWTPermission permission = new TAWTPermission("listenToAllAWTEvents"); //$NON-NLS-1$

        private final TAWTListenerList<TAWTEventListenerProxy> listeners = new TAWTListenerList<TAWTEventListenerProxy>();

        void addTAWTEventListener(TAWTEventListener listener, long eventMask) {
            if (listener != null) {
                listeners.addUserListener(new TAWTEventListenerProxy(eventMask, listener));
            }
        }

        void removeTAWTEventListener(TAWTEventListener listener) {
            if (listener != null) {
                for (TAWTEventListenerProxy proxy : listeners.getUserListeners()) {
                    if (listener == proxy.getListener()) {
                        listeners.removeUserListener(proxy);
                        return;
                    }
                }
            }
        }

        TAWTEventListener[] getTAWTEventListeners() {
            HashSet<EventListener> listenersSet = new HashSet<EventListener>();
            for (TAWTEventListenerProxy proxy : listeners.getUserListeners()) {
                listenersSet.add(proxy.getListener());
            }
            return listenersSet.toArray(new TAWTEventListener[listenersSet.size()]);
        }

        TAWTEventListener[] getTAWTEventListeners(long eventMask) {
            HashSet<EventListener> listenersSet = new HashSet<EventListener>();
            for (TAWTEventListenerProxy proxy : listeners.getUserListeners()) {
                if ((proxy.getEventMask() & eventMask) == eventMask) {
                    listenersSet.add(proxy.getListener());
                }
            }
            return listenersSet.toArray(new TAWTEventListener[listenersSet.size()]);
        }

        void dispatchAWTEvent(TAWTEvent event) {
            TAWTEvent.EventDescriptor descriptor = eventTypeLookup.getEventDescriptor(event);
            if (descriptor == null) {
                return;
            }
            for (TAWTEventListenerProxy proxy : listeners.getUserListeners()) {
                if ((proxy.getEventMask() & descriptor.eventMask) != 0) {
                    proxy.eventDispatched(event);
                }
            }
        }
    }

    static final class AutoNumber {
        int nextComponent = 0;

        int nextCanvas = 0;

        int nextPanel = 0;

        int nextTWindow = 0;

        int nextFrame = 0;

        int nextDialog = 0;

        int nextButton = 0;

        int nextMenuComponent = 0;

        int nextLabel = 0;

        int nextCheckBox = 0;

        int nextScrollbar = 0;

        int nextScrollPane = 0;

        int nextList = 0;

        int nextChoice = 0;

        int nextFileDialog = 0;

        int nextTextArea = 0;

        int nextTextField = 0;
    }

    /**
     * Thread-safe collection of TWindow objects
     */
    static final class WindowList {
        /**
         * If a non-dispatch thread adds/removes a window,
         * this set it is replaced to avoid the possible conflict
         * with concurrently running lock-free iterator loop
         */
        private LinkedHashSet<TComponent> windows = new LinkedHashSet<TComponent>();

        private class Lock {
        }

        private final Object lock = new Lock();

        @SuppressWarnings("unchecked")
        void add(TComponent w) {
            synchronized (lock) {
                if (isDispatchThread()) {
                    windows.add(w);
                } else {
                    windows = (LinkedHashSet<TComponent>) windows.clone();
                    windows.add(w);
                }
            }
        }

        @SuppressWarnings("unchecked")
        void remove(TComponent w) {
            synchronized (lock) {
                if (isDispatchThread()) {
                    windows.remove(w);
                } else {
                    windows = (LinkedHashSet<TComponent>) windows.clone();
                    windows.remove(w);
                }
            }
        }

        Iterator<TComponent> iterator() {
            synchronized (lock) {
                return new ReadOnlyIterator<TComponent>(windows.iterator());
            }
        }

        boolean isEmpty() {
            synchronized (lock) {
                return windows.isEmpty();
            }
        }

        private boolean isDispatchThread() {
            return Thread.currentThread() instanceof TEventDispatchThread;
        }
    }
}
