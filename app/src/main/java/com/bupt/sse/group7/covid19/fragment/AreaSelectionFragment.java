package com.bupt.sse.group7.covid19.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.bupt.sse.group7.covid19.R;
import com.bupt.sse.group7.covid19.model.Area;
import com.bupt.sse.group7.covid19.model.City;
import com.bupt.sse.group7.covid19.model.District;
import com.bupt.sse.group7.covid19.model.Province;
import com.bupt.sse.group7.covid19.presenter.AreaSelectionPresenter;

import java.util.ArrayList;
import java.util.List;

public class AreaSelectionFragment extends Fragment {
    private View view;
    private Spinner spinnerP, spinnerC, spinnerD;
    private List<Province> provinceList;
    private CardView filterButton;

    ArrayAdapter<Province> provinceAdapter;
    ArrayAdapter<City> cityAdapter;
    ArrayAdapter<District> districtAdapter;
    private Province province;
    private City city;
    private District district;

    private List<Spinner> spinners = new ArrayList<>();
    private List<ArrayAdapter> adapters = new ArrayList<>();

    private AreaSelectionPresenter presenter = AreaSelectionPresenter.getInstance();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_area_selection, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.view = view;
        initView();
        initSpinner();
        presenter.setArea(new Area(province, city, district));
    }

    public void initSpinner() {
        provinceList = presenter.getPList(getResources().getXml(R.xml.cities));
        provinceAdapter = new ArrayAdapter<Province>(this.view.getContext(),
                R.layout.support_simple_spinner_dropdown_item,
                provinceList);
        cityAdapter = new ArrayAdapter<City>(this.view.getContext(),
                R.layout.support_simple_spinner_dropdown_item,
                provinceList.get(0).getCities());
        districtAdapter = new ArrayAdapter<District>(this.view.getContext(),
                R.layout.support_simple_spinner_dropdown_item,
                provinceList.get(0).getCities().get(0).getDistricts());

        adapters.add(provinceAdapter);
        adapters.add(cityAdapter);
        adapters.add(districtAdapter);

        province = provinceList.get(0);
        city = province.getCities().get(0);
        district = city.getDistricts().get(0);

        for (int i = 0; i < 3; i++) {
            Spinner spinner = spinners.get(i);
            spinner.setAdapter(adapters.get(i));
            spinner.setSelection(0, true);
        }
        View _view = this.view;

        spinnerP.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                province = provinceList.get(position);
                cityAdapter = new ArrayAdapter<City>(_view.getContext(),
                        R.layout.support_simple_spinner_dropdown_item,
                        province.getCities());
                spinnerC.setAdapter(cityAdapter);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spinnerC.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                city = province.getCities().get(position);
                districtAdapter = new ArrayAdapter<District>(_view.getContext(),
                        R.layout.support_simple_spinner_dropdown_item,
                        city.getDistricts());
                spinnerD.setAdapter(districtAdapter);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spinnerD.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                district = city.getDistricts().get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        filterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.setArea(new Area(province, city, district));
            }
        });
    }

    private void initView() {
        spinnerP = this.view.findViewById(R.id.spinner_P);
        spinnerC = this.view.findViewById(R.id.spinner_C);
        spinnerD = this.view.findViewById(R.id.spinner_D);
        filterButton = this.view.findViewById(R.id.hosp_list_filter);

        spinners.add(spinnerP);
        spinners.add(spinnerC);
        spinners.add(spinnerD);
    }
}
