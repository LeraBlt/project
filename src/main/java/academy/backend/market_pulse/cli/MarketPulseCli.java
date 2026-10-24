package academy.backend.market_pulse.cli;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

/** Корневая команда: показывает пользователю, как открыть список доступных команд. */
@Command(name = "market-pulse", description = "Котировки: quote TICKER [TICKER ...]")
public class MarketPulseCli implements Runnable {

    @Option(names = {"-h", "--help"}, usageHelp = true, description = "Показать справку и завершить работу")
    private boolean helpRequested;

    @Override
    public void run() {
        System.out.println("Используйте --help для списка команд. Котировки: quote SBER GAZP");
    }
}
