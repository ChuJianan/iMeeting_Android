package com.qddagu.app.meetreader.ui;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.qddagu.app.meetreader.AppException;
import com.qddagu.app.meetreader.R;
import com.qddagu.app.meetreader.adapter.ListViewCommentAdapter;
import com.qddagu.app.meetreader.bean.Comment;
import com.qddagu.app.meetreader.bean.Comments;
import com.qddagu.app.meetreader.bean.MtFile;
import com.qddagu.app.meetreader.util.DateUtils;
import com.qddagu.app.meetreader.util.UIHelper;
import com.qddagu.app.meetreader.widget.PullToRefreshListView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ProgressBar;
import android.widget.TextView;

@SuppressLint("HandlerLeak")
public class CommentsActivity extends BaseActivity {
	private PullToRefreshListView mCommentsView;
	private View mCommentsFooter;
	private TextView mCommentsMore;
	private ProgressBar mCommentsProgress;
	private List<Comment> mCommentsData = new ArrayList<Comment>();
	private ListViewCommentAdapter mCommentAdapter;
	private Handler mCommentsHandler;
	private MtFile mFile;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_comments);
		super.onCreate(savedInstanceState);
		//注册标题栏进度条，注意用的是ABS的Window而不是Android的
		//requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		
		mFile = (MtFile) getIntent().getSerializableExtra("file");
		setTitle("评论列表：" + mFile.getName());
		
		initView();
		initData();
	}
	
	private void initView() {
		mCommentAdapter = new ListViewCommentAdapter(this, mCommentsData);
		mCommentsFooter = getLayoutInflater().inflate(R.layout.listview_footer, null);
		mCommentsMore = (TextView)mCommentsFooter.findViewById(R.id.listview_foot_more);
		mCommentsProgress = (ProgressBar)mCommentsFooter.findViewById(R.id.listview_foot_progress);
		mCommentsView = (PullToRefreshListView) findViewById(R.id.list);
		mCommentsView.addFooterView(mCommentsFooter);
		mCommentsView.setAdapter(mCommentAdapter);
		mCommentsView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				//点击头部、底部栏无效
        		if(position == 0 || view == mCommentsFooter) return;
			}
		});
		mCommentsView.setOnRefreshListener(new PullToRefreshListView.OnRefreshListener() {
			@Override
			public void onRefresh() {
				loadCommentsData(mCommentsHandler, UIHelper.LISTVIEW_ACTION_REFRESH);
			}
		});
		View comment = findViewById(R.id.comment);
		if (comment != null) {
			comment.setOnClickListener(this);
			comment.setVisibility(View.VISIBLE);
		}
	}
	
	private void initData() {
		initCommentsData();
	}
	
	private void initCommentsData() {
		mCommentsHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				
				if (msg.what >= 0) {
					Comments list = (Comments)msg.obj;
					mCommentsData.clear();
					mCommentsData.addAll(list.getCommentList());
					//刷新列表
					mCommentsView.setTag(UIHelper.LISTVIEW_DATA_FULL);
					mCommentAdapter.notifyDataSetChanged();
					mCommentsMore.setText(R.string.load_full);
				} else if (msg.what == -1) {
					mCommentsMore.setText(R.string.load_error);
					((AppException)msg.obj).makeToast(CommentsActivity.this);
				}
				if (mCommentAdapter.getCount() == 0) {
					mCommentsMore.setText(R.string.load_empty);
				}
				mCommentsProgress.setVisibility(ProgressBar.GONE);
				if(msg.arg1 == UIHelper.LISTVIEW_ACTION_REFRESH){
					mCommentsView.onRefreshComplete(DateUtils.format(new Date(), getString(R.string.pull_to_refresh_update_pattern)));
					mCommentsView.setSelection(0);
				}
			}
		};
		loadCommentsData(mCommentsHandler, UIHelper.LISTVIEW_ACTION_INIT);
	}
	
	private void loadCommentsData(final Handler handler, final int action) {
		new Thread(){
			public void run() {
				Message msg = new Message();
				try {
					Comments list = appContext.getComments(mFile.getId());				
					msg.what = list.getCommentList().size();
					msg.obj = list;
	            } catch (AppException e) {
	            	e.printStackTrace();
	            	msg.what = -1;
	            	msg.obj = e;
	            }
				msg.arg1 = action;//告知handler当前action
                handler.sendMessage(msg);
			}
		}.start();
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(resultCode == 1) {
			Comment comment = (Comment) data.getSerializableExtra("comment");
			mCommentsData.add(comment);
			//mCommentAdapter.setComments(mCommentsData);
			mCommentAdapter.notifyDataSetChanged();
			UIHelper.ToastMessage(this, "发表成功");
		}
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.refresh:
			mCommentsView.clickRefresh();
			break;
		case R.id.comment:
			Intent intent = new Intent(this, CommentActivity.class);
			intent.putExtra("file", mFile);
			startActivityForResult(intent, 1);
			break;
		default:
			super.onClick(v);
			break;
		}
	}
}
