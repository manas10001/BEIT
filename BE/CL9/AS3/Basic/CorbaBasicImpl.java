import CorbaBasicModule.CorbaBasicPOA;
import java.lang.String;

//extend the portable obj adapter it manages server side resources
class CorbaBasicImpl extends CorbaBasicPOA{
    
    CorbaBasicImpl(){
        super();
    }    

    public String process_string(String tmp){
        StringBuffer str = new StringBuffer(tmp);    
        str.reverse();
        return (("Reversed string is: " + str));
    }
}