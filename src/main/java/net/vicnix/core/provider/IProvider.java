package net.vicnix.core.provider;

import java.util.UUID;

interface IProvider {

    void addEmote(String emoteName, String format);

    void removeEmote(int rowId);

    String getEmote(String emoteName);

    int getEmoteId(String emoteName);

    void setPlayerEmote(String name, int emoteId);

    void setPlayerEmote(UUID uuid, int emoteId);
}