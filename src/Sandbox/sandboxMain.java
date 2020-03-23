package Sandbox;

import Sandbox.*; // Import sandbox package

// Include Junit v5
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.*;

import java.util.Scanner;

public class sandboxMain {


    public static void main(String[] args) {
        System.out.println("----- MAIN START -----");

        Scanner scanner = new Scanner(System.in);

        new gui();

        System.out.println(System.getProperty("user.dir")); // C:\Users\tom\Dropbox\Java\CAB302-Dev



        //scanner.nextLine(); // Wait for any input
        System.out.println("----- MAIN END -----");
    }
}
