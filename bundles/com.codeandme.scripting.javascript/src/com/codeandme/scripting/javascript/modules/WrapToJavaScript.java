/*******************************************************************************
 * Copyright (c) 2011 Infineon Technologies Austria AG
 *
 * Contributors:
 *     Christian Pontesegger - initial version
 *     
 * Version Control:
 *     Last edited by: $Author: pontesegger $
 *     Date:           $Date: 2011-02-18 14:27:56 +0100 (Fr, 18 Feb 2011) $
 *     Revision:       $Revision: 121 $
 *     Head URL:       $URL: https://grzw2b4ph2j.eu.infineon.com/svn/Eclipse_RCP/trunk/bundles/com.infineon.javascript/src/com/infineon/javascript/modules/WrapToJavaScript.java $
 *******************************************************************************/

package com.codeandme.scripting.javascript.modules;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Annotation indicating that the annotated method should be wrapped to JavaScript. AbstractJavaScriptModule is a base class that performs auto wrapping for
 * methods annotated this way. Wrappers add JavaScript code that automatically calls the annotated Java method.
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface WrapToJavaScript {
    /** Delimiter for alias names. */
    String DELIMITER = ";";

    /**
     * Indicates that the method will be visible to online help. Defaults to <code>true</code>.
     */
    boolean visible() default true;

    /**
     * Defines alias names for the same command. Names are delimited by ";"
     */
    String alias() default "";
}
