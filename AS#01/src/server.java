import java.net.*;
import java.io.*;
public class server{

	private final static String IP="127.0.0.1";//�ŧiip�`��
	private final static int PORT = 8888;//�ŧiport�`��
	private static ServerSocket ss;//�ŧi�@��ServerSocket
		
	public static void main(String[] args) throws IOException{
		ss = new ServerSocket();//�Ф@��server socket
		ss.bind(new InetSocketAddress(IP,PORT));//��socket�j�b���w��ip�Mport�W
		//��thread�A�Ƽgrun()��ơA�ϳo��thread�t�d����client socket�M�����startServer();
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
			t.start();//Thread ����run()
		}
	}
	
	public void startServer(Socket s) throws IOException{
		try(BufferedReader input = new BufferedReader(new InputStreamReader(s.getInputStream()));
			PrintStream output = new PrintStream(s.getOutputStream());	)//input�t�d����client�Ƕi�Ӫ��T���Aoutput�t�d�N��ƶǦ^�h��client
		{
			s.setSoTimeout(5000);//�]�wsocket��timeout�ɶ�����5��A�O�ɷ|���XSocketTimeoutException������socket
			s.setKeepAlive(true);//�]�wpersistent connection
			String message="";//�ŧi�ݷ|Ū�i�ӰT����String	
			for(int i=0;i<2&&s.getKeepAlive();i++) {//���@�� connection �̦h����2 objects
				message=input.readLine();//Ū���@��input
				while(message != null && !message.isEmpty()) {//�u�n���i�Ӫ��T�����O�Ū�(�Ū��N��request header�����άO�S���n�D)
					String [] request = message.split("\\s");//�Nmessage�̷ӪŮ���q
					if(request[0].equals("GET")) {//�p�Gheader�OGET
						if(request[1].startsWith("/good.html")) {//���n��good.html
							//�N�H�U��ƼȦs��output
							output.write("HTTP/1.1 200 OK\r\n".getBytes());//���\���A�X
							output.write("Connection: Keep-Alive\r\n".getBytes());//connection: keep alive							output.write("Keep-Alive: timeout=5, max=2\r\n".getBytes());//keep alive ���]�w
							output.write("Content-Type: text/html\r\n".getBytes());//���e����ƫ��A
							output.write("Content-Length: 103\r\n".getBytes());//���e������(BYTE�����)
							output.write("\r\n".getBytes());//������Header�����A���U�ӬO���e
							output.write("<html><head><link href=\"style.css\" rel=\"stylesheet\" type=\"text/css\"></head><body>good</body></html>\r\n".getBytes());
							output.write("\r\n".getBytes());
						}else if (request[1].startsWith("/style.css")) {//���n��style.css
							//�N�H�U��ƼȦs��output
							output.write("HTTP/1.1 200 OK\r\n".getBytes());//���\���A�X
							output.write("Connection: Keep-Alive\r\n".getBytes());//connection: keep alive
							output.write("Keep-Alive: timeout=5, max=2\r\n".getBytes());//keep alive ���]�w
							output.write("Content-Type: text/css\r\n".getBytes());//���e����ƫ��A
							output.write("Content-Length: 22\r\n".getBytes());//���e������(BYTE�����)
							output.write("\r\n".getBytes());//������Header�����A���U�ӬO���e
							output.write("Body {color: red;}\r\n".getBytes());
							output.write("\r\n".getBytes());
						}else if(request[1].startsWith("/redirect.html")) {//���n��redirect.html
							//�N�H�U��ƼȦs��output
							output.write("HTTP/1.1 301 Moved Permanently\r\n".getBytes());//���ܦ�m���A�X
							output.write("Connection: Keep-Alive\r\n".getBytes());//connection: keep alive
							output.write("Keep-Alive: timeout=5, max=2\r\n".getBytes());//keep alive ���]�w
							output.write("Location: http://127.0.0.1:8888/good.html\r\n".getBytes());//���s�ɦV��http://127.0.0.1:8888/good.html							output.write("Content-Length: 0".getBytes());//�S�����e
							output.write("\r\n".getBytes());//������Header����
						}else if(request[1].startsWith("/notfound.html")) {//���n��notfound.html
							//�N�H�U��ƼȦs��output
							output.write("HTTP/1.1 404 Not Found\r\n".getBytes());//�������s�b�N�X
							output.write("Connection: Keep-Alive\r\n".getBytes());//connection: keep alive
							output.write("Keep-Alive: timeout=5, max=2\r\n".getBytes());//keep alive ���]�w
							output.write("Content-Length: 114\r\n".getBytes());//���e������(BYTE�����)
							output.write("Content-Type: text/html\r\n".getBytes());//���e����ƫ��A
							output.write("\r\n".getBytes());//������Header�����A���U�ӬO���e
							output.write("<html><head><title>File Not Found</title></head> \r\n".getBytes());
							output.write("<body><h1>HTTP Error 404: File Not Found</h1></body></html>\r\n".getBytes());  
							output.write("\r\n".getBytes());
						}	
						output.flush();//�N�Ȧs�����eflush�X�h��client
					}
					if(request[0].equals("Connection:")) {//�p�Gheader�Oconnection
						if(request[1].equals("keep-alive")) {//���Ʊ�O���s�u
							s.setKeepAlive(true);//�]�wkeep alive==true�A��while�j���~��]
						}else if(request[1].equals("close")) {//���Ʊ������s�u
							s.setKeepAlive(false);//�]�wkeep alive==false�A��while�j�鰱��
						}
					}
					message=input.readLine();//Ū�U�@��
				}
			}	
			//�����������
			input.close();
			output.close();
			s.close();
		}catch(SocketTimeoutException e) {	//�p�Gtimeout �F
			System.out.println(s.toString()+"Time out");//console ���socket information+timeout
		}	
	}
}