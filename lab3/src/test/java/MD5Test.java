import org.junit.jupiter.api.Test;
import utils.Util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class MD5Test {

    @Test
    public void emptyStringShouldReturnRightHash() {
        assertTrue(MD5.compute("").equalsIgnoreCase("d41d8cd98f00b204e9800998ecf8427e"));
    }

    @Test
    public void hashesShouldNotBeTheSameForSlightlyDifferentStrings() {
        assertFalse(MD5.compute("123").equalsIgnoreCase(MD5.compute("12 3")));
    }

    @Test
    public void nullStringShouldThrowException() {
        assertThrows(IllegalArgumentException.class,
                () -> MD5.compute(null), "Message shouldn't be null");
    }

    @Test
    public void MD5computeShouldReturnSameResultAsLibImpl() throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("MD5");
        Random random = new Random(System.currentTimeMillis());

        StringBuilder string = new StringBuilder();
        for (int i = 0; i < 100; i++) {
            for (int j = 0; j < random.nextInt(100) + 1; j++) {
                string.append((char) random.nextInt(92) + 33);
            }
            assertTrue(Util.toHexString(digest.digest(string.toString().getBytes()))
                    .equalsIgnoreCase(MD5.compute(string.toString())));
        }
    }
}