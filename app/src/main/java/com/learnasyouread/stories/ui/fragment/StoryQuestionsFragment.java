package com.learnasyouread.stories.ui.fragment;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.learnasyouread.stories.R;
import com.learnasyouread.stories.data.entity.StoryQuestionsEntity;
import com.learnasyouread.stories.databinding.FragmentStoryQuestionsBinding;
import com.learnasyouread.stories.ui.viewmodel.StoryQuestionsViewModel;
import java.util.ArrayList;
import java.util.List;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class StoryQuestionsFragment extends Fragment {

    private FragmentStoryQuestionsBinding binding;
    private StoryQuestionsViewModel viewModel;
    private List<StoryQuestionsEntity> questionsList = new ArrayList<>();
    private int correctAnswersCount = 0;
    private int currentQuestionIndex = 0;
    private static final int FINAL_TEXT_SIZE = 35;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentStoryQuestionsBinding.inflate(inflater, container, false);
        viewModel = new ViewModelProvider(this).get(StoryQuestionsViewModel.class);
        setupToolbar();
        initializeQuestionList();
        return binding.getRoot();
    }

    private void setupToolbar() {
        AppCompatActivity appCompatActivity = (AppCompatActivity) requireActivity();
        appCompatActivity.setSupportActionBar(binding.toolbar);
        appCompatActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        binding.toolbar.setNavigationOnClickListener(v -> navigateUp());
    }

    private void navigateUp() {
        NavController navController = Navigation.findNavController(requireActivity(), R.id.fragmentContainerView);
        navController.navigateUp();
    }

    private void initializeQuestionList() {
        StoryQuestionsFragmentArgs args = StoryQuestionsFragmentArgs.fromBundle(getArguments());
        String storyID = args.getStoryID();

        viewModel.loadStoryQuestions(storyID);
        viewModel.getStoryQuestionsLiveData().observe(getViewLifecycleOwner(), questions -> {
            if (questions != null) {
                questionsList = new ArrayList<>(questions);
                displayCurrentQuestion();
            } else {
                Log.e("StoryQuestionsFragment", "Failed to load questions");
            }
        });
    }

    private void displayCurrentQuestion() {
        if (questionsList.isEmpty()) {
            showCompletionMessage();
            return;
        }

        StoryQuestionsEntity currentQuestion = questionsList.get(currentQuestionIndex);
        binding.textViewQuestion.setText(currentQuestion.getQuestion());
        binding.buttonOptionA.setText(currentQuestion.getAnswerA());
        binding.buttonOptionB.setText(currentQuestion.getAnswerB());
        binding.buttonOptionC.setText(currentQuestion.getAnswerC());
        binding.buttonOptionD.setText(currentQuestion.getAnswerD());

        binding.buttonOptionA.setOnClickListener(view -> checkAnswer(currentQuestion, currentQuestion.getAnswerA()));
        binding.buttonOptionB.setOnClickListener(view -> checkAnswer(currentQuestion, currentQuestion.getAnswerB()));
        binding.buttonOptionC.setOnClickListener(view -> checkAnswer(currentQuestion, currentQuestion.getAnswerC()));
        binding.buttonOptionD.setOnClickListener(view -> checkAnswer(currentQuestion, currentQuestion.getAnswerD()));

        updateQuestionCounter();
    }

    private void checkAnswer(StoryQuestionsEntity question, String selectedAnswer) {
        if (selectedAnswer.equals(question.getCorrectAnswer())) {
            correctAnswersCount++;
            Toast.makeText(getContext(), R.string.correct_answer, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getContext(), R.string.wrong_answer, Toast.LENGTH_SHORT).show();
        }
        loadNextQuestion();
    }

    private void loadNextQuestion() {
        currentQuestionIndex++;
        if (currentQuestionIndex < questionsList.size()) {
            displayCurrentQuestion();
        } else {
            showCompletionMessage();
        }
    }

    private void showCompletionMessage() {
        binding.textViewQuestion.setText(R.string.congratulations_message);
        binding.textViewQuestion.setTextSize(FINAL_TEXT_SIZE);
        binding.textView.setVisibility(View.GONE);
        binding.buttonOptionA.setVisibility(View.GONE);
        binding.buttonOptionB.setVisibility(View.GONE);
        binding.buttonOptionC.setVisibility(View.GONE);
        binding.buttonOptionD.setVisibility(View.GONE);
    }

    private void updateQuestionCounter() {
        binding.textView.setText(String.format("%d / %d of the questions", currentQuestionIndex + 1, questionsList.size()));
    }
}
