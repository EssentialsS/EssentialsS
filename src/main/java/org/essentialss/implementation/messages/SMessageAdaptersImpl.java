package org.essentialss.implementation.messages;

import org.essentialss.api.config.configs.MessageConfig;
import org.essentialss.api.message.MessageAdapters;
import org.essentialss.api.message.adapters.MessageAdapter;
import org.essentialss.api.message.adapters.player.command.PingMessageAdapter;
import org.essentialss.api.message.adapters.player.command.PlayerOnlyCommandMessageAdapter;
import org.essentialss.api.message.adapters.player.command.WhoIsMessageAdapter;
import org.essentialss.api.message.adapters.player.command.mute.MutedMessageAdapter;
import org.essentialss.api.message.adapters.player.command.mute.UnmutedMessageAdapter;
import org.essentialss.api.message.adapters.player.command.mute.YouAreNowMutedMessageAdapter;
import org.essentialss.api.message.adapters.player.command.mute.YouAreNowUnmutedMessageAdapter;
import org.essentialss.api.message.adapters.player.listener.afk.AwayFromKeyboardBarMessageAdapter;
import org.essentialss.api.message.adapters.player.listener.afk.AwayFromKeyboardForTooLongMessageAdapter;
import org.essentialss.api.message.adapters.player.listener.afk.AwayFromKeyboardMessageAdapter;
import org.essentialss.api.message.adapters.player.listener.afk.BackToKeyboardMessageAdapter;
import org.essentialss.api.message.adapters.player.listener.spy.CommandSpyMessageAdapter;
import org.essentialss.api.message.adapters.warp.CreateWarpMessageAdapter;
import org.essentialss.api.utils.Singleton;
import org.essentialss.implementation.messages.adapter.player.command.PingMessageAdapterImpl;
import org.essentialss.implementation.messages.adapter.player.command.PlayerOnlyCommandMessageAdapterImpl;
import org.essentialss.implementation.messages.adapter.player.command.WhoIsCommandManagerAdapterImpl;
import org.essentialss.implementation.messages.adapter.player.command.mute.MutedMessageAdapterImpl;
import org.essentialss.implementation.messages.adapter.player.command.mute.UnmutedMessageAdapterImpl;
import org.essentialss.implementation.messages.adapter.player.command.mute.YouAreNowMutedMessageAdapterImpl;
import org.essentialss.implementation.messages.adapter.player.command.mute.YouAreNowUnmutedMessageAdapterImpl;
import org.essentialss.implementation.messages.adapter.player.listener.afk.AwayFromKeyboardBarMessageAdapterImpl;
import org.essentialss.implementation.messages.adapter.player.listener.afk.AwayFromKeyboardForTooLongMessageAdapterImpl;
import org.essentialss.implementation.messages.adapter.player.listener.afk.AwayFromKeyboardMessageAdapterImpl;
import org.essentialss.implementation.messages.adapter.player.listener.afk.BackToKeyboardMessageAdapterImpl;
import org.essentialss.implementation.messages.adapter.player.listener.spy.CommandSpyMessageAdapterImpl;
import org.essentialss.implementation.messages.adapter.warp.CreateWarpMessageAdapterImpl;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.stream.Stream;

public class SMessageAdaptersImpl implements MessageAdapters {

