
package user_client;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the user_client package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _ValidateLogin_QNAME = new QName("http://user/", "validateLogin");
    private final static QName _ValidateLoginResponse_QNAME = new QName("http://user/", "validateLoginResponse");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: user_client
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link ValidateLogin }
     * 
     */
    public ValidateLogin createValidateLogin() {
        return new ValidateLogin();
    }

    /**
     * Create an instance of {@link ValidateLoginResponse }
     * 
     */
    public ValidateLoginResponse createValidateLoginResponse() {
        return new ValidateLoginResponse();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ValidateLogin }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link ValidateLogin }{@code >}
     */
    @XmlElementDecl(namespace = "http://user/", name = "validateLogin")
    public JAXBElement<ValidateLogin> createValidateLogin(ValidateLogin value) {
        return new JAXBElement<ValidateLogin>(_ValidateLogin_QNAME, ValidateLogin.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ValidateLoginResponse }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link ValidateLoginResponse }{@code >}
     */
    @XmlElementDecl(namespace = "http://user/", name = "validateLoginResponse")
    public JAXBElement<ValidateLoginResponse> createValidateLoginResponse(ValidateLoginResponse value) {
        return new JAXBElement<ValidateLoginResponse>(_ValidateLoginResponse_QNAME, ValidateLoginResponse.class, null, value);
    }

}
