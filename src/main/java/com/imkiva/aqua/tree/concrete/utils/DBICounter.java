package com.imkiva.aqua.tree.concrete.utils;

import com.imkiva.aqua.tree.common.NamePair;

import java.util.ArrayList;
import java.util.List;

public final class DBICounter {
    private DBICounter parent;
    private List<NamePair> namePairs;

    public DBICounter(DBICounter parent) {
        this.parent = parent;
        this.namePairs = new ArrayList<>(4);
    }

    public void addName(NamePair pair) {
        namePairs.add(pair);
    }
}
