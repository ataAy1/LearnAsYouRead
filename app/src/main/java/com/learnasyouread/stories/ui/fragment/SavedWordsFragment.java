package com.learnasyouread.stories.ui.fragment;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.learnasyouread.stories.R;
import com.learnasyouread.stories.data.entity.StoryWordsEntity;
import com.learnasyouread.stories.databinding.FragmentSavedWordsBinding;
import com.learnasyouread.stories.ui.adapter.WordsAdapter;
import com.learnasyouread.stories.ui.viewmodel.SavedWordsViewModel;

import java.util.ArrayList;
import java.util.List;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class SavedWordsFragment extends Fragment implements SearchView.OnQueryTextListener {

    private FragmentSavedWordsBinding binding;
    private SavedWordsViewModel viewModel;
    private WordsAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentSavedWordsBinding.inflate(inflater, container, false);
        viewModel = new ViewModelProvider(this).get(SavedWordsViewModel.class);

        setupRecyclerView();
        observeWords();

        return binding.getRoot();
    }

    private void setupRecyclerView() {
        binding.recyclerviewWords.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    private void observeWords() {
        viewModel.getWordsLiveData().observe(getViewLifecycleOwner(), this::updateRecyclerView);
    }

    private void updateRecyclerView(List<StoryWordsEntity> words) {
        if (adapter == null) {
            adapter = new WordsAdapter(new ArrayList<>(words), getContext(), viewModel);
            binding.recyclerviewWords.setAdapter(adapter);
        } else {
            adapter.updateWords(new ArrayList<>(words));
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupToolbar();
        setupSearchView();
    }

    private void setupToolbar() {
        ((AppCompatActivity) requireActivity()).setSupportActionBar(binding.toolbar);
    }

    private void setupSearchView() {
        requireActivity().addMenuProvider(new MenuProvider() {
            @Override
            public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
                menuInflater.inflate(R.menu.search_menu, menu);
                MenuItem item = menu.findItem(R.id.searchWord);
                SearchView searchView = (SearchView) item.getActionView();
                searchView.setOnQueryTextListener(SavedWordsFragment.this);
            }

            @Override
            public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
                return false;
            }
        }, getViewLifecycleOwner(), Lifecycle.State.RESUMED);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        viewModel.searchWordsLiveData(query).observe(getViewLifecycleOwner(), this::updateRecyclerView);
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        viewModel.searchWordsLiveData(newText).observe(getViewLifecycleOwner(), this::updateRecyclerView);
        return false;
    }

    @Override
    public void onStart() {
        super.onStart();
        viewModel.fetchAllWords();
    }
}
