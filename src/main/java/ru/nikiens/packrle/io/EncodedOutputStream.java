package ru.nikiens.packrle.io;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;

public class EncodedOutputStream extends FilterOutputStream {
    private static final int MAX__RUN_LEN = 257;

    private int count, current;

    public EncodedOutputStream(OutputStream out) {
        super(out);
    }

    private void writeEncoded() throws IOException {
        if (count >= 1) {
            super.write(current);

            if (count >= 2) {
                super.write(current);
                super.write(count - 2);
            }
        }
        count = 1;
    }

    public void write(byte[] b, int off, int len) throws IOException {
        Objects.checkFromIndexSize(off, len, b.length);

        for (int i = 0; i < len; i++) {
            if (b[off + i] != current || count == MAX__RUN_LEN) {
                writeEncoded();
                current = b[off + i];
            } else {
                count++;
            }
        }
    }

    public void close() throws IOException {
        writeEncoded();
        super.close();
    }
}