    private final Singleton<PlayerOnlyCommandMessageAdapter> playerOnlyCommand;
    private final Singleton<WhoIsMessageAdapter> whoIs = new Singleton<>(WhoIsCommandManagerAdapterImpl::new);
    private final Singleton<AwayFromKeyboardForTooLongMessageAdapter> awayFromKeyboardForTooLong = new Singleton<>(
            AwayFromKeyboardForTooLongMessageAdapterImpl::new);
    private final Singleton<BackToKeyboardMessageAdapter> backToKeyboard = new Singleton<>(BackToKeyboardMessageAdapterImpl::new);
    private final Singleton<AwayFromKeyboardMessageAdapter> awayFromKeyboard = new Singleton<>(AwayFromKeyboardMessageAdapterImpl::new);
    private final Singleton<AwayFromKeyboardBarMessageAdapter> awayFromKeyboardBar = new Singleton<>(AwayFromKeyboardBarMessageAdapterImpl::new);
    private final Singleton<CreateWarpMessageAdapter> createWarp = new Singleton<>(CreateWarpMessageAdapterImpl::new);
    private final Singleton<MutedMessageAdapter> muted = new Singleton<>(MutedMessageAdapterImpl::new);
    private final Singleton<UnmutedMessageAdapter> unmuted = new Singleton<>(UnmutedMessageAdapterImpl::new);
    private final Singleton<YouAreNowMutedMessageAdapter> youAreNowMuted = new Singleton<>(YouAreNowMutedMessageAdapterImpl::new);
    private final Singleton<YouAreNowUnmutedMessageAdapter> youAreNowUnmuted = new Singleton<>(YouAreNowUnmutedMessageAdapterImpl::new);
    private final Singleton<PingMessageAdapter> ping = new Singleton<>(PingMessageAdapterImpl::new);
    private final Singleton<CommandSpyMessageAdapter> commandSpy = new Singleton<>(CommandSpyMessageAdapterImpl::new);

    private final @NotNull MessageConfig config;

    SMessageAdaptersImpl(@NotNull MessageConfig config) {
        this.config = config;
        this.playerOnlyCommand = new Singleton<>(() -> new PlayerOnlyCommandMessageAdapterImpl(this.config));
    }

    @Override
    public Stream<MessageAdapter> all() {
        return Arrays
                .stream(SMessageAdaptersImpl.class.getDeclaredFields())
                .filter(field -> Modifier.isPrivate(field.getModifiers()))
                .filter(field -> Modifier.isFinal(field.getModifiers()))
                .filter(field -> field.getType().isAssignableFrom(Singleton.class))
                .map(field -> {
                    try {
                        return (Singleton<MessageAdapter>) field.get(this);
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                })
                .map(Singleton::get);
    }

    @Override
    public Singleton<AwayFromKeyboardMessageAdapter> awayFromKeyboard() {
        return this.awayFromKeyboard;
    }

    @Override
    public Singleton<AwayFromKeyboardBarMessageAdapter> awayFromKeyboardBar() {
        return this.awayFromKeyboardBar;
    }

    @Override
    public Singleton<AwayFromKeyboardForTooLongMessageAdapter> awayFromKeyboardForTooLong() {
        return this.awayFromKeyboardForTooLong;
    }

    @Override
    public Singleton<BackToKeyboardMessageAdapter> backToKeyboard() {
        return this.backToKeyboard;
    }

    @Override
    public Singleton<CommandSpyMessageAdapter> commandSpy() {
        return this.commandSpy;
    }

    @Override
    public Singleton<CreateWarpMessageAdapter> createWarp() {
        return this.createWarp;
    }

    @Override
    public Singleton<MutedMessageAdapter> muted() {
        return this.muted;
    }

    @Override
    public Singleton<PingMessageAdapter> ping() {
        return this.ping;
    }

    @Override
    public Singleton<PlayerOnlyCommandMessageAdapter> playerOnlyCommand() {
        return this.playerOnlyCommand;
    }

    @Override
    public Singleton<UnmutedMessageAdapter> unmuted() {
        return this.unmuted;
    }

    @Override
    public Singleton<WhoIsMessageAdapter> whoIs() {
        return this.whoIs;
    }

    @Override
    public Singleton<YouAreNowMutedMessageAdapter> youAreNowMuted() {
        return this.youAreNowMuted;
    }

    @Override
    public Singleton<YouAreNowUnmutedMessageAdapter> youAreNowUnmuted() {
        return this.youAreNowUnmuted;
    }
}
