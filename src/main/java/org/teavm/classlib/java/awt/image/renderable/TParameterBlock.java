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
package org.teavm.classlib.java.awt.image.renderable;

import java.awt.image.RenderedImage;
import java.io.Serializable;
import java.util.Vector;

public class TParameterBlock implements Cloneable, Serializable {


    private static final long serialVersionUID = -7577115551785240750L;

    protected Vector<Object> sources = new Vector<Object>();

    protected Vector<Object> parameters = new Vector<Object>();

    public TParameterBlock(Vector<Object> sources, Vector<Object> parameters) {
        setSources(sources);
        setParameters(parameters);
    }

    public TParameterBlock(Vector<Object> sources) {
        setSources(sources);
    }

    public TParameterBlock() {}

    public TParameterBlock setSource(Object source, int index) {
        if(sources.size() < index + 1){
            sources.setSize(index + 1);
        }
        sources.setElementAt(source, index);
        return this;
    }

    public TParameterBlock set(Object obj, int index) {
        if(parameters.size() < index + 1){
            parameters.setSize(index + 1);
        }
        parameters.setElementAt(obj, index);
        return this;
    }

    public TParameterBlock addSource(Object source) {
        sources.addElement(source);
        return this;
    }

    public TParameterBlock add(Object obj) {
        parameters.addElement(obj);
        return this;
    }

    public void setSources(Vector<Object> sources) {
        this.sources = sources;
    }

    public void setParameters(Vector<Object> parameters) {
        this.parameters = parameters;
    }

    public Vector<Object> getSources() {
        return sources;
    }

    public Vector<Object> getParameters() {
        return parameters;
    }

    public Object getSource(int index) {
        return sources.elementAt(index);
    }

    public Object getObjectParameter(int index) {
        return parameters.elementAt(index);
    }

    public Object shallowClone() {
        try{
            return super.clone();
        }catch(Exception e){
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public Object clone() {
        TParameterBlock replica;
        try{
            replica = (TParameterBlock)super.clone();
        }catch(Exception e){
            return null;
        }
        if(sources != null){
            replica.setSources((Vector<Object>)(sources.clone()));
        }
        if(parameters != null){
            replica.setParameters((Vector<Object>)(parameters.clone()));
        }
        return replica;
    }

    @SuppressWarnings("unchecked")
    public Class[] getParamClasses() {
        int count = parameters.size();
        Class paramClasses[] = new Class[count];

        for(int i = 0; i < count; i++){
            paramClasses[i] = parameters.elementAt(i).getClass();
        }
        return paramClasses;
    }

    public TRenderableImage getRenderableSource(int index) {
        return (TRenderableImage)sources.elementAt(index);
    }

    public TParameterBlock set(short s, int index) {
        return set(new Short(s), index);
    }

    public TParameterBlock add(short s) {
        return add(new Short(s));
    }

    public TParameterBlock set(long l, int index) {
        return set(new Long(l), index);
    }

    public TParameterBlock add(long l) {
        return add(new Long(l));
    }

    public TParameterBlock set(int i, int index) {
        return set(new Integer(i), index);
    }

    public TParameterBlock add(int i) {
        return add(new Integer(i));
    }

    public TParameterBlock set(float f, int index) {
        return set(new Float(f), index);
    }

    public TParameterBlock add(float f) {
        return add(new Float(f));
    }

    public TParameterBlock set(double d, int index) {
        return set(new Double(d), index);
    }

    public TParameterBlock add(double d) {
        return add(new Double(d));
    }

    public TParameterBlock set(char c, int index) {
        return set(new Character(c), index);
    }

    public TParameterBlock add(char c) {
        return add(new Character(c));
    }

    public TParameterBlock set(byte b, int index) {
        return set(new Byte(b), index);
    }

    public TParameterBlock add(byte b) {
        return add(new Byte(b));
    }

    public RenderedImage getRenderedSource(int index) {
        return (RenderedImage)sources.elementAt(index);
    }

    public short getShortParameter(int index) {
        return ((Short)parameters.elementAt(index)).shortValue();
    }

    public long getLongParameter(int index) {
        return ((Long)parameters.elementAt(index)).longValue();
    }

    public int getIntParameter(int index) {
        return ((Integer)parameters.elementAt(index)).intValue();
    }

    public float getFloatParameter(int index) {
        return ((Float)parameters.elementAt(index)).floatValue();
    }

    public double getDoubleParameter(int index) {
        return ((Double)parameters.elementAt(index)).doubleValue();
    }

    public char getCharParameter(int index) {
        return ((Character)parameters.elementAt(index)).charValue();
    }

    public byte getByteParameter(int index) {
        return ((Byte)parameters.elementAt(index)).byteValue();
    }

    public void removeSources() {
        sources.removeAllElements();
    }

    public void removeParameters() {
        parameters.removeAllElements();
    }

    public int getNumSources() {
        return sources.size();
    }

    public int getNumParameters() {
        return parameters.size();
    }
}
