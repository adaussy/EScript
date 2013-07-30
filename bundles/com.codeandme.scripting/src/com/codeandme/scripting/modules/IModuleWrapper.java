package com.codeandme.scripting.modules;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Set;

public interface IModuleWrapper {

    String getSaveVariableName(String variableName);

    String createFunctionWrapper(String moduleVariable, Method method, Set<String> functionNames, String resultName, String preExecutionCode,
            String postExecutionCode);

    String getConstantDefinition(String name, Field field);

    String getEnvironmentModuleName();

    String getVariableDefinition(String name, String content);

    String classInstantiation(Class<?> clazz, String[] parameters);
}
