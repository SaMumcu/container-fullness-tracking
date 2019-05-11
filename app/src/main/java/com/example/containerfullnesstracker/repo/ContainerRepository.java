package com.example.containerfullnesstracker.repo;

import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.example.containerfullnesstracker.model.Container;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ContainerRepository {
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Containers");
    private static ContainerRepository containerRepository;
    MutableLiveData<List<Container>> containers;

    public ContainerRepository() {
    }

    public synchronized static ContainerRepository getInstance() {
        if (containerRepository == null) {
            containerRepository = new ContainerRepository();

        }
        return containerRepository;
    }

    public MutableLiveData getAllContainers() {
        containers = new MutableLiveData<>();
        final List<Container> containerList = new ArrayList<>();

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                containerList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Container container = snapshot.getValue(Container.class);
                    containerList.add(container);
                }
                containers.postValue(containerList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        return containers;
    }

    public Container getContainer(String key) {
        final List<Container> containers = new ArrayList<>();
        ValueEventListener containerListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                Container container = dataSnapshot.getValue(Container.class);
                containers.add(container);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        databaseReference.child(key).addValueEventListener(containerListener);
        return containers.get(0);
    }

    //update fullness
    public void update(String containerId, double lat, double lon) {

        databaseReference.child(containerId).child("latitude").setValue(lat);
        databaseReference.child(containerId).child("longitude").setValue(lon);
    }
}
