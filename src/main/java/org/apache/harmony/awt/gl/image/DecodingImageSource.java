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
 * @author Oleg V. Khaschansky
 */
/*
 * Created on 18.01.2005
 */
package org.apache.harmony.awt.gl.image;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.teavm.classlib.java.awt.image.TImageConsumer;
import org.teavm.classlib.java.awt.image.TImageProducer;

/**
 * This is an abstract class that encapsulates a main part of ImageProducer functionality
 * for the images being decoded by the native decoders, like PNG, JPEG and GIF.
 * It helps to integrate image decoders into producer/consumer model. It provides
 * functionality for working with several decoder instances and several image consumers
 * simultaneously.
 */
public abstract class DecodingImageSource implements TImageProducer {
    List<TImageConsumer> consumers = new ArrayList<TImageConsumer>(5);
    List<ImageDecoder> decoders = new ArrayList<ImageDecoder>(5);
    boolean loading;

    ImageDecoder decoder;

    protected abstract boolean checkConnection();

    protected abstract InputStream getInputStream();

    public synchronized void addConsumer(TImageConsumer ic) {
        if (!checkConnection()) { // No permission for this consumer
            ic.imageComplete(TImageConsumer.IMAGEERROR);
            return;
        }

        TImageConsumer cons = findConsumer(consumers, ic);

        if (cons == null) { // Try to look in the decoders
            ImageDecoder d = null;

            // Check for all existing decoders
            for (Iterator<ImageDecoder> i = decoders.iterator(); i.hasNext();) {
                d = i.next();
                cons = findConsumer(d.consumers, ic);
                if (cons != null) {
                    break;
                }
            }
        }

        if (cons == null) { // Not found, add this consumer
            consumers.add(ic);
        }
    }

    /**
     * This method stops sending data to the given consumer
     * @param ic - consumer
     */
    private void abortConsumer(TImageConsumer ic) {
        ic.imageComplete(TImageConsumer.IMAGEERROR);
        consumers.remove(ic);
    }

    /**
     * This method stops sending data to the list of consumers.
     * @param consumersList - list of consumers
     */
    private void abortAllConsumers(List<TImageConsumer> consumersList) {
        for (TImageConsumer imageConsumer : consumersList) {
            abortConsumer(imageConsumer);
        }
    }

    public synchronized void removeConsumer(TImageConsumer ic) {
        ImageDecoder d = null;

        // Remove in all existing decoders
        for (Iterator<ImageDecoder> i = decoders.iterator(); i.hasNext();) {
            d = i.next();
            removeConsumer(d.consumers, ic);
            if (d.consumers.size() <= 0) {
                d.terminate();
            }
        }

        // Remove in the current queue of consumers
        removeConsumer(consumers, ic);
    }

    /**
     * Static implementation of removeConsumer method
     * @param consumersList - list of consumers
     * @param ic - consumer to be removed
     */
    private static void removeConsumer(List<TImageConsumer> consumersList, TImageConsumer ic) {
        TImageConsumer cons = null;

        for (Iterator<TImageConsumer> i = consumersList.iterator(); i.hasNext();) {
            cons = i.next();
            if (cons.equals(ic)) {
                i.remove();
            }
        }
    }

    public void requestTopDownLeftRightResend(TImageConsumer consumer) {
        // Do nothing
    }

    public synchronized void startProduction(TImageConsumer ic) {
        if (ic != null) {
            addConsumer(ic);
        }

        if (!loading && consumers.size() > 0) {
            ImageLoader.addImageSource(this);
            loading = true;
        }
    }

    public synchronized boolean isConsumer(TImageConsumer ic) {
        ImageDecoder d = null;

        // Check for all existing decoders
        for (Iterator<ImageDecoder> i = decoders.iterator(); i.hasNext();) {
            d = i.next();
            if (findConsumer(d.consumers, ic) != null) {
                return true;
            }
        }

        // Check current queue of consumers
        return findConsumer(consumers, ic) != null;
    }

    /**
     * Checks if the consumer is in the list and returns it if it is there
     * @param consumersList - list of consumers
     * @param ic - consumer
     * @return consumer if found, null otherwise
     */
    private static TImageConsumer findConsumer(List<TImageConsumer> consumersList, TImageConsumer ic) {
        TImageConsumer res = null;

        for (Iterator<TImageConsumer> i = consumersList.iterator(); i.hasNext();) {
            res = i.next();
            if (res.equals(ic)) {
                return res;
            }
        }

        return null;
    }

    /**
     * Use this method to finish decoding or lock the list of consumers
     * for a particular decoder
     * @param d - decoder
     */
    synchronized void lockDecoder(ImageDecoder d) {
        if (d == decoder) {
            decoder = null;
            startProduction(null);
        }
    }

    /**
     * Tries to find an appropriate decoder for the input stream and adds it
     * to the list of decoders
     * @return created decoder
     */
    private ImageDecoder createDecoder() {
        InputStream is = getInputStream();

        ImageDecoder decoder;

        if (is == null) {
            decoder = null;
        } else {
            decoder = ImageDecoder.createDecoder(this, is);
        }

        if (decoder != null) {
            synchronized (this) {
                decoders.add(decoder);
                this.decoder = decoder;
                loading = false;
                consumers = new ArrayList<TImageConsumer>(5); // Reset queue
            }

            return decoder;
        }
        // We were not able to find appropriate decoder
        List<TImageConsumer> cs;
        synchronized (this) {
            cs = consumers;
            consumers = new ArrayList<TImageConsumer>(5);
            loading = false;
        }
        abortAllConsumers(cs);

        return null;
    }

    /**
     * Stop the given decoder and remove it from the list
     * @param dr - decoder
     */
    private synchronized void removeDecoder(ImageDecoder dr) {
        lockDecoder(dr);
        decoders.remove(dr);
    }

    /**
     * This method serves as an entry point.
     * It starts the decoder and loads the image data.
     */
    public void load() {
        synchronized (this) {
            if (consumers.size() == 0) {
                loading = false;
                return;
            }
        }

        ImageDecoder d = createDecoder();
        if (d != null) {
            try {
                decoder.decodeImage();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                removeDecoder(d);
                abortAllConsumers(d.consumers);
            }
        }
    }
}
