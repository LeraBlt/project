package academy.backend.market_pulse.factory;

import academy.backend.market_pulse.model.Currency;
import academy.backend.market_pulse.model.Instrument;
import academy.backend.market_pulse.util.Registry;

/**
 * Выбирает фабрику инструмента по его типу: STOCK, BOND или ETF.
 * При загрузке классов фабрики регистрируют себя в {@link Registry}.
 */
public final class InstrumentFactories {

    private static final Registry<String, InstrumentFactory> REGISTRY = new Registry<>();

    static {
        // Загрузка класса запускает его статический блок, который добавляет фабрику в реестр.
        loadClass(StockFactory.class);
        loadClass(BondFactory.class);
        loadClass(EtfFactory.class);
    }

    private InstrumentFactories() {
    }

    public static void register(String type, InstrumentFactory factory) {
        REGISTRY.register(type.toUpperCase(), factory);
    }

    public static Instrument create(String type, String ticker, String name, Currency currency) {
        InstrumentFactory factory = REGISTRY.find(type.toUpperCase())
                .orElseThrow(() -> new IllegalArgumentException("Неизвестный тип инструмента: " + type
                        + ". Допустимые типы: STOCK, BOND, ETF"));
        return factory.create(ticker, name, currency);
    }

    private static void loadClass(Class<?> factoryClass) {
        try {
            Class.forName(factoryClass.getName());
        } catch (ClassNotFoundException e) {
            throw new ExceptionInInitializerError(e);
        }
    }
}
