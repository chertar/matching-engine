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
    public static void main(String[] args) {
        System.out.println("Starting Matching App");
        List<Instrument> instruments = List.of(
                Instrument.of("BTC-USD"),
                Instrument.of("ETH-USD"),
                Instrument.of("ETH-BTC"));

        Map<Instrument, MatchingEngine> engineMap = new HashMap<>();

        for (Instrument instrument : instruments) {
            engineMap.put(instrument, new MatchingEngine(instrument));
            System.out.println("Created matching engine for " + instrument);
        }

        prompt();

        Scanner in = new Scanner(System.in);
        while (in.hasNext()) {
            String line = in.nextLine();
            try {
                Order order = parseOrder(line);
            }
            catch (IllegalArgumentException e) {
                error(e.getMessage());
            }
            prompt();
        }
    }




    private static Order parseOrder(String line) throws IllegalArgumentException {
        String[] parts = line.split(" ");
        OrderType type = OrderType.valueOf(parts[0].toUpperCase());
        if (type == OrderType.LIMIT && parts.length != 5) {
            throw new IllegalArgumentException("Limit order expects 5 arguments but received " + parts.length);
        }
        else if(type == OrderType.MARKET && parts.length != 4) {
            throw new IllegalArgumentException("Market order expects 4 arguments but received " + parts.length);
        }
        Side side = Side.valueOf(parts[1].toUpperCase());
        long qty = Long.valueOf(parts[2]);

        return null;
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
}
