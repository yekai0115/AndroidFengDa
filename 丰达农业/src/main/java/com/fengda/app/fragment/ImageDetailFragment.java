package com.fengda.app.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.github.chrisbanes.photoview.PhotoView;
import com.fengda.app.R;
import com.fengda.app.constant.MyConstant;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;


@ContentView(R.layout.image_detail_fragment)
public class ImageDetailFragment extends Fragment {

	private View mRootView;
	private String mImageUrl;
	@ViewInject(R.id.image)
	private PhotoView mImageView;
	private Context context;
	
	public static ImageDetailFragment newInstance(String imageUrl) {
		final ImageDetailFragment f = new ImageDetailFragment();

		final Bundle args = new Bundle();
		args.putString("url", imageUrl);
		f.setArguments(args);

		return f;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mImageUrl = getArguments() != null ? getArguments().getString("url"): null;
		context=getActivity();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		if (mRootView == null) {
			mRootView = x.view().inject(this, inflater, container);


		}
		ViewGroup mViewGroup = (ViewGroup) mRootView.getParent();
		if (mViewGroup != null) {
			mViewGroup.removeView(mRootView);
		}

		return mRootView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);


		Glide.with(context).load(MyConstant.ALI_PUBLIC_URL+mImageUrl)
              //  .fitCenter()
				//  .override(width,DimenUtils.dip2px(context,130))
				.placeholder(R.drawable.pic_nomal_loading_style)
                .error(R.drawable.pic_nomal_loading_style).into(mImageView);
		mImageView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				getActivity().finish();
			}
		});

	}

	@Override
	public void onResume() {
		super.onResume();

	}

	@Override
	public void onPause() {
		super.onPause();

	}

}
