package academy.backend.market_pulse.demo;

import academy.backend.market_pulse.cli.AddCommand;
import academy.backend.market_pulse.cli.CliExceptionHandler;
import academy.backend.market_pulse.cli.ListCommand;
import academy.backend.market_pulse.cli.MarketPulseCli;
import academy.backend.market_pulse.cli.QuoteCommand;
import academy.backend.market_pulse.cli.SearchCommand;
import academy.backend.market_pulse.model.Currency;
import academy.backend.market_pulse.model.Quote;
import academy.backend.market_pulse.model.Stock;
import academy.backend.market_pulse.quote.QuoteService;
import academy.backend.market_pulse.quote.QuoteSource;
import academy.backend.market_pulse.repository.InstrumentRepository;
import picocli.CommandLine;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Map;

/**
 * Точка входа консольного клиента.
 * Команду quote разбираем напрямую; остальные команды обрабатывает Picocli.
 */
public class Main {

    public static void main(String[] args) {
        int exitCode = execute(args);
        System.exit(exitCode);
    }

    /** Запускает команду, введённую пользователем. */
    static int execute(String[] args) {
        if (args.length > 0 && "quote".equals(args[0])) {
            if (args.length == 1) {
                System.err.println("Ошибка ввода: укажите хотя бы один тикер. Пример: quote SBER GAZP");
                return 2;
            }
            if (args.length == 2 && ("--help".equals(args[1]) || "-h".equals(args[1]))) {
                System.out.println("Использование: quote TICKER [TICKER ...]");
                return 0;
            }

            // Первое слово — имя команды; все остальные слова — запрошенные тикеры.
            String[] tickers = Arrays.copyOfRange(args, 1, args.length);
            QuoteService service = new QuoteService(createQuoteSource());
            new QuoteCommand(service, tickers).run();
            return 0;
        }

        return createCli(new InstrumentRepository()).execute(args);
    }

    /** Заглушка*/
    private static QuoteSource createQuoteSource() {
        Stock sber = new Stock("SBER", "Сбербанк", Currency.RUB, "Банки", BigDecimal.ZERO);
        Stock gazp = new Stock("GAZP", "Газпром", Currency.RUB, "Энергетика", BigDecimal.ZERO);
        Map<String, Quote> quotes = Map.of(
                "SBER", new Quote(sber, new BigDecimal("300"), new BigDecimal("1.5")),
                "GAZP", new Quote(gazp, new BigDecimal("150"), new BigDecimal("-0.8"))
        );
        return new QuoteSource(quotes);
    }

    /** Собирает команды Picocli и обработчики ошибок; тесты вызывают метод без {@code System.exit}. */
    static CommandLine createCli(InstrumentRepository repository) {
        CommandLine cli = new CommandLine(new MarketPulseCli())
                .addSubcommand(new SearchCommand(repository))
                .addSubcommand(new AddCommand(repository))
                .addSubcommand(new ListCommand(repository));

        // Picocli отдельно сообщает об ошибках ввода и об ошибках во время выполнения команды.
        CliExceptionHandler exceptionHandler = new CliExceptionHandler();
        cli.setParameterExceptionHandler(exceptionHandler);
        cli.setExecutionExceptionHandler(exceptionHandler);
        return cli;
    }
}
