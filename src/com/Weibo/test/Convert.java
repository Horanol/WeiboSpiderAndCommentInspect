package com.Weibo.test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Convert {
	public static void main(String[] args) {
		try (FileWriter writer = new FileWriter(new File("Comments/aaa.txt"))) {
			writer.write("aa");
		} catch (IOException e) {
			
			e.printStackTrace();
		}
		System.out.println("ok");
	}
}
