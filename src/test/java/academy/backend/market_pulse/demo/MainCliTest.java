package academy.backend.market_pulse.demo;

import academy.backend.market_pulse.repository.InstrumentRepository;
import academy.backend.market_pulse.model.Currency;
import academy.backend.market_pulse.model.Stock;
import org.junit.jupiter.api.Test;
import picocli.CommandLine;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Проверяет клиент как пользователь: передаёт аргументы и читает код завершения и сообщения. */
class MainCliTest {

    @Test
    void printsHelpForRootAndSubcommand() {
        Result root = execute("--help");
        Result add = execute("add", "--help");

        assertEquals(0, root.exitCode());
        assertEquals(0, add.exitCode());
        assertTrue(root.output().contains("search"));
        assertTrue(root.output().contains("quote"));
        assertTrue(add.output().contains("<ticker>"));
    }

    @Test
    void reportsInvalidInputWithoutStackTrace() {
        Result result = execute("add", "STOCK", "AAPL", "Apple", "XYZ");

        assertEquals(2, result.exitCode());
        assertTrue(result.error().contains("Допустимые значения: RUB, USD, EUR"));
        assertFalse(result.error().contains("\tat "));
    }

    @Test
    void reportsCommandFailureWithoutStackTrace() {
        Result result = execute("add", "FUTURE", "XYZ", "Name", "USD");

        assertEquals(1, result.exitCode());
        assertTrue(result.error().contains("Неизвестный тип инструмента: FUTURE"));
        assertFalse(result.error().contains("\tat "));
    }

    @Test
    void reportsConflictingFiltersAndIncompletePrice() {
        Result conflict = execute("list", "--type", "STOCK", "--ticker", "AAP");
        Result incompletePrice = execute("list", "--price", "10");

        assertEquals(1, conflict.exitCode());
        assertEquals(1, incompletePrice.exitCode());
        assertTrue(conflict.error().contains("только один критерий"));
        assertTrue(incompletePrice.error().contains("--price-op, и --price"));
    }

    @Test
    void acceptsValidCommands() {
        assertEquals(0, execute("add", "STOCK", "AAPL", "Apple", "USD").exitCode());
        assertEquals(0, execute("list", "--type", "STOCK").exitCode());
    }

    @Test
    void searchReturnsSuccessOnlyWhenInstrumentIsFound() {
        InstrumentRepository repository = new InstrumentRepository();
        repository.add(new Stock("AAPL", "Apple", Currency.USD, "Technology", BigDecimal.ZERO));

        assertEquals(0, execute(repository, "search", "AAPL").exitCode());
        assertEquals(1, execute(repository, "search", "MSFT").exitCode());
    }

    @Test
    void quoteCommandShowsBothRequestedQuotes() {
        PrintStream originalOutput = System.out;
        ByteArrayOutputStream capturedOutput = new ByteArrayOutputStream();
        try {
            System.setOut(new PrintStream(capturedOutput, true, StandardCharsets.UTF_8));

            assertEquals(0, Main.execute(new String[]{"quote", "SBER", "GAZP"}));
            String output = capturedOutput.toString(StandardCharsets.UTF_8);
            assertTrue(output.contains("SBER: 300 RUB"));
            assertTrue(output.contains("GAZP: 150 RUB"));
        } finally {
            System.setOut(originalOutput);
        }
    }

    private Result execute(String... arguments) {
        return execute(new InstrumentRepository(), arguments);
    }

    private Result execute(InstrumentRepository repository, String... arguments) {
        // Создаём новый клиент для каждого сценария, чтобы аргументы одной команды не влияли на другую.
        CommandLine cli = Main.createCli(repository);
        StringWriter output = new StringWriter();
        StringWriter error = new StringWriter();
        PrintWriter outputWriter = new PrintWriter(output, true);
        PrintWriter errorWriter = new PrintWriter(error, true);
        cli.setOut(outputWriter);
        cli.setErr(errorWriter);
        cli.getSubcommands().values().forEach(subcommand -> {
            subcommand.setOut(outputWriter);
            subcommand.setErr(errorWriter);
        });
        int exitCode = cli.execute(arguments);
        return new Result(exitCode, output.toString(), error.toString());
    }

    private record Result(int exitCode, String output, String error) {
    }
}
