package com.coms309.isu_pulse_frontend.ui.courses;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class CoursesViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public CoursesViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is Courses fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}
