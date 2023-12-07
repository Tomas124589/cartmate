package cz.orv0005.cartmate.openfoodfacts;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class APIMapper {

    private static final String BASE_URL = "https://world.openfoodfacts.org/api/v2/";

    public FoodFactItem getByEan(String ean) throws IOException, JSONException {
        URL url = new URL(BASE_URL + "product/" + ean + ".json");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        StringBuilder strBuilder = new StringBuilder();

        String line;
        while ((line = reader.readLine()) != null) {
            strBuilder.append(line);
        }

        JSONObject json = new JSONObject(strBuilder.toString());

        if (json.getInt("status") != 1)
            return null;

        JSONObject product = json.getJSONObject("product");

        String productName = product.getString("product_name").trim();
        String brands = product.getString("brands");
        String categories = product.getString("categories");

        String brandFirst = brands.split(",")[0].trim();

        return new FoodFactItem(brandFirst + " - " + productName, categories);
    }
}
