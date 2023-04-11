package com.example.carsharing.services;

import android.content.Context;
import android.content.Intent;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;

import com.example.carsharing.R;
import com.example.carsharing.activities.MainActivity;
import com.example.carsharing.activities.NewRequestActivity;
import com.example.carsharing.activities.RequestListActivity;
import com.example.carsharing.models.UserModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseUser;

public class NavigationHelper {

    public void navigate(BottomNavigationView navigation, Context context) {
        navigation.setBackground(null);
        navigation.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_map:
                        context.startActivity(new Intent(context, MainActivity.class));
                        break;
                    case R.id.action_todo:
                        context.startActivity(new Intent(context, RequestListActivity.class));
                        break;
                }
                return false;
            }
        });
    }

    public void hideButton(FloatingActionButton floatingButton, BottomNavigationView navigation, UserModel logUser) {
        if(!logUser.getHasCar()) {
            floatingButton.setVisibility(View.INVISIBLE);
            floatingButton.setEnabled(false);
            navigation.getMenu().removeItem(R.id.action_disabled);
        }
    }

    public void floatButtonOnClick(FloatingActionButton floatingButton, Context context) {
        floatingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, NewRequestActivity.class);
                context.startActivity(intent);
            }
        });
    }
}