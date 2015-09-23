import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class MainClass {
    static int bs = 64;
    //bs- block size. 512 bit, 64 bytes

    public static void main(String[] args) throws IOException {
        Path path = Paths.get("test.txt");
        byte[] raw = Files.readAllBytes(path);
        //cb -amount of clear blocks
        //eb - amount of extra blocks
        //res - residue
        int cb = raw.length / bs;
        int res = raw.length % bs;
        int eb = 0;
        if (res > 56)
            eb = 1;
        byte[][] data = new byte[cb + eb+1][bs];
        for (int i = 0; i < cb; i++)
            for (int j = 0; j < bs; j++)
                data[i][j] = raw[j + bs * i];
        for (int i = 0; i < res; i++)
            data[cb][i] = raw[i + cb * bs];
        data[cb][res] = Byte.MIN_VALUE;
        byte[] size = size(raw.length);
        for (int i = 0; i < size.length; i++)
            data[cb + eb][bs - 4 + i] = size[i];
        for (int i = 0; i < cb + eb; i++) {
            for (int j = 0; j < bs; j++)
                System.out.print(String.format("0x%x ", data[i][j]));
            System.out.println();
        }
    }

    public static byte[] size(int size) {
        byte[] bytes = ByteBuffer.allocate(4).putInt(size * 8).array();
        /*for (byte b : bytes) {
            System.out.format("0x%x ", b);
        }*/
        /*for (byte b : bytes) {
            for (int q = 7; q >= 0; q--)
                System.out.print(b >> q & 1);
            System.out.print(" ");
        } */
        return bytes;
    }
}
