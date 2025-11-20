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
 * @author Pavel Dolgov
 */
package org.apache.harmony.awt.datatransfer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Reader;
import java.io.Serializable;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.teavm.classlib.java.awt.TGraphics;
import org.teavm.classlib.java.awt.TImage;
import org.teavm.classlib.java.awt.datatransfer.TDataFlavor;
import org.teavm.classlib.java.awt.datatransfer.TSystemFlavorMap;
import org.teavm.classlib.java.awt.datatransfer.TTransferable;
import org.teavm.classlib.java.awt.image.TBufferedImage;
import org.teavm.classlib.java.awt.image.TDataBufferInt;

/**
 * Convertor from {@link java.awt.datatransfer.Transferable} to 
 * {@link org.apache.harmony.awt.datatransfer.DataProvider}
 */
public class DataSource implements DataProvider {

    // Cached data from transferable object
    private TDataFlavor[] flavors;
    private List<String> nativeFormats;
    
    protected final TTransferable contents;
    
    public DataSource(TTransferable contents) {
        this.contents = contents;
    }

    private boolean isHtmlFlavor(TDataFlavor f) {
        return "html".equalsIgnoreCase(f.getSubType()); //$NON-NLS-1$
    }
    
    protected TDataFlavor[] getDataFlavors() {
        if (flavors == null) {
            flavors = contents.getTransferDataFlavors();
        }
        return flavors;
    }
    
    public String[] getNativeFormats() {
        return getNativeFormatsList().toArray(new String[0]);
    }
    
    public List<String> getNativeFormatsList() {
        if (nativeFormats == null) {
            TDataFlavor[] flavors = getDataFlavors();
            nativeFormats = getNativesForFlavors(flavors);
        }

        return nativeFormats;
    }
    
    private static List<String> getNativesForFlavors(TDataFlavor[] flavors) {
        ArrayList<String> natives = new ArrayList<String>();
        
        TSystemFlavorMap flavorMap = (TSystemFlavorMap) TSystemFlavorMap.getDefaultFlavorMap();
        
        for (int i = 0; i < flavors.length; i++) {
            List<String> list = flavorMap.getNativesForFlavor(flavors[i]);
            for (Iterator<String> it = list.iterator(); it.hasNext(); ) {
                String nativeFormat = it.next();
                if (!natives.contains(nativeFormat)) {
                    natives.add(nativeFormat);
                }
            }
        }
        return natives;
    }
    
    private String getTextFromReader(Reader r) throws IOException {
        StringBuilder buffer = new StringBuilder();
        char chunk[] = new char[1024];
        int len;
        while ((len = r.read(chunk)) > 0) {
            buffer.append(chunk, 0, len);
        }
        return buffer.toString();
    }
    
    private String getText(boolean htmlOnly) {
        TDataFlavor[] flavors = contents.getTransferDataFlavors();
        for (int i = 0; i < flavors.length; i++) {
            TDataFlavor f = flavors[i];
            if (!f.isFlavorTextType()) {
                continue;
            }
            if (htmlOnly && !isHtmlFlavor(f)) {
                continue;
            }
            try {
                if (String.class.isAssignableFrom(
                        f.getRepresentationClass())) {
                    return (String)contents.getTransferData(f);
                }
                Reader r = f.getReaderForText(contents);
                return getTextFromReader(r);
            } catch (Exception e) {}
        }
        return null;
    }

    public String getText() {
        return getText(false);
    }

    public String[] getFileList() {
        try {
            List<?> list = (List<?>) contents.getTransferData(TDataFlavor.javaFileListFlavor);
            return list.toArray(new String[list.size()]);
        } catch (Exception e) {
            return null;
        }
    }

    public String getURL() {
        try {
            URL url = (URL)contents.getTransferData(urlFlavor);
            return url.toString();
        } catch (Exception e) {}
        try {
            URL url = (URL)contents.getTransferData(uriFlavor);
            return url.toString();
        } catch (Exception e) {}
        try {
            URL url = new URL(getText());
            return url.toString();
        } catch (Exception e) {}
        return null;
    }

    public String getHTML() {
        return getText(true);
    }
    
    public RawBitmap getRawBitmap() {
        TDataFlavor[] flavors = contents.getTransferDataFlavors();

        for (int i = 0; i < flavors.length; i++) {
            TDataFlavor f = flavors[i];
            Class<?> c = f.getRepresentationClass();
            if (c != null && TImage.class.isAssignableFrom(c) && 
                    (f.isMimeTypeEqual(TDataFlavor.imageFlavor) || 
                            f.isFlavorSerializedObjectType())) {
                try {
                    TImage im = (TImage) contents.getTransferData(f);
                    return getImageBitmap(im);
                } catch (Throwable ex) {
                    continue;
                }
            }
        }
        return null;
    }
    
    private RawBitmap getImageBitmap(TImage im) {
        if (im instanceof TBufferedImage) {
            TBufferedImage bi = (TBufferedImage)im;
            if (bi.getType() == TBufferedImage.TYPE_INT_RGB) {
                return getImageBitmap32(bi);
            }
        }
        int width = im.getWidth(null);
        int height = im.getHeight(null);
        if (width <= 0 || height <= 0) {
            return null;
        }
        TBufferedImage bi = new TBufferedImage(width, height, TBufferedImage.TYPE_INT_RGB);
        TGraphics gr = bi.getGraphics();
        gr.drawImage(im, 0, 0, null);
        gr.dispose();
        return getImageBitmap32(bi);
    }

    private RawBitmap getImageBitmap32(TBufferedImage bi) {
        int buffer[] = new int[bi.getWidth() * bi.getHeight()];
        TDataBufferInt data = (TDataBufferInt)bi.getRaster().getDataBuffer();
        int bufferPos = 0;
        int bankCount = data.getNumBanks();
        int offsets[] = data.getOffsets();
        for (int i = 0; i < bankCount; i++) {
            int[] fragment = data.getData(i);
            System.arraycopy(fragment, offsets[i], buffer, bufferPos, 
                    fragment.length - offsets[i]);
            bufferPos += fragment.length - offsets[i];
        }
        return new RawBitmap(bi.getWidth(), bi.getHeight(), bi.getWidth(), 
                32, 0xFF0000, 0xFF00, 0xFF, buffer);
    }

    public byte[] getSerializedObject(Class<?> clazz) {
        try {
            TDataFlavor f = new TDataFlavor(clazz, null);
            Serializable s = (Serializable)contents.getTransferData(f);
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            new ObjectOutputStream(bytes).writeObject(s);
            return bytes.toByteArray();
        } catch (Throwable e) {
            return null;
        }
    }

    public boolean isNativeFormatAvailable(String nativeFormat) {
        return getNativeFormatsList().contains(nativeFormat);
    }
}
