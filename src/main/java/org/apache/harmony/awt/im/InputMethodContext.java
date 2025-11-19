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
 * @author Dmitry A. Durnev
 */
package org.apache.harmony.awt.im;

import java.awt.AWTEvent;
import java.awt.Component;
import java.awt.KeyboardFocusManager;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.event.FocusEvent;
import java.awt.event.InputMethodEvent;
import java.awt.event.KeyEvent;
import java.awt.font.TextHitInfo;

import java.lang.Character.Subset;
import java.text.AttributedCharacterIterator;
import java.text.AttributedCharacterIterator.Attribute;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.swing.JFrame;

import org.apache.harmony.awt.wtk.NativeIM;
import org.teavm.classlib.java.awt.im.TInputContext;
import org.teavm.classlib.java.awt.im.TInputMethodRequests;
import org.teavm.classlib.java.awt.im.spi.TInputMethod;
import org.teavm.classlib.java.awt.im.spi.TInputMethodDescriptor;

/**
 * Implementation of InputMethodContext
 * interface, also provides all useful
 * functionality of InputContext
 * 
 */
public class InputMethodContext extends TInputContext implements
        java.awt.im.spi.InputMethodContext {    

    private TInputMethod inputMethod; // current IM
    private Component client; // current "active" client component
    private CompositionWindow composeWindow; // composition Window    
    private final Map<TInputMethodDescriptor, TInputMethod> imInstances; // Map<InputMethodDescriptor, InputMethod>
    private final Map<Locale, TInputMethod> localeIM; // Map<Locale, InputMethod> last user-selected IM for locale
    private final Set<TInputMethod> notifyIM; // set of IMs to notify of client window bounds changes
    /**
     * a flag indicating that IM should be notified of client window
     * position/visibility changes as soon as it is activated(new client
     * appears)
     */    
    private boolean pendingClientNotify;
    private Component nextComp; // component to gain focus after endComposition()
    private final Set<Window> imWindows; // set of all IM windows created by this instance
    private final NativeIM nativeIM;
    

    public InputMethodContext() {
        notifyIM = new HashSet<TInputMethod>();
        imWindows = new HashSet<Window>();
        imInstances = new HashMap<TInputMethodDescriptor, TInputMethod>();
        localeIM = new HashMap<Locale, TInputMethod>();
        selectInputMethod(Locale.US); // not default?
        nativeIM = (NativeIM) inputMethod;
    }

    @Override
    public void dispatchEvent(AWTEvent event) {
        int id = event.getID(); 
        if ((id >= FocusEvent.FOCUS_FIRST) && (id <=FocusEvent.FOCUS_LAST)) {
            dispatchFocusEvent((FocusEvent) event);
        } else {
            // handle special KEY_PRESSED
            // event to show IM selection menu
            if (id == KeyEvent.KEY_PRESSED) {
                KeyEvent ke = (KeyEvent) event;
                IMManager.selectIM(ke, this, 
                                   IMManager.getWindow(ke.getComponent()));
            }
            // dispatch all input events to the current IM:
            if (inputMethod != null) {
                inputMethod.dispatchEvent(event);
            }
        }
    }
    
    private void dispatchFocusEvent(FocusEvent fe) {
        switch (fe.getID()) {
        case FocusEvent.FOCUS_LOST:            
            if (inputMethod != null) {
                inputMethod.deactivate(fe.isTemporary());                
            }
            break;
        case FocusEvent.FOCUS_GAINED:
            
            Component comp = fe.getComponent();
            if (imWindows.contains(comp)) {
                // prevent activating when IM windows
                // attached to this context gain focus                
                return;
            }
            InputMethodContext lastActive = IMManager.getLastActiveIMC();
            if ((lastActive != this) && (lastActive != null)) {
                lastActive.hideWindows();
            }
            if (inputMethod != null) {
                activateIM(inputMethod);
                if (!getCompositionWindow().isEmpty()) {
                    IMManager.showCompositionWindow(composeWindow);
                }
                if (client == comp) {
                    if (nextComp != null) {
                        // temporarily got focus to
                        // end composition
                        endComposition();

                        // transfer focus to new client
                        client = nextComp;
                        nextComp = null;
                        client.requestFocusInWindow();
                    }
                } else if ((client != null) && getCompositionWindow().isVisible()) {
                    // temporarily return focus back
                    // to previous client to be able
                    // to end composition
                    nextComp = comp;
                    client.requestFocusInWindow();
                } else {
                    client = comp;
                }
            }
            if (pendingClientNotify) {
                notifyClientWindowChange(IMManager.getWindow(comp).getBounds());
            }
            break;
        }

    }

    private void activateIM(TInputMethod im) {
        Component c;
        
        im.activate();
        if ((nativeIM != null) && (im != nativeIM)) {
            // when Java IM is active
            // native input method editor must be
            // explicitly disabled
            nativeIM.disableIME();
        }
        IMManager.setLastActiveIMC(this);
        
        c = getClient();
        if (c != null) {
           c.getInputMethodRequests();
        }
    }

    @SuppressWarnings("deprecation")
    private void hideWindows() {
        if (inputMethod != null) {
            inputMethod.hideWindows();
        }
        if (composeWindow != null) {
            composeWindow.hide();
        }
    }

    private void createCompositionWindow() {
        composeWindow = new CompositionWindow(client);        
    }
    
    private CompositionWindow getCompositionWindow() {
        if (composeWindow == null) {
            createCompositionWindow();
        }
        composeWindow.setClient(client);
        return composeWindow;        
    }
    
    /**
     * Gets input method requests for the current client
     * irrespective of input style.
     * @return input method requests of composition window if
     * client is passive,
     * otherwise input method requests of client
     */
    private TInputMethodRequests getIMRequests() {
        TInputMethodRequests imRequests = null;
    
        if (client != null) {
            imRequests = client.getInputMethodRequests();
            if (imRequests == null) {                
                imRequests = getCompositionWindow().getInputMethodRequests();
            }
        }
        
        return imRequests;
    }
    
    /**
     * Gets input method requests for the current client & input style.
     * @return input method requests of composition window if
     * input style is "below-the-spot"(or client is passive),
     * otherwise client input method requests
     */
    private TInputMethodRequests getStyleIMRequests() {
        if (IMManager.belowTheSpot()) {
            return getCompositionWindow().getInputMethodRequests();
        }
        return getIMRequests();
    }
    
    @Override
    public void dispose() {
        if (inputMethod != null) {
            closeIM(inputMethod);
            inputMethod.dispose();
        }
        notifyIM.clear();
        super.dispose();
    }

    @Override
    public void endComposition() {
        if (inputMethod != null) {
            inputMethod.endComposition();
        }
        super.endComposition();
    }

    @Override
    public Object getInputMethodControlObject() {
        if (inputMethod != null) {
            return inputMethod.getControlObject();
        }
        return super.getInputMethodControlObject();
    }

    @Override
    public Locale getLocale() {
        if (inputMethod != null) {
            return inputMethod.getLocale();
        }
        return super.getLocale();
    }

    @Override
    public boolean isCompositionEnabled() {
        if (inputMethod != null) {
            return inputMethod.isCompositionEnabled();
        }
        return super.isCompositionEnabled();
    }

    @Override
    public void reconvert() {
        if (inputMethod != null) {
            inputMethod.reconvert();
        }
        super.reconvert();
    }

    @Override
    public void removeNotify(Component client) {
        if ((inputMethod != null) && (client == this.client)) {
            inputMethod.removeNotify();
            client = null;
            // set flag indicating that IM should be notified
            // as soon as it is activated(new client appears)
            pendingClientNotify = true;
        }
        
        super.removeNotify(client);
    }

    @Override
    public boolean selectInputMethod(Locale locale) {        
        
        if ((inputMethod != null) && inputMethod.setLocale(locale)) {
            return true;
        }
        // first
        // take last user-selected IM for locale            
        TInputMethod newIM = localeIM.get(locale);
        
        // if not found search through IM descriptors
        // and take already created instance if exists
        // or create, store new IM instance in descriptor->instance map
        if (newIM == null) {
            try {
                newIM = getIMInstance(IMManager.getIMDescriptors().iterator(),
                                      locale);
            } catch (Exception e) {
                // ignore exceptions - just return false
            }
        }
        
        return switchToIM(locale, newIM);
    }

    private boolean switchToIM(Locale locale, TInputMethod newIM) {
        if (newIM != null) {
            closeIM(inputMethod);
            client = KeyboardFocusManager.
                    getCurrentKeyboardFocusManager().getFocusOwner();
            if (client != null) {
                client.getInputMethodRequests();
            }
            initIM(newIM, locale);
            inputMethod = newIM;

            return true;
        }
        return false;
    }
    
    /**
     * Is called when IM is selected from UI
     */
    void selectIM(TInputMethodDescriptor imd, Locale locale) {
        try {
            switchToIM(locale, getIMInstance(imd));            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Gets input method instance for the given
     * locale from the given list of descriptors
     * @param descriptors iterator of the list of IM descriptors
     * @param locale the locale to be supported by the IM
     * @return input method instance
     * @throws Exception
     */
    private TInputMethod getIMInstance(Iterator<TInputMethodDescriptor> descriptors,
                                      Locale locale) throws Exception {
        while (descriptors.hasNext()) {
            TInputMethodDescriptor desc = descriptors.next();
            Locale[] locs = desc.getAvailableLocales();
            for (Locale element : locs) {
                if (locale.equals(element)) {
                    return getIMInstance(desc);
                }
            }
        }
        return null;
    }

    private TInputMethod getIMInstance(TInputMethodDescriptor imd) throws Exception {
        TInputMethod im = imInstances.get(imd);
        if (im == null) {
            im = imd.createInputMethod();
            im.setInputMethodContext(this);
            imInstances.put(imd, im);
        }
        return im;
    }
    
    private void initIM(TInputMethod im, Locale locale) {
        if (im == null) {
            return;
        }
        im.setLocale(locale);
        im.setCharacterSubsets(null);
        activateIM(im);
        try {
            im.setCompositionEnabled(inputMethod != null ? 
                                     inputMethod.isCompositionEnabled() : true);
        } catch (UnsupportedOperationException uoe) {

        }
        
    }

    private void closeIM(TInputMethod im) {
        if (im == null) {
            return;
        }
        if (im.isCompositionEnabled()) {
            im.endComposition();
        }
        
        im.deactivate(true);
        im.hideWindows();
        
    }
    
    @Override
    public void setCharacterSubsets(Subset[] subsets) {
        if (inputMethod != null) {
            inputMethod.setCharacterSubsets(subsets);
        }
        super.setCharacterSubsets(subsets);
    }

    @Override
    public void setCompositionEnabled(boolean enable) {
        if (inputMethod != null) {
            inputMethod.setCompositionEnabled(enable);
        }
        super.setCompositionEnabled(enable);
    }

    public JFrame createInputMethodJFrame(String title,
                                          boolean attachToInputContext) {
        JFrame jf = new IMJFrame(title, attachToInputContext ? this : null);
        imWindows.add(jf);
        return jf;
    }

    public Window createInputMethodWindow(String title,
                                          boolean attachToInputContext) {
        Window w = new IMWindow(title, attachToInputContext ? this : null);
        imWindows.add(w);
        return w;
    }

    @SuppressWarnings("deprecation")
    public void dispatchInputMethodEvent(int id,
                                         AttributedCharacterIterator text,
                                         int committedCharacterCount,
                                         TextHitInfo caret,
                                         TextHitInfo visiblePosition) {
        if (client == null) {
            return;
        }
        InputMethodEvent ime = new InputMethodEvent(client, id, text,
                                                    committedCharacterCount,
                                                    caret, visiblePosition);
        
        if ((client.getInputMethodRequests() != null) &&
            !IMManager.belowTheSpot()) {
            
            client.dispatchEvent(ime);
        } else {
            
            // show/hide composition window if necessary
            if (committedCharacterCount < text.getEndIndex()) {
                IMManager.showCompositionWindow(getCompositionWindow());
            } else {
                getCompositionWindow().hide();
            }
            composeWindow.getActiveClient().dispatchEvent(ime);
        }

    }

    public void enableClientWindowNotification(TInputMethod inputMethod,
                                               boolean enable) {
        if (enable) {
            notifyIM.add(inputMethod);
            if (client != null) {
                notifyClientWindowChange(IMManager.getWindow(client).getBounds());
            } else {
                pendingClientNotify = true;
            }
        } else {
            notifyIM.remove(inputMethod);
        }
        
        

    }

    public AttributedCharacterIterator cancelLatestCommittedText(
                                                                 Attribute[] attributes) {
        return getIMRequests().cancelLatestCommittedText(attributes);
    }

    public AttributedCharacterIterator getCommittedText(int beginIndex,
                                                        int endIndex,
                                                        Attribute[] attributes) {
        return getIMRequests().getCommittedText(beginIndex, endIndex,
                                                attributes);
    }

    public int getCommittedTextLength() {
        return getIMRequests().getCommittedTextLength();
    }

    public int getInsertPositionOffset() {
        return getIMRequests().getInsertPositionOffset();
    }

    public TextHitInfo getLocationOffset(int x, int y) {
        TInputMethodRequests imr = getStyleIMRequests();
        if (imr != null) {
            return imr.getLocationOffset(x, y);
        }
        return null;
    }

    public AttributedCharacterIterator getSelectedText(Attribute[] attributes) {
        return getIMRequests().getSelectedText(attributes);
    }

    public Rectangle getTextLocation(TextHitInfo offset) {        
        return getStyleIMRequests().getTextLocation(offset);
    }
    
    /**
     * To be called by AWT when client Window's bounds/visibility/state
     * change
     */
    public void notifyClientWindowChange(Rectangle bounds) {
        if (notifyIM.contains(inputMethod)) {
            inputMethod.notifyClientWindowChange(bounds);
        }
        pendingClientNotify = false;
    }

    public final TInputMethod getInputMethod() {
        return inputMethod;
    }

    public final Component getClient() {
        return client;
    }

    public final NativeIM getNativeIM() {
        return nativeIM;
    }
}
