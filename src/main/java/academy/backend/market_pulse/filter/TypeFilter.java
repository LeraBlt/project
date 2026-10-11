package academy.backend.market_pulse.filter;

import academy.backend.market_pulse.model.Instrument;

import java.util.Objects;

/** Стратегия отбора инструментов по коду типа, возвращаемому {@code getType()}. */
public final class TypeFilter implements InstrumentFilter {

    private final String type;

    public TypeFilter(String type) {
        if (type == null || type.isBlank()) {
            throw new IllegalArgumentException("Тип не должен быть пустым");
        }
        this.type = type;
    }

    @Override
    public boolean matches(Instrument instrument) {
        return Objects.requireNonNull(instrument, "Инструмент не должен быть null")
                .getType().equalsIgnoreCase(type);
    }
}
