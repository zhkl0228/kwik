/*
 * Copyright © 2019 Peter Doornbosch
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

import org.junit.jupiter.api.Test;

import java.nio.ByteBuffer;

import static org.assertj.core.api.Assertions.assertThat;


class VariableLengthIntegerTest {

    @Test
    void parseSingleByteInteger() {
        // Taken from https://tools.ietf.org/html/draft-ietf-quic-transport-19#section-16
        // "and the single byte 25 decodes to 37"
        int value = VariableLengthInteger.parse(wrap((byte) 0x25));

        assertThat(value).isEqualTo(37);
    }

    @Test
    void parseTwoByteInteger() {
        // Taken from https://tools.ietf.org/html/draft-ietf-quic-transport-19#section-16
        // "the two byte sequence 7b bd decodes to 15293; "
        int value = VariableLengthInteger.parse(wrap((byte) 0x7b, (byte) 0xbd));

        assertThat(value).isEqualTo(15293);
    }

    @Test
    void parseSingleByteIntegerEncodedInTwoByte() {
        // Taken from https://tools.ietf.org/html/draft-ietf-quic-transport-19#section-16
        // "(as does the two byte sequence 40 25)"
        int value = VariableLengthInteger.parse(wrap((byte) 0x40, (byte) 0x25));

        assertThat(value).isEqualTo(37);
    }

    @Test
    void parseFourByteInteger() {
        // Taken from https://tools.ietf.org/html/draft-ietf-quic-transport-19#section-16
        // "the four byte sequence 9d 7f 3e 7d decodes to 494878333;"
        int value = VariableLengthInteger.parse(wrap((byte) 0x9d, (byte) 0x7f, (byte) 0x3e, (byte) 0x7d));

        assertThat(value).isEqualTo(494878333);
    }

    @Test
    void encodeSingleByteInteger() {
        ByteBuffer buffer = ByteBuffer.allocate(8);
        int encodedSize = VariableLengthInteger.encode(37, buffer);

        assertThat(encodedSize).isEqualTo(1);
        assertThat(buffer.position()).isEqualTo(1);
        buffer.flip();
        assertThat(buffer.get()).isEqualTo((byte) 0x25);
    }

    @Test
    void encodeTwoByteInteger() {
        ByteBuffer buffer = ByteBuffer.allocate(8);
        int encodedSize = VariableLengthInteger.encode(15293, buffer);

        assertThat(encodedSize).isEqualTo(2);
        assertThat(buffer.position()).isEqualTo(2);
        buffer.flip();
        assertThat(buffer.get()).isEqualTo((byte) 0x7b);
        assertThat(buffer.get()).isEqualTo((byte) 0xbd);
    }

    @Test
    void encodeFourByteInteger() {
        ByteBuffer buffer = ByteBuffer.allocate(8);
        int encodedSize = VariableLengthInteger.encode(494878333, buffer);

        assertThat(encodedSize).isEqualTo(4);
        assertThat(buffer.position()).isEqualTo(4);
        buffer.flip();
        assertThat(buffer.get()).isEqualTo((byte) 0x9d);
        assertThat(buffer.get()).isEqualTo((byte) 0x7f);
        assertThat(buffer.get()).isEqualTo((byte) 0x3e);
        assertThat(buffer.get()).isEqualTo((byte) 0x7d);
    }

    @Test
    void encodeMaxInteger() {
        ByteBuffer buffer = ByteBuffer.allocate(8);
        int encodedSize = VariableLengthInteger.encode(Integer.MAX_VALUE, buffer);

        assertThat(encodedSize).isEqualTo(8);
        assertThat(buffer.position()).isEqualTo(8);
        buffer.flip();
        assertThat(buffer.get()).isEqualTo((byte) 0xc0);
        assertThat(buffer.get()).isEqualTo((byte) 0x00);
        assertThat(buffer.get()).isEqualTo((byte) 0x00);
        assertThat(buffer.get()).isEqualTo((byte) 0x00);
        assertThat(buffer.get()).isEqualTo((byte) 0x7f);
        assertThat(buffer.get()).isEqualTo((byte) 0xff);
        assertThat(buffer.get()).isEqualTo((byte) 0xff);
        assertThat(buffer.get()).isEqualTo((byte) 0xff);
    }

    private ByteBuffer wrap(byte... bytes) {
        return ByteBuffer.wrap(bytes);
    }

}