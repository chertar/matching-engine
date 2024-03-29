package com.chertar;

import com.chertar.util.*;

import java.util.*;

/**
 * A simple command-line based app that accepts orders
 * and prints fills and top of book quotes
 */
public class MatchingApp {
    private final List<Instrument> instruments;
    private final Map<Instrument, MatchingEngine> engineMap;
    private OrderIdGenerator idGenerator = new OrderIdGenerator();
    private OrderCache orderCache = new OrderCache();

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
            String line = in.nextLine().toUpperCase();
            try {
                if (line.startsWith("CANCEL")) {
                    String orderId = parseOrderId(line);
                    Order order = orderCache.get(orderId);
                    if (order == null) {
                        throw new IllegalArgumentException("Cannot find order with id '"+orderId+"'");
                    }
                    MatchingEngine engine = engineMap.get(order.instrument());
                    engine.cancel(order);
                    System.out.println("\tCanceled order " + order);
                }
                else if (line.startsWith("LIMIT") || line.startsWith("MARKET")) {
                    Order order = parseOrder(line);
                    orderCache.add(order);
                    MatchingEngine engine = engineMap.get(order.instrument());
                    List<Fill> fills = engine.newOrder(order);
                    System.out.printf("\tORDER ID ==> %s\n", order.id());

                    if (fills.isEmpty()) {
                        System.out.println("\tNo fills");
                    }
                    else {
                        System.out.print("\tYou got FILLS ==> ");
                        boolean firstFill = true;
                        for (Fill fill : fills) {
                            if (firstFill) {
                                firstFill = false;
                            } else {
                                System.out.print(", ");
                            }

                            System.out.printf("%d @ %.2f", fill.qty(), fill.price().doubleValue());
                        }
                        System.out.print("\n");
                    }
                }
                else {
                    throw new IllegalArgumentException("Unrecognized command. Command must be LIMIT, MARKET or CANCEL");
                }

                printQuotes();
            }
            catch (IllegalArgumentException e) {
                error("Error parsing command. " + e.getMessage());
                printExamples();
            }
            catch (MatchingEngineException e) {
                error("Matching engine returned an error: " + e.getMessage());
                printExamples();
            }
            catch(Exception e) {
                error("General exception occurred. " + e.getClass().getSimpleName() + " : " + e.getMessage());
                printExamples();
            }
            finally {
                prompt();
            }
        }
    }

    private void printQuotes() {
        System.out.printf("\t%10s: %10s %10s %10s %10s\n",
                "Instrument", "bidQty", "bidPrice","askQty","askPrice");
        for (Instrument instrument : instruments) {
            MatchingEngine engine = engineMap.get(instrument);
            Quote topBids = engine.topBids();
            Quote topAsks = engine.topAsks();
            System.out.printf("\t%10s: %10d %10.2f %10d %10.02f\n",
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
        return new Order(idGenerator.next(), instrument, side, type, qty, price);
    }

    private String parseOrderId(String line) {
        String[] parts = line.split(" ");
        if (parts.length != 2) {
            throw new IllegalArgumentException("Cannot parse order id.  Expected 1 argument but found " + (parts.length - 1));
        }
        return parts[1];
    }

    private static void prompt() {
        System.out.print("command> ");
    }

    private static void printExamples() {
        System.out.print("\tSupported commands:\n");
        System.out.print("\t\t - limit buy/sell qty instrument price. Example: limit buy 10 btc-usd 100.0\n");
        System.out.print("\t\t - market buy/sell qty instrument. Example: market sell 10 btc-usd\n");
        System.out.print("\t\t - cancel orderId.  Example cancel 3\n");
    }

    private static void error(String msg) {
        System.out.println("\tERROR: " + msg);
    }
    public static void main(String[] args) {
        MatchingApp app = new MatchingApp();
        app.start();
    }
}
