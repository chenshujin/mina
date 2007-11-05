/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 *
 */
package org.apache.mina.example.echoserver;

import org.apache.mina.common.IdleStatus;
import org.apache.mina.common.IoBuffer;
import org.apache.mina.common.IoFutureListener;
import org.apache.mina.common.IoHandler;
import org.apache.mina.common.IoHandlerAdapter;
import org.apache.mina.common.IoSession;
import org.apache.mina.common.IoSessionLogger;
import org.apache.mina.common.WriteFuture;
import org.apache.mina.filter.ssl.SslFilter;

/**
 * {@link IoHandler} implementation for echo server.
 *
 * @author The Apache MINA Project (dev@mina.apache.org)
 * @version $Rev$, $Date$,
 */
public class EchoProtocolHandler extends IoHandlerAdapter {
    @Override
    public void sessionCreated(IoSession session) {
        session.getConfig().setIdleTime(IdleStatus.BOTH_IDLE, 10);

        // We're going to use SSL negotiation notification.
        session.setAttribute(SslFilter.USE_NOTIFICATION);
    }

    @Override
    public void sessionClosed(IoSession session) throws Exception {
        IoSessionLogger.getLogger(session).info("CLOSED");
    }

    @Override
    public void sessionOpened(IoSession session) throws Exception {
        IoSessionLogger.getLogger(session).info("OPENED");
    }

    @Override
    public void sessionIdle(IoSession session, IdleStatus status) {
        IoSessionLogger.getLogger(session).info(
                "*** IDLE #" + session.getIdleCount(IdleStatus.BOTH_IDLE) + " ***");
    }

    @Override
    public void exceptionCaught(IoSession session, Throwable cause) {
        session.close();
    }

    @Override
    public void messageReceived(IoSession session, Object message)
            throws Exception {
        // Write the received data back to remote peer
        final IoBuffer src = (IoBuffer) message;
        session.write(src.duplicate()).addListener(new IoFutureListener<WriteFuture>() {
            public void operationComplete(WriteFuture future) {
                src.free();
            }
        });
    }
}