import java.util.HashMap;
import java.util.Map;

import static utils.Util.toHexString;

public class MD5 {


    private static final int[] SHIFT = {
            7, 12, 17, 22,
            5, 9, 14, 20,
            4, 11, 16, 23,
            6, 10, 15, 21
    };

    private static final int[] TABLE_T = new int[64];

    static {
        for (int i = 0; i < 64; i++)
            TABLE_T[i] = (int) (long) ((1L << 32) * Math.abs(Math.sin(i + 1)));
    }

    public static String compute(String message) {
        if (message == null) {
            throw new IllegalArgumentException("Message shouldn't be null");
        }
        byte[] messageBytes = message.getBytes();
        int messageLength = messageBytes.length;
        int numBlocks = ((messageLength + 8) / 64) + 1;
        int resultLength = numBlocks * 64;
        byte[] paddingBytes = new byte[resultLength - messageLength];
        paddingBytes[0] = (byte) 0x80;
        long messageLengthOfBits = (long) messageLength * 8;

        for (int i = 0; i < 8; i++) {
            paddingBytes[paddingBytes.length - 8 + i] = (byte) messageLengthOfBits;
            messageLengthOfBits >>>= 8;
        }

        Map<Character, Integer> abcd = new HashMap<>();
        abcd.put('a', 0x67452301);
        abcd.put('b', 0xEFCDAB89);
        abcd.put('c', 0x98BADCFE);
        abcd.put('d', 0x10325476);

        int[] buffer = new int[16];

        for (int i = 0; i < numBlocks; i++) {
            for (int j = 0, index = i * 64; j < 64; j++, index++) {
                buffer[j / 4] = (
                        ((index < messageLength)
                                ? messageBytes[index]
                                : paddingBytes[index - messageLength]
                        ) << 24
                ) | (buffer[j >>> 2] >>> 8);
            }

            Map<Character, Integer> prevABCD = new HashMap<>(abcd);

            for (int j = 0; j < 64; j++) {
                int div16 = j / 16;
                int f = 0;
                int bufferIndex = j;
                switch (div16) {
                    case 0:
                        f = (abcd.get('b') & abcd.get('c')) | (~abcd.get('b') & abcd.get('d'));
                        break;
                    case 1:
                        f = (abcd.get('b') & abcd.get('d')) | (abcd.get('c') & ~abcd.get('d'));
                        bufferIndex = (bufferIndex * 5 + 1) & 0x0F;
                        break;
                    case 2:
                        f = abcd.get('b') ^ abcd.get('c') ^ abcd.get('d');
                        bufferIndex = (bufferIndex * 3 + 5) & 0x0F;
                        break;
                    case 3:
                        f = abcd.get('c') ^ (abcd.get('b') | ~abcd.get('d'));
                        bufferIndex = (bufferIndex * 7) & 0x0F;
                        break;
                }

                int temp = abcd.get('b') +
                        Integer.rotateLeft(abcd.get('a') + f + buffer[bufferIndex] + TABLE_T[j],
                                SHIFT[(div16 << 2) | (j & 3)]);
                abcd.put('a', abcd.get('d'));
                abcd.put('d', abcd.get('c'));
                abcd.put('c', abcd.get('b'));
                abcd.put('b', temp);
            }
            abcd.put('a', abcd.get('a') + prevABCD.get('a'));
            abcd.put('b', abcd.get('b') + prevABCD.get('b'));
            abcd.put('c', abcd.get('c') + prevABCD.get('c'));
            abcd.put('d', abcd.get('d') + prevABCD.get('d'));
        }

        byte[] resultBytes = new byte[16];
        for (int i = 0, count = 0; i < 4; i++) {
            int n = abcd.get((char) ('a' + i));
            for (int j = 0; j < 4; j++, count++) {
                resultBytes[count] = (byte) n;
                n >>>= 8;
            }
        }
        return toHexString(resultBytes);
    }
}
