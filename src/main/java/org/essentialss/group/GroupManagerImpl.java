package org.essentialss.group;

import org.essentialss.EssentialsSMain;
import org.essentialss.api.group.Group;
import org.essentialss.api.group.GroupManager;
import org.essentialss.api.utils.Singleton;
import org.essentialss.api.utils.arrays.UnmodifiableCollection;
import org.essentialss.api.utils.arrays.impl.SingleUnmodifiableCollection;
import org.spongepowered.api.service.permission.Subject;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.concurrent.LinkedTransferQueue;
import java.util.stream.Collectors;

public class GroupManagerImpl implements GroupManager {

    private static final Singleton<Group> DEFAULT_GROUP = new Singleton<>(() -> new GroupImpl("default"));

    private final Collection<Group> groups = new LinkedTransferQueue<>();
    private final Collection<String> displayedWarnings = new LinkedHashSet<>();

    @Override
    public Group defaultGroup() {
        return DEFAULT_GROUP.get();
    }

    @Override
    public Group group(Subject subject) {
        List<Group> found = this.groups.stream().filter(group -> subject.hasPermission("essentials.group." + group.groupName())).collect(Collectors.toList());
        if (found.isEmpty()) {
            return this.defaultGroup();
        }
        if ((found.size() > 1) && !this.displayedWarnings.contains(subject.identifier())) {
            EssentialsSMain
                    .plugin()
                    .logger()
                    .warn(subject.friendlyIdentifier().orElse(subject.identifier()) + " is in multi groups. Using first: " + found
                            .stream()
                            .map(Group::groupName)
                            .collect(Collectors.joining(", ")));
            this.displayedWarnings.add(subject.identifier());
        }
        return found.get(0);
    }

    @Override
    public UnmodifiableCollection<Group> groups() {
        Collection<Group> groups = new HashSet<>(this.groups);
        groups.add(this.defaultGroup());
        return new SingleUnmodifiableCollection<>(groups);
    }

    @Override
    public Group register(String groupName) {
        if (this.group(groupName).isPresent()) {
            throw new IllegalArgumentException("group of " + groupName + " is already registered");
        }
        Group group = new GroupImpl(groupName);
        this.groups.add(group);
        return group;
    }

    @Override
    public void unregister(String groupName) {
        this.group(groupName).ifPresent(this.groups::remove);
    }
}
