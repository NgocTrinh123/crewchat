package com.dazone.crewchat.TestMultiLevelListview;

import android.content.Context;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.dazone.crewchat.R;
import com.dazone.crewchat.Tree.Dtos.TreeUserDTO;

import java.util.ArrayList;
import java.util.List;

public class NLevelAdapter extends BaseAdapter {
	private LayoutInflater inflater;

	private final int TYPE_FOLDER = 0;
	private final int TYPE_USER = 1;

	List<NLevelItem> list;
	List<NLevelListItem> filtered;
	public void setFiltered(ArrayList<NLevelListItem> filtered) {
		this.filtered = filtered;
		
	}
	public NLevelAdapter(Context context, List<NLevelItem> list) {
		this.list = list;
		this.filtered = filterItems();
		this.inflater= (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getItemViewType(int position) {
		if (getItem(position).getObject().getType() == 2)
			return TYPE_USER;
		return  TYPE_FOLDER;
	}

	@Override
	public int getViewTypeCount() {
		return 2;
	}

	@Override
	public int getCount() {
		return filtered.size();
	}

	@Override
	public NLevelListItem getItem(int arg0) {
		return filtered.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return 0;
	}


	private class UserViewHolder{
		public TextView title;

		public UserViewHolder(View currentView){

			title = (TextView) currentView.findViewById(R.id.name);
			/*avatar_imv = (ImageView) currentView.findViewById(R.id.avatar);
		status_imv = (ImageView) currentView.findViewById(R.id.status_imv);
		title = (TextView) currentView.findViewById(R.id.name);
		position = (TextView) currentView.findViewById(R.id.position);
		lnItemWraper = (LinearLayout) currentView.findViewById(R.id.item_org_wrapper);
		//status_tv = (TextView) currentView.findViewById(R.id.status_tv);
		//checkBox = (CheckBox) currentView.findViewById(R.id.row_check);
		tv_work_phone = (TextView) currentView.findViewById(R.id.tv_work_phone);
		tv_personal_phone = (TextView) currentView.findViewById(R.id.tv_personal_phone) ;
		tv_user_status = (TextView) currentView.findViewById(R.id.tv_user_status);

		main = (RelativeLayout) currentView.findViewById(R.id.mainParent);*/

		}
	}

	private class FolderViewHolder{
		public TextView title;
		public FolderViewHolder(View currentView){

			 title = (TextView) currentView.findViewById(R.id.office_title);

			/*title = (TextView) currentView.findViewById(R.id.office_title);
			icon = (ImageView) currentView.findViewById(R.id.ic_folder);
			checkBox = (CheckBox) currentView.findViewById(R.id.row_check);
			main = (RelativeLayout) currentView.findViewById(R.id.mainParent);
			mLnTittle = (LinearLayout) currentView.findViewById(R.id.layout_title);*/
		}
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		TreeUserDTO object = getItem(position).getObject();
		UserViewHolder userHolder = null;
		FolderViewHolder groupHolder = null;

		if (convertView == null) {
			switch (getItemViewType(position)){

				case TYPE_USER:
						convertView = inflater.inflate(R.layout.tree_user_row, null);
						userHolder = new UserViewHolder(convertView);

						convertView.setTag(userHolder);
					break;

				case TYPE_FOLDER:
						convertView = inflater.inflate(R.layout.tree_office_row, null);
						groupHolder = new FolderViewHolder(convertView);

						convertView.setTag(groupHolder);
					break;

				default:
						convertView = inflater.inflate(R.layout.tree_office_row, null);
						groupHolder = new FolderViewHolder(convertView);
						convertView.setTag(groupHolder);
					break;

			}

			// Set tag for view holder




		} else {
			// Get tag for view holder
			switch (getItemViewType(position)) {
				case TYPE_USER:
					userHolder = (UserViewHolder) convertView.getTag();
					break;
				case TYPE_FOLDER:
					groupHolder = (FolderViewHolder) convertView.getTag();
					break;
				default:
					groupHolder = (FolderViewHolder) convertView.getTag();
					break;
			}


		}

		// Bind data
		switch (getItemViewType(position)) {

			case TYPE_USER:
				userHolder.title.setText(object.getName());

				break;
			case TYPE_FOLDER:

				groupHolder.title.setText(object.getName());
				break;
			default:
				groupHolder.title.setText(object.getName());
				break;
		}

		return convertView;
	}

	public NLevelFilter getFilter() {
		return new NLevelFilter();
	}
	

	class NLevelFilter {

		public void filter() {
			new AsyncFilter().execute();
		}
		
		class AsyncFilter extends AsyncTask<Void, Void, ArrayList<NLevelListItem>> {

			@Override
			protected ArrayList<NLevelListItem> doInBackground(Void... arg0) {

				return (ArrayList<NLevelListItem>) filterItems();
			}
			
			@Override
			protected void onPostExecute(ArrayList<NLevelListItem> result) {
				setFiltered(result);
				NLevelAdapter.this.notifyDataSetChanged();
			}
		}	

	}
	
	public List<NLevelListItem> filterItems() {
		List<NLevelListItem> tempfiltered = new ArrayList<NLevelListItem>();
		OUTER: for (NLevelListItem item : list) {
			//add expanded items and top level items
			//if parent is null then its a top level item
			if(item.getParent() == null) {
				tempfiltered.add(item);
			} else {
				//go through each ancestor to make sure they are all expanded
				NLevelListItem parent = item;
				while ((parent = parent.getParent())!= null) {
					if (!parent.isExpanded()){
						//one parent was not expanded
						//skip the rest and continue the OUTER for loop
						continue OUTER;
					}
				}
				tempfiltered.add(item);
			}
		}
		
		return tempfiltered;
	}

	public void toggle(int arg2) {
		filtered.get(arg2).toggle();
	}
}
