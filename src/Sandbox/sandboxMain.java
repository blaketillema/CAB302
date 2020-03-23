package Sandbox;

public class sandboxMain {

    public static void main(String[] args) {
        System.out.println("----- SANDBOX START -----");

        System.out.println(System.getProperty("user.dir")); // Output 'working directory' e.g. C:\java\project

        new gui(); // GUI class for testing billboard features
        new readXML(); // Class for figuring out parsing XML


        System.out.println("----- SANDBOX END -----");
    }
}
