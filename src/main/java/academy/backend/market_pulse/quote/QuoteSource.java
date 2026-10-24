package academy.backend.market_pulse.quote;

import academy.backend.market_pulse.model.Quote;

import java.util.Map;
import java.util.Optional;


/**Источник котировок(сведения о цене тикеров)*/
public class QuoteSource {
    private final Map<String, Quote> data; /*таблица, где тикеру соответствует котировка*/

    public QuoteSource(Map<String, Quote> data) {
        this.data = data;
    }

    public Optional<Quote> fetch(String ticker) {
        return Optional.ofNullable(data.get(ticker.toUpperCase()));
    }
}