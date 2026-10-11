package academy.backend.market_pulse.filter;

import academy.backend.market_pulse.model.Currency;
import academy.backend.market_pulse.model.Instrument;

import java.util.Objects;

/** Стратегия отбора инструментов с точным совпадением валюты. */
public final class CurrencyFilter implements InstrumentFilter {

    private final Currency currency;

    public CurrencyFilter(Currency currency) {
        this.currency = Objects.requireNonNull(currency, "Валюта не должна быть null");
    }

    @Override
    public boolean matches(Instrument instrument) {
        return Objects.requireNonNull(instrument, "Инструмент не должен быть null").getCurrency() == currency;
    }
}
