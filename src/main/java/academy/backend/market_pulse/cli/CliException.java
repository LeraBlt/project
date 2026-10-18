package academy.backend.market_pulse.cli;

/**
 * Ошибка, которую пользователь может исправить сам: например, неверный тип инструмента.
 * Исходная причина сохраняется для разработчика, но не печатается в обычном выводе CLI.
 */
public final class CliException extends RuntimeException {

    public CliException(String message, Throwable cause) {
        super(message, cause);
    }
}
