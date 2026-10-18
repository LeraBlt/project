package academy.backend.market_pulse.cli;

import picocli.CommandLine;
import picocli.CommandLine.MissingParameterException;
import picocli.CommandLine.ParameterException;
import picocli.CommandLine.UnmatchedArgumentException;
import picocli.CommandLine.Model.ArgSpec;
import picocli.CommandLine.Model.OptionSpec;

import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.List;
import java.util.StringJoiner;

/** Превращает исключения Picocli в понятные сообщения для пользователя консоли. */
public final class CliExceptionHandler implements CommandLine.IParameterExceptionHandler,
        CommandLine.IExecutionExceptionHandler {

    // Код завершения 1: команда запустилась, но выполнить её не удалось.
    private static final int COMMAND_ERROR = 1;
    // Код завершения 2: ошибка в написании команды или её аргументов.
    private static final int INVALID_INPUT = 2;

    /** Picocli вызывает этот метод, если не смог разобрать введённую команду. */
    @Override
    public int handleParseException(ParameterException exception, String[] arguments) {
        CommandLine commandLine = exception.getCommandLine();
        // Ошибки пишем в поток ошибок, а не в обычный вывод программы.
        PrintWriter error = commandLine.getErr();
        error.println("Ошибка ввода: " + describeInputError(exception));
        // При ошибке в параметрах показываем синтаксис именно той команды, которую вызвали.
        commandLine.usage(error);
        // Отправляем накопленный текст в консоль до завершения программы.
        error.flush();
        return INVALID_INPUT;
    }

    /** Picocli вызывает этот метод, если команда запустилась и выбросила исключение. */
    @Override
    public int handleExecutionException(Exception exception, CommandLine commandLine,
                                        CommandLine.ParseResult parseResult) {
        PrintWriter error = commandLine.getErr();
        // CliException содержит сообщение, предназначенное для пользователя.
        if (exception instanceof CliException) {
            error.println("Ошибка: " + exception.getMessage());
        } else {
            // Неожиданные ошибки не раскрываем пользователю через технический стек вызовов.
            error.println("Не удалось выполнить команду из-за внутренней ошибки.");
        }
        error.flush();
        return COMMAND_ERROR;
    }

    /** Выбирает понятное сообщение по виду ошибки, которую обнаружил Picocli. */
    private String describeInputError(ParameterException exception) {
        // Пользователь не передал обязательный аргумент или значение параметра.
        if (exception instanceof MissingParameterException missingParameter) {
            return describeMissingParameter(missingParameter);
        }
        // В команде есть слово или параметр, которого Picocli не ожидает.
        if (exception instanceof UnmatchedArgumentException unknownArgument) {
            return describeUnknownArgument(unknownArgument);
        }
        // Ошибка относится к конкретному аргументу, например введено слово вместо числа.
        ArgSpec incorrectArgument = exception.getArgSpec();
        if (incorrectArgument != null) {
            return describeInvalidValue(incorrectArgument, exception.getValue());
        }
        // Picocli сообщил об ошибке, но не указал её конкретный аргумент.
        return "проверьте параметры команды.";
    }

    /** Перечисляет аргументы, которые пользователь забыл указать. */
    private String describeMissingParameter(MissingParameterException exception) {
        // Picocli передаёт список описаний отсутствующих аргументов.
        List<ArgSpec> missing = exception.getMissing();
        // Соединяем их имена через запятую для одного сообщения.
        StringJoiner names = new StringJoiner(", ");
        for (ArgSpec argument : missing) {
            names.add(argumentName(argument));
        }
        // Для одной отсутствующей опции используем отдельную формулировку.
        if (missing.size() == 1 && missing.getFirst() instanceof OptionSpec) {
            return "не указано значение для параметра " + names + ".";
        }
        // Для обязательных аргументов выбираем форму по их количеству.
        String prefix = missing.size() == 1
                ? "не указан обязательный аргумент: "
                : "не указаны обязательные аргументы: ";
        return prefix + names + ".";
    }

    /** Различает неизвестный параметр, неизвестную команду и лишний аргумент. */
    private String describeUnknownArgument(UnmatchedArgumentException exception) {
        // В исключении может быть сразу несколько нераспознанных слов.
        String names = String.join(", ", exception.getUnmatched());
        if (exception.isUnknownOption()) {
            return "неизвестный параметр: " + names + ".";
        }
        // На уровне корневой команды лишнее слово обычно означает неизвестную подкоманду.
        if ("market-pulse".equals(exception.getCommandLine().getCommandName())) {
            return "неизвестная команда: " + names + ".";
        }
        return "лишний аргумент: " + names + ".";
    }

    /** Поясняет, почему введённое значение не подошло для аргумента. */
    private String describeInvalidValue(ArgSpec argument, String value) {
        if (value == null) {
            return "не указано значение для " + argumentName(argument) + ".";
        }

        String message = "некорректное значение «" + value + "» для " + argumentName(argument);
        // Для enum показываем все допустимые значения, например GE, LE, EQ.
        if (argument.type().isEnum()) {
            StringJoiner allowed = new StringJoiner(", ");
            for (Object item : argument.type().getEnumConstants()) {
                allowed.add(item.toString());
            }
            return message + ". Допустимые значения: " + allowed + ".";
        }
        // BigDecimal используется для чисел, например цены инструмента.
        if (argument.type() == BigDecimal.class) {
            return message + ". Ожидается число.";
        }
        return message + ".";
    }

    /** Берёт имя опции (--price) или подпись позиционного аргумента (<ticker>). */
    private String argumentName(ArgSpec argument) {
        // OptionSpec описывает именованную опцию; у остальных аргументов берём подпись.
        return argument instanceof OptionSpec option ? option.longestName() : argument.paramLabel();
    }
}
