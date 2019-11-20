package me.randomhashtags.randomsky.event;

import me.randomhashtags.randomsky.api.SkyKitFund;
import org.bukkit.entity.Player;

import java.math.BigDecimal;

public class FundDepositEvent extends RSEventCancellable {
    private static final SkyKitFund fund = SkyKitFund.getFund();
    public BigDecimal amount;
    private final BigDecimal total;
    public FundDepositEvent(Player player, BigDecimal amount) {
        super(player);
        this.amount = amount;
        total = fund.total;
    }
    public BigDecimal getFundTotal() { return total; }
    public BigDecimal getNewFundTotal() { return total.add(amount); }
}
