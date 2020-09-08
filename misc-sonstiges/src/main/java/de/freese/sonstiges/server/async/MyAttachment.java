// Created: 08.09.2020
package de.freese.sonstiges.server.async;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;

/**
 * @author Thomas Freese
 */
class MyAttachment
{
    /**
     *
     */
    ByteBuffer byteBuffer;

    /**
     *
     */
    AsynchronousSocketChannel channel;

    /**
     *
     */
    StringBuilder httpHeader = new StringBuilder();
}