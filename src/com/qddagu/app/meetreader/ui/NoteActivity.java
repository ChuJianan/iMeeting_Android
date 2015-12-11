package com.qddagu.app.meetreader.ui;

import java.util.Date;

import com.qddagu.app.meetreader.AppException;
import com.qddagu.app.meetreader.R;
import com.qddagu.app.meetreader.bean.MtFile;
import com.qddagu.app.meetreader.bean.Note;
import com.qddagu.app.meetreader.util.Md5;
import com.qddagu.app.meetreader.util.StringUtils;
import com.qddagu.app.meetreader.util.UIHelper;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class NoteActivity extends BaseActivity {
	private EditText mText;
	private MtFile mFile;
	private Note mNote;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_note);
		super.onCreate(savedInstanceState);
		
		mNote = (Note) getIntent().getSerializableExtra("note");
		if (mNote != null) {
			setTitle(mNote.getTitle());
		} else {
			mFile = (MtFile) getIntent().getSerializableExtra("file");
			setTitle("记录笔记：" + mFile.getName());
		}
		
		initView();
	}
	
	private void initView() {
		mText = (EditText) findViewById(R.id.text);
		
		if (mNote != null) {
			mText.setText(mNote.getContent());
		}
		
		View save = findViewById(R.id.save);
		if (save != null) {
			save.setOnClickListener(this);
			save.setVisibility(View.VISIBLE);
		}
	}
	
	private boolean validate() {
		return !StringUtils.isEmpty(mText.getText().toString().trim());
	}
	
	private void saveNote() {
		if (!validate()) {
			UIHelper.ToastMessage(this, "笔记不能为空");
			return;
		}
		if (mNote == null) {
			mNote = new Note();
			mNote.setFileMd5(Md5.encode(mFile.getUrl()));
		}
		if (mText.getText().length() > 15) {
			mNote.setTitle(mText.getText().subSequence(0, 15).toString());
		} else {
			mNote.setTitle(mText.getText().toString());
		}
		mNote.setContent(mText.getText().toString());
		mNote.setTime(new Date());
		
		try {
			appContext.saveNote(mNote);
		} catch (AppException e) {
			e.printStackTrace();
			e.makeToast(this);
		}
		setResult(1, getIntent().putExtra("note", mNote));
		finish();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.save:
			saveNote();
			break;
		default:
			super.onClick(v);
			break;
		}	
	}
}
