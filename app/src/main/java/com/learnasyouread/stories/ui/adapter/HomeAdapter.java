package com.learnasyouread.stories.ui.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;
import com.learnasyouread.stories.data.entity.StoriesEntity;
import com.learnasyouread.stories.databinding.LayoutStoryHomeBinding;
import com.learnasyouread.stories.ui.fragment.StoriesListFragmentDirections;
import java.util.ArrayList;
import java.util.List;

public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.StoryViewHolder> {

    private List<StoriesEntity> storiesList = new ArrayList<>();
    private static final String TAG = "HomeAdapter";

    @NonNull
    @Override
    public StoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutStoryHomeBinding binding = LayoutStoryHomeBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new StoryViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull StoryViewHolder holder, int position) {
        StoriesEntity story = storiesList.get(position);

        holder.binding.cardviewDesign.setOnClickListener(v -> {
            StoriesListFragmentDirections.ActionStoriesListFragmentToStoryReadFragment action = StoriesListFragmentDirections.actionStoriesListFragmentToStoryReadFragment(story);
            Navigation.findNavController(v).navigate(action);
        });

        holder.binding.storyLevel.setText("");
        holder.binding.storyTitle.setText("Story " + (position + 1));

        String storyTitle = story.getStoryTitle().replaceAll("(?i)\\bthe\\b", "").trim();
        holder.binding.storyTitle.setText(storyTitle);
    }

    @Override
    public int getItemCount() {
        return storiesList.size();
    }

    public void updateStories(List<StoriesEntity> stories) {
        this.storiesList = stories;
        notifyDataSetChanged();
    }

    public static class StoryViewHolder extends RecyclerView.ViewHolder {
        private LayoutStoryHomeBinding binding;

        public StoryViewHolder(LayoutStoryHomeBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
