package academy.backend.market_pulse.cli;

import java.util.concurrent.Callable;

import academy.backend.market_pulse.factory.InstrumentFactories;
import academy.backend.market_pulse.model.Currency;
import academy.backend.market_pulse.model.Instrument;
import academy.backend.market_pulse.repository.InstrumentRepository;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

/** Добавляет один инструмент в хранилище текущего запуска программы. */
@Command(name = "add", description = "Добавление инструмента в репозиторий")
public class AddCommand implements Callable<Integer> {

    @Option(names = {"-h", "--help"}, usageHelp = true, description = "Показать справку и завершить работу")
    private boolean helpRequested;

    @Parameters(index = "0", description = "Тип инструмента (STOCK, BOND, ETF)")
    private String type;

    @Parameters(index = "1", description = "Тикер инструмента")
    private String ticker;

    @Parameters(index = "2", description = "Название инструмента")
    private String name;

    @Parameters(index = "3", description = "Валюта инструмента (RUB, USD, EUR)")
    private Currency currency;

    private final InstrumentRepository repository;

    public AddCommand(InstrumentRepository repository) {
        this.repository = repository;
    }

    @Override
    public Integer call() {
        Instrument instrument;
        try {
            instrument = InstrumentFactories.create(type, ticker, name, currency);
        } catch (IllegalArgumentException exception) {
            // Ошибку создания переводим в тип, который общий обработчик покажет без стека вызовов.
            throw new CliException(exception.getMessage(), exception);
        }
        repository.add(instrument);
        System.out.println("Добавлено: " + instrument.getDescription());
        return 0;
    }
}
