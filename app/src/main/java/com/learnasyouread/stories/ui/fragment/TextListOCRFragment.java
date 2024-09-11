    package com.learnasyouread.stories.ui.fragment;

    import android.os.Bundle;
    import androidx.annotation.NonNull;
    import androidx.annotation.Nullable;
    import androidx.fragment.app.Fragment;
    import androidx.lifecycle.ViewModelProvider;
    import androidx.recyclerview.widget.LinearLayoutManager;
    import android.view.LayoutInflater;
    import android.view.View;
    import android.view.ViewGroup;
    import android.widget.Toast;

    import com.learnasyouread.stories.data.entity.TextOcrProcessorEntity;
    import com.learnasyouread.stories.databinding.FragmentTextListOcrBinding;
    import com.learnasyouread.stories.ui.adapter.TextAdapter;
    import com.learnasyouread.stories.ui.viewmodel.TextListOCRViewModel;

    import java.util.ArrayList;
    import java.util.List;
    import dagger.hilt.android.AndroidEntryPoint;

    @AndroidEntryPoint
    public class TextListOCRFragment extends Fragment {

        private FragmentTextListOcrBinding binding;
        private TextListOCRViewModel viewModel;
        private TextAdapter textAdapter;

        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            binding = FragmentTextListOcrBinding.inflate(inflater, container, false);
            viewModel = new ViewModelProvider(this).get(TextListOCRViewModel.class);
            setupRecyclerView();
            observeViewModel();
            observeSaveAndDeleteResults();
            return binding.getRoot();
        }

        private void setupRecyclerView() {
            textAdapter = new TextAdapter(new ArrayList<>(), viewModel);
            binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            binding.recyclerView.setAdapter(textAdapter);
        }

        private void observeViewModel() {
            viewModel.getAllOcrTexts().observe(getViewLifecycleOwner(), this::updateTextList);
        }

        private void updateTextList(List<TextOcrProcessorEntity> texts) {
            textAdapter.updateTextList(texts);
        }

        private void observeSaveAndDeleteResults() {
            viewModel.getSaveSuccess().observe(getViewLifecycleOwner(), isSuccess -> {
                if (isSuccess) {
                    Toast.makeText(getContext(), "Text saved successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "Failed to save text", Toast.LENGTH_SHORT).show();
                }
            });

            viewModel.getDeleteSuccess().observe(getViewLifecycleOwner(), isSuccess -> {
                if (isSuccess) {
                    Toast.makeText(getContext(), "Text deleted successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "Failed to delete text", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
