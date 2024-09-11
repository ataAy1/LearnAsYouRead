package com.learnasyouread.stories.ui.adapter;

import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;
import com.learnasyouread.stories.data.entity.TextOcrProcessorEntity;
import com.learnasyouread.stories.databinding.ItemTextListBinding;
import com.learnasyouread.stories.ui.fragment.TextListOCRFragmentDirections;
import com.learnasyouread.stories.ui.viewmodel.TextListOCRViewModel;
import java.util.List;

public class TextAdapter extends RecyclerView.Adapter<TextAdapter.TextViewHolder> {

    private List<TextOcrProcessorEntity> textOcrs;
    private final TextListOCRViewModel viewModel;

    public TextAdapter(List<TextOcrProcessorEntity> textOcrs, TextListOCRViewModel viewModel) {
        this.textOcrs = textOcrs;
        this.viewModel = viewModel;
    }

    @NonNull
    @Override
    public TextViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemTextListBinding binding = ItemTextListBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new TextViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull TextViewHolder holder, int position) {
        TextOcrProcessorEntity textOcr = textOcrs.get(position);
        holder.binding.textTitle.setText(textOcr.getTextTitle());

        holder.binding.textTitle.setOnClickListener(v -> {
            TextListOCRFragmentDirections.ActionTextListOCRFragmentToTextDetailFragment action = TextListOCRFragmentDirections.actionTextListOCRFragmentToTextDetailFragment(textOcr);
            Navigation.findNavController(v).navigate(action);
        });

        holder.binding.deleteText.setOnClickListener(v -> showDeleteDialog(textOcr, position, v));
    }


    private void showDeleteDialog(TextOcrProcessorEntity textOcr, int position, View view) {
        new AlertDialog.Builder(view.getContext())
                .setTitle("Delete")
                .setMessage("Do you want to delete this item?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    viewModel.deleteOcrText(textOcr.getId());
                    viewModel.getDeleteSuccess().observe((LifecycleOwner) view.getContext(), isSuccess -> {
                        if (isSuccess) {
                            textOcrs.remove(position);
                            notifyItemRemoved(position);
                            notifyItemRangeChanged(position, textOcrs.size());
                        } else {
                            Toast.makeText(view.getContext(), "Failed to delete item", Toast.LENGTH_SHORT).show();
                            notifyItemChanged(position);
                        }
                    });
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .create()
                .show();
    }


    @Override
    public int getItemCount() {
        return textOcrs.size();
    }

    public void updateTextList(List<TextOcrProcessorEntity> newTextList) {
        textOcrs.clear();
        textOcrs.addAll(newTextList);
        notifyDataSetChanged();
    }

    public static class TextViewHolder extends RecyclerView.ViewHolder {

        private final ItemTextListBinding binding;

        public TextViewHolder(ItemTextListBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
