package com.openclassrooms.wonder.viewmodels;


import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModel;

import com.openclassrooms.wonder.models.ApiResponse;
import com.openclassrooms.wonder.repositories.CommentRepository;
import com.openclassrooms.wonder.repositories.ProjectRepository;

import io.reactivex.Observable;

/**
 * Created by Philippe on 27/03/2018.
 */

public class CommentsViewModel extends ViewModel {

    // REPOSITORIES
    private final CommentRepository commentRepository;

    // DATA
    @Nullable
    private Observable<ApiResponse> currentComments;

    public CommentsViewModel(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

    public void init(Integer projectId) {
        if (this.currentComments != null) return;
        this.currentComments = this.commentRepository.getComments(projectId);
    }

    // ---

    public Observable<ApiResponse> getComments(){ return this.currentComments; }
}
