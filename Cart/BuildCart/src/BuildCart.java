import javax.swing.*;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: Rasmus
 * Date: 13-10-13
 * Time: 21:01
 */
public class BuildCart {

    static int BANKS = 8;
    static int BANK_SIZE = 0x2000;
    static int HEADER_SIZE = 0x2A;
    static int LAST_HEADER_SIZE = 0x116;

    static int HIGH_PROGRAM_START = 0xA000;
    static int LOW_PROGRAM_START = 0x2800;

    static int ROAD_HUNTER_HIGH_END = 0xE114;

    public static void main(String[] args) {
        new BuildCart();
    }

    byte[] cartFile = new byte[BANKS * BANK_SIZE];
    String report = "";

    BuildCart() {
        try {
            // Fill cart file with 0xFF
            for (int i = 0; i < cartFile.length; i++) {
                cartFile[i] = (byte) 0xFF;
            }
            // Create header and loader in all ROM banks
            loadFileSegment("cart-headerc.bin", 0, 0, LAST_HEADER_SIZE);
            for (int bank = 1; bank < BANKS; bank++) {
                System.arraycopy(cartFile, 0, cartFile, bank * BANK_SIZE, bank < BANKS - 1 ? HEADER_SIZE : LAST_HEADER_SIZE);
            }
            int offset = HEADER_SIZE;
            // Road Hunter
            int rdmapPart1 = 63 * 128;
            int rdmapPart2 = 45 * 128;
            offset = loadFileSegment("..\\map\\rdmap1.bin", 0, offset, rdmapPart1);
            offset = loadFileSegment("..\\map\\rdmap1.bin", rdmapPart1, offset, rdmapPart2);
            offset = loadFileSegment("..\\map\\rdmap2.bin", 0, offset, rdmapPart1);
            offset = loadFileSegment("..\\map\\rdmap2.bin", rdmapPart1, offset, rdmapPart2);
            offset = loadFileSegment("..\\map\\rdmap3.bin", 0, offset, rdmapPart1);
            offset = loadFileSegment("..\\map\\rdmap3.bin", rdmapPart1, offset, rdmapPart2);
            offset = loadFileSegment("memdump.bin", HIGH_PROGRAM_START, offset, ROAD_HUNTER_HIGH_END - HIGH_PROGRAM_START);
            // Save file
            FileOutputStream fileOutputStream = new FileOutputStream("RoadHunter3.bin");
            fileOutputStream.write(cartFile, 0, BANKS * BANK_SIZE);
            fileOutputStream.close();
            // Report
            System.out.println("\n" + report);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    int loadFileSegment(String fileName, int sourceOffset, int destOffset, int length)  throws IOException {
        report += "Bank " + (destOffset / BANK_SIZE) +  ": >" + Integer.toHexString(0x6000 + 2 * (BANKS - (destOffset / BANK_SIZE) - 1)) + " Offset: >" + Integer.toHexString(destOffset % BANK_SIZE) + " Length: >" + Integer.toHexString(length) + ". " + fileName + "\n";
        int oldDestOffset = destOffset;
        byte[] buffer = new byte[0x10000];
        FileInputStream fileInputStream = new FileInputStream(fileName);
        int n = fileInputStream.read(buffer, 0, 0x10000);
        if (n < sourceOffset + length) {
            System.out.println("Requested " + length + " bytes, only " + n + " bytes (>" + Integer.toHexString(n) + ") available.");
            length = n;
        }
        fileInputStream.close();
        int bytesLeft = length;
        while (bytesLeft > 0) {
            int bankBytesLeft = BANK_SIZE - destOffset % BANK_SIZE;
            int bytesToCopy = Math.min(bytesLeft, bankBytesLeft);
            System.arraycopy(buffer, sourceOffset, cartFile, destOffset, bytesToCopy);
            sourceOffset += bytesToCopy;
            destOffset += bytesToCopy;
            bytesLeft -= bytesToCopy;
            if (bytesLeft > 0 || destOffset % BANK_SIZE == 0) {
                destOffset += destOffset / BANK_SIZE < BANKS - 1 ? HEADER_SIZE : LAST_HEADER_SIZE; // Skip past the header
            }
        }
        if (destOffset % 2 == 1) {
            destOffset++;
        }
        System.out.println(length + " (>" + Integer.toHexString(length) + ") bytes read from " + fileName + " at offset " + oldDestOffset + " (>" + Integer.toHexString(oldDestOffset) + "). New offset " + destOffset + " (>" + Integer.toHexString(destOffset) + ")).");
        return destOffset;
    }
}
