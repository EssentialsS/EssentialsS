package org.essentialss.group;

import org.essentialss.api.group.Group;

public class GroupImpl implements Group {

    private final String name;

    public GroupImpl(String name) {
        this.name = name;
    }

    @Override
    public String groupName() {
        return this.name;
    }
}
