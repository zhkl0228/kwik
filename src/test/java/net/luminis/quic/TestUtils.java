/*
 * Copyright © 2020, 2021 Peter Doornbosch
 *
 * This file is part of Kwik, a QUIC client Java library
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

import net.luminis.quic.crypto.Keys;
import net.luminis.quic.log.Logger;
import org.mockito.internal.util.reflection.FieldSetter;

import javax.crypto.Cipher;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TestUtils {

    /**
     * Create a valid Keys object that can be used for encrypting/decrypting packets in tests.
     * @return
     * @throws Exception
     */
    public static Keys createKeys() throws Exception {
        Keys keys = mock(Keys.class);
        when(keys.getHp()).thenReturn(new byte[16]);
        when(keys.getWriteIV()).thenReturn(new byte[12]);
        when(keys.getWriteKey()).thenReturn(new byte[16]);
        Keys dummyKeys = new Keys(Version.getDefault(), new byte[16], null, mock(Logger.class));
        FieldSetter.setField(dummyKeys, Keys.class.getDeclaredField("hp"), new byte[16]);
        Cipher hpCipher = dummyKeys.getHeaderProtectionCipher();
        when(keys.getHeaderProtectionCipher()).thenReturn(hpCipher);
        FieldSetter.setField(dummyKeys, Keys.class.getDeclaredField("writeKey"), new byte[16]);
        Cipher wCipher = dummyKeys.getWriteCipher();
        // The Java implementation of this cipher (GCM), prevents re-use with the same iv.
        // As various tests often use the same packet numbers (used for creating the nonce), the cipher must be re-initialized for each test.
        // Still, a consequence is that generatePacketBytes cannot be called twice on the same packet.
        when(keys.getWriteCipher()).thenReturn(wCipher);
        when(keys.getWriteKeySpec()).thenReturn(dummyKeys.getWriteKeySpec());

        when(keys.aeadEncrypt(any(), any(), any())).thenCallRealMethod();
        when(keys.createHeaderProtectionMask(any())).thenCallRealMethod();

        return keys;
    }
}

