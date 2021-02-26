import java.io.*;
import java.util.Scanner;

public class InputFile{
    public static void main(String[] args) throws IOException {

        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter number of entries");

        int size = scanner.nextInt();

        double d = 123.123;

        PrintWriter printWriter = new PrintWriter(new File("input.txt"));
        printWriter.println(size+"\n");

        for(int i=1;i<=size;i++)
        {
            printWriter.println(d);
            d++;
        }

        printWriter.close();
        System.out.println("File written successfully");
    }
}