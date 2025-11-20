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

import java.io.Serializable;
import java.net.URL;
import java.util.Hashtable;
import java.util.Map;
import java.util.Properties;
import org.apache.harmony.awt.gl.font.FontMetricsImpl;
import org.apache.harmony.awt.datatransfer.DTK;
import org.apache.harmony.awt.gl.*;
import org.apache.harmony.awt.gl.image.*;
import org.teavm.classlib.java.awt.image.TImageObserver;
import org.teavm.classlib.java.awt.peer.TFontPeer;
import org.teavm.classlib.java.awt.peer.TScrollPanePeer;
import org.teavm.classlib.java.awt.peer.TScrollbarPeer;
import org.teavm.classlib.java.awt.peer.TTextAreaPeer;
import org.teavm.classlib.java.awt.peer.TTextFieldPeer;

class ToolkitImpl extends TToolkit {
    static final Hashtable<Serializable, TImage> imageCache = new Hashtable<Serializable, TImage>();

    static final TFontMetrics cacheFM[] =  new TFontMetrics[10];

    @Override
    public void sync() {
        lockAWT();
        try {
        } finally {
            unlockAWT();
        }
    }

    @Override
    protected TTextAreaPeer createTextArea(TTextArea a0) {
        lockAWT();
        try {
            return null;
        } finally {
            unlockAWT();
        }
    }

    @Override
    public int checkImage(Image image, int width, int height, ImageObserver observer) {
        lockAWT();
        try {
            if (width == 0 || height == 0) {
                return TImageObserver.ALLBITS;
            }
            if (!(image instanceof OffscreenImage)) {
                return TImageObserver.ALLBITS;
            }
            OffscreenImage oi = (OffscreenImage) image;
            return oi.checkImage(observer);
        } finally {
            unlockAWT();
        }
    }

    @Override
    public Image createImage(ImageProducer producer) {
        lockAWT();
        try {
            return new OffscreenImage(producer);
        } finally {
            unlockAWT();
        }
    }

    @Override
    public Image createImage(byte[] imagedata, int imageoffset, int imagelength) {
        lockAWT();
        try {
            return new OffscreenImage(new ByteArrayDecodingImageSource(imagedata, imageoffset,
                    imagelength));
        } finally {
            unlockAWT();
        }
    }

    @Override
    public TImage createImage(URL url) {
        lockAWT();
        try {
            return new OffscreenImage(new URLDecodingImageSource(url));
        } finally {
            unlockAWT();
        }
    }

    @Override
    public TImage createImage(String filename) {
        lockAWT();
        try {
            return new OffscreenImage(new FileDecodingImageSource(filename));
        } finally {
            unlockAWT();
        }
    }

    @Override
    public TColorModel getColorModel() {
        lockAWT();
        try {
            return TGraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice()
                    .getDefaultConfiguration().getColorModel();
        } finally {
            unlockAWT();
        }
    }

    /**
     * Returns FontMetrics object that keeps metrics of the specified font.
     * 
     * @param font specified Font
     * @return FontMetrics object corresponding to the specified Font object
     */
    @Override
    @Deprecated
    public FontMetrics getFontMetrics(Font font) {
        lockAWT();
        try {
            TFontMetrics fm;
            for (TFontMetrics element : cacheFM) {
                fm = element;
                if (fm == null){
                    break;
                }

                if (fm.getFont().equals(font)){
                    return fm;
                }
            }
            fm = new FontMetricsImpl(font);

            System.arraycopy(cacheFM, 0, cacheFM, 1, cacheFM.length -1);
            cacheFM[0] = fm;

            return fm;

//            return getGraphicsFactory().getFontMetrics(font);
        } finally {
            unlockAWT();
        }
    }

    @Override
    public boolean prepareImage(TImage image, int width, int height, TImageObserver observer) {
        lockAWT();
        try {
            if (width == 0 || height == 0) {
                return true;
            }
            if (!(image instanceof OffscreenImage)) {
                return true;
            }
            OffscreenImage oi = (OffscreenImage) image;
            return oi.prepareImage(observer);
        } finally {
            unlockAWT();
        }
    }

    @Override
    public void beep() {
        lockAWT();
        try {
        } finally {
            unlockAWT();
        }
    }

    @Override
    protected ButtonPeer createButton(Button a0) {
        lockAWT();
        try {
            return null;
        } finally {
            unlockAWT();
        }
    }

    @Override
    protected CanvasPeer createCanvas(Canvas a0) {
        lockAWT();
        try {
            return null;
        } finally {
            unlockAWT();
        }
    }

    @Override
    protected CheckboxPeer createCheckbox(Checkbox a0) {
        lockAWT();
        try {
            return null;
        } finally {
            unlockAWT();
        }
    }

    @Override
    protected CheckboxMenuItemPeer createCheckboxMenuItem(CheckboxMenuItem a0)
            throws HeadlessException {
        lockAWT();
        try {
            return null;
        } finally {
            unlockAWT();
        }
    }

    @Override
    protected ChoicePeer createChoice(Choice a0) {
        lockAWT();
        try {
            return null;
        } finally {
            unlockAWT();
        }
    }

    @Override
    protected DialogPeer createDialog(Dialog a0) {
        lockAWT();
        try {
            return null;
        } finally {
            unlockAWT();
        }
    }

