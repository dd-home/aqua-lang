package com.imkiva.aqua.tree.concrete.utils;

import com.imkiva.aqua.tree.common.DBIConstants;
import com.imkiva.aqua.tree.common.Name;
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

    public Name resolve(Name name) {
        if (name.isResolved()) {
            return name;
        }

        if (name.isUnknown()) {
            name.setNameDBI(DBIConstants.UNKNOWN);
            return name;
        }

        name.setNameDBI(resolveIndex(name.name));
        return name;
    }

    private int resolveIndex(String name) {
        // search it in current name pairs
        int index = searchName(name);

        // if the name exists in current scope,
        // just return it
        if (index >= 0) {
            return index;
        }

        // otherwise search in parent scope
        if (parent != null) {
            int parentIndex = parent.searchName(name);

            // if found in parent, we offset it by
            // adding current scope size
            if (parentIndex >= 0) {
                return parentIndex + namePairs.size();
            }
        }

        // otherwise, the name is free
        return DBIConstants.FREE;
    }

    private int searchName(String name) {
        int i = 0;
        for (NamePair np : namePairs) {
            if (np.name.name.equals(name)) {
                return i;
            }
            ++i;
        }
        return -1;
    }
}
