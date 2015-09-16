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
        byte[] data = Files.readAllBytes(path);
        int nob;
        if (data.length % bs < 56)
            nob = data.length / bs + 1;
        else
            nob = data.length / bs + 2;
        //number of blocks
        byte[][] temp = new byte[nob][bs];
        int i = 0;
        while (i < data.length / bs) {
            for (int j = 0; j < bs; j++)
                temp[i][j] = data[(i + 1) * j];
            i++;
        }
        if (data.length % bs > 0) {
            for (int j = 0; j < data.length % bs; j++)
                temp[i][j] = data[(i + 1) * j];
            temp[i][data.length % bs] = Byte.MIN_VALUE;

            if (data.length % bs < 56) {
                for (int j = data.length % bs + 1; j < 56; j++)
                    temp[i][j] = 0;
                //size inserting
            } else {
                for (int j = data.length % bs + 1; j < bs; j++)
                    temp[i][j] = 0;
                for (int j = 0; j < 56; j++) {
                    temp[i + 1][j] = 0;
                }
            }   //size inserting
        } else {
            temp[i][0] = Byte.MIN_VALUE;
            for (int j = 1; j < 56; j++)
                temp[i][j] = 0;
            //size inserting
        }
        //Minimal value of byte = 10000000 in binary representation
        for (int j = 0; j < 2; j++) {
            for (int k = 0; k < 64; k++) {
                for (int q = 7; q >= 0; q--)
                    System.out.print(temp[j][k] >> q & 1);
                System.out.print(" ");
            }
            System.out.println();
        }
    }

    public static byte[] size(int size) {
        byte[] bytes = ByteBuffer.allocate(4).putInt(size * 8).array();
        /*for (byte b : bytes) {
            System.out.format("0x%x ", b);
        }*/
        for (byte b : bytes) {
            for (int q = 7; q >= 0; q--)
            System.out.print(b >> q & 1);
            System.out.print(" ");
        }
        return bytes;
    }
}
