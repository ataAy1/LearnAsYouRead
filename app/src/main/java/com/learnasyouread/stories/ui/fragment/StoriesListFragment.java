package com.learnasyouread.stories.ui.fragment;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.learnasyouread.stories.data.entity.StoriesEntity;
import com.learnasyouread.stories.databinding.FragmentStoryListBinding;
import com.learnasyouread.stories.ui.adapter.HomeAdapter;
import com.learnasyouread.stories.ui.viewmodel.StoriesListViewModel;

import java.util.List;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class StoriesListFragment extends Fragment {

    private FragmentStoryListBinding binding;
    private StoriesListViewModel viewModel;
    private HomeAdapter homeAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentStoryListBinding.inflate(inflater, container, false);
        viewModel = new ViewModelProvider(this).get(StoriesListViewModel.class);

        setupRecyclerView();
        observeStories();

        return binding.getRoot();
    }

    private void setupRecyclerView() {
        binding.recyclerviewStories.setLayoutManager(new LinearLayoutManager(getContext()));
        homeAdapter = new HomeAdapter();
        binding.recyclerviewStories.setAdapter(homeAdapter);
    }

    private void observeStories() {
        viewModel.getAllStories().observe(getViewLifecycleOwner(), stories -> {
            if (stories != null) {
                updateRecyclerView(stories);
                if (!stories.isEmpty()) {
                    Log.d("StoriesListFragment", "First story: " + stories.get(0).getStory());
                }
            } else {
                Log.d("StoriesListFragment", "No stories available");
            }
        });
    }

    private void updateRecyclerView(List<StoriesEntity> stories) {
        homeAdapter.updateStories(stories);
    }
}
