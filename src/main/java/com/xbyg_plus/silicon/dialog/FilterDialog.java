package com.xbyg_plus.silicon.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.xbyg_plus.silicon.R;
import com.xbyg_plus.silicon.fragment.adapter.VideoRVAdapter.RequestFilter;

import java.util.ArrayList;

public class FilterDialog extends Dialog {
    private RequestFilter requestFilter;
    private Runnable optionsSelectedAction;

    private ArrayList<String> categoryPostValueList;
    private ArrayList<String> categoryNameList;
    private ArrayList<String> sortPostValueList;
    private ArrayList<String> sortNameList;
    private ArrayList<String> timePostValueList;
    private ArrayList<String> timeNameList;

    private Button doneBtn;
    private Spinner categorySpinner;
    private Spinner sortSpinner;
    private Spinner timeSpinner;

    protected FilterDialog(Context context) {
        super(context);
    }

    public FilterDialog setRequestFilter(RequestFilter requestFilter) {
        this.requestFilter = requestFilter;

        this.categoryPostValueList = requestFilter.categoryMap.getKeyList();
        this.categoryNameList = requestFilter.categoryMap.getValueList();
        this.sortPostValueList = requestFilter.sortMap.getKeyList();
        this.sortNameList = requestFilter.sortMap.getValueList();
        this.timePostValueList = requestFilter.timeMap.getKeyList();
        this.timeNameList = requestFilter.timeMap.getValueList();
        return this;
    }

    public FilterDialog setOptionsSelectedAction(Runnable action) {
        this.optionsSelectedAction = action;
        return this;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_filter);

        categorySpinner = (Spinner) findViewById(R.id.category_spinner);
        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, categoryNameList);
        categorySpinner.setAdapter(categoryAdapter);
        categorySpinner.setSelection(categoryPostValueList.indexOf(requestFilter.category));

        sortSpinner = (Spinner) findViewById(R.id.sort_spinner);
        ArrayAdapter<String> sortAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, sortNameList);
        sortSpinner.setAdapter(sortAdapter);
        sortSpinner.setSelection(sortPostValueList.indexOf(requestFilter.sort));

        timeSpinner = (Spinner) findViewById(R.id.time_spinner);
        ArrayAdapter<String> timeAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, timeNameList);
        timeSpinner.setAdapter(timeAdapter);
        timeSpinner.setSelection(timePostValueList.indexOf(requestFilter.time));

        doneBtn = (Button) findViewById(R.id.done);
        doneBtn.setOnClickListener(v -> {
            requestFilter.category = categoryPostValueList.get(categorySpinner.getSelectedItemPosition());
            requestFilter.sort = sortPostValueList.get(sortSpinner.getSelectedItemPosition());
            requestFilter.time = timePostValueList.get(timeSpinner.getSelectedItemPosition());
            dismiss();
            if (optionsSelectedAction != null) {
                optionsSelectedAction.run();
            }
        });
    }
}
