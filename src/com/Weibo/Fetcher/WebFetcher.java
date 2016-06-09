package com.Weibo.Fetcher;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.jsoup.Connection.Response;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class WebFetcher {
	private static Random random = new Random();
	/**
	 * 根据url爬取页面信息，返回Document对象
	 * 
	 * @param url
	 * @return
	 */

	public static Document fetchHtml(String url) {
		Document document = null;
		Response response = null;
		Map<String, String> cookies = getAvaliableCookie();
		int status = 501;
		while (status == 501) {// 若出现501错误，等待20秒钟再发起请求
			try {
				response = Jsoup.connect(url).userAgent("spider").cookies(cookies).execute();
				document = response.parse();
				status = response.statusCode();
			} catch (IOException e) {
				if (e instanceof HttpStatusException) {
					HttpStatusException statusException = (HttpStatusException) e;
					status = statusException.getStatusCode();
					if (status == 501) {
						System.out.println("501 error found！waiting for 20 seconds");
						cookies = getAvaliableCookie();
						try {
							Thread.sleep(20000);
						} catch (InterruptedException e1) {
							e1.printStackTrace();
						}
					}else {
						System.out.println("HttpStatusException occurs !");
						break;
					}
				}else {
					System.out.println("IOException occurs !");
					break;
				}
			}
		}
		return document;
	}

	/**
	 * 根据url，模拟ajax爬取JSON信息，返回字符串
	 * 
	 * @param url
	 * @return
	 */
	public static String fetchJSON(String url) {
		String body = null;
		Response response = null;
		int status = 501;
		while (status == 501) {// 若出现501错误，等待40秒钟再发起请求
			try {
				Map<String, String> cookies = getAvaliableCookie();
				response = Jsoup.connect(url).header("Connection", "keep-alive").cookies(cookies)
						.userAgent(getRandomUserAgent()).header("X-Forwarded-For", getRandomIPAddress())
						.ignoreContentType(true).timeout(5000).execute();
				body = response.body();
				status = response.statusCode();
			} catch (IOException e) {
				if (e instanceof HttpStatusException) {
					HttpStatusException statusException = (HttpStatusException) e;
					status = statusException.getStatusCode();
					if (status == 501) {
						System.out.println("501 error found！waiting for 20 seconds");
						try {
							Thread.sleep(40000);
						} catch (InterruptedException e1) {
							e1.printStackTrace();
						}
					}else {
						System.out.println("HttpStatusException occurs ! waiting for 10 sec");
						try {
							Thread.sleep(10000);
						} catch (InterruptedException e1) {
						}
						break;
					}
				}else {
					System.out.println("IOException occurs ! retrying after 10 seconds...");
					try {
						Thread.sleep(10000);
					} catch (InterruptedException e1) {
					}
					break;
				}
			}
		}
		return body;
	}
	
	private static String getRandomUserAgent() {
		String[] agents = {"Mozilla/5.0 (Windows; U; Windows NT 5.2) Gecko/2008070208 Firefox/3.0.1",
					     "Mozilla/5.0 (Windows; U; Windows NT 5.1) Gecko/20070309 Firefox/2.0.0.3",
					     "Mozilla/5.0 (Windows; U; Windows NT 5.1) Gecko/20070803 Firefox/1.5.0.12",
					     "Opera/8.0 (Macintosh; PPC Mac OS X; U; en)",
					     "Mozilla/5.0 (Windows; U; Windows NT 5.2) AppleWebKit/525.13 (KHTML, like Gecko) Chrome/0.2.149.27 Safari/525.13",
					     "Mozilla/5.0 (Windows NT 5.1) AppleWebKit/534.55.3 (KHTML, like Gecko) Version/5.1.5 Safari/534.55.3"
								};
		int index = random.nextInt(6);
		return agents[index];
	}
	
	private static String getRandomIPAddress() {
		String[] ipAddress = {
				"202.108.22.5",
				"202.108.22.103",
				"202.108.23.50",
				"220.181.118.87",
				"210.131.18.82",
				"202.181.18.7"
		};
		int index = random.nextInt(6);
		return ipAddress[index];
	}
	private static Map<String, String> getAvaliableCookie() {
		List<Map<String, String>> cookies = new ArrayList<>();

		Map<String, String> cookieMap1 = new HashMap<>();
		cookieMap1.put("YF-V5-G0", "35ff6d315d1a536c0891f71721feb16e");
		cookieMap1.put("SUBP", "0033WrSXqPxfM72-Ws9jqgMF55z29P9D9WFR_yEFZsy9k4UdgCRvjfYF");
		cookieMap1.put("SUB", "_2AkMgRs1Wf8NjqwJRmP0XyWvlbY12zQrEiebDAHzsJxJjHnII7DxnqGVLsy-RRQFryf-z-YkfV7VIkkCq");

		Map<String, String> cookieMap2 = new HashMap<>();
		cookieMap2.put("YF-V5-G0", "c37fc61749949aeb7f71c3016675ad75");
		cookieMap2.put("SUBP", "Ws9jqgMF55z29P9D9WFR_yEFZsy9k4UdgCRvjfYF");
		cookieMap2.put("SUB", "_2AkMgRswZf8NjqwJRmP0XyWvlbY12zQrEiebDAHzsJxJjHloo7DxnqEuiifMCLu9VaIRdUwVrJAHHC01Y");

		Map<String, String> cookieMap3 = new HashMap<>();
		cookieMap2.put("YF-V5-G0", "5f9bd778c31f9e6f413e97a1d464047a");
		cookieMap2.put("SUBP", "Ws9jqgMF55529P9D9WWjb876SjmzWUV85YkIr5Ak");
		cookieMap2.put("SUB", "_2AkMgeetff8NhqwJRmP0XxG7lbI9zwwDEiebDAHzsJxJjHloz7DxnqNGbCpzL9TXDaF0JzUFMzvHDAa0I");
		
		cookies.add(cookieMap1);
		cookies.add(cookieMap2);
		cookies.add(cookieMap3);


		int index = random.nextInt(3);
		return cookies.get(index);
	}
}
