package com.dazone.crewchat.socket;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.charset.Charset;

import android.os.AsyncTask;

import com.dazone.crewchat.dto.AttachDTO;
import com.dazone.crewchat.utils.CrewChatApplication;
import com.dazone.crewchat.utils.Utils;


public class Client extends AsyncTask<Void, Void, Void> {

    private String dstAddress;
    private int dstPort;
    private AttachDTO attachDTO;
    private final Charset UTF8_CHARSET = Charset.forName("UTF-8");
    private String domainName = "dazone.crewcloud.net";
    private int deviceType = 2;
    private InputStream in;
    private Socket socket = null;
    public long m_SendFileSize = 0;
    public String responseLine = null;

    public class AsyncObject {
        public int BufferSize = 4096;
        public byte[] buffer = new byte[BufferSize];

        public AsyncObject(int bufferSize) {
            this.buffer = new byte[bufferSize];
        }
    }

    public Client(String addr, int port, AttachDTO attachDTO) {
        dstAddress = addr;
        dstPort = port;
        this.attachDTO = attachDTO;
    }

    @Override
    protected Void doInBackground(Void... arg0) {

        try {
            socket = new Socket(dstAddress, dstPort);
            //socket.connect(new InetSocketAddress(dstAddress, dstPort), 20000);
            socket.setKeepAlive(true);
            socket.setSoTimeout(20000);
            if (socket != null && socket.isConnected()) {
                AsyncObject ao = new AsyncObject(4096);
                byte[] fileByteData = new byte[4096];
                Boolean isFirst = true;
                int nBytes = 0;
                int nCurPercent = 0;
                File myFile = new File(attachDTO.getFullPath());
                FileInputStream fis = new FileInputStream(myFile);
                long dataLeng = fis.available();
                OutputStream os = socket.getOutputStream();
                in = socket.getInputStream();
                while ((nBytes = fis.read(fileByteData, 0, fileByteData.length)) > 0) {
                    if (isFirst) {
                        isFirst = false;
                        int attachType = Utils.getTypeFileAttach(attachDTO.getFileType());
                        byte[] fType = BitConverter.getBytes(attachType);
                        byte[] fName = attachDTO.getFileName().getBytes(UTF8_CHARSET);
                        byte[] fNameLen = BitConverter.getBytes(fName.length);
                        byte[] fDomain = domainName.getBytes(UTF8_CHARSET);
                        byte[] fDomainLen = BitConverter.getBytes(fDomain.length);
                        byte[] fSessionID = CrewChatApplication.getInstance().getmPrefs().getaccesstoken().getBytes(UTF8_CHARSET);
                        byte[] fSessionIDLen = BitConverter.getBytes(fSessionID.length);
                        byte[] fDeviceType = BitConverter.getBytes(deviceType);
                        byte[] fData = new byte[nBytes];
                        System.arraycopy(fileByteData, 0, fData, 0, nBytes);
                        byte[] fDataLen = BitConverter.getBytes(dataLeng);

                        byte[] firstSendData = new byte[fType.length + fNameLen.length + fName.length + fDomainLen.length + fDomain.length + fSessionIDLen.length + fSessionID.length + fDeviceType.length + fDataLen.length + fData.length];

                        System.arraycopy(fType, 0, firstSendData, 0, fType.length);
                        System.arraycopy(fNameLen, 0, firstSendData, fType.length, fNameLen.length);
                        System.arraycopy(fName, 0, firstSendData, fType.length + fNameLen.length, fName.length);
                        System.arraycopy(fDomainLen, 0, firstSendData, fType.length + fNameLen.length + fName.length, fDomainLen.length);
                        System.arraycopy(fDomain, 0, firstSendData, fType.length + fNameLen.length + fName.length + fDomainLen.length, fDomain.length);
                        System.arraycopy(fSessionIDLen, 0, firstSendData, fType.length + fNameLen.length + fName.length + fDomainLen.length + fDomain.length, fSessionIDLen.length);
                        System.arraycopy(fSessionID, 0, firstSendData, fType.length + fNameLen.length + fName.length + fDomainLen.length + fDomain.length + fSessionIDLen.length, fSessionID.length);
                        System.arraycopy(fDeviceType, 0, firstSendData, fType.length + fNameLen.length + fName.length + fDomainLen.length + fDomain.length + fSessionIDLen.length + fSessionID.length, fDeviceType.length);
                        System.arraycopy(fDataLen, 0, firstSendData, fType.length + fNameLen.length + fName.length + fDomainLen.length + fDomain.length + fSessionIDLen.length + fSessionID.length + fDeviceType.length, fDataLen.length);
                        System.arraycopy(fData, 0, firstSendData, fType.length + fNameLen.length + fName.length + fDomainLen.length + fDomain.length + fSessionIDLen.length + fSessionID.length + fDeviceType.length + fDataLen.length, fData.length);
                        m_SendFileSize += nBytes;
                        System.out.println("Sending...");
                        if (socket.isConnected())
                            os.write(firstSendData, 0, firstSendData.length);
                        else
                            Utils.printLogs("Socket close 1");
                    } else {
                        m_SendFileSize += nBytes;
                        ao.buffer = new byte[nBytes];
                        System.arraycopy(fileByteData, 0, ao.buffer, 0, nBytes);
                        if (socket.isConnected())
                            os.write(ao.buffer, 0, ao.buffer.length);
                        else
                            Utils.printLogs("Socket close 2");
                    }
                    int percent = (int) (((double) m_SendFileSize / (double) dataLeng) * 100);

                    if (percent != nCurPercent && percent > 1) {
                        nCurPercent = percent;
                    }

                    Utils.printLogs("Success --------------------------: " + nCurPercent + "%");
                }
                os.flush();
                responseLine = getDataFromServer();
                os.close();
                socket.close();
            }

        } catch (UnknownHostException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            responseLine = "UnknownHostException: " + e.toString();
        } catch (IOException e) {
            e.printStackTrace();
            responseLine = "IOException: " + e.toString();
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void result) {
        Utils.showMessage(responseLine);
    }


    public String getDataFromServer() {
        String temp = "";
        try {
        /*ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(
                1024);
        byte[] buffer = new byte[1024];
        int bytesRead = in.read(buffer);
        while (bytesRead > 0) {
            byteArrayOutputStream.write(buffer, 0, bytesRead);
            responseLine += byteArrayOutputStream.toString("UTF-8");
            bytesRead = in.read(buffer);
        }*/


            BufferedReader r = new BufferedReader(new InputStreamReader(in));
            StringBuilder total = new StringBuilder();
            String line;
            int i = r.read();
            while ((line = r.readLine()) != null) {
                total.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
            temp = "IOException: " + e.toString();
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
        return temp;
    }

    /*public String receiveDataFromServer() {
        try {
            String message = "";
            String line;
            HybiParser.HappyDataInputStream stream = new HybiParser.HappyDataInputStream(socket.getInputStream());
            readLine(stream);
            while (!TextUtils.isEmpty(line = readLine(stream))) {
                message += line;
            }

            return message;
        } catch (Exception e) {
            return "Error receiving response:  " + e.getMessage();
        }
    }

    public byte[] readBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];
        int len = 0;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        return byteBuffer.toByteArray();
    }

    private static String getStringFromInputStream(InputStream is) {

        BufferedReader br = null;
        StringBuilder sb = new StringBuilder();

        String line;
        try {

            br = new BufferedReader(new InputStreamReader(is));
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return sb.toString();

    }

    private String readLine(HybiParser.HappyDataInputStream reader) throws IOException {
        int readChar = reader.read();
        if (readChar == -1) {
            return null;
        }
        StringBuilder string = new StringBuilder("");
        while (readChar != '\n') {
            if (readChar != '\r') {
                string.append((char) readChar);
            }

            readChar = reader.read();
            if (readChar == -1) {
                return null;
            }
        }
        return string.toString();
    }

    public static void reverse(byte[] array) {
        if (array == null) {
            return;
        }
        int i = 0;
        int j = array.length - 1;
        byte tmp;
        while (j > i) {
            tmp = array[j];
            array[j] = array[i];
            array[i] = tmp;
            j--;
            i++;
        }
    }
*//*
    public byte[] longToByteArray(long value) {
		return new byte[] {
				(byte) (value >> 56),
				(byte) (value >> 48),
				(byte) (value >> 40),
				(byte) (value >> 32),
				(byte) (value >> 24),
				(byte) (value >> 16),
				(byte) (value >> 8),
				(byte) value
		};
	}*//*

    public byte[] longToByteArray(long value) {
        return new byte[]{
                (byte) value,
                (byte) (value >> 8),
                (byte) (value >> 16),
                (byte) (value >> 24),
                (byte) (value >> 32),
                (byte) (value >> 40),
                (byte) (value >> 48),
                (byte) (value >> 56)
        };
    }*/
}
