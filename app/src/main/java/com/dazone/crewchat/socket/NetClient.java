package com.dazone.crewchat.socket;

import android.app.NotificationManager;
import android.content.Context;
import android.support.v4.app.NotificationCompat;
import android.widget.ProgressBar;
import com.dazone.crewchat.R;
import com.dazone.crewchat.dto.AttachDTO;
import com.dazone.crewchat.utils.CrewChatApplication;
import com.dazone.crewchat.utils.Prefs;
import com.dazone.crewchat.utils.Utils;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;

/**
 * Created by THANHTUNG on 14/03/2016.
 */
public class NetClient {

    private static NotificationCompat.Builder notificationBuilder;
    private static NotificationManager notificationManager;
    private static Integer notificationIDUpload = 200;
    private static int sdk = android.os.Build.VERSION.SDK_INT;

    /**
     * Maximum size of buffer
     */
    public static final int BUFFER_SIZE = 4096;
    private Socket socket = null;
    private OutputStream out = null;
    private InputStream in = null;

    private String host = null;
    private int port = 9999;

    private final Charset UTF8_CHARSET = Charset.forName("UTF-8");
    private String domainName = new Prefs().getServerSite().replace("http://", "");
    private int deviceType = 2;
    public long m_SendFileSize = 0;
    public String responseLine = null;

    public class AsyncObject {
        public int BufferSize = 4096;
        public byte[] buffer = new byte[BufferSize];

        public AsyncObject(int bufferSize) {
            this.buffer = new byte[bufferSize];
        }
    }


