import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class MainClass {
    static int bs = 64;
    //bs- block size. 512 bit, 64 bytes

    public static void main(String[] args) throws IOException {
        /*Path path = Paths.get("test.txt");
        byte[] hash = computeHash(pad(path));
        for (int i = 0; i < hash.length; i++) {
            System.out.print(String.format("%x", hash[i]));
        }*/
        int a = 0x12345678;
        System.out.println(String.format("%x",a));
/*        for (int i = 0; i < 5; i++) {
            a = a << 1 | (a >> 31);
        }*/
        int x,z = a;
        for (int l = 0; l < 5; l++) {
            x = z >>> 31;
            z = z << 1 | x;
        }
        System.out.println(String.format("%x",z));

    }

    public static byte[][] pad(Path path) throws IOException {
        byte[] raw = Files.readAllBytes(path);
        //cb -amount of clear blocks
        //eb - amount of extra blocks
        //res - residue
        int cb = raw.length / bs;
        int res = raw.length % bs;
        int eb = 0;
        if (res > 56)
            eb = 1;
        byte[][] data = new byte[cb + eb + 1][bs];
        for (int i = 0; i < cb; i++)
            for (int j = 0; j < bs; j++)
                data[i][j] = raw[j + bs * i];
        for (int i = 0; i < res; i++)
            data[cb][i] = raw[i + cb * bs];
        data[cb][res] = Byte.MIN_VALUE;
        byte[] size = ByteBuffer.allocate(4).putInt(raw.length * 8).array();
        for (int i = 0; i < size.length; i++)
            data[cb + eb][bs - 4 + i] = size[i];
        return data;
    }

    public static byte[] computeHash(byte[][] data) {
        int[] h = new int[5];
        h[0] = 0x67452301;
        h[1] = 0xEFCDAB89;
        h[2] = 0x98BADCFE;
        h[3] = 0x10325476;
        h[4] = 0xC3D2E1F0;
        int a, b, c, d, e, f = 0, k = 0, temp;
        for (int i = 0; i < data.length; i++) {
            int[] w = new int[80];
            for (int j = 0; j < 16; j++)
                //transformation from (4x8)bit blocks to (1x32)block
                //e  = (((a & 0xff) << 8 | (b&0xff)) << 8 | (c&0xff)) << 8 | (d&0xff);
                w[j] = (((data[i][j * 4]&0xff) << 8 | (data[i][4 * j + 1]&0xff)) << 8 | (data[i][4 * j + 2]&0xff)) << 8 | (data[i][4 * j + 3]&0xff);
            for (int j = 16; j < 80; j++) {
                w[j] = w[j - 3] ^ w[j - 8] ^ w[j - 14] ^ w[j - 16];
                //left cyclic shift
                temp = w[j]>>>31;
                w[j] = w[j] << 1 | temp;
            }

            a = h[0];
            b = h[1];
            c = h[2];
            d = h[3];
            e = h[4];

            for (int j = 0; j < 80; j++) {

                if ((j >= 0) && (j <= 19)) {
                    f = (b & c) | ((~b) & d);
                    k = 0x5A827999;
                }
                if ((j >= 20) && (j <= 39)) {
                    f = b ^ c ^ d;
                    k = 0x6ED9EBA1;
                }
                if ((j >= 40) && (j <= 59)) {
                    f = (b & c) | (b & d) | (c & d);
                    k = 0x8F1BBCDC;
                }
                if ((j >= 60) && (j <= 79)) {
                    f = b ^ c ^ d;
                    k = 0xCA62C1D6;
                }
                int x,z,y;
                z = a;
                for (int l = 0; l < 5; l++) {
                    x = z >>> 31;
                    z = z << 1 | x;
                }
                temp = ((a << 5)|(a>>31)) + f + e + k + w[i];
                e = d;
                d = c;
                c = b << 30;
                b = a;
                a = temp;
            }
            h[0] += a;
            h[1] += b;
            h[2] += c;
            h[3] += d;
            h[4] += e;
        }
        byte[] result = new byte[20];
        byte[] cache;
        for (int i = 0; i < h.length; i++) {
            System.out.println(String.format("%x",h[i]));
        }

        for (int i = 0; i < 5; i++) {
            cache = ByteBuffer.allocate(4).putInt(h[i]).array();
            for (int j = 0; j < 4; j++)
                result[(i * 4) + j] = cache[j];
        }
        return result;
    }
}
