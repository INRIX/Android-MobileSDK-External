package com.inrix.inrixrouterecorder;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.inrix.sdk.model.GeoPoint;

import android.location.Address;
import android.net.Uri;
import android.os.AsyncTask;

public class InrixGeocoder extends AsyncTask<String, Void, GeoPoint> {
	public interface GeocoderCallBack {
		public void onResult(GeoPoint point);
	}

	private final String GOOGLE_RESULTS_KEY = "results";
	private final String GOOGLE_FORMATTED_ADDRESS_KEY = "formatted_address";
	private final String GOOGLE_GEOMETRY_KEY = "geometry";
	private final String GOOGLE_LOCATION_KEY = "location";
	private final String GOOGLE_LAT_KEY = "lat";
	private final String GOOGLE_LON_KEY = "lng";
	private final String GOOGLE_ADDRESS_COMPONENTS_KEY = "address_components";
	private final String GOOGLE_LONG_NAME_KEY = "long_name";
	private final String GOOGLE_SHORT_NAME_KEY = "short_name";
	private final String GOOGLE_COUNTRY_KEY = "country";
	private final String GOOGLE_AAL_1_KEY = "administrative_area_level_1";
	private final String GOOGLE_AAL_2_KEY = "administrative_area_level_2";
	private final String GOOGLE_POSTAL_CODE_KEY = "postal_code";
	private final String GOOGLE_LOCALITY_KEY = "locality";
	private final String GOOGLE_ROUTE_KEY = "route";
	private final String GOOGLE_STREET_NUMBER_KEY = "street_number";
	private final String GOOGLE_PARK_KEY = "park";
	private final String GOOGLE_AIRPORY_KEY = "airport";
	private final String GOOGLE_TYPES_KEY = "types";
	private final String GOOGLE_POI_KEY = "point_of_interest";
	private final String GOOGLE_ESTABLISHMENT_KEY = "establishment";
	private final String GOOGLE_NATURAL_FEATURE_KEY = "natural_feauture";
	GeocoderCallBack callback;

	public InrixGeocoder(GeocoderCallBack callback) {
		this.callback = callback;
	}

	public void getAddress(String addressLine) {
		this.execute(addressLine);
	}

	@Override
	protected GeoPoint doInBackground(String... params) {
		GeoPoint result = null;

		if (params == null || params.length == 0) {
			return null;
		}

		Object obj = params[0];

		try {
			List<Address> addresses = requestFromAddressString((String) obj);
			if (addresses != null && !addresses.isEmpty()) {
				result = new GeoPoint(addresses.get(0).getLatitude(), addresses
						.get(0).getLongitude());
			}

		} catch (IOException e) {
		} catch (Exception e) {
		}

		return result;

	}

	@Override
	protected void onPostExecute(GeoPoint result) {

		if (callback == null || this.isCancelled()) {
			return;
		}

		callback.onResult(result);
	}

	private List<Address> requestFromAddressString(String address)
			throws IOException, JSONException {
		String request = "http://maps.google.com/maps/api/geocode/json?address="
				+ Uri.encode(address) + "&sensor=true";
		return doRequest(request);
	}

	private List<Address> doRequest(String uri) throws IOException,
			JSONException {
		ArrayList<Address> result = new ArrayList<Address>();

		HttpGet httpGet = new HttpGet(uri);
		HttpClient client = new DefaultHttpClient();
		HttpResponse response;

		response = client.execute(httpGet);
		HttpEntity entity = response.getEntity();
		InputStream stream = entity.getContent();

		JSONObject root;

		try {
			String jsonString = readData(stream);
			root = new JSONObject(jsonString);
		} catch (Exception e) {
			throw new JSONException("Failed to parse response");
		}

		JSONArray dataResults = root.getJSONArray(GOOGLE_RESULTS_KEY);
		if (dataResults == null) {
			throw new JSONException("Failed to parse response");
		}

		for (int i = 0; i < dataResults.length(); i++) {

			try {
				Address address = parseAddress(dataResults.getJSONObject(i));
				result.add(address);
			} catch (JSONException parseException) {
			}
		}

		return result;
	}

	private String readData(InputStream stream) throws Exception {
		final char[] buffer = new char[0x10000];
		StringBuilder stringBuilder = new StringBuilder();
		InputStreamReader reader = new InputStreamReader(stream, "UTF-8");
		int offset;
		do {
			offset = reader.read(buffer, 0, buffer.length);
			if (offset > 0) {
				stringBuilder.append(buffer, 0, offset);
			}
		} while (offset >= 0);

		return stringBuilder.toString();
	}

	private Address parseAddress(JSONObject addressObj) throws JSONException {
		Address result = new Address(Locale.getDefault());

		result.setAddressLine(0,
				addressObj.getString(GOOGLE_FORMATTED_ADDRESS_KEY));
		result.setLatitude(addressObj.getJSONObject(GOOGLE_GEOMETRY_KEY)
				.getJSONObject(GOOGLE_LOCATION_KEY).getDouble(GOOGLE_LAT_KEY));
		result.setLongitude(addressObj.getJSONObject(GOOGLE_GEOMETRY_KEY)
				.getJSONObject(GOOGLE_LOCATION_KEY).getDouble(GOOGLE_LON_KEY));

		JSONArray addressComponents = addressObj
				.getJSONArray(GOOGLE_ADDRESS_COMPONENTS_KEY);

		for (int i = 0; i < addressComponents.length(); i++) {
			JSONObject component = addressComponents.getJSONObject(i);
			String type = component.getJSONArray(GOOGLE_TYPES_KEY).getString(0);
			if (type.equals(GOOGLE_AAL_1_KEY)) {
				result.setAdminArea(component.getString(GOOGLE_SHORT_NAME_KEY));
			} else if (type.equals(GOOGLE_COUNTRY_KEY)) {
				result.setCountryName(component.getString(GOOGLE_LONG_NAME_KEY));
				result.setCountryCode(component
						.getString(GOOGLE_SHORT_NAME_KEY));
			} else if (type.equals(GOOGLE_POSTAL_CODE_KEY)) {
				result.setPostalCode(component.getString(GOOGLE_SHORT_NAME_KEY));
			} else if (type.equals(GOOGLE_LOCALITY_KEY)) {
				result.setLocality(component.getString(GOOGLE_SHORT_NAME_KEY));
			} else if (type.equals(GOOGLE_AAL_2_KEY)) {
				result.setSubAdminArea(component
						.getString(GOOGLE_SHORT_NAME_KEY));
			} else if (type.equals(GOOGLE_POI_KEY)
					|| type.equals(GOOGLE_NATURAL_FEATURE_KEY)
					|| type.equals(GOOGLE_AIRPORY_KEY)
					|| type.equals(GOOGLE_ESTABLISHMENT_KEY)
					|| type.equals(GOOGLE_PARK_KEY)
					|| type.equals(GOOGLE_STREET_NUMBER_KEY)) {
				result.setFeatureName(component.getString(GOOGLE_LONG_NAME_KEY));
			} else if (type.equals(GOOGLE_ROUTE_KEY)) {
				result.setThoroughfare(component
						.getString(GOOGLE_SHORT_NAME_KEY));
			}
		}
		return result;
	}
}