package TTTModule;

/**
* TTTModule/TTTHolder.java .
* Generated by the IDL-to-Java compiler (portable), version "3.2"
* from TTTModule.idl
* Tuesday, 9 March 2021 22:22:34 o'clock IST
*/

public final class TTTHolder implements org.omg.CORBA.portable.Streamable
{
  public TTTModule.TTT value = null;

  public TTTHolder ()
  {
  }

  public TTTHolder (TTTModule.TTT initialValue)
  {
    value = initialValue;
  }

  public void _read (org.omg.CORBA.portable.InputStream i)
  {
    value = TTTModule.TTTHelper.read (i);
  }

  public void _write (org.omg.CORBA.portable.OutputStream o)
  {
    TTTModule.TTTHelper.write (o, value);
  }

  public org.omg.CORBA.TypeCode _type ()
  {
    return TTTModule.TTTHelper.type ();
  }

}
