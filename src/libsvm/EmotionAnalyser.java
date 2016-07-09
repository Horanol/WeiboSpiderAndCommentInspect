package libsvm;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.ansj.domain.Term;
import org.ansj.splitWord.analysis.ToAnalysis;
import org.ansj.util.FilterModifWord;

import com.Weibo.Dao.CommentDAO;

public class EmotionAnalyser {
	private static Map<String, Float> emotionalMap;
	private static Map<String, Float> degreeMap;
	private static List<String> oppositeWords;

	static {
		// 获取情感词典
		emotionalMap = getEmotionalWordMap();
		degreeMap = getDegreeWordsMap();
		oppositeWords = getOppositeWords();
		// 获取停用词列表
		List<String> stopWords = getStopWords();

		for (Iterator<String> iterator = stopWords.iterator(); iterator.hasNext();) {
			String word = (String) iterator.next();
			if (emotionalMap.containsKey(word) || oppositeWords.contains(word) || degreeMap.containsKey(word)) {
				iterator.remove();
			}
		}
		FilterModifWord.insertStopWords(stopWords);
	}

	private static Map<String, Float> getEmotionalWordMap() {
		Map<String, Float> map = new HashMap<>();

		File file2 = new File("./Dictionarys/ntusd.txt");
		File file3 = new File("./Dictionarys/NetWords.txt");
		try (BufferedReader bufferedReader2 = new BufferedReader(new InputStreamReader(new FileInputStream(file2)));
				BufferedReader bufferedReader3 = new BufferedReader(new InputStreamReader(new FileInputStream(file3)))) {
			String string = null;
			int index = 0;
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

	private static void getTrainModel() {
		File trainFile = new File("./Comments/TrainModel/WeiboComment_train.txt");
		File trainModel = new File("./Comments/TrainModel/WeiboComment_parsedTrain.txt");
		List<String> trainComments = CommentDAO.getComments(trainFile.getPath());
		try (FileWriter writer = new FileWriter(trainModel)) {
			for (String comment : trainComments) {
				List<Term> terms = FilterModifWord
						.modifResult(ToAnalysis.parse(comment.substring(comment.indexOf('\t') + 1, comment.length())));
				String data = getSVMFormatData(terms, emotionalMap, oppositeWords, degreeMap);
				writer.write(comment.substring(0, comment.indexOf('\t')));
				writer.write("	");
				if (data != null && data.length() != 0) {
					writer.write(data);
				} else {
					writer.write("1:0");
				}
				writer.write("\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void parseRawComment() {

		// 获取评论列表
		File rawCommentDir = new File("./Comments/RawComments");
		File parsedCommentDir = new File("./Comments/ParsedComments");
		File[] fileList = rawCommentDir.listFiles();

		for (int i = 0; i < fileList.length; i++) {
			File rawComment = fileList[i];
			if (rawComment.isFile()) {
				List<String> commentList = CommentDAO.getComments(rawComment.getPath());
				File parsedComment = new File(parsedCommentDir + "/"
						+ rawComment.getName().substring(0, rawComment.getName().length() - 4) + "_parsed.txt");

				try (FileWriter writer = new FileWriter(parsedComment)) {
					for (String comment : commentList) {
						List<Term> terms = FilterModifWord.modifResult(ToAnalysis.parse(comment));
						String data = getSVMFormatData(terms, emotionalMap, oppositeWords, degreeMap);
						if (data != null && data.length() != 0) {
							writer.write("0\t");
							writer.write(data);
							writer.write("\n");
						}else {
							writer.write("0\t");
							writer.write("1:0");
							writer.write("\n");
						}
					}
				} catch (IOException e) {
					e.printStackTrace();
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
//		getTrainModel();
		parseRawComment();
		System.out.println("TrainText has created!");

		String[] trainArgs = { "-h", "0", "-v", "5", "./Comments/TrainModel/WeiboComment_parsedTrain.txt",
				"./Comments/TrainModel/WeiboComment_model.txt" };
		File file = new File("./Comments/ParsedComments");
		File[] files = file.listFiles();
		List<String[]> predictArgsList = new LinkedList<>();
		for (int i = 0; i < files.length; i++) {
			String parsedCommentPath = files[i].getPath();
			String[] predictArgs = { parsedCommentPath, "./Comments/TrainModel/WeiboComment_model.txt",
					"./Comments/PredictResult/" + files[i].getName().substring(0, files[i].getName().length() - 4)
							+ "_predict.txt" };
			predictArgsList.add(predictArgs);
		}
		System.out.println("----------------SVM运行开始-----------------");

		try {
//			svm_train.main(trainArgs);// 训练
			for (String[] predictArgs : predictArgsList) {
				svm_predict.main(predictArgs);// 预测或分类
			}
		} catch (IOException e) {

			e.printStackTrace();
		}

		System.out.println("SVM结束");
	}
}
