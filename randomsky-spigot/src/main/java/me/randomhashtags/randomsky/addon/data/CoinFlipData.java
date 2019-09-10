package me.randomhashtags.randomsky.addon.data;

import java.math.BigDecimal;

public interface CoinFlipData {
    BigDecimal getTotalWins();
    void setTotalWins(BigDecimal wins);

    BigDecimal getTotalLosses();
    void setTotalLosses(BigDecimal losses);

    BigDecimal getTotalWonCash();
    void setTotalWonCash(BigDecimal cash);

    BigDecimal getTotalLostCash();
    void setTotalLostCash(BigDecimal cash);

    default void completed(BigDecimal wager, double taxPercent, boolean didWin) {
        final BigDecimal value = wager.multiply(BigDecimal.valueOf(didWin ? taxPercent/100 : 0.5));
        if(didWin) {
            setTotalWins(getTotalWins().add(BigDecimal.ONE));
            setTotalWonCash(getTotalWonCash().add(value));
        } else {
            setTotalLosses(getTotalLosses().add(BigDecimal.ONE));
            setTotalLostCash(getTotalLostCash().add(value));
        }
    }
}
