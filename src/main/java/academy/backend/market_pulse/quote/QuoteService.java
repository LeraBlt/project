package academy.backend.market_pulse.quote;

import academy.backend.market_pulse.model.Quote;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Queue;


/**  Организатор запросов*/

public class QuoteService {
    private final QuoteSource source;
    private final Queue<String> pending = new ArrayDeque<>(); /*очередь тикеров, которые ещё нужно обработать*/
    private final Map<String, Quote> cache = new HashMap<>(); /*котировки, которые уже получили*/

    public QuoteService(QuoteSource source) {
        this.source = source;
    }

    /* Положить тикер вв очередь*/
    public void request(String ticker) {
        pending.offer(ticker.toUpperCase());
    }

    /* взять тикер из очереди, спросить QuoteSource и сохранить результат в кеш*/
    public void processAll() {
        while (!pending.isEmpty()) {
            String ticker = pending.poll();
            if (cache.containsKey(ticker)) {
                continue;
            }
            source.fetch(ticker).ifPresent(quote -> cache.put(ticker, quote));
        }
    }

    /* Вернуть сохранённую котировку*/
    public Optional<Quote> cached(String ticker) {
        return Optional.ofNullable(cache.get(ticker.toUpperCase()));
    }
}