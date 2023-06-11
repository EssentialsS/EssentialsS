package org.essentialss.messages;

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
import org.essentialss.api.message.adapters.player.listener.chat.ChatMessageAdapter;
import org.essentialss.api.message.adapters.player.listener.spy.CommandSpyMessageAdapter;
import org.essentialss.api.message.adapters.vanilla.player.PlayerJoinMessageAdapter;
import org.essentialss.api.message.adapters.warp.CreateWarpMessageAdapter;
import org.essentialss.api.message.adapters.world.NoWorldByThatKeyMessageAdapter;
import org.essentialss.api.message.adapters.world.WorldHasAlreadyLoadedMessageAdapter;
import org.essentialss.api.message.adapters.world.create.CreatedWorldMessageAdapter;
import org.essentialss.api.message.adapters.world.create.CreatingWorldMessageAdapter;
import org.essentialss.api.message.adapters.world.load.LoadedWorldMessageAdapter;
import org.essentialss.api.message.adapters.world.load.LoadingWorldMessageAdapter;
import org.essentialss.api.message.adapters.world.unload.UnloadedWorldMessageAdapter;
import org.essentialss.api.message.adapters.world.unload.UnloadingWorldMessageAdapter;
import org.essentialss.api.utils.Singleton;
import org.essentialss.messages.adapter.player.command.PingMessageAdapterImpl;
import org.essentialss.messages.adapter.player.command.PlayerOnlyCommandMessageAdapterImpl;
import org.essentialss.messages.adapter.player.command.WhoIsCommandManagerAdapterImpl;
import org.essentialss.messages.adapter.player.command.mute.MutedMessageAdapterImpl;
import org.essentialss.messages.adapter.player.command.mute.UnmutedMessageAdapterImpl;
import org.essentialss.messages.adapter.player.command.mute.YouAreNowMutedMessageAdapterImpl;
import org.essentialss.messages.adapter.player.command.mute.YouAreNowUnmutedMessageAdapterImpl;
import org.essentialss.messages.adapter.player.listener.afk.AwayFromKeyboardBarMessageAdapterImpl;
import org.essentialss.messages.adapter.player.listener.afk.AwayFromKeyboardForTooLongMessageAdapterImpl;
import org.essentialss.messages.adapter.player.listener.afk.AwayFromKeyboardMessageAdapterImpl;
import org.essentialss.messages.adapter.player.listener.afk.BackToKeyboardMessageAdapterImpl;
import org.essentialss.messages.adapter.player.listener.chat.SChatMessageAdapterImpl;
import org.essentialss.messages.adapter.player.listener.spy.CommandSpyMessageAdapterImpl;
import org.essentialss.messages.adapter.vanilla.player.PlayerJoinMessageAdapterImpl;
import org.essentialss.messages.adapter.warp.CreateWarpMessageAdapterImpl;
import org.essentialss.messages.adapter.world.NoWorldByThatKeyMessageAdapterImpl;
import org.essentialss.messages.adapter.world.WorldHasAlreadyLoadedMessageAdapterImpl;
import org.essentialss.messages.adapter.world.create.CreatedWorldAdapterImpl;
import org.essentialss.messages.adapter.world.create.CreatingWorldAdapterImpl;
import org.essentialss.messages.adapter.world.load.LoadedWorldAdapterImpl;
import org.essentialss.messages.adapter.world.load.LoadingWorldAdapterImpl;
import org.essentialss.messages.adapter.world.unload.UnloadedWorldAdapterImpl;
import org.essentialss.messages.adapter.world.unload.UnloadingWorldAdapterImpl;
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
    @Deprecated
    private final Singleton<ChatMessageAdapter> chat = new Singleton<>(SChatMessageAdapterImpl::new);
    private final Singleton<PlayerJoinMessageAdapter> playerJoin = new Singleton<>(PlayerJoinMessageAdapterImpl::new);
    private final Singleton<CreatedWorldMessageAdapter> createdWorld = new Singleton<>(CreatedWorldAdapterImpl::new);
    private final Singleton<CreatingWorldMessageAdapter> creatingWorld = new Singleton<>(CreatingWorldAdapterImpl::new);
    private final Singleton<LoadingWorldMessageAdapter> loadingWorld = new Singleton<>(LoadingWorldAdapterImpl::new);
    private final Singleton<LoadedWorldMessageAdapter> loadedWorld = new Singleton<>(LoadedWorldAdapterImpl::new);
    private final Singleton<UnloadingWorldMessageAdapter> unloadingWorld = new Singleton<>(UnloadingWorldAdapterImpl::new);
    private final Singleton<UnloadedWorldMessageAdapter> unloadedWorld = new Singleton<>(UnloadedWorldAdapterImpl::new);
    private final Singleton<NoWorldByThatKeyMessageAdapter> noWorldByThatKey = new Singleton<>(NoWorldByThatKeyMessageAdapterImpl::new);
    private final Singleton<WorldHasAlreadyLoadedMessageAdapter> worldHasAlreadyLoaded = new Singleton<>(WorldHasAlreadyLoadedMessageAdapterImpl::new);


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
    public Singleton<ChatMessageAdapter> chat() {
        return this.chat;
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
    public Singleton<CreatedWorldMessageAdapter> createdWorld() {
        return this.createdWorld;
    }

    @Override
    public Singleton<CreatingWorldMessageAdapter> creatingWorld() {
        return this.creatingWorld;
    }

    @Override
    public Singleton<LoadedWorldMessageAdapter> loadedWorld() {
        return this.loadedWorld;
    }

    @Override
    public Singleton<LoadingWorldMessageAdapter> loadingWorld() {
        return this.loadingWorld;
    }

    @Override
    public Singleton<MutedMessageAdapter> muted() {
        return this.muted;
    }

    @Override
    public Singleton<NoWorldByThatKeyMessageAdapter> noWorldByThatKey() {
        return this.noWorldByThatKey;
    }

    @Override
    public Singleton<PingMessageAdapter> ping() {
        return this.ping;
    }

    @Override
    public Singleton<PlayerJoinMessageAdapter> playerJoin() {
        return this.playerJoin;
    }

    @Override
    public Singleton<PlayerOnlyCommandMessageAdapter> playerOnlyCommand() {
        return this.playerOnlyCommand;
    }

    @Override
    public Singleton<UnloadedWorldMessageAdapter> unloadWorld() {
        return this.unloadedWorld;
    }

    @Override
    public Singleton<UnloadingWorldMessageAdapter> unloadingWorld() {
        return this.unloadingWorld;
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
    public Singleton<WorldHasAlreadyLoadedMessageAdapter> worldHasAlreadyLoaded() {
        return this.worldHasAlreadyLoaded;
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
