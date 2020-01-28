package com.imkiva.aqua.tree.concrete;

import com.imkiva.aqua.tree.common.Name;
import com.imkiva.aqua.tree.common.NamePair;
import com.imkiva.aqua.tree.common.Type;

import java.util.List;

public class FunctionDefStatement extends DefinitionStatement {
    public List<NamePair> teles;
    public Type functionType;
    public Name functionName;
}
