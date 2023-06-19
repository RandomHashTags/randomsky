package me.randomhashtags.randomsky.supported;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.randomhashtags.randomsky.RSPlayer;
import me.randomhashtags.randomsky.addon.PlayerRank;
import me.randomhashtags.randomsky.addon.alliance.Alliance;
import me.randomhashtags.randomsky.universal.UVersionable;
import org.bukkit.OfflinePlayer;

import java.util.UUID;

public class Placeholders extends PlaceholderExpansion implements UVersionable {

    @Override
    public String getAuthor() {
        return "RandomHashTags";
    }

    @Override
    public String getIdentifier() {
        return "randomsky";
    }

    @Override
    public String getVersion() {
        return "2.0.0";
    }

    @Override
    public String onRequest(OfflinePlayer player, String identifier) {
        final UUID u = player != null ? player.getUniqueId() : null;
        final RSPlayer pdata = u != null ? RSPlayer.get(u) : null;
        if(pdata == null) {
            return "";
        } else if(identifier.equals("ah_listings")) {
            return Integer.toString(pdata.auctions.size());

        } else if(identifier.startsWith("alliance_")) {
            String r = "";
            final Alliance a = Alliance.players.getOrDefault(u, null);
            if(a != null) {
                if(identifier.endsWith("_tag")) {
                    r = a.getTag();
                } else if(identifier.endsWith("_role")) {
                    r = a.getMember(player).role.chatTag;
                }
            }
            return r;

        } else if(identifier.equals("coinflip_wins")) {
            return formatBigDecimal(pdata.getCoinFlipStats().wins);
        } else if(identifier.equals("coinflip_won$")) {
            return formatBigDecimal(pdata.getCoinFlipStats().cash_won, true);
        } else if(identifier.equals("coinflip_losses")) {
            return formatBigDecimal(pdata.getCoinFlipStats().losses);
        } else if(identifier.equals("coinflip_lost$")) {
            return formatBigDecimal(pdata.getCoinFlipStats().cash_lost);
        } else if(identifier.equals("coinflip_notifications")) {
            return Boolean.toString(pdata.getCoinFlipStats().receives_notifications);

        } else if(identifier.equals("jackpot_countdown")) {
            return Boolean.toString(pdata.jackpotCountdown);
        } else if(identifier.equals("jackpot_wins")) {
            return formatBigDecimal(pdata.getJackpotStats().getTimesWon());
        } else if(identifier.equals("jackpot_won$")) {
            return formatBigDecimal(pdata.getJackpotStats().getWonCash());
        } else if(identifier.equals("jackpot_tickets")) {
            return formatBigDecimal(pdata.getJackpotStats().getTicketsPurchased());

        } else if(identifier.equals("rank")) {
            final PlayerRank rank = pdata.getRank();
            return rank != null ? rank.getAppearance() : "";
        }
        return null;
    }
}