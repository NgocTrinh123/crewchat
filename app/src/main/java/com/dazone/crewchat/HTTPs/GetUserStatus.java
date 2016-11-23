package com.dazone.crewchat.HTTPs;

import com.dazone.crewchat.constant.Statics;
import com.dazone.crewchat.dto.StatusDto;
import com.dazone.crewchat.dto.StatusItemDto;
import com.dazone.crewchat.interfaces.Urls;
import com.dazone.crewchat.utils.Utils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Map;

public class GetUserStatus {

	/*public static void main(String[] args) {
		Map<String, Integer> result =  new GetUserStatus().getStatusOfUsers("122.41.175.77", 1);
		
		for(Entry<String, Integer> user : result.entrySet()) {
			System.out.println(user.getKey() + ": " + user.getValue());
		}
	}*/

	private void updateUserStatus(Map<String, Integer> userStatus){

	}
	
	public StatusDto getStatusOfUsers(String domain, int companyNo) {

		StatusDto status = new StatusDto();
		try {
			String command = "idstatus";
			String id = String.valueOf(companyNo) + "_*";
			
			Socket socket = new Socket(domain, Urls.DDS_SERVER_PORT);
			InputStream input = socket.getInputStream();
			Reader reader = new InputStreamReader(input);
			BufferedReader bufferedReader = new BufferedReader(reader);
			bufferedReader.readLine();
			
			OutputStream output = socket.getOutputStream();
			Writer writer = new OutputStreamWriter(output);
			
			writer.write(String.format("#%d#%s=%s", command.length() + 1 + id.length(), command, id));
			writer.flush();
			
			ArrayList<StatusItemDto> mapUsers = new ArrayList<>();
			
			String list = bufferedReader.readLine();
		
			writer.close();
			output.close();
			
			bufferedReader.close();
			reader.close();
			input.close();
			
			socket.close();

			Utils.printLogs("List string = "+list);

			list = list.substring(list.indexOf(",") + 1);
			String[] users = list.split("/");
			
			for (String user : users) {
				String[] infos = user.split(",");
				String userId = infos[0].replace(id.replace("*", ""), "");
				int time = -1;
				
				try {
					time = Integer.valueOf(infos[2]);
					StatusItemDto item = new StatusItemDto();
					item.setUserID(userId);
					if (time >= Statics.USER_STATUS_AWAY_TIME) {
						item.setStatus(3);
					} else {
						item.setStatus(1);
					}
					mapUsers.add(item);
				} catch (NumberFormatException e) {
					e.printStackTrace();
				}
			}

			status.setItems(mapUsers);

			return status;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}
}