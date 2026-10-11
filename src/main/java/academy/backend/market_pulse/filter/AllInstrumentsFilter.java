package academy.backend.market_pulse.filter;

import academy.backend.market_pulse.model.Instrument;

import java.util.Objects;

/** Стратегия без отбора: подходит любой финансовый инструмент. */
public final class AllInstrumentsFilter implements InstrumentFilter {

    public static final AllInstrumentsFilter INSTANCE = new AllInstrumentsFilter();

    private AllInstrumentsFilter() {
    }

    @Override
    public boolean matches(Instrument instrument) {
        Objects.requireNonNull(instrument, "Инструмент не должен быть null");
        return true;
    }
}
