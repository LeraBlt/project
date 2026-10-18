package academy.backend.market_pulse.filter;

import academy.backend.market_pulse.model.Instrument;
import academy.backend.market_pulse.model.Stock;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Отбор по цене — применим только к {@link Stock}: остальные типы инструментов сегодня не несут
 * числового значения цены, поэтому для них {@code matches} всегда возвращает {@code false}. В
 * роли цены — дивидендная доходность ({@link Stock#getDividendYield()}), единственный числовой
 * атрибут акции на этом этапе проекта. Логика операторов сравнения реализована внутри
 * перечисления {@link Operator}.
 */
public final class PriceFilter implements InstrumentFilter {

    public enum Operator {
        GE {
            @Override
            boolean matches(int comparison) { return comparison >= 0; }
        },
        LE {
            @Override
            boolean matches(int comparison) { return comparison <= 0; }
        },
        EQ {
            @Override
            boolean matches(int comparison) { return comparison == 0; }
        };

        abstract boolean matches(int comparison);
    }

    private final Operator operator;
    private final BigDecimal threshold;

    public PriceFilter(Operator operator, BigDecimal threshold) {
        this.operator = Objects.requireNonNull(operator, "Оператор сравнения не должен быть null");
        this.threshold = Objects.requireNonNull(threshold, "Пороговое значение не должно быть null");
    }

    @Override
    public boolean matches(Instrument instrument) {
        if (!(Objects.requireNonNull(instrument, "Инструмент не должен быть null") instanceof Stock stock)) {
            return false;
        }
        int comparison = stock.getDividendYield().compareTo(threshold);
        return operator.matches(comparison);
    }
}
