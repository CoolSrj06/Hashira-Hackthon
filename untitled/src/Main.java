import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.FileReader;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class Main {

    static class DataPoint {
        final int x;
        final BigInteger y;

        DataPoint(int x, BigInteger y) {
            this.x = x;
            this.y = y;
        }
    }

    public static BigInteger decodeValue(String value, int base) {
        return new BigInteger(value, base);
    }


    public static BigInteger lagrangeConstantTerm(List<DataPoint> points) {
        int k = points.size();
        BigInteger result = BigInteger.ZERO;

        for (int i = 0; i < k; i++) {
            BigInteger xi = BigInteger.valueOf(points.get(i).x);
            BigInteger yi = points.get(i).y;

            BigInteger numerator = BigInteger.ONE;
            BigInteger denominator = BigInteger.ONE;

            for (int j = 0; j < k; j++) {
                if (j != i) {
                    BigInteger xj = BigInteger.valueOf(points.get(j).x);
                    numerator = numerator.multiply(xj.negate());
                    denominator = denominator.multiply(xi.subtract(xj));
                }
            }

            BigInteger term = yi.multiply(numerator).divide(denominator);
            result = result.add(term);
        }

        return result;
    }

    public static void main(String[] args) {
        try {
            // Parse input JSON file using json-simple
            JSONParser parser = new JSONParser();
            JSONObject rootObj = (JSONObject) parser.parse(new FileReader("data.json"));

            // Extract keys object
            JSONObject keysObj = (JSONObject) rootObj.get("keys");
            int n = ((Long) keysObj.get("n")).intValue();
            int k = ((Long) keysObj.get("k")).intValue();

            List<DataPoint> points = new ArrayList<>();

            // Iterate through keys 1 to n collecting points until have k roots
            for (int i = 1; i <= n && points.size() < k; i++) {
                JSONObject pointObj = (JSONObject) rootObj.get(String.valueOf(i));
                if (pointObj != null) {
                    int x = i;
                    String baseStr = (String) pointObj.get("base");
                    String valueStr = (String) pointObj.get("value");

                    int base = Integer.parseInt(baseStr);

                    BigInteger y = decodeValue(valueStr, base);
                    points.add(new DataPoint(x, y));
                }
            }

            BigInteger secretC = lagrangeConstantTerm(points);
            System.out.println("Secret constant c = " + secretC.toString());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
