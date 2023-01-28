package io.github.apricotfarmer11.mods.tubion.core.tubnet;

import io.github.apricotfarmer11.mods.tubion.core.helper.PlayerHelper;
import io.github.apricotfarmer11.mods.tubion.event.ChatMessageEvent;
import net.minecraft.util.ActionResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TubnetParty {
    private static final Logger LOGGER = LoggerFactory.getLogger("Tubion/Party");
    public TubnetPlayer partyOwner;
    public ArrayList<TubnetPlayer> members = new ArrayList<>();

    // regex
    private static final Pattern PARTY_JOINED_MSG_MATCHER = Pattern.compile("([a-zA-Z0-9_]{2,16}) has joined the party!");
    private static final Pattern PARTY_LEFT_MSG_MATCHER = Pattern.compile("([a-zA-Z0-9_]{2,16}) left the party\\.");
    public boolean deleted = false;
    public UUID partyId;
    public UUID partyIdSecret;
    public TubnetParty(TubnetPlayer owner, @Nullable UUID partyId, @Nullable UUID partyIdSecret) {
        this.partyId = partyId;
        this.partyIdSecret = partyIdSecret;
        if (this.partyId == null) this.partyId = UUID.randomUUID();
        if (this.partyIdSecret == null) this.partyIdSecret = UUID.randomUUID();
        partyOwner = owner;
        members.add(partyOwner);
        ChatMessageEvent.EVENT.register((msg) -> {
            if (deleted) return ActionResult.PASS;
            if (msg.getString().contains("Your party was disbanded.")) {
                deleted = true;
                return ActionResult.PASS;
            }

            Matcher partyJoinTest = PARTY_JOINED_MSG_MATCHER.matcher(msg.getString());
            if (partyJoinTest.find()) {
                String user = partyJoinTest.group(1);
                members.add(new TubnetPlayer(user, null));
                LOGGER.info(user + " has joined the party");
            } else {
                Matcher partyLeaveTest = PARTY_LEFT_MSG_MATCHER.matcher(msg.getString());
                if (partyLeaveTest.find()) {
                    String user = partyLeaveTest.group(1);
                    members.forEach((player) -> {
                        if (player.username == user) {
                            members.remove(player);
                        }
                    });
                    LOGGER.info(user + "has left the party");
                }
            }

            return ActionResult.PASS;
        });
    }
    public void invitePlayer(String name) {
        if (this.deleted) return;
        for (Iterator<TubnetPlayer> it = this.members.iterator(); it.hasNext(); ) {
            TubnetPlayer member = it.next();
            if (member.username == name) {
                return;
            }
        }

        PlayerHelper.sendCommand("party invite" + name);
    }
}
