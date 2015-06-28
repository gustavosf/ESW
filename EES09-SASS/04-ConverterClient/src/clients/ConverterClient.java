package clients;
import DefaultNamespace.*;

public class ConverterClient {

	public static void main (String args[]) throws Exception {
		ConverterServiceLocator l = new ConverterServiceLocator();
		Converter c = l.getConverter();

		float celsius = 10;
		float farenheit = 0;
		farenheit = c.celsiusToFarenheit(celsius);

		System.out.printf("%f celsius is %f farenheit", celsius, farenheit);
	}

}