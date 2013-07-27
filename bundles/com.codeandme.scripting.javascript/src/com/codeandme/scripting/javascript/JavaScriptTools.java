package com.codeandme.scripting.javascript;

import java.util.regex.Pattern;

public class JavaScriptTools {

    @Deprecated
    private JavaScriptTools() {
        throw new RuntimeException("Helper class provides static methods only");
    }

    public static String getSaveName(final String identifier) {
        // check if name is already valid
        if (Pattern.matches("[a-zA-Z_$][a-zA-Z0-9_$]*", identifier))
            return identifier;

        // not valid, convert string to valid format
        final StringBuffer buffer = new StringBuffer(identifier.replaceAll("[^a-zA-Z0-9]", "_"));

        // remove '_' at the beginning
        while ((buffer.length() > 0) && (buffer.charAt(0) == '_'))
            buffer.deleteCharAt(0);

        // remove trailing '_'
        while ((buffer.length() > 0) && (buffer.charAt(buffer.length() - 1) == '_'))
            buffer.deleteCharAt(buffer.length() - 1);

        // check for valid first character
        if (buffer.length() > 0) {
            final char start = buffer.charAt(0);
            if ((start < 65) || ((start > 90) && (start < 97)) || (start > 122))
                buffer.insert(0, '_');
        } else
            // buffer is empty
            buffer.insert(0, '_');

        return buffer.toString();
    }
}
