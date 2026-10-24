package academy.backend.market_pulse.cli;

import java.util.concurrent.Callable;
import java.util.Optional;

import academy.backend.market_pulse.model.Instrument;
import academy.backend.market_pulse.repository.InstrumentRepository;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

/**
 * Ищет инструмент по тикеру в хранилище текущего запуска программы.
 * Код завершения: {@code 0}, если инструмент найден; {@code 1}, если его нет.
 */
@Command(name = "search", description = "Поиск инструмента по тикеру")
public class SearchCommand implements Callable<Integer> {

    @Option(names = {"-h", "--help"}, usageHelp = true, description = "Показать справку и завершить работу")
    private boolean helpRequested;

    @Parameters(index = "0", description = "Тикер инструмента")
    private String ticker;

    private final InstrumentRepository repository;

    public SearchCommand(InstrumentRepository repository) {
        this.repository = repository;
    }

    @Override
    public Integer call() {
        Optional<Instrument> found = repository.findByTicker(ticker);
        if (found.isEmpty()) {
            System.out.println("Инструмент не найден: " + ticker);
            return 1;
        }

        System.out.println("Инструмент найден: " + found.get());
        return 0;
    }
}
