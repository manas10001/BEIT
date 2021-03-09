idlj -fall CorbaBasicModule.idl

javac *.java CorbaBasicModule/*.java
javac *.java TTTModule/*.java

orbd -ORBInitialPort 1050

java CorbaBasicServer -ORBInitialPort 1050& -ORBInitialHost localhost&

java TTTServer -ORBInitialPort 1050& -ORBInitialHost localhost&

java CorbaBasicClient -ORBInitialPort 1050 -ORBInitialHost localhost
    
java TTTClient -ORBInitialPort 1050 -ORBInitialHost localhost