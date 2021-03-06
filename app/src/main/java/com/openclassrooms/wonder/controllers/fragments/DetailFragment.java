package com.openclassrooms.wonder.controllers.fragments;


import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import com.bumptech.glide.Glide;
import com.openclassrooms.wonder.base.BaseFragment;
import com.openclassrooms.wonder.controllers.activities.DetailActivity;
import com.openclassrooms.wonder.injection.ViewModelFactory;
import com.openclassrooms.wonder.models.Project;
import com.openclassrooms.wonderfuloc.R;
import com.openclassrooms.wonder.injection.Injection;
import com.openclassrooms.wonder.models.ApiResponse;
import com.openclassrooms.wonder.viewmodels.ProjectDetailViewModel;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;


public class DetailFragment extends BaseFragment {

    // FOR DATA
    private ProjectDetailViewModel projectDetailViewModel;
    private Project currentProject;

    // FOR DESIGN
    @BindView(R.id.fragment_detail_image) ImageView imageProject;
    @BindView(R.id.fragment_detail_title) TextView titleProject;
    @BindView(R.id.fragment_detail_description) TextView descriptionProject;
    @BindView(R.id.fragment_detail_views) TextView viewsProject;
    @BindView(R.id.fragment_detail_likes) TextView likesProject;
    @BindView(R.id.fragment_detail_comments) TextView commentsProject;
    @BindView(R.id.fragment_detail_share) Button shareButton;

    @Override
    protected int getLayoutId() { return R.layout.fragment_detail; }

    @Override
    protected void updateData() {
        this.configureViewModel();
        this.getProject();
        this.updateDesignWhenStarting();
    }

    // -------------------
    // CONFIGURATION
    // -------------------

    private void configureViewModel(){
        ViewModelFactory mViewModelFactory = Injection.provideViewModelFactory();
        this.projectDetailViewModel = ViewModelProviders.of(this, (ViewModelProvider.Factory) mViewModelFactory).get(ProjectDetailViewModel.class);
        this.projectDetailViewModel.init(this.getProjectIdFromBundle());
    }

    // -------------------
    // ACTIONS
    // -------------------

    @OnClick(R.id.fragment_detail_share)
    public void onClickShareButton(View view) {
        this.showMessage();
    }

    // -------------------
    // DATA
    // -------------------

    private void getProject(){
        this.disposable = this.projectDetailViewModel.getProject()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .timeout(10, TimeUnit.SECONDS)
                .subscribe(this::updateDesign, throwable -> Log.e("TAG", "ERROR: ", throwable));
    }

    // -------------------
    // UI
    // -------------------

    private void updateDesignWhenStarting(){
        Glide.with(this).load(this.getImageURLFromBundle()).into(this.imageProject);
    }

    private void updateDesign(ApiResponse projectResponse){
        this.currentProject = projectResponse.getProject();
        this.titleProject.setText(this.currentProject.getName());
        this.descriptionProject.setText(this.currentProject.getDescription());
        this.viewsProject.setText(this.currentProject.getStats().getViews()+" views");
        this.likesProject.setText(this.currentProject.getStats().getAppreciations()+" likes");
        this.commentsProject.setText(this.currentProject.getStats().getComments()+" comments");
    }

    private void showMessage(){
        Toast.makeText(getActivity(), "Fonctionnalit?? en cours de d??veloppement, veuillez patienter !", Toast.LENGTH_LONG).show();
    }

    // -------------------
    // UTILS
    // -------------------

    private Integer getProjectIdFromBundle(){
        Bundle bundle = getActivity().getIntent().getExtras();
        return bundle.getInt(DetailActivity.BUNDLE_KEY_PROJECT_ID);
    }

    private String getImageURLFromBundle(){
        Bundle bundle = getActivity().getIntent().getExtras();
        return bundle.getString(DetailActivity.BUNDLE_KEY_PROJECT_IMAGE_URL);
    }
}
