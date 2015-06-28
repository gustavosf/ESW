
public class Converter {

	/**
	 * Convert from Celsius to Farenheit
	 */
	public float celsiusToFarenheit(float celsius) {
		return (celsius * 9 / 5) + 32;
	}
	
	/**
	 * Convert from Farenheit to Celsius
	 */
	public float farenheitToCelsius(float farenheit) {
		return (farenheit - 32) * 5 / 9;
	}

}
