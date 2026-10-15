package academy.backend.market_pulse.filter;

import academy.backend.market_pulse.model.Instrument;

/** Стратегия без отбора: подходит любой финансовый инструмент. */
public final class AllInstrumentsFilter implements InstrumentFilter {

    public static final AllInstrumentsFilter INSTANCE = new AllInstrumentsFilter();

    private AllInstrumentsFilter() {
    }

    @Override
    public boolean matches(Instrument instrument) {
        return true;
    }
}
