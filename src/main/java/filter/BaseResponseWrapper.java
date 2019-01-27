package filter;

import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.*;

public class BaseResponseWrapper extends HttpServletResponseWrapper {
    private CharArrayWriter writer;
    private ByteArrayOutputStream outputStream;

    public BaseResponseWrapper(HttpServletResponse response) {
        super(response);
    }

    public CharArrayWriter getWrapperWriter(){
        return writer;
    }

    public ByteArrayOutputStream getWrapperOutPutStream(){
        return outputStream;
    }

    @Override
    public PrintWriter getWriter() throws IOException {
        writer = new CharArrayWriter();
        return new PrintWriter(writer);
    }

    @Override
    public ServletOutputStream getOutputStream() throws IOException {
        outputStream = new ByteArrayOutputStream();
        return new OutputStreamWrapper(outputStream);
    }

    private class OutputStreamWrapper extends ServletOutputStream {
        private ByteArrayOutputStream bos = null;

        public OutputStreamWrapper(ByteArrayOutputStream stream) throws IOException {
            bos = stream;
        }

        @Override
        public void write(int b) throws IOException {
            bos.write(b);
        }
        @Override
        public void write(byte[] b) throws IOException {
            bos.write(b, 0, b.length);
        }

        @Override
        public boolean isReady() {
            return true;
        }

        @Override
        public void setWriteListener(WriteListener writeListener) {

        }
    }
}
