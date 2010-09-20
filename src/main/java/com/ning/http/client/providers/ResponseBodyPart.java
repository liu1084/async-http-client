/*
 * Copyright 2010 Ning, Inc.
 *
 * Ning licenses this file to you under the Apache License, version 2.0
 * (the "License"); you may not use this file except in compliance with the
 * License.  You may obtain a copy of the License at:
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package com.ning.http.client.providers;

import com.ning.http.client.AsyncHttpProvider;
import com.ning.http.client.HttpResponseBodyPart;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.handler.codec.http.HttpChunk;
import org.jboss.netty.handler.codec.http.HttpResponse;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.util.concurrent.atomic.AtomicReference;

/**
 * A callback class used when an HTTP response body is received.
 */
public class ResponseBodyPart extends HttpResponseBodyPart {

    private final HttpChunk chunk;
    private final HttpResponse response;
    private final AtomicReference<byte[]> bytes = new AtomicReference(null);

    public ResponseBodyPart(URI uri, HttpResponse response, AsyncHttpProvider<HttpResponse> provider) {
        super(uri, provider);
        this.chunk = null;
        this.response = response;
    }

    public ResponseBodyPart(URI uri, HttpResponse response, AsyncHttpProvider<HttpResponse> provider, HttpChunk chunk) {
        super(uri, provider);
        this.chunk = chunk;
        this.response = response;
    }

    /**
     * Return the response body's part bytes received.
     *
     * @return the response body's part bytes received.
     */
    public byte[] getBodyPartBytes() {

        if (bytes.get() != null) {
            return bytes.get();
        }

        ChannelBuffer b = chunk != null ? chunk.getContent() : response.getContent();
        int read = b.readableBytes();
        byte[] rb = new byte[read];
        b.readBytes(rb);
        bytes.set(rb);
        return bytes.get();
    }

    public int writeTo(OutputStream outputStream) throws IOException {
        ChannelBuffer b = chunk != null ? chunk.getContent() : response.getContent();
        int read = b.readableBytes();
        b.readBytes(outputStream, read);
        return read;
    }


    protected HttpChunk chunk() {
        return chunk;
    }
}