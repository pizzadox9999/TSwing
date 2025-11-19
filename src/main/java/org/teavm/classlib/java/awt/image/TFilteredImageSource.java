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
 * @author Igor V. Stolyarov
 */
package org.teavm.classlib.java.awt.image;

import java.util.Hashtable;


public class TFilteredImageSource implements TImageProducer {

    private final TImageProducer source;
    private final TImageFilter filter;

    private final Hashtable<TImageConsumer, TImageConsumer> consTable = new Hashtable<TImageConsumer, TImageConsumer>();

    public TFilteredImageSource(TImageProducer orig, TImageFilter imgf) {
        source = orig;
        filter = imgf;
    }

    public synchronized boolean isConsumer(TImageConsumer ic) {
        if(ic != null) {
            return consTable.containsKey(ic);
        }
        return false;
    }

    public void startProduction(TImageConsumer ic) {
        addConsumer(ic);
        TImageConsumer fic = consTable.get(ic);
        source.startProduction(fic);
    }

    public void requestTopDownLeftRightResend(TImageConsumer ic) {
        if(ic != null && isConsumer(ic)){
            TImageFilter fic = (TImageFilter) consTable.get(ic);
            fic.resendTopDownLeftRight(source);
        }
    }

    public synchronized void removeConsumer(TImageConsumer ic) {
        if(ic != null && isConsumer(ic)){
            TImageConsumer fic = consTable.get(ic);
            source.removeConsumer(fic);
            consTable.remove(ic);
        }
    }

    public synchronized void addConsumer(TImageConsumer ic) {
        if(ic != null && !isConsumer(ic)){
            TImageConsumer fic = filter.getFilterInstance(ic);
            source.addConsumer(fic);
            consTable.put(ic, fic);
        }
    }
}
