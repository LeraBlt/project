package academy.backend.market_pulse.filter;

import academy.backend.market_pulse.model.Instrument;

import java.util.Locale;
import java.util.Objects;

/** Стратегия отбора инструментов, тикер которых содержит искомую подстроку без учёта регистра. */
public final class TickerFilter implements InstrumentFilter {

    private final String query;

    public TickerFilter(String query) {
        if (query == null || query.isBlank()) {
            throw new IllegalArgumentException("Подстрока тикера не должна быть пустой");
        }
        this.query = query;
    }

    @Override
    public boolean matches(Instrument instrument) {
        return Objects.requireNonNull(instrument, "Инструмент не должен быть null").getTicker()
                .toLowerCase(Locale.ROOT)
                .contains(query.toLowerCase(Locale.ROOT));
    }
}
