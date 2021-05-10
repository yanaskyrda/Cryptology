public class XTEA {
    private final static int DELTA = 0x9E3779B9;
    private final static int ROUNDS = 32;

    private final int[] K = new int[4];

    public XTEA(byte[] key) {
        if (key == null) {
            throw new IllegalArgumentException("Key shouldn't be null");
        }
        if (key.length != 16) {
            throw new IllegalArgumentException("Length of the key should be 16 characters. Current length = " +
                    key.length);
        }

        for (int i = 0, offset = 0; i < 4; i++, offset += 4) {
            K[i] = ((key[offset] & 0xff)) |
                    ((key[offset + 1] & 0xff) << 8) |
                    ((key[offset + 2] & 0xff) << 16) |
                    ((key[offset + 3] & 0xff) << 24);
        }
    }

    public byte[] encipher(String string) {
        if (string == null || string.isEmpty() || string.isBlank()) {
            throw new IllegalArgumentException("String should not be empty");
        }

        byte[] bytes = string.getBytes();
        int paddedSize = ((bytes.length / 8) + (((bytes.length % 8) == 0) ? 0 : 1)) * 2;
        int[] buffer = new int[paddedSize + 1];
        buffer[0] = bytes.length;
        packToIntBuffer(bytes, buffer, 1);
        encipherBuffer(buffer);
        return unpackFromIntBuffer(buffer, 0, buffer.length * 4);
    }


    public String decipher(byte[] byteArray) {
        if (byteArray.length == 0) {
            throw new IllegalArgumentException("Data to decipher shouldn't be empty");
        }
        if (byteArray.length % 4 != 0) {
            throw new IllegalArgumentException("Length should be divisible by 4");
        }
        if ((byteArray.length / 4) % 2 != 1) {
            throw new IllegalArgumentException("Length shouldn't be divisible by 8");
        }

        int[] buffer = new int[byteArray.length / 4];
        packToIntBuffer(byteArray, buffer, 0);
        decipherBuffer(buffer);
        return new String(unpackFromIntBuffer(buffer, 1, buffer[0]));
    }

    private void encipherBuffer(int[] buffer) {
        for (int i = 1; i < buffer.length; i += 2) {
            int v0 = buffer[i];
            int v1 = buffer[i + 1];
            int sum = 0;
            for (int n = 0; n < ROUNDS; n++) {
                sum += DELTA;
                v0 += ((v1 << 4) + K[0] ^ v1) + (sum ^ (v1 >>> 5)) + K[1];
                v1 += ((v0 << 4) + K[2] ^ v0) + (sum ^ (v0 >>> 5)) + K[3];
            }
            buffer[i] = v0;
            buffer[i + 1] = v1;
        }
    }

    private void decipherBuffer(int[] buffer) {
        for (int i = 1; i < buffer.length; i += 2) {
            int v0 = buffer[i];
            int v1 = buffer[i + 1];
            int sum = DELTA * ROUNDS;
            for (int n = 0; n < ROUNDS; n++) {
                v1 -= ((v0 << 4) + K[2] ^ v0) + (sum ^ (v0 >>> 5)) + K[3];
                v0 -= ((v1 << 4) + K[0] ^ v1) + (sum ^ (v1 >>> 5)) + K[1];
                sum -= DELTA;
            }
            buffer[i] = v0;
            buffer[i + 1] = v1;
        }
    }

    private void packToIntBuffer(byte[] src, int[] dest, int destStart) {
        if (destStart + (src.length / 4) > dest.length) {
            throw new IllegalArgumentException("dest array couldn't fit src");
        }

        int shift = 24;
        int j = destStart;
        dest[j] = 0;
        for (byte b : src) {
            dest[j] |= ((b & 0xff) << shift);
            if (shift == 0) {
                shift = 24;

                j++;
                if (j < dest.length) {
                    dest[j] = 0;
                }
            } else {
                shift -= 8;
            }
        }
    }

    private byte[] unpackFromIntBuffer(int[] src, int srcStart, int destLength) {
        if (destLength > (src.length - srcStart) * 4) {
            throw new IllegalArgumentException("src doesn't have enough data from position = " + srcStart
                    + " to fill array with length = "
                    + destLength);
        }

        byte[] dest = new byte[destLength];
        int i = srcStart;
        for (int j = 0, count = 0; j < destLength; j++) {
            dest[j] = (byte) ((src[i] >> (24 - (8 * count))) & 0xff);
            count++;
            if (count == 4) {
                count = 0;
                i++;
            }
        }
        return dest;
    }
}
