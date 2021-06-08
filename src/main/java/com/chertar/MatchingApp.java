package com.chertar;

import com.chertar.util.Instrument;
import com.chertar.util.OrderParsingException;
import com.chertar.util.OrderType;
import com.chertar.util.Side;

import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class MatchingApp {
    private List<Instrument> instruments = List.of(
            Instrument.of("BTC-USD"),
            Instrument.of("ETH-USD"),
            Instrument.of("ETH-BTC"));

    private Map<Instrument, MatchingEngine> engineMap = new HashMap<>();

    public MatchingApp () {
        System.out.println("Starting Matching App");

        for (Instrument instrument : instruments) {
            engineMap.put(instrument, new MatchingEngine(instrument));
            System.out.println("Created matching engine for " + instrument);
        }
    }

    public void start() {
        prompt();

        Scanner in = new Scanner(System.in);
        while (in.hasNext()) {
            String line = in.nextLine();
            try {
                Order order = parseOrder(line);
                MatchingEngine engine = engineMap.get(order.instrument());
            }
            catch (IllegalArgumentException e) {
                error(e.getMessage());
            }
            prompt();
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
            throw new IllegalArgumentException("No matching engine exists for instrument '"+  instrument + "'");
        }

        double price;

        if (type == OrderType.LIMIT) {
            price = Double.parseDouble(parts[4]);
        } else if (type == OrderType.MARKET) {
            price = Double.NaN;
        } else {
            throw new IllegalArgumentException("Unsupported order type " + type);
        }

        return new Order(side, type, qty, price);
    }

    private static void prompt() {
        System.out.print("Enter an order in one of these two formats:\n");
        System.out.print("\t - limit buy/sell qty instrument price. Example: limit buy 10 BTC-USD 100.0\n");
        System.out.print("\t - market buy/sell qty instrument. Example: market sell 10 BTC-USD\n");
        System.out.print("> ");
    }

    private static void error(String msg) {
        System.out.println("Command line parsing error. Please check your arguments. " + msg);
    }
    public static void main(String[] args) {
        MatchingApp app = new MatchingApp();
        app.start();
    }
}
