package com.qddagu.app.meetreader.ui;

import com.qddagu.app.meetreader.R;
import com.qddagu.app.meetreader.bean.User;
import com.qddagu.app.meetreader.util.BitmapManager;
import com.qddagu.app.meetreader.util.StringUtils;
import com.qddagu.app.meetreader.util.UIHelper;

import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
/**
 * 名片
 * @author SYZ
 */
public class CardActivity extends BaseActivity {
	private TextView mName;
	//private TextView mPost;
	private TextView mSite;
	//private TextView mEmail;
	//private TextView mPhone;
	//private TextView mMobile;
	private TextView mCompany;
	//private TextView mAddress;
	//private TextView mFax;
	private TextView mRemark;
	private ImageView mLogo;
	private User mCard;
	private BitmapManager bmpManager;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_card);
		super.onCreate(savedInstanceState);
		
		mCard = (User) getIntent().getSerializableExtra("card");
		if (mCard == null) {
			UIHelper.ToastMessage(this, "非法调用");
			finish();
			return;
		}
		
		setTitle(mCard.getName());
		
		bmpManager = new BitmapManager(BitmapFactory
				.decodeResource(getResources(), R.drawable.ic_logo_default));
		initView();
	}
	
	private void initView() {		
		mName = (TextView) findViewById(R.id.name);
		//mPost = (TextView) findViewById(R.id.post);
		mSite = (TextView) findViewById(R.id.site);
		//mEmail = (TextView) findViewById(R.id.email);
		//mPhone = (TextView) findViewById(R.id.phone);
		//mMobile = (TextView) findViewById(R.id.mobile);
		mCompany = (TextView) findViewById(R.id.company);
		//mAddress = (TextView) findViewById(R.id.address);
		//mFax = (TextView) findViewById(R.id.fax);
		mRemark = (TextView) findViewById(R.id.remark);
		mLogo = (ImageView) findViewById(R.id.logo);
		
		if (mCard != null) {
			mName.setText(mCard.getName());
			//mPost.setText(mCard.getPost());
			mSite.setText(mCard.getSite());
			//mEmail.setText(mCard.getEmail());
			//mPhone.setText(mCard.getPhone());
			//mMobile.setText(mCard.getMobile());
			mCompany.setText(mCard.getCompany());
			//mAddress.setText(mCard.getAddress());
			//mFax.setText(mCard.getFax());
			mRemark.setText(mCard.getRemark());
			
			
			//临时加入，后期要修改
			if (StringUtils.isEmpty(mCard.getLogo()) && "青岛大谷农业信息有限公司".equals(mCard.getCompany())) {
				mLogo.setImageResource(R.drawable.ic_logo_dagu);
			} else {
				bmpManager.loadBitmap(mCard.getLogo(), mLogo);
			}
		}
	}
}
