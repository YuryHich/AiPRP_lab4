package com.example.service;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.List;

/** Калькулятор варианта 1: бесскобочные выражения вида 2+4*7. Им пользуются и REST, и веб-сокет. */
public class Calculator {

    private static final String OPS = "+-*/";

    /** Сначала * и /, затем + и -. Бросает IllegalArgumentException с текстом ошибки. */
    public static BigDecimal evaluate(String expr) {
        if (expr == null || expr.trim().isEmpty()) {
            throw new IllegalArgumentException("пустое выражение");
        }
        String s = expr.replaceAll("\\s+", "");
        List<BigDecimal> numbers = new ArrayList<BigDecimal>();
        List<Character> ops = new ArrayList<Character>();

        int i = 0;
        while (i < s.length()) {
            int start = i;
            if (s.charAt(i) == '-') {
                i++;
            }
            while (i < s.length() && (Character.isDigit(s.charAt(i)) || s.charAt(i) == '.')) {
                i++;
            }
            String number = s.substring(start, i);
            if (number.isEmpty() || number.equals("-")) {
                char c = start < s.length() ? s.charAt(start) : ' ';
                throw new IllegalArgumentException("ожидалось число, а встретился символ '" + c + "'");
            }
            try {
                numbers.add(new BigDecimal(number));
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("неверное число " + number);
            }
            if (i == s.length()) {
                break;
            }
            char op = s.charAt(i);
            if (OPS.indexOf(op) < 0) {
                throw new IllegalArgumentException("неверный символ '" + op + "'");
            }
            ops.add(op);
            i++;
            if (i == s.length()) {
                throw new IllegalArgumentException("выражение обрывается на операции " + op);
            }
        }

        // первый проход: умножение и деление
        List<BigDecimal> terms = new ArrayList<BigDecimal>();
        List<Character> addOps = new ArrayList<Character>();
        BigDecimal current = numbers.get(0);
        for (int k = 0; k < ops.size(); k++) {
            char op = ops.get(k);
            BigDecimal next = numbers.get(k + 1);
            if (op == '*') {
                current = current.multiply(next);
            } else if (op == '/') {
                if (next.signum() == 0) {
                    throw new IllegalArgumentException("деление на ноль");
                }
                current = current.divide(next, MathContext.DECIMAL64);
            } else {
                terms.add(current);
                addOps.add(op);
                current = next;
            }
        }
        terms.add(current);

        // второй проход: сложение и вычитание слева направо
        BigDecimal result = terms.get(0);
        for (int k = 0; k < addOps.size(); k++) {
            result = addOps.get(k) == '+' ? result.add(terms.get(k + 1)) : result.subtract(terms.get(k + 1));
        }
        return result;
    }

    /** 30 вместо 30.0, 3.5 вместо 3.50. */
    public static String format(BigDecimal value) {
        if (value.signum() == 0) {
            return "0";
        }
        return value.stripTrailingZeros().toPlainString();
    }

    /** Похоже ли сообщение чата на выражение: только цифры, операции, точки, пробелы и есть операция после числа. */
    public static boolean isExpression(String text) {
        return text != null
                && text.matches("[0-9.\\s+\\-*/]+")
                && text.matches(".*\\d\\s*[-+*/].*");
    }

    public static String json(String expr) {
        String e = expr == null ? "" : expr;
        try {
            return "{\"expression\": \"" + escape(e) + "\", \"result\": " + format(evaluate(e)) + "}";
        } catch (IllegalArgumentException ex) {
            return "{\"expression\": \"" + escape(e) + "\", \"error\": \"" + escape(ex.getMessage()) + "\"}";
        }
    }

    private static String escape(String value) {
        return value.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
