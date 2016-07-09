package com.Weibo.Dao;

import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Map.Entry;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

import com.Weibo.Beans.RepostInfo;

public class RepostDAO  {
    private static Map<String, String> edgeMap = new HashMap<>();
    private static List<RepostInfo> infoList = new LinkedList<>();
	private static Document document = DocumentHelper.createDocument();
	private static Element rootElement = null;
	private static Element graphElement = null;
	private static Random random = new Random();

	public static synchronized void record(RepostInfo info) {
		System.out.println(Thread.currentThread().getName()+":  "+info);
		infoList.add((RepostInfo)info);
	}
	static {
		initializeXML();
	}
	private static void initializeXML() {
		// 根节点
		rootElement = document.addElement("graphml", "http://graphml.graphdrawing.org/xmlns");
		document.setRootElement(rootElement);
		// graph 节点
		graphElement = rootElement.addElement("graph");
		graphElement.addAttribute("edgedefault", "undirected");
		// key 节点
		Element keyElement1 = graphElement.addElement("key");
		keyElement1.addAttribute("id", "name");
		keyElement1.addAttribute("for", "node");
		keyElement1.addAttribute("attr.name", "name");
		keyElement1.addAttribute("attr.type", "string");

		Element keyElement2 = graphElement.addElement("key");
		keyElement2.addAttribute("id", "directRepostCount");
		keyElement2.addAttribute("for", "node");
		keyElement2.addAttribute("attr.name", "directRepostCount");
		keyElement2.addAttribute("attr.type", "string");

		Element keyElement3 = graphElement.addElement("key");
		keyElement3.addAttribute("id", "view");
		keyElement3.addAttribute("for", "node");
		keyElement3.addAttribute("attr.name", "view");
		keyElement3.addAttribute("attr.type", "string");
		
		Element keyElement4 = graphElement.addElement("key");
		keyElement4.addAttribute("id", "time");
		keyElement4.addAttribute("for", "node");
		keyElement4.addAttribute("attr.name", "time");
		keyElement4.addAttribute("attr.type", "string");
	}
	private static void makeEdgeElem(String sourceId,String targetId) {
		Element edgeElem = graphElement.addElement("edge");
		edgeElem.addAttribute("source", sourceId	);
		edgeElem.addAttribute("target", targetId);
	}

	private static void makeNodeElem(RepostInfo info) {
		Element nodeElement = graphElement.addElement("node");

		nodeElement.addAttribute("id", String.valueOf(info.getId()));
		
		Element dataElement1 = nodeElement.addElement("data");
		dataElement1.addAttribute("key", "name");
		dataElement1.addText(info.getReposerName());
		
		Element dataElement2 = nodeElement.addElement("data");
		dataElement2.addAttribute("key", "directRepostCount");
		dataElement2.addText(String.valueOf(info.getRepostNum()));

		Element dataElement3 = nodeElement.addElement("data");
		dataElement3.addAttribute("key", "view");
		dataElement3.addText(String.valueOf(1.0));
		
		Element dataElement4 = nodeElement.addElement("data");
		dataElement4.addAttribute("key", "time");
		dataElement4.addText(info.getRepostTime());

	}
	
	public static void store() {
		resetRepostNumAndEdgeMap();
		for (RepostInfo info : infoList) {
			makeNodeElem(info);
		}
		for (Entry<String, String> entry:edgeMap.entrySet()) {
			makeEdgeElem(entry.getValue(),entry.getKey());//按照转发源id：转发者id顺序创建edge节点
		}
		try {
			OutputFormat format = OutputFormat.createPrettyPrint();
			XMLWriter writer = new XMLWriter(new FileWriter("./Repost/repostNet.xml"), format);
			writer.write(document);
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}finally {
			infoList.clear();
			edgeMap.clear();
		}
	}
	//重新计算每个info的直接转发数，同时往edgeMap添加边
	private static void resetRepostNumAndEdgeMap() {
		String rootId = String.valueOf(infoList.get(0).getId());
		for (RepostInfo repostInfoSource : infoList) {
			int unDriectRepostCount = 0;
			String sourceName = repostInfoSource.getReposerName();
			for (RepostInfo repostInfoTarget : infoList) {
				String fromName = repostInfoTarget.getRepostFrom();
				if (fromName.equals("root") ) {
					edgeMap.put(String.valueOf(repostInfoTarget.getId()), rootId);
				}
				else if (sourceName.equals(fromName) ) {
					unDriectRepostCount+=repostInfoTarget.getRepostNum();
					edgeMap.put(String.valueOf(repostInfoTarget.getId()), String.valueOf(repostInfoSource.getId()));
				}
			}
			int directRepostCount=repostInfoSource.getRepostNum() - unDriectRepostCount;
			repostInfoSource.setRepostNum(directRepostCount);
		}
	}

	
}
