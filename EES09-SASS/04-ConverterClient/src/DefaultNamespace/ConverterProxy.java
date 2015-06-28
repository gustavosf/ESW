package DefaultNamespace;

public class ConverterProxy implements DefaultNamespace.Converter {
  private String _endpoint = null;
  private DefaultNamespace.Converter converter = null;
  
  public ConverterProxy() {
    _initConverterProxy();
  }
  
  public ConverterProxy(String endpoint) {
    _endpoint = endpoint;
    _initConverterProxy();
  }
  
  private void _initConverterProxy() {
    try {
      converter = (new DefaultNamespace.ConverterServiceLocator()).getConverter();
      if (converter != null) {
        if (_endpoint != null)
          ((javax.xml.rpc.Stub)converter)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
        else
          _endpoint = (String)((javax.xml.rpc.Stub)converter)._getProperty("javax.xml.rpc.service.endpoint.address");
      }
      
    }
    catch (javax.xml.rpc.ServiceException serviceException) {}
  }
  
  public String getEndpoint() {
    return _endpoint;
  }
  
  public void setEndpoint(String endpoint) {
    _endpoint = endpoint;
    if (converter != null)
      ((javax.xml.rpc.Stub)converter)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
    
  }
  
  public DefaultNamespace.Converter getConverter() {
    if (converter == null)
      _initConverterProxy();
    return converter;
  }
  
  public float celsiusToFarenheit(float celsius) throws java.rmi.RemoteException{
    if (converter == null)
      _initConverterProxy();
    return converter.celsiusToFarenheit(celsius);
  }
  
  public float farenheitToCelsius(float farenheit) throws java.rmi.RemoteException{
    if (converter == null)
      _initConverterProxy();
    return converter.farenheitToCelsius(farenheit);
  }
  
  
}