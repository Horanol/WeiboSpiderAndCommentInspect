package com.Weibo.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.ansj.domain.Term;
import org.ansj.splitWord.analysis.ToAnalysis;
import org.ansj.util.FilterModifWord;

import com.Weibo.Dao.CommentDAO;
import libsvm.svm_predict;
import libsvm.svm_scale;
import libsvm.svm_train;

public class Test {
	private static Map<String, Float> getEmotionalWordMap() {
		Map<String, Float> map = new HashMap<>();

		File file1 = new File("./Dictionarys/汉语情感词极值表.txt");
		File file2 = new File("./Dictionarys/ntusd.txt");
		File file3 = new File("./Dictionarys/NetWords.txt");
		try (BufferedReader bufferedReader1 = new BufferedReader(new InputStreamReader(new FileInputStream(file1)));
				BufferedReader bufferedReader2 = new BufferedReader(new InputStreamReader(new FileInputStream(file2)));
				BufferedReader bufferedReader3 = new BufferedReader(
						new InputStreamReader(new FileInputStream(file3)))) {
			String string = null;
			int index = 0;
			// while ((string = bufferedReader1.readLine()) != null) {
			// index = string.indexOf("\t");
			// map.put(string.substring(0, index),
			// Float.valueOf(string.substring(index + 1, string.length())));
			// }
			while ((string = bufferedReader2.readLine()) != null) {
				index = string.indexOf("\t");
				map.put(string.substring(0, index), Float.valueOf(string.substring(index + 1, string.length())));
			}
			while ((string = bufferedReader3.readLine()) != null) {
				index = string.indexOf("\t");
				map.put(string.substring(0, index), Float.valueOf(string.substring(index + 1, string.length())));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return map;
	}

	private static List<String> getStopWords() {
		List<String> list = new LinkedList<>();
		File file = new File("./StopWords.txt");
		try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(file)))) {
			String string = null;
			while ((string = bufferedReader.readLine()) != null) {
				list.add(string);
			}
		} catch (IOException e) {
		}

		return list;
	}

	private static List<String> getOppositeWords() {
		List<String> list = new LinkedList<>();
		File file = new File("./OppositeWords.txt");
		try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(file)))) {
			String string = null;
			while ((string = bufferedReader.readLine()) != null) {
				list.add(string);
			}
		} catch (IOException e) {
		}

		return list;
	}

	private static Map<String, Float> getDegreeWordsMap() {
		Map<String, Float> map = new HashMap<>();
		File file = new File("./DegreeWords.txt");
		try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(file)))) {
			String string = null;
			int index = 0;
			while ((string = bufferedReader.readLine()) != null) {
				index = string.indexOf("\t");
				map.put(string.substring(0, index), Float.valueOf(string.substring(index + 1, string.length())));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return map;
	}

	private static void parseCommentToTrainText() {
		// 获取情感词典
		Map<String, Float> emotionalMap = getEmotionalWordMap();
		Map<String, Float> degreeMap = getDegreeWordsMap();
		List<String> oppositeWords = getOppositeWords();
		// 获取停用词列表
		List<String> stopWords = getStopWords();

		for (Iterator<String> iterator = stopWords.iterator(); iterator.hasNext();) {
			String word = (String) iterator.next();
			if (emotionalMap.containsKey(word) || oppositeWords.contains(word) || degreeMap.containsKey(word)) {
				iterator.remove();
			}
		}
		FilterModifWord.insertStopWords(stopWords);

		// 获取评论列表
		File rawCommentDir = new File("./Comments/RawComments");
		File parsedCommentDir = new File("./Comments/ParsedComments");
		File[] fileList = rawCommentDir.listFiles();
		
		for (int i = 0; i < fileList.length; i++) {
			File rawComment = fileList[i];
			if (rawComment.isFile()) {
				List<String> commentList = CommentDAO.getComments(rawComment.getPath());
				File parsedComment = new File(parsedCommentDir+"/"+rawComment.getName().substring(0, rawComment.getName().length()-4)+"_parsed.txt");
				
				for (String string : commentList) {
					try (FileWriter writer = new FileWriter(parsedComment)) {
						List<Term> terms = FilterModifWord.modifResult(
								ToAnalysis.parse(string.substring(string.indexOf('\t') + 1, string.length())));
						String data = getSVMFormatData(terms, emotionalMap, oppositeWords, degreeMap);
						if (data != null && data.length() != 0) {
							writer.write(string.substring(0, string.indexOf('\t')));
							writer.write("  ");
							writer.write(data);
							writer.write("\n");
						}
						writer.write(string);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}

	}

	private static String getSVMFormatData(List<Term> terms, Map<String, Float> emotionalMap,
			List<String> oppositeWords, Map<String, Float> degreeMap) {
		StringBuilder result = new StringBuilder();
		boolean opp = false;
		float degree = 1.0f;
		float score = 0.0f;
		int count = 1;
		for (Term term : terms) {
			String word = term.getName();
			if (oppositeWords.contains(word)) {
				opp = true;
			} else if (degreeMap.containsKey(word)) {
				degree = degreeMap.get(word);
			} else if (emotionalMap.containsKey(word)) {
				score = emotionalMap.get(word) * degree * (opp ? -1.0f : 1.0f);
				result.append((count++) + ":" + score + " ");
				opp = false;
				degree = 1.0f;
			}
		}
		System.out.println(terms);
		return result.toString();
	}

	public static void main(String[] args) {
		 parseCommentToTrainText();
		 System.out.println("TrainText has created!");

		String filepath = "F:\\SVM评论测试\\";
		String[] trainArgs = { "-v", "5", filepath + "train_comment.txt", filepath + "model_comment.txt" };
		String[] predictArgs = { filepath + "predict_comment.txt", filepath + "model_comment.txt",
				filepath + "predictResult_comment.txt" };
		System.out.println("----------------SVM运行开始-----------------");

		// String[] scaleArgs = { "-s", filepath +"scaledTrain_comment.txt",filepath + "train_comment.txt" };
		try {
			// svm_scale.main(scaleArgs);// 缩小范围
			svm_train.main(trainArgs);// 训练
			svm_predict.main(predictArgs);// 预测或分类
		} catch (IOException e) {

			e.printStackTrace();
		}

		System.out.println("SVM结束");
	}

}
