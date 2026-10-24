package academy.backend.market_pulse.cli;

import academy.backend.market_pulse.model.Quote;
import academy.backend.market_pulse.quote.QuoteService;

import java.util.Optional;

/** Принимает тикеры из консоли и показывает найденные котировки. */
public class QuoteCommand implements Runnable {

    private final QuoteService service; /*ставит тикеры в очередь, получает котировки и хранит их в кеше*/
    private final String[] tickers; /* массив строк, которые ввёл пользователь.*/

    public QuoteCommand(QuoteService service, String[] tickers) {
        this.service = service;
        this.tickers = tickers;
    }

    @Override
    public void run() {
        /* Собирает все запросы в очередь*/
        for (String ticker : tickers) {
            service.request(ticker);
        }

        service.processAll(); /*забирает тикеры из очереди по порядку и сохраняет найденные котировки в кеш*/

        /* Проходим по исходному массиву tickers выводим результаты из кеша*/
        for (String ticker : tickers) {
            Optional<Quote> quote = service.cached(ticker);
            if (quote.isPresent()) {
                System.out.println(quote.get());
            } else {
                System.out.println("Котировка не найдена: " + ticker);
            }
        }
    }
}
