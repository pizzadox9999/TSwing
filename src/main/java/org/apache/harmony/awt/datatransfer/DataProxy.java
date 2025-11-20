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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.Reader;
import java.io.StringReader;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.apache.harmony.awt.internal.nls.Messages;
import org.teavm.classlib.java.awt.TImage;
import org.teavm.classlib.java.awt.TTransparency;
import org.teavm.classlib.java.awt.color.TColorSpace;
import org.teavm.classlib.java.awt.datatransfer.TDataFlavor;
import org.teavm.classlib.java.awt.datatransfer.TSystemFlavorMap;
import org.teavm.classlib.java.awt.datatransfer.TTransferable;
import org.teavm.classlib.java.awt.datatransfer.TUnsupportedFlavorException;
import org.teavm.classlib.java.awt.image.TBufferedImage;
import org.teavm.classlib.java.awt.image.TColorModel;
import org.teavm.classlib.java.awt.image.TComponentColorModel;
import org.teavm.classlib.java.awt.image.TDataBuffer;
import org.teavm.classlib.java.awt.image.TDataBufferByte;
import org.teavm.classlib.java.awt.image.TDataBufferInt;
import org.teavm.classlib.java.awt.image.TDataBufferUShort;
import org.teavm.classlib.java.awt.image.TDirectColorModel;
import org.teavm.classlib.java.awt.image.TRaster;
import org.teavm.classlib.java.awt.image.TWritableRaster;

/**
 * Wrapper for native data
 */
public final class DataProxy implements TTransferable {
    
    public static final Class<?>[] unicodeTextClasses = 
            { String.class, Reader.class, CharBuffer.class, char[].class }; 
    public static final Class<?>[] charsetTextClasses = 
              { byte[].class, ByteBuffer.class, InputStream.class };
    
    private final DataProvider data;
    private final TSystemFlavorMap flavorMap;
    
    public DataProxy(DataProvider data) {
        this.data = data;
        this.flavorMap = (TSystemFlavorMap) TSystemFlavorMap.getDefaultFlavorMap();
    }
    
    public DataProvider getDataProvider() {
        return data;
    }
    
    public Object getTransferData(TDataFlavor flavor)
            throws TUnsupportedFlavorException, IOException {
        
        String mimeType = flavor.getPrimaryType() + "/" + flavor.getSubType(); //$NON-NLS-1$
        if (flavor.isFlavorTextType()) {
            if (mimeType.equalsIgnoreCase(DataProvider.TYPE_HTML)) {
                return getHTML(flavor);
            }
            if (mimeType.equalsIgnoreCase(DataProvider.TYPE_URILIST)) {
                return getURL(flavor);
            }
            return getPlainText(flavor);
        }
        if (flavor.isFlavorJavaFileListType()) {
            return getFileList(flavor);
        }
        if (flavor.isFlavorSerializedObjectType()) {
            return getSerializedObject(flavor);
        }
        if (flavor.equals(DataProvider.urlFlavor)) {
            return getURL(flavor);
        }
        if (mimeType.equalsIgnoreCase(DataProvider.TYPE_IMAGE) && 
                TImage.class.isAssignableFrom(flavor.getRepresentationClass())) {
            return getImage(flavor);
        }
        
        throw new TUnsupportedFlavorException(flavor);
    }

    public TDataFlavor[] getTransferDataFlavors() {
        ArrayList<TDataFlavor> result = new ArrayList<TDataFlavor>();
        String[] natives = data.getNativeFormats();
        
        for (int i = 0; i < natives.length; i++) {
            List<TDataFlavor> flavors = flavorMap.getFlavorsForNative(natives[i]);
            for (Iterator<TDataFlavor> it = flavors.iterator(); it.hasNext(); ) {
                TDataFlavor f = it.next();
                if (!result.contains(f)) {
                    result.add(f);
                }
            }
        }
        return result.toArray(new TDataFlavor[result.size()]);
    }
    
    public boolean isDataFlavorSupported(TDataFlavor flavor) {
        TDataFlavor[] flavors = getTransferDataFlavors();
        for (int i=0; i<flavors.length; i++) {
            if (flavors[i].equals(flavor)) {
                return true;
            }
        }
        return false;
    }
    
    private Object getPlainText(TDataFlavor f)
            throws IOException, TUnsupportedFlavorException {
        if (!data.isNativeFormatAvailable(DataProvider.FORMAT_TEXT)) {
            throw new TUnsupportedFlavorException(f);
        }
        String str = data.getText();
        if (str == null) {
            // awt.4F=Data is not available
            throw new IOException(Messages.getString("awt.4F")); //$NON-NLS-1$
        }
        return getTextRepresentation(str, f);
    }

    private Object getFileList(TDataFlavor f) 
            throws IOException, TUnsupportedFlavorException {
        if (!data.isNativeFormatAvailable(DataProvider.FORMAT_FILE_LIST)) {
            throw new TUnsupportedFlavorException(f);
        }
        String[] files = data.getFileList();
        if (files == null) {
            // awt.4F=Data is not available
            throw new IOException(Messages.getString("awt.4F")); //$NON-NLS-1$
        }
        return Arrays.asList(files);
    }

    private Object getHTML(TDataFlavor f)
            throws IOException, TUnsupportedFlavorException {
        if (!data.isNativeFormatAvailable(DataProvider.FORMAT_HTML)) {
            throw new TUnsupportedFlavorException(f);
        }
        String str = data.getHTML();
        if (str == null) {
            // awt.4F=Data is not available
            throw new IOException(Messages.getString("awt.4F")); //$NON-NLS-1$
        }
        return getTextRepresentation(str, f);
    }

