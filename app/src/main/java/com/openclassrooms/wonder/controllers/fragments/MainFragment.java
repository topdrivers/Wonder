package com.openclassrooms.wonder.controllers.fragments;


import android.content.Intent;

import android.os.Bundle;
import android.util.Log;

import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.openclassrooms.wonder.controllers.activities.DetailActivity;
import com.openclassrooms.wonder.models.Project;
import com.openclassrooms.wonderfuloc.R;
import com.openclassrooms.wonder.adapters.ProjectAdapter;
import com.openclassrooms.wonder.base.BaseFragment;
import com.openclassrooms.wonder.injection.Injection;
import com.openclassrooms.wonder.injection.ViewModelFactory;
import com.openclassrooms.wonder.models.ApiResponse;
import com.openclassrooms.wonder.utils.ItemClickSupport;
import com.openclassrooms.wonder.viewmodels.ProjectViewModel;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class MainFragment extends BaseFragment {

    //FOR DATA
    private ProjectViewModel projectViewModel;
    public static final String REQUEST_ANDROID = "android material design";
    public static final String REQUEST_LOGO = "logo";
    public static final String REQUEST_LANDSCAPE = "landscape";

    // FOR DESIGN
    @BindView(R.id.fragment_main_recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.fragment_main_swipe_container)    SwipeRefreshLayout swipeRefreshLayout;
    private ProjectAdapter projectAdapter;

    @Override
    protected int getLayoutId() { return R.layout.fragment_main; }

    @Override
    protected void updateData() {
        this.configureViewModel();
        this.configureRecyclerView();
        this.getProjects();
    }

    // -------------------
    // CONFIGURATION
    // -------------------

    private void configureViewModel(){
        ViewModelFactory mViewModelFactory = Injection.provideViewModelFactory();

        this.projectViewModel = ViewModelProviders.of(this,mViewModelFactory).get(ProjectViewModel.class);
        this.projectViewModel.init(REQUEST_ANDROID);
    }

    private void configureRecyclerView(){
        this.projectAdapter = new ProjectAdapter(new ArrayList<>(), Glide.with(this));
        this.recyclerView.setAdapter(this.projectAdapter);
        this.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        ItemClickSupport.addTo(recyclerView, R.layout.fragment_main_item)
                .setOnItemClickListener((rv, position, v) -> this.navigateToDetail(this.projectAdapter.getProject(position)));
        this.swipeRefreshLayout.setOnRefreshListener(() -> this.refreshProjects(REQUEST_ANDROID));
    }

    // -------------------
    // DATA
    // -------------------

    private void getProjects(){
        this.disposable = this.projectViewModel.getProjects()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .timeout(10, TimeUnit.SECONDS)
                .subscribe(this::updateDesign, throwable -> Log.e("TAG", "ERROR: ", throwable));
    }

    private void refreshProjects(String request){
        this.disposable = this.projectViewModel.refreshProjects(request)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .timeout(10, TimeUnit.SECONDS)
                .subscribe(this::updateDesign, throwable -> Log.e("TAG", "ERROR: ", throwable));
    }

    // -------------------
    // UI
    // -------------------

    private void updateDesign(ApiResponse projectResponse){
        this.swipeRefreshLayout.setRefreshing(false);
        this.projectAdapter.update(projectResponse.getProjects());
    }

    // -------------------
    // NAVIGATION
    // -------------------

    private void navigateToDetail(Project project){
        Intent intent = new Intent(getActivity(), DetailActivity.class);
        Bundle bundle = new Bundle();
        bundle.putInt(DetailActivity.BUNDLE_KEY_PROJECT_ID, project.getId());
        bundle.putString(DetailActivity.BUNDLE_KEY_PROJECT_IMAGE_URL, project.getCovers().getOriginal());
        intent.putExtras(bundle);

        startActivity(intent);
    }
}