    @Override
    public DragSourceContextPeer createDragSourceContextPeer(DragGestureEvent dge)
            throws InvalidDnDOperationException {
        return dtk.createDragSourceContextPeer(dge);
    }

    @Override
    protected FileDialogPeer createFileDialog(FileDialog a0) {
        lockAWT();
        try {
            return null;
        } finally {
            unlockAWT();
        }
    }

    @Override
    protected FramePeer createFrame(Frame a0) {
        lockAWT();
        try {
            return null;
        } finally {
            unlockAWT();
        }
    }

    @Override
    protected LabelPeer createLabel(Label a0) {
        lockAWT();
        try {
            return null;
        } finally {
            unlockAWT();
        }
    }

    @Override
    protected ListPeer createList(List a0) {
        lockAWT();
        try {
            return null;
        } finally {
            unlockAWT();
        }
    }

    @Override
    protected MenuPeer createMenu(Menu a0) {
        lockAWT();
        try {
            return null;
        } finally {
            unlockAWT();
        }
    }

    @Override
    protected MenuBarPeer createMenuBar(MenuBar a0) {
        lockAWT();
        try {
            return null;
        } finally {
            unlockAWT();
        }
    }

    @Override
    protected MenuItemPeer createMenuItem(MenuItem a0) {
        lockAWT();
        try {
            return null;
        } finally {
            unlockAWT();
        }
    }

    @Override
    protected PanelPeer createPanel(Panel a0) {
        lockAWT();
        try {
            return null;
        } finally {
            unlockAWT();
        }
    }

    @Override
    protected PopupMenuPeer createPopupMenu(PopupMenu a0) {
        lockAWT();
        try {
            return null;
        } finally {
            unlockAWT();
        }
    }

    @Override
    protected TScrollPanePeer createScrollPane(TScrollPane a0) {
        lockAWT();
        try {
            return null;
        } finally {
            unlockAWT();
        }
    }

    @Override
    protected TScrollbarPeer createScrollbar(TScrollbar a0) {
        lockAWT();
        try {
            return null;
        } finally {
            unlockAWT();
        }
    }

    @Override
    protected TTextFieldPeer createTextField(TTextFieldPeer a0) {
        lockAWT();
        try {
            return null;
        } finally {
            unlockAWT();
        }
    }

    @Override
    protected WindowPeer createWindow(Window a0) {
        lockAWT();
        try {
            return null;
        } finally {
            unlockAWT();
        }
    }

    @Override
    @Deprecated
    public String[] getFontList() {
        lockAWT();
        try {
        } finally {
            unlockAWT();
        }
        return null;
    }

    @Override
    @Deprecated
    protected TFontPeer getFontPeer(String a0, int a1) {
        lockAWT();
        try {
            return null;
        } finally {
            unlockAWT();
        }
    }

    @Override
    public Image getImage(String filename) {
        return getImage(filename, this);
    }

    static Image getImage(String filename, Toolkit toolkit) {
        synchronized (imageCache) {
            TImage im = (filename == null ? null : imageCache.get(filename));

            if (im == null) {
                try {
                    im = toolkit.createImage(filename);
                    imageCache.put(filename, im);
                } catch (Exception e) {
                }
            }

            return im;
        }
    }

    @Override
    public Image getImage(URL url) {
        return getImage(url, this);
    }

    static Image getImage(URL url, Toolkit toolkit) {
        synchronized (imageCache) {
            TImage im = imageCache.get(url);
            if (im == null) {
                try {
                    im = toolkit.createImage(url);
                    imageCache.put(url, im);
                } catch (Exception e) {
                }
            }
            return im;
        }
    }

    @Override
    public PrintJob getPrintJob(Frame a0, String a1, Properties a2) {
        lockAWT();
        try {
        } finally {
            unlockAWT();
        }
        return null;
    }

    @Override
    public int getScreenResolution() throws HeadlessException {
        lockAWT();
        try {
            return ((GLGraphicsDevice) TGraphicsEnvironment.getLocalGraphicsEnvironment()
                    .getDefaultScreenDevice()).getResolution().width;
        } finally {
            unlockAWT();
        }
    }

    @Override
    public Dimension getScreenSize() {
        lockAWT();
        try {
            TDisplayMode dm = TGraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDisplayMode();
            return new TDimension(dm.getWidth(), dm.getHeight());
        } finally {
            unlockAWT();
        }
    }

    @Override
    public TClipboard getSystemClipboard() {
        lockAWT();
        try {
            SecurityManager security = System.getSecurityManager();
            if (security != null) {
                security.checkSystemClipboardAccess();
            }
            if (systemClipboard == null) {
                systemClipboard = DTK.getDTK().getNativeClipboard();
            }
            if (systemClipboard.getContents(null) == null) {
                systemClipboard.setContents ( new StringSelection("") , null);
            }
            return systemClipboard;
        } finally {
            unlockAWT();
        }
    }

    @Override
    public Map<TTextAttribute, ?> mapInputMethodHighlight(TInputMethodHighlight highlight) throws HeadlessException {
        lockAWT();
        try {
            return mapInputMethodHighlightImpl(highlight);
        } finally {
            unlockAWT();
        }
    }

    @Override
    protected EventQueue getSystemEventQueueImpl() {
        return getSystemEventQueueCore().getActiveEventQueue();
    }
}
