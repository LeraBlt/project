package academy.backend.market_pulse.demo;

import academy.backend.market_pulse.cli.AddCommand;
import academy.backend.market_pulse.cli.CliExceptionHandler;
import academy.backend.market_pulse.cli.ListCommand;
import academy.backend.market_pulse.cli.MarketPulseCli;
import academy.backend.market_pulse.cli.SearchCommand;
import academy.backend.market_pulse.repository.InstrumentRepository;
import picocli.CommandLine;

/**
 * Точка входа консольного клиента. Создаёт одно хранилище и передаёт его всем командам.
 * Так команды работают с одними данными в пределах одного запуска программы.
 */
public class Main {

    public static void main(String[] args) {
        int exitCode = createCli(new InstrumentRepository()).execute(args);
        System.exit(exitCode);
    }

    /** Собирает команды и обработчики ошибок; тесты вызывают этот метод без {@code System.exit}. */
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
