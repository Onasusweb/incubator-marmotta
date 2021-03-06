/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.marmotta.kiwi.model.caching;

import com.google.common.hash.HashCode;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hasher;
import com.google.common.hash.Hashing;
import org.openrdf.model.Resource;
import org.openrdf.model.URI;
import org.openrdf.model.Value;

import java.nio.IntBuffer;
import java.util.Arrays;

/**
 * Add file description here!
 * <p/>
 * Author: Sebastian Schaffert
 */
public final class IntArray implements Comparable<IntArray> {

    private static HashFunction hashFunction = Hashing.goodFastHash(32);

    private int[] data;

    private HashCode goodHashCode;


    public IntArray(int[] data) {
        this.data = data;
    }

    private void ensureHashCode() {
        if(goodHashCode == null) {
            Hasher hasher = hashFunction.newHasher();
            for(int i : data) {
                hasher.putInt(i);
            }
            goodHashCode = hasher.hash();
        }
    }

    public static final IntArray createSPOCKey(Resource subject, URI property, Value object, Resource context){

        // the cache key is generated by appending the bytes of the hashcodes of subject, property, object, context and inferred and
        // storing them as a BigInteger; generating the cache key should thus be very efficient

        int s = subject != null ? subject.hashCode() : Integer.MIN_VALUE;
        int p = property != null ? property.hashCode() : Integer.MIN_VALUE;
        int o = object != null ? object.hashCode() : Integer.MIN_VALUE;
        int c = context != null ? context.hashCode() : Integer.MIN_VALUE;

        IntBuffer bb = IntBuffer.allocate(4);
        bb.put(s);
        bb.put(p);
        bb.put(o);
        bb.put(c);

        return new IntArray(bb.array());

    }

    public static final IntArray createSPOCMaxKey(Resource subject, URI property, Value object, Resource context){

        // the cache key is generated by appending the bytes of the hashcodes of subject, property, object, context and inferred and
        // storing them as a BigInteger; generating the cache key should thus be very efficient

        int s = subject != null ? subject.hashCode() : Integer.MAX_VALUE;
        int p = property != null ? property.hashCode() : Integer.MAX_VALUE;
        int o = object != null ? object.hashCode() : Integer.MAX_VALUE;
        int c = context != null ? context.hashCode() : Integer.MAX_VALUE;

        IntBuffer bb = IntBuffer.allocate(4);
        bb.put(s);
        bb.put(p);
        bb.put(o);
        bb.put(c);

        return new IntArray(bb.array());

    }

    public static final IntArray createCSPOKey(Resource subject, URI property, Value object, Resource context){

        // the cache key is generated by appending the bytes of the hashcodes of subject, property, object, context and inferred and
        // storing them as a BigInteger; generating the cache key should thus be very efficient

        int s = subject != null ? subject.hashCode() : Integer.MIN_VALUE;
        int p = property != null ? property.hashCode() : Integer.MIN_VALUE;
        int o = object != null ? object.hashCode() : Integer.MIN_VALUE;
        int c = context != null ? context.hashCode() : Integer.MIN_VALUE;

        IntBuffer bb = IntBuffer.allocate(4);
        bb.put(c);
        bb.put(s);
        bb.put(p);
        bb.put(o);

        return new IntArray(bb.array());

    }

    public static final IntArray createCSPOMaxKey(Resource subject, URI property, Value object, Resource context){

        // the cache key is generated by appending the bytes of the hashcodes of subject, property, object, context and inferred and
        // storing them as a BigInteger; generating the cache key should thus be very efficient

        int s = subject != null ? subject.hashCode() : Integer.MAX_VALUE;
        int p = property != null ? property.hashCode() : Integer.MAX_VALUE;
        int o = object != null ? object.hashCode() : Integer.MAX_VALUE;
        int c = context != null ? context.hashCode() : Integer.MAX_VALUE;

        IntBuffer bb = IntBuffer.allocate(4);
        bb.put(c);
        bb.put(s);
        bb.put(p);
        bb.put(o);

        return new IntArray(bb.array());

    }

    @Override
    public String toString() {
        return "IntArray{" +
                "data=" + Arrays.toString(data) +
                '}';
    }

    @Override
    public int compareTo(IntArray o) {
        for(int i=0; i < data.length && i < o.data.length; i++) {
            if(data[i] < o.data[i]) {
                return -1;
            } else if(data[i]  > o.data[i]) {
                return 1;
            }
        }
        return 0;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        IntArray intArray = (IntArray) o;

        return Arrays.equals(data, intArray.data);

    }

    @Override
    public int hashCode() {
        ensureHashCode();
        return goodHashCode.hashCode();
    }
}
