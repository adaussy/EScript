package com.codeandme.scripting;

import java.io.InputStream;

/**
 * @since 1.1
 */
public interface IScriptable {
    InputStream getSourceCode() throws Exception;
}
