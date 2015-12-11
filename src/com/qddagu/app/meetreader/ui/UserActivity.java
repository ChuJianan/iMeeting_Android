package com.qddagu.app.meetreader.ui;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Locale;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.qddagu.app.meetreader.AppException;
import com.qddagu.app.meetreader.R;
import com.qddagu.app.meetreader.bean.User;
import com.qddagu.app.meetreader.util.BitmapManager;
import com.qddagu.app.meetreader.util.StringUtils;
import com.qddagu.app.meetreader.util.UIHelper;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
/**
 * 名片
 * @author SYZ
 */
public class UserActivity extends BaseActivity {
	private static final int TAKE_PICTURE = 0;
	private static final int CHOOSE_PICTURE = 1;
	private static final int CROP_PICTURE = 2;
	private static final String TAG = null;
	
	@ViewInject(R.id.name) private EditText mName;
	@ViewInject(R.id.post) private EditText mPost;
	@ViewInject(R.id.site) private EditText mSite;
	@ViewInject(R.id.email) private EditText mEmail;
	@ViewInject(R.id.phone) private EditText mPhone;
	@ViewInject(R.id.mobile) private EditText mMobile;
	@ViewInject(R.id.company) private EditText mCompany;
	@ViewInject(R.id.remark) private EditText mRemark;
	@ViewInject(R.id.logo) private ImageView mLogo;
	//private EditText mAddress;
	//private EditText mFax;
	