    private Object getURL(TDataFlavor f)
            throws IOException, TUnsupportedFlavorException {
        if (!data.isNativeFormatAvailable(DataProvider.FORMAT_URL)) {
            throw new TUnsupportedFlavorException(f);
        }
        String str = data.getURL();
        if (str == null) {
            // awt.4F=Data is not available
            throw new IOException(Messages.getString("awt.4F")); //$NON-NLS-1$
        }
        URL url = new URL(str);
        if (f.getRepresentationClass().isAssignableFrom(URL.class)) {
            return url;
        }
        if (f.isFlavorTextType()) {
            return getTextRepresentation(url.toString(), f);
        }
        throw new TUnsupportedFlavorException(f);
    }
    
    private Object getSerializedObject(TDataFlavor f)
            throws IOException, TUnsupportedFlavorException {
        String nativeFormat = TSystemFlavorMap.encodeDataFlavor(f);
        if ((nativeFormat == null) || 
                !data.isNativeFormatAvailable(nativeFormat)) {
            throw new TUnsupportedFlavorException(f);
        }
        byte bytes[] = data.getSerializedObject(f.getRepresentationClass());
        if (bytes == null) {
            // awt.4F=Data is not available
            throw new IOException(Messages.getString("awt.4F")); //$NON-NLS-1$
        }
        ByteArrayInputStream str = new ByteArrayInputStream(bytes);
        try {
            return new ObjectInputStream(str).readObject();
        } catch (ClassNotFoundException ex) {
            throw new IOException(ex.getMessage());
        }
    }
    
    private String getCharset(TDataFlavor f) {
        return f.getParameter("charset"); //$NON-NLS-1$
    }

    private Object getTextRepresentation(String text, TDataFlavor f)
            throws TUnsupportedFlavorException, IOException {
        if (f.getRepresentationClass() == String.class) {
            return text;
        }
        if (f.isRepresentationClassReader()) {
            return new StringReader(text);
        }
        if (f.isRepresentationClassCharBuffer()) {
            return CharBuffer.wrap(text);
        }
        if (f.getRepresentationClass() == char[].class) {
            char[] chars = new char[text.length()];
            text.getChars(0, text.length(), chars, 0);
            return chars;
        }
        String charset = getCharset(f);
        if (f.getRepresentationClass() == byte[].class) {
            byte[] bytes = text.getBytes(charset);
            return bytes;
        }
        if (f.isRepresentationClassByteBuffer()) {
            byte[] bytes = text.getBytes(charset);
            return ByteBuffer.wrap(bytes);
        }
        if (f.isRepresentationClassInputStream()) {
            byte[] bytes = text.getBytes(charset);
            return new ByteArrayInputStream(bytes);
        }
        throw new TUnsupportedFlavorException(f);
    }

    private TImage getImage(TDataFlavor f) 
            throws IOException, TUnsupportedFlavorException {
        if (!data.isNativeFormatAvailable(DataProvider.FORMAT_IMAGE)) {
            throw new TUnsupportedFlavorException(f);
        }
        RawBitmap bitmap = data.getRawBitmap();
        if (bitmap == null) {
            // awt.4F=Data is not available
            throw new IOException(Messages.getString("awt.4F")); //$NON-NLS-1$
        }
        return createBufferedImage(bitmap);
    }
    
    private boolean isRGB(RawBitmap b) {
        return b.rMask == 0xFF0000 && b.gMask == 0xFF00 && b.bMask == 0xFF;
    }
    
    private boolean isBGR(RawBitmap b) {
        return b.rMask == 0xFF && b.gMask == 0xFF00 && b.bMask == 0xFF0000;
    }
    
    private TBufferedImage createBufferedImage(RawBitmap b) {
        if (b == null || b.buffer == null
                || b.width <= 0 || b.height <= 0) {
            return null;
        }
        
        TColorModel cm = null;
        TWritableRaster wr = null;

        if (b.bits == 32 && b.buffer instanceof int[]) {
            if (!isRGB(b) && !isBGR(b)) {
                return null;
            }
            int masks[] = { b.rMask, b.gMask, b.bMask };
            int buffer[] = (int [])b.buffer;
            cm = new TDirectColorModel(24, b.rMask, b.gMask, b.bMask);
            wr = TRaster.createPackedRaster(
                    new TDataBufferInt(buffer, buffer.length), 
                    b.width, b.height, b.stride,
                    masks, null);

        } else  if (b.bits == 24 && b.buffer instanceof byte[]) {
            int bits[] = { 8, 8, 8 };
            int offsets[];
            if (isRGB(b)) {
                offsets = new int[] { 0, 1, 2 };
            } else if (isBGR(b)) {
                offsets = new int[] { 2, 1, 0 };
            } else {
                return null;
            }
            byte buffer[] = (byte [])b.buffer;
            cm = new TComponentColorModel(
                    TColorSpace.getInstance(TColorSpace.CS_sRGB),
                    bits, false, false, 
                    TTransparency.OPAQUE, 
                    TDataBuffer.TYPE_BYTE);

            wr = TRaster.createInterleavedRaster(
                    new TDataBufferByte(buffer, buffer.length),
                    b.width, b.height, b.stride, 3, offsets, null);

        } else if ((b.bits == 16 || b.bits == 15)
                && b.buffer instanceof short[]) {
            int masks[] = { b.rMask, b.gMask, b.bMask };
            short buffer[] = (short [])b.buffer;
            cm = new TDirectColorModel(b.bits, b.rMask, b.gMask, b.bMask);
            wr = TRaster.createPackedRaster(
                    new TDataBufferUShort(buffer, buffer.length), 
                    b.width, b.height, b.stride,
                    masks, null);
        }
        
        if (cm == null || wr == null) {
            return null;
        }
        return new TBufferedImage(cm, wr, false, null);
    }
}