    /**
     * Constructor with Host, Port and MAC Address
     *
     * @param host
     * @param port
     */
    public NetClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    private void connectWithServer() {
        try {
            if (socket == null) {
                socket = new Socket(this.host, this.port);
                socket.setKeepAlive(true);
                //socket.setSoTimeout(5000);
                out = socket.getOutputStream();
                in = socket.getInputStream();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void disConnectWithServer() {
        if (socket != null) {
            if (socket.isConnected()) {
                try {
                    in.close();
                    out.close();
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // 파일서버로 해당 파일 정보를 전송합니다.
    public void sendDataWithStringTest(AttachDTO attachDTO, ProgressBar progressBar) {
        if (attachDTO != null) {
            connectWithServer();
            senData(attachDTO, progressBar);
        }
    }

    public void sendDataWithString(AttachDTO attachDTO) {
        if (attachDTO != null) {
            connectWithServer();
            senData(attachDTO);
        }
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

    public int receiveDataFromServer() {
        try {
            byte[] bytes = new byte[4];
            in.read(bytes);

            byte[] reserveBytes = new byte[4];
            reserveBytes[0] = bytes[3];
            reserveBytes[1] = bytes[2];
            reserveBytes[2] = bytes[1];
            reserveBytes[3] = bytes[0];

            ByteBuffer wrapped = ByteBuffer.wrap(reserveBytes);
            int attachNo = wrapped.getInt();


            //int attachNo =

            //int attachNo = bb.getInt();
            //= in.read();
/*            Utils.printLogs("part 2: "+in.read());*/
            Utils.printLogs("part attach: " + attachNo);
            disConnectWithServer(); // disconnect server
            return attachNo;
        } catch (IOException e) {
            e.printStackTrace();
            //Utils.showMessage("Error receiving response:  " + e.getMessage());
            return 0;
        } catch (Exception e) {
            e.printStackTrace();
            //Utils.showMessage("Error receiving response:  " + e.getMessage());
            return 0;
        }
    }

    public void senData(AttachDTO attachDTO, ProgressBar progressBar) {
        try {
            if (socket != null && socket.isConnected()) {
               /* if (sdk > android.os.Build.VERSION_CODES.JELLY_BEAN) {
                    notificationManager = (NotificationManager) CrewChatApplication.getInstance().getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
                    notificationBuilder = new NotificationCompat.Builder(CrewChatApplication.getInstance().getApplicationContext());
                    notificationBuilder
                            .setContentTitle(Utils.getString(R.string.app_name) + "_" + attachDTO.getFileName())
                            .setContentText("0%")
                            .setSmallIcon(R.drawable.attach_ic_file);
                }*/

                AsyncObject ao = new AsyncObject(4096);
                byte[] fileByteData = new byte[409];
                Boolean isFirst = true;
                int nBytes = 0;
                int nCurPercent = 0;
                File myFile = new File(attachDTO.getFullPath());
                FileInputStream fis = new FileInputStream(myFile);
                long dataLeng = fis.available();
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
                        out.write(firstSendData, 0, firstSendData.length);
                    } else {
                        m_SendFileSize += nBytes;
                        ao.buffer = new byte[nBytes];
                        System.arraycopy(fileByteData, 0, ao.buffer, 0, nBytes);
                        out.write(ao.buffer, 0, ao.buffer.length);
                    }
                    int percent = (int) (((double) m_SendFileSize / (double) dataLeng) * 100);

                    if (percent != nCurPercent && percent > 1) {
                        nCurPercent = percent;
                    }

                    /*if (sdk > android.os.Build.VERSION_CODES.JELLY_BEAN) {
                        notificationBuilder.setContentText(percent + "%");
                        notificationBuilder.setProgress(100, percent, false);
                        notificationManager.notify(notificationIDUpload, notificationBuilder.build());
                    }*/
                    progressBar.setProgress(nCurPercent);
                    Utils.printLogs("Success --------------------------: " + nCurPercent + "%");
                }

                /*notificationBuilder.setContentTitle("Upload complete")
                        .setContentText("FINISH")
                        .setProgress(0, 0, false);
                notificationManager.notify(notificationIDUpload, notificationBuilder.build());
                if (notificationManager != null) {
                    notificationManager.cancel(notificationIDUpload);
                }*/
                out.flush();
            }

        } catch (UnknownHostException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            responseLine = "UnknownHostException: " + e.toString();
        } catch (IOException e) {
            e.printStackTrace();
            responseLine = "IOException: " + e.toString();
        } finally {
            /*if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }*/
        }
    }

    public void senData(AttachDTO attachDTO) {
        try {
            if (socket != null && socket.isConnected()) {
                if (sdk > android.os.Build.VERSION_CODES.JELLY_BEAN) {
                    notificationManager = (NotificationManager) CrewChatApplication.getInstance().getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
                    notificationBuilder = new NotificationCompat.Builder(CrewChatApplication.getInstance().getApplicationContext());
                    notificationBuilder
                            .setContentTitle(Utils.getString(R.string.app_name) + "_" + attachDTO.getFileName())
                            .setContentText("0%")
                            .setSmallIcon(R.drawable.attach_ic_file);
                }

                AsyncObject ao = new AsyncObject(4096);
                byte[] fileByteData = new byte[4096];
                Boolean isFirst = true;
                int nBytes = 0;
                int nCurPercent = 0;
                File myFile = new File(attachDTO.getFullPath());
                FileInputStream fis = new FileInputStream(myFile);
                long dataLeng = fis.available();
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
                        out.write(firstSendData, 0, firstSendData.length);
                    } else {
                        m_SendFileSize += nBytes;
                        ao.buffer = new byte[nBytes];
                        System.arraycopy(fileByteData, 0, ao.buffer, 0, nBytes);
                        out.write(ao.buffer, 0, ao.buffer.length);
                    }
                    int percent = (int) (((double) m_SendFileSize / (double) dataLeng) * 100);

                    if (percent != nCurPercent && percent > 1) {
                        nCurPercent = percent;
                    }

                    if (sdk > android.os.Build.VERSION_CODES.JELLY_BEAN) {
                        notificationBuilder.setContentText(percent + "%");
                        notificationBuilder.setProgress(100, percent, false);
                        notificationManager.notify(notificationIDUpload, notificationBuilder.build());
                    }

                    Utils.printLogs("Success --------------------------: " + nCurPercent + "%");
                }

                notificationBuilder.setContentTitle("Upload complete")
                        .setContentText("FINISH")
                        .setProgress(0, 0, false);
                notificationManager.notify(notificationIDUpload, notificationBuilder.build());
                if (notificationManager != null) {
                    notificationManager.cancel(notificationIDUpload);
                }
                out.flush();
            }

        } catch (UnknownHostException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            responseLine = "UnknownHostException: " + e.toString();
        } catch (IOException e) {
            e.printStackTrace();
            responseLine = "IOException: " + e.toString();
        } finally {
            /*if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }*/
        }
    }


}