	private Uri mLogoUri;
	private File mLogoFile;
	private boolean isLogoChanged = false;
	private BitmapManager bmpManager;
	private Handler mUserHandler;
	private ProgressDialog mLoadingDialog;
	private User mUser;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_user);
		super.onCreate(savedInstanceState);
		ViewUtils.inject(this);
		
		if (appContext.getUserId() > 0) {
			mUser = appContext.getUserInfo();
		}
		
		setTitle("个人信息设置");
		
		bmpManager = new BitmapManager(BitmapFactory.decodeResource(getResources(), R.drawable.ic_logo_default));
		initView();
		
		mLogoFile = new File(getExternalCacheDir() + "/tmpLogo.jpg");
		mLogoUri = Uri.fromFile(mLogoFile);
	}
	
	private void initView() {
		mLoadingDialog = new ProgressDialog(this);
		mLoadingDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		mLoadingDialog.setTitle("提示");
		mLoadingDialog.setMessage("正在加载……");
		mLoadingDialog.setCancelable(false);
		
		// 把文字控件添加监听，点击弹出自定义窗口
		mLogo.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				new AlertDialog.Builder(UserActivity.this)
				.setTitle("请选择")
				.setItems(new String[] {"拍照","相册"}, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						Intent intent = null;
						switch (which) {
						case 0:
							if(mLogoUri == null)
								Log.e(TAG, "image uri can't be null");
							//capture a big bitmap and store it in Uri
							intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);//action is capture
							intent.putExtra(MediaStore.EXTRA_OUTPUT, mLogoUri);
							startActivityForResult(intent, TAKE_PICTURE);
							break;
						case 1:
							intent = new Intent(Intent.ACTION_GET_CONTENT, null);
							intent.setType("image/*");
							intent.putExtra("crop", "true");
							intent.putExtra("aspectX", 1);
							intent.putExtra("aspectY", 1);
							intent.putExtra("outputX", 400);
							intent.putExtra("outputY", 400);
							intent.putExtra("scale", true);
							intent.putExtra("return-data", false);
							intent.putExtra(MediaStore.EXTRA_OUTPUT, mLogoUri);
							intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
							intent.putExtra("noFaceDetection", false); // no face detection
							startActivityForResult(intent, CHOOSE_PICTURE);
							break;
						default:
							break;
						}
					}
				})
				.setNegativeButton("取消", null)
				.show();
			}
		});
		
		if (mUser != null) {
			mName.setText(mUser.getName());
			mPost.setText(mUser.getPost());
			mSite.setText(mUser.getSite());
			mEmail.setText(mUser.getEmail());
			mPhone.setText(mUser.getPhone());
			mMobile.setText(mUser.getMobile());
			mCompany.setText(mUser.getCompany());
			//mAddress.setText(mUser.getAddress());
			//mFax.setText(mUser.getFax());
			mRemark.setText(mUser.getRemark());
			if (!StringUtils.isEmpty(mUser.getLogo())) {
				bmpManager.loadBitmap(mUser.getLogo(), mLogo);
			}
		}
		
		findViewById(R.id.send).setOnClickListener(this);
		View save = findViewById(R.id.save);
		if (save != null) {
			save.setOnClickListener(this);
			save.setVisibility(View.VISIBLE);
		}
	}
	
	private boolean validateSave() {
		return !StringUtils.isEmpty(mName.getText().toString().trim()) &&
				!StringUtils.isEmpty(mMobile.getText().toString().trim());
	}
	
	private void saveCard(final Handler handler) {
		if (!validateSave()) {
			UIHelper.ToastMessage(this, "请按要求填写");
			return;
		}
		mLoadingDialog.show();
		new Thread() {
			public void run() {
				Message msg = new Message();
				try {
					if (mUser == null) {
						mUser = new User();
					}
					mUser.setName(mName.getText().toString().trim());
					mUser.setPost(mPost.getText().toString().trim());
					mUser.setSite(mSite.getText().toString().trim().toLowerCase(Locale.CHINA));
					mUser.setEmail(mEmail.getText().toString().trim().toLowerCase(Locale.CHINA));
					mUser.setPhone(mPhone.getText().toString().trim());
					mUser.setMobile(mMobile.getText().toString().trim());
					mUser.setCompany(mCompany.getText().toString().trim());
					//mUser.setAddress(mAddress.getText().toString().trim());
					//mUser.setFax(mFax.getText().toString().trim());
					mUser.setRemark(mRemark.getText().toString().trim());
					User user = appContext.saveUser(mUser, isLogoChanged ? mLogoFile : null);
					appContext.saveUserInfo(user);	//保存用户信息
					msg.obj = user;
					msg.what = 1;
				} catch (AppException e) {
					e.printStackTrace();
					msg.what = -1;
					msg.obj = e;
				}
				handler.sendMessage(msg);
			};
		}.start();
	}
	
	private Handler getCardHandler() {
		if(mUserHandler == null) {
			mUserHandler = new Handler(){
				@Override
				public void dispatchMessage(Message msg) {
					mLoadingDialog.dismiss();
					if(msg.what == 1) {
						UIHelper.ToastMessage(UserActivity.this, "保存成功");
						finish();
					} else if (msg.what == -1) {
						((AppException)msg.obj).makeToast(UserActivity.this);
					}
				}
			};
		}
		return mUserHandler;
	}
	
	private void sendCard() {
		if (!validateSend()) {
			UIHelper.ToastMessage(this, "暂时不能递交名片");
			return;
		}
		mLoadingDialog.show();
		final Handler handler = new Handler() {
			public void dispatchMessage(Message msg) {
				mLoadingDialog.dismiss();
				if(msg.what == 1) {
					appContext.Meeting().setSend(true);
					UIHelper.ToastMessage(UserActivity.this, "递交成功");
					finish();
				} else if (msg.what == -1) {
					((AppException)msg.obj).makeToast(UserActivity.this);
				}
			};
		};
		new Thread() {
			public void run() {
				Message msg = new Message();
				try {
					appContext.sendCard();
					msg.what = 1;
				} catch (AppException e) {
					e.printStackTrace();
					msg.what = -1;
					msg.obj = e;
				}
				handler.sendMessage(msg);
			};
		}.start();
	}
	
	private boolean validateSend() {
		return mUser != null;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.save:
			saveCard(getCardHandler());
			break;
		case R.id.send:
			sendCard();
			break;
		default:
			super.onClick(v);
			break;
		}	
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(resultCode != Activity.RESULT_OK){//result is not correct
			Log.e(TAG, "requestCode = " + requestCode);
			Log.e(TAG, "resultCode = " + resultCode);
			Log.e(TAG, "data = " + data);
			isLogoChanged = false;
			return;
		}
		switch (requestCode) {
		case TAKE_PICTURE:
			Log.d(TAG, "TAKE_PICTURE: data = " + data);//it seems to be null
			//TODO sent to crop
			cropImageUri(mLogoUri, 400, 400, CROP_PICTURE);
			break;
		case CHOOSE_PICTURE:
			Log.d(TAG, "CHOOSE_PICTURE: data = " + data);//it seems to be null
			if(mLogoUri != null){
				Bitmap bitmap = decodeUriAsBitmap(mLogoUri);
				mLogo.setImageBitmap(bitmap);
				isLogoChanged = true;
			}
			break;
		case CROP_PICTURE:
			Log.d(TAG, "CROP_PICTURE: data = " + data);//it seems to be null
			if(mLogoUri != null){
				Bitmap bitmap = decodeUriAsBitmap(mLogoUri);
				mLogo.setImageBitmap(bitmap);
				isLogoChanged = true;
			}
		break;
		default:
			break;
		}
	}
	
	private void cropImageUri(Uri uri, int outputX, int outputY, int requestCode){
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, "image/*");
		intent.putExtra("crop", "true");
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		intent.putExtra("outputX", outputX);
		intent.putExtra("outputY", outputY);
		intent.putExtra("scale", true);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
		intent.putExtra("return-data", false);
		intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
		intent.putExtra("noFaceDetection", true); // no face detection
		startActivityForResult(intent, requestCode);
	}
	
	private Bitmap decodeUriAsBitmap(Uri uri){
		Bitmap bitmap = null;
		try {
			bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(uri));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		}
		return bitmap;
	}
}
