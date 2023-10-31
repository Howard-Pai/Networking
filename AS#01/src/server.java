import java.net.*;
import java.io.*;
public class server{

	private final static String IP="127.0.0.1";//宣告ip常數
	private final static int PORT = 8888;//宣告port常數
	private static ServerSocket ss;//宣告一個ServerSocket
		
	public static void main(String[] args) throws IOException{
		ss = new ServerSocket();//創一個server socket
		ss.bind(new InetSocketAddress(IP,PORT));//把socket綁在給定的ip和port上
		//創thread，複寫run()函數，使這個thread負責接收client socket然後執行startServer();
		while (true) {
			Thread t = new Thread() {
				public void run() {
					try {
						Socket s = ss.accept();
						new server().startServer(s);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			};
			t.start();//Thread 執行run()
		}
	}
	
	public void startServer(Socket s) throws IOException{
		try(BufferedReader input = new BufferedReader(new InputStreamReader(s.getInputStream()));
			PrintStream output = new PrintStream(s.getOutputStream());	)//input負責接收client傳進來的訊息，output負責將資料傳回去給client
		{
			s.setSoTimeout(5000);//設定socket的timeout時間等於5秒，逾時會跳出SocketTimeoutException並關閉socket
			s.setKeepAlive(true);//設定persistent connection
			String message="";//宣告待會讀進來訊息的String	
			for(int i=0;i<2&&s.getKeepAlive();i++) {//讓一個 connection 最多接受2 objects
				message=input.readLine();//讀取一行input
				while(message != null && !message.isEmpty()) {//只要收進來的訊息不是空的(空的代表request header結束或是沒有要求)
					String [] request = message.split("\\s");//將message依照空格分段
					if(request[0].equals("GET")) {//如果header是GET
						if(request[1].startsWith("/good.html")) {//對方要拿good.html
							//將以下資料暫存到output
							output.write("HTTP/1.1 200 OK\r\n".getBytes());//成功狀態碼
							output.write("Connection: Keep-Alive\r\n".getBytes());//connection: keep alive							output.write("Keep-Alive: timeout=5, max=2\r\n".getBytes());//keep alive 的設定
							output.write("Content-Type: text/html\r\n".getBytes());//內容的資料型態
							output.write("Content-Length: 103\r\n".getBytes());//內容的長度(BYTE為單位)
							output.write("\r\n".getBytes());//換行表示Header結束，接下來是內容
							output.write("<html><head><link href=\"style.css\" rel=\"stylesheet\" type=\"text/css\"></head><body>good</body></html>\r\n".getBytes());
							output.write("\r\n".getBytes());
						}else if (request[1].startsWith("/style.css")) {//對方要拿style.css
							//將以下資料暫存到output
							output.write("HTTP/1.1 200 OK\r\n".getBytes());//成功狀態碼
							output.write("Connection: Keep-Alive\r\n".getBytes());//connection: keep alive
							output.write("Keep-Alive: timeout=5, max=2\r\n".getBytes());//keep alive 的設定
							output.write("Content-Type: text/css\r\n".getBytes());//內容的資料型態
							output.write("Content-Length: 22\r\n".getBytes());//內容的長度(BYTE為單位)
							output.write("\r\n".getBytes());//換行表示Header結束，接下來是內容
							output.write("Body {color: red;}\r\n".getBytes());
							output.write("\r\n".getBytes());
						}else if(request[1].startsWith("/redirect.html")) {//對方要拿redirect.html
							//將以下資料暫存到output
							output.write("HTTP/1.1 301 Moved Permanently\r\n".getBytes());//改變位置狀態碼
							output.write("Connection: Keep-Alive\r\n".getBytes());//connection: keep alive
							output.write("Keep-Alive: timeout=5, max=2\r\n".getBytes());//keep alive 的設定
							output.write("Location: http://127.0.0.1:8888/good.html\r\n".getBytes());//重新導向至http://127.0.0.1:8888/good.html							output.write("Content-Length: 0".getBytes());//沒有內容
							output.write("\r\n".getBytes());//換行表示Header結束
						}else if(request[1].startsWith("/notfound.html")) {//對方要拿notfound.html
							//將以下資料暫存到output
							output.write("HTTP/1.1 404 Not Found\r\n".getBytes());//網頁不存在代碼
							output.write("Connection: Keep-Alive\r\n".getBytes());//connection: keep alive
							output.write("Keep-Alive: timeout=5, max=2\r\n".getBytes());//keep alive 的設定
							output.write("Content-Length: 114\r\n".getBytes());//內容的長度(BYTE為單位)
							output.write("Content-Type: text/html\r\n".getBytes());//內容的資料型態
							output.write("\r\n".getBytes());//換行表示Header結束，接下來是內容
							output.write("<html><head><title>File Not Found</title></head> \r\n".getBytes());
							output.write("<body><h1>HTTP Error 404: File Not Found</h1></body></html>\r\n".getBytes());  
							output.write("\r\n".getBytes());
						}	
						output.flush();//將暫存的內容flush出去給client
					}
					if(request[0].equals("Connection:")) {//如果header是connection
						if(request[1].equals("keep-alive")) {//對方希望保持連線
							s.setKeepAlive(true);//設定keep alive==true，讓while迴圈繼續跑
						}else if(request[1].equals("close")) {//對方希望關閉連線
							s.setKeepAlive(false);//設定keep alive==false，讓while迴圈停止
						}
					}
					message=input.readLine();//讀下一行
				}
			}	
			//把該關的關掉
			input.close();
			output.close();
			s.close();
		}catch(SocketTimeoutException e) {	//如果timeout 了
			System.out.println(s.toString()+"Time out");//console 顯示socket information+timeout
		}	
	}
}