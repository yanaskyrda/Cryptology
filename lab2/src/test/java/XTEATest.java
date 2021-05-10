import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class XTEATest {
    private static final XTEA xtea = new XTEA("124562sa35jfjgas".getBytes());

    @Test
    void encipherThrowExceptionOnEmptyString() {
        assertThrows(IllegalArgumentException.class,
                () -> xtea.encipher(""), "String should not be empty");
    }

    @Test
    void encipherThrowExceptionOnNullString() {
        assertThrows(IllegalArgumentException.class,
                () -> xtea.encipher(null), "String should not be empty");
    }

    @Test
    void encipherThrowExceptionOnBlankString() {
        assertThrows(IllegalArgumentException.class,
                () -> xtea.encipher("     "), "String should not be empty");
    }

    @Test
    void decipherThrowExceptionWhenPassEmptyData() {
        assertThrows(IllegalArgumentException.class,
                () -> xtea.decipher(new byte[]{}),
                "Data to decipher shouldn't be empty");
    }

    @Test
    void decipherThrowExceptionWhenLengthOfArrayToDecipherNotDivisibleBy4() {
        assertThrows(IllegalArgumentException.class,
                () -> xtea.decipher(new byte[]{123}),
                "Length should be divisible by 4");
    }

    @Test
    void decipherThrowExceptionWhenLengthOfArrayToDecipherDivisibleBy8() {
        assertThrows(IllegalArgumentException.class,
                () -> xtea.decipher(new byte[]{
                        123, 123, 123, 123,
                        123, 123, 123, 123
                }),
                "Length shouldn't be divisible by 8");
    }

    @Test
    void constructorShouldThrowExceptionOnNullKey() {
        assertThrows(IllegalArgumentException.class,
                () -> new XTEA(null),
                "Key shouldn't be null");
    }

    @Test
    void constructorShouldThrowExceptionOnKeyWithLengthNot16() {
        assertThrows(IllegalArgumentException.class,
                () -> new XTEA("".getBytes()),
                "Length of the key should be 16 characters. Current length = 0");
        assertThrows(IllegalArgumentException.class,
                () -> new XTEA("123456".getBytes()),
                "Length of the key should be 16 characters. Current length = 6");
        assertThrows(IllegalArgumentException.class,
                () -> new XTEA("12345678901234567890".getBytes()),
                "Length of the key should be 16 characters. Current length = 20");
    }

    @Test
    void constructorShouldNotThrowExceptionOnKeyWithLength16() {
        assertDoesNotThrow(() -> new XTEA("0123456789123456".getBytes()));
    }

    @Test
    void decipherOfEncipherResultShouldBeEqualsToOriginalString() {
        Random random = new Random(System.currentTimeMillis());
        StringBuilder string = new StringBuilder();
        for (int i = 0; i < 100; i++) {
            for (int j = 0; j < random.nextInt(100) + 1; j++) {
                string.append((char) random.nextInt(92) + 33);
            }
            assertEquals(string.toString(), xtea.decipher(xtea.encipher(string.toString())));
        }
    }
}