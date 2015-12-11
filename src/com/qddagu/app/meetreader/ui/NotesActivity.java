package com.qddagu.app.meetreader.ui;

import java.util.ArrayList;
import java.util.List;

import com.qddagu.app.meetreader.AppException;
import com.qddagu.app.meetreader.R;
import com.qddagu.app.meetreader.adapter.ListViewNoteAdapter;
import com.qddagu.app.meetreader.bean.MtFile;
import com.qddagu.app.meetreader.bean.Note;
import com.qddagu.app.meetreader.bean.Notes;
import com.qddagu.app.meetreader.util.UIHelper;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AdapterView.AdapterContextMenuInfo;

@SuppressLint("HandlerLeak")
public class NotesActivity extends BaseActivity {
	private ListView mNotesView;
	private List<Note> mNotesData = new ArrayList<Note>();
	private ListViewNoteAdapter mNoteAdapter;
	private Handler mNotesHandler;
	private MtFile mFile;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_notes);
		super.onCreate(savedInstanceState);
		//注册标题栏进度条，注意用的是ABS的Window而不是Android的
		//requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		
		mFile = (MtFile) getIntent().getSerializableExtra("file");
		setTitle("笔记列表：" + mFile.getName());
		
		initView();
		initData();
	}
	
	private void initView() {
		mNoteAdapter = new ListViewNoteAdapter(this, mNotesData);
		mNotesView = (ListView) findViewById(R.id.list);
		mNotesView.setAdapter(mNoteAdapter);
		mNotesView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Note note = (Note) mNoteAdapter.getItem(position);
				UIHelper.showNote(NotesActivity.this, note);
			}
		});
		registerForContextMenu(mNotesView);
		
		View note = findViewById(R.id.note);
		if (note != null) {
			note.setOnClickListener(this);
			note.setVisibility(View.VISIBLE);
		}
	}
	
	private void initData() {
		 initNotesData();
	}
	
	private void initNotesData() {
		mNotesHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				//隐藏标题栏进度条
				setProgressBarIndeterminateVisibility(false);
				
				if (msg.what >= 0) {
					Notes notes = (Notes)msg.obj;
					mNotesData.clear();
					mNotesData.addAll(notes.getNoteList());
					mNoteAdapter.notifyDataSetChanged();
				} else if (msg.what == -1) {
					((AppException)msg.obj).makeToast(NotesActivity.this);
				}
			}
		};
		loadNotesData(mNotesHandler);
	}
	
	private void loadNotesData(final Handler handler) {
		//显示标题栏进度条
		setProgressBarIndeterminateVisibility(true);
		new Thread(){
			public void run() {
				Message msg = new Message();
				try {
					Notes list = appContext.getNotes(mFile.getUrl());				
					msg.what = list.getNoteList().size();
					msg.obj = list;
	            } catch (AppException e) {
	            	e.printStackTrace();
	            	msg.what = -1;
	            	msg.obj = e;
	            }
                handler.sendMessage(msg);
			}
		}.start();
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(resultCode == 1) {
			//Note note = (Note) data.getSerializableExtra("note");
			//mNotesData.add(note);
			try {
				mNotesData.clear();
				mNotesData.addAll(appContext.getNotes(mFile.getUrl()).getNoteList());
				mNoteAdapter.notifyDataSetChanged();
				UIHelper.ToastMessage(this, "记录成功");
			} catch (AppException e) {
				e.printStackTrace();
				e.makeToast(NotesActivity.this);
			}
		}
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		getMenuInflater().inflate(R.menu.listview_notes, menu);
		super.onCreateContextMenu(menu, v, menuInfo);
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
		Note note = (Note) mNoteAdapter.getItem(info.position);
		switch (item.getItemId()) {
		case R.id.delete:
			deleteNote(note);
			break;
		default:
			return super.onContextItemSelected(item);
		}
		return true;
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.note:
			UIHelper.showNote(NotesActivity.this, mFile);
			break;
		default:
			super.onClick(v);
			break;
		}
	}
	
	/**
	 * 删除笔记
	 * @param meeting
	 */
	private void deleteNote(final Note note) {
		new AlertDialog.Builder(NotesActivity.this)
		.setTitle("提示")
		.setMessage("确定删除“" + note.getTitle() + "”？")
		.setPositiveButton("确定", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				appContext.deleteNote(note);
				mNotesData.remove(note);
				mNoteAdapter.notifyDataSetChanged();
			}
		})
		.setNegativeButton("取消", null)
		.create().show();
	}
}
