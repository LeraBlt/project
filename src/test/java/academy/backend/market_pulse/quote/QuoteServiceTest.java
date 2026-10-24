package academy.backend.market_pulse.quote;

import academy.backend.market_pulse.model.Currency;
import academy.backend.market_pulse.model.Quote;
import academy.backend.market_pulse.model.Stock;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class QuoteServiceTest {

    @Test
    void processesRequestsInOrderAndFetchesCachedTickerOnlyOnce() {
        Quote sber = quote("SBER");
        Quote gazp = quote("GAZP");
        RecordingSource source = new RecordingSource(Map.of("SBER", sber, "GAZP", gazp));
        QuoteService service = new QuoteService(source);

        service.request("sber");
        service.request("GAZP");
        service.request("SBER");
        service.processAll();

        assertEquals(List.of("SBER", "GAZP"), source.requestedTickers);
        assertSame(sber, service.cached("sber").orElseThrow());
        assertSame(gazp, service.cached("gazp").orElseThrow());

        service.request("SBER");
        service.processAll();
        assertEquals(List.of("SBER", "GAZP"), source.requestedTickers);
    }

    @Test
    void returnsEmptyWhenSourceHasNoQuote() {
        QuoteService service = new QuoteService(new QuoteSource(Map.of()));

        service.request("UNKNOWN");
        service.processAll();

        assertTrue(service.cached("UNKNOWN").isEmpty());
    }

    private static Quote quote(String ticker) {
        Stock stock = new Stock(ticker, ticker, Currency.RUB, "Пример", BigDecimal.ZERO);
        return new Quote(stock, BigDecimal.TEN, BigDecimal.ZERO);
    }

    /** Запоминает порядок обращений к источнику. */
    private static class RecordingSource extends QuoteSource {
        private final List<String> requestedTickers = new ArrayList<>();

        private RecordingSource(Map<String, Quote> data) {
            super(data);
        }

        @Override
        public Optional<Quote> fetch(String ticker) {
            requestedTickers.add(ticker);
            return super.fetch(ticker);
        }
    }
}
