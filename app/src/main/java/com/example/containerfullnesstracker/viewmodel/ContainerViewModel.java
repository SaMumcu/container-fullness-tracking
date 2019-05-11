package com.example.containerfullnesstracker.viewmodel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.example.containerfullnesstracker.model.Container;
import com.example.containerfullnesstracker.repo.ContainerRepository;

import java.util.List;

public class ContainerViewModel extends ViewModel {
    private MutableLiveData<List<Container>> containers;
    private ContainerRepository containerRepository = ContainerRepository.getInstance();


    public LiveData<List<Container>> getContainers() {
        if (containers == null) {
            containers = new MutableLiveData<>();
            containers = containerRepository.getAllContainers();
        }
        return containers;
    }

    public Container getContainer(String containerId) {
        return containerRepository.getContainer(containerId);
    }

    public void updateLocation(String containerId, double lat, double lon) {

        containerRepository.update(containerId, lat, lon);
    }
}
