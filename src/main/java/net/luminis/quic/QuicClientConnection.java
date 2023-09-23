/*
 * Copyright © 2020, 2021, 2022, 2023 Peter Doornbosch
 *
 * This file is part of Kwik, an implementation of the QUIC protocol in Java.
 *
 * Kwik is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the
 * Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * Kwik is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for
 * more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package net.luminis.quic;

import net.luminis.quic.core.QuicClientConnectionImpl;
import net.luminis.quic.log.Logger;
import net.luminis.tls.TlsConstants;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.net.URI;
import java.net.UnknownHostException;
import java.nio.file.Path;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.time.Duration;
import java.util.List;


public interface QuicClientConnection extends QuicConnection {

    void connect(String alpn) throws IOException;

    void connect(String alpn, TransportParameters transportParameters) throws IOException;

    List<QuicStream> connect(String applicationProtocol, TransportParameters transportParameters, List<StreamEarlyData> earlyData) throws IOException;

    void keepAlive(int seconds);

    List<QuicSessionTicket> getNewSessionTickets();

    InetSocketAddress getLocalAddress();

    InetSocketAddress getServerAddress();

    List<X509Certificate> getServerCertificateChain();

    boolean isConnected();

    static Builder newBuilder() {
        return QuicClientConnectionImpl.newBuilder();
    }

    class StreamEarlyData {
        byte[] data;
        boolean closeOutput;

        public StreamEarlyData(byte[] data, boolean closeImmediately) {
            this.data = data;
            closeOutput = closeImmediately;
        }

        public byte[] getData() {
            return data;
        }

        public boolean isCloseOutput() {
            return closeOutput;
        }
    }

    interface Builder {
        QuicClientConnection build() throws SocketException, UnknownHostException;

        Builder connectTimeout(Duration duration);

        Builder version(QuicVersion version);

        Builder initialVersion(QuicVersion version);

        Builder preferredVersion(QuicVersion version);

        Builder logger(Logger log);

        Builder sessionTicket(QuicSessionTicket ticket);

        Builder proxy(String host);

        Builder secrets(Path secretsFile);

        Builder uri(URI uri);

        Builder connectionIdLength(int length);

        Builder initialRtt(int initialRtt);

        Builder cipherSuite(TlsConstants.CipherSuite cipherSuite);

        Builder noServerCertificateCheck();

        Builder quantumReadinessTest(int nrOfDummyBytes);

        Builder clientCertificate(X509Certificate certificate);

        Builder clientCertificateKey(PrivateKey privateKey);

        Builder socketFactory(DatagramSocketFactory socketFactory);
    }

}
