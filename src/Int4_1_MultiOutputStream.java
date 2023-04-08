import java.io.IOException;
import java.io.OutputStream;

// This class is used to redirect the output of the console to multiple streams

public class Int4_1_MultiOutputStream extends OutputStream
{
    OutputStream[] outputStreams;

    public Int4_1_MultiOutputStream(OutputStream... outputStreams)
    {
        this.outputStreams= outputStreams;
    }

    @Override
    public void write(int b) throws IOException
    {
        for (OutputStream out: outputStreams)
            out.write(b);
    }

    @Override
    public void write(byte[] b) throws IOException
    {
        for (OutputStream out: outputStreams)
            out.write(b);
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        for (int out = 0; out < outputStreams.length; out++ ) {

            if (out == 0) {
                // Writing to the console, don't filter out any bytes
                outputStreams[0].write(b, off, len);
            } else {
                // Writing to the file, filter out escape sequences
                int i = off;
                int end = off + len;
                while (i < end) {
                    byte currentByte = b[i++];
                    if (currentByte == 27) {
                        // Found an escape sequence, ignore until the end of the sequence
                        while (i < end) {
                            currentByte = b[i++];
                            if (currentByte == 'm') {
                                break;
                            }
                        }
                    } else {
                        // Found a normal byte, write it to the file
                        outputStreams[out].write(currentByte);
                    }
                }
            }
        }
    }

    @Override
    public void flush() throws IOException
    {
        for (OutputStream out: outputStreams)
            out.flush();
    }

    @Override
    public void close() throws IOException
    {
        for (OutputStream out: outputStreams)
            out.close();
    }
}