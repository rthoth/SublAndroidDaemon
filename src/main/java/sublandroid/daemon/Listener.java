package sublandroid.daemon;

import java.net.*;
import java.io.*;

import sublandroid.IOUtils;
import static sublandroid.Log.*;

public class Listener implements Runnable, AutoCloseable {

	private final Daemon daemon;
	private final int port;

	private ServerSocket serverSocket;
	private Socket clientSocket;

	private Writer clientWriter;
	private Reader clientReader;

	public Listener(final Daemon daemon, final int port) {
		this.daemon = daemon;
		this.port = port;
	}

	@Override
	public void close() {

		IOUtils.close(serverSocket);
		IOUtils.close(clientSocket);
		IOUtils.close(clientWriter);
		IOUtils.close(clientReader);
	}

	@Override
	public void run() {
		try {
			serverSocket = new ServerSocket(port, 1, InetAddress.getLoopbackAddress());
			println("SublAndroid listen @ %d", serverSocket.getLocalPort());

			boolean loop = true;
			while (loop) {

				IOUtils.close(clientSocket);
				IOUtils.close(clientReader);
				IOUtils.close(clientWriter);

				clientReader = null;
				clientWriter = null;

				clientSocket = serverSocket.accept();
				println("Woohoo! I've a new sublandroid developer!");

				try {
					clientReader = new InputStreamReader(clientSocket.getInputStream());
					clientWriter = new OutputStreamWriter(clientSocket.getOutputStream());
				} catch (Throwable throwable) {
					println("Ops! Problems with my little friend!");
					throwable.printStackTrace();
				}

				if (clientReader == null && clientWriter == null)
					continue;

				try {
					daemon.talk(clientReader, clientWriter);
				} catch (SocketException socketException) {
					if (serverSocket.isClosed()) {
						println("Exiting...");
						loop = false;
					} else {
						IOUtils.close(clientSocket);
						IOUtils.close(clientWriter);
						IOUtils.close(clientReader);
					}
				}
			}
		} catch (Throwable throwable) {
			throwable.printStackTrace();
		}
	}
}