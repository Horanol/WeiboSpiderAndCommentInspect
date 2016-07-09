package com.Weibo.Dao;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;

import com.Weibo.Beans.CommentInfo;

public class CommentDAO {
	private static ArrayBlockingQueue<CommentInfo> commentInfos = new ArrayBlockingQueue<>(1000);

	// 把一个文件中的评论读入内存
	public static List<String> getComments(String commentFile) {

		List<String> list = new LinkedList<>();

		String string = null;
		try (BufferedReader bufferedReader = new BufferedReader(
				new InputStreamReader(new FileInputStream(commentFile)))) {
			while ((string = bufferedReader.readLine()) != null) {
				list.add(string);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return list;
	}

	public static void record(CommentInfo info) {
		System.out.println(Thread.currentThread().getName() + ":    " + info);
		try {
			commentInfos.add(info);
		} catch (IllegalStateException e) {
			synchronized (CommentDAO.class) {
				store();
				commentInfos.clear();
				commentInfos.add(info);
			}
		}
	}

	public static void store() {
		FileWriter writer = null;
		try {
			writer = new FileWriter("./Comments/RawComments/WeiboComment_" + System.currentTimeMillis() + ".txt");
			for (CommentInfo commentInfo : commentInfos) {
				writer.write("0	"+commentInfo.getContent() + "\r\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				writer.close();
			} catch (IOException e) {
			}
		}

	}
}
