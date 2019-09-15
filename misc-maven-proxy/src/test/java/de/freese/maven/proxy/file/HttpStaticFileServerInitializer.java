/*
 * Copyright 2012 The Netty Project The Netty Project licenses this file to you under the Apache License, version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at: http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law
 * or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing permissions and limitations under the License.
 */
package de.freese.maven.proxy.file;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.stream.ChunkedWriteHandler;

/**
 * @author Thomas Freese
 */
public class HttpStaticFileServerInitializer extends ChannelInitializer<SocketChannel>
{
    /**
     *
     */
    private final SslContext sslCtx;

    /**
     * Erstellt ein neues {@link HttpStaticFileServerInitializer} Object.
     *
     * @param sslCtx {@link SslContext}
     */
    public HttpStaticFileServerInitializer(final SslContext sslCtx)
    {
        super();

        this.sslCtx = sslCtx;
    }

    /**
     * @see io.netty.channel.ChannelInitializer#initChannel(io.netty.channel.Channel)
     */
    @Override
    public void initChannel(final SocketChannel ch)
    {
        ChannelPipeline pipeline = ch.pipeline();

        if (this.sslCtx != null)
        {
            pipeline.addLast(this.sslCtx.newHandler(ch.alloc()));
        }

        pipeline.addLast(new HttpServerCodec());
        pipeline.addLast(new HttpObjectAggregator(65536));
        pipeline.addLast(new ChunkedWriteHandler());
        pipeline.addLast(new HttpStaticFileServerHandler());
    }
}