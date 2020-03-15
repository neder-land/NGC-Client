package com.github.neder_land.gamecenter.client.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class IOUtils {

    private static final Class<?> ABSTRACT_STRING_BUILDER_CLASS;

    static {
        try {
            ABSTRACT_STRING_BUILDER_CLASS = Class.forName("java.lang.AbstractStringBuilder");
        } catch (ClassNotFoundException ex) {
            throw new InternalError("Could not find class java.lang.AbstractStringBuilder,is your runtime broken???", ex);
        }
    }

    public static byte[] readFully(InputStream is) throws IOException {
        byte[] data = new byte[128];
        ByteArrayOutputStream baos = new ByteArrayOutputStream(1024);
        while (is.read(data) != -1) {
            baos.write(data);
        }
        return baos.toByteArray();
    }

    public static <E extends Appendable> Exception appendLine(E appendable, CharSequence seq) {
        try {
            appendable.append(seq).append("\r\n");
        } catch (IOException e) {
            if (ABSTRACT_STRING_BUILDER_CLASS.isInstance(appendable))
                throw new UnsupportedOperationException("An exception was thrown whilst operating AbstractStringBuilder", e);
            return e;
        }
        return null;
    }
}
