package com.chertar;

import com.chertar.util.Instrument;
import com.chertar.util.OrderType;
import com.chertar.util.Side;

import java.util.*;

/**
 * A simple command-line based app that accepts orders
 * and prints fills and top of book quotes
 */
public class MatchingApp {
    private final List<Instrument> instruments;
    private final Map<Instrument, MatchingEngine> engineMap;

    public MatchingApp () {
        System.out.println("Starting Matching App");

        instruments = new ArrayList<>();
        instruments.add(Instrument.of("BTC-USD"));
        instruments.add(Instrument.of("ETH-USD"));
        instruments.add(Instrument.of("ETH-BTC"));

        engineMap = new HashMap<>();
        for (Instrument instrument : instruments) {
            engineMap.put(instrument, new MatchingEngine(instrument));
            System.out.println("Created matching engine for " + instrument.symbol());
        }
    }

    public void start() {
        printExamples();
        prompt();

        Scanner in = new Scanner(System.in);
        while (in.hasNext()) {
            String line = in.nextLine();
            try {
                Order order = parseOrder(line);
                MatchingEngine engine = engineMap.get(order.instrument());
                List<Fill> fills = engine.process(order);
                if (fills.isEmpty()) {
                    System.out.println("No fills");
                }
                else {
                    System.out.println("You got fills!");
                    for (Fill fill : fills) {
                        System.out.printf("%10d %10.2f\n", fill.qty(), fill.price().doubleValue());
                    }
                }
                printQuotes();
            }
            catch (IllegalArgumentException e) {
                error(e.getMessage());
                printExamples();
            }
            prompt();
        }
    }

    private void printQuotes() {
        System.out.printf("%10s: %10s %10s %10s. %10s\n",
                "Instrument", "bidQty", "bidPrice","askQty","askPrice");
        for (Instrument instrument : instruments) {
            MatchingEngine engine = engineMap.get(instrument);
            Quote topBids = engine.topBids();
            Quote topAsks = engine.topAsks();
            System.out.printf("%10s: %10d %10.2f %10d %10.02f\n",
                    instrument.symbol(), topBids.qty(), topBids.price().doubleValue(), topAsks.qty(), topAsks.price().doubleValue());
        }
    }

    private Order parseOrder(String line) throws IllegalArgumentException {
        String[] parts = line.split(" ");
        OrderType type = OrderType.valueOf(parts[0].toUpperCase());
        if (type == OrderType.LIMIT && parts.length != 5) {
            throw new IllegalArgumentException("Limit order expects 5 arguments but received " + parts.length);
        } else if(type == OrderType.MARKET && parts.length != 4) {
            throw new IllegalArgumentException("Market order expects 4 arguments but received " + parts.length);
        }

        Side side = Side.valueOf(parts[1].toUpperCase());
        long qty = Long.valueOf(parts[2]);
        Instrument instrument = Instrument.of(parts[3].toUpperCase());

        MatchingEngine engine = engineMap.get(instrument);
        if (engine == null) {
            throw new IllegalArgumentException("No matching engine configured for instrument '"+instrument.symbol()+"'");
        }
        double price;

        if (type == OrderType.LIMIT) {
            price = Double.parseDouble(parts[4]);
        } else if (type == OrderType.MARKET) {
            price = Double.NaN;
        } else {
            throw new IllegalArgumentException("Unsupported order type " + type);
        }

        return new Order(instrument, side, type, qty, price);
    }

    private static void prompt() {
        System.out.print("Enter order> ");
    }

    private static void printExamples() {
        System.out.print("Enter an order in one of these two formats:\n");
        System.out.print("\t - LIMIT BUY/SELL qty instrument price. Example: LIMIT BUY 10 BTC-USD 100.0\n");
        System.out.print("\t - MARKET BUYS/SELL qty instrument. Example: MARKET SELL 10 BTC-USD\n");
    }

    private static void error(String msg) {
        System.out.println("Command line parsing error. Please check your arguments. " + msg);
    }
    public static void main(String[] args) {
        MatchingApp app = new MatchingApp();
        app.start();
    }
}
