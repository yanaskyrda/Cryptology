package utils;

public class Util {
    public static String toHexString(byte[] b) {
        StringBuilder sb = new StringBuilder();
        for (byte value : b)
            sb.append(String.format("%02X", value & 0xFF));
        return sb.toString();
    }
}
